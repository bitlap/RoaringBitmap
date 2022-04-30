package org.bitlap.roaringbitmap.buffer;

import org.bitlap.roaringbitmap.ContainerBatchIterator;

import java.nio.CharBuffer;

import static org.bitlap.roaringbitmap.buffer.BufferUtil.unsignedBinarySearch;


public final class ArrayBatchIterator implements ContainerBatchIterator {

  private int index = 0;
  private MappeableArrayContainer array;

  public ArrayBatchIterator(MappeableArrayContainer array) {
    wrap(array);
  }

  @Override
  public int next(int key, int[] buffer) {
    int consumed = 0;
    CharBuffer data = array.content;
    while (consumed < buffer.length && index < array.getCardinality()) {
      buffer[consumed++] = key + (data.get(index++));
    }
    return consumed;
  }

  @Override
  public boolean hasNext() {
    return index < array.getCardinality();
  }

  @Override
  public ContainerBatchIterator clone() {
    try {
      return (ContainerBatchIterator)super.clone();
    } catch (CloneNotSupportedException e) {
      // won't happen
      throw new IllegalStateException(e);
    }
  }

  @Override
  public void releaseContainer() {
    array = null;
  }

  @Override
  public void advanceIfNeeded(char target) {
    int position = unsignedBinarySearch(array.content, 0, array.getCardinality(), target);
    this.index = position < 0 ? (-position - 1) : position;
  }

  public void wrap(MappeableArrayContainer array) {
    this.array = array;
    this.index = 0;
  }
}
