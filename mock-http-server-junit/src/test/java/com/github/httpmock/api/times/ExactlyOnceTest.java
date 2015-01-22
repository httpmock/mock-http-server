package com.github.httpmock.api.times;

import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class ExactlyOnceTest {
	@Test
	public void matches() throws Exception {
		ExactlyOnce once = new ExactlyOnce();
		assertThat(once.matches(1), is(true));
		assertThat(once.matches(0), is(false));
		assertThat(once.matches(2), is(false));
		assertThat(once.matches(-2), is(false));
	}

	@Test
	public void description() throws Exception {
		ExactlyOnce once = new ExactlyOnce();
		assertThat(once.getFailedDescription(), is("once"));
	}

	@Test
	public void factory() throws Exception {
		assertThat(ExactlyOnce.once(), is(notNullValue()));
		assertThat(ExactlyOnce.once(), is(instanceOf(ExactlyOnce.class)));
	}
}
