package com.github.httpmock.dto;

import org.junit.Test;

import java.util.Map;

import static com.github.httpmock.util.CollectionUtil.emptyMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class ResponseDtoTest {
	@Test
	public void equalsTrue() throws Exception {
		assertThat(new ResponseDto(), is(equalTo(new ResponseDto())));
	}

	@Test
	public void hashCodeIsEqual() throws Exception {
		assertThat(new ResponseDto().hashCode(),
				is(equalTo(new ResponseDto().hashCode())));
	}

	@Test
	public void getContentTypeFromHeaders() throws Exception {
		ResponseDto responseDto = new ResponseDto();
		Map<String, String> headers = emptyMap();
		headers.put("Content-Type", "some/type");
		responseDto.setHeaders(headers);
		assertThat(responseDto.getHeaders().get("Content-Type"), is("some/type"));
	}
}
