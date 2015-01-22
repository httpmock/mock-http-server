package com.github.httpmock.request;

import org.hamcrest.collection.IsMapContaining;
import org.junit.Test;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class UrlTest {
	@Test
	public void extractPathAndQuery() throws Exception {
		Url url = new Url("/some/url?var=value");
		assertThat(url.getPath(), is("/some/url"));
		assertThat(url.getQueryParameters(),
				IsMapContaining.hasEntry("var", "value"));
	}

	@Test
	public void multipleParameters() throws Exception {
		Url url = new Url("/some/url?var=value&var2=value2");
		assertThat(url.getPath(), is("/some/url"));
		assertThat(url.getQueryParameters(),
				IsMapContaining.hasEntry("var", "value"));
		assertThat(url.getQueryParameters(),
				IsMapContaining.hasEntry("var2", "value2"));
	}

	@Test
	public void parameterWithoutValue() throws Exception {
		Url url = new Url("/some/url?var");
		assertThat(url.getPath(), is("/some/url"));
		assertThat(url.getQueryParameters(),
				IsMapContaining.hasEntry("var", null));
	}

	@Test
	public void normalizePath() throws Exception {
		Url url = new Url("//some/url/?var=value");
		assertThat(url.getPath(), is("/some/url"));
		assertThat(url.getQueryParameters(),
				IsMapContaining.hasEntry("var", "value"));
	}

	@Test
	public void withoutParameters() throws Exception {
		Url url = new Url("/some/url");
		assertThat(url.getPath(), is("/some/url"));
		assertThat(url.getQueryParameters().keySet(), is(empty()));
	}
}
