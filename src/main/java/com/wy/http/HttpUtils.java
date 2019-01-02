package com.wy.http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.wy.common.Encoding;
import com.wy.utils.ClassUtils;
import com.wy.utils.MapUtils;
import com.wy.utils.StrUtils;

public class HttpUtils {
	// post默认请求方式
	private static final String DEFAULT_CONTENTTYPE = "text/html;charset=utf8;application/x-www-form-urlencoded;";
	// 默认字符编码
	private static final String DEFAULT_CHARSET = Encoding.UTF8;
	// 默认超时时间,1分钟
	private static final int TIMEOUT = 60000;

	public static void main(String[] args) {
		Map<String, Object> map = new HashMap<>();
		map.put("rewrew", "234");
		map.put("rewrw", "rwfdgfd");
		System.out.println(sendGet("http://192.168.1.112:8080/JcWeb/BaseDataModule/getData"));
	}

	/**
	 * get请求,无参数或参数已经拼接到url后面
	 */
	public static Object sendGet(String desUrl) {
		return sendGet(desUrl, null);
	}

	/**
	 * 发送get请求,实体类
	 */
	public static <T> Object sendGet(String desUrl, T t) {
		if (t.getClass().isSynthetic()) {
			Map<String, Object> map = ClassUtils.beanToMap(t);
			return sendGet(desUrl, map);
		}
		return null;
	}

	/**
	 * get请求,默认不编码
	 * @param params:参数
	 */
	public static Object sendGet(String desUrl, Map<String, Object> params) {
		return sendGet(desUrl, params, false);
	}

	/**
	 * get请求,默认请求头application/json;charset=utf8
	 * @param params:参数
	 * @param isEncode:是否编码,false不编码
	 */
	public static Object sendGet(String desUrl, Map<String, Object> params, boolean isEncode) {
		return sendGet(desUrl, params, isEncode, DEFAULT_CHARSET);
	}

	/**
	 * get请求,默认请求头application/json;charset=utf8
	 * @param params:参数
	 * @param isEncode:是否编码,false不编码
	 */
	public static Object sendGet(String desUrl, Map<String, Object> params, boolean isEncode,
			String charset) {
		return sendGet(desUrl, params, isEncode, charset, TIMEOUT);
	}

	/**
	 * 发送get请求,只适合基本类型参数组成的map
	 */
	public static Object sendGet(String desUrl, Map<String, Object> params, boolean isEncode,
			String charset, int timeout) {
		try {
			String encodeUrl = createParams(desUrl, params, isEncode);
			URL url = new URL(encodeUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			// 设置请求方式,不写默认get,请求方式必须大写
			conn.setRequestMethod("GET");
			// 设置超时时间
			conn.setConnectTimeout(timeout);
			// 设置是否缓存请求
			conn.setDefaultUseCaches(false);
			conn.setUseCaches(false);
			// 需要输出参数
			conn.setDoOutput(true);
			// 设置属性请求
			// 维持长连接
			conn.setRequestProperty("Connection", "keep-Alive");
			conn.setRequestProperty("Content-Encoding", "gzip");
			conn.setRequestProperty("Contert-length", String.valueOf(desUrl.length()));
			conn.setRequestProperty("charset", charset);
			// 链接
			conn.connect();
			// 获得响应状态
			int respCode = conn.getResponseCode();
			if (HttpURLConnection.HTTP_OK == respCode) {
				BufferedReader br = new BufferedReader(
						new InputStreamReader(conn.getInputStream(), "utf-8"));
				String line = null;
				StringBuffer sb = new StringBuffer();
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				br.close();
				conn.disconnect();
				return JSON.parseObject(sb.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * post请求,无参数或参数已经拼接到url后面
	 */
	public static Object sendPost(String desUrl) {
		return sendPost(desUrl, null);
	}

	/**
	 * 发送get请求,参数为pj
	 */
	public static <T> Object sendPost(String desUrl, T t) {
		if (t.getClass().isSynthetic()) {
			Map<String, Object> map = ClassUtils.beanToMap(t);
			return sendPost(desUrl, map);
		}
		return null;
	}

	/**
	 * get请求,默认不编码
	 * @param params:参数
	 */
	public static Object sendPost(String desUrl, Map<String, Object> params) {
		return sendPost(desUrl, params, DEFAULT_CONTENTTYPE);
	}

	/**
	 * get请求,默认请求头application/json;charset=utf8
	 * @param params:参数
	 * @param isEncode:是否编码,false不编码
	 */
	public static Object sendPost(String desUrl, Map<String, Object> params, String contentType) {
		return sendPost(desUrl, params, contentType, DEFAULT_CHARSET);
	}

	/**
	 * get请求,默认请求头application/json;charset=utf8
	 * @param params:参数
	 * @param isEncode:是否编码,false不编码
	 */
	public static Object sendPost(String desUrl, Map<String, Object> params, String contentType,
			String charset) {
		return sendPost(desUrl, params, contentType, charset, TIMEOUT);
	}

	/**
	 * 发送post请求
	 */
	public static Object sendPost(String desUrl, Map<String, Object> params, String contentType,
			String charset, int timeout) {
		try {
			URL url = new URL(desUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setConnectTimeout(timeout);
			conn.setDefaultUseCaches(false);
			conn.setUseCaches(false);
			// 需要输出参数
			conn.setDoOutput(true);
			// 设置属性请求
			// 维持长连接
			conn.setRequestProperty("Connection", "keep-Alive");
			conn.setRequestProperty("charset",
					charset = StrUtils.isBlank(charset) ? DEFAULT_CHARSET : charset);
			conn.setRequestProperty("Content-Encoding", "gzip");
			conn.setRequestProperty("Contert-length", String.valueOf(desUrl.length()));
			conn.setRequestProperty("Content-type",
					contentType = StrUtils.isBlank(contentType) ? DEFAULT_CONTENTTYPE
							: contentType);
			conn.connect();
			if (MapUtils.isNotBlank(params)) {
				// 参数输出
				DataOutputStream dataout = new DataOutputStream(conn.getOutputStream());
				String param = JSON.toJSONString(params);
				// 将参数输出到连接
				dataout.writeBytes(param);
				// 输出完成后刷新并关闭流
				dataout.flush();
				// 重要且易忽略步骤 (关闭流,切记!)
				dataout.close();
			}
			// 获得响应状态
			int respCode = conn.getResponseCode();
			if (HttpURLConnection.HTTP_OK == respCode) {
				BufferedReader br = new BufferedReader(
						new InputStreamReader(conn.getInputStream(), "utf-8"));
				String line = null;
				StringBuffer sb = new StringBuffer();
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				br.close();
				conn.disconnect();
				return JSON.parseObject(sb.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 发送请求
	 * @param type:请求方式
	 * @param params:参数
	 * @param contentType:请求的contenttype
	 */
	public static void send(String type, Map<String, Object> params, String contentType) {

	}

	/**
	 * url拼接,默认不对参数进行编码
	 */
	public static String createParams(String url, Map<String, Object> params) {
		return createParams(url, params, false);
	}

	/**
	 * url拼接
	 */
	public static String createParams(String url, Map<String, Object> params, boolean isEncode) {
		if (StrUtils.isBlank(url) || MapUtils.isBlank(params)) {
			return url;
		}
		return MessageFormat.format("{0}?{1}", url, createParams(params, isEncode));
	}

	/**
	 * 参数拼接
	 */
	public static String createParams(Map<String, Object> params, boolean isEncode) {
		return createParams(params, isEncode, Encoding.UTF8);
	}

	/**
	 * 参数拼接
	 */
	public static String createParams(Map<String, Object> params, boolean isEncode,
			String charset) {
		List<String> list = new ArrayList<>();
		for (String key : params.keySet()) {
			if (isEncode) {
				try {
					list.add(URLEncoder.encode(serializerParams(key, params.get(key)), charset));
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				list.add(serializerParams(key, params.get(key)));
			}
		}
		return String.join("&", list);
	}

	/**
	 * 判断是否需要对值进行序列化
	 */
	public static String serializerParams(String key, Object value) {
		if (key == null) {
			return null;
		} else if (value == null) {
			return key + "=";
		} else if (ClassUtils.isPrimitives(value.getClass())) {
			return key + "=" + value;
		} else {
			return key + "=" + JSON.toJSONString(value);
		}
	}

	/**
	 * 文件下载
	 * @param downloadAddress 下载地址
	 * @param desFile 本地存储地址
	 */
	public static void httpDownload(String downloadAddress, String desFile) {
		httpDownload(downloadAddress, new File(desFile));
	}

	/**
	 * 文件下载
	 * @param downloadAddress 下载地址
	 * @param desFile 本地存储地址
	 */
	public static void httpDownload(String downloadAddress, File desFile) {
		httpDownload(downloadAddress, desFile, HttpDownloads.DEFAULT_THREADCOUNT);
	}

	/**
	 * 文件下载
	 * @param downloadAddress 下载地址
	 * @param desFile 本地存储地址
	 * @param threadCount 下载线程数
	 */
	public static void httpDownload(String downloadAddress, File desFile, int threadCount) {
		httpDownload(downloadAddress, desFile, threadCount, HttpDownloads.DEFAULT_METHOD);
	}

	/**
	 * 文件下载
	 * @param downloadAddress 文件下载地址
	 * @param desFile 本地存储地址
	 * @param threadCount 下载线程数
	 */
	public static void httpDownload(String downloadAddress, String desFile, int threadCount) {
		httpDownload(downloadAddress, new File(desFile), threadCount, HttpDownloads.DEFAULT_METHOD);
	}

	/**
	 * 文件下载
	 * @param downloadAddress 文件下载地址
	 * @param desFile 本地存储地址
	 * @param threadCount 下载线程数
	 * @param method 请求远程文件方式
	 */
	public static void httpDownload(String downloadAddress, File desFile, int threadCount,
			String method) {
		HttpDownloads.download(downloadAddress, desFile, threadCount, method);
	}

	/**
	 * 基于httpclient的get方法 FIXME
	 */
	public static void httpClientGet() {

	}
}
