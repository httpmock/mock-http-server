package com.github.httpmock.exec;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.hamcrest.xml.HasXPath.hasXPath;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationServerDistributionTest {

	private Properties properties;

	@InjectMocks
	private ApplicationServerDistribution distribution;

	@Mock
	private File distributionDirectory;

	@Before
	public void setup() {
		properties = properties();
		distribution = spy(new ApplicationServerDistribution(new File("target/test/resources/tomee"), properties));
	}

	private Properties properties() {
		Properties properties = new Properties();
		return properties;
	}

	@Test
	public void extractDistributionFolder() throws Exception {
		properties.put("timestamp", "0");
		properties.put(ApplicationServerDistribution.PROPERTY_DISTRIBUTION, ".distribution");
		doNothing().when(distribution).unzip(any(File.class), any(String.class));
		doNothing().when(distribution).writeTimestamp();

		distribution.updateIfNecessary();

		verify(distribution).unzip(new File("target/test/resources/tomee"), ".distribution");
	}


}
