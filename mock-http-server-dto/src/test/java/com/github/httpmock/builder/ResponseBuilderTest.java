package com.github.httpmock.builder;

import static com.github.httpmock.builder.ResponseBuilder.response;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.collection.IsMapContaining.hasEntry;

import org.junit.Test;

import com.github.httpmock.dto.ResponseDto;

public class ResponseBuilderTest {
	@Test
	public void defaults() throws Exception {
		ResponseDto response = response().build();
		assertThat(response.getHeaders(), is(notNullValue()));
		assertThat(response.getHeaders().keySet(), is(empty()));
		assertThat(response.getPayload(), is(nullValue()));
		assertThat(response.getStatusCode(), is(200));
	}

	@Test
	public void statusCode() throws Exception {
		ResponseDto response = response().statusCode(123).build();
		assertThat(response.getStatusCode(), is(123));
	}

	@Test
	public void payload() throws Exception {
		ResponseDto response = response().payload("test").build();
		assertThat(response.getPayload(), is("dGVzdA=="));
	}

	@Test
	public void binaryPayload() throws Exception {
		ResponseDto response = response().payload("test".getBytes()).build();
		assertThat(response.getPayload(), is("dGVzdA=="));
	}

	@Test
	public void headers() throws Exception {
		ResponseDto response = response().header("key", "value").build();
		assertThat(response.getHeaders(), hasEntry("key", "value"));
	}

	@Test
	public void headersOverwrite() throws Exception {
		ResponseDto response = response().header("key", "value")
				.header("key", "value2").build();
		assertThat(response.getHeaders(), hasEntry("key", "value2"));
	}

	@Test
	public void headersMultiple() throws Exception {
		ResponseDto response = response().header("key", "value")
				.header("key2", "value2").build();
		assertThat(response.getHeaders(), hasEntry("key", "value"));
		assertThat(response.getHeaders(), hasEntry("key2", "value2"));
	}

	@Test
	public void contentType() throws Exception {
		ResponseDto response = response().contentType("some/type").build();
		assertThat(response.getHeaders(), hasEntry("Content-Type", "some/type"));
	}

}
