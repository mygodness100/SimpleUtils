package com.wy.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author 万杨 Administrator 2017年6月13日 下午2:13:11 TODO
 */
public class ThreadTest extends Thread {
//	创建固定线程数的线程池
	private static ExecutorService fixPool = Executors.newFixedThreadPool(20);
//	创建一个可缓存的线程池,线程数不固定,有空闲的则用,没有则创建,空间超过60秒将被移除
	private static ExecutorService cachePool = Executors.newCachedThreadPool();
//	创建一个单线程
	private static ExecutorService singlePool = Executors.newSingleThreadExecutor();
//	创建一个定时类的线程,多数情况下可代替timer类
	private static ExecutorService schedulePool = Executors.newScheduledThreadPool(20);
	
	private static int T = 0;
	
	public void run(){
		System.out.println(Thread.currentThread().getName() + T++);
	}
	
	public static void main(String[] args) {
		ThreadTest t1 = new ThreadTest();
		Thread t2 = new Thread(t1);
		t1.start();
		t2.start();
		fixPool.execute(t1);
		cachePool.execute(t2);
		singlePool.execute(t2);
		schedulePool.execute(t1);
	}
}

class MyRunnable implements Runnable {

	public void run() {
		System.out.println(Thread.currentThread().getName());
	}
}
