/**
 * 
 */
package org.dars.proto;

/**
 * Base Class for all protocol specific node types.
 * 
 * @author kresss
 * 
 */
public abstract interface Node {

	/**
	 * Send raw message into the network.
	 * 
	 * This function sends a pre-formatted message into the network. A raw
	 * message should be thought of as the bits on a wire not text.
	 * 
	 * @author kresss
	 * 
	 * @param message
	 *            Pre-formatted message to be transmitted.
	 */
	void sendRawMessage();

	/**
	 * Receive raw message from the network.
	 * 
	 * This function will receive a message from the network then determine what
	 * type of message it is and call the appropriate message processing
	 * routine.
	 * 
	 * @author kresss
	 * 
	 * @param message
	 *            Message from the network simulation engine.
	 */
	void receiveRawMessage();
	
	

}
