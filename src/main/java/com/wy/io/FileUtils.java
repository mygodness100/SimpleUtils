package com.wy.io;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import com.wy.utils.StrUtils;

/**
 * 文件帮助类,未使用
 * 
 * @author 万杨
 */
public final class FileUtils {
	private static final Logger logger = Logger.getLogger(FileUtils.class);

	/**
	 * 文件后缀类型归类
	 */
	public static final Map<Integer, List<String>> FILE_SUFFIXMAP = new HashMap<Integer, List<String>>() {
		private static final long serialVersionUID = 1L;
		{
			put(0, new ArrayList<>(Arrays.asList(".BMP", ".PNG", ".GIF", ".JPG", ".JPEG")));
			put(1, new ArrayList<>(Arrays.asList(".AMR", ".MP3", ".WMA", ".WAV", ".MID")));
			put(2, new ArrayList<>(Arrays.asList(".MP4", ".AVI", ".3GP", ".RM", ".RMVB", ".WMV")));
			put(3, new ArrayList<>(Arrays.asList(".TXT", ".JSON", ".XML", ".DOC", ".XLS", ".XLSX")));
		}
	};

	/**
	 * 根据文件后缀返回文件类型
	 */
	public static int getFileType(String suffix) {
		for (Map.Entry<Integer, List<String>> entry : FILE_SUFFIXMAP.entrySet()) {
			if (entry.getValue().contains(suffix.toUpperCase())) {
				return entry.getKey();
			}
		}
		return 4;
	}

	/**
	 * 检查文件是否存在,存在为true,不存在则创建新文件
	 */
	public static boolean fileExists(String filePath) {
		return fileExists(new File(filePath));
	}

	/**
	 * 检查文件是否存在,存在为true,不存在则创建新文件
	 */
	public static boolean fileExists(File file) {
		try {
			if (file.exists()) {
				return true;
			} else {
				return file.createNewFile();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
		}
		return false;
	}

	public static String getFileName(String filename) {
		if (StrUtils.isBlank(filename)) {
			return "文件名不能为空";
		}
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(filename);
			byte[] b = new byte[128];
			try {
				while (fis.read() > -1) {
					fis.read(b);
					System.out.println(new String(b));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 将一个文件移动到另外一个地方,不删除源文件,删除已经存在于目标地址的文件
	 * 
	 * @param from 源文件,需要进行移动的文件
	 * @param to 目标地址
	 */
	public static final void moveFileDT(File from, File to) {
		moveFile(from, to, true, true);
	}

	/**
	 * 将一个文件移动到另外一个地方,删除或移动源文件,删除已经存在于目标地址的文件
	 * 
	 * @param from 源文件,需要进行移动的文件
	 * @param to 目标地址
	 */
	public static final void moveFileDFDT(File from, File to) {
		moveFile(from, to, true, false);
	}

	/**
	 * 移动文件从一个地方到另一个地方,若目标文件已经存在,判断是否删除之后再移动
	 * 
	 * @param from 源文件,需要进行移动的文件
	 * @param to 目标地址
	 * @param deleteTo 若目标文件存在,是否删除,true删除
	 * @param isCopy true复制,false剪切
	 */
	public static final void moveFile(File from, File to, boolean deleteTo, boolean isCopy) {
		if (to.exists()) {
			if (deleteTo) {
				if (!to.delete()) {
					throw new RuntimeException(String.format("删除%s文件失败", to.getName()));
				}
			} else {
				System.exit(0);
			}
		}
		// renameto只能在一个盘内移动文件,不能从c移到d,在linux上也是一样,而且移动之后相当于剪切,原来的文件就不在了
		if (!isCopy) {
			if (from.renameTo(to)) {
				return;
			}
		}
		// 若移动失败,则直接用流的方式复制或剪切
		moveFile(from, to, isCopy);
	}

	/**
	 * 直接用流移动文件
	 * 
	 * @param from 源文件
	 * @param to 目标地址
	 * @param isCopy true复制,false剪切
	 */
	public static final void moveFile(File from, File to, boolean isCopy) {
		InputStream is = null;
		OutputStream os = null;
		try {
			is = new FileInputStream(from);
			os = new FileOutputStream(to);
			byte[] buffer = new byte[1024];
			int read;
			while ((read = is.read(buffer)) != -1) {
				os.write(buffer, 0, read);
			}
			if (!isCopy) {
				if (!from.delete()) {
					throw new RuntimeException("删除源文件失败");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(os, is);
		}
	}

	/**
	 * 生成缩略图
	 * 
	 * @param originalFile
	 * @param thumbnailFile
	 * @param thumbWidth
	 * @param thumbHeight
	 * @param quality
	 */
	public static void transform(String originalFile, String thumbnailFile, int thumbWidth, int thumbHeight,
			int quality) throws Exception {
		Image image = javax.imageio.ImageIO.read(new File(originalFile));

		double thumbRatio = (double) thumbWidth / (double) thumbHeight;
		int imageWidth = image.getWidth(null);
		int imageHeight = image.getHeight(null);
		double imageRatio = (double) imageWidth / (double) imageHeight;
		if (thumbRatio < imageRatio) {
			thumbHeight = (int) (thumbWidth / imageRatio);
		} else {
			thumbWidth = (int) (thumbHeight * imageRatio);
		}

		if (imageWidth < thumbWidth && imageHeight < thumbHeight) {
			thumbWidth = imageWidth;
			thumbHeight = imageHeight;
		} else if (imageWidth < thumbWidth) {
			thumbWidth = imageWidth;
		} else if (imageHeight < thumbHeight) {
			thumbHeight = imageHeight;
		}

		BufferedImage thumbImage = new BufferedImage(thumbWidth, thumbHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics2D = thumbImage.createGraphics();
		graphics2D.setBackground(Color.WHITE);
		graphics2D.setPaint(Color.WHITE);
		graphics2D.fillRect(0, 0, thumbWidth, thumbHeight);
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics2D.drawImage(image, 0, 0, thumbWidth, thumbHeight, null);
		ImageIO.write(thumbImage, "JPG", new File(thumbnailFile));
	}

	/**
	 * 批量修改文件名,自用
	 */
	public static boolean modifyFilesBatch(String parent) {
		if (StrUtils.isBlank(parent)) {
			return false;
		}
		File parentFile = new File(parent);
		if (parentFile.exists()) {
			File[] listFiles = parentFile.listFiles();
			if (listFiles != null && listFiles.length > 0) {
				for (File childFile : listFiles) {
					if (childFile.isFile()) {
						String fileName = childFile.getParent() + File.separator
								+ childFile.getName().replace("[YYDM-11FANS][Gundam_Seed-Destiny-HD-ReMaster]", "")
										.replace("[BDRIP][X264-10bit_AAC][720P]", "");
						childFile.renameTo(new File(fileName));
					}
				}
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * 将一个文件复制到另一个文件,若是目的文件存在,会将原来的内容覆盖
	 */
	public static void copyFile(File src, File des) {
		BufferedReader br = null;
		BufferedWriter bw = null;
		try {
			br = new BufferedReader(new FileReader(src));
			bw = new BufferedWriter(new FileWriter(des, true));
			String line = null;
			while ((line = br.readLine()) != null) {
				bw.write(line);
				bw.newLine();
				bw.flush();
			}
			br.close();
			bw.close();
		} catch (Exception e) {
			logger.info(e.getMessage());
		} finally {
			if (br != null) {
				try {
					br.close();
					if (bw != null) {
						bw.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 利用流来复制文件
	 * 
	 * @param src
	 * @param des
	 */
	public static void copyFileIO(File src, File des) {
		InputStream br = null;
		OutputStream bw = null;
		try {
			br = new BufferedInputStream(new FileInputStream(src));
			bw = new BufferedOutputStream(new FileOutputStream(des, true));
			int len = 0;
			byte[] b = new byte[2048];
			while ((len = br.read()) != -1) {
				bw.write(b, 0, len);
				bw.flush();
			}
			br.close();
			bw.close();
		} catch (Exception e) {
			logger.info(e.getMessage());
		} finally {
			close(bw, br);
		}
	}

	/**
	 * 创建文件夹,多层目录
	 */
	public static boolean mkdirs(String path) {
		try {
			if (StrUtils.isBlank(path)) {
				throw new Exception("文件路径为空");
			}
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		return mkdirs(new File(path));
	}

	/**
	 * 创建文件夹,多层目录
	 * 
	 * @param file
	 */
	public static boolean mkdirs(File file) {
		try {
			if (file == null) {
				throw new Exception("文件目录为空");
			}
			if (!file.exists() && !file.isDirectory()) {
				return file.mkdirs();
			}
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		return false;
	}

	public static void write(String filePath, String content) {
		write(new File(filePath), content);
	}

	/**
	 * 将字符串写入到文件中
	 */
	public static void write(File file, String content) {
		byte[] bytes = content.getBytes(Charset.defaultCharset());
		BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(file, true));
			bos.write(bytes);
		} catch (Exception e) {
			logger.info(e.getMessage());
		} finally {
			close(bos);
		}
	}

	/**
	 * 往文件中写入数据
	 */
	public static void write(File file, InputStream is) {
		if (is != null) {
			BufferedOutputStream bos = null;
			byte[] bs = new byte[1024];
			int len = 0;
			try {
				bos = new BufferedOutputStream(new FileOutputStream(file, true));
				while ((len = is.read(bs)) != -1) {
					bos.write(bs, 0, len);
				}
			} catch (Exception e) {
				logger.info(e.getMessage());
			} finally {
				close(bos);
			}
		}
	}

	public static void write(OutputStream os) {
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
		try {

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(bw);
		}
	}

	/**
	 * 读取输入流里的字节,返回一个字符串,流未关闭,需要调用函数自行关闭
	 */
	public static String readIO(InputStream is) {
		try {
			StringBuilder sb = new StringBuilder();
			byte[] b = new byte[1024];
			int i = 0;
			while ((i = is.read(b)) != -1) {
				sb.append(new String(b, 0, i));
			}
			return sb.toString();
		} catch (Exception e) {
			logger.info(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public static void readFile(String pathName) {
		FileReader fr = null;
		try {
			fr = new FileReader(pathName);
			char[] c = new char[1024];
			int num = 0;
			while ((num = fr.read(c)) != -1) {
				System.out.println(new String(c, 0, num));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(fr);
		}
	}

	/**
	 * 利用缓冲流读取文件中的数据
	 */
	public static String readBuffer(String path) {
		return readBuffer(new File(path));
	}

	public static String readBuffer(File file) {
		BufferedReader br = null;
		StringBuffer sb = new StringBuffer();
		try {
			br = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			return sb.toString();
		} catch (Exception e) {
			logger.info(e.getMessage());
		} finally {
			close(br);
		}
		return null;
	}

	/**
	 * 将字节流转换为字符读取
	 */
	public static String readInputStr(InputStream is) {
		BufferedReader br = null;
		String line = null;
		StringBuilder sb = new StringBuilder();
		try {
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close(br);
		}
		return null;
	}

	/**
	 * url去重
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		BufferedReader br = null;
		OutputStreamWriter osr = null;
		try {
			File src = new File("D:\\Java\\responsity\\javas\\Utils\\src\\com\\wy\\utils\\kukudas1530372519755.txt");
			InputStreamReader isr = new InputStreamReader(new FileInputStream(src));
			br = new BufferedReader(isr);
			String line = null;
			Set<String> urls = new HashSet<>();
			while ((line = br.readLine()) != null) {
				urls.add(line);
			}
			osr = new OutputStreamWriter(new FileOutputStream("D:\\Java\\test.txt"));
			for (String url : urls) {
				osr.write(url + "\r\n");
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			close(br, osr);
		}
	}
	
	/**
	 * 递归删除文件夹以及文件夹中内容
	 */
	public static void deleteFile(File file) {
		if(!file.exists()) {
			return;
		}
		if(file.isDirectory()) {
			File[] listFiles = file.listFiles();
			if(listFiles.length == 0) {
				file.delete();
				return;
			}
			for (File fi : listFiles) {
				deleteFile(fi);
			}
		}else {
			file.delete();
		}
	}

	/**
	 * 递归删除文件夹中的某类型文件
	 */
	public static void deleteFile(String filePath, String fileType) {
		File file = new File(filePath);
		if(file.exists() && file.isDirectory()) {
			File[] listFiles = file.listFiles();
			for (File fi : listFiles) {
				if (fi.isDirectory()) {
					deleteFile(fi.getAbsolutePath(), fileType);
				} else {
					if (fi.getName().endsWith(fileType)) {
						fi.delete();
					}
				}
			}
		}
	}

	/**
	 * 关闭io流
	 */
	public static void close(Closeable... closeables) {
		if (closeables.length > 0) {
			for (Closeable closeable : closeables) {
				if (closeable != null) {
					try {
						closeable.close();
					} catch (Exception e) {
						logger.error(e.getMessage(), e.getCause());
					}
				}
			}
		}
	}
}