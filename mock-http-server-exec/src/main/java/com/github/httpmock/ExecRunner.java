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

public class ExecRunner {
	private static final String PROPERTY_WORKING_DIR = "workingDir";
	private static final String PROPERTY_DISTRIBUTION = "distribution";
	private static final String UTF_8 = "UTF-8";
	private static final String CONFIGURATION_PROPERTIES = "configuration.properties";

	private static final String PORT_STOP_DEFAULT = "9099";
	private static final String PORT_HTTP_DEFAULT = "9090";
	private static final String ENV_HTTP_PORT = "HTTP_MOCK_SERVER_PORT_HTTP";
	private static final String ENV_STOP_PORT = "HTTP_MOCK_SERVER_PORT_STOP";

	public static void main(String[] args) throws Exception {
		Properties config = readConfiguration();
		createDistributionFolderIfNecessary(config);
		configureServerConfig(config);
		startServer(config);
	}

	private static void configureServerConfig(Properties config) throws SAXException, IOException, ParserConfigurationException, TransformerException, XPathExpressionException {
		File serverXml = new File(getDistrubtionDirectory(config), "conf/server.xml");
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(serverXml.getAbsolutePath()));

		configurePortForProtocol(doc, getStartupPort(), "HTTP/1.1");
		configurePortForElementsInXpath(doc, getStopPort(), "/Server");

		saveXml(doc, serverXml);
	}

	private static void saveXml(Document doc, File serverXml) throws TransformerConfigurationException, TransformerFactoryConfigurationError, TransformerException {
		Transformer xformer = TransformerFactory.newInstance().newTransformer();
		xformer.transform(new DOMSource(doc), new StreamResult(serverXml));
	}

	private static void configurePortForProtocol(Document doc, String port, String protocol) throws XPathExpressionException {
		String xmlPathFormat = String.format("//Connector[@protocol='%s']", protocol);
		configurePortForElementsInXpath(doc, port, xmlPathFormat);
	}

	private static void configurePortForElementsInXpath(Document doc, String port, String xmlPathFormat) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nodes = (NodeList) xpath.evaluate(xmlPathFormat, doc, XPathConstants.NODESET);
		for (int idx = 0; idx < nodes.getLength(); idx++) {
			Element element = (Element) nodes.item(idx);
			element.setAttribute("port", port);
		}
	}

	private static ClassLoader getClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

	private static Properties readConfiguration() throws IOException, UnsupportedEncodingException {
		ClassLoader contextClassLoader = getClassLoader();
		InputStream is = contextClassLoader.getResourceAsStream(CONFIGURATION_PROPERTIES);
		if (is == null)
			throw new IllegalArgumentException("Config not found");
		return loadProperties(is);
	}

	private static Properties loadProperties(final InputStream is) throws IOException, UnsupportedEncodingException {
		Properties config = new Properties();
		config.load(new InputStreamReader(is, UTF_8));
		is.close();
		return config;
	}

	private static void createDistributionFolderIfNecessary(final Properties config) throws IOException {
		File distribOutput = getDistrubtionDirectory(config);
		File timestampFile = getTimestampFile(config);
		boolean forceDelete = Boolean.getBoolean("tomee.runner.force-delete");
		if (forceDelete || !timestampFile.exists() || isUpdateRequired(config)) {
			if (forceDelete || timestampFile.exists()) {
				System.out.println("Deleting " + distribOutput.getAbsolutePath());
				Files.delete(distribOutput);
			}
			extractApplicationServer(config, distribOutput);
			writeTimestamp(config);
		}
	}

	private static void writeTimestamp(final Properties config) throws IOException {
		File timestampFile = getTimestampFile(config);
		IO.writeString(timestampFile, config.getProperty("timestamp", Long.toString(System.currentTimeMillis())));
	}

	private static boolean isUpdateRequired(final Properties config) throws IOException {
		File timestampFile = getTimestampFile(config);
		return getTimestampFromFile(timestampFile) < getTimestampFromConfig(config);
	}

	private static long getTimestampFromConfig(final Properties config) {
		return Long.parseLong(config.getProperty("timestamp"));
	}

	private static long getTimestampFromFile(File timestampFile) throws IOException {
		return Long.parseLong(IO.slurp(timestampFile).replace(System.getProperty("line.separator"), ""));
	}

	private static File getTimestampFile(Properties config) {
		File distribOutput = getDistrubtionDirectory(config);
		return new File(distribOutput, "timestamp.txt");
	}

	private static void extractApplicationServer(final Properties config, File distribOutput) throws IOException {
		String distrib = config.getProperty(PROPERTY_DISTRIBUTION);
		ClassLoader contextClassLoader = getClassLoader();
		InputStream distribIs = contextClassLoader.getResourceAsStream(distrib);
		System.out.println("Extracting tomee to " + distribOutput.getAbsolutePath());
		Zips.unzip(distribIs, distribOutput, false);
	}

	private static File getDistrubtionDirectory(final Properties config) {
		return new File(config.getProperty(PROPERTY_WORKING_DIR));
	}

	private static void startServer(final Properties config) throws InterruptedException {
		setupSystemProperties(config);
		RemoteServer server = new RemoteServer();
		server.setPortStartup(Integer.parseInt(getStartupPort()));
		setupClassPath(config, server);
		server.start(getJvmArgs(config), "start", true);
		server.getServer().waitFor();
	}

	private static List<String> getJvmArgs(final Properties config) {
		final String additionalArgs = System.getProperty("additionalSystemProperties");
		final List<String> jvmArgs = new LinkedList<String>();
		if (additionalArgs != null)
			Collections.addAll(jvmArgs, additionalArgs.split(" "));

		for (final String k : config.stringPropertyNames())
			if (k.startsWith("jvmArg."))
				jvmArgs.add(config.getProperty(k));
		return jvmArgs;
	}

	private static void setupClassPath(final Properties config, final RemoteServer server) {
		if (config.containsKey("additionalClasspath")) {
			server.setAdditionalClasspath(config.getProperty("additionalClasspath"));
		}
	}

	private static void setupSystemProperties(final Properties config) {
		File distribOutput = getDistrubtionDirectory(config);
		System.setProperty("openejb.home", distribOutput.getAbsolutePath());
		System.setProperty("server.shutdown.port", getStopPort());
		System.setProperty("server.shutdown.command", config.getProperty("shutdownCommand"));
	}

	private static String getStartupPort() {
		String httpPort = PORT_HTTP_DEFAULT;
		if (System.getenv(ENV_HTTP_PORT) != null)
			httpPort = System.getenv(ENV_HTTP_PORT);
		return httpPort;
	}

	private static String getStopPort() {
		String stopPort = PORT_STOP_DEFAULT;
		if (System.getenv(ENV_STOP_PORT) != null)
			stopPort = System.getenv(ENV_STOP_PORT);
		return stopPort;
	}

}
