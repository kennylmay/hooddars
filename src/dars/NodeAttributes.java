package dars;

public final class NodeAttributes {
  public String id;
  public int    locationx;
  public int    locationy;
  public int    range;

  // Copy constructor.
  public NodeAttributes(NodeAttributes ni) {
    id = ni.id;
    locationx = ni.locationx;
    locationy = ni.locationy;
    range = ni.range;
  }

  // No arg constructor
  public NodeAttributes() {

  }

}
