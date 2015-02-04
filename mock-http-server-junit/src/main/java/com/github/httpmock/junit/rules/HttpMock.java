package com.github.httpmock.junit.rules;

import com.github.httpmock.api.Stubbing;
import com.github.httpmock.api.times.Times;
import com.github.httpmock.builder.RequestBuilder;
import com.github.httpmock.dto.ConfigurationDto;
import com.github.httpmock.dto.RequestDto;

public interface HttpMock {

	void delete();

	Stubbing when(RequestDto request);

	Stubbing when(RequestBuilder requestBuilder);

	void configure(ConfigurationDto config);

	String getRequestUrl();

	void verify(RequestDto request, Times times);

	void verify(RequestBuilder request, Times times);
}