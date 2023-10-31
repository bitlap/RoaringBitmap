package org.bitlap.roaringbitmap.realdata.wrapper;

public interface BitmapAggregator {

  Bitmap aggregate(Iterable<Bitmap> bitmaps);

}
