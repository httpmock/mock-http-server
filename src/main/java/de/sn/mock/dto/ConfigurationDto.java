package de.sn.mock.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ConfigurationDto {

	private RequestDto request;
	private ResponseDto response;

	public ConfigurationDto() {
	}

	public ConfigurationDto(RequestDto request, ResponseDto response) {
		this.request = request;
		this.response = response;
	}

	public RequestDto getRequest() {
		return request;
	}

	public void setRequest(RequestDto request) {
		this.request = request;
	}

	public ResponseDto getResponse() {
		return response;
	}

	public void setResponse(ResponseDto response) {
		this.response = response;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
}
