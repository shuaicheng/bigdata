package com.ccx.spark.demo;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class TestMain {
	// 请求总数
    public static int clientTotal = 100000;
    // 同时并发执行的线程数
    public static int threadTotal = 20;

    public static AtomicInteger  count =new AtomicInteger();
    
	public static void main(String[] args)   throws Exception{
		long start = System.currentTimeMillis();
	        ExecutorService executorService = Executors.newFixedThreadPool(threadTotal);
	        //信号量，此处用于控制并发的线程数
	        final Semaphore semaphore = new Semaphore(threadTotal);
	        //闭锁，可实现计数器递减
	        final CountDownLatch countDownLatch = new CountDownLatch(clientTotal);
	        for (int i = 0; i < clientTotal ; i++) {
	            executorService.execute(() -> {
	                try {
	                	//执行此方法用于获取执行许可，当总计未释放的许可数不超过 threadTotal 时，
	                	//允许通行，否则线程阻塞等待，直到获取到许可。
//	                    semaphore.acquire();
	                    String name="8D23AA3874B8538CFA568C3CCFBD4B0F";
				    	String mobile="A2ADFA19FBC11119D76D7B0701390EC2";
				    	String card="D0A1126F6D45E14A721A41A928AD0FB6";
				    	String cid="27D773500BA10D5625A7ED328BFAA4ECF591B4B9BD6CFEDD";
				    	try {
							jiemi(name);
//							jiemi(mobile);
//							jiemi(card);
//							jiemi(cid);
						} catch (Exception e) {
							e.printStackTrace();
						}
	                    add();
	                    //释放许可
//	                    semaphore.release();
	                } catch (Exception e) {
	                    //log.error("exception", e);
	                    e.printStackTrace();
	                }
	                //闭锁减一
	                countDownLatch.countDown();
	            });
	        }
	        countDownLatch.await();//线程阻塞，直到闭锁值为0时，阻塞才释放，继续往下执行
	        executorService.shutdown();
	        System.out.println("用时：" + (System.currentTimeMillis() - start));
	        System.out.println("*******"+count);
	    }

	 private static void add() {
	     count.incrementAndGet();
	}
	 
	 public static String jiemi(String str) throws Exception {

			return DES3Util.desedeDecoder(str, "88888888");
		}
	 
}
