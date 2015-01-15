package com.github.httpmock.exec;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import com.github.httpmock.MockServer;
import com.github.httpmock.ServerException;
import com.jayway.awaitility.Awaitility;
import com.jayway.awaitility.Duration;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.RestAssuredConfig;

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
		startServerInBackground();
		waitUntilServerIsStarted();
		Logger.getLogger(getClass().getName()).info("server is started");
	}

	void startServerInBackground() {
		runner = runnerFactory.create(config);
		ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
		try {
			singleThreadExecutor.submit(runner).get();
		} catch (InterruptedException e) {
			throw new ServerException(e);
		} catch (ExecutionException e) {
			throw new ServerException(e);
		}
	}

	public void waitUntilServerIsStarted() {
		Awaitility.await().catchUncaughtExceptions()//
				.atMost(Duration.ONE_MINUTE)//
				.pollDelay(Duration.TWO_SECONDS)//
				.pollInterval(Duration.ONE_SECOND)//
				.until(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						Logger.getLogger(getClass().getName()).info("waiting for server to start");
						return isServerStarted();
					}
				});
	}

	public boolean isServerStarted() {
		RestAssuredConfig config = RestAssured.config().httpClient(RestAssuredConfig.config().getHttpClientConfig().setParam("CONNECTION_MANAGER_TIMEOUT", 1000));
		return RestAssured.given().config(config).baseUri(getBaseUri()).basePath("/mockserver").get("/").statusCode() == 200;
	}

	@Override
	public void stop() {
		try {
			runner.stopServer();
		} catch (Exception e) {
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
