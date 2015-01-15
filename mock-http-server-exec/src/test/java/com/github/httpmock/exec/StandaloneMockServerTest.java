package com.github.httpmock.exec;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.httpmock.ServerException;

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

		verify(runner).start();
	}

	@Test
	public void stopServer() throws Exception {
		Socket socket = mock(Socket.class);
		OutputStream outputStream = mock(OutputStream.class);
		when(socket.getOutputStream()).thenReturn(outputStream);
		doReturn(socket).when(server).createStopSocket();

		server.start();
		server.stop();

		ArgumentCaptor<byte[]> captor = ArgumentCaptor.forClass(byte[].class);
		verify(outputStream).write(captor.capture(), eq(0), eq("SHUTDOWN".length()));
		assertThat(new String(captor.getValue()).trim(), is("SHUTDOWN"));
		verify(outputStream).close();
		verify(socket).close();
	}

	@Test
	public void stopServerIOError() throws Exception {
		Socket socket = mock(Socket.class);
		IOException exception = new IOException();
		when(socket.getOutputStream()).thenThrow(exception);
		doReturn(socket).when(server).createStopSocket();

		server.start();

		expectedException.expect(ServerException.class);
		expectedException.expectCause(is(exception));
		server.stop();
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
