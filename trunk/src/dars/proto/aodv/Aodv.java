package dars.proto.aodv;

import dars.NodeAttributes;
import dars.proto.Node;

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
	

	public static final int ACTIVE_ROUTE_TIMEOUT = 3000; // Milliseconds
	public static final int HELLO_INTERVAL = 1000; // Milliseconds
	public static final int NODE_TRAVERSAL_TIME = 40; // Milliseconds

	public static final int ALLOWED_HELLO_LOSS = 2;
	public static final int LOCAL_ADD_TTL = 2;
	public static final int NET_DIAMETER = 35;
	public static final int RREQ_RETRIES = 2;
	public static final int RREQ_RATELIMIT = 10;
	public static final int TIMEOUT_BUFFER = 2;
	public static final int TTL_START = 1;
	public static final int TTL_INCREMENT = 2;
	public static final int TTL_THRESHOLD = 7;
	public static final int RERR_RATELIMIT = 10;

	public static final int MAX_REPAIR_TTL = (int)(0.3 * NET_DIAMETER);
	public static final int NET_TRAVERSAL_TIME = 2 * NODE_TRAVERSAL_TIME
			* NET_DIAMETER;
	public static final int BLACKLIST_TIMEOUT = RREQ_RETRIES
			* NET_TRAVERSAL_TIME;
	public static final int MY_ROUTE_TIMEOUT = 2 * ACTIVE_ROUTE_TIMEOUT;
	public static final int NEXT_HOP_WAIT = NODE_TRAVERSAL_TIME + 10;
	public static final int PATH_DISCOVERY_TIME = 2 * NET_TRAVERSAL_TIME;
	
	// Ring Traversal Time is dependent on TTL_VALUE.
	// Again I find myself not knowing what to do.
	// public static final int RING_TRAVERSAL_TIME = 2 * NODE_TRAVERSAL_TIME
	//		* (TTL_VALUE + TIMEOUT_BUFFER);
	
	/**
	 * DELETE_PERIOD = K * max (ACTIVE_ROUTE_TIMEOUT, HELLO_INTERVAL)
	 *     (K = 5 is recommended).
	 */
	public static final int DELETE_PERIOD = 5 * ACTIVE_ROUTE_TIMEOUT;
	
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
	 *            Preformated message to be transmitted.
	 */
	public void sendRawMessage() {

	}

	/**
	 * Receive raw message from the network.
	 * 
	 * This function will receive a message from the network then determine what
	 * type of message it is and call the appropriate message processing
	 * routine.
	 * 
	 * @author kresss
	 * @see org.dars.proto.node.receiveRawMessage
	 * 
	 * @param message
	 *            Message from the network simulation engine.
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
	 * @param
	 */
	void sendRREQ() {

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
    * This function will return the attributes that are defined in the Node class.
    * 
    * @author mayk
    * 
    * @return NodeAttributes
    * 
    * @param
    */
	public NodeAttributes getAttributes(){  
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
	public void setAttributes(NodeAttributes atts){
      att.locationx = atts.locationx;
      att.locationy = atts.locationy;
      att.range = atts.range;
   }
	
	public void clockTick(){
	   
	}
}
