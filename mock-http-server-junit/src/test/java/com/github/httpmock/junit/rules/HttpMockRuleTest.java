package com.github.httpmock.junit.rules;

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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.httpmock.api.MockService;
import com.github.httpmock.api.MockVerifyException;
import com.github.httpmock.api.Stubbing;
import com.github.httpmock.api.times.ExactlyOnce;
import com.github.httpmock.builder.RequestBuilder;
import com.github.httpmock.builder.ResponseBuilder;
import com.github.httpmock.dto.ConfigurationDto;
import com.github.httpmock.dto.RequestDto;
import com.github.httpmock.dto.ResponseDto;
import com.github.httpmock.dto.VerifyResponseDto;

@RunWith(MockitoJUnitRunner.class)
public class HttpMockRuleTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Mock
	private HttpMockServerContext mockServer;

	@Mock
	private MockService mockService;

	@InjectMocks
	private HttpMockRule httpMockRule = new HttpMockRule(mockServer);

	@Before
	public void setup() {
		when(mockServer.getMockService()).thenReturn(mockService);
	}

	@Test
	public void configure() throws Exception {
		ConfigurationDto config = mock(ConfigurationDto.class);
		httpMockRule.configure(config);
		verify(mockService).configure(config);
	}

	@Test
	public void delete() throws Exception {
		httpMockRule.delete();
		verify(mockService).delete();
	}

	@Test
	public void requestUrl() throws Exception {
		when(mockService.getRequestUrl()).thenReturn("some url");
		assertThat(httpMockRule.getRequestUrl(), is("some url"));
	}

	@Test
	public void verifyMock_failed() throws Exception {
		RequestDto request = mock(RequestDto.class);
		when(mockService.verify(request)).thenReturn(numberOfTimes(0));

		expectedException.expect(MockVerifyException.class);
		httpMockRule.verify(request, ExactlyOnce.once());
	}

	@Test
	public void verifyMock_ok() throws Exception {
		RequestDto request = mock(RequestDto.class);
		when(mockService.verify(request)).thenReturn(numberOfTimes(1));

		httpMockRule.verify(request, ExactlyOnce.once());
	}

	private VerifyResponseDto numberOfTimes(int times) {
		VerifyResponseDto response = new VerifyResponseDto();
		response.setTimes(times);
		return response;
	}

	@Test
	public void startStubbing() throws Exception {
		RequestDto request = mock(RequestDto.class);
		Stubbing stubbing = httpMockRule.when(request);
		assertThat(stubbing, is(notNullValue()));
	}

	@Test
	public void completeStubbing() throws Exception {
		RequestDto request = mock(RequestDto.class);
		ResponseDto response = mock(ResponseDto.class);

		httpMockRule.when(request).thenRespond(response);

		verify(mockService).configure(any(ConfigurationDto.class));
	}

	@Test
	public void stubbingUsingBuilders() throws Exception {
		RequestBuilder requestBuilder = RequestBuilder.request();
		ResponseBuilder responseBuilder = ResponseBuilder.response();
		httpMockRule.when(requestBuilder).then(responseBuilder);

		ArgumentCaptor<ConfigurationDto> configCaptor = ArgumentCaptor.forClass(ConfigurationDto.class);
		verify(mockService).configure(configCaptor.capture());
		ConfigurationDto config = configCaptor.getValue();
		assertThat(config.getRequest(), is(requestBuilder.build()));
		assertThat(config.getResponse(), is(responseBuilder.build()));
	}

	@Test
	public void createNewMockBeforeTest() throws Throwable {
		httpMockRule.before();
		verify(mockService).create();
	}

	@Test
	public void deleteMockAfterTest() throws Throwable {
		httpMockRule.after();
		verify(mockService).delete();
	}
}
