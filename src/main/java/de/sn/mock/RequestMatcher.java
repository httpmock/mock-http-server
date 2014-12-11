package de.sn.mock;

import javax.ejb.Stateless;

import de.sn.mock.dto.RequestDto;

@Stateless
public class RequestMatcher {

	public boolean matches(RequestDto configuredRequest, RequestDto request1) {
		return request1.getMethod().equals(configuredRequest.getMethod())
				&& request1.getUrl().matches(configuredRequest.getUrl())
				&& contentTypeMatches(configuredRequest, request1);
	}

	private boolean contentTypeMatches(RequestDto configuredRequest,
			RequestDto incomingRequest) {
		if (configuredRequest.getContentType() == null)
			return true;
		return incomingRequest.getContentType().matches(
				configuredRequest.getContentType());
	}

}
