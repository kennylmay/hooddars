package dars;

public final class NodeAttributes {

  public final String id;
  public final int x;
  public final int y;
  public final int range;
  public final boolean isPromiscuous;
  public final boolean isDroppingMessages;
  public final boolean isOverridingHops;
  public final boolean isChangingMessages;
  public final int hops;
  public boolean isMaliciousNode = false;

  public NodeAttributes(String id, int x, int y, int range, boolean isPromiscuous, boolean isDroppingMessages, boolean isOverridingHops, int hops, boolean isChangingMessages) {
    this.id = id;
    this.x = x;
    this.y = y;
    this.range = range;
    this.isPromiscuous = isPromiscuous;
    this.isDroppingMessages = isDroppingMessages;
    this.isOverridingHops = isOverridingHops;
    this.hops = hops;
    this.isChangingMessages = isChangingMessages;
    if( this.isPromiscuous || this.isDroppingMessages || this.isOverridingHops || this.isChangingMessages ){
      this.isMaliciousNode = true;
    }
  }
 
  //Copy constructor
  public NodeAttributes(NodeAttributes ni) {
    this(ni.id, ni.x, ni.y, ni.range, ni.isPromiscuous, ni.isDroppingMessages, ni.isOverridingHops, ni.hops, ni.isChangingMessages);
  }

  // Hide the no arg constructor.
  @SuppressWarnings("unused")
  private NodeAttributes() {
    //Put values in so compiler doesn't complain
    this.id = "";
    this.x = 0;
    this.y = 0;
    this.range = 0;
    this.isPromiscuous = false;
    this.isDroppingMessages = false;
    this.isMaliciousNode = false;
    this.isOverridingHops = false;
    this.isChangingMessages = false;
    this.hops = 1;
  }

}
