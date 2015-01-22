package com.github.httpmock.exec;

import com.github.httpmock.ServerException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PortUtil.class)
public class PortUtilTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Test
	public void getRandomPort() throws Exception {
		assertThat(PortUtil.getRandomPorts(1).get(0), is(not(0)));
	}

	@Test
	public void getRandomPorts() throws Exception {
		List<Integer> ports = PortUtil.getRandomPorts(2);
		assertThat(ports, hasSize(2));
		assertThat(ports.get(0), is(not(equalTo(ports.get(1)))));
	}

	@Test
	public void socketError() throws Exception {
		PowerMockito.spy(PortUtil.class);
		IOException exception = mock(IOException.class);
		PowerMockito.when(PortUtil.class, "createServerSocket").thenThrow(exception);

		expectedException.expect(ServerException.class);
		expectedException.expectCause(is(exception));

		PortUtil.getRandomPorts(1);
	}
}
