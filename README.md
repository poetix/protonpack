protonpack
==========

[![Build Status](https://travis-ci.org/poetix/protonpack.svg?branch=master)](https://travis-ci.org/poetix/protonpack)

A small collection of ```Stream``` utilities for Java 8. Protonpack provides the following:

* ```takeWhile``` and ```takeUntil```
* ```skipWhile``` and ```skipUntil```
* ```zip``` and ```zipWithIndex```
* ```unfold```
* ```MapStream```
* ```aggregate```
* ```Streamable<T>```
* ```unique``` collector

For full API documentation, see (http://poetix.github.io/protonpack).

Available from Maven Central:

```xml
<dependency>
    <groupId>com.codepoetics</groupId>
    <artifactId>protonpack</artifactId>
    <version>1.7</version>
</dependency>
```

## takeWhile

Takes elements from the stream while the supplied condition is met. ```takeUntil``` does the same, but with the condition negated.

```java
Stream<Integer> infiniteInts = Stream.iterate(0, i -> i + 1);
Stream<Integer> finiteInts = StreamUtils.takeWhile(infiniteInts, i -> i < 10);

assertThat(finiteInts.collect(Collectors.toList()),
           hasSize(10));
```

## skipWhile

Skips elements from the stream while the supplied condition is met. ```skipUntil``` does the same, but with the condition negated.

```java
Stream<Integer> ints = Stream.of(1,2,3,4,5,6,7,8,9,10);
Stream<Integer> skipped = StreamUtils.skipWhile(ints, i -> i < 4);

List<Integer> collected = skipped.collect(Collectors.toList());

assertThat(collected,
           contains(4, 5, 6, 7, 8, 9, 10));
```

## zip

Combines two streams using the supplied combiner function.

```java
Stream<String> streamA = Stream.of("A", "B", "C");
Stream<String> streamB  = Stream.of("Apple", "Banana", "Carrot", "Doughnut");

List<String> zipped = StreamUtils.zip(streamA,
                                      streamB,
                                      (a, b) -> a + " is for " + b)
                                 .collect(Collectors.toList());

assertThat(zipped,
           contains("A is for Apple", "B is for Banana", "C is for Carrot"));
```

## unfold

Generates a (potentially infinite) stream using a generator that can indicate the end of the stream at any time by returning Optional.empty().

```java
Stream<Integer> unfolded = StreamUtils.unfold(1, i ->
    (i < 10)
        ? Optional.of(i + 1)
        : Optional.empty());

assertThat(unfolded.collect(Collectors.toList()),
           contains(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
```

## stream

Transforms a source type into a stream

`stream(Optional<T> optional):`
```java
Stream<Item> items = idStream.flatMap(id -> StreamUtils.stream(fetchItem(id));
```

## Streamable<T>

Streamable is to Stream as Iterable is to Iterator. Useful when you will want to stream repeatedly over some source.

## unique

A collector that returns the one and only item in a stream, if present, or throws an exception if multiple items are found.

```java
assertThat(Stream.of(1, 2, 3).filter(i -> i > 3).collect(CollectorUtils.unique()),
           equalTo(Optional.empty()));

assertThat(Stream.of(1, 2, 3).filter(i -> i > 2).collect(CollectorUtils.unique()),
           equalTo(Optional.of(3)));

// Throws NonUniqueValueException
Stream.of(1, 2, 3).filter(i -> i > 1).collect(CollectorUtils.unique());
```
