package com.github.httpmock;

import static com.github.httpmock.builder.RequestBuilder.request;

import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.codec.binary.Base64;

import com.github.httpmock.dto.ConfigurationDto;
import com.github.httpmock.dto.MockDto;
import com.github.httpmock.dto.RequestDto;
import com.github.httpmock.dto.ResponseDto;
import com.github.httpmock.dto.VerifyResponseDto;
import com.github.httpmock.request.RequestMatcher;

@Stateless
@Path("/")
public class MockResource {

	@EJB
	private MockService mockService;

	@EJB
	private RequestMatcher requestMatcher;

	@GET
	@Path("/")
	public Response isRunning() {
		return Response.ok().build();
	}

	@POST
	@Path("/mock/create")
	@Produces(MediaType.APPLICATION_JSON)
	public Response create() {
		MockInstance mock = mockService.create();
		MockDto mockDto = createMockDto(mock);
		return Response.ok().entity(mockDto).build();
	}

	MockDto createMockDto(MockInstance mockInstance) {
		String mockId = mockInstance.getId();
		MockDto mockDto = new MockDto();
		mockDto.setUrl(String.format("/mock/%s", mockId));
		mockDto.setConfigurationUrl(String.format("/mock/%s/configure", mockId));
		mockDto.setRequestUrl(String.format("/mock/%s/request", mockId));
		mockDto.setVerifyUrl(String.format("/mock/%s/verify", mockId));
		return mockDto;
	}

	@POST
	@Path("/mock/{id}/configure")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response configure(@PathParam("id") String id, ConfigurationDto configuration) {
		MockInstance mock = mockService.findMock(id);
		mock.addConfiguration(configuration);
		return Response.ok().build();
	}

	@POST
	@Path("/mock/{id}/request/{url : .*}")
	public Response replayPost(@PathParam("id") String id, @PathParam("url") String url, @Context UriInfo urlInfo, @Context HttpHeaders headers, @Context Request request) {
		return replay(id, url, headers, request);
	}

	@GET
	@Path("/mock/{id}/request/{url : .*}")
	public Response replayGet(@PathParam("id") String id, @PathParam("url") String url, @Context UriInfo urlInfo, @Context HttpHeaders headers, @Context Request request) {
		return replay(id, url, headers, request);
	}

	@PUT
	@Path("/mock/{id}/request/{url : .*}")
	public Response replayPut(@PathParam("id") String id, @PathParam("url") String url, @Context UriInfo urlInfo, @Context HttpHeaders headers, @Context Request request) {
		return replay(id, url, headers, request);
	}

	@DELETE
	@Path("/mock/{id}/request/{url : .*}")
	public Response replayDelete(@PathParam("id") String id, @PathParam("url") String url, @Context UriInfo urlInfo, @Context HttpHeaders headers, @Context Request request) {
		return replay(id, url, headers, request);
	}

	@HEAD
	@Path("/mock/{id}/request/{url : .*}")
	public Response replayHead(@PathParam("id") String id, @PathParam("url") String url, @Context UriInfo urlInfo, @Context HttpHeaders headers, @Context Request request) {
		return replay(id, url, headers, request);
	}

	private Response replay(String id, String url, HttpHeaders headers, Request request) {
		MockInstance mock = mockService.findMock(id);
		if (mock == null)
			return notFound();

		ConfigurationDto configuration = findConfiguration(mock, toRequestDto(url, headers, request));
		if (configuration == null)
			return notFound();

		mock.count(configuration.getRequest());
		return toResponse(configuration.getResponse());
	}

	private Response notFound() {
		return Response.noContent().build();
	}

	private RequestDto toRequestDto(String url, HttpHeaders headers, Request request) {
		MediaType mediaType = headers.getMediaType();
		String contentType = getContentType(mediaType);
		return request().method(request.getMethod()).url(url).contentType(contentType).build();
	}

	private String getContentType(MediaType mediaType) {
		if (mediaType == null)
			return null;
		return removeCharset(mediaType.toString());
	}

	private String removeCharset(String contentType) {
		if (contentType.matches(".+[;].+"))
			return contentType.split(";")[0];
		return contentType;
	}

	private Response toResponse(ResponseDto response) {
		ResponseBuilder replayResponse = Response.status(response.getStatusCode()).entity(decodePayload(response.getPayload()));
		addHeaders(response, replayResponse);
		return replayResponse.build();
	}

	private void addHeaders(ResponseDto response, ResponseBuilder replayResponse) {
		Set<String> keySet = response.getHeaders().keySet();
		for (String key : keySet) {
			replayResponse.header(key, response.getHeaders().get(key));
		}
	}

	private byte[] decodePayload(String payload) {
		return Base64.decodeBase64(payload);
	}

	private ConfigurationDto findConfiguration(MockInstance mock, RequestDto requestDto) {
		List<ConfigurationDto> configurations = mock.getConfigurations();
		for (ConfigurationDto configurationDto : configurations) {
			if (requestMatcher.matches(configurationDto.getRequest(), requestDto)) {
				return configurationDto;
			}
		}
		return null;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/mock/{id}/verify")
	public Response verify(@PathParam("id") String id, RequestDto request) {
		MockInstance mock = mockService.findMock(id);
		ConfigurationDto configuration = findConfiguration(mock, request);

		VerifyResponseDto verifyResponseDto = new VerifyResponseDto();
		if (configuration != null)
			verifyResponseDto.setTimes(mock.getCount(configuration.getRequest()));
		return Response.ok(verifyResponseDto).build();
	}

	@DELETE
	@Path("/mock/{id}")
	public Response delete(@PathParam("id") String id) {
		mockService.delete(id);
		return Response.ok().build();
	}
}
