package com.wy.common;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Formatter;
import java.util.Locale;

import com.wy.enums.SysEnum;

/**
 * 获得系统和网络的一些信息
 *
 * @author ParadiseWY
 * @date 2018-02-26 20:39:28
 * @git {@link https://github.com/mygodness100}
 */
public interface SysConfig {

	/**
	 * 获得系统参数
	 */
	public static String getSysConfig(SysEnum sysEnum) {
		return System.getProperty(sysEnum.toString());
	}

	/**
	 * 获得本地服务器的主机名
	 * 
	 * @return 返回主机名,若获取失败,返回localhost
	 */
	public static String getLocalHost() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return "localhost";
	}

	/**
	 * 获得本地服务器的ip,可能会有多个地址
	 * 
	 * @return 本地内网ip,非外网ip,若失败,返回127.0.01
	 */
	public static String getLocalIp() {
		try {
			InetAddress[] allByName = InetAddress.getAllByName(InetAddress.getLocalHost().getHostName());
			for (InetAddress inetAddress : allByName) {
				// 主机名都是一样的
				System.out.println(inetAddress.getHostName());
				// 有多少个ip就会返回多少次,包括ip4和ip6
				System.out.println(inetAddress.getHostAddress());
			}
			// 该方法默认只返回第一个ip
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return "127.0.0.1";
	}

	/**
	 * 获得本地服务器mac地址,获取失败返回null
	 * 
	 * @return mac地址,失败返回null
	 */
	public static String getLocalMac() {
		try (Formatter formatter = new Formatter();) {
			NetworkInterface ni = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
			byte[] mac = ni.getHardwareAddress();
			for (int i = 0; i < mac.length; i++) {
				formatter.format(Locale.getDefault(), "%02X%s", mac[i], (i < mac.length - 1) ? "-" : "").toString();
			}
		} catch (UnknownHostException | SocketException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 返回进程pid
	 * 
	 * @return 当前进程pid字符串
	 */
	public static String getPid() {
		String info = ManagementFactory.getRuntimeMXBean().getName();
		return info.split("@")[0];
	}
}