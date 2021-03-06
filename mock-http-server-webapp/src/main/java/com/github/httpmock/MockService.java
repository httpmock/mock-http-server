package com.github.httpmock;

import javax.ejb.Singleton;
import java.util.Map;
import java.util.UUID;

import static com.github.httpmock.util.CollectionUtil.emptyMap;

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
		return new MockInstance(randomId());
	}

	private String randomId() {
		return UUID.randomUUID().toString();
	}

	public MockInstance findMock(String mockId) {
		return mocks.get(mockId);
	}

	public void delete(String id) {
		mocks.remove(id);
	}
}
