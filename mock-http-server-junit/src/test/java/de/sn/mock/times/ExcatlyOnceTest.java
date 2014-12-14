package de.sn.mock.times;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ExcatlyOnceTest {
	@Test
	public void matches() throws Exception {
		ExcatlyOnce once = new ExcatlyOnce();
		assertThat(once.matches(1), is(true));
		assertThat(once.matches(0), is(false));
		assertThat(once.matches(2), is(false));
		assertThat(once.matches(-2), is(false));
	}

	@Test
	public void descriptoin() throws Exception {
		ExcatlyOnce once = new ExcatlyOnce();
		assertThat(once.getFailedDescription(), is("once"));
	}

}
