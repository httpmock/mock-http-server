package de.sn.mock.rules;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.sn.mock.dto.ConfigurationDto;
import de.sn.mock.dto.RequestDto;
import de.sn.mock.dto.ResponseDto;
import de.sn.mock.dto.VerifyResponseDto;
import de.sn.mock.times.Times;

@RunWith(MockitoJUnitRunner.class)
public class HttpMockTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Mock
	private HttpMockServer mockServer;

	@Mock
	private MockService mockService;

	@InjectMocks
	private HttpMock httpMock = new HttpMock(mockServer);

	@Before
	public void setup() {
		when(mockServer.getMockService()).thenReturn(mockService);
	}

	@Test
	public void configure() throws Exception {
		ConfigurationDto config = mock(ConfigurationDto.class);
		httpMock.configure(config);
		verify(mockService).configure(config);
	}

	@Test
	public void delete() throws Exception {
		httpMock.delete();
		verify(mockService).delete();
	}

	@Test
	public void requestUrl() throws Exception {
		when(mockService.getRequestUrl()).thenReturn("some url");
		assertThat(httpMock.getRequestUrl(), is("some url"));
	}

	@Test
	public void verifyMock_failed() throws Exception {
		RequestDto request = mock(RequestDto.class);
		when(mockService.verify(request)).thenReturn(numberOfTimes(0));

		expectedException.expect(MockVerifyException.class);
		httpMock.verify(request, Times.once());
	}

	@Test
	public void verifyMock_ok() throws Exception {
		RequestDto request = mock(RequestDto.class);
		when(mockService.verify(request)).thenReturn(numberOfTimes(1));

		httpMock.verify(request, Times.once());
	}

	private VerifyResponseDto numberOfTimes(int times) {
		VerifyResponseDto response = new VerifyResponseDto();
		response.setTimes(times);
		return response;
	}

	@Test
	public void startStubbing() throws Exception {
		RequestDto request = mock(RequestDto.class);
		Stubbing stubbing = httpMock.when(request);
		assertThat(stubbing, is(notNullValue()));
	}

	@Test
	public void completeStubbing() throws Exception {
		RequestDto request = mock(RequestDto.class);
		ResponseDto response = mock(ResponseDto.class);

		httpMock.when(request).thenRespond(response);

		verify(mockService).configure(any(ConfigurationDto.class));
	}

	@Test
	public void createNewMockBeforeTest() throws Throwable {
		httpMock.before();
		verify(mockService).create();
	}

	@Test
	public void deleteMockAfterTest() throws Throwable {
		httpMock.after();
		verify(mockService).delete();
	}
}
