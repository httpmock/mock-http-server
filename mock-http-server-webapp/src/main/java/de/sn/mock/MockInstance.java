package de.sn.mock;

import static de.sn.mock.util.CollectionUtil.emptyList;

import java.util.List;

import de.sn.mock.dto.ConfigurationDto;

public class MockInstance {

	private String id;
	private List<ConfigurationDto> configurations;

	public MockInstance(String id) {
		this.id = id;
		this.configurations = emptyList();
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

}
