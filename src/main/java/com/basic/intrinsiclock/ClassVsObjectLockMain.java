package com.basic.intrinsiclock;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ClassVsObjectLockMain {

    static CountDownLatch latch = new CountDownLatch(1);

    public static void main(String[] args) throws InterruptedException {
        ClassVsObjectLock a = new ClassVsObjectLock();
        ExecutorService executorService = Executors.newFixedThreadPool(6);
        executorService.submit(new MyThread(a::m1));
        executorService.submit(new MyThread(ClassVsObjectLock::m2));
        executorService.submit(new MyThread(a::m3));
        executorService.submit(new MyThread(a::m4));
        executorService.submit(new MyThread(ClassVsObjectLock::m5));
        executorService.submit(new MyThread(ClassVsObjectLock::m6));
        latch.countDown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);
        executorService.shutdown();
    }

    static class MyThread implements Runnable{
        private Runnable method;

        public MyThread(Runnable method) {
            this.method = method;
        }


        @Override
        public void run() {
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while(true) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                method.run();
            }
        }
    }
}
