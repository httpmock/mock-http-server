package de.sn.mock.rules;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.specification.RequestSpecification;

import de.sn.mock.dto.ConfigurationDto;
import de.sn.mock.dto.MockDto;
import de.sn.mock.dto.RequestDto;
import de.sn.mock.dto.VerifyResponseDto;

public class MockService {

	private String mockServerContext;
	private MockDto mock;
	private String baseUri;

	public MockService(String baseUri, String mockServerContext) {
		this.baseUri = baseUri;
		this.mockServerContext = mockServerContext;
	}

	public void create() {
		mock = given().post("/mock/create").as(MockDto.class);
	}

	RequestSpecification given() {
		return RestAssured.given().baseUri(baseUri).basePath(mockServerContext);
	}

	public void delete() {
		given().delete(mock.getUrl());
	}

	public void configure(ConfigurationDto config) {
		given().body(config).contentType("application/json")
		.post(mock.getConfigurationUrl());
	}

	public VerifyResponseDto verify(RequestDto request) {
		return given()//
				.body(request)//
				.contentType("application/json")//
				.post(mock.getVerifyUrl())//
				.as(VerifyResponseDto.class);
	}

	public String getRequestUrl() {
		return mockServerContext + mock.getRequestUrl();
	}

	MockDto getMock() {
		return mock;
	}

	void setMock(MockDto mock) {
		this.mock = mock;
	}

}
