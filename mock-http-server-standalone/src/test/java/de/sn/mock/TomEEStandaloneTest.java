package de.sn.mock;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

import java.io.File;

import org.apache.tomee.embedded.Configuration;
import org.apache.tomee.embedded.Container;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TomEEStandaloneTest {
	private static final int SERVER_PORT = 0;
	private static final int SERVER_STOP_PORT = 0;

	@Mock
	private Container container;

	@InjectMocks
	private TomEEStandalone tomee;

	private TomEEStandalone createServer() {
		return new TomEEStandalone(SERVER_PORT, SERVER_STOP_PORT, container);
	}

	@Before
	public void setup() throws Exception {
		tomee = createServer();
	}

	@Test
	public void setupContainer() throws Exception {
		verify(container).setup(any(Configuration.class));
	}

	@Test
	public void startContainer() throws Exception {
		tomee.start();
		verify(container).start();
	}

	@Test
	public void stopContainer() throws Exception {
		tomee.stop();
		verify(container).stop();
	}

	@Test
	public void deployWebapp() throws Exception {
		String pathToWar = "some.war";
		File war = new File(pathToWar);

		tomee.deploy(pathToWar);
		verify(container).deploy("mockserver", war);
	}
}
