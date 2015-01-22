package com.github.httpmock.api.times;

public class Never extends Exactly {

	public Never() {
		super(0);
	}

	@Override
	public String getFailedDescription() {
		return "never";
	}

	public static Never never() {
		return new Never();
	}

}
