package com.github.httpmock;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

public class ServerExceptionTest {

	@Test
	public void isRuntimeException() throws Exception {
		ServerException serverException = new ServerException("");
		assertThat(serverException, is(instanceOf(RuntimeException.class)));
	}

	@Test
	public void hasMessage() throws Exception {
		ServerException serverException = new ServerException("my message");
		assertThat(serverException.getMessage(), is("my message"));
	}

	@Test
	public void hasCause() throws Exception {
		Throwable cause = mock(Throwable.class);
		ServerException serverException = new ServerException(cause);
		assertThat(serverException.getCause(), is(cause));
	}

}
