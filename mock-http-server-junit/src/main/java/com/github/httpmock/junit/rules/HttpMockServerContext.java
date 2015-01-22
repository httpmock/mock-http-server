package com.github.httpmock.junit.rules;

import com.github.httpmock.MockServer;
import com.github.httpmock.api.MockService;
import org.junit.rules.ExternalResource;

public class HttpMockServerContext extends ExternalResource {
	private static final String MOCK_SERVER_CONTEXT = "/mockserver";
	private MockServer mockServer;

	public HttpMockServerContext(MockServer mockServer) {
		this.mockServer = mockServer;
	}

	@Override
	protected void before() throws Throwable {
		mockServer.start();
	}

	@Override
	protected void after() {
		stop();
	}

	public void stop() {
		mockServer.stop();
	}

	public MockService getMockService() {
		return new MockService(getBaseUri(), MOCK_SERVER_CONTEXT);
	}

	public String getBaseUri() {
		return mockServer.getBaseUri();
	}

}
