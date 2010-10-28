package dars.proto.aodv;

/**
 * AODV Node Routing Table Entry
 * 
 * This class represents a single entry in a routing table.
 * 
 * From RFC 3561 Section 2 Paragraph 6 
 *
 * @author kresss
 */
public class RouteEntry {

	/** 
	 * Valid States for a Route Entry
	 */
	public enum StateFlags {
	    /**
	     * The entry is believed Viable.
	     */
		VALID,
		/**
		 * The entry is known to be flawed but has not been removed yet.
		 */
		INVALID, 
		/**
		 * The entry is broken but a repair operation is possible.
		 */
		REPAIRABLE, 
		/**
		 * The entry is broken but the repair procedure is underway. 
		 */
		REPAIRING,
		/**
		 * The entry was valid but has expired but should not be deleted yet.
		 * 
		 * TODO: This state was not clearly called for in the RFC.
		 * 
		 * An expired routing table entry SHOULD NOT be expunged before
		 * (current_time + DELETE_PERIOD).
		 * 
		 * Maybe this state should really be REPAIRABLE.
		 */
		EXPIRED
	}

	
	
	// TODO: Need some code here.
	
	
	/**
	 * Destination IP Address for the Destination Node
	 */
	private String DestIP;
	/**
	 * Destination Sequence Number
	 */
	private int SeqNum;
	/**
	 * Current State of the Routing Table Entry
	 */
	private StateFlags State;
	/**
	 * Hop Count to the Destination Node
	 */
	private int HopCount;
	/**
	 * IP Address of the Next Hop Node on the path to the Destination Node.
	 */
	private String NextHopIP;
	/**
	 * List of "Precursor" Node's IP Addresses.
	 * Simply put this is a list of Nodes that need to be notified if this route breaks.
	 */
	private String PrecursorIPs[];
	/**
	 * Time, in milliseconds for which this entry is to be considered valid.
	 */
	private long Lifetime;

}
