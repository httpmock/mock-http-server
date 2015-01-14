package com.github.httpmock;

import static com.github.httpmock.PortUtil.getRandomPorts;

import java.util.List;

import com.github.httpmock.MockServer;

public class EmbeddedMockServer implements MockServer {
	private ApplicationServerStandalone applicationServer;

	@Override
	public void start() {
		applicationServer = createApplicationServer();
		try {
			applicationServer.start();
		} catch (Exception e) {
			throw new ServerException(e);
		}
		applicationServer.deploy("target/wars/mockserver.war");
	}

	ApplicationServerStandalone createApplicationServer() {
		List<Integer> randomPorts = getRandomPorts(2);
		return new ApplicationServerStandalone(randomPorts.get(0), randomPorts.get(1));
	}

	@Override
	public void stop() {
		try {
			applicationServer.stop();
		} catch (Exception e) {
			throw new ServerException(e);
		}
	}

	public int getPort() {
		return applicationServer.getHttpPort();
	}

	@Override
	public String getBaseUri() {
		int port = getPort();
		String host = "localhost";
		return String.format("http://%s:%d", host, port);
	}

}
