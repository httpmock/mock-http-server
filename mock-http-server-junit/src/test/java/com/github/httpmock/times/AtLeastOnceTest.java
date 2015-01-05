package com.github.httpmock.times;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.github.httpmock.times.AtLeastOnce;

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
	public void descriptoin() throws Exception {
		AtLeastOnce once = new AtLeastOnce();
		assertThat(once.getFailedDescription(), is("at least once"));
	}

	@Test
	public void factory() throws Exception {
		assertThat(AtLeastOnce.atLeastOnce(), is(notNullValue()));
	}
}
