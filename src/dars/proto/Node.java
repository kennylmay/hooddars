/**
 * 
 */
package dars.proto;

import dars.NodeAttributes;
import dars.Message;

/**
 * Base Class for all protocol specific node types.
 * 
 * @author kresss
 * 
 */
public abstract interface Node {
  
  /**
   * Pop a message of the node's transmit queue and return it.
   * 
   * This function is used to return a message off the transmit queue of a node and return it for the simulation engine to consume.  Effectively this is used to simulate the transmittal of a message into the network.
   * 
   * @author kresss
   * 
   * @return Message Message that is being sent into the network.
   */
  Message messageToNetwork();
  
  /**
   * Push a message into the node's receive queue.
   * 
   * This function is used to deliver a message to a node.  The message will be placed into the nodes receive queue effectively the node is receiving the message.
   * 
   * @author kresss
   * 
   * @param message Message to be delivered to the node.
   * 
   */
  void messageToNode(Message message);

  /**
   * Send a narrative message from one node to another.
   * 
   * Narrative messages are messages that the user inits.
   * 
   * @author kresss
   * 
   * @param message
   */
  void newNarrativeMessage(Message message);
  
  /**
   * Returns the Node's attributes
   * 
   * @return NodeAttributes
   */
  NodeAttributes getAttributes();

  /**
   * Sets the Node's attributes
   * 
   * @author mayk
   * 
   * @param atts The new attributes for the node.
   */
  void setAttributes(NodeAttributes atts);

  /**
   * Sets the X and Y coordinates of the node.
   * @param x The new x coordinate.
   * @param y The new y coordinate.
   */
  void setXY(int x, int y);
  
  /**
   * Sets the range of the node.
   * 
   * @param newRange
   */
  void setRange(int range);
  
  
  /**
   * Process an iteration of this node.
   * 
   * This will do all the processing for a node's time interval.
   * 
   * @author kresss
   */
  void clockTick();

}