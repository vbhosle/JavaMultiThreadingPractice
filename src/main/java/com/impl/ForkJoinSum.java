package com.impl;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.LongStream;

public class ForkJoinSum {

	public static void main(String[] args) {
		long[] numbers = LongStream.rangeClosed(1, 9000000).toArray();
		long start = System.nanoTime();
		long result = computeSum(numbers);
		long end = System.nanoTime();
		System.out.println("sum="+result + " found in " + (end - start));
		start = System.nanoTime();
		result = computeSum(numbers);
		end = System.nanoTime();
		System.out.println("sum="+result + " found in " + (end - start));
	}
	
	public static long computeSum(long[] numbers) {
		return computeSumBetween(numbers, 0, numbers.length);
	}
	
	public static long computeSumBetween(long[] numbers, int start, int end) {
		long result = 0L;
		for(int i = start; i < end; i++) {
			result += numbers[i];
		}
		
		return result;
	}
	
	public static long computeSumParallel(long[] numbers) throws InterruptedException, ExecutionException {
		ForkJoinPool pool = new ForkJoinPool();
		ForkJoinTask<Long> task = pool.submit(new SumCalculator(numbers));
		return task.get();
	}
	private static class SumCalculator extends RecursiveTask<Long>{

		long[] numbers;
		int start;
		int end;
		private final static int THRESHOLD = 10_000;
		
		public SumCalculator(long[] numbers) {
			this(numbers, 0, numbers.length);
		}


		public SumCalculator(long[] numbers, int start, int end) {
			this.numbers = numbers;
			this.start = start;
			this.end = end;
		}


		@Override
		protected Long compute() {
			int length = end - start;
			if(length <= THRESHOLD) {
				return computeSumBetween(numbers, start, end);
			}
			
			SumCalculator leftTask = new SumCalculator(numbers, start, start + length/2);
			leftTask.fork();
			SumCalculator rightTask = new SumCalculator(numbers, start + length/2, end);
			Long rightResult = rightTask.compute();
			Long leftResult = leftTask.join();
			return leftResult + rightResult;
		}
		
		
		
	}
}
