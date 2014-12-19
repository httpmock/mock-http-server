package de.sn.mock.util;

public class KeyValuePair<T1, T2> {
	private T1 key;
	private T2 value;

	public KeyValuePair(T1 key, T2 value) {
		this.key = key;
		this.value = value;
	}

	public T1 getKey() {
		return key;
	}

	public T2 getValue() {
		return value;
	}
}
