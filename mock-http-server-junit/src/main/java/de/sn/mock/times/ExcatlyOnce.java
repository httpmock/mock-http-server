package de.sn.mock.times;

public class ExcatlyOnce extends Times {

	@Override
	public boolean matches(int num) {
		return num >= 1;
	}

	@Override
	public String getFailedDescription() {
		return "at least once";
	}

}
