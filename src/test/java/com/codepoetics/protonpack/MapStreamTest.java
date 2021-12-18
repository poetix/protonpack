/*
 * Author: 		Alexis Cartier <alexcrt>
 * Date :  		27 déc. 2014
*/

package com.codepoetics.protonpack;

import com.codepoetics.protonpack.maps.MapStream;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;

public class MapStreamTest {
    
    private MapStream<String, Integer> mapStream;
    
    @Before
    public void setUp() {
        mapStream = MapStream.of("John", 1, "Alice", 2);
    }
    
    @Test
    public void testMapKeys() {
        Map<String, Integer> map = mapStream.mapKeys(x -> x.substring(2)).collect();
        assertTrue(map.containsKey("hn"));
        assertEquals(Integer.valueOf(1), map.get("hn"));
    }
    
    @Test
    public void testMapValues() {
        Map<String, Integer> map = mapStream.mapValues(x -> x + 5).collect();
        assertEquals(Integer.valueOf(6), map.get("John"));
    }
    
    @Test
    public void testMapEntries() {
        Map<String, Integer> map = mapStream.mapEntries(x -> x.concat(" Doe"), i -> i%2).collect();
        assertEquals(Integer.valueOf(1), map.get("John Doe"));
        assertEquals(Integer.valueOf(0), map.get("Alice Doe"));
    }

    @Test
    public void testMapEntriesWithBiFunction() {
        List<String> list = mapStream.mapEntries((k, v) -> k + " " + v).collect(toList());
        assertThat(list, contains("John 1", "Alice 2"));
    }

    @Test
    public void testMapEntriesToKeys() {
        Map<Character, Integer> map = mapStream.mapEntriesToKeys(String::charAt).collect();
        assertEquals(MapStream.of('o', 1, 'i', 2).collect(), map);
    }

    @Test
    public void testMapEntriesToValues() {
        Map<String, String> map = mapStream.mapEntriesToValues((k, v) -> "Pope "
                                                                         + k
                                                                         + " "
                                                                         + String.join("", Collections.nCopies(v, "I"))).collect();
        assertEquals(MapStream.of("John", "Pope John I", "Alice", "Pope Alice II").collect(), map);
    }

    @Test(expected = IllegalStateException.class)
    public void testFailMergeKeys() {
       mapStream.mapKeys(x -> Character.isUpperCase(x.charAt(0))).collect();
    } 
    
    @Test
    public void testMergeKeys() {
        Map<Boolean, List<Integer>> map = mapStream.mapKeys(x -> Character.isUpperCase(x.charAt(0))).mergeKeys().collect();
        assertEquals(Arrays.asList(1, 2), map.get(true));
        assertEquals(null, map.get(false));
    }
    
    @Test
    public void testMergeKeysWithBinaryFunction() {
        Map<Boolean, Integer> map = mapStream.mapKeys(x -> Character.isUpperCase(x.charAt(0))).mergeKeys(Integer::sum).collect();
        assertEquals(Integer.valueOf(3), map.get(true));
        assertEquals(null, map.get(false));
    }
    
    @Test
    public void testReverseMapping() {
        Map<Integer, String> map = mapStream.inverseMapping().collect();
        assertEquals("John", map.get(1));
        assertEquals("Alice", map.get(2));
        
        map.put(3, "John");
        
        Map<String, Integer> mapReversed = MapStream.of(map).inverseMapping().collect(Integer::sum);
        assertEquals(Integer.valueOf(4), mapReversed.get("John"));
        assertEquals(Integer.valueOf(2), mapReversed.get("Alice"));
    }

    @Test
    public void testFilterKeys() {
        Predicate<String> predicate = s -> s.length() < 5;
        Map<String, Integer> map = mapStream.filterKeys(predicate).collect();

        assertNull(map.get("Alice"));
        assertEquals(Integer.valueOf(1),map.get("John"));
    }

    @Test
    public void testFilterValues() {
        Predicate<Integer> predicate = i -> i == 1;
        Map<String, Integer> map = mapStream.filterValues(predicate).collect();

        assertNull(map.get("Alice"));
        assertEquals(Integer.valueOf(1),map.get("John"));
    }
}
