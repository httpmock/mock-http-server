package com.github.httpmock.exec;

import com.github.httpmock.ServerException;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;

import static com.github.httpmock.util.CollectionUtil.emptyList;

public abstract class PortUtil {

	public static List<Integer> getRandomPorts(int numberOfPorts) {
		List<ServerSocket> serverSockets = createServerSockets(numberOfPorts);
		List<Integer> ports = emptyList(numberOfPorts);
		for (ServerSocket serverSocket : serverSockets) {
			ports.add(serverSocket.getLocalPort());
			IOUtils.closeQuietly(serverSocket);
		}
		return ports;
	}

	private static List<ServerSocket> createServerSockets(int numberOfSockets) {
		List<ServerSocket> serverSockets = emptyList(numberOfSockets);
		for (int i = 1; i <= numberOfSockets; i++) {
			try {
				serverSockets.add(createServerSocket());
			} catch (IOException e) {
				closeAllSockets(serverSockets);
				throw new ServerException(e);
			}
		}
		return serverSockets;
	}

	private static void closeAllSockets(List<ServerSocket> serverSockets) {
		for (ServerSocket serverSocket : serverSockets) {
			IOUtils.closeQuietly(serverSocket);
		}
	}

	private static ServerSocket createServerSocket() throws IOException {
		return new ServerSocket(0);
	}

}
