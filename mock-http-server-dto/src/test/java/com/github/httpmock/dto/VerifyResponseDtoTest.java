package com.github.httpmock.dto;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class VerifyResponseDtoTest {
	@Test
	public void verify() throws Exception {
		VerifyResponseDto verifyResponseDto = new VerifyResponseDto();
		verifyResponseDto.setTimes(123);
		assertThat(verifyResponseDto.getTimes(), is(123));
	}
}
