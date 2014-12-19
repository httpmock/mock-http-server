mock-http-server
================

[![Coverage Status](https://img.shields.io/coveralls/snordquist/mock-http-server.svg)](https://coveralls.io/r/snordquist/mock-http-server)


With this mock http server you can write integration tests for your application and mock its http based backends.
This example shows an integration test which configures the mock to answer for a specific request with a specific response. 
It also shows how to use this mock server with junit.

```java
import static com.jayway.restassured.RestAssured.given;
import static de.sn.mock.builder.RequestBuilder.request;
import static de.sn.mock.builder.ResponseBuilder.response;
import static de.sn.mock.times.ExcatlyOnce.once;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import com.jayway.restassured.response.Response;

import de.sn.mock.dto.RequestDto;
import de.sn.mock.dto.ResponseDto;

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

```
