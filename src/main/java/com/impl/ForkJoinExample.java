package com.impl;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.LongStream;

public class ForkJoinExample {

	public static void main(String[] args) {
		long[] numbers = LongStream.rangeClosed(1, 500000).toArray();
		ForkJoinTask<Long> task = new ForkJoinSumCalculator(numbers);
		System.out.println(new ForkJoinPool().invoke(task));
	}

	static class ForkJoinSumCalculator extends RecursiveTask<Long>{

		private final long[] numbers;
		private final int start;
		private final int end;
		public static final long THRESHOLD = 10_000;
		
		public ForkJoinSumCalculator(long[] numbers) {
			this(numbers, 0, numbers.length);
		}
		public ForkJoinSumCalculator(long[] numbers, int start, int end) {
			this.numbers = numbers;
			this.start = start;
			this.end = end;
		}
		
		@Override
		protected Long compute() {
			int length = end - start;
			
			if(length <= THRESHOLD) {
				return computeSequentially();
			}
			
			ForkJoinSumCalculator leftTask = new ForkJoinSumCalculator(numbers, start, start+length/2);
			leftTask.fork();
			ForkJoinSumCalculator rightTask = new ForkJoinSumCalculator(numbers, start+length/2, end);
			Long rightResult = rightTask.compute();
			Long leftResult = leftTask.join();
			return leftResult+rightResult;
		}
		private Long computeSequentially() {
			long sum = 0;
			for(int i=start;i<end;i++) {
				sum+=numbers[i];
			}
			return sum;
		}
		
	}
}
