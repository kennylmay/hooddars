package dars.proto.aodv;

import java.util.HashMap;
import java.util.Iterator;
import java.util.AbstractQueue;
import java.util.NoSuchElementException;

import dars.NodeAttributes;
import dars.proto.Node;
import dars.Message;
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
  public static final int DELETE_PERIOD = 5;

  // KRESSS - I am unsure if the constants listed below are needed for our implementation.
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
  //public static final int DELETE_PERIOD        = 5 * ACTIVE_ROUTE_TIMEOUT;

  // Don't think that MIN_REPAIR_TTL will really be needed.
  // public static final int MIN_REPAIR_TTL = ??

  // Don't think that TTL_VALUE is really a constant.
  // public static final int TTL_VALUE = ??

  /*
   * Functions that define the org.dars.proto.node interface.
   */

  /**
   * Pop a message of the node's transmit queue and return it.
   * 
   * This function is used to return a message off the transmit queue of a node
   * and return it for the simulation engine to consume. Effectively this is
   * used to simulate the transmittal of a message into the network.
   * 
   * @author kresss
   * @see dars.node.proto.node.messageToNetwork
   * 
   * @return Message Message that is being sent into the network. If there are
   *         no more messages Null is returned.
   */
  public Message messageToNetwork() {

    Message Msg;

    try {
      Msg = txQueue.remove();
    } catch (NoSuchElementException exception) {
      Msg = null;
    }

    return (Msg);
  }

  /**
   * Push a message into the node's receive queue.
   * 
   * This function is used to deliver a message to a node.  The message will be placed into the nodes receive queue effectively the node is receiving the message.
   * 
   * @author kresss
   * @see dars.node.proto.node.messageToNode
   * 
   * @param Message Message to be delivered to the node.
   * 
   */
  public void messageToNode(Message message){
    
  }
  
  /*
   * Functions that extend the org.dars.proto.node interface to make it unique
   * to aodv.
   */
  
  /**
   * Place message into the transmit queue.
   * 
   * This function sends a message into the network by adding it to the transmit
   * queue.
   * 
   * @author kresss
   * 
   * @param message
   *          Message to be transmitted.
   */
  private void sendMessage(Message message) {
    
  }
  
  /**
   * Take a message off the receive queue.
   * 
   * This function receives a message from the network by removing it from the
   * receive queue for processing.
   * 
   * @author kresss
   * 
   */
  private void receiveMessage() {
    
  }

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

    sendMessage(Msg);

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
   * Invalidate the RouteEntry for DestNodeID and any other entries that use
   * that entry for their next hop.
   * 
   * @author kresss
   * 
   * @param DestNodeID
   *          The Destination Node ID for the node that is no longer reachable.
   */
  void sendREER(String DestNodeID) {

    /**
     * RERR Message Format
     * 
     * TYPE|FLAGS|DESTCOUNT|DESTID1|DESTSEQID1|...|DESTIDX|DESTSEQX
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
    String MsgType = "RERR";
    String MsgFlags = "";
    int DestPairCount = 0;
    String IDSEQPairs = "";

    /**
     * Destination Route Table Entry
     */
    RouteEntry DestRouteEntry = RouteTable.get(DestNodeID);

    /**
     * Iterator and RouteEntry for going through the values in the RouteTable.
     */
    Iterator<RouteEntry> RouteTableIter;
    RouteEntry TempRouteEntry;

    /**
     * Add the DestNodeID and Sequence Number to the ID & Pair List
     */
    IDSEQPairs = DestNodeID + '|' + DestRouteEntry.getSeqNum();
    DestPairCount++;

    /**
     * Invalidate Route Entry in the Route Table and increase lifetime by delete
     * period.
     */
    DestRouteEntry.setState(RouteEntry.StateFlags.INVALID);
    DestRouteEntry.setLifetime(DestRouteEntry.getLifetime() + DELETE_PERIOD);
    RouteTable.put(DestNodeID, DestRouteEntry);

    /**
     * Get Iterator for RouteTable and then traverse it looking for nodes that
     * have DestNodeID as their next hop.
     */
    RouteTableIter = RouteTable.values().iterator();

    /**
     * Check each route entry in the route table to see if the next hop is the
     * error destination ID. If it then mark the Entry as Reparable. The Route
     * is marked Repairable because it is no longer valid but there may be
     * another path the the destination node for that route through a new next
     * hop.
     * 
     * Since the route is no longer known good. Add it to the DestNodeID and
     * Sequence Number to the ID & Pair List.
     */
    while (RouteTableIter.hasNext()) {
      TempRouteEntry = RouteTableIter.next();
      if (TempRouteEntry.getNextHopIP() == DestNodeID) {
        TempRouteEntry.setState(RouteEntry.StateFlags.REPAIRABLE);
        TempRouteEntry
            .setLifetime(TempRouteEntry.getLifetime() + DELETE_PERIOD);
        RouteTable.put(TempRouteEntry.getDestIP(), TempRouteEntry);

        IDSEQPairs += '|' + TempRouteEntry.getDestIP() + '|'
            + TempRouteEntry.getSeqNum();
        DestPairCount++;
      }
    }

    MsgStr = MsgType + '|' + MsgFlags + '|' + DestPairCount + '|' + IDSEQPairs;

    Msg = new Message(Message.BCAST_STRING, this.att.id, MsgStr);

    sendMessage(Msg);

  }

  /**
   * Send Route Acknowledgment Message.
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
   * Note, this returns a copy of the node attributes. Not a reference to the
   * attributes object itself.
   * @author mayk
   * 
   * @return NodeAttributes
   * 
   * @param
   */
  public NodeAttributes getAttributes() {
    return new NodeAttributes(att);
  }

  /**
   * Implements the setAttributes function that is defined in the Node Class.
   * 
   * This function will update the attributes for the node.
   * 
   *  Note, to be sure that no outside entity can modify the node attributes
   * object belonging to this instance, this method invokes the copy 
   * constructor of node attributes.
   * @author mayk
   * 
   * @return void
   * 
   * @param NodeAttributes
   */
  public void setAttributes(NodeAttributes atts) {
    this.att = new NodeAttributes(atts);
  }

  public void clockTick() {

    /**
     * Increment the CurrentTick for this time quantum.
     */
    CurrentTick++;
    
    //TODO: Need to determine what is needed for a cycle in a node.  
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
  private int                                 CurrentTick = 0;
  
  /**
   * Transmit Queue
   * 
   * Queue of messages that are waiting to be transmitted into the network.
   */
  private AbstractQueue<Message> txQueue;
  
  /**
   * Receive Queue
   * 
   * Queue of messages that have been received from the network. 
   */
  private AbstractQueue<Message> rxQueue;
  
}
