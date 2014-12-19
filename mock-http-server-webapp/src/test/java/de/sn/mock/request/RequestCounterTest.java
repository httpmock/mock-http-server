package de.sn.mock.request;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import de.sn.mock.dto.RequestDto;
import de.sn.mock.request.RequestCounter;

public class RequestCounterTest {
	@Test
	public void countNone() throws Exception {
		RequestCounter requestCounter = new RequestCounter();
		assertThat(requestCounter.getCount(mock(RequestDto.class)), is(0));
	}

	@Test
	public void countOne() throws Exception {
		RequestCounter requestCounter = new RequestCounter();

		RequestDto request = mock(RequestDto.class);
		requestCounter.count(request);
		assertThat(requestCounter.getCount(request), is(1));
	}

	@Test
	public void countOne_differentRequests() throws Exception {
		RequestCounter requestCounter = new RequestCounter();

		RequestDto request1 = mock(RequestDto.class);
		RequestDto request2 = mock(RequestDto.class);
		requestCounter.count(request1);
		requestCounter.count(request2);
		assertThat(requestCounter.getCount(request1), is(1));
	}

	@Test
	public void countMultiple() throws Exception {
		RequestCounter requestCounter = new RequestCounter();

		RequestDto request1 = mock(RequestDto.class);
		requestCounter.count(request1);
		requestCounter.count(request1);
		assertThat(requestCounter.getCount(request1), is(2));
	}
}
