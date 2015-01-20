package com.github.httpmock.request;

import javax.ejb.Stateless;

import com.github.httpmock.dto.RequestDto;

@Stateless
public class RequestMatcher {

	public boolean matches(RequestDto configuredRequest, RequestDto request) {
		return isMethodMatching(configuredRequest, request)
				&& isUrlMatching(configuredRequest, request)
				&& isContentTypeMatching(configuredRequest, request)
				&& isContentMatching(configuredRequest, request);
	}

	private boolean isContentMatching(RequestDto configuredRequest, RequestDto request) {
		return configuredRequest.getPayload() == null ||
		configuredRequest.getPayload().equals(request.getPayload());
	}

	private boolean isMethodMatching(RequestDto configuredRequest,
			RequestDto request1) {
		return request1.getMethod().equalsIgnoreCase(
				configuredRequest.getMethod());
	}

	private boolean isUrlMatching(RequestDto configuredRequest,
			RequestDto request1) {
		Url incommingUrl = new Url(request1.getUrl());
		Url configuredUrl = new Url(configuredRequest.getUrl());
		return incommingUrl.getPath().matches(configuredUrl.getPath())
				&& incommingUrl.getQueryParameters().equals(
						configuredUrl.getQueryParameters());
	}

	private boolean isContentTypeMatching(RequestDto configuredRequest,
			RequestDto incomingRequest) {
		if (configuredRequest.getContentType() == null)
			return true;
		return incomingRequest.getContentType().matches(
				configuredRequest.getContentType());
	}

}
