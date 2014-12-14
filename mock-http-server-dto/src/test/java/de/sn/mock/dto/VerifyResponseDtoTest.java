package de.sn.mock.dto;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class VerifyResponseDtoTest {
	@Test
	public void verify() throws Exception {
		VerifyResponseDto verifyResponseDto = new VerifyResponseDto();
		verifyResponseDto.setTimes(123);
		assertThat(verifyResponseDto.getTimes(), is(123));
	}
}
