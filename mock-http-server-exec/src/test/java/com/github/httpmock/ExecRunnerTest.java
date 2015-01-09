package com.github.httpmock;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.List;
import java.util.Properties;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

import org.apache.openejb.config.RemoteServer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@RunWith(MockitoJUnitRunner.class)
public class ExecRunnerTest {
	@Captor
	private ArgumentCaptor<List<String>> stringListCaptor;

	@Mock
	private RemoteServer remoteServer;

	@Mock
	private Process serverProcess;

	private Properties properties;

	private ExecRunner runner;

	@Mock
	private Document document;

	@Before
	public void setup() {
		properties = properties();
		when(remoteServer.getServer()).thenReturn(serverProcess);
		runner = spy(new ExecRunner(properties));
		when(runner.createRemoteServer()).thenReturn(remoteServer);
	}

	@Test
	public void startServer() throws Exception {
		runner.startServer();

		verify(remoteServer).start(stringListCaptor.capture(), eq("start"), eq(true));
		verify(serverProcess).waitFor();

		List<String> jvmArgs = stringListCaptor.getValue();
		assertThat(jvmArgs, hasSize(0));
	}

	@Test
	public void startServerWithAdditionalArgs() throws Exception {
		properties.put("jvmArg.0", "arg0");
		properties.put("jvmArg.1", "arg1");
		properties.put("jvmArg.2", "arg2");

		runner.startServer();

		verify(remoteServer).start(stringListCaptor.capture(), eq("start"), eq(true));
		verify(serverProcess).waitFor();

		List<String> jvmArgs = stringListCaptor.getValue();
		assertThat(jvmArgs, hasItems("arg0", "arg1", "arg2"));
	}

	@Test
	public void startServerWithAdditionalClassPath() throws Exception {
		properties.put("additionalClasspath", "additional classpath");

		runner.startServer();

		verify(remoteServer).setAdditionalClasspath("additional classpath");
	}

	@Test
	public void configureServerConfig() throws Exception {
		when(runner.getStartupPort()).thenReturn("12345");
		when(runner.getStopPort()).thenReturn("54321");
		doReturn(document).when(runner).getServerXmlDocument(any(File.class));
		doNothing().when(runner).saveXml(eq(document), any(File.class));

		XPath xpath = mock(XPath.class);
		doReturn(xpath).when(runner).xpath();
		Element serverNode = mock(Element.class);
		NodeList serverNodes = mockXmlNodes(serverNode);
		Element connectorNode = mock(Element.class);
		NodeList connectorNodes = mockXmlNodes(connectorNode);
		when(xpath.evaluate("/Server", document, XPathConstants.NODESET)).thenReturn(serverNodes);
		when(xpath.evaluate("//Connector[@protocol='HTTP/1.1']", document, XPathConstants.NODESET)).thenReturn(connectorNodes);

		runner.configureServerConfig();

		verify(serverNode).setAttribute("port", "54321");
		verify(connectorNode).setAttribute("port", "12345");
		verify(runner).saveXml(eq(document), any(File.class));
	}

	private NodeList mockXmlNodes(Element... elements) {
		NodeList nodeList = mock(NodeList.class);
		when(nodeList.getLength()).thenReturn(elements.length);
		int index = 0;
		for (Element element : elements) {
			when(nodeList.item(index)).thenReturn(element);
			index++;
		}
		return nodeList;
	}

	@Test
	public void extractDistributionFolder() throws Exception {
		properties.put("timestamp", "0");
		properties.put(ExecRunner.PROPERTY_DISTRIBUTION, ".distribution");
		doNothing().when(runner).unzip(any(File.class), any(String.class));
		doNothing().when(runner).writeTimestamp();

		runner.createDistributionFolderIfNecessary();

		verify(runner).unzip(new File("target/test/resources/tomee"), ".distribution");
	}

	private Properties properties() {
		Properties properties = new Properties();
		properties.put(ExecRunner.PROPERTY_WORKING_DIR, "target/test/resources/tomee");
		properties.put("shutdownCommand", "shutdown");
		return properties;
	}
}
