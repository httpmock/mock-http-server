package de.sn.mock;

import static de.sn.mock.util.CollectionUtil.emptyMap;

import java.util.Map;
import java.util.UUID;

import javax.ejb.Singleton;

@Singleton
public class MockService {

	private Map<String, MockInstance> mocks;

	public MockService() {
		mocks = emptyMap();
	}

	public MockInstance create() {
		MockInstance mock = createMockInstance();
		mocks.put(mock.getId(), mock);
		return mock;
	}

	private MockInstance createMockInstance() {
		MockInstance mockInstance = new MockInstance(randomId());
		return mockInstance;
	}

	private String randomId() {
		return UUID.randomUUID().toString();
	}

	public MockInstance findMock(String mockId) {
		return mocks.get(mockId);
	}
}
