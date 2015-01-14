package com.github.httpmock.rules;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.Callable;

import org.apache.commons.io.IOUtils;

import com.github.httpmock.Configuration;
import com.github.httpmock.ExecRunner;
import com.github.httpmock.MockServer;
import com.jayway.awaitility.Awaitility;
import com.jayway.awaitility.Duration;
import com.jayway.restassured.RestAssured;

public class StandaloneMockServer implements MockServer {

	private Configuration config;
	private ExecRunner runner;

	public StandaloneMockServer(Configuration config) {
		this.config = config;
	}

	@Override
	public void start() {
		runner = new ExecRunner(config, ExecRunner.readProperties());
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
		try {
			socket = new Socket("localhost", config.getStopPort());
			PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
			printWriter.print("SHUTDOWN");
			printWriter.flush();
			socket.close();
			runner.join();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(socket);
		}
	}

	@Override
	public String getBaseUri() {
		return String.format("http://localhost:%d", config.getHttpPort());
	}

}
