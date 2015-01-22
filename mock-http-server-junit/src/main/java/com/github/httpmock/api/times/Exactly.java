package com.github.httpmock.api.times;

public class Exactly implements Times {

	private int numberOfTimes;

	public Exactly(int numberOfTimes) {
		this.numberOfTimes = numberOfTimes;
	}

	@Override
	public boolean matches(int num) {
		return num == numberOfTimes;
	}

	@Override
	public String getFailedDescription() {
		return String.format("exactly %d times", numberOfTimes);
	}

	public static Exactly times(int numberOfTimes) {
		return new Exactly(numberOfTimes);
	}

}
