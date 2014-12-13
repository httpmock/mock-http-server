package de.sn.mock;

import org.junit.rules.ExternalResource;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.specification.RequestSpecification;

import de.sn.mock.dto.ConfigurationDto;
import de.sn.mock.dto.MockDto;
import de.sn.mock.dto.RequestDto;

public class MockRule extends ExternalResource implements HttpMock {

	private MockServer mockServer;
	private MockDto mock;

	public MockRule(MockServer mockServer) {
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

	@Override
	public void delete() {
		given().delete(mock.getUrl());
	}

	private RequestSpecification given() {
		return RestAssured.given().baseUri(getBaseUri())
				.basePath("/mockserver");
	}

	@Override
	public Stubbing when(RequestDto request) {
		return new Stubbing(this, request);
	}

	@Override
	public void addConfig(ConfigurationDto config) {
		given().body(config).contentType("application/json")
		.post(mock.getConfigurationUrl());
	}

	public String getRequestUrl() {
		return "/mockserver" + mock.getRequestUrl();
	}
}
