package com.github.httpmock.api.times;

import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class NeverTest {
	@Test
	public void matches() throws Exception {
		Never never = new Never();
		assertThat(never.matches(0), is(true));
		assertThat(never.matches(1), is(false));
		assertThat(never.matches(2), is(false));
		assertThat(never.matches(-2), is(false));
	}

	@Test
	public void description() throws Exception {
		Never never = new Never();
		assertThat(never.getFailedDescription(), is("never"));
	}

	@Test
	public void factory() throws Exception {
		assertThat(Never.never(), is(notNullValue()));
		assertThat(Never.never(), is(instanceOf(Never.class)));
	}

}
