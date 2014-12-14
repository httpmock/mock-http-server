package de.sn.mock;

import static com.jayway.restassured.RestAssured.given;
import static de.sn.mock.builder.RequestBuilder.request;
import static de.sn.mock.builder.ResponseBuilder.response;
import static de.sn.mock.times.Times.once;
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
