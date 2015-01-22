package com.github.httpmock;

import com.github.httpmock.dto.ConfigurationDto;
import com.github.httpmock.dto.RequestDto;
import com.github.httpmock.dto.ResponseDto;
import com.github.httpmock.request.RequestCounter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MockInstanceTest {

	@Spy
	private RequestCounter requestCounter;

	@InjectMocks
	private MockInstance mockInstance = new MockInstance("some id");

	@Test
	public void getCount() throws Exception {
		RequestDto request = mock(RequestDto.class);
		mockInstance.getCount(request);
		verify(requestCounter).getCount(request);
	}

	@Test
	public void count() throws Exception {
		RequestDto request = mock(RequestDto.class);
		ResponseDto response = mock(ResponseDto.class);
		mockInstance.onReplay(new ConfigurationDto(request, response));
		verify(requestCounter).count(request);
	}
}
