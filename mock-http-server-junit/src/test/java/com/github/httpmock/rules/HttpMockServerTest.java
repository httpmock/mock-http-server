package com.github.httpmock.rules;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.httpmock.ServerException;
import com.github.httpmock.TomEEStandalone;
import com.github.httpmock.rules.HttpMockServer;
import com.github.httpmock.rules.MockService;

@RunWith(MockitoJUnitRunner.class)
public class HttpMockServerTest {

	@Spy
	private HttpMockServer httpMockServer = new HttpMockServer();

	@Mock
	private TomEEStandalone applicationServer;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Before
	public void setup() {
		Mockito.doReturn(applicationServer).when(httpMockServer)
		.createApplicationServer();
	}

	@Test
	public void startServerBeforeTest() throws Throwable {
		httpMockServer.before();
		verify(applicationServer).start();
		verify(applicationServer).deploy(anyString());
		verify(applicationServer, Mockito.never()).stop();
	}

	@Test
	public void stopServerAfterTest() throws Throwable {
		httpMockServer.before();
		httpMockServer.after();
		verify(applicationServer).stop();
	}

	@Test
	public void getMockService() throws Throwable {
		httpMockServer.before();
		when(applicationServer.getHttpPort()).thenReturn(123);

		MockService mockService = httpMockServer.getMockService();
		assertThat(mockService, is(notNullValue()));
	}

	@Test
	public void startError() throws Throwable {
		Exception exception = mock(RuntimeException.class);
		doThrow(exception).when(applicationServer).start();

		expectedException.expect(ServerException.class);
		expectedException.expectCause(is(exception));

		httpMockServer.before();
	}

	@Test
	public void stopError() throws Throwable {
		Exception exception = mock(RuntimeException.class);
		doThrow(exception).when(applicationServer).stop();

		expectedException.expect(ServerException.class);
		expectedException.expectCause(is(exception));

		httpMockServer.before();
		httpMockServer.after();
	}

}
