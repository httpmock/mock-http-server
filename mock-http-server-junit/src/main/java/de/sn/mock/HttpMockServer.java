package de.sn.mock;

import org.junit.rules.ExternalResource;

public class HttpMockServer extends ExternalResource {
	private TomEEStandalone standalone;

	@Override
	protected void before() throws Throwable {
		startServer();
	}

	private void startServer() {
		standalone = new TomEEStandalone();
		try {
			standalone.start();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		standalone.deploy("target/wars/mockserver.war");
	}

	@Override
	protected void after() {
		stopServer();
	}

	private void stopServer() {
		try {
			standalone.stop();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public int getPort() {
		return standalone.getHttpPort();
	}
}
