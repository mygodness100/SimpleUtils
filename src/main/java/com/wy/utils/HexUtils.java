package com.wy.utils;

public class HexUtils {
	private static final char[] HEX = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
	
	/**
	 * 字节数组转16进制字符串
	 */
	public static String bytes2HexStr(byte[] bytes) {
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<bytes.length;i++) {
			byte bt = bytes[i];
			sb.append(HEX[(bt & 0x0f0) >> 4]);
			sb.append(HEX[bt & 0x0f]);
		}
		return sb.toString();
	}
	
	/**
     * 16进制转换为字节数组
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
}
