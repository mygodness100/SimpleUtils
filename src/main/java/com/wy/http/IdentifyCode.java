package com.wy.http;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class IdentifyCode extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	public void service(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		// 创建图片 -- 在内存中
		int width = 80;
		int height = 40;
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		// 创建图层，获得画板
		Graphics g = image.getGraphics();
		// 确定画笔颜色
		g.setColor(Color.BLACK);
		// 填充一个矩形
		g.fillRect(0, 0, width, height);
		// 只需要一个边框
		// 设置颜色
		g.setColor(Color.WHITE);
		// 填充一个矩形
		g.fillRect(1, 1, width - 2, height - 2);
		// 填充字符
		String data = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		// 设置字体
		g.setFont(new Font("宋体", Font.BOLD, 30));
		// 缓存随机生成的字符
		StringBuffer buf = new StringBuffer();
		// 随机获得4个字符
		Random random = new Random();
		for (int i = 0; i < 4; i++) {
			// 设置随机颜色
			g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
			// 获得一个随机字符
			int index = random.nextInt(62);
			// 截取字符串
			String str = data.substring(index, index + 1);
			// 需要将随机的字符，写到图片中
			g.drawString(str, 20 * i, 30);
			// 缓存
			buf.append(str);
		}
		// 获得session
		HttpSession session = req.getSession();
		// 保存值
		session.setAttribute("number", buf.toString());
		// 验证的时候由于同一个sessionid只会从同一个session中取,直接取保存在session中的值即可,为了不重复多次输入验证码,需要在每次验证验证码之后将其删除
		// 登录或其他需要的地方,获得用户提交的数据 imageNumber
		// String imageNumber = request.getParameter("imageNumber");
		// //需要判断 从session获得保存的验证码的信息
		// // * 获得session
		// HttpSession session = request.getSession();
		// // * 获得保存的值number
		// String number = (String)session.getAttribute("number");
		//
		// PrintWriter out = response.getWriter();
		//
		// //匹配 用户提交的数据与程序保存的数据
		// if(number != null){ //程序保存
		// if(number.equalsIgnoreCase(imageNumber)){
		// //输入正确
		// out.print("验证通过");
		// } else {
		// //验证码错误
		// out.print("验证码错误");
		// }
		// //无论情况，程序存储的数据，只能使用一次
		// session.removeAttribute("number");
		// } else {
		// out.print("验证码失效");
		// }
		// 干扰线
		for (int i = 0; i < 10; i++) {
			// 设置随机颜色
			g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
			// 随机画直线
			g.drawLine(random.nextInt(width), random.nextInt(height), random.nextInt(width),
					random.nextInt(height));
		}
		// 通知浏览器发送的数据时一张图片
		res.setContentType("image/jpeg");
		// 将图片发送给浏览器
		ImageIO.write(image, "jpg", res.getOutputStream());
	}

}
