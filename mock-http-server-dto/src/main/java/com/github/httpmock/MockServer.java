package com.github.httpmock;

public interface MockServer {

	void start();

	void stop();

	String getBaseUri();

}