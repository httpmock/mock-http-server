package com.github.httpmock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.httpmock.dto.RequestDto;
import com.github.httpmock.request.RequestCounter;

@RunWith(MockitoJUnitRunner.class)
public class MockInstanceTest {

	private MockInstance mockInstance;

	@Mock
	private RequestCounter requestCounter;

	@Before
	public void setup() {
		mockInstance = new MockInstance("some id");
		mockInstance.setRequestCounter(requestCounter);
	}

	@Test
	public void getCount() throws Exception {
		RequestDto request = mock(RequestDto.class);
		mockInstance.getCount(request);
		verify(requestCounter).getCount(request);
	}

	@Test
	public void count() throws Exception {
		RequestDto request = mock(RequestDto.class);
		mockInstance.count(request);
		verify(requestCounter).count(request);
	}
}
