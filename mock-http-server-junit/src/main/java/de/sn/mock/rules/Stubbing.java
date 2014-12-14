package de.sn.mock.rules;

import de.sn.mock.dto.ConfigurationDto;
import de.sn.mock.dto.RequestDto;
import de.sn.mock.dto.ResponseDto;

public class Stubbing {

	private MockService httpMock;
	private RequestDto request;

	public Stubbing(MockService mockService, RequestDto request) {
		this.httpMock = mockService;
		this.request = request;
	}

	public void thenRespond(ResponseDto response) {
		httpMock.configure(new ConfigurationDto(request, response));
	}

}
