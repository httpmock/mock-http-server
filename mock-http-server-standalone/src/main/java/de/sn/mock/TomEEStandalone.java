package de.sn.mock;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;

import javax.naming.NamingException;

import org.apache.commons.io.IOUtils;
import org.apache.openejb.OpenEJBException;
import org.apache.tomee.embedded.Configuration;
import org.apache.tomee.embedded.Container;

public class TomEEStandalone {
	final Container container;
	private final int serverPort;
	private final int stopPort;

	public static void main(String[] args) {
		TomEEStandalone tomee = new TomEEStandalone(9090, 9099);
		tomee.start();
		tomee.deploy(getPathToWar(args));
		tomee.waitUntilStop();
	}

	private static String getPathToWar(String[] args) {
		int firstArgument = 0;
		if (args.length > firstArgument)
			return args[firstArgument];
		return "target/wars/mockserver.war";
	}

	public void start() {
		try {
			container.start();
		} catch (Exception e) {
			throw new ServerException(e);
		}
	}

	private void waitUntilStop() {
		Runtime.getRuntime().addShutdownHook(new ShutdownHook(container));
		container.await();
	}

	public TomEEStandalone(int serverPort, int stopPort) {
		this(serverPort, stopPort, new Container());
	}

	public TomEEStandalone() {
		this(getRandomPort(), getRandomPort());
	}

	private static int getRandomPort() {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(0);
			int port = serverSocket.getLocalPort();
			serverSocket.close();
			return port;
		} catch (IOException e) {
			throw new ServerException(e);
		} finally {
			IOUtils.closeQuietly(serverSocket);
		}
	}

	TomEEStandalone(int serverPort, int stopPort, Container container) {
		this.serverPort = serverPort;
		this.stopPort = stopPort;
		this.container = container;
		this.container.setup(createConfiguration());
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
		config.setDir(new File(new File("target"), "apache-tomee")
		.getAbsolutePath());
		return config;
	}

	public void stop() throws Exception {
		container.stop();
	}

	public int getHttpPort() {
		return serverPort;
	}
}