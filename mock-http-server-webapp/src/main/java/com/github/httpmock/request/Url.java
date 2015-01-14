package com.github.httpmock.request;

import static com.github.httpmock.util.CollectionUtil.emptyMap;

import java.util.Map;

import com.github.httpmock.util.KeyValuePair;
import com.github.httpmock.util.UrlNormalizer;

public class Url {

	private String path;
	private Map<String, String> queryParameters;

	public Url(String url) {
		this.path = parsePath(url);
		this.queryParameters = parseQueryParameters(url);
	}

	private String parsePath(String url) {
		String path = url.split("[?]")[0];
		return UrlNormalizer.normalizeUrl(path);
	}

	private Map<String, String> parseQueryParameters(String url) {
		String parameterString = parseParameterString(url);
		if (parameterString.equals(""))
			return emptyMap();

		return extractParametersFromQueryString(parameterString);
	}

	private Map<String, String> extractParametersFromQueryString(String parameterString) {
		Map<String, String> parameters = emptyMap();
		String[] keyValuePairs = parameterString.split("&");
		for (String keyValuePairString : keyValuePairs) {
			KeyValuePair<String, String> keyValuePair = getKeyValuePair(keyValuePairString);
			parameters.put(keyValuePair.getKey(), keyValuePair.getValue());
		}
		return parameters;
	}

	private KeyValuePair<String, String> getKeyValuePair(String keyValuePairString) {
		String[] keyValuePair = keyValuePairString.split("=");
		if (keyValuePair.length == 1)
			return new KeyValuePair<String, String>(keyValuePair[0], null);
		return new KeyValuePair<String, String>(keyValuePair[0], keyValuePair[1]);
	}

	private String parseParameterString(String url) {
		String[] urlParts = url.split("[?]");
		if (urlParts.length > 1)
			return urlParts[1];
		return "";
	}

	public String getPath() {
		return path;
	}

	public Map<String, String> getQueryParameters() {
		return queryParameters;
	}

}
