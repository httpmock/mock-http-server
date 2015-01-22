package com.github.httpmock;

import java.util.List;

import static com.github.httpmock.exec.PortUtil.getRandomPorts;

public class EmbeddedMockServer implements MockServer {
	private ApplicationServerStandalone applicationServer;

	public EmbeddedMockServer() {
		this(createApplicationServer());
	}

	public EmbeddedMockServer(ApplicationServerStandalone applicationServer) {
		this.applicationServer = applicationServer;
	}

	@Override
	public void start() {
		try {
			applicationServer.start();
		} catch (Exception e) {
			throw new ServerException(e);
		}
		applicationServer.deploy("target/wars/mockserver.war");
	}

	static ApplicationServerStandalone createApplicationServer() {
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
		return String.format("http://localhost:%d", getPort());
	}

}
