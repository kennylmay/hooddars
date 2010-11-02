package dars;

public final class NodeAttributes {
  public String id;
  public int    x;
  public int    y;
  public int    range;

  // Copy constructor.
  public NodeAttributes(NodeAttributes ni) {
    id = ni.id;
    x = ni.x;
    y = ni.y;
    range = ni.range;
  }

  // No arg constructor
  public NodeAttributes() {

  }

}
