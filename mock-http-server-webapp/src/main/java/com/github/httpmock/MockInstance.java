package com.github.httpmock;

import static com.github.httpmock.util.CollectionUtil.emptyList;

import java.util.Arrays;
import java.util.List;

import com.github.httpmock.dto.ConfigurationDto;
import com.github.httpmock.dto.RequestDto;
import com.github.httpmock.request.RequestCounter;

public class MockInstance implements MockReplayListener {
	private String id;
	private List<ConfigurationDto> configurations;
	private RequestCounter requestCounter;

	public MockInstance(String id) {
		this.id = id;
		this.configurations = emptyList();
		this.requestCounter = new RequestCounter();
	}

	public String getId() {
		return id;
	}

	public List<ConfigurationDto> getConfigurations() {
		return configurations;
	}

	public void addConfiguration(ConfigurationDto configuration) {
		configurations.add(configuration);
	}

	public int getCount(RequestDto request) {
		return requestCounter.getCount(request);
	}

	@Override
	public void onReplay(ConfigurationDto configuration) {
		for(MockReplayListener replayListener : getReplayListeners()) {
			replayListener.onReplay(configuration);
		}
	}

	private List<? extends MockReplayListener> getReplayListeners() {
		return Arrays.asList(requestCounter);
	}

}
