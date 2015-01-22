package com.github.httpmock.dto;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class MockDtoTest {
	@Test
	public void mockDefinition() throws Exception {
		MockDto mockDto = new MockDto();
		mockDto.setUrl("some url");
		mockDto.setRequestUrl("request url");
		mockDto.setConfigurationUrl("config url");
		mockDto.setVerifyUrl("verify url");

		assertThat(mockDto.getUrl(), is("some url"));
		assertThat(mockDto.getRequestUrl(), is("request url"));
		assertThat(mockDto.getConfigurationUrl(), is("config url"));
		assertThat(mockDto.getVerifyUrl(), is("verify url"));
	}
}
