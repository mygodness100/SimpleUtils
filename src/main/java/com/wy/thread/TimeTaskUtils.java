package com.wy.thread;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TimeTaskUtils{
//	private static ExecutorService FIXPOOL = Executors.newFixedThreadPool(20);		//创建一个固定数量的线程池
//	private static ExecutorService CACHEPOOL = Executors.newCachedThreadPool();	//创建一个可缓存的线程池
	private static ScheduledThreadPoolExecutor taskScheduler = new ScheduledThreadPoolExecutor(getBestPoolSize());	//创建一个定时任务的线程池

//	public static void fixTask(Runnable r) {
//		FIXPOOL.execute(r);
//	}
//	
//	public static void cacheTask(Runnable r) {
//		CACHEPOOL.execute(r);
//	}
	
	/**
	 * 立即执行定时类线程任务
	 * @param t			任务,runnable
	 * @param num		线程池数量
	 * @param period	时间间隔
	 * @param unit		时间间隔单位
	 */
	public static void timeTask(Runnable r,long period, TimeUnit unit) {
		timeTask(r,0, period, unit);
	}
	
	/**
	 * 定时任务类线程池
	 * @param t					runnable,任务
	 * @param num				线程池数量
	 * @param initialDelay	首次执行延迟时间
	 * @param period			时间间隔
	 * @param unit				时间间隔单位
	 */
	public static void timeTask(Runnable r,long initialDelay,long period, TimeUnit unit) {
		taskScheduler.scheduleAtFixedRate(r, initialDelay, period, unit);
	}
	
	/**
	 * 单个定时任务执行
	 * @param t					Runnable,要执行的任务
	 * @param initialDelay	首次执行定时任务延迟时间
	 * @param period			执行间隔
	 * @param unit				执行间隔单位
	 */
	public static void intervalTask(Thread t,long initialDelay,long period,TimeUnit unit) {
		final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		service.scheduleAtFixedRate(t, initialDelay, period, unit); 
	}
	
	/**
     * 根据 Java 虚拟机可用处理器数目返回最佳的线程数。<br>
     * 最佳的线程数 = CPU可用核心数 / (1 - 阻塞系数)，其中阻塞系数这里设为0.9
     */
    private static int getBestPoolSize() {
        try {
            // JVM可用处理器的个数
            final int cores = Runtime.getRuntime().availableProcessors();
            // 最佳的线程数 = CPU可用核心数 / (1 - 阻塞系数)
            return (int)(cores / (1 - 0.9));
        }catch (Throwable e) {
            // 异常发生时姑且返回10个任务线程池
            return 10;
        }
    }
    
    /**
     * 停止所有正在执行的任务、暂停等待任务的处理，并返回等待执行的任务列表
     */
    public static List<Runnable> depose(){
    	List<Runnable> runs = taskScheduler.shutdownNow();
    	return runs;
    }
    
    /**
     * 重启动定时任务服务
     */
    public static void reset() {
        depose();
        taskScheduler = new ScheduledThreadPoolExecutor(getBestPoolSize());
    }
    
    /**
     * 调整线程池大小
     * @param threadPoolSize 线程池大小
     */
    public static void resizeThreadPool(int threadPoolSize) {
        taskScheduler.setCorePoolSize(threadPoolSize);
    }
}
