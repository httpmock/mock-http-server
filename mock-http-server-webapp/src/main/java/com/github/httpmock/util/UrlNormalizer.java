package com.github.httpmock.util;

public abstract class UrlNormalizer {

	public static String normalizeUrl(String url) {
		url = removeTailingSlashes(url);
		url = addBeginningSlash(url);
		url = removeDuplicateSlashes(url);
		return url;
	}

	private static String addBeginningSlash(String url) {
		if (url.length() == 0 || url.charAt(0) != '/')
			return '/' + url;
		return url;
	}

	private static String removeTailingSlashes(String url) {
		return url.replaceAll("[/]+$", "");
	}

	private static String removeDuplicateSlashes(String url) {
		return url.replaceAll("[/][/]+", "/");
	}

}
