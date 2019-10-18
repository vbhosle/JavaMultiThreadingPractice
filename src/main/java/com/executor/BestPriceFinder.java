package com.executor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.computation.Quote;
import com.computation.Shop;

public class BestPriceFinder {
	
	private final List<Shop> shops = Arrays.asList(new Shop("BestPrice"),
            new Shop("LetsSaveBig"),
            new Shop("MyFavoriteShop"),
            new Shop("BuyItAll"),
            new Shop("ShopEasy"));
	
//	private final ExecutorService executor = Executors.newFixedThreadPool(5);
	private final ExecutorService executor = new TimingThreadPool(5);
	private final CompletionService<String> completionService = new ExecutorCompletionService<>(executor);
	
	public List<Quote> getQuotes(String product) throws InterruptedException{
		List<Callable<String>> tasks = shops.stream().map(shop -> toCallable(product, p -> shop.getPrice(product))).collect(Collectors.toList());
		tasks.forEach(task -> completionService.submit(task));
		List<Quote> quotes = new ArrayList<>(tasks.size());
		for(int i = 0; i < tasks.size(); i++) {
			Future<String> result = completionService.take();
			Quote quote;
			try {
				quote = Quote.parse(result.get());
				System.out.println("Got quote : " + quote);
				quotes.add(quote);
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		return quotes;
	}
	
	public <T, V> Callable<V> toCallable(T t, Function<T, V> computation){
		return new Callable<V>() {
			public V call() {
				return computation.apply(t);
			}
		};
	}
	
	public void shutdown() throws InterruptedException {
		executor.shutdown();
		executor.awaitTermination(5, TimeUnit.MINUTES);
	}
}
