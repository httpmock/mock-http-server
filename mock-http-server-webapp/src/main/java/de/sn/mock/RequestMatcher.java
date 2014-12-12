package de.sn.mock;

import static de.sn.mock.util.UrlNormalizer.normalizeUrl;

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
		String incommingUrl = normalizeUrl(request1.getUrl());
		String configuredUrl = normalizeUrl(configuredRequest.getUrl());
		return incommingUrl.matches(configuredUrl);
	}

	private boolean isContentTypeMatching(RequestDto configuredRequest,
			RequestDto incomingRequest) {
		if (configuredRequest.getContentType() == null)
			return true;
		return incomingRequest.getContentType().matches(
				configuredRequest.getContentType());
	}

}
