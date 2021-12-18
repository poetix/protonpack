/*
 * Author: 		Alexis Cartier <alexcrt>
 * Date :  		24 déc. 2014
*/

package com.codepoetics.protonpack.maps;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

/**
 * A stream of {@code Map.Entry<K, V>}. 
 */
public interface MapStream<K, V> extends Stream<Entry<K, V>> {

    /**
     * Construct a {@code MapStream<K, V>} from the map
     * @param map - the map to build the stream from
     * @param <K> - the type of the map keys
     * @param <V> - the type of the map values
     * @return a new {@code MapStream<K, V>}
     */
    public static <K, V> MapStream<K, V> of(Map<K, V> map) {
        return new DefaultMapStream<>(map.entrySet().stream());
    }
    
    /**
     * Construct a {@code MapStream<K, V>} from the map
     * @param maps - the map to build the stream from
     * @param <K> - the type of the map keys
     * @param <V> - the type of the map values
     * @return a new {@code MapStream<K, V>}
     */
    @SafeVarargs
    public static <K, V> MapStream<K, V> ofMaps(Map<K, V>... maps) {
        return new DefaultMapStream<>(Stream.of(maps).flatMap(m -> m.entrySet().stream()));
    }
    
    /**
     * Construct a {@code MapStream<K, V>} from a single key-value pair
     * @param key - the key
     * @param value - the value
     * @param <K> - the type of the map keys
     * @param <V> - the type of the map values
     * @return a new {@code MapStream<K, V>}
     */
    public static <K, V> MapStream<K, V> of(K key, V value) {
        return new DefaultMapStream<>(Stream.of(new SimpleImmutableEntry<>(key, value)));
    }  
    
    /**
     * Construct a {@code MapStream<K, V>} from a multiple key-value pairs
     * @param key - key
     * @param value - value
     * @param key1 - key1
     * @param value1 - value1
     * @param <K> - the type of the map keys
     * @param <V> - the type of the map values
     * @return a new {@code MapStream<K, V>}
     */
    public static <K, V> MapStream<K, V> of(K key, V value, K key1, V value1) {
        return new DefaultMapStream<>(Stream.of(new SimpleImmutableEntry<>(key, value),
                                                new SimpleImmutableEntry<>(key1, value1)));
    }  
    
    /**
     * Construct a {@code MapStream<K, V>} from a multiple key-value pairs
     * @param key - key
     * @param value - value
     * @param key1 - key1
     * @param value1 - value1
     * @param key2 - key2
     * @param value2 - value2
     * @param <K> - the type of the map keys
     * @param <V> - the type of the map values
     * @return a new {@code MapStream<K, V>}
     */
    public static <K, V> MapStream<K, V> of(K key, V value, K key1, V value1, K key2, V value2) {
        return new DefaultMapStream<>(Stream.of(new SimpleImmutableEntry<>(key, value),
                                                new SimpleImmutableEntry<>(key1, value1),
                                                new SimpleImmutableEntry<>(key2, value2)));
    }    
    
    /**
     * Construct a {@code MapStream<K, V>} from a multiple key-value pairs
     * @param key - key
     * @param value - value
     * @param key1 - key1
     * @param value1 - value1
     * @param key2 - key2
     * @param value2 - value2
     * @param key3 - key3
     * @param value3 - value3
     * @param <K> - the type of the map keys
     * @param <V> - the type of the map values
     * @return a new {@code MapStream<K, V>}
     */
    public static <K, V> MapStream<K, V> of(K key, V value, K key1, V value1, K key2, V value2, K key3, V value3) {
        return new DefaultMapStream<>(Stream.of(new SimpleImmutableEntry<>(key, value),
                                                new SimpleImmutableEntry<>(key1, value1),
                                                new SimpleImmutableEntry<>(key2, value2),
                                                new SimpleImmutableEntry<>(key3, value3)));
    }   
    
    /**
     * Construct a {@code MapStream<K, V>} from a multiple key-value pairs
     * @param key - key
     * @param value - value
     * @param key1 - key1
     * @param value1 - value1
     * @param key2 - key2
     * @param value2 - value2
     * @param key3 - key3
     * @param value3 - value3
     * @param key4 - key4
     * @param value4 - value4
     * @param <K> - the type of the map keys
     * @param <V> - the type of the map values
     * @return a new {@code MapStream<K, V>}
     */
    public static <K, V> MapStream<K, V> of(K key, V value, K key1, V value1, K key2, V value2, K key3, V value3,  K key4, V value4) {
        return new DefaultMapStream<>(Stream.of(new SimpleImmutableEntry<>(key, value),
                                                new SimpleImmutableEntry<>(key1, value1),
                                                new SimpleImmutableEntry<>(key2, value2),
                                                new SimpleImmutableEntry<>(key3, value3),
                                                new SimpleImmutableEntry<>(key4, value4)));
    }   
    
    /**
     * Applies the mapping for each key in the map. If your mapping function is not injective,
     * make sure you call {@code mergeKeys} or that you provide a merge function when calling
     * {@code collect}
     * @param mapper - the key mapping to be applied
     * @param <K1> the type to map the keys into
     * @return a new MapStream
     */
    default <K1> MapStream<K1, V> mapKeys(Function<? super K, ? extends K1> mapper) {
        return new DefaultMapStream<>(map(e -> new SimpleImmutableEntry<>(mapper.apply(e.getKey()), e.getValue())));
    }
    
    /**
     * Applies the mapping for each value in the map.
     * @param mapper - the value mapping to be applied
     * @param <V1> the type to map the values into
     * @return a new MapStream
     */
    default <V1> MapStream<K, V1> mapValues(Function<? super V, ? extends V1> mapper) {
        return new DefaultMapStream<>(map(e -> new SimpleImmutableEntry<>(e.getKey(), mapper.apply(e.getValue()))));
    }
    
    /**
     * Applies the mapping for each key and value in the map. If your mapping function is not
     * injective for the keys, make sure you call {@code mergeKeys} or that you provide a merge
     * function when calling {@code collect}.
     * @param keyMapper - the key mapping to be applied
     * @param valueMapper - the value mapping to be applied
     * @param <K1> the type to map the keys into
     * @param <V1> the type to map the values into
     * @return a new MapStream
     */
    default <K1, V1> MapStream<K1, V1> mapEntries(Function<? super K, ? extends K1> keyMapper, Function<? super V, ? extends V1> valueMapper) {
        return new DefaultMapStream<>(map(e -> new SimpleImmutableEntry<>(keyMapper.apply(e.getKey()), valueMapper.apply(e.getValue()))));
    }
    
    /**
     * Applies the mapping for each (key, value) pair in the map.
     * @param mapper the mapping function to be applied
     * @param <R> the type to map the (key, value) pairs into
     * @return a new Stream
     */
    default <R> Stream<R> mapEntries(BiFunction<? super K, ? super V, ? extends R> mapper) {
        return map(e -> mapper.apply(e.getKey(), e.getValue()));
    }

    /**
     * Applies the mapping for each key and value in the map, to produce a new key for each
     * entry. If your mapping function is not injective for the keys, make sure you call {@code
     * mergeKeys} or that you provide a merge function when calling {@code collect}.
     * @param mapper the mapping function to be applied
     * @param <R>    the new key type to map the (key, value) pairs into
     * @return a new MapStream
     */
    default <R> MapStream<R, V> mapEntriesToKeys(BiFunction<? super K, ? super V, ? extends R> mapper) {
        return new DefaultMapStream<>(map(e -> new SimpleImmutableEntry<>(mapper.apply(e.getKey(), e.getValue()), e.getValue())));
    }

    /**
     * Applies the mapping for each key and value in the map, to produce a new value for each
     * entry.
     * @param mapper the mapping function to be applied
     * @param <R>    the new value type to map the (key, value) pairs into
     * @return a new MapStream
     */
    default <R> MapStream<K, R> mapEntriesToValues(BiFunction<? super K, ? super V, ? extends R> mapper) {
        return new DefaultMapStream<>(map(e -> new SimpleImmutableEntry<>(e.getKey(), mapper.apply(e.getKey(), e.getValue()))));
    }

    /**
     * Merge keys of the Stream into a new Stream 
     * @return a new MapStream
     */
    default MapStream<K, List<V>> mergeKeys() {
        return of(collect(groupingBy(Entry::getKey, mapping(Entry::getValue, toList()))));
    }
    
    /**
     * Merge keys of the Stream into a new Stream with the merge function provided
     * @param mergeFunction The merge function to use
     * @return a new MapStream
     */
    default MapStream<K, V> mergeKeys(BinaryOperator<V> mergeFunction) {
        return of(collect(mergeFunction));
    }
    
    /**
     * Return a Map from the stream. If you have similar keys in the stream,
     * don't forget to call {@code mergeKeys()} or {@code collect (BinaryOperator<V> mergeFunction)}
     * @return a map
     */
    default Map<K, V> collect() {
        return collect(toMap(Entry::getKey, Entry::getValue));
    }

    /**
    * Return a Map from the stream. If there are similar keys in the stream,
    * the merge function will be applied to merge the values of those keys
    * @param mergeFunction the function to merge the values if the keys are not unique
    * @return a map
    */
    default Map<K, V> collect(BinaryOperator<V> mergeFunction) {
        return collect(toMap(Entry::getKey, Entry::getValue, mergeFunction));
    }

    /**
     * Return a MapStream from which the keys and values are reversed.
     * @return a new MapStream
     */
    default MapStream<V, K> inverseMapping() {
        return new DefaultMapStream<>(map(e -> new SimpleImmutableEntry<>(e.getValue(), e.getKey())));
    }

    default MapStream<K, V> filterKeys(Predicate<K> predicate) {
        return filter(e -> predicate.test(e.getKey()));
    }

    default MapStream<K, V> filterValues(Predicate<V> predicate) {
        return filter(e -> predicate.test(e.getValue()));
    }

    @Override
    MapStream<K, V> limit(long n);

    @Override
    MapStream<K, V> skip(long n);

    @Override
    MapStream<K, V> sorted();

    @Override
    MapStream<K, V> sorted(Comparator<? super Entry<K, V>> comparator);

    @Override
    MapStream<K, V> peek(Consumer<? super Entry<K, V>> action);

    @Override
    MapStream<K, V> onClose(Runnable closeHandler);

    @Override
    MapStream<K, V> filter(Predicate<? super Entry<K, V>> predicate);

    @Override
    MapStream<K, V> parallel();

    @Override
    MapStream<K, V> sequential();

    @Override
    MapStream<K, V> unordered();

    @Override
    MapStream<K, V> distinct();
}
