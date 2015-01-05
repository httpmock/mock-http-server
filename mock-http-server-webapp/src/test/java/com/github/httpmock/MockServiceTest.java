package com.github.httpmock;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.httpmock.MockInstance;
import com.github.httpmock.MockService;

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
	public void findMock() throws Exception {
		MockInstance mock = mockService.create();
		assertThat(mockService.findMock(mock.getId()), is(mock));
	}

	@Test
	public void delete() throws Exception {
		MockInstance mock = mockService.create();
		String mockId = mock.getId();
		mockService.delete(mockId);
		assertThat(mockService.findMock(mockId), is(nullValue()));
	}

	@Test
	public void deleteNonExisting() throws Exception {
		String mockId = "someid";
		mockService.delete(mockId);
		assertThat(mockService.findMock(mockId), is(nullValue()));
	}
}
