package com.github.httpmock;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class EmbeddedMockServerTest {
	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Mock
	private ApplicationServerStandalone applicationServer;

	@InjectMocks
	private EmbeddedMockServer mockServer;

	@Test
	public void start() throws Exception {
		mockServer.start();

		verify(applicationServer).start();
		verify(applicationServer).deploy("target/wars/mockserver.war");
	}

	@Test
	public void startupError() throws Exception {
		Exception exception = new RuntimeException();
		doThrow(exception).when(applicationServer).start();

		expectedException.expect(ServerException.class);
		expectedException.expectCause(is(exception));
		mockServer.start();

		verify(applicationServer, never()).deploy(any(String.class));
	}

	@Test
	public void stop() throws Exception {
		mockServer.stop();

		verify(applicationServer).stop();
	}

	@Test
	public void stopError() throws Exception {
		Exception exception = new RuntimeException();
		doThrow(exception).when(applicationServer).stop();

		expectedException.expect(ServerException.class);
		expectedException.expectCause(is(exception));
		mockServer.stop();
	}

	@Test
	public void baseUri() throws Exception {
		when(applicationServer.getHttpPort()).thenReturn(12345);

		assertThat(mockServer.getBaseUri(), is("http://localhost:12345"));
	}
}
