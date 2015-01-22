package com.github.httpmock.api.times;

public class ExactlyOnce extends Exactly {

	public ExactlyOnce() {
		super(1);
	}

	@Override
	public String getFailedDescription() {
		return "once";
	}

	public static ExactlyOnce once() {
		return new ExactlyOnce();
	}

}
