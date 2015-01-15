package com.github.httpmock.exec;

public class ConfigurationBuilder {

	private Configuration config;

	public ConfigurationBuilder() {
		this.config = new Configuration();
	}

	public ConfigurationBuilder httpPort(int httpPort) {
		config.setHttpPort(httpPort);
		return this;
	}

	public ConfigurationBuilder stopPort(int stopPort) {
		config.setStopPort(stopPort);
		return this;
	}

	public ConfigurationBuilder ajpPort(int ajpPort) {
		config.setAjpPort(ajpPort);
		return this;
	}

	public Configuration build() {
		return config;
	}

	public static ConfigurationBuilder config() {
		return new ConfigurationBuilder();
	}

}
