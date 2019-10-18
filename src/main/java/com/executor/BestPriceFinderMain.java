package com.executor;

import java.util.List;

import com.computation.Quote;

public class BestPriceFinderMain {

	public static void main(String[] args) throws InterruptedException {
		BestPriceFinder priceFinder = new BestPriceFinder();
		List<Quote> quotes = priceFinder.getQuotes("apple");
		System.out.println("Found " + quotes.size() + " quotes.");
		priceFinder.shutdown();
	}

}
