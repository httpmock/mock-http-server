package de.sn.mock;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PortUtil.class)
public class PortUtilTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Test
	public void getRandomPort() throws Exception {
		assertThat(PortUtil.getRandomPort(), is(not(0)));
	}

	@Test
	public void socketError() throws Exception {
		PowerMockito.spy(PortUtil.class);
		IOException exception = mock(IOException.class);
		PowerMockito.when(PortUtil.class, "createServerSocket").thenThrow(
				exception);

		expectedException.expect(ServerException.class);
		expectedException.expectCause(is(exception));

		PortUtil.getRandomPort();
	}
}
