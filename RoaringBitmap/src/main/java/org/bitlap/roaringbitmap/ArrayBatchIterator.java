package org.bitlap.roaringbitmap;


import static org.bitlap.roaringbitmap.Util.unsignedBinarySearch;

public final class ArrayBatchIterator implements ContainerBatchIterator {

  private int index = 0;
  private ArrayContainer array;

  public ArrayBatchIterator(ArrayContainer array) {
    wrap(array);
  }

  @Override
  public int next(int key, int[] buffer) {
    int consumed = 0;
    char[] data = array.content;
    while (consumed < buffer.length && index < array.getCardinality()) {
      buffer[consumed++] = key + (data[index++]);
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

  void wrap(ArrayContainer array) {
    this.array = array;
    this.index = 0;
  }
}
