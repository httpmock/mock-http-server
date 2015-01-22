package com.github.httpmock.dto;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ConfigurationDtoTest {
	@Test
	public void configurationConstructor() throws Exception {
		RequestDto request = new RequestDto();
		ResponseDto response = new ResponseDto();
		ConfigurationDto config = new ConfigurationDto(request, response);
		assertThat(config.getRequest(), is(request));
		assertThat(config.getResponse(), is(response));
	}

	@Test
	public void configuration() throws Exception {
		RequestDto request = new RequestDto();
		ResponseDto response = new ResponseDto();
		ConfigurationDto config = new ConfigurationDto();
		config.setRequest(request);
		config.setResponse(response);
		assertThat(config.getRequest(), is(request));
		assertThat(config.getResponse(), is(response));
	}

	@Test
	public void equalsTrue() throws Exception {
		assertThat(new ConfigurationDto().hashCode(),
				is(equalTo(new ConfigurationDto().hashCode())));
		assertThat(new ConfigurationDto(), is(equalTo(new ConfigurationDto())));
	}
}
