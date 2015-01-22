package com.github.httpmock.exec;

import org.apache.openejb.config.RemoteServer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;

public class ApplicationServerRunner implements Callable<Void> {
	private static final String PROPERTY_ADDITIONAL_SYSTEM_PROPERTIES = "additionalSystemProperties";
	public static final String PROPERTY_WORKING_DIR = "workingDir";
	private static final String CONFIGURATION_PROPERTIES = "configuration.properties";

	private static final int PORT_STOP_DEFAULT = 9099;
	private static final int PORT_HTTP_DEFAULT = 9090;
	private static final int PORT_AJP_DEFAULT = 9009;
	private static final String ENV_HTTP_PORT = "HTTP_MOCK_SERVER_PORT_HTTP";
	private static final String ENV_STOP_PORT = "HTTP_MOCK_SERVER_PORT_STOP";
	private static final String ENV_AJP_PORT = "HTTP_MOCK_SERVER_PORT_AJP";
	private Configuration config;
	private Properties properties;
	private RemoteServer server;
	private ApplicationServerDistribution distribution;

	public ApplicationServerRunner(Configuration config, Properties properties) {
		this.config = config;
		this.properties = properties;
		this.distribution = new ApplicationServerDistribution(getDistributionDirectory(), properties);
	}

	public ApplicationServerRunner(Configuration config) {
		this.config = config;
		this.properties = readProperties();
		this.distribution = new ApplicationServerDistribution(getDistributionDirectory(), properties);
	}

	public static void main(String[] args) throws Exception {
		ApplicationServerRunner runner = new ApplicationServerRunner(getConfig(args));
		runner.call();
	}

	public static Configuration getConfig(String[] args) {
		ConfigurationBuilder configBuilder = ConfigurationBuilder.config()//
				.httpPort(getHttpPort())//
				.stopPort(getStopPort())//
				.ajpPort(getAjpPort());
		if (args.length == 3) {
			configBuilder.httpPort(Integer.parseInt(args[0]))//
					.stopPort(Integer.parseInt(args[1]))//
					.ajpPort(Integer.parseInt(args[2]));
		}
		return configBuilder.build();
	}

	@Override
	public Void call() throws Exception {
		createDistributionFolderIfNecessary();
		configureServerConfig();
		startServer();
		return null;
	}

	void configureServerConfig() throws SAXException, IOException, ParserConfigurationException, TransformerException, XPathExpressionException {
		File serverXml = new File(getDistributionDirectory(), "conf/server.xml");
		Document doc = getServerXmlDocument(serverXml);

		configureHttpPort(doc, Integer.toString(config.getHttpPort()));
		configureStopPort(doc, Integer.toString(config.getStopPort()));
		configureAjpPort(doc, Integer.toString(config.getAjpPort()));

		saveXml(doc, serverXml);
	}

	void configureAjpPort(Document doc, String port) throws XPathExpressionException {
		configurePortForProtocol(doc, port, "AJP/1.3");
	}

	void configureStopPort(Document doc, String stopPort) throws XPathExpressionException {
		configurePortForElementsInXpath(doc, stopPort, "/Server");
	}

	void configureHttpPort(Document doc, String port) throws XPathExpressionException {
		configurePortForProtocol(doc, port, "HTTP/1.1");
	}

	Document getServerXmlDocument(File serverXml) throws SAXException, IOException, ParserConfigurationException {
		return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(serverXml.getAbsolutePath()));
	}

	void saveXml(Document doc, File serverXml) throws TransformerConfigurationException, TransformerFactoryConfigurationError, TransformerException {
		Transformer xformer = TransformerFactory.newInstance().newTransformer();
		xformer.transform(new DOMSource(doc), new StreamResult(serverXml));
	}

	private void configurePortForProtocol(Document doc, String port, String protocol) throws XPathExpressionException {
		String xmlPathFormat = String.format("//Connector[@protocol='%s']", protocol);
		configurePortForElementsInXpath(doc, port, xmlPathFormat);
	}

	private void configurePortForElementsInXpath(Document doc, String port, String xmlPathFormat) throws XPathExpressionException {
		NodeList nodes = (NodeList) xpath().evaluate(xmlPathFormat, doc, XPathConstants.NODESET);
		for (int idx = 0; idx < nodes.getLength(); idx++) {
			Element element = (Element) nodes.item(idx);
			element.setAttribute("port", port);
		}
	}

	XPath xpath() {
		return XPathFactory.newInstance().newXPath();
	}

	private static ClassLoader getClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

	public Properties readProperties() {
		PropertiesReader propertiesReader = new PropertiesReader(getClassLoader());
		return propertiesReader.read(CONFIGURATION_PROPERTIES);
	}

	void createDistributionFolderIfNecessary() throws IOException {
		distribution.updateIfNecessary();
	}

	private File getDistributionDirectory() {
		return new File(properties.getProperty(PROPERTY_WORKING_DIR));
	}

	void startServer() throws InterruptedException {
		setupSystemProperties();
		server = createRemoteServer();
		server.setPortStartup(config.getHttpPort());
		setupClassPath(server);
		server.start(getJvmArgs(), "start", true);
		server.getServer().waitFor();
	}

	RemoteServer createRemoteServer() {
		return new RemoteServer(60, true);
	}

	private List<String> getJvmArgs() {
		final String additionalArgs = System.getProperty(PROPERTY_ADDITIONAL_SYSTEM_PROPERTIES);
		final List<String> jvmArgs = new LinkedList<String>();
		if (additionalArgs != null)
			Collections.addAll(jvmArgs, additionalArgs.split(" "));

		for (final String k : properties.stringPropertyNames())
			if (k.startsWith("jvmArg."))
				jvmArgs.add(properties.getProperty(k));
		return jvmArgs;
	}

	private void setupClassPath(final RemoteServer server) {
		if (properties.containsKey("additionalClasspath")) {
			server.setAdditionalClasspath(properties.getProperty("additionalClasspath"));
		}
	}

	private void setupSystemProperties() {
		File distribOutput = getDistributionDirectory();
		System.setProperty("openejb.home", distribOutput.getAbsolutePath());
		System.setProperty("server.shutdown.port", Integer.toString(config.getStopPort()));
		System.setProperty("server.shutdown.command", properties.getProperty("shutdownCommand"));
	}

	private static Integer getHttpPort() {
		int httpPort = PORT_HTTP_DEFAULT;
		if (System.getenv(ENV_HTTP_PORT) != null)
			httpPort = Integer.parseInt(System.getenv(ENV_HTTP_PORT));
		return httpPort;
	}

	private static Integer getStopPort() {
		int stopPort = PORT_STOP_DEFAULT;
		if (System.getenv(ENV_STOP_PORT) != null)
			stopPort = Integer.parseInt(System.getenv(ENV_STOP_PORT));
		return stopPort;
	}

	private static Integer getAjpPort() {
		int stopPort = PORT_AJP_DEFAULT;
		if (System.getenv(ENV_AJP_PORT) != null)
			stopPort = Integer.parseInt(System.getenv(ENV_AJP_PORT));
		return stopPort;
	}

	public void stopServer() throws Exception {
		server.stop();
	}
}
