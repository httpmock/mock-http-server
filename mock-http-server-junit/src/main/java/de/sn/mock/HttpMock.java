package de.sn.mock;

import de.sn.mock.dto.ConfigurationDto;
import de.sn.mock.dto.RequestDto;

public interface HttpMock {
	public void delete();

	public void addConfig(ConfigurationDto config);

	public Stubbing when(RequestDto request);
}
