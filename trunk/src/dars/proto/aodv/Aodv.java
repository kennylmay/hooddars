package dars.proto.aodv;

import java.util.HashMap;

import dars.NodeAttributes;
import dars.proto.Node;
import dars.Message;
import dars.proto.aodv.*;
import dars.proto.aodv.RouteEntry.StateFlags;

/**
 * AODV Node Class.
 * 
 * @author kresss
 * 
 */
public class Aodv implements Node {

  /**
   * Constants needed by the AODV Protocol
   */
  public static final int TTL_START            = 5;
  public static final int NET_DIAMETER         = 35;
  public static final int NODE_TRAVERSAL_TIME  = 2;
  public static final int NET_TRAVERSAL_TIME   = 2 * NODE_TRAVERSAL_TIME
                                                   * NET_DIAMETER;
  public static final int PATH_DISCOVERY_TIME  = 2 * NET_TRAVERSAL_TIME;

  public static final int ACTIVE_ROUTE_TIMEOUT = 3000;                      // Milliseconds
  public static final int HELLO_INTERVAL       = 1000;                      // Milliseconds
  // public static final int NODE_TRAVERSAL_TIME = 40; // Milliseconds

  public static final int ALLOWED_HELLO_LOSS   = 2;
  public static final int LOCAL_ADD_TTL        = 2;

  public static final int RREQ_RETRIES         = 2;
  public static final int RREQ_RATELIMIT       = 10;
  public static final int TIMEOUT_BUFFER       = 2;
  // public static final int TTL_START = 1;
  public static final int TTL_INCREMENT        = 2;
  public static final int TTL_THRESHOLD        = 7;
  public static final int RERR_RATELIMIT       = 10;

  public static final int MAX_REPAIR_TTL       = (int) (0.3 * NET_DIAMETER);

  public static final int BLACKLIST_TIMEOUT    = RREQ_RETRIES
                                                   * NET_TRAVERSAL_TIME;
  public static final int MY_ROUTE_TIMEOUT     = 2 * ACTIVE_ROUTE_TIMEOUT;
  public static final int NEXT_HOP_WAIT        = NODE_TRAVERSAL_TIME + 10;

  // Ring Traversal Time is dependent on TTL_VALUE.
  // Again I find myself not knowing what to do.
  // public static final int RING_TRAVERSAL_TIME = 2 * NODE_TRAVERSAL_TIME
  // * (TTL_VALUE + TIMEOUT_BUFFER);

  /**
   * DELETE_PERIOD = K * max (ACTIVE_ROUTE_TIMEOUT, HELLO_INTERVAL) (K = 5 is
   * recommended).
   */
  public static final int DELETE_PERIOD        = 5 * ACTIVE_ROUTE_TIMEOUT;

  // Don't think that MIN_REPAIR_TTL will really be needed.
  // public static final int MIN_REPAIR_TTL = ??

  // Don't think that TTL_VALUE is really a constant.
  // public static final int TTL_VALUE = ??

  /**
   * Functions that define the org.dars.proto.node interface.
   */

  /**
   * Send raw message into the network.
   * 
   * This function sends a preformed message into the network. A raw message
   * should be thought of as the bits on a wire not text.
   * 
   * @author kresss
   * @see org.dars.proto.node.sendRawMessage
   * 
   * @param message
   *          Preformated message to be transmitted.
   */
  public void sendRawMessage(dars.Message message) {

  }

  /**
   * Receive raw message from the network.
   * 
   * This function will receive a message from the network then determine what
   * type of message it is and call the appropriate message processing routine.
   * 
   * @author kresss
   * @see org.dars.proto.node.receiveRawMessage
   * 
   * @param message
   *          Message from the network simulation engine.
   */
  public void receiveRawMessage() {

  }

  /**
   * Functions that extend the org.dars.proto.node interface to make it unique
   * to aodv.
   */

  /**
   * Generate and send a Route Request Message.
   * 
   * Send a route request message as defined by RFC 3561 Section 5.1
   * 
   * @author kresss
   * 
   * @param DestNodeID
   *          The Node ID for the destination that a route is needed for.
   */
  void sendRREQ(String DestNodeID) {

    /**
     * RREQ Message Format
     * 
     * TYPE|FLAGS|TTL|RREQID|DESTID|DESTSEQNUM|SRCID|SRCSEQNUM
     * 
     */

    /**
     * Message object that will be passed to SendRawMessage.
     */
    Message Msg;
    /**
     * MsgStr will hold the message that is sent into the network.
     */
    String MsgStr = "";

    /**
     * Message Properties
     */
    String MsgType = "RREQ";
    String MsgFlags = "";
    int MsgTTL = TTL_START;
    int MsgRREQID = ++this.LastRREQID;
    String MsgDestID = DestNodeID;
    int MsgDestSeqNum; // Must look this up in the RouteTable.
    String MsgSrcID = this.att.id;
    int MsgSrcSeqNum = ++this.LastSeqNum;

    /**
     * Destination Route Table Entry This is used to determine what the last
     * known sequence number for the destination and also if the source node has
     * already requested a route for the destination.
     */
    RouteEntry DestRouteEntry = RouteTable.get(MsgDestID);

    /**
     * If a RouteEntry does not exist for Destination ID then create one to
     * record that a RREQ was sent. Otherwise inspect the entry.
     */
    if (DestRouteEntry == null) {
      /**
       * Create a new Route Entry in the Route Table to record that a RREQ has
       * been sent.
       */
      DestRouteEntry = new RouteEntry(MsgDestID, StateFlags.RREQSENT,
          CurrentTick + PATH_DISCOVERY_TIME);
    } else {
      /**
       * There is already a RouteEntry in the RouteTable, but we are still
       * processing a RREQ. WHY?
       */
      if (DestRouteEntry.getState() == StateFlags.RREQSENT) {
        if (DestRouteEntry.getLifetime() > CurrentTick) {
          /**
           * A RREQ has already been sent for this node but a response has not
           * yet been received. Do not send another RREQ.
           */
          return;
        }
      } else {
        // TODO: There could be other possible StateFlags conditions at this
        // point.
        System.out.println("TODO: sendRREQ: StateFlags = "
            + DestRouteEntry.getState().toString());
      }

    }

    /**
     * Look up the last known Destination Sequence Number out of the route
     * table. If the destination sequence number == 0 and set the destination
     * sequence unknown flag(U).
     */
    MsgDestSeqNum = DestRouteEntry.getSeqNum();
    if (MsgDestSeqNum == 0) {
      MsgFlags += "U";
    }

    /**
     * Build the actual message string.
     */
    MsgStr = MsgType + '|' + MsgFlags + '|' + MsgTTL + '|' + MsgRREQID + '|'
        + MsgDestID + '|' + MsgDestSeqNum + '|' + MsgSrcID + '|' + MsgSrcSeqNum;

    Msg = new Message(Message.BCAST_STRING, MsgSrcID, MsgStr);

    sendRawMessage(Msg);

  }

  /**
   * Receive and decode a Route Request Message.
   * 
   * Decode a Route Request Message as defined by RFC 3561 Section 5.2
   * 
   * @author kresss
   * 
   * @param
   */
  void receiveRREQ() {

  }

  /**
   * Send Route Error Message.
   * 
   * Send a route error message as defined by RFC 3561 Section 5.3
   * 
   * @author kresss
   * 
   * @param
   */
  void sendREER() {

  }

  /**
   * Send Route Acknoledgement Message.
   * 
   * Send a route ack message as defined by RFC 3561 Section 5.4
   * 
   * @author kresss
   * 
   * @param
   */
  void sendRREPACK() {

  }

  /**
   * Implements the getAttributes function that is defined in the Node Class.
   * 
   * This function will return the attributes that are defined in the Node
   * class.
   * 
   * @author mayk
   * 
   * @return NodeAttributes
   * 
   * @param
   */
  public NodeAttributes getAttributes() {
    return att;
  }

  /**
   * Implements the setAttributes function that is defined in the Node Class.
   * 
   * This function will update the attributes for the node.
   * 
   * @author mayk
   * 
   * @return void
   * 
   * @param NodeAttributes
   */
  public void setAttributes(NodeAttributes atts) {
    this.att = atts;
  }

  public void clockTick() {

  }

  /**
   * Attributes member
   */
  NodeAttributes                      att         = new NodeAttributes();

  /**
   * Route Request ID
   * 
   * The RREQ ID must be unique to each route request sent out by a node. It is
   * incremented immediately before a route request is generated.
   */
  private int                         LastRREQID  = 0;

  /**
   * Node Sequence Number
   * 
   * The node sequence number is a identifier that is unique across all protocol
   * control messages(RREQ, RREP, RERR) for a node. It is incremented
   * immediately before a protocol control message is generated.
   */
  private int                         LastSeqNum  = 0;

  /**
   * Route Table
   */
  private HashMap<String, RouteEntry> RouteTable  = new HashMap<String, RouteEntry>();

  /**
   * Current Tick
   * 
   * Time is loosely defined in the simulation. This is the current tick count
   * for the node.
   */
  int                                 CurrentTick = 0;
}
