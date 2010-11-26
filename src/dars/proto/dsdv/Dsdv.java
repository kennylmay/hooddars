package dars.proto.dsdv;

import javax.swing.JDialog;

import dars.Message;
import dars.NodeAttributes;
import dars.proto.Node;

public class Dsdv implements Node {

  /**
   * Constructor
   */
  public Dsdv(NodeAttributes atts) {
    // TODO implement constructor
  }

  /**
   * Constants needed by DSDV
   */

  /**
   * Send Updates Interval
   * 
   * Each node periodically sends routing table updates.  This is the interval between transmissions.  Messured in clock ticks.  
   */
  public static final int UpdateInterval = 25;
  
  
  
  /**
   * Private Member Fields
   */

  /**
   * Current Tick
   * 
   * Time is loosely defined in the simulation. This is the current tick count
   * for the node. Basically this is the node's time.
   */
  private int CurrentTick = 0;

  /**
   * Node Sequence Number
   * 
   * The node sequence number is a identifier that is unique across all protocol
   * control messages(RREQ, RREP, RERR) for a node. It is incremented
   * immediately before a protocol control message is generated.
   */
  private int LastSeqNum  = 1;

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
     * 
     */
    
    /**
     * From www.cs.jhu.edu/~cs647/dsdv.pdf
     * 
     * Each node periodically transmits updates to keep table consistency
     * Includes its own sequence number #, route table updates <dest_addr,
     * dest_seq#, hop-count>
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
     * When two routes to a destination received from two different neighbors
     * Choose the one with the greatest destination sequence number If equal,
     * choose the smallest hop-count
     */
  }
  

  /**
   * Public Member Functions Not part of the Node Interface
   */

  /**
   * Public Member Function Required to Implement the Node Interface
   */

  @Override
  public void clockTick() {
    // TODO Auto-generated method stub

  }

  @Override
  public NodeAttributes getAttributes() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public JDialog getNodeDialog() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean isPromiscuous() {
    // TODO Auto-generated method stub
    return false;
  }

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
  public void setAttributes(NodeAttributes atts) {
    // TODO Auto-generated method stub

  }

  @Override
  public void setPromiscuity(boolean value) {
    // TODO Auto-generated method stub

  }

  @Override
  public void setRange(int range) {
    // TODO Auto-generated method stub

  }

  @Override
  public void setXY(int x, int y) {
    // TODO Auto-generated method stub

  }

  @Override
  public void updateNodeDialog(JDialog dialog) {
    // TODO Auto-generated method stub

  }

}
