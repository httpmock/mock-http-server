package com.github.httpmock.times;

public class Never extends Times {

	@Override
	public boolean matches(int num) {
		return num == 0;
	}

	@Override
	public String getFailedDescription() {
		return "never";
	}

	public static Never never() {
		return new Never();
	}

}
