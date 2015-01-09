package com.github.httpmock;

import static com.github.httpmock.builder.ResponseBuilder.response;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.io.File;
import java.io.FileReader;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.httpmock.dto.ConfigurationDto;
import com.github.httpmock.dto.MockDto;
import com.github.httpmock.dto.RequestDto;
import com.github.httpmock.dto.ResponseDto;
import com.google.gson.Gson;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

public class MockIT {

	private static final int STATUS_OK = 200;
	private static final int STATUS_NOT_CONFIGURED = 204;
	private static final String METHOD_GET = "GET";
	private static final String METHOD_POST = "POST";
	private static final int SERVER_PORT = 8089;
	private static final int SERVER_STOP_PORT = 8012;
	private static HttpMockServerStandalone mockServer;

	@BeforeClass
	public static void startServer() throws Exception {
		mockServer = new HttpMockServerStandalone(SERVER_PORT, SERVER_STOP_PORT);
		mockServer.start();
		mockServer.deploy("target/wars/mockserver.war");
	}

	@AfterClass
	public static void stopServer() throws Exception {
		mockServer.stop();
	}

	private RequestSpecification given() {
		RequestSpecification given = RestAssured.given();
		given.baseUri("http://localhost") //
				.port(SERVER_PORT) //
				.basePath("/mockserver");
		return given;
	}

	@Test
	public void checkIfRunning() {
		assertThat(given().get("/").getStatusCode(), is(STATUS_OK));
	}

	@Test
	public void createMock() {
		MockDto mock = initMock();
		assertThat(mock.getRequestUrl(), is(notNullValue()));
		assertThat(mock.getConfigurationUrl(), is(notNullValue()));
		assertThat(mock.getVerifyUrl(), is(notNullValue()));
	}

	@Test
	public void createConfiguration() {
		MockDto mockDefinition = initMock();
		ConfigurationDto mockConfiguration = someMockConfiguration(someRequest(METHOD_POST), someResponse());

		given().contentType("application/json").body(toJson(mockConfiguration)).post(mockDefinition.getConfigurationUrl()) //
				.then().statusCode(is(STATUS_OK));
	}

	private String toJson(Object object) {
		Gson gson = new Gson();
		return gson.toJson(object);
	}

	@Test
	public void replayPost() {
		MockDto mock = initMock();
		RequestDto request = someRequest(METHOD_POST);
		ResponseDto response = someResponse();
		ConfigurationDto mockConfiguration = someMockConfiguration(request, response);

		configureRequestAndResponse(mock, mockConfiguration);

		validateResponse(doRequest(mock, request), response);
	}

	@Test
	public void replayWithPathPattern() {
		MockDto mock = initMock();
		RequestDto requestWithPattern = someRequest(METHOD_POST);
		requestWithPattern.setUrl(".+/url");
		ResponseDto response = someResponse();

		ConfigurationDto mockConfiguration = someMockConfiguration(requestWithPattern, response);
		configureRequestAndResponse(mock, mockConfiguration);

		RequestDto request = someRequest(METHOD_POST);
		request.setUrl("/some/url");
		validateResponse(doRequest(mock, request), response);
	}

	@Test
	public void replayPostMultiple() {
		MockDto mock = initMock();
		RequestDto request = someRequest(METHOD_POST);
		ResponseDto response = someResponse();
		ConfigurationDto mockConfiguration = someMockConfiguration(request, response);

		configureRequestAndResponse(mock, mockConfiguration);

		validateResponse(doRequest(mock, request), response);
		validateResponse(doRequest(mock, request), response);
		validateResponse(doRequest(mock, request), response);
	}

	@Test
	public void replayGet() {
		MockDto mock = initMock();
		RequestDto request = someRequest(METHOD_GET);
		ResponseDto response = someResponse();
		ConfigurationDto mockConfiguration = someMockConfiguration(request, response);

		configureRequestAndResponse(mock, mockConfiguration);

		validateResponse(doRequest(mock, request), response);
	}

	@Test
	public void replayGetWithBigData() {
		MockDto mock = initMock();
		RequestDto request = someRequest(METHOD_GET);
		ResponseDto response = someResponse();
		response.setPayload(Base64.encodeBase64String(longString().getBytes()));
		ConfigurationDto mockConfiguration = someMockConfiguration(request, response);

		configureRequestAndResponse(mock, mockConfiguration);

		validateResponse(doRequest(mock, request), response);
	}

	private String longString() {
		return StringUtils.repeat("some string", 1000);
	}

	@Test
	public void replayBinaryData() throws Exception {
		MockDto mock = initMock();
		RequestDto request = someRequest(METHOD_GET);
		ResponseDto response = response().contentType("application/pdf").payload(getByteArray("src/test/resources/mock.pdf")).build();

		ConfigurationDto mockConfiguration = someMockConfiguration(request, response);

		configureRequestAndResponse(mock, mockConfiguration);

		validateResponse(doRequest(mock, request), response);
	}

	private byte[] getByteArray(String string) throws Exception {
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(new File(string));
			return IOUtils.toByteArray(fileReader);
		} finally {
			IOUtils.closeQuietly(fileReader);
		}
	}

	@Test
	public void replayWithoutContentType() {
		MockDto mock = initMock();
		RequestDto request = createRequestWithoutContentType();
		ResponseDto response = someResponse();
		ConfigurationDto mockConfiguration = someMockConfiguration(request, response);

		configureRequestAndResponse(mock, mockConfiguration);

		validateResponse(doRequest(mock, request), response);
	}

	private RequestDto createRequestWithoutContentType() {
		RequestDto request = someRequest(METHOD_GET);
		request.setContentType(null);
		return request;
	}

	@Test
	public void verifyUndefinedRequest() {
		MockDto mock = initMock();
		RequestDto request = someRequest(METHOD_GET);

		verifyNumberOfCalls(mock, request, 0);
	}

	@Test
	public void verifyNoCall() {
		MockDto mock = initMock();
		RequestDto request = someRequest(METHOD_GET);
		ConfigurationDto mockConfiguration = someMockConfiguration(request, someResponse());

		configureRequestAndResponse(mock, mockConfiguration);

		verifyNumberOfCalls(mock, request, 0);
	}

	@Test
	public void verifyOneCall() {
		MockDto mock = initMock();
		RequestDto request = someRequest(METHOD_GET);
		ConfigurationDto mockConfiguration = someMockConfiguration(request, someResponse());
		configureRequestAndResponse(mock, mockConfiguration);

		doRequest(mock, request);

		verifyNumberOfCalls(mock, request, 1);
	}

	@Test
	public void verifyMultipleCalls() {
		MockDto mock = initMock();

		RequestDto request = someRequest(METHOD_POST);
		ConfigurationDto mockConfiguration = someMockConfiguration(request, someResponse());

		configureRequestAndResponse(mock, mockConfiguration);

		doRequest(mock, request);
		doRequest(mock, request);
		doRequest(mock, request);

		verifyNumberOfCalls(mock, request, 3);
	}

	private void verifyNumberOfCalls(MockDto mock, RequestDto request, int numberOfCalls) {
		given().contentType("application/json").body(toJson(request)).post(mock.getVerifyUrl()).then().body("times", is(numberOfCalls));
	}

	private void validateResponse(Response response, ResponseDto responseConfig) {
		response.then().statusCode(responseConfig.getStatusCode())
		//
				.and().contentType(is(responseConfig.getContentType()))
				//
				.and().body(is(new String(Base64.decodeBase64(responseConfig.getPayload()))));
	}

	private Response doRequest(MockDto mockDefinition, RequestDto request) {
		String method = request.getMethod();
		return request(method, mockDefinition.getRequestUrl() + request.getUrl(), request.getContentType());
	}

	private Response request(String method, String url, String contentType) {
		RequestSpecification request = given();
		if (contentType != null)
			request.contentType(contentType);
		if (method.equals(METHOD_POST))
			return request.post(url);
		if (method.equals(METHOD_GET))
			return request.get(url);
		throw new UnsupportedOperationException("method not supported: " + method);
	}

	private void configureRequestAndResponse(MockDto mockDefintion, ConfigurationDto mockConfiguration) {
		Gson gson = new Gson();
		given().contentType("application/json").body(gson.toJson(mockConfiguration)).post(mockDefintion.getConfigurationUrl());
	}

	private ConfigurationDto someMockConfiguration(RequestDto request, ResponseDto response) {
		ConfigurationDto configurationDTO = new ConfigurationDto();
		configurationDTO.setRequest(request);
		configurationDTO.setResponse(response);
		return configurationDTO;
	}

	private ResponseDto someResponse() {
		return response().contentType("application/json").statusCode(200).payload("{\"result\": \"some response\"}").build();
	}

	private RequestDto someRequest(String method) {
		RequestDto requestConfiguration = new RequestDto();
		requestConfiguration.setUrl("/some/url");
		requestConfiguration.setMethod(method);
		requestConfiguration.setContentType("application/json");
		return requestConfiguration;
	}

	private MockDto initMock() {
		return given().contentType("application/json").post("/mock/create").body().as(MockDto.class);
	}

	@Test
	public void deleteMock() {
		MockDto mock = initMock();
		RequestDto request = someRequest(METHOD_GET);
		ConfigurationDto mockConfiguration = someMockConfiguration(request, someResponse());
		configureRequestAndResponse(mock, mockConfiguration);

		given().delete(mock.getUrl());

		assertThat(doRequest(mock, request).statusCode(), is(STATUS_NOT_CONFIGURED));
	}
}
