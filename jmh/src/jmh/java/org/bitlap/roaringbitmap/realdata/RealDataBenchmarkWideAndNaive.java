package org.bitlap.roaringbitmap.realdata;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.bitlap.roaringbitmap.realdata.state.RealDataBenchmarkState;
import org.bitlap.roaringbitmap.realdata.wrapper.BitmapAggregator;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class RealDataBenchmarkWideAndNaive {

  @Benchmark
  public int wideAnd_naive(RealDataBenchmarkState bs) {
    BitmapAggregator aggregator = bs.bitmaps.get(0).naiveAndAggregator();
    return aggregator.aggregate(bs.bitmaps).cardinality();
  }

}
