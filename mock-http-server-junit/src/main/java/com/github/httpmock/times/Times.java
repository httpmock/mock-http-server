package com.github.httpmock.times;

public abstract class Times {

	public abstract boolean matches(int num);

	public abstract String getFailedDescription();
}
