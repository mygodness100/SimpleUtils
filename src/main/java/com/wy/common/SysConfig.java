package com.wy.common;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Formatter;
import java.util.Locale;

import com.wy.enums.SysEnum;

/**
 * 获得系统的一些信息
 * @author wanyang 2018年2月26日 FIXME
 */
public class SysConfig {

	/**
	 * 获得系统参数
	 */
	public static String getSysConfig(SysEnum sysEnum) {
		return System.getProperty(sysEnum.toString());
	}

	/**
	 *  得到计算机的ip地址和mac地址
	 */
	public static String[] getLocalIpMac() {
		String[] res = new String[3];
		try {
			InetAddress address = InetAddress.getLocalHost();
			res[0] = address.getHostAddress();
			res[1] = address.getHostName();
			NetworkInterface ni = NetworkInterface.getByInetAddress(address);
			byte[] mac = ni.getHardwareAddress();
			Formatter formatter = new Formatter();
			for (int i = 0; i < mac.length; i++) {
				res[2] = formatter.format(Locale.getDefault(), "%02X%s", mac[i],
						(i < mac.length - 1) ? "-" : "").toString();
			}
			formatter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
}