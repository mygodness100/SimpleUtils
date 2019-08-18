package com.wy.http;

import java.net.URI;
import java.util.Map;

public interface HttpUtils {

	void sendGet(String urlString);

	void sendGet(URI uri);

	void sendGet(URI uri, Map<String, Object> params);

	void sendPost(String urString);

	void sendPost(URI uri);

	void sendPost(URI uri, Map<String, Object> params);
}