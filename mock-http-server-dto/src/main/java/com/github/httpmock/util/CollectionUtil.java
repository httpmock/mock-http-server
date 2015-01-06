package com.github.httpmock.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CollectionUtil {

	public static <T> List<T> emptyList() {
		return new ArrayList<T>();
	}

	public static <T> List<T> emptyList(int size) {
		return new ArrayList<T>(size);
	}

	public static <K, V> Map<K, V> emptyMap() {
		return new HashMap<K, V>();
	}
}
