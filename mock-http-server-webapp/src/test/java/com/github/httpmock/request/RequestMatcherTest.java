package com.github.httpmock.request;

import com.github.httpmock.builder.RequestBuilder;
import com.github.httpmock.dto.RequestDto;
import org.junit.Before;
import org.junit.Test;

import static com.github.httpmock.builder.RequestBuilder.request;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class RequestMatcherTest {
	private RequestMatcher matcher;

	@Before
	public void setup() {
		matcher = new RequestMatcher();
	}

	@Test
	public void matchesEqual() throws Exception {
		RequestDto request = someRequest().build();
		assertThat(matcher.matches(request, request), is(true));
	}

	@Test
	public void noMatchDifferentMethod() throws Exception {
		RequestDto configuredRequest = someRequest().method("GET").build();
		RequestDto request2 = someRequest().method("POST").build();
		assertThat(matcher.matches(configuredRequest, request2), is(false));
	}

	@Test
	public void noMatchDifferentUrl() throws Exception {
		RequestDto configuredRequest = request().url("url1").build();
		RequestDto incommingRequest = request().url("url2").build();
		assertThat(matcher.matches(configuredRequest, incommingRequest),
				is(false));
	}

	@Test
	public void matchRegexUrl() throws Exception {
		RequestDto configuredRequest = request().url(".+").build();
		RequestDto incommingRequest = request().url("url2").build();
		assertThat(matcher.matches(configuredRequest, incommingRequest),
				is(true));
	}

	@Test
	public void matchNormalizedConfiguredUrl() throws Exception {
		RequestDto configuredRequest = request().url("url2").build();
		RequestDto incommingRequest = request().url("/url2").build();
		assertThat(matcher.matches(configuredRequest, incommingRequest),
				is(true));
	}

	@Test
	public void matchNormalizedIncommingUrl() throws Exception {
		RequestDto configuredRequest = request().url("/url2").build();
		RequestDto incommingRequest = request().url("url2").build();
		assertThat(matcher.matches(configuredRequest, incommingRequest),
				is(true));
	}

	@Test
	public void matchEqualContentType() throws Exception {
		RequestDto configuredRequest = someRequest().contentType("some/type")
				.build();
		RequestDto incommingRequest = someRequest().contentType("some/type")
				.build();
		assertThat(matcher.matches(configuredRequest, incommingRequest),
				is(true));
	}

	@Test
	public void matchRegexContentType() throws Exception {
		RequestDto configuredRequest = someRequest().contentType(".+/type")
				.build();
		RequestDto incommingRequest = someRequest().contentType("some/type")
				.build();
		assertThat(matcher.matches(configuredRequest, incommingRequest),
				is(true));
	}

	@Test
	public void matchNoConfiguredContentType() throws Exception {
		RequestDto configuredRequest = someRequest().contentType((String) null)
				.build();
		RequestDto incommingRequest = someRequest().contentType("some/type")
				.build();
		assertThat(matcher.matches(configuredRequest, incommingRequest),
				is(true));
	}

	@Test
	public void noMatchContentType() throws Exception {
		RequestDto configuredRequest = someRequest().contentType(
				"some/othertype").build();
		RequestDto incommingRequest = someRequest().contentType("some/type")
				.build();
		assertThat(matcher.matches(configuredRequest, incommingRequest),
				is(false));
	}

	@Test
	public void matchRequestWithQueryParametersInDifferentOrder()
			throws Exception {
		RequestDto configuredRequest = request().url(
				"some/url?var1=value1&var2=value2").build();
		RequestDto incommingRequest = request().url(
				"some/url?var2=value2&var1=value1").build();
		assertThat(matcher.matches(configuredRequest, incommingRequest),
				is(true));
	}

	@Test
	public void noMatchRequestWithDifferentQueryParameters() throws Exception {
		RequestDto configuredRequest = request().url(
				"some/url?var1=value1&var2=value2").build();
		RequestDto incommingRequest = request().url(
				"some/url?var2=value2&var1=value123").build();
		assertThat(matcher.matches(configuredRequest, incommingRequest),
				is(false));
	}

	@Test
	public void matchRequestPayloadIsDifferent() throws Exception {
		String url = "some/url?var1=value1&var2=value2";
		RequestDto configuredRequest = request().payload("test").url(
				url).build();
		RequestDto incommingRequest = request().payload("test2").url(
				url).build();
		assertThat(matcher.matches(configuredRequest, incommingRequest),
				is(false));
	}

	@Test
	public void matchRequestPayloadNotSet() throws Exception {
		String url = "some/url?var1=value1&var2=value2";
		RequestDto configuredRequest = request().payload("test").url(
				url).build();
		RequestDto incommingRequest = request().url(
				url).build();
		assertThat(matcher.matches(configuredRequest, incommingRequest),
				is(false));
	}

	@Test
	public void matchRequestPayloadNotConfigured() throws Exception {
		String url = "some/url?var1=value1&var2=value2";
		RequestDto configuredRequest = request().url(
				url).build();
		RequestDto incommingRequest = request().payload("test").url(
				url).build();
		assertThat(matcher.matches(configuredRequest, incommingRequest),
				is(true));
	}

	@Test
	public void matchRequestPayloadIsEqual() throws Exception {
		String url = "some/url?var1=value1&var2=value2";
		RequestDto configuredRequest = request().payload("test").url(
				url).build();
		RequestDto incommingRequest = request().payload("test").url(
				url).build();
		assertThat(matcher.matches(configuredRequest, incommingRequest),
				is(true));
	}

	private RequestBuilder someRequest() {
		return request().url("some/url");
	}
}
