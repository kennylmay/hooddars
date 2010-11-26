package dars.proto.dsdv;

public class RouteEntry {
  /**
   * Constants needed by DSDV
   */
  
  
  /**
   * Private Member Fields
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
   * Private Member Functions
   */
  
  
  /**
   * Public Member Functions Not part of the Node Interface
   */
  
}
