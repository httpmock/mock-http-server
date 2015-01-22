package com.github.httpmock.api.times;

import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class ExactlyTest {
	@Test
	public void matches() throws Exception {
		Exactly exactly = new Exactly(5);
		assertThat(exactly.matches(5), is(true));
		assertThat(exactly.matches(0), is(false));
		assertThat(exactly.matches(2), is(false));
		assertThat(exactly.matches(-5), is(false));
	}

	@Test
	public void description() throws Exception {
		Exactly exactly = new Exactly(5);
		assertThat(exactly.getFailedDescription(), is("exactly 5 times"));
	}

	@Test
	public void factory() throws Exception {
		assertThat(Exactly.times(3), is(notNullValue()));
		assertThat(Exactly.times(3), is(instanceOf(Exactly.class)));
	}
}
