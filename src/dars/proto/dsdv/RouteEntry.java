package dars.proto.dsdv;

public class RouteEntry {
  
  /**
   * **************************************************************************
   * Constants needed by DSDV Route Entry
   * **************************************************************************
   */
  
  
  /**
   * **************************************************************************
   * Private Member Fields
   * **************************************************************************
   */


  /**
   * Destination IP Address for the Destination Node
   */
  private String     DestIP = "";
  /**
   * Destination Sequence Number
   */
  private int        SeqNum;
  /**
   * Hop Count to the Destination Node
   */
  private int        HopCount;
  /**
   * IP Address of the Next Hop Node on the path to the Destination Node.
   */
  private String     NextHopIP = "";
  /**
   * Time that route was installed into the table.
   */
  private int InstTime;
  
  /**
   * **************************************************************************
   * *** Private Member Fields
   * **************************************************************************
   */
    
  /**
   * **************************************************************************
   * *** Public Member Functions 
   * **************************************************************************
   */
  
  /**
   * Constructor with all fields defined.
   * 
   * @param destIP
   * @param seqNum
   * @param hopCount
   * @param nextHopIP
   * @param instTime
   */
  RouteEntry(String destIP, int seqNum, int hopCount, String nextHopIP,
      int instTime) {
    super();
    DestIP = destIP;
    SeqNum = seqNum;
    HopCount = hopCount;
    NextHopIP = nextHopIP;
    InstTime = instTime;
  }  
  
  
}
