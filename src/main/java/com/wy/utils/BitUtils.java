package com.wy.utils;

public class BitUtils {

	private static final char[] HEX = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E',
			'F' };

	/**
	 * 将 2 进制转换成 16 进制字符串
	 */
	public static String byte2Hex(byte buf[]) {
		StringBuilder sb = new StringBuilder();
		for (byte aBuf : buf) {
			String hex = Integer.toHexString(aBuf & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex.toUpperCase());
		}
		return sb.toString();
	}

	/**
	 * 将2进制字节数组转16进制字符串,和上面一个算法不一样,但是结果是一样的
	 */
	public static String bytes2HexStr(byte[] bytes) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			byte bt = bytes[i];
			sb.append(HEX[(bt & 0x0f0) >> 4]);
			sb.append(HEX[bt & 0x0f]);
		}
		return sb.toString();
	}

	/**
	 * 16进制转换为2进制字节数组
	 */
	public static byte[] hexStr2Bytes(String hexStr) {
		if (hexStr.length() < 1) {
			return null;
		}
		byte[] result = new byte[hexStr.length() / 2];
		for (int i = 0; i < hexStr.length() / 2; i++) {
			int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
			result[i] = (byte) (high * 16 + low);
		}
		return result;
	}

	public static byte[] int2Bytes(int num) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte) num;
		bytes[1] = (byte) (num >> 8);
		bytes[2] = (byte) (num >> 16);
		bytes[3] = (byte) (num >> 24);
		return bytes;
	}

	public static int bytes2Int(byte[] bytes) {
		int b0 = bytes[0] & 0xFF;
		int b1 = (bytes[1] & 0xFF) << 8;
		int b2 = (bytes[2] & 0xFF) << (2 * 8);
		int b3 = (bytes[3] & 0xFF) << (3 * 8);
		return b0 | b1 | b2 | b3;
	}
}