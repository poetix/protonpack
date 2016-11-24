package com.codepoetics.protonpack.collectors;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public final class CompletableFutures {

    private CompletableFutures() {
    }

    /**
     * Collector which converts a stream of CompletableFuture&lt;T&gt; into a CompletableFuture&lt;List&lt;T&gt;&gt;
     * @param <T> The type of value returned by each {@link CompletableFuture} in the stream.
     * @return A {@link CompletableFuture} which completes with a list of all the values returned by futures in the
     *         stream, once they have all completed, or fails if any future in the stream fails.
     */
    public static <T> Collector<CompletableFuture<T>, ?, CompletableFuture<List<T>>> toFutureList() {
        return Collectors.collectingAndThen(
                Collectors.<CompletableFuture<T>>toList(),
                futures -> {
                    AtomicLong resultsRemaining = new AtomicLong(futures.size());
                    CompletableFuture<List<T>> result = new CompletableFuture<>();

                    BiFunction<T, Throwable, Void> handler = (success, failure) -> {
                        if (failure == null) {
                            if (resultsRemaining.decrementAndGet() == 0) {
                                result.complete(
                                        futures.stream()
                                                .map(CompletableFutures::safeGet)
                                                .collect(toList()));
                            }
                        } else {
                            result.completeExceptionally(failure);
                        }
                        return null;
                    };

                    futures.stream().forEach(future -> future.handle(handler));
                    return result;
                });
    }

    private static <T> T safeGet(CompletableFuture<T> future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new IllegalStateException("safeGet called on failed future: " + e);
        }
    }
}
