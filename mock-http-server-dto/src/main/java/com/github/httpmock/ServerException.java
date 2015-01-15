package com.github.httpmock;

public class ServerException extends RuntimeException {

	public ServerException(Throwable throwable) {
		super(throwable);
	}

	public ServerException(String message) {
		super(message);
	}

	private static final long serialVersionUID = 1L;

}
