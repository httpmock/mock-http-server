package de.sn.mock.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import de.sn.mock.util.KeyValuePair;

public class KeyValuePairTest {
	@Test
	public void keyAndValue() throws Exception {
		KeyValuePair<String, String> keyValuePair = new KeyValuePair<>("key",
				"value");
		assertThat(keyValuePair.getKey(), is("key"));
		assertThat(keyValuePair.getValue(), is("value"));
	}
}
