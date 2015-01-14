package com.github.httpmock.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

public class KeyValuePairTest {
	@Test
	public void keyAndValue() throws Exception {
		KeyValuePair<String, String> keyValuePair = new KeyValuePair<String, String>("key", "value");
		assertThat(keyValuePair.getKey(), is("key"));
		assertThat(keyValuePair.getValue(), is("value"));
	}
}
