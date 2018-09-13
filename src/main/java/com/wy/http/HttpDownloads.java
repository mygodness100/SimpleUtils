package com.wy.http;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import com.wy.enums.MethodEnum;
import com.wy.io.FileUtils;

/**
 * 下载文件类
 * FIXME
 * @author wanyang
 */
public class HttpDownloads {
	// private static ExecutorService EXE = null;
	// 默认最多5个线程
	public static final int DEFAULT_THREADCOUNT = 5;
	// 默认请求方式
	public static final MethodEnum DEFAULT_METHOD = MethodEnum.GET;

	private HttpDownloads() {
	}

	/**
	 * 多线程下载文件
	 * 
	 * @param downloadAddress:下载地址
	 * @param desFile:下载文件存放地址
	 * @param threadCount:线程数
	 */
	public static void download(String downloadAddress, File desFile, int threadCount, MethodEnum method) {
		try {
			if(!FileUtils.fileExists(desFile)) {
				throw new Exception("下载失败,文件不存在或新建文件失败");
			}
			// 限制最大线程数
			threadCount = threadCount >= 5 ? 5 : threadCount;
			URL url = new URL(downloadAddress);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(method.toString());
			// 接收信息
			conn.setDoInput(true);
			conn.connect();
			// 获得返回状态码
			int code = conn.getResponseCode();
			if (code == 200) {
				// 数据的长度
				int sum = conn.getContentLength();
				// 有了文件的长度,直接创建一个相同大小的文件
				RandomAccessFile file = new RandomAccessFile(desFile, "rw");
				file.setLength(sum);
				file.close();
				// 计算每个线程的下载量
				int threadSize = sum / threadCount + (sum % threadCount == 0 ? 0 : 1);
				// 计算每个线程下载的数据量
				for (int i = 0; i < threadCount; i++) {
					int start = i * threadSize;
					int end = start + (threadSize - 1);
					new DownloadThread(url, desFile, start, end, method.toString()).start();
				}
			}else {
				System.out.println(code);
			}
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

/**
 * 所有线程要知 道url地址 写哪一个文件 从哪儿开始写 一共多少字节,数据
 */
class DownloadThread extends Thread {
	private URL url;
	private File fileName;
	private int start;
	private int end;
	private String method;

	public DownloadThread(URL url, File fileName, int start, int end, String method) {
		this.url = url;
		this.fileName = fileName;
		this.start = start;
		this.end = end;
		this.method = method;
	}

	@Override
	public void run() {
		try {
			// 打开连接
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			// 设置
			con.setRequestMethod(method);
			con.setDoInput(true);
			// 设置从哪儿开始下载数据
			con.setRequestProperty("range", "bytes=" + start + "-" + end);
			con.connect();
			int code = con.getResponseCode();
			// ?206
			if (code == 200) {
				int size = con.getContentLength();
				System.err.println("线程:" + this.getName() + ",下载的数据量为:" + size);
				InputStream in = con.getInputStream();
				// 写同一文件
				RandomAccessFile file = new RandomAccessFile(fileName, "rw");
				// 设置从文件的什么位置开始写数据
				file.seek(start);
				// 读取数据
				byte[] b = new byte[1024];
				int len = 0;
				while ((len = in.read(b)) != -1) {
					file.write(b, 0, len);
				}
				file.close();
			}
			con.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
