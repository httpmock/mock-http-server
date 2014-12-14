package de.sn.mock.builder;

import de.sn.mock.dto.RequestDto;

public class RequestBuilder {

	private String url;
	private String method;
	private String contentType;

	public RequestBuilder() {
		this.method = "GET";
		this.contentType = null;
	}

	public RequestBuilder url(String url) {
		this.url = url;
		return this;
	}

	public RequestBuilder post(String url) {
		return method("POST").url(url);
	}

	public RequestBuilder get(String url) {
		return method("GET").url(url);
	}

	public RequestBuilder method(String method) {
		this.method = method;
		return this;
	}

	public RequestBuilder contentType(String contentType) {
		this.contentType = contentType;
		return this;
	}

	public RequestDto build() {
		RequestDto requestDto = new RequestDto();
		requestDto.setMethod(method);
		requestDto.setUrl(url);
		requestDto.setContentType(contentType);
		return requestDto;
	}

	public static RequestBuilder request() {
		return new RequestBuilder();
	}

}
