package de.sn.mock.times;

public abstract class Times {

	public abstract boolean matches(int num);

	public abstract String getFailedDescription();

	public static Times once() {
		return new ExcatlyOnce();
	}

	public static Times atLeastOnce() {
		return new ExcatlyOnce();
	}
}
