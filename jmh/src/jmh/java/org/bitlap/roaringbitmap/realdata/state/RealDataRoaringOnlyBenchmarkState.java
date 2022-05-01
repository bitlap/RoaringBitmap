package org.bitlap.roaringbitmap.realdata.state;

import org.bitlap.roaringbitmap.RoaringOnlyBenchmarkState;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import static org.bitlap.roaringbitmap.RealDataset.*;

@State(Scope.Benchmark)
public class RealDataRoaringOnlyBenchmarkState extends RoaringOnlyBenchmarkState {

  @Param({// putting the data sets in alpha. order
      CENSUS_INCOME, CENSUS1881, DIMENSION_008, DIMENSION_003, DIMENSION_033, USCENSUS2000,
      WEATHER_SEPT_85, WIKILEAKS_NOQUOTES, CENSUS_INCOME_SRT, CENSUS1881_SRT, WEATHER_SEPT_85_SRT,
      WIKILEAKS_NOQUOTES_SRT})
  public String dataset;

  public RealDataRoaringOnlyBenchmarkState() {}

  @Setup
  public void setup() throws Exception {
    super.setup(dataset);
  }

}