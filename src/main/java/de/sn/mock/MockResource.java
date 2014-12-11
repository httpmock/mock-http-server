package de.sn.mock;

import static de.sn.mock.dto.RequestBuilder.request;

import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
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

import de.sn.mock.dto.ConfigurationDto;
import de.sn.mock.dto.MockDto;
import de.sn.mock.dto.RequestDto;
import de.sn.mock.dto.ResponseDto;

@Path("/mock")
public class MockResource {

	@EJB
	private MockService mockService;

	@EJB
	private RequestMatcher requestMatcher;

	@POST
	@Path("/create")
	@Produces(MediaType.APPLICATION_JSON)
	public Response create() {
		MockInstance mock = mockService.create();
		MockDto mockDto = createMockDto(mock);
		return Response.ok().entity(mockDto).build();
	}

	MockDto createMockDto(MockInstance mockInstance) {
		MockDto mockDto = new MockDto();
		String mockId = mockInstance.getId();
		mockDto.setConfigurationUrl(String.format("/mock/%s/configure", mockId));
		mockDto.setRequestUrl(String.format("/mock/%s/request", mockId));
		mockDto.setVerifyUrl(String.format("/mock/%s/verify", mockId));
		return mockDto;
	}

	@POST
	@Path("/{id}/configure")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response configure(@PathParam("id") String id,
			ConfigurationDto configuration) {
		MockInstance mock = mockService.findMock(id);
		mock.addConfiguration(configuration);
		return Response.ok().build();
	}

	@POST
	@GET
	@PUT
	@DELETE
	@HEAD
	@Path("/{id}/request/{url .*}")
	public Response replay(@PathParam("id") String id,
			@PathParam("url") String url, @Context UriInfo urlInfo,
			@Context HttpHeaders headers, @Context Request request) {

		MockInstance mock = mockService.findMock(id);
		ResponseDto response = findResponse(
				mock,
				request().method(request.getMethod()).url(url)
						.contentType(headers.getMediaType()).build());
		if (response == null) {
			return Response.noContent().build();
		}
		return toResponse(response);
	}

	private Response toResponse(ResponseDto response) {
		ResponseBuilder replayResponse = Response
				.status(response.getStatusCode())
				.entity(decodePayload(response.getPayload()))
				.header("Content-Type", response.getContentType());
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

	private ResponseDto findResponse(MockInstance mock, RequestDto requestDto) {
		List<ConfigurationDto> configurations = mock.getConfigurations();
		for (ConfigurationDto configurationDto : configurations) {
			if (requestMatcher.matches(configurationDto.getRequest(),
					requestDto)) {
				return configurationDto.getResponse();
			}
		}
		return null;
	}
}
