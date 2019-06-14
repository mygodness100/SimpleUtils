package com.wy.crypto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.wy.result.ResultException;
import com.wy.utils.HexUtils;
import com.wy.utils.StrUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CryptoUtils {
	/**
	 * 按照UUID算法生成一个字符串
	 */
	public static String UUID() {
		return UUID(true);
	}

	/**
	 * 按照UUID算法生成一个字符串,是否做处理,true是
	 */
	public static String UUID(boolean flag) {
		return flag ? UUID.randomUUID().toString().replaceAll("-", "")
				: UUID.randomUUID().toString();
	}

	/**
	 * 将传入的消息只进行md5加密,不进行其他编码操作
	 */
	public static String MD5(String message) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] input = message.getBytes();
			byte[] output = md.digest(input);// 将字节信息加密
			return new String(output);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 将传入的消息进行md5加密
	 * 不可逆,任意长度变等长,flag为true返回base64编码后的加密串,false返回16进制编码后的加密串
	 */
	public static String MD5(String message, boolean flag) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] input = message.getBytes();
			byte[] output = md.digest(input);// 将字节信息加密
			// 利用base64将转码后的字节信息转成字符串
			return flag ? Base64.getEncoder().encodeToString(output)
					: HexUtils.bytes2HexStr(output);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String AESSimpleEncrypt(String encodeRules, String content) {
		if (StrUtils.isBlank(encodeRules, content)) {
			throw new ResultException("加密内容或密钥不能为空");
		}
		if (encodeRules.length() % 16 != 0) {
			throw new ResultException("密钥长度必须是16的倍数位");
		}
		return HexUtils.bytes2HexStr(AES(content.getBytes(StandardCharsets.UTF_8),
				encodeRules.getBytes(StandardCharsets.UTF_8), Cipher.ENCRYPT_MODE));
	}

	public static String AESSimpleDecrypt(String encodeRules, String content) {
		if (StrUtils.isBlank(encodeRules, content)) {
			throw new ResultException("加密内容或密钥不能为空");
		}
		if (encodeRules.length() % 16 != 0) {
			throw new ResultException("密钥长度必须是16的倍数位");
		}
		return new String(AES(HexUtils.hexStr2Bytes(content),
				encodeRules.getBytes(StandardCharsets.UTF_8), Cipher.DECRYPT_MODE),
				StandardCharsets.UTF_8);
	}

	/**
	 * aes加密,若使用des加密,可将密钥生成器的随机源改为56
	 * 
	 * @param encodeRules 加密规则
	 * @param content 加密内容
	 * @return 返回一个16进制字符串
	 */
	public static String AESEncrypt(String encodeRules, String content) {
		if (StrUtils.isBlank(encodeRules, content)) {
			return "加密参数不能为空";
		}
		return HexUtils.bytes2HexStr(aesCrypto(encodeRules,
				content.getBytes(StandardCharsets.UTF_8), Cipher.ENCRYPT_MODE));
	}

	/**
	 * AES解密
	 * 
	 * @param encodeRules 解密规则
	 * @param content 需解密16进制字符串
	 */
	public static String AESDecrypt(String encodeRules, String content) {
		if (StrUtils.isBlank(encodeRules, content)) {
			return "加密参数不能为空";
		}
		// 若是将此解密方法放到linux机器报错,则使用以下方法生成随机源
		// SecretKeySpec key2 = null;
		// SecureRandom random =
		// SecureRandom.getInstance("SHA1PRNG");
		// random.setSeed(encodeRules.getBytes());
		// keygen.init(128, random);
		byte[] byte_decode = aesCrypto(encodeRules, HexUtils.hexStr2Bytes(content),
				Cipher.DECRYPT_MODE);
		return new String(byte_decode, StandardCharsets.UTF_8);
	}

	private static byte[] aesCrypto(String encodeRules, byte[] content, int mode) {
		try {
			// 1.构造密钥生成器，指定为AES算法,不区分大小写
			KeyGenerator keygen = KeyGenerator.getInstance("AES");
			// 2.根据ecnodeRules规则初始化密钥生成器
			// 生成一个128位的随机源,根据传入的字节数组
			keygen.init(128, new SecureRandom(encodeRules.getBytes()));
			// 3.产生原始对称密钥
			SecretKey original_key = keygen.generateKey();
			// 4.获得原始对称密钥的字节数组
			byte[] raw = original_key.getEncoded();
			return AES(content, raw, mode);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static final byte[] AES(byte[] content, byte[] raw, int mode) {
		// 5.根据字节数组生成AES密钥
		SecretKey key = new SecretKeySpec(raw, "AES");
		try {
			// 6.根据指定算法AES生成密码器
			Cipher cip = Cipher.getInstance("AES/ECB/PKCS5Padding");
			// 7.初始化密码器,第一个参数为加密(Encrypt_mode)或解密(Decrypt_mode),第二个参数为使用的KEY
			cip.init(mode, key);
			// 不要使用base64加密,会在前端传输中少字符数
			// 9.根据密码器的初始化方式--加密/解密
			return cip.doFinal(content);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
				| IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 从字符串中加载公钥
	 * @param publicKey 公钥字符串
	 */
	public static final RSAPublicKey loadPublicKey(String publicKey) {
		byte[] buffer = Base64.getDecoder().decode(publicKey);
		try {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
			return (RSAPublicKey) keyFactory.generatePublic(keySpec);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			log.error("算法错误或公钥非法");
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 从字符串中加载私钥
	 * @param privateKeyStr 私钥字符串
	 */
	public static RSAPrivateKey loadPrivateKey(String privateKeyStr) {
		byte[] buffer = Base64.getDecoder().decode(privateKeyStr);
		try {
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			log.error("算法错误或私钥非法");
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * RSA公钥加密
	 * @param key
	 * @param xmlstr
	 *            加密内容长度受秘钥长度限制，若加密内容长度大于(秘钥长度(1024)/8-11=117),
	 *            则需要分段加密
	 */
	public static String RsaEncrypt(PublicKey key, String xmlstr) {
		byte[] plainText = xmlstr.getBytes(StandardCharsets.UTF_8);
		try (ByteArrayOutputStream out = new ByteArrayOutputStream();) {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			int inputLen = plainText.length;
			int offSet = 0;
			for (int i = 0; inputLen - offSet > 0; offSet = i * 116) {
				byte[] cache;
				if (inputLen - offSet > 116) {
					cache = cipher.doFinal(plainText, offSet, 116);
				} else {
					cache = cipher.doFinal(plainText, offSet, inputLen - offSet);
				}
				out.write(cache, 0, cache.length);
				++i;
			}
			return Base64.getEncoder().encodeToString(out.toByteArray());
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
				| IllegalBlockSizeException | BadPaddingException | IOException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * RSA私钥解密
	 * @param key 私钥
	 * @param encodedText 若是分段加密，则需要分段解密
	 */
	public static String RsaDecrypt(PrivateKey key, String encodedText) {
		// base64编码规定一行字符串不能超过76个,超过换行,换行符会导致编码失败
		encodedText = encodedText.replaceAll("\r|\n", "");
		byte[] textb = Base64.getDecoder().decode(encodedText);
		try (ByteArrayOutputStream out = new ByteArrayOutputStream();) {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, key);
			int inputLen = textb.length;
			int offSet = 0;
			for (int i = 0; inputLen - offSet > 0; offSet = i * 128) {
				byte[] cache;
				if (inputLen - offSet > 128) {
					cache = cipher.doFinal(textb, offSet, 128);
				} else {
					cache = cipher.doFinal(textb, offSet, inputLen - offSet);
				}
				out.write(cache, 0, cache.length);
				++i;
			}
			return new String(out.toByteArray(), StandardCharsets.UTF_8);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
				| IllegalBlockSizeException | BadPaddingException | IOException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
}