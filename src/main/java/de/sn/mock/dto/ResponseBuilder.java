package de.sn.mock.dto;

import static de.sn.mock.util.CollectionUtil.emptyMap;
import static org.apache.commons.codec.binary.Base64.encodeBase64String;

import java.util.Map;

public class ResponseBuilder {
	private String payload;
	private String contentType;
	private int statusCode;
	private Map<String, String> headers;

	public ResponseBuilder() {
		statusCode = 200;
		headers = emptyMap();
	}

	public ResponseBuilder payload(String string) {
		this.payload = encodeBase64String(string.getBytes());
		return this;
	}

	public ResponseBuilder contentType(String contentType) {
		this.contentType = contentType;
		return this;
	}

	public ResponseBuilder statusCode(int statusCode) {
		this.statusCode = statusCode;
		return this;
	}

	public ResponseBuilder header(String key, String value) {
		headers.put(key, value);
		return this;
	}

	public ResponseDto build() {
		ResponseDto responseDto = new ResponseDto();
		responseDto.setPayload(payload);
		responseDto.setContentType(contentType);
		responseDto.setStatusCode(statusCode);
		responseDto.setHeaders(headers);
		return responseDto;
	}

	public static ResponseBuilder response() {
		return new ResponseBuilder();
	}
}
