package org.bitlap.roaringbitmap.realdata.state;

import org.openjdk.jmh.annotations.*;
import org.bitlap.roaringbitmap.RoaringBitmap;
import org.bitlap.roaringbitmap.ZipRealDataRetriever;
import org.bitlap.roaringbitmap.buffer.ImmutableRoaringBitmap;

import java.util.Arrays;
import java.util.stream.StreamSupport;

import static org.bitlap.roaringbitmap.RealDataset.*;

@State(Scope.Benchmark)
public class RealDataRoaringBitmaps {

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
    ZipRealDataRetriever dataRetriever = new ZipRealDataRetriever(dataset);
    bitmaps = StreamSupport.stream(dataRetriever.fetchBitPositions().spliterator(), false)
            .map(RoaringBitmap::bitmapOf)
            .toArray(RoaringBitmap[]::new);
    immutableRoaringBitmaps = Arrays.stream(bitmaps).map(RoaringBitmap::toMutableRoaringBitmap)
            .toArray(ImmutableRoaringBitmap[]::new);
  }

  public RoaringBitmap[] getBitmaps() {
    return bitmaps;
  }

  public ImmutableRoaringBitmap[] getImmutableRoaringBitmaps() {
    return immutableRoaringBitmaps;
  }
}
