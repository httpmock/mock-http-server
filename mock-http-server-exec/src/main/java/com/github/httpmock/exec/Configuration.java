package com.github.httpmock.exec;

public class Configuration {
	private int httpPort;
	private int stopPort;
	private int ajpPort;

	public int getHttpPort() {
		return httpPort;
	}

	public void setHttpPort(int httpPort) {
		this.httpPort = httpPort;
	}

	public int getStopPort() {
		return stopPort;
	}

	public void setStopPort(int stopPort) {
		this.stopPort = stopPort;
	}

	public int getAjpPort() {
		return ajpPort;
	}

	public void setAjpPort(int ajpPort) {
		this.ajpPort = ajpPort;
	}
}
