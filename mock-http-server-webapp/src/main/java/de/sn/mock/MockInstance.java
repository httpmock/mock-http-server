package de.sn.mock;

import static de.sn.mock.util.CollectionUtil.emptyList;

import java.util.List;

import de.sn.mock.dto.ConfigurationDto;
import de.sn.mock.dto.RequestDto;
import de.sn.mock.request.RequestCounter;

public class MockInstance {

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

	public void count(RequestDto mock) {
		requestCounter.count(mock);
	}

	void setRequestCounter(RequestCounter requestCounter) {
		this.requestCounter = requestCounter;
	}

	public int getCount(RequestDto request) {
		return requestCounter.getCount(request);
	}

}
