package com.github.httpmock.api.times;

public class AtLeastOnce extends AtLeast {

	public AtLeastOnce() {
		super(1);
	}

	@Override
	public String getFailedDescription() {
		return "at least once";
	}

	public static AtLeastOnce atLeastOnce() {
		return new AtLeastOnce();
	}

}
