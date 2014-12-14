package de.sn.mock.rules;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import de.sn.mock.TomEEStandalone;

@RunWith(MockitoJUnitRunner.class)
public class HttpMockServerTest {

	@Spy
	private HttpMockServer httpMockServer = new HttpMockServer();

	@Mock
	private TomEEStandalone applicationServer;

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
}
