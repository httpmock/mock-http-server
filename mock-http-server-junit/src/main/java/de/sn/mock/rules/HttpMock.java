package de.sn.mock.rules;

import org.junit.rules.ExternalResource;

import de.sn.mock.dto.ConfigurationDto;
import de.sn.mock.dto.RequestDto;
import de.sn.mock.dto.VerifyResponseDto;
import de.sn.mock.times.Times;

public class HttpMock extends ExternalResource {

	private static final String VERIFICATION_FAILED = "Mock verification failed. Request was called %d times but should have been called %s";
	private HttpMockServer mockServer;

	private MockService mockService;

	public HttpMock(HttpMockServer mockServer) {
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

	public void delete() {
		mockService.delete();
	}

	public Stubbing when(RequestDto request) {
		return new Stubbing(mockService, request);
	}

	public void configure(ConfigurationDto config) {
		mockService.configure(config);
	}

	public String getRequestUrl() {
		return mockService.getRequestUrl();
	}

	public void verify(RequestDto request, Times times) {
		int numberOfCalls = getNumberOfCalls(request);
		if (!times.matches(numberOfCalls)) {
			throw new MockVerifyException(String.format(VERIFICATION_FAILED,
					numberOfCalls, times.getFailedDescription()));
		}
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
