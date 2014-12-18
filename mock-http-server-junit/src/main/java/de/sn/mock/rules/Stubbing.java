package de.sn.mock.rules;

import de.sn.mock.dto.ConfigurationDto;
import de.sn.mock.dto.RequestDto;
import de.sn.mock.dto.ResponseDto;

public class Stubbing {

	private MockService mockService;
	private RequestDto request;

	public Stubbing(MockService mockService, RequestDto request) {
		this.mockService = mockService;
		this.request = request;
	}

	public void thenRespond(ResponseDto response) {
		mockService.configure(new ConfigurationDto(request, response));
	}

}
