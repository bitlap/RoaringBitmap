package org.bitlap.roaringbitmap.realdata;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bitlap.roaringbitmap.FastAggregation;
import org.bitlap.roaringbitmap.ParallelAggregation;
import org.bitlap.roaringbitmap.RoaringBitmap;
import org.bitlap.roaringbitmap.ZipRealDataRetriever;
import org.bitlap.roaringbitmap.buffer.BufferFastAggregation;
import org.bitlap.roaringbitmap.buffer.BufferParallelAggregation;
import org.bitlap.roaringbitmap.buffer.ImmutableRoaringBitmap;
import org.bitlap.roaringbitmap.buffer.MutableRoaringBitmap;
import org.openjdk.jmh.annotations.*;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.stream.StreamSupport;

import static org.bitlap.roaringbitmap.RealDataset.*;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class ParallelAggregatorBenchmark {

  private static final Cache<String, RoaringBitmap[]> DATASET_CACHE =
          CacheBuilder.newBuilder().maximumSize(1).build();

  @Param({// putting the data sets in alpha. order
          CENSUS_INCOME, CENSUS1881, DIMENSION_008,
          DIMENSION_003, DIMENSION_033, USCENSUS2000,
          WEATHER_SEPT_85, WIKILEAKS_NOQUOTES, CENSUS_INCOME_SRT, CENSUS1881_SRT, WEATHER_SEPT_85_SRT,
          WIKILEAKS_NOQUOTES_SRT
  })
  public String dataset;

  RoaringBitmap[] bitmaps;
  ImmutableRoaringBitmap[] immutableRoaringBitmaps;

  @Setup(Level.Trial)
  public void setup() throws Exception {
    bitmaps = DATASET_CACHE.get(dataset, () -> {
      System.out.println("Loading" + dataset);
      ZipRealDataRetriever dataRetriever = new ZipRealDataRetriever(dataset);
      return StreamSupport.stream(dataRetriever.fetchBitPositions().spliterator(), false)
              .map(RoaringBitmap::bitmapOf)
              .toArray(RoaringBitmap[]::new);
    });
    immutableRoaringBitmaps = Arrays.stream(bitmaps).map(RoaringBitmap::toMutableRoaringBitmap)
            .toArray(ImmutableRoaringBitmap[]::new);
  }

  @Benchmark
  public RoaringBitmap parallelOr() {
    return ParallelAggregation.or(bitmaps);
  }

  @Benchmark
  public RoaringBitmap parallelXor() {
    return ParallelAggregation.xor(bitmaps);
  }

  @Benchmark
  public Object groupByKey() {
    return ParallelAggregation.groupByKey(bitmaps);
  }

  @Benchmark
  public RoaringBitmap fastOr() {
    return FastAggregation.or(bitmaps);
  }

  @Benchmark
  public RoaringBitmap fastAnd() {
    return FastAggregation.and(bitmaps);
  }

  @Benchmark
  public RoaringBitmap fastXor() {
    return FastAggregation.xor(bitmaps);
  }


  @Benchmark
  public MutableRoaringBitmap bufferParallelOr() {
    return BufferParallelAggregation.or(immutableRoaringBitmaps);
  }

  @Benchmark
  public MutableRoaringBitmap bufferParallelXor() {
    return BufferParallelAggregation.xor(immutableRoaringBitmaps);
  }

  @Benchmark
  public Object bufferGroupByKey() {
    return BufferParallelAggregation.groupByKey(immutableRoaringBitmaps);
  }

  @Benchmark
  public MutableRoaringBitmap bufferFastOr() {
    return BufferFastAggregation.or(immutableRoaringBitmaps);
  }

  @Benchmark
  public MutableRoaringBitmap bufferFastXor() {
    return BufferFastAggregation.xor(immutableRoaringBitmaps);
  }

}
