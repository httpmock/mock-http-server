package com.github.httpmock.dto;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.github.httpmock.dto.VerifyResponseDto;

public class VerifyResponseDtoTest {
	@Test
	public void verify() throws Exception {
		VerifyResponseDto verifyResponseDto = new VerifyResponseDto();
		verifyResponseDto.setTimes(123);
		assertThat(verifyResponseDto.getTimes(), is(123));
	}
}
