package com.github.httpmock.exec;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import com.github.httpmock.ServerException;

public class PropertiesReader {
	private ClassLoader classLoader;

	public PropertiesReader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	private static final String UTF_8 = "UTF-8";

	public Properties read(String resourceName) {
		InputStream is = classLoader.getResourceAsStream(resourceName);
		if (is == null)
			throw new ServerException("resource not found: " + resourceName);
		try {
			return loadProperties(is);
		} catch (IOException e) {
			throw new ServerException(e);
		}
	}

	private Properties loadProperties(InputStream is) throws IOException {
		Properties config = new Properties();
		config.load(new InputStreamReader(is, UTF_8));
		is.close();
		return config;
	}
}
