package de.sn.mock;

import java.io.File;
import java.io.IOException;

import javax.naming.NamingException;

import org.apache.openejb.OpenEJBException;
import org.apache.tomee.embedded.Configuration;
import org.apache.tomee.embedded.Container;

public class TomEEStandalone {
	private final Container container;
	private final int serverPort;
	private final int stopPort;

	public static void main(String[] args) {
		TomEEStandalone tomee = new TomEEStandalone(9090, 9099);
		try {
			tomee.start();
			tomee.deploy(getPathToWar(args));
			tomee.waitUntilStop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String getPathToWar(String[] args) {
		int firstArgument = 0;
		if (args.length > firstArgument)
			return args[firstArgument];
		return "target/wars/mockserver.war";
	}

	public void start() throws Exception {
		container.start();
	}

	private void waitUntilStop() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					container.stop();
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		});
		container.await();
	}

	public TomEEStandalone(int serverPort, int stopPort) {
		this(serverPort, stopPort, new Container());
	}

	TomEEStandalone(int serverPort, int stopPort, Container container) {
		this.serverPort = serverPort;
		this.stopPort = stopPort;
		this.container = container;
		this.container.setup(createConfiguration());
	}

	public void deploy(String pathToWar) {
		try {
			container.deploy("rgw-mock-broker", new File(pathToWar));
		} catch (OpenEJBException | IOException | NamingException e) {
			e.printStackTrace();
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
}