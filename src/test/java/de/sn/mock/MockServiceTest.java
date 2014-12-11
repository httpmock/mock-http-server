package de.sn.mock;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MockServiceTest {

	private MockService mockService;

	@Before
	public void setup() {
		mockService = new MockService();
	}

	@Test
	public void create() throws Exception {
		MockInstance mock = mockService.create();

		assertThat(mock, is(notNullValue()));
		assertThat(mock.getId(), is(notNullValue()));
		assertThat(mock.getConfigurations(), is(empty()));
	}

	@Test
	public void findMock() {
		MockInstance mock = mockService.create();
		assertThat(mockService.findMock(mock.getId()), is(mock));
	}
}
