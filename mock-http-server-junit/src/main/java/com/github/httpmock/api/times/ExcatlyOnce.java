package com.github.httpmock.api.times;

public class ExcatlyOnce extends Times {

	@Override
	public boolean matches(int num) {
		return num == 1;
	}

	@Override
	public String getFailedDescription() {
		return "once";
	}

	public static ExcatlyOnce once() {
		return new ExcatlyOnce();
	}

}
