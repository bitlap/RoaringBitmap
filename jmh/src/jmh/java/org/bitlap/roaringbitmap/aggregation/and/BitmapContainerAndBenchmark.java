package org.bitlap.roaringbitmap.aggregation.and;

import org.bitlap.roaringbitmap.BitmapContainer;
import org.bitlap.roaringbitmap.Container;
import org.openjdk.jmh.annotations.*;

import java.util.SplittableRandom;
import java.util.concurrent.ThreadLocalRandom;

@State(Scope.Benchmark)
public class BitmapContainerAndBenchmark {

  @Param({"0.1", "0.5"})
  double thisDensity;
  @Param({"0.1", "0.5"})
  double thatDensity;

  private BitmapContainer modified;
  private BitmapContainer parameter;

  @Setup(Level.Trial)
  public void setup() {
    SplittableRandom random = new SplittableRandom(42);
    modified = new BitmapContainer();
    parameter = new BitmapContainer();
    while (modified.getCardinality() < 0x10000 * thisDensity) {
      modified.add((char) random.nextInt(0x10000));
    }
    while (parameter.getCardinality() < 0x10000 * thatDensity) {
      parameter.add((char) random.nextInt(0x10000));
    }
  }

  @Benchmark
  public Container stable() {
    return modified.clone().iand(parameter);
  }

  @Benchmark
  public Container tendToZero() {
    return modified.iand(parameter);
  }
}
