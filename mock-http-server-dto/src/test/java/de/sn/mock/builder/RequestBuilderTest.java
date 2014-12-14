package de.sn.mock.builder;

import static de.sn.mock.builder.RequestBuilder.request;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.Test;

import de.sn.mock.dto.RequestDto;

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

}
