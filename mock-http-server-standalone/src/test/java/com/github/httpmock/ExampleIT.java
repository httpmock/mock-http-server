package com.github.httpmock;

import static com.github.httpmock.builder.RequestBuilder.request;
import static com.github.httpmock.builder.ResponseBuilder.response;
import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import com.github.httpmock.api.times.ExactlyOnce;
import com.github.httpmock.builder.RequestBuilder;
import com.github.httpmock.builder.ResponseBuilder;
import com.github.httpmock.junit.rules.HttpMockRule;
import com.github.httpmock.junit.rules.HttpMockServerContext;
import com.jayway.restassured.response.Response;

public class ExampleIT {

	@ClassRule
	public static HttpMockServerContext mockServer = new HttpMockServerContext(new EmbeddedMockServer());

	@Rule
	public HttpMockRule mock = new HttpMockRule(mockServer);

	@Test
	public void someTest() throws Exception {
		RequestBuilder request = request().method("POST").url("/some/url");
		ResponseBuilder response = response().payload("data").contentType("text/plain");
		mock.when(request).thenRespond(response);

		Response mockResponse = given().baseUri(getBaseUri()).basePath(mock.getRequestUrl()).post("/some/url");

		assertThat(mockResponse.getBody().asString(), is("data"));
		assertThat(mockResponse.getContentType(), is("text/plain"));
		mock.verify(request, ExactlyOnce.once());
	}

	private String getBaseUri() {
		return mockServer.getBaseUri();
	}
}
