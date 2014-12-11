package de.sn.mock;

import static de.sn.mock.dto.RequestBuilder.request;
import static de.sn.mock.dto.ResponseBuilder.response;
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

@RunWith(MockitoJUnitRunner.class)
public class MockResourceTest {
	private static final int STATUS_NOT_CONFIGURED = 204;

	private static final String URL = "some/url";

	private static final int STATUS_OK = 200;

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
		if (requestDto.getContentType() == null)
			when(headers.getMediaType()).thenReturn(null);
		else
			when(headers.getMediaType()).thenReturn(
					MediaType.valueOf(requestDto.getContentType()));

		return mockResource.replay(ID, requestDto.getUrl(), null, headers,
				request);
	}

	@Test
	public void replayWithPayload() throws Exception {
		RequestDto requestDto = someRequest();
		ResponseDto responseDto = response().payload("test").build();
		configureRequestAndResponse(requestDto, responseDto);

		Response replayedResponse = replayUrl(requestDto);

		assertThat(replayedResponse, is(notNullValue()));
		assertThat(replayedResponse.getStatus(), is(200));
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
		assertThat(replayedResponse.getStatus(), is(200));
		assertThat(replayedResponse.getHeaders().containsKey("Content-Type"),
				is(true));
		assertThat(replayedResponse.getMediaType().toString(),
				is("some/mediatype"));
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
	}

	@Test
	public void replayNotMatchingUrl() throws Exception {
		RequestDto requestDto = someRequest();
		ResponseDto responseDto = response().header("a", "b").header("c", "d")
				.build();
		configureRequestAndResponse(request().get("other/url").build(),
				responseDto);

		Response replayedResponse = replayUrl(requestDto);

		assertThat(replayedResponse, is(notNullValue()));
		assertThat(replayedResponse.getStatus(), is(STATUS_NOT_CONFIGURED));
	}

	private RequestDto someRequest() {
		return request().get(URL).build();
	}

	private void configureRequestAndResponse(RequestDto requestDto,
			ResponseDto responseDto) {
		MockInstance mock = createFakeMock();
		ConfigurationDto configuration = new ConfigurationDto(requestDto,
				responseDto);
		mock.addConfiguration(configuration);
		when(requestMatcher.matches(requestDto, requestDto)).thenReturn(true);
	}

	private MockInstance createFakeMock() {
		MockInstance mock = new MockInstance(ID);
		when(mockService.findMock(ID)).thenReturn(mock);
		return mock;
	}
}
