package de.sn.mock;

import javax.ejb.Stateless;

import de.sn.mock.dto.RequestDto;

@Stateless
public class RequestMatcher {

	public boolean matches(RequestDto configuredRequest, RequestDto request1) {
		return isMethodMatching(configuredRequest, request1)
				&& isUrlMatching(configuredRequest, request1)
				&& isContentTypeMatching(configuredRequest, request1);
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
