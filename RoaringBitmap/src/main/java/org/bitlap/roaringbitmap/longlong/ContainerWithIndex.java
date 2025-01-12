package org.bitlap.roaringbitmap.longlong;

import org.bitlap.roaringbitmap.Container;

public class ContainerWithIndex {

  private Container container;
  private long containerIdx;

  public ContainerWithIndex(Container container, long containerIdx) {
    this.container = container;
    this.containerIdx = containerIdx;
  }

  public Container getContainer() {
    return container;
  }

  public long getContainerIdx() {
    return containerIdx;
  }
}
