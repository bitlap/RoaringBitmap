package org.bitlap.roaringbitmap;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Execution(ExecutionMode.CONCURRENT)
public class TestUtil {

    @Test
    public void testUtilUnsignedIntersection() {
        char[] data1 = fromShorts(new short[]{-19, -17, -15, -13, -11, -9, -7, -5, -3, -1});
        char[] data2 = fromShorts(new short[]{-18, -16, -14, -12, -10, -8, -1});
        assertTrue(Util.unsignedIntersects(data1, data1.length, data2, data2.length));
        char[] data3 = fromShorts(new short[]{-19, -17, -15, -13, -11, -9, -7});
        char[] data4 = fromShorts(new short[]{-18, -16, -14, -12, -10, -8, -6, -4, -2, 0});
        assertFalse(Util.unsignedIntersects(data3, data3.length, data4, data4.length));
        char[] data5 = {};
        char[] data6 = {};
        assertFalse(Util.unsignedIntersects(data5, data5.length, data6, data6.length));
    }

    @Test
    public void testBranchyUnsignedBinarySearch() {
        char[] data1 = fromShorts(new short[]{-19, -17, -15, -13, -11, -9, -7, -5, -3});
        assertEquals(8, Util.branchyUnsignedBinarySearch(data1, 0, data1.length, data1[8]));
        assertEquals(0, Util.branchyUnsignedBinarySearch(data1, 0, data1.length, data1[0]));
        assertEquals(data1.length-1, Util.branchyUnsignedBinarySearch(data1, data1.length-1, data1.length, data1[data1.length-1]));
        assertEquals(-1, Util.branchyUnsignedBinarySearch(data1, 0, 0, (char)0));
        assertEquals(-10, Util.branchyUnsignedBinarySearch(data1, 0, data1.length, (char) -1));
    }

    @Test
    public void testPartialRadixSortEmpty() {
        int[] data = new int[] {};
        int[] test = Arrays.copyOf(data, data.length);
        Util.partialRadixSort(test);
        assertArrayEquals(data, test);
    }

    @Test
    public void testPartialRadixSortIsStableInSameKey() {
        int[] data = new int[] {25, 1, 0, 10};
        for (int key : new int[]{0, 1 << 16, 1 << 17, 1 << 24, 1 << 25}) {
            // clear the old key and prepend the new key
            for (int i = 0; i < data.length; ++i) {
                data[i] &= 0xFFFF;
                data[i] |= key;
            }
            int[] test = Arrays.copyOf(data, data.length);
            Util.partialRadixSort(test);
            assertArrayEquals(data, test);
        }
    }

    @Test
    public void testPartialRadixSortSortsKeysCorrectly() {
        int[] keys = {
                // the test expects the first key to be less than the second key,
                // neither should have any of the lower 16 bits set
                1 << 16, 1 << 25,
                1 << 16, 1 << 17,
                1 << 17, 1 << 18,
                1 << 25, 1 << 26,
                1 << 23, 1 << 25,
                1 << 24, 1 << 25,
                1 << 25, 1 << 27,
                1 << 29, 1 << 30,
        };
        for (int i = 0; i < keys.length; i += 2) {
            int key1 = keys[i];
            int key2 = keys[i+1];
            int[] data = new int[]{key2 | 25, key1 | 1, 0, key2 | 10, 25, key1 | 10, key1, 10};
            // sort by keys, leave values stable
            int[] expected = new int[]{0, 25, 10, key1 | 1, key1 | 10, key1, key2 | 25, key2 | 10};
            int[] test = Arrays.copyOf(data, data.length);
            Util.partialRadixSort(test);
            assertArrayEquals(expected, test);
        }
    }

    @Test
    public void testPartialRadixSortSortsKeysCorrectlyWithDuplicates() {
        int[] keys = {
                // the test expects the first key to be less than the second key,
                // neither should have any of the lower 16 bits set
                1 << 16, 1 << 25,
                1 << 16, 1 << 17,
                1 << 17, 1 << 18,
                1 << 25, 1 << 26,
                1 << 23, 1 << 25,
                1 << 24, 1 << 25,
                1 << 25, 1 << 27,
                1 << 29, 1 << 30,
        };
        for (int i = 0; i < keys.length; i += 2) {
            int key1 = keys[i];
            int key2 = keys[i + 1];
            int[] data = new int[]{key2 | 25, key1 | 1, 0, key2 | 10, 25, key1 | 10, key1, 10,
                    key2 | 25, key1 | 1, 0, key2 | 10, 25, key1 | 10, key1, 10};
            // sort by keys, leave values stable
            int[] expected = new int[]{0, 25, 10, 0, 25, 10, key1 | 1, key1 | 10, key1, key1 | 1, key1 | 10, key1,
                    key2 | 25, key2 | 10, key2 | 25, key2 | 10};
            int[] test = Arrays.copyOf(data, data.length);
            Util.partialRadixSort(test);
            assertArrayEquals(expected, test);
        }
    }

    @Test
    public void testAdvanceUntil() {
        char[] data = fromShorts(new short[]{0, 3, 16, 18, 21, 29, 30,-342});
        assertEquals(1, Util.advanceUntil(data, -1, data.length, (char) 3));
        assertEquals(5, Util.advanceUntil(data, -1, data.length, (char) 28));
        assertEquals(5, Util.advanceUntil(data, -1, data.length, (char) 29));
        assertEquals(7, Util.advanceUntil(data, -1, data.length, (char) -342));
    }

    @Test
    public void testReverseUntil() {
        char[] data = fromShorts(new short[]{1, 3, 16, 18, 21, 29, 30,-342});
        assertEquals(0, Util.reverseUntil(data, data.length, data.length, (char) 0));
        assertEquals(1, Util.reverseUntil(data, data.length, data.length, (char) 3));
        assertEquals(4, Util.reverseUntil(data, data.length, data.length, (char) 28));
        assertEquals(5, Util.reverseUntil(data, data.length, data.length, (char) 29));
        assertEquals(6, Util.reverseUntil(data, data.length, data.length, (char) 30));
        assertEquals(6, Util.reverseUntil(data, data.length, data.length, (char) 31));
        assertEquals(7, Util.reverseUntil(data, data.length, data.length, (char) -342));
    }

    @Test
    public void testIterateUntil() {
        char[] data = fromShorts(new short[]{0, 3, 16, 18, 21, 29, 30,-342});
        assertEquals(1, Util.iterateUntil(data, 0, data.length, ((char) 3)));
        assertEquals(5, Util.iterateUntil(data, 0, data.length, ((char) 28)));
        assertEquals(5, Util.iterateUntil(data, 0, data.length, ((char) 29)));
        assertEquals(7, Util.iterateUntil(data, 0, data.length, ((char) -342)));
    }

    static char[] fromShorts(short[] array) {
        char[] result = new char[array.length];
        for (int i = 0 ; i < array.length; ++i) {
            result[i] = (char)(array[i] & 0xFFFF);
        }
        return result;
    }

    public static Stream<Arguments> sets() {
        return Stream.of(
                Arguments.of(SeededTestData.rleRegion().toArray(), SeededTestData.rleRegion().toArray()),
                Arguments.of(SeededTestData.denseRegion().toArray(), SeededTestData.rleRegion().toArray()),
                Arguments.of(SeededTestData.sparseRegion().toArray(), SeededTestData.rleRegion().toArray()),
                Arguments.of(SeededTestData.rleRegion().toArray(), SeededTestData.denseRegion().toArray()),
                Arguments.of(SeededTestData.denseRegion().toArray(), SeededTestData.denseRegion().toArray()),
                Arguments.of(SeededTestData.sparseRegion().toArray(), SeededTestData.denseRegion().toArray()),
                Arguments.of(SeededTestData.rleRegion().toArray(), SeededTestData.sparseRegion().toArray()),
                Arguments.of(SeededTestData.denseRegion().toArray(), SeededTestData.sparseRegion().toArray()),
                Arguments.of(SeededTestData.sparseRegion().toArray(), SeededTestData.sparseRegion().toArray())
        );
    }


    @MethodSource("sets")
    @ParameterizedTest
    public void testIntersectBitmapWithArray(int[] set1, int[] set2) {
        long[] bitmap = new long[1024];
        for (int i : set1) {
            bitmap[i >>> 6] |= 1L << i;
        }
        long[] referenceBitmap = new long[1024];
        char[] array = new char[set2.length];
        int pos = 0;
        for (int i : set2) {
            referenceBitmap[i >>> 6] |= 1L << i;
            array[pos++] = (char)i;
        }
        int expectedCardinality = 0;
        for (int i = 0; i < 1024; ++i) {
            referenceBitmap[i] &= bitmap[i];
            expectedCardinality += Long.bitCount(referenceBitmap[i]);
        }
        int cardinality = Util.intersectArrayIntoBitmap(bitmap, array, array.length);
        assertEquals(expectedCardinality, cardinality);
        assertArrayEquals(referenceBitmap, bitmap);
    }
}