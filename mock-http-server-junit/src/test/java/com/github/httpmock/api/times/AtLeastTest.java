package com.github.httpmock.api.times;

import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class AtLeastTest {
	@Test
	public void matches() throws Exception {
		AtLeast once = new AtLeast(2);
		assertThat(once.matches(1), is(false));
		assertThat(once.matches(2), is(true));
		assertThat(once.matches(3), is(true));
		assertThat(once.matches(0), is(false));
		assertThat(once.matches(-3), is(false));
	}

	@Test
	public void description() throws Exception {
		AtLeast once = new AtLeast(2);
		assertThat(once.getFailedDescription(), is("at least 2 times"));
	}

	@Test
	public void factory() throws Exception {
		assertThat(AtLeast.atLeast(4), is(notNullValue()));
		assertThat(AtLeast.atLeast(4), is(instanceOf(AtLeast.class)));
	}
}
