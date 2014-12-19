package de.sn.mock.rules;

import org.junit.rules.ExternalResource;

import de.sn.mock.ServerException;
import de.sn.mock.TomEEStandalone;

public class HttpMockServer extends ExternalResource {
	private TomEEStandalone applicationServer;
	private static final String MOCK_SERVER_CONTEXT = "/mockserver";

	@Override
	protected void before() throws Throwable {
		startServer();
	}

	private void startServer() {
		applicationServer = createApplicationServer();
		try {
			applicationServer.start();
		} catch (Exception e) {
			throw new ServerException(e);
		}
		applicationServer.deploy("target/wars/mockserver.war");
	}

	TomEEStandalone createApplicationServer() {
		return new TomEEStandalone();
	}

	@Override
	protected void after() {
		stopServer();
	}

	private void stopServer() {
		try {
			applicationServer.stop();
		} catch (Exception e) {
			throw new ServerException(e);
		}
	}

	public int getPort() {
		return applicationServer.getHttpPort();
	}

	public MockService getMockService() {
		return new MockService(getBaseUri(), MOCK_SERVER_CONTEXT);
	}

	private String getBaseUri() {
		int port = getPort();
		String host = "localhost";
		return String.format("http://%s:%d", host, port);
	}
}
