package com.github.httpmock.api;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.httpmock.api.MockService;
import com.github.httpmock.api.Stubbing;
import com.github.httpmock.dto.ConfigurationDto;
import com.github.httpmock.dto.RequestDto;
import com.github.httpmock.dto.ResponseDto;

@RunWith(MockitoJUnitRunner.class)
public class StubbingTest {
	@Mock
	private MockService mockService;

	@Test
	public void addConfig() throws Exception {
		RequestDto request = mock(RequestDto.class);
		ResponseDto response = mock(ResponseDto.class);

		Stubbing stubbing = new Stubbing(mockService, request);
		stubbing.thenRespond(response);

		ArgumentCaptor<ConfigurationDto> configCaptor = ArgumentCaptor
				.forClass(ConfigurationDto.class);
		verify(mockService).configure(configCaptor.capture());
		assertThat(configCaptor.getValue().getRequest(), is(request));
		assertThat(configCaptor.getValue().getResponse(), is(response));
	}
}
