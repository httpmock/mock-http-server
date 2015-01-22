package com.github.httpmock.api.times;

public interface Times {

	boolean matches(int num);

	String getFailedDescription();
}
