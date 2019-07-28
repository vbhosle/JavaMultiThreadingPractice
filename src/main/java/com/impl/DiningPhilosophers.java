package com.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class DiningPhilosophers {

	public static void main(String[] args) throws InterruptedException {
		List<Philosopher> philosophers = setup();
		ExecutorService threadPool = Executors.newFixedThreadPool(5);
		List<Callable<Void>> readyPhilosophers = philosophers.stream().map(DiningPhilosophers::takeUpSeat)
				.collect(Collectors.toList());
		System.out.println("Start!!!!");
		List<Future<Void>> activePhilosophers = threadPool.invokeAll(readyPhilosophers, 5, TimeUnit.SECONDS);
		activePhilosophers.forEach(f -> f.cancel(true));
		threadPool.shutdown();
		threadPool.awaitTermination(5, TimeUnit.SECONDS);
	}

	public static Callable<Void> takeUpSeat(Philosopher philosopher) {
		return new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				while (!Thread.currentThread().isInterrupted()) {
					philosopher.think();
					philosopher.eat();
				}

				return null;
			}

		};

	}

	private static List<Philosopher> setup() {
		Chopstick chopstick_1 = new Chopstick("chopstick-1");
		Chopstick chopstick_2 = new Chopstick("chopstick-2");
		Chopstick chopstick_3 = new Chopstick("chopstick-3");
		Chopstick chopstick_4 = new Chopstick("chopstick-4");
		Chopstick chopstick_5 = new Chopstick("chopstick-5");

		Philosopher philosopher_1 = new Philosopher("P1", chopstick_1, chopstick_2);
		Philosopher philosopher_2 = new Philosopher("P2", chopstick_2, chopstick_3);
		Philosopher philosopher_3 = new Philosopher("P3", chopstick_3, chopstick_4);
		Philosopher philosopher_4 = new Philosopher("P4", chopstick_4, chopstick_5);
		Philosopher philosopher_5 = new Philosopher("P5", chopstick_5, chopstick_1);

		List<Philosopher> philosophers = Arrays.asList(philosopher_1, philosopher_2, philosopher_3, philosopher_4,
				philosopher_5);
		return philosophers;
	}

	static class Philosopher {
		String name;
		Chopstick left;
		Chopstick right;
		Random random = new Random();

		public Philosopher(String name, Chopstick left, Chopstick right) {
			this.name = name;
			this.left = left;
			this.right = right;
		}

		@Override
		public String toString() {
			return "Philosopher [name=" + name + ", left=" + left + ", right=" + right + "]";
		}

		public void think() throws InterruptedException {
			Thread.sleep(random.nextInt(50));
		}

		public void eat() throws InterruptedException {
			boolean ate = false;
			try {
				if (left.pickChopstick(100, TimeUnit.MILLISECONDS)) {
					if (right.pickChopstick(100, TimeUnit.MILLISECONDS)) {
						System.out.println("Eating .. " + this.toString());
						Thread.sleep(random.nextInt(100));
						ate = true;
					}
				}
			} finally {
				left.returnChopstick();
				right.returnChopstick();
			}

			if (ate) {
				System.out.println("Done Eating .." + this.toString());
			} else {
				System.out.println("Failed to get chopsticks .. " + this.toString());
			}
		}
	}

	static class Chopstick {
		private String name;
		private Lock lock = new ReentrantLock();

		public Chopstick(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}

		public boolean pickChopstick(long time, TimeUnit timeUnit) throws InterruptedException {
			return lock.tryLock(time, timeUnit);
		}

		public void returnChopstick() throws InterruptedException {
			lock.unlock();
		}

	}

}
