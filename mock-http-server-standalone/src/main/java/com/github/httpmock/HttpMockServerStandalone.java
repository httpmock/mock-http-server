package com.github.httpmock;

import java.io.File;
import java.io.IOException;

import javax.naming.NamingException;

import org.apache.openejb.OpenEJBException;
import org.apache.tomee.embedded.Configuration;
import org.apache.tomee.embedded.Container;

public class HttpMockServerStandalone {
	private static final int PORT_STOP_DEFAULT = 9099;
	private static final int PORT_HTTP_DEFAULT = 9090;
	private static final String ENV_HTTP_PORT = "HTTP_MOCK_SERVER_PORT_HTTP";
	private static final String ENV_STOP_PORT = "HTTP_MOCK_SERVER_PORT_STOP";

	final Container container;
	private final int serverPort;
	private final int stopPort;

	public static void main(String[] args) {
		HttpMockServerStandalone mockServer = new HttpMockServerStandalone(//
				getConfiguredHttpPort(), //
				getConfiguredStopPort());
		mockServer.start();
		mockServer.deploy(getPathToWar(args));
		mockServer.waitUntilStop();
	}

	static int getConfiguredStopPort() {
		int stopPort = PORT_STOP_DEFAULT;
		if (System.getenv(ENV_STOP_PORT) != null)
			stopPort = Integer.parseInt(System.getenv(ENV_STOP_PORT));
		return stopPort;
	}

	static int getConfiguredHttpPort() {
		int httpPort = PORT_HTTP_DEFAULT;
		if (System.getenv(ENV_HTTP_PORT) != null)
			httpPort = Integer.parseInt(System.getenv(ENV_HTTP_PORT));
		return httpPort;
	}

	static String getPathToWar(String[] args) {
		int firstArgument = 0;
		if (args.length > firstArgument)
			return args[firstArgument];
		return "target/wars/mockserver.war";
	}

	public HttpMockServerStandalone(int serverPort, int stopPort) {
		this(serverPort, stopPort, new Container());
	}

	HttpMockServerStandalone(int serverPort, int stopPort, Container container) {
		this.serverPort = serverPort;
		this.stopPort = stopPort;
		this.container = container;
		this.container.setup(createConfiguration());
	}

	public void start() {
		try {
			container.start();
		} catch (Exception e) {
			throw new ServerException(e);
		}
	}

	public void deploy(String pathToWar) {
		try {
			container.deploy("mockserver", new File(pathToWar));
		} catch (OpenEJBException | IOException | NamingException e) {
			throw new ServerException(e);
		}
	}

	private Configuration createConfiguration() {
		final Configuration config = new Configuration();
		config.setDir(System.getProperty("java.io.tmpdir"));
		config.setHttpPort(serverPort);
		config.setStopPort(stopPort);
		config.setDir(new File(new File("target"), "apache-tomee").getAbsolutePath());
		return config;
	}

	public void stop() throws Exception {
		container.stop();
	}

	void waitUntilStop() {
		Runtime.getRuntime().addShutdownHook(new ShutdownHook(container));
		container.await();
	}

	public int getHttpPort() {
		return serverPort;
	}
}