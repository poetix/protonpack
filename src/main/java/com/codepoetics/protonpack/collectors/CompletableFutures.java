package com.codepoetics.protonpack.collectors;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
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

                    futures.forEach(future -> future.handle(handler));
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

    private interface FutureReducer<T, A> {

        static <T, A> FutureReducer<T, A> of(A identity, BiFunction<T, A, A> reducer, BinaryOperator<A> combiner) {
            return new SingleFutureReducer<>(identity, reducer, combiner);
        }

        CompletableFuture<A> complete();

        FutureReducer<T, A> addFuture(CompletableFuture<T> future);

        FutureReducer<T, A> combine(FutureReducer<T, A> other);
    }

    private static final class SingleFutureReducer<T, A> implements FutureReducer<T, A> {


        private AtomicLong futureCount = new AtomicLong(0L);
        private long resultsReceived = 0;
        private CompletableFuture<A> output = null;
        private Throwable exception = null;

        private A accumulator;
        private final BiFunction<T, A, A> reducer;
        private final BinaryOperator<A> combiner;

        private SingleFutureReducer(A accumulator, BiFunction<T, A, A> reducer, BinaryOperator<A> combiner) {
            this.accumulator = accumulator;
            this.reducer = reducer;
            this.combiner = combiner;
        }

        private synchronized void resultReceived(T result) {
            try {
                accumulator = reducer.apply(result, accumulator);
                resultsReceived += 1;
                completeIfReady();
            } catch (Exception e) {
                exceptionReceived(e);
            }
        }

        private synchronized void exceptionReceived(Throwable exception) {
            this.exception = exception;
            completeIfReady();
        }

        @Override
        public synchronized CompletableFuture<A> complete() {
            output = new CompletableFuture<>();
            completeIfReady();
            return output;
        }

        private void completeIfReady() {
            if (output == null) {
                return;
            }
            if (exception != null) {
                output.completeExceptionally(exception);
            } else if (futureCount.get() == resultsReceived) {
                output.complete(accumulator);
            }
        }

        @Override
        public FutureReducer<T, A> addFuture(CompletableFuture<T> future) {
            futureCount.incrementAndGet();
            future.handle((result, error) -> {
                if (error != null) {
                    exceptionReceived(error);
                } else {
                    resultReceived(result);
                }
                return null;
            });
            return this;
        }

        @Override
        public FutureReducer<T, A> combine(FutureReducer<T, A> other) {
            return new CombinedFutureReducer<>(this, other, combiner);
        }
    }

    private static final class CombinedFutureReducer<T, A> implements FutureReducer<T, A> {
        private final FutureReducer<T, A> left;
        private final FutureReducer<T, A> right;
        private final BinaryOperator<A> combiner;

        private CombinedFutureReducer(FutureReducer<T, A> left, FutureReducer<T, A> right, BinaryOperator<A> combiner) {
            this.left = left;
            this.right = right;
            this.combiner = combiner;
        }

        @Override
        public CompletableFuture<A> complete() {
            return left.complete().thenCombine(right.complete(), combiner);
        }

        @Override
        public FutureReducer<T, A> addFuture(CompletableFuture<T> future) {
            throw new IllegalStateException("Cannot add futures after combination");
        }

        @Override
        public FutureReducer<T, A> combine(FutureReducer<T, A> other) {
            return new CombinedFutureReducer<>(this, other, combiner);
        }
    }

    public static <A> Collector<CompletableFuture<A>, ?, CompletableFuture<Optional<A>>> reducing(BinaryOperator<A> reducer) {
        return toFuture(
                Optional::empty,

                (left, maybeRight) -> maybeRight.isPresent()
                    ? maybeRight.map(right -> reducer.apply(right, left))
                    : Optional.of(left),

                (maybeLeft, maybeRight) -> maybeLeft.isPresent()
                    ? maybeLeft.flatMap(left -> maybeRight.map(right -> reducer.apply(left, right)))
                    : maybeRight);
    }

    public static <T, A> Collector<CompletableFuture<T>, ?, CompletableFuture<A>> toFuture(
            Supplier<A> identitySupplier, BiFunction<T, A, A> reducer, BinaryOperator<A> combiner) {
        return toFuture(identitySupplier, reducer, combiner, Function.identity());
    }

    public static <T, A, R> Collector<CompletableFuture<T>, ?, CompletableFuture<R>> toFuture(
            Supplier<A> identitySupplier, BiFunction<T, A, A> reducer, BinaryOperator<A> combiner, Function<? super A, ? extends R> completer) {
        return Collector.of(
                    () -> FutureReducer.of(identitySupplier.get(), reducer, combiner),
                    FutureReducer::addFuture,
                    FutureReducer::combine,
                    fr -> fr.complete().thenApply(completer));
    }

}
