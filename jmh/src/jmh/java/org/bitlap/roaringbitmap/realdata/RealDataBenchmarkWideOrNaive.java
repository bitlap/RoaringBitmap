package org.bitlap.roaringbitmap.realdata;

import java.util.concurrent.TimeUnit;

import org.bitlap.roaringbitmap.realdata.state.RealDataBenchmarkState;
import org.bitlap.roaringbitmap.realdata.wrapper.BitmapAggregator;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class RealDataBenchmarkWideOrNaive {

  @Benchmark
  public int wideOr_naive(RealDataBenchmarkState bs) {
    BitmapAggregator aggregator = bs.bitmaps.get(0).naiveOrAggregator();
    return aggregator.aggregate(bs.bitmaps).cardinality();
  }

}