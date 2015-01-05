package com.github.httpmock.rules;

import static com.github.httpmock.builder.RequestBuilder.request;
import static com.github.httpmock.builder.ResponseBuilder.response;
import static com.github.httpmock.times.ExcatlyOnce.once;
import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import com.github.httpmock.dto.RequestDto;
import com.github.httpmock.dto.ResponseDto;
import com.jayway.restassured.response.Response;

public class ExampleIT {

	@ClassRule
	public static HttpMockServer mockServer = new HttpMockServer();

	@Rule
	public HttpMock mock = new HttpMock(mockServer);

	@Test
	public void someTest() throws Exception {
		RequestDto request = request().method("POST").url("/some/url").build();
		ResponseDto response = response().payload("data")
				.contentType("text/plain").build();
		mock.when(request).thenRespond(response);

		Response mockResponse = given().baseUri(getBaseUri())
				.basePath(mock.getRequestUrl()).post("/some/url");

		assertThat(mockResponse.getBody().asString(), is("data"));
		assertThat(mockResponse.getContentType(), is("text/plain"));
		mock.verify(request, once());
	}

	private String getBaseUri() {
		return String.format("http://localhost:%d", mockServer.getPort());
	}
}
