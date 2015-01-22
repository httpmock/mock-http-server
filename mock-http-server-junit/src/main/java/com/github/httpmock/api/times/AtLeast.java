package com.github.httpmock.api.times;

public class AtLeast implements Times {

	private int numberOfTimes;

	public AtLeast(int numberOfTimes) {
		this.numberOfTimes = numberOfTimes;
	}

	@Override
	public boolean matches(int num) {
		return num >= numberOfTimes;
	}

	@Override
	public String getFailedDescription() {
		return String.format("at least %d times", numberOfTimes);
	}

	public static AtLeast atLeast(int numberOfTimes) {
		return new AtLeast(numberOfTimes);
	}

}
