package de.sn.mock;

import static de.sn.mock.builder.RequestBuilder.request;
import static de.sn.mock.builder.ResponseBuilder.response;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.sn.mock.dto.ConfigurationDto;
import de.sn.mock.dto.MockDto;
import de.sn.mock.dto.RequestDto;
import de.sn.mock.dto.ResponseDto;
import de.sn.mock.dto.VerifyResponseDto;
import de.sn.mock.request.RequestMatcher;

@RunWith(MockitoJUnitRunner.class)
public class MockResourceTest {
	private static final int STATUS_OK = 200;

	private static final int STATUS_NOT_CONFIGURED = 204;

	private static final String URL = "some/url";

	private static final String ID = "some id";

	@Mock
	private MockService mockService;

	@Mock
	private Request request;

	@InjectMocks
	private MockResource mockResource;

	@Mock
	private RequestMatcher requestMatcher;

	@Before
	public void setup() {
		when(mockService.create()).thenReturn(mock(MockInstance.class));
	}

	@Test
	public void createMock() throws Exception {
		Response response = mockResource.create();

		verify(mockService).create();
		assertThat(response.getEntity(), is(instanceOf(MockDto.class)));
		MockDto mockDto = (MockDto) response.getEntity();
		assertThat(mockDto.getUrl(), is(notNullValue()));
		assertThat(mockDto.getConfigurationUrl(), is(notNullValue()));
		assertThat(mockDto.getRequestUrl(), is(notNullValue()));
		assertThat(mockDto.getVerifyUrl(), is(notNullValue()));
	}

	@Test
	public void configure() throws Exception {
		MockInstance mock = createFakeMock();
		ConfigurationDto configuration = new ConfigurationDto();

		Response response = mockResource.configure(ID, configuration);

		assertThat(mock.getConfigurations(), hasItem(configuration));
		assertThat(response.getStatus(), is(STATUS_OK));
	}

	@Test
	public void replay() throws Exception {
		RequestDto requestDto = someRequest();
		ResponseDto responseDto = response().build();
		configureRequestAndResponse(requestDto, responseDto);

		Response replayedResponse = replayUrl(requestDto);

		assertThat(replayedResponse, is(notNullValue()));
		assertThat(replayedResponse.getEntity(),
				is((Object) responseDto.getPayload()));
	}

	@Test
	public void replayNotExistingMock() throws Exception {
		RequestDto requestDto = someRequest();

		Response replayedResponse = replayUrl(requestDto);

		assertThat(replayedResponse, is(notNullValue()));
		assertThat(replayedResponse.getStatus(), is(204));
	}

	@Test
	public void replayNotConfigured() throws Exception {
		createFakeMock();
		RequestDto requestDto = someRequest();

		Response replayedResponse = replayUrl(requestDto);

		assertThat(replayedResponse, is(notNullValue()));
		assertThat(replayedResponse.getStatus(), is(204));
	}

	private Response replayUrl(RequestDto requestDto) {
		when(request.getMethod()).thenReturn(requestDto.getMethod());
		HttpHeaders headers = mock(HttpHeaders.class);
		mockContentTypeHeaders(headers, requestDto);

		return mockResource.replayPost(ID, requestDto.getUrl(), null, headers,
				request);
	}

	private void mockContentTypeHeaders(HttpHeaders headers,
			RequestDto requestDto) {
		if (requestDto.getContentType() == null)
			when(headers.getMediaType()).thenReturn(null);
		else
			when(headers.getMediaType()).thenReturn(
					MediaType.valueOf(requestDto.getContentType()));
	}

	@Test
	public void replayWithPayload() throws Exception {
		RequestDto requestDto = someRequest();
		ResponseDto responseDto = response().payload("test").build();
		configureRequestAndResponse(requestDto, responseDto);

		Response replayedResponse = replayUrl(requestDto);

		assertThat(replayedResponse, is(notNullValue()));
		assertThat(replayedResponse.getStatus(), is(STATUS_OK));
		assertThat(replayedResponse.getEntity(), is((Object) "test".getBytes()));
	}

	@Test
	public void replayWithContentType() throws Exception {
		RequestDto requestDto = someRequest();
		ResponseDto responseDto = response().contentType("some/mediatype")
				.build();
		configureRequestAndResponse(requestDto, responseDto);

		Response replayedResponse = replayUrl(requestDto);

		assertThat(replayedResponse, is(notNullValue()));
		assertThat(replayedResponse.getHeaders().containsKey("Content-Type"),
				is(true));
		assertThat(replayedResponse.getMediaType().toString(),
				is("some/mediatype"));
	}

	@Test
	public void replayRequestWithContentType() throws Exception {
		RequestDto requestDto = request().get(URL)
				.contentType("application/json").build();
		RequestDto incommingRequest = request().get(URL)
				.contentType("application/json").build();

		ResponseDto responseDto = response().build();
		configureRequestAndResponse(requestDto, responseDto);

		Response replayedResponse = replayUrl(incommingRequest);

		assertThat(replayedResponse, is(notNullValue()));
		assertThat(replayedResponse.getStatus(), is(STATUS_OK));
	}

	@Test
	public void replayRequestWithContentTypeWithCharset() throws Exception {
		RequestDto requestDto = request().get(URL)
				.contentType("application/json").build();
		RequestDto incommingRequest = request().get(URL)
				.contentType("application/json;charset=ISO-8859-1").build();

		ResponseDto responseDto = response().build();
		configureRequestAndResponse(requestDto, responseDto);

		Response replayedResponse = replayUrl(incommingRequest);

		assertThat(replayedResponse, is(notNullValue()));
		assertThat(replayedResponse.getStatus(), is(STATUS_OK));
	}

	@Test
	public void replayWithStatusCode() throws Exception {
		RequestDto requestDto = someRequest();
		ResponseDto responseDto = response().statusCode(123).build();
		configureRequestAndResponse(requestDto, responseDto);

		Response replayedResponse = replayUrl(requestDto);

		assertThat(replayedResponse, is(notNullValue()));
		assertThat(replayedResponse.getStatus(), is(123));
	}

	@Test
	public void replayWithHeaders() throws Exception {
		RequestDto requestDto = someRequest();
		ResponseDto responseDto = response().header("a", "b").header("c", "d")
				.build();
		configureRequestAndResponse(requestDto, responseDto);

		Response replayedResponse = replayUrl(requestDto);

		assertThat(replayedResponse, is(notNullValue()));
		assertThat(replayedResponse.getHeaderString("a"), is("b"));
		assertThat(replayedResponse.getHeaderString("c"), is("d"));
	}

	@Test
	public void replayNotMatchingUrl() throws Exception {
		RequestDto requestDto = someRequest();
		ResponseDto responseDto = response().build();
		configureRequestAndResponse(request().get("other/url").build(),
				responseDto);

		Response replayedResponse = replayUrl(requestDto);

		assertThat(replayedResponse, is(notNullValue()));
		assertThat(replayedResponse.getStatus(), is(STATUS_NOT_CONFIGURED));
	}

	@Test
	public void verifyNoRequest() throws Exception {
		createFakeMock();
		RequestDto requestDto = someRequest();
		Response response = mockResource.verify(ID, requestDto);
		VerifyResponseDto verifyResponse = getVerifyResponse(response);
		assertThat(verifyResponse.getTimes(), is(0));
	}

	@Test
	public void replayCount() throws Exception {
		RequestDto requestDto = someRequest();
		ResponseDto responseDto = response().build();
		MockInstance mock = configureRequestAndResponse(requestDto, responseDto);

		replayUrl(requestDto);

		Response response = mockResource.verify(ID, requestDto);
		assertThat(mock.getCount(requestDto), is(1));
		VerifyResponseDto verifyResponse = getVerifyResponse(response);
		assertThat(verifyResponse.getTimes(), is(1));
	}

	@Test
	public void replayOtherMethods() throws Exception {
		RequestDto requestDto = someRequest();
		ResponseDto responseDto = response().build();
		configureRequestAndResponse(requestDto, responseDto);
		when(request.getMethod()).thenReturn(requestDto.getMethod());
		HttpHeaders headers = mock(HttpHeaders.class);
		mockContentTypeHeaders(headers, requestDto);

		Response replayPost = mockResource.replayPost(ID, requestDto.getUrl(),
				null, headers, request);
		assertThat(replayPost.getStatus(), is(STATUS_OK));
		assertEqualResponses(mockResource.replayGet(ID, requestDto.getUrl(),
				null, headers, request), replayPost);
		assertEqualResponses(mockResource.replayPut(ID, requestDto.getUrl(),
				null, headers, request), replayPost);
		assertEqualResponses(mockResource.replayHead(ID, requestDto.getUrl(),
				null, headers, request), replayPost);
		assertEqualResponses(mockResource.replayDelete(ID, requestDto.getUrl(),
				null, headers, request), replayPost);
	}

	@Test
	public void replayWithGetParameters() throws Exception {
		RequestDto requestDto = request().post(URL + "?var=value&var2=value2")
				.build();
		ResponseDto responseDto = response().build();
		configureRequestAndResponse(requestDto, responseDto);
		when(request.getMethod()).thenReturn(requestDto.getMethod());
		HttpHeaders headers = mock(HttpHeaders.class);
		mockContentTypeHeaders(headers, requestDto);

		Response replayResponse = mockResource.replayPost(ID,
				requestDto.getUrl(), null, headers, request);

		assertThat(replayResponse.getStatus(), is(STATUS_OK));
	}

	@Test
	public void delete() throws Exception {
		Response response = mockResource.delete(ID);
		verify(mockService).delete(ID);
		assertThat(response.getStatus(), is(STATUS_OK));
	}

	private void assertEqualResponses(Response response,
			Response expectedResponse) {
		assertThat(response.getEntity(), is(expectedResponse.getEntity()));
		assertThat(response.getStatus(), is(expectedResponse.getStatus()));
		assertThat(response.getHeaders(), is(expectedResponse.getHeaders()));
	}

	private VerifyResponseDto getVerifyResponse(Response response) {
		return (VerifyResponseDto) response.getEntity();
	}

	private RequestDto someRequest() {
		return request().get(URL).build();
	}

	private MockInstance configureRequestAndResponse(RequestDto requestDto,
			ResponseDto responseDto) {
		MockInstance mock = createFakeMock();
		ConfigurationDto configuration = new ConfigurationDto(requestDto,
				responseDto);
		mock.addConfiguration(configuration);
		when(requestMatcher.matches(requestDto, requestDto)).thenReturn(true);
		return mock;
	}

	private MockInstance createFakeMock() {
		MockInstance mock = new MockInstance(ID);
		when(mockService.findMock(ID)).thenReturn(mock);
		return mock;
	}

}
