package com.basic.intrinsiclock;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class ClassVsObjectLockMain {

    public static void main(String[] args) throws InterruptedException {
        ClassVsObjectLock a = new ClassVsObjectLock();
        ExecutorService executorService = Executors.newFixedThreadPool(6);
        executorService.submit(new MyThread(a::m1));
        executorService.submit(new MyThread(ClassVsObjectLock::m2));
        executorService.submit(new MyThread(a::m3));
        executorService.submit(new MyThread(a::m4));
        executorService.submit(new MyThread(ClassVsObjectLock::m5));
        executorService.submit(new MyThread(ClassVsObjectLock::m6));
        executorService.awaitTermination(10, TimeUnit.SECONDS);
        executorService.shutdown();
    }

    static class MyThread implements Runnable{
        private Supplier<String> supplier;

        public MyThread(Supplier<String> supplier) {
            this.supplier = supplier;
        }


        @Override
        public void run() {
            while(true) {
                System.out.println(supplier.get());
            }
        }
    }
}
