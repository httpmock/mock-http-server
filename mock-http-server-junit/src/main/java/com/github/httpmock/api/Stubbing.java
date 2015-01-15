package com.github.httpmock.api;

import com.github.httpmock.dto.ConfigurationDto;
import com.github.httpmock.dto.RequestDto;
import com.github.httpmock.dto.ResponseDto;

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
