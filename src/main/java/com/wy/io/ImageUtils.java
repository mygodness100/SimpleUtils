package com.wy.io;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

import javax.imageio.ImageIO;

import com.itextpdf.text.Document;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

public class ImageUtils {

	// 标准A4的宽
	private static final Integer A4_WEIGHT = 595 - 60;

	// 标准A4的高
	private static final Integer A4_HEIGHT = 842 - 60;

	/**
	 * 将图片文件转成base64
	 */
	public static String getBase64Image(String path) {
		byte[] b = null;
		try (InputStream is = new FileInputStream(path);) {
			b = new byte[is.available()];
			is.read(b);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Base64.getEncoder().encodeToString(b);
	}

	/**
	 * 对字节数组字符串进行Base64解码并生成图片
	 */
	public static boolean generateImage(String imgStr, String imgFilePath) {
		if (imgStr == null) {
			return false;
		}
		try (OutputStream out = new FileOutputStream(imgFilePath);) {
			// Base64解码
			byte[] bytes = Base64.getDecoder().decode(imgStr);
			for (int i = 0; i < bytes.length; ++i) {
				// 调整异常数据
				if (bytes[i] < 0) {
					bytes[i] += 256;
				}
			}
			out.write(bytes);
			out.flush();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * 将图片转成pdf,需要用到itextpdf.jar
	 * 
	 * @param filePath 当前文件路径
	 * @param fileName 文件名称
	 */
	public static final void img2Pdf(String filePath, String fileName) {
		File pdfFile = null;
		Document document = null;
		try {
			String pdfPath = filePath + File.separator + fileName + ".pdf";
			pdfFile = new File(pdfPath);
			if (pdfFile.exists()) {
				return;
			}
			// 第一步:创建一个document对象。
			document = new Document();
			document.setMargins(0, 0, 0, 0);
			// 第二步:创建一个PdfWriter实例
			PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
			// 第三步:打开文档。
			document.open();
			// 第四步:在文档中增加图片。
			File files = new File(filePath);
			String[] images = files.list();
			Arrays.sort(images);
			int len = images.length;
			for (int i = 0; i < len; i++) {
				if (images[i].toLowerCase().endsWith(".bmp") || images[i].toLowerCase().endsWith(".jpg")
						|| images[i].toLowerCase().endsWith(".jpeg") || images[i].toLowerCase().endsWith(".gif")
						|| images[i].toLowerCase().endsWith(".png")) {
					com.itextpdf.text.Image img = com.itextpdf.text.Image
							.getInstance(filePath + File.separator + images[i]);
					img.setAlignment(com.itextpdf.text.Image.ALIGN_CENTER);
					// 根据图片大小设置页面,一定要先设置页面,再newPage(),否则无效
					if (img.getWidth() > 1440) {
						document.setPageSize(new Rectangle(A4_WEIGHT, A4_HEIGHT));
					} else {
						document.setPageSize(new Rectangle(img.getWidth(), img.getHeight()));
					}
					document.newPage();
					document.add(img);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 第五步：关闭文档
			if (document != null) {
				document.close();
			}
		}
	}

	/**
	 * @param waterFile 水印文件路径,可是图片,文字
	 * @param srcImg 需要进行水印处理的图片
	 * @param desImg 处理完成后生成的图片地址
	 * @param x 水印在目标图片的坐标x
	 * @param y 水印在目标图片的坐标y
	 * @return 处理后图片
	 */
	public static boolean waterMark(String waterFile, String srcImg, String desImg, int x, int y) {
		FileOutputStream fos = null;
		String lowerImage = waterFile.toLowerCase();
		if (lowerImage.endsWith(".png") || lowerImage.endsWith(".jpg") || lowerImage.endsWith(".jpeg")) {
			try {
				// 转换成图片对象
				Image image = ImageIO.read(new File(waterFile));
				// 获得图片对象的宽高
				int width = image.getWidth(null);
				int height = image.getHeight(null);
				// 加载目标文件
				File srcFile = new File(srcImg);
				Image target = ImageIO.read(srcFile);
				int targetWidth = target.getWidth(null);
				int targetHeight = target.getHeight(null);

				// 创建一块画板,画板的宽高,三原色
				BufferedImage bi = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
				// 创建画笔
				Graphics2D g2d = bi.createGraphics();
				// 绘制底图,底图,从x,y坐标是开始画,宽高
				g2d.drawImage(target, 0, 0, targetWidth, targetHeight, null);
				// 绘制水印
				g2d.drawImage(image, x, y, width, height, null);
				// 结束绘制
				g2d.dispose();

				// 输出新图片
				fos = new FileOutputStream(desImg);
				// 将生成的图片缓冲流放入到文件输出流中
				ImageIO.write(bi, "jpeg", fos);
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				IOUtils.close(fos);
			}
		} else {
			return false;
		}
		return false;
	}

	/**
	 * 生成验证码
	 * 
	 * @param os 验证码生成时输出的流
	 * @param width 验证码生成的宽度,默认120px
	 * @param height 验证码生成的高度,默认35px
	 * @param length 验证码的长度,默认4位
	 * @return 生成的验证码,若返回null表示验证码生成失败
	 */
	public static String obtainVerifyImage(OutputStream os, int width, int height, int length) {
		width = width <= 0 ? 120 : width;
		height = height <= 0 ? 35 : height;
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics g = image.getGraphics();
		Random random = new Random();
		g.setColor(getRandColor(200, 250));
		g.fillRect(0, 0, width, height);
		g.setFont(new Font("Times New Roman", Font.ITALIC, 20));
		g.setColor(getRandColor(160, 200));
		for (int i = 0; i < 155; i++) {
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			int xl = random.nextInt(12);
			int yl = random.nextInt(12);
			g.drawLine(x, y, x + xl, y + yl);
		}
		String code = "";
		length = length <= 0 ? 4 : length;
		for (int i = 0; i < length; i++) {
			String rand = String.valueOf(random.nextInt(10));
			code += rand;
			g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
			g.drawString(rand, width * i / length + 6, height * 3/ 4);
		}
		g.dispose();
		try {
			ImageIO.write(image, "jpeg", os);
			return code;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 生成随机背景条纹
	 * 
	 * @param fc
	 * @param bc
	 * @return
	 */
	private static Color getRandColor(int fc, int bc) {
		Random random = new Random();
		if (fc > 255) {
			fc = 255;
		}
		if (bc > 255) {
			bc = 255;
		}
		int r = fc + random.nextInt(bc - fc);
		int g = fc + random.nextInt(bc - fc);
		int b = fc + random.nextInt(bc - fc);
		return new Color(r, g, b);
	}
}