package com.wy.qrcode;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.wy.crypto.CryptoUtils;
import com.wy.io.FileUtils;
import com.wy.utils.StrUtils;

/**
 * 二维码生成 利用google的zxing.jar包生成,
 * jar包下载地址:https://oss.sonatype.org/content/repositories/releases/com/google/zxing/core/3.2.1/
 * githup地址:https://github.com/zxing/zxing/tree/zxing-3.0.0
 */
public class QREncode {

	private static final int WHITE = 0xFF000000;
	private static final int BLACK = 0xFFFFFFFF;
	private static final String IMG_SUFFIX = "JPG";// 二维码图片后缀
	private static final String IMG_DES = "%s.jpg";// 生成的二维码图片名字
	private static final int QRCODE_SIZE = 300; // 二维码尺寸
	private static final int WIDTH = 60; // logo宽度
	private static final int HEIGHT = 60; // logo高度

	/**
	 * 生成二维码
	 * 
	 * @param content:二维码内容
	 * @param desPath:生成的二维码存放地址
	 */
	public static void encode(String content, String desPath) {
		encode(content, desPath, false);
	}

	public static void encode(String content, String desPath, boolean isCompress) {
		encode(content, desPath, null, isCompress);
	}

	public static void encode(String content, String desPath, String imgPath) {
		encode(content, desPath, imgPath, false);
	}

	/**
	 * 生成二维码
	 * 
	 * @param content:二维码内容
	 * @param desPath:二维码图片生成地址
	 * @param imgPath:需要插入到二维码图片中的图片地址
	 * @param isCompress:是否需要压缩,true需要
	 */
	public static void encode(String content, String desPath, String imgPath, boolean isCompress) {
		try {
			BufferedImage image = createImage(content, imgPath, isCompress);
			FileUtils.mkdirs(desPath);
			ImageIO.write(image, IMG_SUFFIX,
					new File(desPath + File.separator + String.format(IMG_DES, CryptoUtils.UUID())));
		} catch (Exception e) {
			e.getMessage();
		}
	}

	public static void encodeIO(String content, OutputStream output) {
		encodeIO(content, output, false);
	}

	public static void encodeIO(String content, OutputStream output, boolean isCompress) {
		encodeIO(content, null, output, isCompress);
	}

	public static void encodeIO(String content, String imgPath, OutputStream output) {
		encodeIO(content, imgPath, output, false);
	}

	public static void encodeIO(String content, String imgPath, OutputStream output, boolean isCompress) {
		try {
			BufferedImage image = createImage(content, imgPath, isCompress);
			ImageIO.write(image, IMG_SUFFIX, output);
		} catch (Exception e) {
			e.getMessage();
		}
	}

	/**
	 * 生成二维码图片的流
	 */
	public static BufferedImage createImage(String content, String imgPath, boolean isCompress) {
		try {
			Map<EncodeHintType, Object> map = new HashMap<>();
			map.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
			// 设置字符集
			map.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.toString());
			// 设置边框
			map.put(EncodeHintType.MARGIN, 1);
			BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, QRCODE_SIZE,
					QRCODE_SIZE, map);
			int width = bitMatrix.getWidth();
			int height = bitMatrix.getHeight();
			BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					bi.setRGB(x, y, bitMatrix.get(x, y) ? WHITE : BLACK);
				}
			}
			// 是否插入图片插入图片
			if (StrUtils.isBlank(imgPath)) {
				return bi;
			} else {
				insertImage(bi, imgPath, isCompress);
			}
			return bi;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 在生成的二维码上插入图片
	 */
	public static void insertImage(BufferedImage bi, String imgPath, boolean isCompress) {
		try {
			File file = new File(imgPath);
			if (!file.exists()) {
				throw new Exception(imgPath + "该文件不存在");
			}
			Image src = ImageIO.read(file);
			int width = src.getWidth(null);
			int height = src.getHeight(null);
			if (isCompress) {
				if (width > WIDTH) {
					width = WIDTH;
				}
				if (height > HEIGHT) {
					height = HEIGHT;
				}
				Image image = src.getScaledInstance(width, height, Image.SCALE_SMOOTH);
				BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				Graphics g = tag.getGraphics();
				g.drawImage(image, 0, 0, null); // 绘制缩小后的图
				g.dispose();
				src = image;
			}
			// 插入LOGO
			Graphics2D graph = bi.createGraphics();
			int x = (QRCODE_SIZE - width) / 2;
			int y = (QRCODE_SIZE - height) / 2;
			graph.drawImage(src, x, y, width, height, null);
			Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);
			graph.setStroke(new BasicStroke(3f));
			graph.draw(shape);
			graph.dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String decode(String path) {
		return decode(new File(path));
	}

	public static String decode(File file) {
		try {
			BufferedImage image = ImageIO.read(file);
			if (image == null) {
				return null;
			}
			QRDecode source = new QRDecode(image);
			BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
			Map<DecodeHintType, String> hints = new HashMap<>();
			hints.put(DecodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.toString());
			Result result = new MultiFormatReader().decode(bitmap, hints);
			return result.getText();
		} catch (Exception e) {
			e.getMessage();
		}
		return null;
	}
}
