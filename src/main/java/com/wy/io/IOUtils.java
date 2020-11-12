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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.Pipe;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import com.wy.result.ResultException;
import com.wy.utils.StrUtils;

/**
 * 文件帮助类,未使用
 * 
 * @author 万杨
 */
public final class IOUtils {

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
			e.printStackTrace();
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
			e.printStackTrace();
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
			e.printStackTrace();
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
			e.printStackTrace();
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
			e.printStackTrace();
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
				e.printStackTrace();
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
			e.printStackTrace();
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
	// public static void main(String[] args) {
	// BufferedReader br = null;
	// OutputStreamWriter osr = null;
	// try {
	// File src = new
	// File("D:\\Java\\responsity\\javas\\Utils\\src\\com\\wy\\utils\\kukudas1530372519755.txt");
	// InputStreamReader isr = new InputStreamReader(new FileInputStream(src));
	// br = new BufferedReader(isr);
	// String line = null;
	// Set<String> urls = new HashSet<>();
	// while ((line = br.readLine()) != null) {
	// urls.add(line);
	// }
	// osr = new OutputStreamWriter(new FileOutputStream("D:\\Java\\test.txt"));
	// for (String url : urls) {
	// osr.write(url + "\r\n");
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// close(br, osr);
	// }
	// }

	/**
	 * 递归删除文件夹以及文件夹中内容
	 */
	public static void deleteFile(File file) {
		if (!file.exists()) {
			return;
		}
		if (file.isDirectory()) {
			File[] listFiles = file.listFiles();
			if (listFiles.length == 0) {
				file.delete();
				return;
			}
			for (File fi : listFiles) {
				deleteFile(fi);
			}
		} else {
			file.delete();
		}
	}

	/**
	 * 递归删除文件夹中的某类型文件
	 */
	public static void deleteFile(String filePath, String fileType) {
		File file = new File(filePath);
		if (file.exists() && file.isDirectory()) {
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
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * 解压缩
	 * 
	 * @param source 源数据,需要解压的数据
	 * @return 解压后的数据,恢复的数据
	 */
	public static byte[] unzip(byte[] source) {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream();
				ByteArrayInputStream in = new ByteArrayInputStream(source);
				GZIPInputStream zipIn = new GZIPInputStream(in);) {
			byte[] temp = new byte[1024];
			int length = 0;
			while ((length = zipIn.read(temp, 0, temp.length)) != -1) {
				out.write(temp, 0, length);
			}
			return out.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 压缩
	 * 
	 * @param source 源数据,需要压缩的数据
	 * @return 压缩后的数据
	 */
	public static byte[] zip(byte[] source) {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream();
				GZIPOutputStream zipOut = new GZIPOutputStream(out);) {
			// 将压缩信息写入到内存, 写入的过程会实现解压
			zipOut.write(source);
			zipOut.finish();
			return out.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void zipFile(String srcPath, String desPath) {
		File file = new File(desPath);
		boolean fileExists = fileExists(file);
		if (!fileExists) {
			throw new ResultException("文件不存在");
		}
		try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(file));
				InputStream is = new FileInputStream(srcPath);) {
			ZipEntry zipEntry = new ZipEntry(file.getName());
			zipEntry.setSize(is.available());
			zos.putNextEntry(zipEntry);
			int size = 0;
			while ((size = is.read()) != -1) {
				zos.write(size);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		zipFile("E:\\repository\\paradise-study-hadoop\\docs\\HBase\\emp.txt",
				"E:\\repository\\paradise-study-hadoop\\docs\\HBase\\emp.zip");
	}

	/**
	 * 使用文件channel压缩文件 FIXME
	 * 
	 * @param path 需要进行压缩的文件地址
	 */
	public static void zipChannel(String srcPath, String desPath) {
		File file = new File(srcPath);
		boolean exists = fileExists(file);
		if (!exists) {
			throw new ResultException("文件不存在");
		}
		File desFile = new File(desPath);
		boolean fileExists = fileExists(desFile);
		if (!fileExists) {
			throw new ResultException("文件不存在");
		}
		try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(desFile));
				WritableByteChannel channel = Channels.newChannel(zipOutputStream);
				FileChannel fileChannel = new FileInputStream(file).getChannel();) {
			zipOutputStream.putNextEntry(new ZipEntry("test"));
			fileChannel.transferTo(0, file.getTotalSpace(), channel);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 使用Map映射文件压缩文件 FIXME
	 */
	public static void zipMap(String srcPath, String desPath) {
		File zipFile = new File(desPath);
		File file = new File(srcPath);
		try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
				WritableByteChannel writableByteChannel = Channels.newChannel(zipOut);
				RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");) {
			zipOut.putNextEntry(new ZipEntry(".zip"));
			// 内存中的映射文件
			MappedByteBuffer mappedByteBuffer = randomAccessFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0,
					1024);
			writableByteChannel.write(mappedByteBuffer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 异步压缩文件 FIXME
	 * 
	 * @param srcPath
	 * @param desPath
	 */
	public static void zipPip(String srcPath, String desPath) {
		try (WritableByteChannel out = Channels.newChannel(new FileOutputStream(desPath))) {
			Pipe pipe = Pipe.open();
			// 异步任务
			CompletableFuture.runAsync(() -> {
				try (ZipOutputStream zos = new ZipOutputStream(Channels.newOutputStream(pipe.sink()));
						WritableByteChannel innerOut = Channels.newChannel(zos);
						FileChannel jpgChannel = new FileInputStream(new File(srcPath)).getChannel();) {
					zos.putNextEntry(new ZipEntry(".zip"));
					jpgChannel.transferTo(0, new File(srcPath).getTotalSpace(), innerOut);
					jpgChannel.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			// 获取读通道
			ReadableByteChannel readableByteChannel = pipe.source();
			ByteBuffer buffer = ByteBuffer.allocate(((int) new File(srcPath).getTotalSpace()) * 10);
			while (readableByteChannel.read(buffer) >= 0) {
				buffer.flip();
				out.write(buffer);
				buffer.clear();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 文件分块,将大文件分成多个小块
	 * 
	 * @param fileSrc 需要分块的文件地址
	 * @param chunkFolder 分块之后的文件存放目录
	 */
	public void testChunk(String fileSrc, String chunkFolder) {
		File sourceFile = new File(fileSrc);
		if (sourceFile == null || !sourceFile.exists()) {
			throw new ResultException("文件不存在");
		}
		// 块文件大小,1M
		long chunkFileSize = 1 * 1024 * 1024;
		// 块数
		long chunkFileNum = (long) Math.ceil(sourceFile.length() * 1.0 / chunkFileSize);
		try {
			RandomAccessFile raf_read = new RandomAccessFile(sourceFile, "r");
			byte[] b = new byte[1024];
			for (int i = 0; i < chunkFileNum; i++) {
				File chunkFile = new File(chunkFolder + i);
				// 创建向块文件的写对象
				RandomAccessFile raf_write = new RandomAccessFile(chunkFile, "rw");
				int len = -1;
				while ((len = raf_read.read(b)) != -1) {
					raf_write.write(b, 0, len);
					// 如果块文件的大小达到1M开始写下一块儿
					if (chunkFile.length() >= chunkFileSize) {
						break;
					}
				}
				raf_write.close();
			}
			raf_read.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 文件合并,文件必须是按顺序切割的文件列表,文件名必须是纯数字或数字是接在下划线之后,如filename_01,filename_02...
	 * 
	 * 文件名若是带下划线,则只能有一个下划线,否则同样会出问题
	 * 
	 * @param chunkFolder 块文件目录
	 * @param desFile 合成后的文件地址,是一个文件
	 */
	public void testMergeFile(String chunkFolder, String desFile) {
		File chunkFile = new File(chunkFolder);
		if (chunkFile == null || !chunkFile.exists() || !chunkFile.isDirectory()) {
			throw new ResultException("文件不存在或文件不是一个目录");
		}
		// 块文件列表
		File[] files = chunkFile.listFiles();
		// 将块文件排序,按名称升序
		List<File> fileList = Arrays.asList(files);
		Collections.sort(fileList, (file1, file2) -> {
			if (Integer.parseInt(
					file1.getName().indexOf("_") > -1 ? file1.getName().substring(file1.getName().indexOf("_") + 1)
							: file1.getName()) > Integer
									.parseInt(file2.getName().indexOf("_") > -1
											? file2.getName().substring(file2.getName().indexOf("_") + 1)
											: file2.getName())) {
				return 1;
			}
			return -1;
		});
		File mergeFile = new File(desFile);
		try (RandomAccessFile raf_write = new RandomAccessFile(mergeFile, "rw");) {
			byte[] b = new byte[1024];
			RandomAccessFile raf_read = null;
			for (File file : fileList) {
				raf_read = new RandomAccessFile(file, "r");
				int len = -1;
				while ((len = raf_read.read(b)) != -1) {
					raf_write.write(b, 0, len);
				}
				if (raf_read != null) {
					raf_read.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}