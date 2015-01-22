package com.github.httpmock.api.times;

import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class AtLeastOnceTest {
	@Test
	public void matches() throws Exception {
		AtLeastOnce once = new AtLeastOnce();
		assertThat(once.matches(1), is(true));
		assertThat(once.matches(2), is(true));
		assertThat(once.matches(0), is(false));
		assertThat(once.matches(-2), is(false));
	}

	@Test
	public void description() throws Exception {
		AtLeastOnce once = new AtLeastOnce();
		assertThat(once.getFailedDescription(), is("at least once"));
	}

	@Test
	public void factory() throws Exception {
		assertThat(AtLeastOnce.atLeastOnce(), is(notNullValue()));
		assertThat(AtLeastOnce.atLeastOnce(), is(instanceOf(AtLeastOnce.class)));
	}
}
