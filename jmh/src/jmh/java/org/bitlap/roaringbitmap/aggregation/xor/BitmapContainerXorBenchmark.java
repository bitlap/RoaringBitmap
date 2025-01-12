package org.bitlap.roaringbitmap.aggregation.xor;

import org.openjdk.jmh.annotations.*;
import org.bitlap.roaringbitmap.BitmapContainer;
import org.bitlap.roaringbitmap.Container;

import java.util.concurrent.ThreadLocalRandom;

@State(Scope.Benchmark)
public class BitmapContainerXorBenchmark {

  @Param({"0.1", "0.5"})
  double thisDensity;
  @Param({"0.1", "0.5"})
  double thatDensity;

  private BitmapContainer modified;
  private BitmapContainer parameter;

  @Setup(Level.Trial)
  public void setup() {
    modified = new BitmapContainer();
    parameter = new BitmapContainer();
    while (modified.getCardinality() < 0x10000 * thisDensity) {
      modified.add((char) ThreadLocalRandom.current().nextInt(0x10000));
    }
    while (parameter.getCardinality() < 0x10000 * thatDensity) {
      parameter.add((char) ThreadLocalRandom.current().nextInt(0x10000));
    }
  }

  @Benchmark
  public Container stable() {
    return modified.clone().ixor(parameter);
  }

  @Benchmark
  public Container tendToZero() {
    return modified.ixor(parameter);
  }
}
