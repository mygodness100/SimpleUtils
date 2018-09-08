package com.wy.crypto;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.wy.utils.HexUtils;
import com.wy.utils.StrUtils;

import sun.misc.BASE64Encoder;

@SuppressWarnings("restriction")
public class CryptoUtils {
	/**
	 * 按照UUID算法生成一个字符串
	 */
	public static String UUID(){
		return UUID(true);
	}
	
	/**
	 * 按照UUID算法生成一个字符串,是否做处理,true是
	 */
	public static String UUID(boolean flag){
		return flag ? UUID.randomUUID().toString().replaceAll("-", "") : UUID.randomUUID().toString();
	}
	
	/**
	 * 将传入的消息只进行md5加密,不进行其他编码操作
	 */
	public static String MD5(String message){
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] input = message.getBytes();
			byte[] output = md.digest(input);//将字节信息加密
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
	public static String MD5(String message,boolean flag){
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] input = message.getBytes();
			byte[] output = md.digest(input);//将字节信息加密
			//利用base64将转码后的字节信息转成字符串
			return flag ?  new BASE64Encoder().encode(output) : HexUtils.bytes2HexStr(output);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * aes加密,若使用des加密,可将密钥生成器的随机源改为56
	 * @param encodeRules	加密规则
	 * @param content		加密内容
	 * @return					返回一个16进制字符串
	 */
	public static String AESEncrypt(String encodeRules,String content) {
		if(StrUtils.isBlank(encodeRules,content)){
			return "加密参数不能为空";
		}
		try {
			//1.构造密钥生成器，指定为AES算法,不区分大小写
			KeyGenerator keygen = KeyGenerator.getInstance("AES");
			//2.根据ecnodeRules规则初始化密钥生成器
            //生成一个128位的随机源,根据传入的字节数组
            keygen.init(128, new SecureRandom(encodeRules.getBytes()));
            //3.产生原始对称密钥
			SecretKey original_key = keygen.generateKey();
			//4.获得原始对称密钥的字节数组
			byte [] raw=original_key.getEncoded();
			//5.根据字节数组生成AES密钥
            SecretKey key=new SecretKeySpec(raw, "AES");
            //6.根据指定算法AES生成密码器
			Cipher cip = Cipher.getInstance("AES");
			//7.初始化密码器，第一个参数为加密(Encrypt_mode)或者解密解密(Decrypt_mode)操作，第二个参数为使用的KEY
			cip.init(Cipher.ENCRYPT_MODE, key);
			//8.获取加密内容的字节数组(这里要设置为utf-8)不然内容中如果有中文和英文混合中文就会解密为乱码
            byte [] byte_encode=content.getBytes("utf-8");
            //9.根据密码器的初始化方式--加密：将数据加密
            byte [] byte_AES=cip.doFinal(byte_encode);
            //若用base64加密会在前端传输中少字符数
//            return new String(new BASE64Encoder().encode(byte_AES));
            //将字节转成16进制字符串
            return HexUtils.bytes2HexStr(byte_AES);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * AES解密
	 * @param encodeRules	解密规则
	 * @param content		需解密16进制字符串
	 */
	public static String AESDecrypt(String encodeRules,String content) {
		if(StrUtils.isBlank(encodeRules,content)){
			return "加密参数不能为空";
		}
		//若是将此解密方法放到linux机器报错,则使用以下方法生成随机源
//		SecretKeySpec key2 = null;
//		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
//		random.setSeed(encodeRules.getBytes());
//		keygen.init(128, random);
		try {
            KeyGenerator keygen=KeyGenerator.getInstance("AES");
            keygen.init(128, new SecureRandom(encodeRules.getBytes()));
            SecretKey original_key=keygen.generateKey();
            byte [] raw=original_key.getEncoded();
            SecretKey key=new SecretKeySpec(raw, "AES");
            Cipher cipher=Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            //将16进制字符串转成2进制数组
            byte[] byte_content = HexUtils.hexStr2Bytes(content);
            //解密
            byte [] byte_decode=cipher.doFinal(byte_content);
            String AES_decode=new String(byte_decode,"utf-8");
            return AES_decode;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
