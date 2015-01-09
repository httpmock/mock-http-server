package com.github.httpmock;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;

import org.apache.tomee.embedded.Configuration;
import org.apache.tomee.embedded.Container;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class HttpMockServerStandaloneTest {
	private static final int SERVER_PORT = 0;
	private static final int SERVER_STOP_PORT = 0;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Mock
	private Container container;

	private HttpMockServerStandalone tomee;

	private HttpMockServerStandalone createServer() {
		return new HttpMockServerStandalone(SERVER_PORT, SERVER_STOP_PORT, container);
	}

	@Before
	public void setup() throws Exception {
		tomee = createServer();
	}

	@Test
	public void setupContainer() throws Exception {
		verify(container).setup(any(Configuration.class));
	}

	@Test
	public void startContainer() throws Exception {
		tomee.start();
		verify(container).start();
	}

	@Test
	public void stopContainer() throws Exception {
		tomee.stop();
		verify(container).stop();
	}

	@Test
	public void deployWebapp() throws Exception {
		String pathToWar = "some.war";
		File war = new File(pathToWar);

		tomee.deploy(pathToWar);
		verify(container).deploy("mockserver", war);
	}

	@Test
	public void startError() throws Exception {
		Throwable exception = mock(IOException.class);
		Mockito.doThrow(exception).when(container).start();

		expectedException.expect(ServerException.class);
		expectedException.expectCause(is(exception));
		tomee.start();
	}

	@Test
	public void deployError() throws Exception {
		Throwable exception = mock(IOException.class);
		Mockito.doThrow(exception).when(container).deploy(any(String.class), any(File.class));

		expectedException.expect(ServerException.class);
		expectedException.expectCause(is(exception));
		tomee.deploy("some.war");
	}

	@Test
	public void waitUnitlStop() throws Exception {
		tomee.waitUntilStop();
		verify(container).await();
	}

	@Test
	public void defaultStartPort() throws Exception {
		assertThat(HttpMockServerStandalone.getConfiguredHttpPort(), is(9090));
	}

	@Test
	public void defaultStopPort() throws Exception {
		assertThat(HttpMockServerStandalone.getConfiguredStopPort(), is(9099));
	}

	@Test
	public void httpPort() throws Exception {
		assertThat(tomee.getHttpPort(), is(SERVER_PORT));
	}

	@Test
	public void pathToWar() throws Exception {
		assertThat(HttpMockServerStandalone.getPathToWar(new String[] {}), is("target/wars/mockserver.war"));
		assertThat(HttpMockServerStandalone.getPathToWar(new String[] { "pathtowar" }), is("pathtowar"));
	}
}
