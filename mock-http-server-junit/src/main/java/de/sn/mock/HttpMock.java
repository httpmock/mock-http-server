package de.sn.mock;

import junit.framework.AssertionFailedError;

import org.junit.rules.ExternalResource;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.specification.RequestSpecification;

import de.sn.mock.dto.ConfigurationDto;
import de.sn.mock.dto.MockDto;
import de.sn.mock.dto.RequestDto;
import de.sn.mock.dto.VerifyResponseDto;
import de.sn.mock.times.Times;

public class HttpMock extends ExternalResource {

	private static final String VERIFICATION_FAILED = "Mock verification failed. Request was called %d times but should have been called %s";
	private HttpMockServer mockServer;
	private MockDto mock;

	public HttpMock(HttpMockServer mockServer) {
		this.mockServer = mockServer;
	}

	@Override
	protected void before() throws Throwable {
		mock = createMock();
	}

	private MockDto createMock() {
		return given().post("/mock/create").as(MockDto.class);
	}

	private String getBaseUri() {
		int port = mockServer.getPort();
		String host = "localhost";
		return String.format("http://%s:%d", host, port);
	}

	@Override
	protected void after() {
		delete();
	}

	public void delete() {
		given().delete(mock.getUrl());
	}

	private RequestSpecification given() {
		return RestAssured.given().baseUri(getBaseUri())
				.basePath("/mockserver");
	}

	public Stubbing when(RequestDto request) {
		return new Stubbing(this, request);
	}

	public void addConfig(ConfigurationDto config) {
		given().body(config).contentType("application/json")
		.post(mock.getConfigurationUrl());
	}

	public String getRequestUrl() {
		return "/mockserver" + mock.getRequestUrl();
	}

	public void verify(RequestDto request, Times times) {
		int numberOfCalls = getNumberOfCalls(request);
		if (!times.matches(numberOfCalls)) {
			throw new AssertionFailedError(String.format(VERIFICATION_FAILED,
					numberOfCalls, times.getFailedDescription()));
		}
	}

	private int getNumberOfCalls(RequestDto request) {
		VerifyResponseDto verifyResponse = getVerifyResponse(request);
		int numberOfCalls = verifyResponse.getTimes();
		return numberOfCalls;
	}

	private VerifyResponseDto getVerifyResponse(RequestDto request) {
		return given()//
				.body(request)//
				.contentType("application/json")//
				.post(mock.getVerifyUrl())//
				.as(VerifyResponseDto.class);
	}
}
