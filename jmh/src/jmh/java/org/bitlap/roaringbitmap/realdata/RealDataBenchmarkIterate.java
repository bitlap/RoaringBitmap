package org.bitlap.roaringbitmap.realdata;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.bitlap.roaringbitmap.realdata.state.RealDataBenchmarkState;
import org.bitlap.roaringbitmap.realdata.wrapper.Bitmap;
import org.bitlap.roaringbitmap.realdata.wrapper.BitmapIterator;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class RealDataBenchmarkIterate {

  @Benchmark
  public int iterate(RealDataBenchmarkState bs) {
    int total = 0;
    for (int k = 0; k < bs.bitmaps.size(); ++k) {
      Bitmap bitmap = bs.bitmaps.get(k);
      BitmapIterator i = bitmap.iterator();
      while (i.hasNext()) {
        total += i.next();
      }
    }
    return total;
  }

}
