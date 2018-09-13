package com.wy.socket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.wy.common.Encoding;

public class MyServer {
	private ServerSocket server;

	public MyServer(){
		try {
			//在本地上新开一个9999端口号的服务
			server = new ServerSocket(9999);
			//等待某个客户端来链接,返回一个socket链接
			Socket accept = server.accept();
			//读取accept中传递的数据
			BufferedReader br = new BufferedReader(
					new InputStreamReader(accept.getInputStream(), Encoding.UTF8));
			String info = br.readLine();
			System.out.println(info);
			//向客户端发送数据
			PrintWriter pw = new PrintWriter(accept.getOutputStream(),true);
			pw.println("收到登录消息了");
		} catch (Exception e) {
			e.getMessage();
		}
	}
}
