package com.producer.consumer;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class ProducerConsumerWithWaitNotify {

	public static void main(String[] args) throws InterruptedException {
		MyBlockingQueue<Integer> queue = new MyBlockingQueue<>(10);
		Random random = new Random();

		final Runnable producer = () -> {
			while (!Thread.currentThread().isInterrupted()) {
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
			while (!Thread.currentThread().isInterrupted()) {
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

		public MyBlockingQueue(int size) {
			this.max = size;
			this.queue = new LinkedList<>();
		}

		public synchronized void put(E e) throws InterruptedException {
			while (queue.size() == max) {
				wait();
			} 
			queue.add(e);
			notifyAll();
		}

		public synchronized E take() throws InterruptedException {
			while (queue.size() == 0) {
				wait();
			}
			E item = queue.remove();
			notifyAll();
			return item;
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