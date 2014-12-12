package de.sn.mock.util;

import static de.sn.mock.util.UrlNormalizer.normalizeUrl;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

public class UrlNormalizerTest {
	@Test
	public void removeTailingSlashes() throws Exception {
		assertThat(normalizeUrl("/some/url/"), is("/some/url"));
	}

	@Test
	public void addBeginnigSlash() throws Exception {
		assertThat(normalizeUrl("/some/url"), is("/some/url"));
		assertThat(normalizeUrl("some/url"), is("/some/url"));
		assertThat(normalizeUrl(""), is("/"));
	}

	@Test
	public void removeDuplicateSlashes() throws Exception {
		assertThat(normalizeUrl("/some///url//"), is("/some/url"));
	}
}
