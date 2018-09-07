package com.wy.socket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.wy.common.Encoding;

public class MySocket {
	private Socket socket;

	public  MySocket() {
		try {
			socket = new Socket("127.0.0.1",9999);
			//链接成功,通过pw写数据,true表示即时刷新
			PrintWriter pw = new PrintWriter(socket.getOutputStream(),true);
			pw.println("登录了吧");
			//接收从服务端返回的信息
			BufferedReader br = new BufferedReader(
					new InputStreamReader(socket.getInputStream(), Encoding.UTF8));
			String info = br.readLine();
			System.out.println(info);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
