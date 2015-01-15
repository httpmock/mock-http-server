package com.github.httpmock.rules;

import com.github.httpmock.ApplicationServerRunner;
import com.github.httpmock.Configuration;

public class ApplicationServerRunnerFactory {

	public ApplicationServerRunner create(Configuration config) {
		return new ApplicationServerRunner(config, ApplicationServerRunner.readProperties());
	}

}
