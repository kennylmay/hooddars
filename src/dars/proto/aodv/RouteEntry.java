package dars.proto.aodv;

/**
 * AODV Node Routing Table Entry
 * 
 * This class represents a single entry in a routing table.
 * 
 * From RFC 3561 Section 2 Paragraph 6
 * 
 * @author kresss
 */
public class RouteEntry {
  /**
   * @param destIP
   * @param state
   * @param lifetime
   */
  public RouteEntry(String destIP, StateFlags state, int lifetime) {
    super();
    DestIP = destIP;
    State = state;
    Lifetime = lifetime;

    SeqNum = 0;
    HopCount = 0;
  }

  /**
   * @param destIP
   * @param seqNum
   * @param state
   * @param hopCount
   * @param nextHopIP
   * @param lifetime
   */
  public RouteEntry(String destIP, int seqNum, StateFlags state, int hopCount,
      String nextHopIP, int lifetime) {
    super();
    DestIP = destIP;
    SeqNum = seqNum;
    State = state;
    HopCount = hopCount;
    NextHopIP = nextHopIP;
    Lifetime = lifetime;
  }

  /**
   * @return the destIP
   */
  public String getDestIP() {
    return DestIP;
  }

  /**
   * @param destIP
   *          the destIP to set
   */
  public void setDestIP(String destIP) {
    DestIP = destIP;
  }

  /**
   * @return the seqNum
   */
  public int getSeqNum() {
    return SeqNum;
  }

  /**
   * @param seqNum
   *          the seqNum to set
   */
  public void setSeqNum(int seqNum) {
    SeqNum = seqNum;
  }

  /**
   * @return the state
   */
  public StateFlags getState() {
    return State;
  }

  /**
   * @param state
   *          the state to set
   */
  public void setState(StateFlags state) {
    State = state;
  }

  /**
   * @return the hopCount
   */
  public int getHopCount() {
    return HopCount;
  }

  /**
   * @param hopCount
   *          the hopCount to set
   */
  public void setHopCount(int hopCount) {
    HopCount = hopCount;
  }

  /**
   * @return the nextHopIP
   */
  public String getNextHopIP() {
    return NextHopIP;
  }

  /**
   * @param nextHopIP
   *          the nextHopIP to set
   */
  public void setNextHopIP(String nextHopIP) {
    NextHopIP = nextHopIP;
  }

  /**
   * @return the precursorIPs
   */
  public String[] getPrecursorIPs() {
    return PrecursorIPs;
  }

  /**
   * @param precursorIPs
   *          the precursorIPs to set
   */
  public void setPrecursorIPs(String[] precursorIPs) {
    PrecursorIPs = precursorIPs;
  }

  /**
   * @return the lifetime
   */
  public int getLifetime() {
    return Lifetime;
  }

  /**
   * @param lifetime
   *          the lifetime to set
   */
  public void setLifetime(int lifetime) {
    Lifetime = lifetime;
  }

  /**
   * Valid States for a Route Entry
   */
  public enum StateFlags {
    /**
     * The entry is believed Viable.
     */
    VALID,
    /**
     * The entry is known to be flawed but has not been removed yet.
     */
    INVALID,
    /**
     * The entry is broken but a repair operation is possible.
     */
    REPAIRABLE,
    /**
     * The entry is broken but the repair procedure is underway.
     */
    REPAIRING,
    /**
     * The entry was valid but has expired but should not be deleted yet.
     * 
     * TODO: This state was not clearly called for in the RFC.
     * 
     * An expired routing table entry SHOULD NOT be expunged before
     * (current_time + DELETE_PERIOD).
     * 
     * Maybe this state should really be REPAIRABLE.
     */
    EXPIRED,
    /**
     * A route request has been sent for this destination ID, but no response
     * has been received.
     */
    RREQSENT
  }

  // TODO: Need some code here.

  /**
   * Destination IP Address for the Destination Node
   */
  private String     DestIP;
  /**
   * Destination Sequence Number
   */
  private int        SeqNum;
  /**
   * Current State of the Routing Table Entry
   */
  private StateFlags State;
  /**
   * Hop Count to the Destination Node
   */
  private int        HopCount;
  /**
   * IP Address of the Next Hop Node on the path to the Destination Node.
   */
  private String     NextHopIP;
  /**
   * List of "Precursor" Node's IP Addresses. Simply put this is a list of Nodes
   * that need to be notified if this route breaks.
   */
  private String     PrecursorIPs[];
  /**
   * Time, in milliseconds for which this entry is to be considered valid.
   */
  private int       Lifetime;

}
