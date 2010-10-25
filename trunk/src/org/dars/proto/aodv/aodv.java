package org.dars.proto.aodv;
import org.dars.proto.*;

/**
 * AODV Node Class
 * 
 * @author kresss
 *
 */
public class aodv implements node {

	/**
	 * Functions that define the org.dars.proto.node interface.
	 */
	
	/**
	 * Send raw message into the network.
	 * 
	 * This function sends a preformed message into the network.  
	 * A raw message should be thought of as the bits on a wire 
	 *  not text.
	 * 
	 * @author kresss
	 * @see org.dars.proto.node.sendRawMessage
	 * 
	 * @param message Preformated message to be transmitted.
	 */
	public void sendRawMessage() {
		
	}
	
	/**
	 * Receive raw message from the network.
	 * 
	 * This function will receive a message from the network then
	 *  determine what type of message it is and call the appropriate 
	 *  message processing routine.
	 *  
	 * @author kresss
	 * @see org.dars.proto.node.receiveRawMessage
	 * 
	 * @param message Message from the network simulation engine.
	 */
	public void receiveRawMessage(){
		
	}
	
	
	/**
	 * Functions that extend the org.dars.proto.node interface
	 *  to make it unique to aodv.
	 */
	
	/**
	 * Generate and send a Route Request Message.
	 *   
	 * Send a route request message as defined by
	 * RFC 3561 Section 5.1
	 * 
	 * @author kresss
	 * 
	 * @param 
	 */
	void sendRREQ() {
		
	}
	
	/**
	 * Receive and decode a Route Request Message.
	 * 
	 * Decode a Route Request Message as defined by 
	 * RFC 3561 Section 5.2
	 * 
	 * @author kresss
	 * 
	 * @param
	 */
	void receiveRREQ() {
		
	}
	
}
