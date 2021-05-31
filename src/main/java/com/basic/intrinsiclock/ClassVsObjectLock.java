package com.basic.intrinsiclock;

public class ClassVsObjectLock {
    public void m1() {
        System.out.println("m1");
    }

    public static void m2() {
        System.out.println("m2");
    }

    public synchronized void m3() {
        System.out.println("m3");
    }

    public synchronized void m4() {
        System.out.println("m4");
    }

    public static synchronized void m5() {
        System.out.println("m5");
    }

    public static  synchronized void m6() {
        System.out.println("m6");
    }
}
