package com.github.httpmock.rules;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.httpmock.MockServer;
import com.github.httpmock.ServerException;

@RunWith(MockitoJUnitRunner.class)
public class HttpMockServerContextTest {

	private MockServer mockServer;

	private HttpMockServerContext httpMockServer;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Before
	public void setup() {
		mockServer = mock(MockServer.class);
		httpMockServer = spy(new HttpMockServerContext(mockServer));
	}

	@Test
	public void startServerBeforeTest() throws Throwable {
		httpMockServer.before();
		verify(mockServer).start();
		verify(mockServer, never()).stop();
	}

	@Test
	public void stopServerAfterTest() throws Throwable {
		httpMockServer.before();
		httpMockServer.after();
		verify(mockServer).stop();
	}

	@Test
	public void getMockService() throws Throwable {
		httpMockServer.before();

		MockService mockService = httpMockServer.getMockService();
		assertThat(mockService, is(notNullValue()));
	}

	@Test
	public void startError() throws Throwable {
		Exception exception = new ServerException(mock(RuntimeException.class));
		doThrow(exception).when(mockServer).start();

		expectedException.expect(ServerException.class);

		httpMockServer.before();
	}

	@Test
	public void stopError() throws Throwable {
		Exception exception = new ServerException(mock(RuntimeException.class));
		doThrow(exception).when(mockServer).stop();

		expectedException.expect(ServerException.class);

		httpMockServer.before();
		httpMockServer.after();
	}

}
