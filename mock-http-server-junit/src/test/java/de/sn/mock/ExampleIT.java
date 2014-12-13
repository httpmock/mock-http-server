package de.sn.mock;

import static com.jayway.restassured.RestAssured.given;
import static de.sn.mock.builder.RequestBuilder.request;
import static de.sn.mock.builder.ResponseBuilder.response;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import com.jayway.restassured.response.Response;

import de.sn.mock.builder.RequestBuilder;
import de.sn.mock.dto.ResponseDto;

public class ExampleIT {

	@ClassRule
	public static MockServer mockServer = new MockServer();

	@Rule
	public MockRule mock = new MockRule(mockServer);

	@Test
	public void someTest() throws Exception {
		RequestBuilder request = request().method("POST").url("/some/url");
		ResponseDto response = response().payload("data")
				.contentType("text/plain").build();
		mock.when(request.build()).thenRespond(response);

		Response mockResponse = given().baseUri(getBaseUri())
				.basePath(mock.getRequestUrl()).post("/some/url");

		assertThat(mockResponse.getBody().asString(), is("data"));
		assertThat(mockResponse.getContentType(), is("text/plain"));
	}

	private String getBaseUri() {
		return String.format("http://localhost:%d",
				mockServer.getPort());
	}
}
