package com.github.httpmock;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

public class ConfigurationBuilderTest {
	@Test
	public void createConfig() throws Exception {
		Configuration config = ConfigurationBuilder.config().httpPort(123).stopPort(321).ajpPort(542).build();
		assertThat(config.getHttpPort(), is(123));
		assertThat(config.getStopPort(), is(321));
		assertThat(config.getAjpPort(), is(542));
	}
}
