package com.github.httpmock.builder;

import com.github.httpmock.dto.RequestDto;
import org.junit.Test;

import static com.github.httpmock.builder.RequestBuilder.request;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class RequestBuilderTest {

	@Test
	public void defaults() throws Exception {
		RequestDto request = request().build();
		assertThat(request.getMethod(), is("GET"));
		assertThat(request.getContentType(), is(nullValue()));
		assertThat(request.getUrl(), is(nullValue()));
	}

	@Test
	public void method() throws Exception {
		RequestDto request = request().method("my method").build();
		assertThat(request.getMethod(), is("my method"));
	}

	@Test
	public void url() throws Exception {
		RequestDto request = request().url("my url").build();
		assertThat(request.getUrl(), is("my url"));
	}

	@Test
	public void contentType() throws Exception {
		RequestDto request = request().contentType("my content type").build();
		assertThat(request.getContentType(), is("my content type"));
	}

	@Test
	public void get() throws Exception {
		RequestDto request = request().get("some url").build();
		assertThat(request.getUrl(), is("some url"));
		assertThat(request.getMethod(), is("GET"));
	}

	@Test
	public void post() throws Exception {
		RequestDto request = request().post("some url").build();
		assertThat(request.getUrl(), is("some url"));
		assertThat(request.getMethod(), is("POST"));
	}

	@Test
	public void payload() throws Exception {
		RequestDto request = request().payload("payload").build();
		assertThat(request.getPayload(), is("payload"));
	}

}
