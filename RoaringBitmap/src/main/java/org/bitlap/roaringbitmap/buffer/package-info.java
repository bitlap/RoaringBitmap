/*
 * (c) the authors Licensed under the Apache License, Version 2.0.
 */



/**
 * The org.bitlap.roaringbitmap.buffer package  provides
 * two classes ({@link org.bitlap.roaringbitmap.buffer.MutableRoaringBitmap} and
 * ({@link org.bitlap.roaringbitmap.buffer.ImmutableRoaringBitmap}) that   users
 * can rely upon for fast set of integers.
 * It differs from the org.bitlap.roaringbitmap in that
 * the backing stores are ByteBuffers. 
 * 
 * Initially, one wants to construct a bitmap using
 * the MutableRoaringBitmap class. After serialization,
 * one can memory-map an ImmutableRoaringBitmap to the
 * serialized bytes, enabling off-heap processing.
 * 
 * <pre>
 * {@code
 *      import org.bitlap.roaringbitmap.buffer.*;
 *
 *      //...
 *
 *      MutableRoaringBitmap r1 = new MutableRoaringBitmap();
 *      for(int k = 4000; k<4255;++k) r1.add(k);
 *      
 *      MutableRoaringBitmap r2 = new MutableRoaringBitmap();
 *      for(int k = 1000; k<4255; k+=2) r2.add(k);
 *
 *      MutableRoaringBitmap union = ImmutableRoaringBitmap.or(r1, r2);
 *      MutableRoaringBitmap intersection = ImmutableRoaringBitmap.and(r1, r2);
 *
 *      //...
 *      DataOutputStream wheretoserialize = ...
 *      r1.runOptimize(); // can help compression
 *      r1.serialize(wheretoserialize);
 * }
 * </pre>
 *
 */
package org.bitlap.roaringbitmap.buffer;

