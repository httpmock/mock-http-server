package de.sn.mock;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.apache.tomee.embedded.Container;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Test
	public void stopOnRun() throws Exception {
		shutdownHook.run();

		verify(container).stop();
	}

	@Test
	public void errorOnShutdown() throws Exception {
		Exception exception = mock(Exception.class);
		doThrow(exception).when(container).stop();
		shutdownHook.run();

		verify(exception).printStackTrace();
	}
}
