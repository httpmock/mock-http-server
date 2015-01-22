package com.github.httpmock.dto;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

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
