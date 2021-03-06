package com.github.httpmock.junit.rules;

import org.junit.rules.ExternalResource;

import com.github.httpmock.api.MockService;
import com.github.httpmock.api.MockVerifyException;
import com.github.httpmock.api.Stubbing;
import com.github.httpmock.api.times.Times;
import com.github.httpmock.builder.RequestBuilder;
import com.github.httpmock.dto.ConfigurationDto;
import com.github.httpmock.dto.RequestDto;
import com.github.httpmock.dto.VerifyResponseDto;

public class HttpMockRule extends ExternalResource implements HttpMock {

	private static final String VERIFICATION_FAILED = "Mock verification failed. Request was called %d times but should have been called %s";
	private HttpMockServerContext mockServer;

	private MockService mockService;

	public HttpMockRule(HttpMockServerContext mockServer) {
		this.mockServer = mockServer;
	}

	@Override
	protected void before() throws Throwable {
		create();
	}

	void create() {
		mockService = mockServer.getMockService();
		mockService.create();
	}

	@Override
	protected void after() {
		delete();
	}

	@Override
	public void delete() {
		mockService.delete();
	}

	@Override
	public Stubbing when(RequestDto request) {
		return new Stubbing(mockService, request);
	}

	@Override
	public Stubbing when(RequestBuilder requestBuilder) {
		return when(requestBuilder.build());
	}

	@Override
	public void configure(ConfigurationDto config) {
		mockService.configure(config);
	}

	@Override
	public String getRequestUrl() {
		return mockService.getRequestUrl();
	}

	@Override
	public void verify(RequestDto request, Times times) {
		int numberOfCalls = getNumberOfCalls(request);
		if (!times.matches(numberOfCalls)) {
			throw new MockVerifyException(String.format(VERIFICATION_FAILED, numberOfCalls, times.getFailedDescription()));
		}
	}

	@Override
	public void verify(RequestBuilder requestBuilder, Times times) {
		verify(requestBuilder.build(), times);
	}

	private int getNumberOfCalls(RequestDto request) {
		VerifyResponseDto verifyResponse = getVerifyResponse(request);
		int numberOfCalls = verifyResponse.getTimes();
		return numberOfCalls;
	}

	private VerifyResponseDto getVerifyResponse(RequestDto request) {
		return mockService.verify(request);
	}
}
