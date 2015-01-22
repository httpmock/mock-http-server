package com.github.httpmock.api;

import com.github.httpmock.dto.ConfigurationDto;
import com.github.httpmock.dto.MockDto;
import com.github.httpmock.dto.RequestDto;
import com.github.httpmock.dto.VerifyResponseDto;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MockServiceTest {

	@Spy
	private MockService mockService = new MockService("baseuri", "context");

	@Mock
	private RequestSpecification given;

	@Mock
	private Response response;

	@Mock
	private MockDto mockDto;

	@Before
	public void setup() {
		when(mockService.given()).thenReturn(given);
		mockService.setMock(mockDto);
	}

	@Test
	public void createMock() throws Exception {
		when(given.post("/mock/create")).thenReturn(response);
		when(response.as(MockDto.class)).thenReturn(mockDto);

		mockService.create();

		verify(given).post("/mock/create");
		verify(response).as(MockDto.class);
		assertThat(mockService.getMock(), is(mockDto));
	}

	@Test
	public void deleteMock() throws Exception {
		when(mockDto.getUrl()).thenReturn("mock/url");

		mockService.delete();

		verify(given).delete("mock/url");
	}

	@Test
	public void configureMock() throws Exception {
		when(mockDto.getConfigurationUrl()).thenReturn("configure/url");
		ConfigurationDto config = mock(ConfigurationDto.class);
		when(given.body(config)).thenReturn(given);
		when(given.contentType("application/json")).thenReturn(given);

		mockService.configure(config);

		InOrder inOrder = inOrder(given);
		inOrder.verify(given).body(config);
		inOrder.verify(given).contentType("application/json");
		inOrder.verify(given).post("configure/url");
	}

	@Test
	public void verifyMock() throws Exception {
		when(mockDto.getVerifyUrl()).thenReturn("verify/url");
		RequestDto request = mock(RequestDto.class);
		when(given.body(request)).thenReturn(given);
		when(given.contentType("application/json")).thenReturn(given);
		when(given.post("verify/url")).thenReturn(response);
		VerifyResponseDto verifyResponse = mock(VerifyResponseDto.class);
		when(response.as(VerifyResponseDto.class)).thenReturn(verifyResponse);

		assertThat(mockService.verify(request), is(verifyResponse));

		InOrder inOrder = inOrder(given);
		inOrder.verify(given).body(request);
		inOrder.verify(given).contentType("application/json");
		inOrder.verify(given).post("verify/url");
	}

	@Test
	public void requestUrl() throws Exception {
		when(mockDto.getRequestUrl()).thenReturn("request/url");
		assertThat(mockService.getRequestUrl(), is("context" + "request/url"));
	}
}
