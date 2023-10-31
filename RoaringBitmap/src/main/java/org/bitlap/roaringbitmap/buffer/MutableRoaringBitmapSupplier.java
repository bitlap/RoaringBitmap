/*
 * (c) the authors Licensed under the Apache License, Version 2.0.
 */
package org.bitlap.roaringbitmap.buffer;

import org.bitlap.roaringbitmap.BitmapDataProvider;
import org.bitlap.roaringbitmap.BitmapDataProviderSupplier;

/**
 * A {@link BitmapDataProviderSupplier} providing {@link MutableRoaringBitmap} as
 * {@link BitmapDataProvider}
 * 
 * @author Benoit Lacelle
 *
 */
public class MutableRoaringBitmapSupplier implements BitmapDataProviderSupplier {

  @Override
  public BitmapDataProvider newEmpty() {
    return new MutableRoaringBitmap();
  }

}
