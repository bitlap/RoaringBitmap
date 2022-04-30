package org.bitlap.roaringbitmap.needwork.state;

import org.bitlap.roaringbitmap.AbstractBenchmarkState;
import org.bitlap.roaringbitmap.RealDataset;
import org.bitlap.roaringbitmap.realdata.wrapper.BitmapFactory;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class NeedWorkBenchmarkState extends AbstractBenchmarkState {

  @Param({// putting the data sets in alpha. order
      RealDataset.CENSUS_INCOME, RealDataset.CENSUS1881, RealDataset.DIMENSION_008, RealDataset.DIMENSION_003, RealDataset.DIMENSION_033, RealDataset.USCENSUS2000,
      RealDataset.WEATHER_SEPT_85, RealDataset.WIKILEAKS_NOQUOTES, RealDataset.CENSUS_INCOME_SRT, RealDataset.CENSUS1881_SRT, RealDataset.WEATHER_SEPT_85_SRT,
      RealDataset.WIKILEAKS_NOQUOTES_SRT})
  public String dataset;

  @Param({BitmapFactory.ROARING, BitmapFactory.ROARING_WITH_RUN})
  public String type;

  @Param({"false", "true"})
  public boolean immutable;


  public NeedWorkBenchmarkState() {}

  @Setup
  public void setup() throws Exception {
    super.setup(dataset, type, immutable);
  }

}
