package org.bitlap.roaringbitmap.realdata.wrapper;

import org.bitlap.roaringbitmap.IntIterator;

final class RoaringIteratorWrapper implements BitmapIterator {

  private final IntIterator iterator;

  RoaringIteratorWrapper(IntIterator iterator) {
    this.iterator = iterator;
  }

  @Override
  public boolean hasNext() {
    return iterator.hasNext();
  }

  @Override
  public int next() {
    return iterator.next();
  }

}
