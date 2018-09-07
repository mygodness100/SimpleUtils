package com.wy.utils;

/**
 * 这样写接口的好处是当实现类不想实现某些方法时,可以不实现
 * 若是全部写成print这样的,必须实现,不实现的写空方法
 * @author wanyang 2018年2月25日
 */
public interface Ex {
	void print();
	
	interface Ex1 extends Ex{
		void print1();
	}
}
