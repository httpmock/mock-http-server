package de.sn.mock.builder;

import static de.sn.mock.util.CollectionUtil.emptyMap;

import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import de.sn.mock.dto.ResponseDto;

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
		return payload(string.getBytes());
	}

	public ResponseBuilder payload(byte[] bytes) {
		this.payload = new String(Base64.encodeBase64(bytes));
		return this;
	}

	public ResponseBuilder contentType(String contentType) {
		return header("Content-Type", contentType);
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
		responseDto.setStatusCode(statusCode);
		responseDto.setHeaders(headers);
		return responseDto;
	}

	public static ResponseBuilder response() {
		return new ResponseBuilder();
	}

}
