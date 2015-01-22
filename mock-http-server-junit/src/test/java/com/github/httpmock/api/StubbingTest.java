package com.github.httpmock.api;

import com.github.httpmock.builder.ResponseBuilder;
import com.github.httpmock.dto.ConfigurationDto;
import com.github.httpmock.dto.RequestDto;
import com.github.httpmock.dto.ResponseDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class StubbingTest {
    @Mock
    private MockService mockService;

    @Test
    public void addConfig_thenRespond() throws Exception {
        RequestDto request = mock(RequestDto.class);
        ResponseDto response = mock(ResponseDto.class);

        Stubbing stubbing = new Stubbing(mockService, request);
        stubbing.thenRespond(response);

        verifyConfiguration(request, response);
    }

    @Test
    public void addConfig_then() throws Exception {
        RequestDto request = mock(RequestDto.class);
        ResponseDto response = mock(ResponseDto.class);

        Stubbing stubbing = new Stubbing(mockService, request);
        stubbing.then(response);

        verifyConfiguration(request, response);
    }

    private void verifyConfiguration(RequestDto request, ResponseDto response) {
        ArgumentCaptor<ConfigurationDto> configCaptor = ArgumentCaptor
                .forClass(ConfigurationDto.class);
        verify(mockService).configure(configCaptor.capture());
        assertThat(configCaptor.getValue().getRequest(), is(request));
        assertThat(configCaptor.getValue().getResponse(), is(response));
    }

    @Test
    public void addConfig_thenRespondWithBuilder() throws Exception {
        RequestDto request = mock(RequestDto.class);
        ResponseBuilder responseBuilder = mock(ResponseBuilder.class);
        ResponseDto response = mock(ResponseDto.class);
        when(responseBuilder.build()).thenReturn(response);

        Stubbing stubbing = new Stubbing(mockService, request);
        stubbing.thenRespond(responseBuilder);

        verifyConfiguration(request, response);
    }

    @Test
    public void addConfig_thenWithBuilder() throws Exception {
        RequestDto request = mock(RequestDto.class);
        ResponseBuilder responseBuilder = mock(ResponseBuilder.class);
        ResponseDto response = mock(ResponseDto.class);
        when(responseBuilder.build()).thenReturn(response);

        Stubbing stubbing = new Stubbing(mockService, request);
        stubbing.then(responseBuilder);

        verifyConfiguration(request, response);
    }
}
