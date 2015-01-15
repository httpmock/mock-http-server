package com.github.httpmock.rules;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;

import org.apache.commons.io.IOUtils;

import com.github.httpmock.ApplicationServerRunner;
import com.github.httpmock.Configuration;
import com.github.httpmock.MockServer;
import com.github.httpmock.ServerException;
import com.jayway.awaitility.Awaitility;
import com.jayway.awaitility.Duration;
import com.jayway.restassured.RestAssured;

public class StandaloneMockServer implements MockServer {

	private Configuration config;
	private ApplicationServerRunnerFactory runnerFactory;
	private ApplicationServerRunner runner;

	public StandaloneMockServer(Configuration config) {
		this(config, new ApplicationServerRunnerFactory());
	}

	StandaloneMockServer(Configuration config, ApplicationServerRunnerFactory runnerFactory) {
		this.config = config;
		this.runnerFactory = runnerFactory;
	}

	@Override
	public void start() {
		runner = runnerFactory.create(config);
		runner.start();
		waitUntilServerIsStarted();
	}

	public void waitUntilServerIsStarted() {
		Awaitility.await().atMost(Duration.ONE_MINUTE).until(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return isServerStarted();
			}
		});
	}

	public boolean isServerStarted() {
		return RestAssured.given().baseUri(getBaseUri()).basePath("/mockserver").get("/").statusCode() == 200;
	}

	@Override
	public void stop() {
		Socket socket = null;
		PrintWriter printWriter = null;
		try {
			socket = createStopSocket();
			printWriter = new PrintWriter(socket.getOutputStream());
			printWriter.print("SHUTDOWN");
			printWriter.flush();
			runner.join();
		} catch (IOException e) {
			throw new ServerException(e);
		} catch (InterruptedException e) {
			throw new ServerException(e);
		} finally {
			IOUtils.closeQuietly(printWriter);
			IOUtils.closeQuietly(socket);
		}
	}

	Socket createStopSocket() throws UnknownHostException, IOException {
		return new Socket("localhost", config.getStopPort());
	}

	@Override
	public String getBaseUri() {
		return String.format("http://localhost:%d", config.getHttpPort());
	}

}
