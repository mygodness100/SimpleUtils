package com.wy.test;

import java.awt.Dimension;
import java.awt.Panel;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JFrame;


/**
* @author 万杨
* Administrator 2017年6月13日 下午3:38:33
* TODO
*/
public class SwingTest {
	private static final int WIDTH = 300;
	private static final int HEIGHT = 500;
	
	public static void main(String[] args) {
		//创建一个顶层框架类
		JFrame jf = new JFrame();
		//设置框架的大小
		jf.setSize(WIDTH, HEIGHT);
		//设置关闭
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setTitle("只是一个测试");
		//获得toolkit工具对象
		Toolkit tk = Toolkit.getDefaultToolkit();
		//获得屏幕的尺寸
		Dimension screentSize = tk.getScreenSize();
		int width = screentSize.width;
		int height = screentSize.height;
		int x = (width - WIDTH )/2;
		int y = (height - HEIGHT) /2;
		//设置显示位置
		jf.setLocation(x, y);
		JButton b1 = new JButton("确定");
		JButton b2 = new JButton("取消");
		Panel p1 = new Panel();
		p1.add(b1);
		p1.add(b2);
		jf.add(p1);
		//设置可见
		jf.setVisible(true);
	}
}
