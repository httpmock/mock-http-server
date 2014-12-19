package de.sn.mock;

import static org.mockito.Mockito.verify;

import org.apache.tomee.embedded.Container;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ShutdownHookTest {

	@Mock
	private Container container;

	@InjectMocks
	private ShutdownHook shutdownHook;

	@Test
	public void stopOnRun() throws Exception {
		shutdownHook.run();

		verify(container).stop();
	}
}
