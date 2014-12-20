package de.sn.mock;

import java.io.IOException;
import java.net.ServerSocket;

import org.apache.commons.io.IOUtils;

public abstract class PortUtil {

	public static int getRandomPort() {
		ServerSocket serverSocket = null;
		try {
			serverSocket = createServerSocket();
			int port = serverSocket.getLocalPort();
			serverSocket.close();
			return port;
		} catch (IOException e) {
			throw new ServerException(e);
		} finally {
			IOUtils.closeQuietly(serverSocket);
		}
	}

	private static ServerSocket createServerSocket() throws IOException {
		return new ServerSocket(0);
	}

}
