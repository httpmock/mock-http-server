package com.github.httpmock.util;

import org.junit.Test;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CollectionUtilTest {

	@Test
	public void emptyList() throws Exception {
		assertThat(CollectionUtil.emptyList(), is(empty()));
	}

	@Test
	public void emptyListWithSize() throws Exception {
		assertThat(CollectionUtil.emptyList(3), is(empty()));
	}

	@Test
	public void emptyMap() throws Exception {
		assertThat(CollectionUtil.emptyMap().keySet(), is(empty()));
	}
}
