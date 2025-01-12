/*
 * (c) the authors Licensed under the Apache License, Version 2.0.
 */

package org.bitlap.roaringbitmap.buffer;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.bitlap.roaringbitmap.CharIterator;
import org.bitlap.roaringbitmap.IntIterator;
import org.bitlap.roaringbitmap.PeekableIntIterator;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Execution(ExecutionMode.CONCURRENT)
public class TestIterators {

  private static List<Integer> asList(IntIterator ints) {
    int[] values = new int[10];
    int size = 0;
    while (ints.hasNext()) {
      if (!(size < values.length)) {
        values = Arrays.copyOf(values, values.length * 2);
      }
      values[size++] = ints.next();
    }
    return Ints.asList(Arrays.copyOf(values, size));
  }

  private static List<Integer> asList(final CharIterator shorts) {
    return asList(new IntIterator() {
      @Override
      public IntIterator clone() {
        throw new UnsupportedOperationException();
      }

      @Override
      public boolean hasNext() {
        return shorts.hasNext();
      }

      @Override
      public int next() {
        return shorts.next();
      }
    });
  }


  private static int[] takeSortedAndDistinct(Random source, int count) {
    LinkedHashSet<Integer> ints = new LinkedHashSet<Integer>(count);
    for (int size = 0; size < count; size++) {
      int next;
      do {
        next = Math.abs(source.nextInt());
      } while (!ints.add(next));
    }
    int[] unboxed = Ints.toArray(ints);
    Arrays.sort(unboxed);
    return unboxed;
  }

  // https://github.com/RoaringBitmap/RoaringBitmap/issues/475
  @Test
  public void testCorruptionInfiniteLoop() {
    MutableRoaringBitmap bitmap = new MutableRoaringBitmap();
    bitmap.add(Integer.MAX_VALUE - 0);
    bitmap.add(Integer.MAX_VALUE - 1);
    bitmap.add(Integer.MAX_VALUE - 2);
    // Adding this one leads to the issue
    bitmap.add(Integer.MAX_VALUE - 3);
    bitmap.forEach((org.bitlap.roaringbitmap.IntConsumer) e -> {
      if (!bitmap.contains(e)) {
        throw new IllegalStateException("Not expecting to find: " + e);
      }
    });

    bitmap.runOptimize(); // This is the line causing the issue
    bitmap.forEach((org.bitlap.roaringbitmap.IntConsumer) e -> {
      if (!bitmap.contains(e)) {
        throw new IllegalStateException("Not expecting to find: " + e);
      }
    });
  }

  @Test
  public void testBitmapIteration() {
    final MappeableBitmapContainer bits =
        new MappeableBitmapContainer(2, LongBuffer.allocate(2).put(0x1l).put(1l << 63));

      assertEquals(asList(bits.getCharIterator()), ImmutableList.of(0, 127));
    assertEquals(asList(bits.getReverseCharIterator()), ImmutableList.of(127, 0));
  }

  @Test
  public void testEmptyIteration() {
    assertFalse(MutableRoaringBitmap.bitmapOf().iterator().hasNext());
    assertFalse(MutableRoaringBitmap.bitmapOf().getIntIterator().hasNext());
    assertFalse(MutableRoaringBitmap.bitmapOf().getReverseIntIterator().hasNext());
  }


  @Test
  public void testIteration() {
    final Random source = new Random(0xcb000a2b9b5bdfb6l);
    final int[] data = takeSortedAndDistinct(source, 450000);
    MutableRoaringBitmap bitmap = MutableRoaringBitmap.bitmapOf(data);

    final List<Integer> iteratorCopy = ImmutableList.copyOf(bitmap.iterator());
    final List<Integer> intIteratorCopy = asList(bitmap.getIntIterator());
    final List<Integer> reverseIntIteratorCopy = asList(bitmap.getReverseIntIterator());

    assertEquals(bitmap.getCardinality(), iteratorCopy.size());
    assertEquals(bitmap.getCardinality(), intIteratorCopy.size());
    assertEquals(bitmap.getCardinality(), reverseIntIteratorCopy.size());
    assertEquals(Ints.asList(data), iteratorCopy);
    assertEquals(Ints.asList(data), intIteratorCopy);
    assertEquals(Lists.reverse(Ints.asList(data)), reverseIntIteratorCopy);
  }



  @Test
  public void testIteration1() {
    final Random source = new Random(0xcb000a2b9b5bdfb6l);
    final int[] data1 = takeSortedAndDistinct(source, 450000);
    final int[] data = Arrays.copyOf(data1, data1.length + 50000);

    HashSet<Integer> data1Members = new HashSet<Integer>();
    for (int i : data1) {
      data1Members.add(i);
    }

    int counter = 77777;
    for (int i = data1.length; i < data.length; ++i) {
      // ensure uniqueness
      while (data1Members.contains(counter)) {
        ++counter;
      }
      data[i] = counter; // must be unique
      counter++;
      if (i % 15 == 0) {
        counter += 10; // runs of length 15 or so, with gaps of 10
      }
    }
    Arrays.sort(data);

    MutableRoaringBitmap bitmap = MutableRoaringBitmap.bitmapOf(data);
    bitmap.runOptimize(); // result should have some runcontainers and some non.

    final List<Integer> iteratorCopy = ImmutableList.copyOf(bitmap.iterator());
    final List<Integer> intIteratorCopy = asList(bitmap.getIntIterator());
    final List<Integer> reverseIntIteratorCopy = asList(bitmap.getReverseIntIterator());

    assertEquals(bitmap.getCardinality(), iteratorCopy.size());
    assertEquals(bitmap.getCardinality(), intIteratorCopy.size());
    assertEquals(bitmap.getCardinality(), reverseIntIteratorCopy.size());
    assertEquals(Ints.asList(data), iteratorCopy);
    assertEquals(Ints.asList(data), intIteratorCopy);
    assertEquals(Lists.reverse(Ints.asList(data)), reverseIntIteratorCopy);
  }

  @Test
  public void testSmallIteration() {
    MutableRoaringBitmap bitmap = MutableRoaringBitmap.bitmapOf(1, 2, 3);

    final List<Integer> iteratorCopy = ImmutableList.copyOf(bitmap.iterator());
    final List<Integer> intIteratorCopy = asList(bitmap.getIntIterator());
    final List<Integer> reverseIntIteratorCopy = asList(bitmap.getReverseIntIterator());

    assertEquals(ImmutableList.of(1, 2, 3), iteratorCopy);
    assertEquals(ImmutableList.of(1, 2, 3), intIteratorCopy);
    assertEquals(ImmutableList.of(3, 2, 1), reverseIntIteratorCopy);
  }

  @Test
  public void testSmallIteration1() {
    MutableRoaringBitmap bitmap = MutableRoaringBitmap.bitmapOf(1, 2, 3);
    bitmap.runOptimize();

    final List<Integer> iteratorCopy = ImmutableList.copyOf(bitmap.iterator());
    final List<Integer> intIteratorCopy = asList(bitmap.getIntIterator());
    final List<Integer> reverseIntIteratorCopy = asList(bitmap.getReverseIntIterator());

    assertEquals(ImmutableList.of(1, 2, 3), iteratorCopy);
    assertEquals(ImmutableList.of(1, 2, 3), intIteratorCopy);
    assertEquals(ImmutableList.of(3, 2, 1), reverseIntIteratorCopy);
  }
  
  
  @Test
  public void testSkips() {
    final Random source = new Random(0xcb000a2b9b5bdfb6l);
    final int[] data = takeSortedAndDistinct(source, 45000);
    MutableRoaringBitmap bitmap = MutableRoaringBitmap.bitmapOf(data);
    PeekableIntIterator pii = bitmap.getIntIterator();
    for(int i = 0; i < data.length; ++i) {
      pii.advanceIfNeeded(data[i]);
      assertEquals(data[i], pii.peekNext());
    }
    pii = bitmap.getIntIterator();
    for(int i = 0; i < data.length; ++i) {
      pii.advanceIfNeeded(data[i]);
      assertEquals(data[i], pii.next());
    }
    pii = bitmap.getIntIterator();
    for(int i = 1; i < data.length; ++i) {
      pii.advanceIfNeeded(data[i-1]);
      pii.next();
      assertEquals(data[i],pii.peekNext() );
    }
    bitmap.getIntIterator().advanceIfNeeded(-1);
  }
  
  @Test
  public void testSkipsDense() {
    MutableRoaringBitmap bitmap = new MutableRoaringBitmap();
    int N = 100000;
    for(int i = 0; i < N; ++i) {
      bitmap.add(2 * i);
    }
    for(int i = 0; i < N; ++i) {
      PeekableIntIterator pii = bitmap.getIntIterator();
      pii.advanceIfNeeded(2 * i);
      assertEquals(pii.peekNext(), 2 * i);
      assertEquals(pii.next(), 2 * i);
    }
  }

  @Test
  public void testIndexIterator4() throws Exception {
    MutableRoaringBitmap b = new MutableRoaringBitmap();
    for (int i = 0; i < 4096; i++) {
      b.add(i);
    }
    PeekableIntIterator it = b.getIntIterator();
    it.advanceIfNeeded(4096);
    while (it.hasNext()) {
      it.next();
    }
  }


  @Test
  public void testSkipsRun() {
    MutableRoaringBitmap bitmap = new MutableRoaringBitmap();
    bitmap.add(4L, 100000L);
    bitmap.runOptimize();
    for(int i = 4; i < 100000; ++i) {
      PeekableIntIterator pii = bitmap.getIntIterator();
      pii.advanceIfNeeded(i);
      assertEquals(pii.peekNext(), i);
      assertEquals(pii.next(), i);
    }
  }
  
  @Test
  public void testEmptySkips() {
    MutableRoaringBitmap bitmap = new MutableRoaringBitmap();
    PeekableIntIterator it = bitmap.getIntIterator();
    it.advanceIfNeeded(0);
  }
  
  @Test
  public void testIteratorsOnLargeBitmap() throws IOException {
      MutableRoaringBitmap bitmap = new MutableRoaringBitmap();

      int inc = Short.MAX_VALUE;

      for (long i = -Integer.MIN_VALUE; i < Integer.MAX_VALUE; i += inc) {
          bitmap.add((int) i);
      }

      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      DataOutputStream dos = new DataOutputStream(bos);
      bitmap.serialize(dos);
      dos.close();
      ByteBuffer bb = ByteBuffer.wrap(bos.toByteArray());
      ImmutableRoaringBitmap rrback1 = new ImmutableRoaringBitmap(bb);
      int j = 0;

      // we can iterate over the mutable bitmap
      for (int i : bitmap) {
          j += i;
      }
      
      int jj = 0;

      // we can iterate over the immutable bitmap
      for (int i : rrback1) {
          jj+= i;
      }
      assertEquals(j, jj);

  }
}

