package com.github.httpmock;

import com.github.httpmock.api.times.ExactlyOnce;
import com.github.httpmock.dto.RequestDto;
import com.github.httpmock.dto.ResponseDto;
import com.github.httpmock.junit.rules.HttpMock;
import com.github.httpmock.junit.rules.HttpMockServerContext;
import com.jayway.restassured.response.Response;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import static com.github.httpmock.builder.RequestBuilder.request;
import static com.github.httpmock.builder.ResponseBuilder.response;
import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ExampleIT {

	@ClassRule
	public static HttpMockServerContext mockServer = new HttpMockServerContext(new EmbeddedMockServer());

	@Rule
	public HttpMock mock = new HttpMock(mockServer);

	@Test
	public void someTest() throws Exception {
		RequestDto request = request().method("POST").url("/some/url").build();
		ResponseDto response = response().payload("data").contentType("text/plain").build();
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
