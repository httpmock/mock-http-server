package com.github.httpmock.exec;

public class ApplicationServerRunnerFactory {

	public ApplicationServerRunner create(Configuration config) {
		return new ApplicationServerRunner(config);
	}

}
