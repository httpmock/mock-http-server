package com.github.httpmock;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.openejb.config.RemoteServer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

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
		FileUtils.copyDirectoryToDirectory(new File("src/test/resources/tomee"), new File("target/test/resources"));
		when(runner.getStartupPort()).thenReturn("12345");
		when(runner.getStopPort()).thenReturn("54321");

		runner.configureServerConfig();

		Document document = getServerXmlDocument();
		assertThat(document, hasXPath("/Server[@port='54321']"));
		assertThat(document, hasXPath("//Connector[@port='12345' and @protocol='HTTP/1.1']"));
	}

	private Document getServerXmlDocument() throws SAXException, IOException, ParserConfigurationException {
		return DocumentBuilderFactory.newInstance()//
				.newDocumentBuilder()//
				.parse(new InputSource("target/test/resources/tomee/conf/server.xml"));
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
