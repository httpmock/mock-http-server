package de.sn.mock.times;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

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
	public void descriptoin() throws Exception {
		Never never = new Never();
		assertThat(never.getFailedDescription(), is("never"));
	}

}
