package dars.proto.dsdv;

import java.util.HashMap;

import javax.swing.JDialog;

import dars.Message;
import dars.NodeAttributes;
import dars.proto.Node;
import dars.proto.dsdv.RouteEntry;

public class Dsdv extends Node {

  /**
   * **************************************************************************
   * *** Constants Needed by DSDV
   * **************************************************************************
   */

  // TODO: Adjust constant values after experimentation.

  /**
   * Send Updates Interval
   * 
   * Each node periodically sends routing table updates. This is the interval
   * between transmissions. Measured in clock ticks.
   */
  public static final int UPDATE_INTERVAL = 25;

  /**
   * Maximum Size of Network Protocol Data Unit(NPDU)
   * 
   * This is basically the maximum number of route updates that can be sent in
   * one message.
   */
  public static final int MAX_NPDU        = 10;

  /**
   * **************************************************************************
   * *** Private Member Fields
   * **************************************************************************
   */

  /**
   * Current Tick
   * 
   * Time is loosely defined in the simulation. This is the current tick count
   * for the node. Basically this is the node's time.
   */
  private int             CurrentTick    = 0;

  /**
   * Node Sequence Number
   * 
   * The node sequence number is a identifier that is unique across all protocol
   * control messages(RREQ, RREP, RERR) for a node. It is incremented
   * immediately before a protocol control message is generated.
   */
  private int             LastSeqNum     = 1;

  /**
   * Last Tick that a Route Table Update Message was Sent
   */
  private int             LastUpdate   = 0;
  
  /**
   * Route Table
   */
  private HashMap<String, RouteEntry> RouteTable  = new HashMap<String, RouteEntry>();

  /**
   * Private Member Functions
   */

  /**
   * Send Route Updates
   * 
   * 
   */
  void sendUpdates() {

    /**
     * Updates Message format.
     * 
     * TYPE|FLAGS|DESTCOUNT|DESTID1|SEQ1|HOPCOUNT1|...|DESTIDX|SEQX|HOPCOUNTX
     * 
     * TYPE = RTUP - Routing Table Update
     * 
     * FLAGS =
     * 
     * DESTCOUNT = Number of Destination Entries in the message.
     * 
     * DESTID = Destination ID
     * 
     * SEQ = Destination Sequence Number
     * 
     * HOPCOUNT = Hop Count from the Destination to the Sender of this Message.
     * (ie. The receiver of this message would add 1 to the hop count.)
     */

    /*
     * Find out how many routes have changed since the LastFullDump.
     * 
     * If there are more than MaxNPDU updates to the route table that are newer
     * than the last Full Dump.
     * 
     * - Send Full Dump
     * 
     * ELSE
     * 
     * - Send Incremental Update
     */

  }

  /**
   * Receive Route Updates
   * 
   * 
   */
  void receiveUpdates() {
    /**
     * Updates Message format.
     * 
     * 
     */

    /**
     * Process each entry in the update message.
     */
    
    /**
     * When two routes to a destination received from two different neighbors
     * Choose the one with the greatest destination sequence number If equal,
     * choose the smallest hop-count
     */
  }

  /**
   * **************************************************************************
   * *** Public Member Functions - Not in Node Interface
   * **************************************************************************
   */

  /**
   * Constructor
   */
  public Dsdv(NodeAttributes atts) {

    /**
     * Set Node Attributes
     */
    this.att = atts;

    /**
     * Add this node into it's own route table.
     * 
     * This kind of creates a functionality similar to AODV's Hello message as
     * the node sends out its first Update Message.
     */
    this.RouteTable.put(this.att.id, new RouteEntry(this.att.id,
        this.LastSeqNum, 0, this.att.id, this.CurrentTick));

  }

  /**
   * **************************************************************************
   * *** Public Member Functions - Implement Node Interface
   * **************************************************************************
   */

  @Override
  public Message messageToNetwork() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void messageToNode(Message message) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void newNarrativeMessage(String sourceID, String desinationID,
      String messageText) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void clockTick() {
    // TODO Auto-generated method stub
    
    if (this.CurrentTick >= (this.LastUpdate + this.UPDATE_INTERVAL)) {
      // TODO: Send Update Message
    }
    
  }

  @Override
  public JDialog getNodeDialog() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void updateNodeDialog(JDialog dialog) {
    // TODO Auto-generated method stub
    
  }


  /**
   * **************************************************************************
   * *** Public Member Functions - Implement Node Interface
   * **************************************************************************
   */

}
