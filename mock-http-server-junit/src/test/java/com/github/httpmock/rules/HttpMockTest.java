package com.github.httpmock.rules;

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

import com.github.httpmock.dto.ConfigurationDto;
import com.github.httpmock.dto.RequestDto;
import com.github.httpmock.dto.ResponseDto;
import com.github.httpmock.dto.VerifyResponseDto;
import com.github.httpmock.times.ExcatlyOnce;

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
		httpMock.verify(request, ExcatlyOnce.once());
	}

	@Test
	public void verifyMock_ok() throws Exception {
		RequestDto request = mock(RequestDto.class);
		when(mockService.verify(request)).thenReturn(numberOfTimes(1));

		httpMock.verify(request, ExcatlyOnce.once());
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
