package org.acme;

import static java.util.concurrent.CompletableFuture.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.Test;

public class CompletableFuturaTest {
	int counter = 0;

	// @Test
	public void simpleTest() {
		ExecutorService executor = Executors.newFixedThreadPool(4);
		counter = 0;

		for (int i = 0; i < 8; i++) {
			executor.submit(() -> {
				for (int j = 0; j < 5; j++) {
					counter++;
					System.out.println("Thread " + Thread.currentThread().getName() + ": " + counter);
				}
			});
		}

		System.out.println("Hello World!");
	}

	// @Test
	public void cascadeFuture() throws Exception {
		ExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		CompletableFuture
				.runAsync(() -> System.out.println("Thread " + Thread.currentThread().getName() + ": " + "Ação 1"))
				.thenRun(() -> System.out.println("Thread " + Thread.currentThread().getName() + ": " + "Ação 2"))
				.thenAccept(c -> System.out.println("Thread " + Thread.currentThread().getName() + ": " + "Ação 5"))
				.thenAccept(c -> System.out.println("Thread " + Thread.currentThread().getName() + ": " + "Ação 6"))
				.thenAccept(c -> System.out.println("Thread " + Thread.currentThread().getName() + ": " + "Ação 7"))
				
				.anyOf( //
						runAsync(() -> System.out.println("Thread " + Thread.currentThread().getName() + ": " + "Ação 3")),
						runAsync(() -> System.out.println("Thread " + Thread.currentThread().getName() + ": " + "Ação 4")))
				.get();
	}
}
