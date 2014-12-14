package de.sn.mock.dto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

public class RequestDtoTest {
	@Test
	public void equalsTrue() throws Exception {
		assertThat(new RequestDto(), is(equalTo(new RequestDto())));
	}

	@Test
	public void hashCodeIsEqual() throws Exception {
		assertThat(new RequestDto().hashCode(),
				is(equalTo(new RequestDto().hashCode())));
	}
}
