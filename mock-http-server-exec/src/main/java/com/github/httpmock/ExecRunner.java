package com.github.httpmock;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.openejb.config.RemoteServer;
import org.apache.openejb.loader.Files;
import org.apache.openejb.loader.IO;
import org.apache.openejb.loader.Zips;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ExecRunner extends Thread {
	private static final String PROPERTY_ADDITIONAL_SYSTEM_PROPERTIES = "additionalSystemProperties";
	public static final String PROPERTY_WORKING_DIR = "workingDir";
	public static final String PROPERTY_DISTRIBUTION = "distribution";
	private static final String UTF_8 = "UTF-8";
	private static final String CONFIGURATION_PROPERTIES = "configuration.properties";

	private static final String PORT_STOP_DEFAULT = "9099";
	private static final String PORT_HTTP_DEFAULT = "9090";
	private static final String ENV_AJP_PORT = "9009";
	private static final String ENV_HTTP_PORT = "HTTP_MOCK_SERVER_PORT_HTTP";
	private static final String ENV_STOP_PORT = "HTTP_MOCK_SERVER_PORT_STOP";
	private static final String PORT_AJP_DEFAULT = "HTTP_MOCK_SERVER_PORT_AJP";
	private Properties properties;
	private String startPort;
	private String stopPort;
	private String ajpPort;

	public ExecRunner(Properties properties, String startPort, String stopPort, String ajpPort) {
		this.properties = properties;
		this.startPort = startPort;
		this.stopPort = stopPort;
		this.ajpPort = ajpPort;
	}

	public ExecRunner(Configuration config, Properties properties) {
		this(properties, Integer.toString(config.getHttpPort()), //
				Integer.toString(config.getStopPort()), Integer.toString(config.getAjpPort()));
	}

	public static void main(String[] args) throws Exception {
		String startPort = getStartupPort();
		String stopPort = getStopPort();
		String ajpPort = getAjpPort();
		if (args.length == 3) {
			startPort = args[0];
			stopPort = args[1];
			ajpPort = args[2];
		}
		ExecRunner runner = new ExecRunner(readProperties(), startPort, stopPort, ajpPort);
		runner.run();
	}

	@Override
	public void run() {
		try {
			createDistributionFolderIfNecessary();
			configureServerConfig();
			startServer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void configureServerConfig() throws SAXException, IOException, ParserConfigurationException, TransformerException, XPathExpressionException {
		File serverXml = new File(getDistrubtionDirectory(), "conf/server.xml");
		Document doc = getServerXmlDocument(serverXml);

		configureStartPort(doc, startPort);
		configureStopPort(doc, stopPort);
		configureAjpPort(doc, ajpPort);

		saveXml(doc, serverXml);
	}

	void configureAjpPort(Document doc, String port) throws XPathExpressionException {
		configurePortForProtocol(doc, port, "AJP/1.3");
	}

	void configureStopPort(Document doc, String stopPort) throws XPathExpressionException {
		configurePortForElementsInXpath(doc, stopPort, "/Server");
	}

	void configureStartPort(Document doc, String port) throws XPathExpressionException {
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

	public static Properties readProperties() {
		ClassLoader contextClassLoader = getClassLoader();
		InputStream is = contextClassLoader.getResourceAsStream(CONFIGURATION_PROPERTIES);
		if (is == null)
			throw new RuntimeException("Config not found");
		try {
			return loadProperties(is);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new ServerException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ServerException(e);
		}
	}

	private static Properties loadProperties(final InputStream is) throws IOException, UnsupportedEncodingException {
		Properties config = new Properties();
		config.load(new InputStreamReader(is, UTF_8));
		is.close();
		return config;
	}

	void createDistributionFolderIfNecessary() throws IOException {
		File distribOutput = getDistrubtionDirectory();
		File timestampFile = getTimestampFile();
		boolean forceDelete = Boolean.getBoolean("tomee.runner.force-delete");
		if (forceDelete || !timestampFile.exists() || isUpdateRequired()) {
			if (forceDelete || timestampFile.exists()) {
				System.out.println("Deleting " + distribOutput.getAbsolutePath());
				Files.delete(distribOutput);
			}
			extractApplicationServer(distribOutput);
			writeTimestamp();
		}
	}

	void writeTimestamp() throws IOException {
		File timestampFile = getTimestampFile();
		String currentTime = Long.toString(System.currentTimeMillis());
		IO.writeString(timestampFile, properties.getProperty("timestamp", currentTime));
	}

	private boolean isUpdateRequired() throws IOException {
		File timestampFile = getTimestampFile();
		return getTimestampFromFile(timestampFile) < getTimestampFromConfig();
	}

	private long getTimestampFromConfig() {
		return Long.parseLong(properties.getProperty("timestamp"));
	}

	private long getTimestampFromFile(File timestampFile) throws IOException {
		return Long.parseLong(IO.slurp(timestampFile).replace(System.getProperty("line.separator"), ""));
	}

	private File getTimestampFile() {
		File distribOutput = getDistrubtionDirectory();
		return new File(distribOutput, "timestamp.txt");
	}

	private void extractApplicationServer(File distribOutput) throws IOException {
		String distrib = properties.getProperty(PROPERTY_DISTRIBUTION);
		System.out.println("Extracting tomee to " + distribOutput.getAbsolutePath());
		unzip(distribOutput, distrib);
	}

	void unzip(File distribOutput, String distrib) throws IOException {
		ClassLoader contextClassLoader = getClassLoader();
		InputStream distribIs = contextClassLoader.getResourceAsStream(distrib);
		Zips.unzip(distribIs, distribOutput, false);
	}

	private File getDistrubtionDirectory() {
		return new File(properties.getProperty(PROPERTY_WORKING_DIR));
	}

	void startServer() throws InterruptedException {
		setupSystemProperties();
		RemoteServer server = createRemoteServer();
		server.setPortStartup(Integer.parseInt(getStartupPort()));
		setupClassPath(server);
		server.start(getJvmArgs(), "start", true);
		server.getServer().waitFor();
	}

	RemoteServer createRemoteServer() {
		return new RemoteServer();
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
		File distribOutput = getDistrubtionDirectory();
		System.setProperty("openejb.home", distribOutput.getAbsolutePath());
		System.setProperty("server.shutdown.port", getStopPort());
		System.setProperty("server.shutdown.command", properties.getProperty("shutdownCommand"));
	}

	static String getStartupPort() {
		String httpPort = PORT_HTTP_DEFAULT;
		if (System.getenv(ENV_HTTP_PORT) != null)
			httpPort = System.getenv(ENV_HTTP_PORT);
		return httpPort;
	}

	static String getStopPort() {
		String stopPort = PORT_STOP_DEFAULT;
		if (System.getenv(ENV_STOP_PORT) != null)
			stopPort = System.getenv(ENV_STOP_PORT);
		return stopPort;
	}

	private static String getAjpPort() {
		String stopPort = PORT_AJP_DEFAULT;
		if (System.getenv(ENV_AJP_PORT) != null)
			stopPort = System.getenv(ENV_AJP_PORT);
		return stopPort;
	}

}
