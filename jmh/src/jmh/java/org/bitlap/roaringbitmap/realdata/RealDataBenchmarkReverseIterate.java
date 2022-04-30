package org.bitlap.roaringbitmap.realdata;

import java.util.concurrent.TimeUnit;

import org.bitlap.roaringbitmap.realdata.state.RealDataBenchmarkState;
import org.bitlap.roaringbitmap.realdata.wrapper.Bitmap;
import org.bitlap.roaringbitmap.realdata.wrapper.BitmapIterator;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class RealDataBenchmarkReverseIterate {

  @Benchmark
  public int reverseIterate(RealDataBenchmarkState bs) {
    int total = 0;
    for (int k = 0; k < bs.bitmaps.size(); ++k) {
      Bitmap bitmap = bs.bitmaps.get(k);
      BitmapIterator i = bitmap.reverseIterator();
      while (i.hasNext()) {
        total += i.next();
      }
    }
    return total;
  }

}
