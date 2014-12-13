package de.sn.mock;

import de.sn.mock.dto.ConfigurationDto;
import de.sn.mock.dto.RequestDto;
import de.sn.mock.dto.ResponseDto;

public class Stubbing {

	private HttpMock httpMock;
	private RequestDto request;

	public Stubbing(HttpMock httpMock, RequestDto request) {
		this.httpMock = httpMock;
		this.request = request;
	}

	public void thenRespond(ResponseDto response) {
		httpMock.addConfig(new ConfigurationDto(request, response));
	}

}
