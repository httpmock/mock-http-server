package com.github.httpmock.exec;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.OutputStream;
import java.net.Socket;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class StandaloneMockServerTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Mock
	private ApplicationServerRunnerFactory runnerFactory;

	@Mock
	private Configuration config;

	@Mock
	private ApplicationServerRunner runner;

	@Spy
	@InjectMocks
	private StandaloneMockServer server;

	@Before
	public void setup() {
		when(runnerFactory.create(config)).thenReturn(runner);
		doReturn(true).when(server).isServerStarted();
	}

	@Test
	public void startServer() throws Exception {
		server.start();

		verify(runner).call();
	}

	@Test
	public void stopServer() throws Exception {
		Socket socket = mock(Socket.class);
		OutputStream outputStream = mock(OutputStream.class);
		when(socket.getOutputStream()).thenReturn(outputStream);

		server.start();
		server.stop();

		verify(runner).stopServer();
	}

	@Test
	public void baseUri() throws Exception {
		when(config.getHttpPort()).thenReturn(1516);
		assertThat(server.getBaseUri(), is("http://localhost:1516"));
	}

	@Test
	public void initWithConfig() throws Exception {
		when(config.getHttpPort()).thenReturn(1516);
		server = new StandaloneMockServer(config);
		assertThat(server.getBaseUri(), is("http://localhost:1516"));
	}

}
