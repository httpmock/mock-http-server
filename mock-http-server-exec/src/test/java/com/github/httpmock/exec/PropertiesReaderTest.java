package com.github.httpmock.exec;

import com.github.httpmock.ServerException;
import org.fusesource.hawtbuf.ByteArrayInputStream;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PropertiesReaderTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Mock
	private ClassLoader classLoader;

	private PropertiesReader propertiesReader;

	@Before
	public void setUp() throws Exception {
		propertiesReader = new PropertiesReader(classLoader);
	}

	@Test
	public void readProperties() throws Exception {
		when(classLoader.getResourceAsStream("some.properties")).thenReturn(new ByteArrayInputStream("var=value".getBytes()));

		propertiesReader = new PropertiesReader(classLoader);
		Properties properties = propertiesReader.read("some.properties");

		assertThat(properties.getProperty("var"), is((Object) "value"));
	}

	@Test
	public void resourceNotFound() throws Exception {
		expectedException.expect(ServerException.class);
		expectedException.expectMessage("resource not found: some.properties");
		propertiesReader.read("some.properties");
	}

}
