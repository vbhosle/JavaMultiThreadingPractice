package com.producer.consumer;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ProducerConsumerWithLockAndCondition {

	public static void main(String[] args) throws InterruptedException {
		MyBlockingQueue<Integer> queue = new MyBlockingQueue<>(10);
		Random random = new Random();
		
		final Runnable producer = () -> {
			while(!Thread.currentThread().isInterrupted()) {
				try {
					queue.put(random.nextInt(100));
					Thread.sleep(random.nextInt(500));
				} catch (InterruptedException e) {
					System.out.println("Producer Interrupted.");
					Thread.currentThread().interrupt();
				}
			}
		};
		
		Thread producer_1 = new Thread(producer);
		producer_1.start();
		
		Thread producer_2 = new Thread(producer);
		producer_2.start();
		
		final Runnable consumer = () -> {
			while(!Thread.currentThread().isInterrupted()) {
				try {
					System.out.println("Consumed: " + queue.take());
					Thread.sleep(random.nextInt(5));
				} catch (InterruptedException e) {
					System.out.println("Consumer Interrupted.");
					Thread.currentThread().interrupt();
				}
			}
		};
		
		Thread consumer_1 = new Thread(consumer);
		consumer_1.start();
		
		Thread consumer_2 = new Thread(consumer);
		consumer_2.start();
		
		Thread.sleep(5000);
		producer_1.interrupt();
		producer_2.interrupt();
		consumer_1.interrupt();
		consumer_2.interrupt();
		
		producer_1.join();
		producer_2.join();
		consumer_1.join();
		consumer_2.join();
	}

	public static class MyBlockingQueue<E> {

		private Queue<E> queue;
		private int max = 10;

		Lock lock = new ReentrantLock();
		Condition notFull = lock.newCondition();
		Condition notEmpty = lock.newCondition();

		public MyBlockingQueue(int size) {
			this.max = size;
			this.queue = new LinkedList<>();
		}

		public void put(E e) throws InterruptedException {
			lock.lock();
			try {
				while(queue.size() == max) {
					notFull.await();
				}
				queue.add(e);
				notEmpty.signalAll();
			} finally {
				lock.unlock();
			}
		}

		public E take() throws InterruptedException {
			lock.lock();
			try {
				while (queue.size() == 0) {
					notEmpty.await();
				}
				E item = queue.remove();
				notFull.signalAll();
				return item;
			} finally {
				lock.unlock();
			}
		}

	}

}

//private Queue<E> queue;
//private int max = 10;
//
//public BlockingQueueWithLockAndCondition(int size) {
//	this.max = size;
//	this.queue = new LinkedList<>();
//}
//
//public void put(E e) {
//	queue.add(e);
//}
//
//public E take() {
//	return queue.remove();
//}