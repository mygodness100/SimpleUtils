package com.wy.crawler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.wy.io.FileUtils;
import com.wy.io.ImageUtils;
import com.wy.utils.NumUtils;

/**
 * 网络爬虫 利用jsoup解析html,他可以像jquery那样利用选择器抓取dom元素,select方法
 * @author wanyang 2018年6月30日
 */
public final class Crawler extends Thread {

	public static ThreadPoolExecutor POOL_FIX = (ThreadPoolExecutor)Executors.newFixedThreadPool(20);
	public static ThreadPoolExecutor POOL_CACHE = (ThreadPoolExecutor)Executors.newCachedThreadPool();
//	 public static ThreadPoolExecutor POOL_CACHE = (ThreadPoolExecutor)Executors.newFixedThreadPool(40);
	// 漫画主页地址
	public static final String HOST = "https://kukudas.xyz";
	// 网站主文件夹
	public static final String MAIN_FOLDER = "G:\\comics\\kukudas";
	// 所有爬取图片地址存放的文件夹地址
	private static final String FILE_ALL_URLS = "F:\\repository\\javas\\Utils\\src\\com\\wy\\crawler\\kukudas1530372519755.txt";
	// 已经爬过的网址列文件夹地址
	private static final String FILE_ALREADY_CRAWLER = "F:\\repository\\javas\\Utils\\src\\com\\wy\\crawler";
	// 每次爬取生成的已经爬取网址文件,用于从原网址中删除已经爬取的地址
	public static final File CRAWLER_FILE = makeCrawler();
	
	static {
		POOL_FIX.setCorePoolSize(20);
		POOL_CACHE.setCorePoolSize(40);
	}

	/**
	 * 获得某漫画地址的所有漫画
	 * @第一步:获得主页或其他分页所有漫画地址
	 * @第二步:从第一步获得的地址上获得该漫画每一集的地址
	 * @第三步:从第二步上获得的地址中获得该地址中所有图片地址
	 * @第四步:将图片下载保存到本地
	 */
	public static void main(String[] args) throws UnsupportedEncodingException {
		// 1.从主页拿所有的url地址
		// getHomePageUrls(URLDecoder.decode(host, "utf8"));
		// Set<String> homePageUrl = new HashSet<>();
		// 2.获取已经爬取的url地址,拿到所有的图片
//		 Set<String> homeUrls = getHomeUrls();
//		 getSingleUrls(HOST, homeUrls);
		// 3.获取单个url地址里的图片
		// homePageUrl.add(
		// URLDecoder.decode("https://kukudas.xyz/toon/%EC%9A%95%EA%B5%AC%EC%99%95",
		// "utf8"));
		// getSingleUrls(host, homePageUrl);
		// 4.将图片处理成pdf
		handlerImgs(MAIN_FOLDER);
	}

	/**
	 * 第一步:需找主页的所有连接地址 需要寻找该网站路径为.toonMainWeeks .toonMainWeek ul li a>href
	 * @param url 主页地址
	 */
	public static void getHomePageUrls(String host) {
		// 主页a标签url结果集
		Set<String> result = new HashSet<>();
		// 第一步:获得主页显示的所有漫画主页地址
		Document doc = getHtmlTextByUrl(host);
		if (doc != null) {
			// 获得主页部分所有的单本漫画主页连接,a标签
			Elements mainAll = doc.select(".toonMainWeeks .toonMainWeek ul li a");
			if (mainAll == null) {
				System.out.println("error");
				return;
			}
			String desUrl = null;
			String filePath = MAIN_FOLDER + System.currentTimeMillis() + ".txt";
			BufferedOutputStream bos = null;
			try {
				bos = new BufferedOutputStream(new FileOutputStream(filePath));
				for (Element mainA : mainAll) {
					desUrl = host + mainA.attr("href");
					result.add(desUrl);
					byte[] bytes = (desUrl + "\r\n").getBytes(Charset.defaultCharset());
					bos.write(bytes);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				FileUtils.close(bos);
			}
		}
		getSingleUrls(host, result);
	}

	/**
	 * 第二步:获得每本漫画的主页url地址之后,拿到该页的集数地址
	 * @每一部漫画的主页url:https://kukudas.xyz/toon/요괴소녀
	 */
	public static void getSingleUrls(String host, Set<String> homePageUrl) {
		List<String> homePageUrls = new ArrayList<>(homePageUrl);
		BufferedWriter bw = null;
		// 所有的集数
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(CRAWLER_FILE)));
			for (String url : homePageUrls) {
				System.out.println("主页域名" + url);
				bw.write(url);
				bw.newLine();
				bw.flush();
				Document singleDoc = getHtmlTextByUrl(url);
				// 所有的集数地址
				Elements episodes = singleDoc.select(".toonInfoWrap .toonEpisodeList li a");
				Set<String> allEpisodes = null;
				if (episodes != null) {
					allEpisodes = new HashSet<>();
					for (Element episode : episodes) {
						allEpisodes.add(host + episode.attr("href"));
					}
					getEveryEpisode(allEpisodes, url.substring(url.lastIndexOf("/") + 1));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			FileUtils.close(bw);
		}
	}

	/**
	 * 第三步:获得每一集的地址
	 * @每一集url:https://kukudas.xyz/view/144840/요괴소녀-40화
	 */
	public static void getEveryEpisode(Set<String> allEpisodeUrl, String comicName) {
		List<String> allEpisodeUrls = new ArrayList<>(allEpisodeUrl);
		Document singleDoc = null;
		for (String url : allEpisodeUrls) {
			System.out.println("每一集域名:" + url);
			// 每一部漫画的文件夹
			String parentDir = MAIN_FOLDER + File.separator + comicName;
			File file = new File(parentDir);
			if (!file.exists()) {
				file.mkdirs();
			}
			singleDoc = getHtmlTextByUrl(url);
			if (singleDoc == null) {
				return;
			}
			// 所有的集数地址
			Elements episodes = singleDoc.select(".contents div img");
			try {
				if (episodes != null) {
					final List<String> picUrls = new ArrayList<>();
					for (Element episode : episodes) {
						picUrls.add(episode.attr("src"));
					}
					POOL_CACHE.execute(new Runnable() {
						@Override
						public void run() {
							System.out.println("已使用线程数:"+POOL_CACHE.getActiveCount());
							getSinglePics(parentDir, url.substring(url.lastIndexOf("/") + 1),
									picUrls);
						}
					});
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 第四步:获得所有每一集所有图片地址,并保存到本地
	 */
	public static void getSinglePics(String filePath, String dirName, List<String> picUrls) {
//		HttpHost proxy = new HttpHost("127.0.0.1", 8580, "http");
		// 下载每一张图片的地址
		CloseableHttpClient defaultClient = HttpClients.createDefault();	
		HttpGet get = null;
		try {
			for (String url : picUrls) {
				System.out.println("图片地址:" + url);
				Thread.sleep(2000);
				get = new HttpGet(url);
				get.setHeader("User-Agent",
						"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.79 Safari/537.1\"");
				get.setHeader("Accept",
						"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
				// 设置代理
//				RequestConfig rc = RequestConfig.custom().setProxy(proxy).build();
//				get.setConfig(rc);
				
				HttpResponse resp = defaultClient.execute(get);
				if (HttpStatus.SC_OK == resp.getStatusLine().getStatusCode()) {
					POOL_CACHE.submit(new Runnable() {
						@Override
						public void run() {
							try {
								saveFile(resp.getEntity().getContent(), filePath, dirName, url,
										picUrls.indexOf(url));
							} catch (Exception e) {
								e.printStackTrace();
							}finally {
								System.out.println("还有多少线程数:"+POOL_CACHE.getActiveCount());
							}
						}
					});
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将图片存放到指定地址
	 * @param filePath 存放的指定文件夹
	 * @param dirName 漫画名称
	 * @param url 图片地址
	 * @param index 图片是第几集,可能会有错误
	 */
	public static void saveFile(InputStream in, String filePath, String dirName, String url,
			int index) {
		FileOutputStream fos = null;
		try {
			String parentDir = filePath + File.separator + dirName;
			File dir = new File(parentDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			fos = new FileOutputStream(new File(
					parentDir + File.separator + index + url.substring(url.lastIndexOf("."))));
			byte[] buf = new byte[1024];
			int len = 0;
			while ((len = in.read(buf)) != -1) {
				fos.write(buf, 0, len);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			FileUtils.close(fos);
		}
	}

	/**
	 * 获得网页连接,根据网址不同获得不同页面的doc
	 */
	public static Document getHtmlTextByUrl(String url) {
		Document doc = null;
		try {
			int i = (int) (Math.random() * 1000); // 做一个随机延时，防止网站屏蔽
			while (i != 0) {
				i--;
			}
			doc = Jsoup.connect(url).data("query", "Java").userAgent("Mozilla")
					.cookie("auth", "token").timeout(300000).post();
		} catch (IOException e) {
			try {
				doc = Jsoup.connect(url).timeout(5000000).get();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return doc;
	}

	/**
	 * 从已经存放好的所有url地址里选择性爬取网站图片
	 */
	public static Set<String> getHomeUrls() {
		BufferedReader br = null;
		try {
			InputStreamReader isr = new InputStreamReader(
					new BufferedInputStream(new FileInputStream(FILE_ALL_URLS)));
			br = new BufferedReader(isr);
			String line = null;
			Set<String> result = new HashSet<>();
			while ((line = br.readLine()) != null) {
				result.add(line);
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			FileUtils.close(br);
		}
		return null;
	}

	/**
	 * 每次爬图片完成之后将已经爬过的网址存如到一个新文件中
	 */
	public static File makeCrawler() {
		File srcPath = new File(FILE_ALREADY_CRAWLER);
		if (srcPath.exists()) {
			File[] oldFiles = srcPath.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					if (name.contains("爬取") && name.endsWith(".txt")) {
						return true;
					}
					return false;
				}
			});
			int max = 0;
			if (oldFiles != null && oldFiles.length == 0) {
				max = 1;
			} else {
				int[] times = new int[oldFiles.length];
				for (int i = 0; i < oldFiles.length; i++) {
					String serial = oldFiles[i].getName().replaceAll("\\D", "");
					times[i] = Integer.parseInt(serial);
				}
				max = NumUtils.getMax(times) + 1;
			}
			return new File(FILE_ALREADY_CRAWLER + File.separator + "第" + max + "次爬取.txt");
		}
		return null;
	}

	/**
	 * 1.生成pdf文件 2.将已经爬完的图片转成每一集的pdf文件存放到当前文件夹
	 * @param fileParent 最外层文件夹->每部漫画文件夹->每部漫画每集文件夹->每集图片合集
	 */
	public static void handlerImgs(String fileParent) {
		File file = new File(fileParent);
		if (file.exists()) {
			File[] listFiles = file.listFiles();
			if (listFiles != null && listFiles.length > 0) {
				for (File children : listFiles) {
					// 判断是不是文件夹,该层文件夹是每一本漫画的独立文件夹
					// 通过该文件夹找到该文件里漫画的每一集的文件夹,找到的文件夹就是该集漫画的所有图片合集
					if (children.isDirectory()) {
						// 文件夹名称
						String folderName = children.getName();
						// 每一集漫画的合集
						File[] everyAll = children.listFiles();
						if (everyAll != null && everyAll.length > 0) {
							// 对每一集漫画里的图片进行转换
							for (File child : everyAll) {
								POOL_FIX.execute(new Runnable() {
									@Override
									public void run() {
										String pdfName = child.getName().replaceAll(folderName, "")
												.replaceAll("\\D", "");
										// 1.多线程生成pdf文件
										ImageUtils.img2Pdf(child.getAbsolutePath(), pdfName);

										// 2.多线程将pdf文件转移到外层同一个文件夹中
										// transFile(child.getAbsolutePath(), pdfName);

										// 3.多线程删除已经不需要的文件夹
										// if (child.isDirectory()) {
										// FileUtils.deleteFile(child);
										// }
									}
								});
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 将文件移到外层文件中
	 * @param filePath 文件路径
	 * @param fileName 文件名称
	 */
	public static void transFile(String filePath, String fileName) {
		String pdfPath = filePath + File.separator + fileName + ".pdf";
		String parent = new File(filePath).getParent() + File.separator + fileName + ".pdf";
		File src = new File(pdfPath);
		File des = new File(parent);
		src.renameTo(des);
	}
}