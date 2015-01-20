package com.github.httpmock.exec;

import java.util.concurrent.*;
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
	private Future<Void> runnerFuture;

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
		runnerFuture = singleThreadExecutor.submit(runner);
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
		try {
			RestAssuredConfig config = RestAssured.config().httpClient(RestAssuredConfig.config().getHttpClientConfig().setParam("CONNECTION_MANAGER_TIMEOUT", 1000));
			return RestAssured.given().config(config).baseUri(getBaseUri()).basePath("/mockserver").get("/").statusCode() == 200;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public void stop() {
		try {
			runner.stopServer();
			runnerFuture.get(5, TimeUnit.SECONDS);
		} catch (Exception e) {
		}
		runnerFuture.cancel(true);
	}

	@Override
	public String getBaseUri() {
		return String.format("http://localhost:%d", config.getHttpPort());
	}

}
