package com.github.httpmock.rules;

import org.junit.rules.ExternalResource;

import com.github.httpmock.MockServer;

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
