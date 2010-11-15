package dars.proto.aodv;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.HashSet;

// Exceptions
import java.util.NoSuchElementException;
import java.lang.IllegalStateException;

import javax.swing.JDialog;

import dars.NodeAttributes;
import dars.OutputHandler;
import dars.proto.Node;
import dars.Message;
import dars.proto.aodv.RouteEntry.StateFlags;
import dars.event.DARSEvent;
import dars.proto.aodv.WaitQueueEntry;

/**
 * AODV Node Class.
 * 
 * @author kresss
 * 
 */
public class Aodv implements Node {

  /**
   * Private Types that are needed for AODV
   */

  /**
   * Constants needed by the AODV Protocol
   */
  public static final int TTL_START            = 5;
  public static final int NET_DIAMETER         = 2 * TTL_START;
  public static final int NODE_TRAVERSAL_TIME  = 2;
  public static final int NET_TRAVERSAL_TIME   = 2 * NODE_TRAVERSAL_TIME
                                                   * NET_DIAMETER;
  public static final int PATH_DISCOVERY_TIME  = 2 * NET_TRAVERSAL_TIME;
  public static final int DELETE_PERIOD        = 5;
  public static final int ALLOWED_HELLO_LOSS   = 2;

  public static final int ACTIVE_ROUTE_TIMEOUT = 75;
  public static final int MY_ROUTE_TIMEOUT     = 2 * ACTIVE_ROUTE_TIMEOUT;

  /**
   * How often to send Hello Messages.
   * 
   * Hello_Interval is in the number of 'ticks'.
   * 
   * The value (25) for Hello_Interval is derived from the original 1000
   * millisecond hello interval specifed in RFC 3561 divided by the node
   * traversal time in the RFC.
   * 
   */
  public static final int HELLO_INTERVAL       = 25;                      // Ticks

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
   * This function is used to deliver a message to a node. The message will be
   * placed into the nodes receive queue effectively the node is receiving the
   * message.
   * 
   * @author kresss
   * @see dars.node.proto.node.messageToNode
   * 
   * @param message
   *          Message to be delivered to the node.
   * 
   */
  public void messageToNode(Message message) {

    try {
      rxQueue.add(message);
    } catch (IllegalStateException exception) {
      OutputHandler
          .dispatch(DARSEvent
              .outError(this.att.id
                  + " Failed to successfully receive message due to a full receive queue."));
    }

  }

  /**
   * Send a narrative message from one node to another.
   * 
   * Narrative messages are messages that the user inits.
   * 
   * @author kresss
   * 
   * @param srcID
   * @param destID
   * @param messageText
   */
  public void newNarrativeMessage(String srcID, String destID,
      String messageText) {

    /**
     * NARR Message Format
     * 
     * TYPE|FLAGS|TTL|DESTID|ORIGID|TEXT
     * 
     */

    /**
     * The message that will be sent.
     */
    Message Msg;

    /**
     * MsgStr will hold the message that is sent into the network.
     */
    String MsgStr = "";

    /**
     * Message Properties
     */
    String MsgType = "NARR";
    String MsgFlags = "";
    int MsgTTL = 0;
    String MsgOrigID = srcID;
    String MsgDestID = destID;

    /**
     * Route Table Entry used to get the destination ID info in our Route Table.
     */
    RouteEntry DestEntry;

    /**
     * Check to make sure that the sourceID is this node.
     */
    if (!this.att.id.equals(srcID)) {
      OutputHandler.dispatch(DARSEvent.outError(this.att.id
          + " Tried to create a new Narrative Message but the source ID was "
          + srcID));
    }

    /**
     * Build the Message String.
     * 
     * This is independent of which logic path is chosen below.
     */
    MsgStr = MsgType + '|' + MsgFlags + '|' + MsgTTL + '|' + MsgDestID + '|'
        + MsgOrigID + '|' + messageText;

    /**
     * Check to see if the destination node ID is in our Route Table.
     */
    if (RouteTable.containsKey(destID)) {
      /**
       * Get the Route Entry.
       */
      DestEntry = RouteTable.get(destID);
      /**
       * The destination is in our RouteTable. Make sure that the route is valid
       * and not too old.
       */
      if ((DestEntry.getState() == RouteEntry.StateFlags.VALID)
          && (DestEntry.getLifetime() >= this.CurrentTick)) {
        /**
         * Create the message and send it.
         */
        Msg = new Message(DestEntry.getNextHopIP(), this.att.id, MsgStr);
        sendMessage(Msg);

        OutputHandler.dispatch(DARSEvent.outDebug(MsgStr));
        /**
         * Done processing this request.
         */
        return;

      } else {
        /**
         * The route is stale. Repair it.
         * 
         * During this time the message will need to be placed on the WaitQueue.
         */
        sendRREQ(MsgDestID);
        addMessageToWaitQueue(MsgOrigID, MsgDestID, MsgStr);
      }

    } else {
      /**
       * Do not have a route to the destination node. Need to perform a Route
       * Request.
       */
      sendRREQ(MsgDestID);
      /**
       * Add the message into the wait queue. Already did the work to build the
       * message so we use the message string. We only need the destination ID
       * for the next iteration to figure out of we have a good route to send
       * to.
       */
      addMessageToWaitQueue(MsgOrigID, MsgDestID, MsgStr);

      OutputHandler.dispatch(DARSEvent.outDebug(this.att.id
          + " Wants to send to " + MsgDestID + " but has no Route."));

    }

  }

  /**
   * Return true if the nodes is listen only.
   * 
   * @author kresss
   * 
   * @return True/False based on the nodes Promiscuity
   */
  public boolean isPromiscuous() {
    return this.Promiscuous;
  }

  /**
   * Set whether or not a node is listen only.
   * 
   * @author kresss
   * 
   * @param value
   */
  public void setPromiscuity(boolean value) {
    this.Promiscuous = value;
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
    if (!this.Promiscuous) {
      try {
        txQueue.add(message);
      } catch (IllegalStateException exception) {
        OutputHandler
            .dispatch(DARSEvent
                .outError(this.att.id
                    + " Failed to successfully queue message to be sent due to a full transmit queue."));
      }
    }
  }

  /**
   * Take a message off the receive queue.
   * 
   * This function receives a message from the network by removing it from the
   * receive queue for processing.
   * 
   * @author kresss
   * 
   * @param message
   *          The message the is being received from the network.
   */
  private void receiveMessage(Message message) {

    OutputHandler.dispatch(DARSEvent.outDebug(this.att.id
        + " Received the following message text: " + message.message));

    String MsgType;

    /**
     * Check to see if this node sent the message. If it did then ignore the
     * message.
     */
    if (message.originId.equals(this.att.id)) {
      return;
    }

    /**
     * Get the message type.
     * 
     * Message type is always the first token in the message string. Split on
     * the '|' and get the first item in the resultant Array of Strings.
     */
    MsgType = message.message.split("\\|")[0];

    // TODO: Replace this terrible list of if statements with a switch statement
    // once Java 7 is released. Java 7 supposedly has the ability to switch on
    // strings.

    if (MsgType.equals("RREQ")) {
      receiveRREQ(message);
      return;
    }

    if (MsgType.equals("NARR")) {
      receiveNarrative(message);
      return;
    }

    if (MsgType.equals("RREP")) {
      receiveRREP(message);
      return;
    }

    if (MsgType.equals("RERR")) {
      receiveRERR(message);
      return;
    }

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
     * TYPE|FLAGS|TTL|HOPCOUNT|RREQID|DESTID|DESTSEQNUM|SRCID|SRCSEQNUM
     * 
     */

    /**
     * Message object that will be passed to sendMessage.
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
    int MsgHopCount = 0;
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
      this.RouteTable.put(MsgDestID, DestRouteEntry);
    } else {
      /**
       * There is already a RouteEntry in the RouteTable, but we are still
       * processing a RREQ. WHY?
       */
      if ((DestRouteEntry.getState() == StateFlags.RREQSENT)
          || DestRouteEntry.getState() == StateFlags.REPAIRING) {
        if (DestRouteEntry.getLifetime() > CurrentTick) {
          /**
           * A RREQ has already been sent for this node but a response has not
           * yet been received. Do not send another RREQ.
           */
          return;
        } else {
          /**
           * The last RREQ Failed. Send another.
           */
          DestRouteEntry.setState(RouteEntry.StateFlags.RREQSENT);
          DestRouteEntry.setLifetime(this.CurrentTick + PATH_DISCOVERY_TIME);
          this.RouteTable.put(MsgDestID, DestRouteEntry);
        }
      } else {

        DestRouteEntry.setState(RouteEntry.StateFlags.REPAIRING);
        DestRouteEntry.setLifetime(this.CurrentTick + PATH_DISCOVERY_TIME);

        this.RouteTable.put(MsgDestID, DestRouteEntry);
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
    MsgStr = MsgType + '|' + MsgFlags + '|' + MsgTTL + '|' + MsgHopCount + '|'
        + MsgRREQID + '|' + MsgDestID + '|' + MsgDestSeqNum + '|' + MsgSrcID
        + '|' + MsgSrcSeqNum;

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
   * @param message
   *          The Route Request message that was received.
   */
  void receiveRREQ(Message message) {
    /*
     * Break message string down into pieces. Check if the requested destination
     * node is in our RouteTable YES - Send RouteReply RREP, NO - Decrement TTL
     * and Forward on.
     */

    /**
     * RREQ Message Format
     * 
     * TYPE|FLAGS|TTL|HOPCOUNT|RREQID|DESTID|DESTSEQNUM|SRCID|SRCSEQNUM
     * 
     */

    /**
     * Message object that will be passed to sendMessage if the RREQ needs
     * forwarded.
     */
    Message Msg;
    /**
     * MsgStr will hold the message that is sent into the network.
     */
    String MsgStr = "";

    /**
     * Message Properties
     */
    String MsgType;
    String MsgFlags;
    int MsgTTL;
    int MsgHopCount;
    int MsgRREQID;
    String MsgDestID;
    int MsgDestSeqNum;
    String MsgSrcID;
    int MsgSrcSeqNum;

    /**
     * Route Table Entry used to look up the entries in the Route Table.
     */
    RouteEntry DestEntry;

    /**
     * Array to hold Message Fields
     */
    String MsgArray[];

    /**
     * Split Message into fields based on '|' delimiters and store in MsgArray.
     */
    MsgArray = message.message.split("\\|");

    /**
     * Store message fields into local variables. Yes this is not really needed
     * but I (SAK) think it makes the code more readable.
     */
    MsgType = MsgArray[0];
    MsgFlags = MsgArray[1];
    MsgTTL = Integer.parseInt(MsgArray[2]);
    MsgHopCount = Integer.parseInt(MsgArray[3]);
    MsgRREQID = Integer.parseInt(MsgArray[4]);
    MsgDestID = MsgArray[5];
    MsgDestSeqNum = Integer.parseInt(MsgArray[6]);
    MsgSrcID = MsgArray[7];
    MsgSrcSeqNum = Integer.parseInt(MsgArray[8]);

    /**
     * Check to see if this node is the originator of the RREQ.
     * 
     * This happens when a neighbor node re-broadcasts the RREQ and it creates a
     * rather interesting loop.
     */
    if (MsgSrcID.equals(this.att.id)) {
      /**
       * Don't process your own RREQ.
       */
      OutputHandler.dispatch(DARSEvent.outDebug(this.att.id
          + "Dropped message due to it being its own RREQ. Message String: "
          + message.message));
      return;
    }

    /**
     * Check to see if we have already processed this RREQID.
     * 
     * This happens because our neighbor may re-broadcast the RREQ. We don't
     * want to create a loop. That would be bad MMMKay.
     */
    if (this.RREQHistory.containsKey(MsgSrcID)) {
      if (this.RREQHistory.get(MsgSrcID) >= MsgRREQID) {
        /**
         * The RREQ is old. Ignore it.
         */
        OutputHandler.dispatch(DARSEvent.outDebug(this.att.id
            + " Dropped message due to old RREQID. Message String: "
            + message.message));
        return;
      }
    } else {
      this.RREQHistory.put(MsgSrcID, MsgRREQID);
    }

    /**
     * First add the sending node into our Route Table if it is not already
     * there.
     * 
     * RFC 3561 Section 6.5 Paragraph 1
     */
    DestEntry = RouteTable.get(message.originId);
    if (DestEntry == null) {
      DestEntry = new RouteEntry(message.originId, 0,
          RouteEntry.StateFlags.VALID, 1, message.originId, this.CurrentTick
              + MY_ROUTE_TIMEOUT);
      RouteTable.put(message.originId, DestEntry);
    }

    /**
     * If this node does not have a Route to the originator of the RREQ add the
     * Originator to the Route Table otherwise update the Originators Route
     * Entry.
     */
    DestEntry = RouteTable.get(MsgSrcID);
    if (DestEntry == null) {
      DestEntry = new RouteEntry(MsgSrcID, MsgSrcSeqNum,
          RouteEntry.StateFlags.VALID, MsgHopCount, message.originId,
          this.CurrentTick + MY_ROUTE_TIMEOUT);
      RouteTable.put(MsgSrcID, DestEntry);
    } else {
      /**
       * Update the Originator's Route Entry information if needed.
       */

      /**
       * If our route is not VALID take all of the message's info. This may be a
       * little over simplified from the RFC, but Meh. For now.
       */
      if (DestEntry.getState() != RouteEntry.StateFlags.VALID) {
        DestEntry.setSeqNum(MsgSrcSeqNum);
        DestEntry.setState(RouteEntry.StateFlags.VALID);
        DestEntry.setHopCount(MsgHopCount);
        DestEntry.setNextHopIP(message.originId);
        DestEntry.setLifetime(this.CurrentTick + MY_ROUTE_TIMEOUT);

        this.RouteTable.put(MsgSrcID, DestEntry);
      } else {
        /**
         * If the messages Originator sequence number is as new or newer (>=)
         * than ours and our hop count is >= the messages then update our info.
         * Else ours is better.
         */
        if ((MsgSrcSeqNum >= DestEntry.getSeqNum())
            && (DestEntry.getHopCount() >= MsgHopCount)) {
          /**
           * Update the Route Attributes.
           */
          DestEntry.setSeqNum(MsgSrcSeqNum);
          DestEntry.setHopCount(MsgHopCount);
          DestEntry.setNextHopIP(message.originId);
          DestEntry.setLifetime(this.CurrentTick + MY_ROUTE_TIMEOUT);

          this.RouteTable.put(MsgSrcID, DestEntry);
        }
      }
    }

    /**
     * If this node is the desired Destination Node we can service this RREQ.
     */
    if (this.att.id.equals(MsgDestID)) {
      sendRREP(MsgDestID, MsgSrcID, message.originId);
      /**
       * This RREQ has bee handled.
       */
      return;
    }

    /**
     * Check to see if the desired Destination ID is in this nodes Route Table.
     */
    if (RouteTable.containsKey(MsgDestID)) {
      /**
       * This node has knowledge of the desired destination node, but still need
       * to check more parameters before sending a RREP.
       * 
       * Get the Route Entry for the destination ID.
       */
      DestEntry = RouteTable.get(MsgDestID);

      /**
       * If the Route Entry is marked valid and the TTL is not passed then send
       * a RREP. Otherwise continue on and forward the RREQ. If all goes well
       * this node will get updated an updated route when it forwards back the
       * RREP.
       */
      if ((DestEntry.getState() == RouteEntry.StateFlags.VALID)
          && (DestEntry.getLifetime() >= this.CurrentTick)) {
        sendRREP(MsgDestID, MsgSrcID, message.originId);
        return;
      }
    }

    /**
     * This node can not service this RREQ directly. Forward on the RREQ into
     * the network.
     * 
     * Build and send an updated RREQ message.
     */

    /**
     * Reduce the Messages TTL by 1 since this node has processed it.
     */
    MsgTTL--;

    MsgStr = MsgType + '|' + MsgFlags + '|' + MsgTTL + '|' + MsgHopCount + '|'
        + MsgRREQID + '|' + MsgDestID + '|' + MsgDestSeqNum + '|' + MsgSrcID
        + '|' + MsgSrcSeqNum;

    /**
     * As long as the message has TTL forward it.
     */
    if (MsgTTL > 0) {
      Msg = new Message(Message.BCAST_STRING, this.att.id, MsgStr);
      sendMessage(Msg);
    } else {
      // TODO: Remove this code block it is for testing only? The building of
      // MsgStr above can then be moved inside the If to save some CPU time.

      OutputHandler.dispatch(DARSEvent.outDebug(this.att.id
          + "Dropped RREQ.  TTL Expired. MsgStr: " + MsgStr));
    }
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
  void sendRERR(String DestNodeID) {

    /**
     * RERR Message Format
     * 
     * TYPE|FLAGS|DESTCOUNT|DESTID1|DESTSEQID1|...|DESTIDX|DESTSEQX
     * 
     */

    /**
     * Message object that will be passed to sendMessage.
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
      if (TempRouteEntry.getNextHopIP().equals(DestNodeID)) {
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
   * Receive a Route Error Message.
   * 
   * @author kresss
   * 
   * @param message
   *          The Route Error Message that was received.
   */
  void receiveRERR(Message message) {

    /**
     * RERR Message Format
     * 
     * TYPE|FLAGS|DESTCOUNT|DESTID1|DESTSEQID1|...|DESTIDX|DESTSEQX
     * 
     */

    /**
     * Message Properties
     */
    int DestPairCount;

    String DestID;
    int DestSeqNum;

    /**
     * Route Table Entry used to add and modify the entries in the Route Table.
     */
    RouteEntry DestEntry;

    /**
     * Array to hold Message Fields
     */
    String MsgArray[];

    /**
     * Split Message into fields based on '|' delimiters and store in MsgArray.
     */
    MsgArray = message.message.split("\\|");
    /**
     * Store message fields into local variables.
     */
    DestPairCount = Integer.parseInt(MsgArray[2]);

    /**
     * Process the list of affected Destinations
     */
    for (int i = 0; i < DestPairCount; i++) {
      DestID = MsgArray[3 + (i * 2)];
      DestSeqNum = Integer.parseInt(MsgArray[3 + (i * 2) + 1]);

      if (this.RouteTable.containsKey(DestID)) {
        DestEntry = this.RouteTable.get(DestID);

        /**
         * Check to see if our Route is affected.
         */
        if ((DestEntry.getNextHopIP().equals(message.originId))
            && (DestEntry.getSeqNum() == DestSeqNum)
            && (DestEntry.getState() == RouteEntry.StateFlags.VALID)) {
          sendRERR(DestID);
        }
      }
    }

  }

  /**
   * Send Route Acknowledgment Message.
   * 
   * Send a route ack message as defined by RFC 3561 Section 5.4
   * 
   * @author kresss
   */
  void sendRREPACK() {
    // TODO: Don't think RREP ACK messages will be needed for the initial
    // implementation of DARS. RREP ACK's are requested if there is an
    // expectation of unidirectional links. Not so much in the problem domain
    // for initial releases of DARS.
  }

  /**
   * Send a Route Reply Message
   * 
   * @author kresss
   * 
   * @param DestNodeID
   *          The node that the RREQ was made for.
   * @param RequesterID
   *          The node that originated the RREQ.
   * @param SenderID
   *          The node that this node received the RREQ from.
   */
  void sendRREP(String DestNodeID, String RequesterID, String SenderID) {

    /**
     * RREP Message Format
     * 
     * TYPE|FLAGS|HOPCOUNT|DESTID|DESTSEQ|ORIGID|LIFETIME
     * 
     */

    /**
     * Destination Route Entry
     */
    RouteEntry DestEntry;
    /**
     * Temporary Precursors list for updating the Route Entry's List.
     */
    HashSet<String> PrecList;

    /**
     * Message object that will be passed to sendMessage.
     */
    Message Msg;
    /**
     * MsgStr will hold the message that is sent into the network.
     */
    String MsgStr = "";
    /**
     * Message Fields
     */
    String MsgType = "RREP";
    String MsgFlags = "";
    int MsgHopCount;
    String MsgDestID = DestNodeID;
    int MsgDestSeqNum;
    String MsgOrigID = RequesterID;
    int MsgLifetime;

    /**
     * If this node is the desired Destination Node just send the reply no need
     * to update a Precursors list.
     */
    if (this.att.id.equals(MsgDestID)) {

      /**
       * Zero Hops to get to here.
       */
      MsgHopCount = 0;
      /**
       * Increment our Last Sequence Number and pass it back.
       */
      MsgDestSeqNum = ++this.LastSeqNum;
      /**
       * LifeTime should be the normal Route LifeTime. This is different if
       * below when we look up an actual entry in the Route Table.
       * 
       * Note that this value is given in 'Ticks' but is an interval and does
       * not have a reference to this nodes current tick count.
       */
      MsgLifetime = MY_ROUTE_TIMEOUT;

      /**
       * Build Message Sting and Send.
       */
      MsgStr = MsgType + '|' + MsgFlags + '|' + MsgHopCount + '|' + MsgDestID
          + '|' + MsgDestSeqNum + '|' + MsgOrigID + '|' + MsgLifetime;

      Msg = new Message(SenderID, this.att.id, MsgStr);

      /**
       * Place the message into the txQueue.
       */
      sendMessage(Msg);

      /**
       * Done processing this control flow.
       */
      return;
    }

    /**
     * Find the Destination Node in our Route Table and reply with our Route.
     */

    if (!this.RouteTable.containsKey(DestNodeID)) {
      OutputHandler.dispatch(DARSEvent.outError(this.att.id
          + " Tried to send a RREP for a node that it "
          + "did not have a valid Route For."));

      /**
       * This node can not send a RREP for the Destination needed so drop the
       * message.
       */
      return;
    }

    DestEntry = this.RouteTable.get(DestNodeID);

    /**
     * Update the Route Entry's precursor's list to contain the destination node
     * that this RREP is being sent to as well as the next hop of this RREP.
     * 
     * MsgOrigID = The Original Creator of the RREQ. SenderID = The Node that
     * sent us the RREQ.
     */
    PrecList = DestEntry.getPrecursorIPs();

    PrecList.add(MsgOrigID);
    PrecList.add(SenderID);
    DestEntry.setPrecursorIPs(PrecList);

    /**
     * Set the message properties that were not known at initialization.
     * 
     * Must convert Lifetime into an interval instead of being node dependent.
     */
    MsgHopCount = DestEntry.getHopCount();
    MsgDestSeqNum = DestEntry.getSeqNum();
    MsgLifetime = DestEntry.getLifetime() - this.CurrentTick;

    /**
     * Build Message Sting and Send.
     */
    MsgStr = MsgType + '|' + MsgFlags + '|' + MsgHopCount + '|' + MsgDestID
        + '|' + MsgDestSeqNum + '|' + MsgOrigID + '|' + MsgLifetime;

    Msg = new Message(SenderID, this.att.id, MsgStr);

    /**
     * Place the message into the txQueue.
     */
    sendMessage(Msg);

  }

  /**
   * Receive a Route Reply Message
   * 
   * @author kresss
   */
  void receiveRREP(Message message) {

    /**
     * RREP Message Format
     * 
     * TYPE|FLAGS|HOPCOUNT|DESTID|DESTSEQ|ORIGID|LIFETIME
     * 
     */
    /**
     * Message object that will be passed to sendMessage. For forwarding on the
     * RREP.
     */
    Message Msg;
    /**
     * MsgStr will hold the message that is sent into the network.
     */
    String MsgStr = "";
    /**
     * Message Fields
     */
    String MsgType;
    String MsgFlags;
    int MsgHopCount;
    String MsgDestID;
    int MsgDestSeqNum;
    String MsgOrigID;
    int MsgLifetime;

    /**
     * Route Table Entry used to add and modify the entries in the Route Table.
     */
    RouteEntry DestEntry;
    /**
     * Temporary Precursors list for updating the Route Entry's List.
     */
    HashSet<String> PrecList;

    /**
     * Array to hold Message Fields
     */
    String MsgArray[];

    /**
     * Split Message into fields based on '|' delimiters and store in MsgArray.
     */
    MsgArray = message.message.split("\\|");

    /**
     * Store message fields into local variables. Yes this is not really needed
     * but I (SAK) think it makes the code more readable.
     */
    MsgType = MsgArray[0];
    MsgFlags = MsgArray[1];
    /**
     * Add 1 to the Hop Count for the hop that it took for the message to get to
     * this node.
     */
    MsgHopCount = Integer.parseInt(MsgArray[2]) + 1;
    MsgDestID = MsgArray[3];
    MsgDestSeqNum = Integer.parseInt(MsgArray[4]);
    MsgOrigID = MsgArray[5];
    MsgLifetime = Integer.parseInt(MsgArray[6]);

    /**
     * Special case for Hello Message.
     * 
     * If Destination ID and Original ID are Equal then this is a Hello Message.
     */
    if (MsgDestID.equals(MsgOrigID)) {
      /**
       * Check to see if the node sending the hello message is in our Route
       * Table.
       */
      if (this.RouteTable.containsKey(MsgDestID)) {
        /**
         * The sending node is in the receiving node's Route Table.
         */

        /**
         * Get the DestID's existing Route Table Entry.
         */
        DestEntry = this.RouteTable.get(MsgDestID);
        /**
         * Update the Sequence Number and Lifetime if it is newer.
         */
        if (DestEntry.getSeqNum() < MsgDestSeqNum) {
          DestEntry.setSeqNum(MsgDestSeqNum);
          DestEntry.setState(RouteEntry.StateFlags.VALID);
          DestEntry.setLifetime(this.CurrentTick + MY_ROUTE_TIMEOUT);

          /**
           * Put the updated Route Entry back into the Route Table.
           */
          this.RouteTable.put(MsgDestID, DestEntry);

          OutputHandler.dispatch(DARSEvent.outDebug(this.att.id + " Updated "
              + MsgDestID + " in its RouteTable"));
        }

      } else {
        /**
         * The sending node is NOT in the receiving node's Route Table. Add it.
         * 
         * The parameters for the RouteEntry are fairly straight forward except
         * for Lifetime. Lifetime is sent to us as an interval so add it to our
         * current tick count.
         */
        DestEntry = new RouteEntry(MsgDestID, MsgDestSeqNum,
            RouteEntry.StateFlags.VALID, MsgHopCount, MsgDestID,
            this.CurrentTick + MsgLifetime);

        this.RouteTable.put(MsgDestID, DestEntry);

        OutputHandler.dispatch(DARSEvent.outDebug(this.att.id + " Added "
            + MsgDestID + " to its RouteTable"));
      }

      /**
       * Fully processed the hello message.
       */
      return;
    }

    /**
     * Process a normal RREP
     */

    /**
     * If we sent the RREQ then update our Route Table.
     */
    if (MsgOrigID.equals(this.att.id)) {
      if (this.RouteTable.containsKey(MsgDestID)) {
        /**
         * Get the DestID's existing Route Table Entry.
         */
        DestEntry = this.RouteTable.get(MsgDestID);
        /**
         * Update the Route Attributes.
         */
        if (DestEntry.getSeqNum() <= MsgDestSeqNum) {
          DestEntry.setSeqNum(MsgDestSeqNum);
          DestEntry.setState(RouteEntry.StateFlags.VALID);
          DestEntry.setHopCount(MsgHopCount);
          DestEntry.setNextHopIP(message.originId);
          DestEntry.setLifetime(this.CurrentTick + MsgLifetime);

          /**
           * Put the updated Route Entry back into the Route Table.
           */
          this.RouteTable.put(MsgDestID, DestEntry);

          OutputHandler.dispatch(DARSEvent.outDebug(this.att.id + " Updated "
              + MsgDestID + " in its RouteTable"));
        }

      } else {
        /**
         * The RREQ that was sent out to generate this is really old. Drop the
         * message?
         */
        OutputHandler.dispatch(DARSEvent.outDebug(this.att.id
            + " Received a RREP to a REALLY old RREQ.  Dropping."));
      }

      /**
       * This node originated the RREQ that resulted in this RREP. No need to
       * forward it on so we are done processing this message.
       */
      return;

    }

    /**
     * Forward on the RREP
     * 
     * Update our Route Table. RFC 3561 Section 6.5 Things get crazy.
     */

    /**
     * Lookup the DestID of the RREP in our Route Table. If we do not already
     * have an entry create an entry, otherwise we have to make some decisions.
     */
    if (!this.RouteTable.containsKey(MsgDestID)) {
      /**
       * Create the Destination's Route Entry with all of the information from
       * the message and put it into this node's Route Table..
       */
      DestEntry = new RouteEntry(MsgDestID, MsgDestSeqNum,
          RouteEntry.StateFlags.VALID, MsgHopCount, message.originId,
          this.CurrentTick + MsgLifetime);

      this.RouteTable.put(MsgDestID, DestEntry);
    } else {
      /**
       * Check to see if this RREP has 'better' information than what is in our
       * Route Entry. 'Better' gets rather complicated.
       */

      /**
       * Get the DestID's existing Route Table Entry.
       */
      DestEntry = this.RouteTable.get(MsgDestID);

      /**
       * If our route is not VALID take all of the message's info. This may be a
       * little over simplified from the RFC, but Meh. For now.
       */
      if (DestEntry.getState() != RouteEntry.StateFlags.VALID) {
        DestEntry.setSeqNum(MsgDestSeqNum);
        DestEntry.setState(RouteEntry.StateFlags.VALID);
        DestEntry.setHopCount(MsgHopCount);
        DestEntry.setNextHopIP(message.originId);
        DestEntry.setLifetime(this.CurrentTick + MsgLifetime);

        this.RouteTable.put(MsgDestID, DestEntry);
      } else {
        /**
         * If the messages Destination sequence number is as new or newer (>=)
         * than ours and our hop count is >= the messages then update our info.
         * Else ours is better. We will still forward the other. Most likely we
         * already replied to the original RREQ, but don't change this RREP.
         */
        if ((MsgDestSeqNum >= DestEntry.getSeqNum())
            && (DestEntry.getHopCount() >= MsgHopCount)) {
          /**
           * Update the Route Attributes.
           */
          DestEntry.setSeqNum(MsgDestSeqNum);
          DestEntry.setHopCount(MsgHopCount);
          DestEntry.setNextHopIP(message.originId);
          DestEntry.setLifetime(this.CurrentTick + MsgLifetime);
          this.RouteTable.put(MsgDestID, DestEntry);
        }
      }

      /**
       * Update the Route Entry's precursor's list to contain the destination
       * node that this RREP is being sent to as well as the next hop of this
       * RREP.
       * 
       * MsgOrigID = The Original Creator of the RREQ. SenderID = The Node that
       * sent us the RREQ.
       */
      PrecList = DestEntry.getPrecursorIPs();
      PrecList.add(MsgOrigID);
      PrecList.add(this.RouteTable.get(MsgOrigID).getNextHopIP());
      DestEntry.setPrecursorIPs(PrecList);

    }

    /**
     * Build the RREP Message Sting and Forward on the message. This message
     * should be the same as this node received except for an increased hop
     * count.
     */
    MsgStr = MsgType + '|' + MsgFlags + '|' + MsgHopCount + '|' + MsgDestID
        + '|' + MsgDestSeqNum + '|' + MsgOrigID + '|' + MsgLifetime;

    /**
     * Lookup the origin of original RREQ so that we can decide how to forward
     * on this RREP. If we don't have a route just Broadcast it? Not sure how
     * that would happen.
     */
    if (this.RouteTable.containsKey(MsgOrigID)) {
      Msg = new Message(this.RouteTable.get(MsgOrigID).getNextHopIP(),
          this.att.id, MsgStr);
    } else {
      Msg = new Message(Message.BCAST_STRING, this.att.id, MsgStr);
      OutputHandler.dispatch(DARSEvent.outDebug(this.att.id
          + "Forwarding on RREP as a broadcast.  Look into this cast."));
    }

    /**
     * Place the message into the txQueue.
     */
    sendMessage(Msg);
  }

  /**
   * Send a Hello Message (Special RREQ)
   * 
   * This function will broadcast a hello message if it is time.
   * 
   * @author kresss
   */
  void sendHello() {

    /**
     * RREP Message Format
     * 
     * TYPE|FLAGS|HOPCOUNT|DESTID|DESTSEQ|ORIGID|LIFETIME
     * 
     */

    /**
     * Message object that will be passed to sendMessage.
     */
    Message Msg;
    /**
     * MsgStr will hold the message that is sent into the network.
     */
    String MsgStr = "";

    /**
     * Message Properties
     */
    String MsgType = "RREP";
    String MsgFlags = "";
    int MsgHopCount = 0;
    String MsgDestID = this.att.id;
    int MsgDestSeqNum = this.LastSeqNum;
    String MsgOrigID = this.att.id;
    int MsgLifetime = ALLOWED_HELLO_LOSS * HELLO_INTERVAL;

    /**
     * The node should only send out a Hello Message every Hello_Interval ticks.
     * If it is not time to send a new hello message yet then return with out
     * sending a message.
     */
    if ((HelloSentAt + HELLO_INTERVAL) > this.CurrentTick) {
      return;
    }

    /**
     * Build the message string that will be sent to the other nodes.
     */
    MsgStr = MsgType + '|' + MsgFlags + '|' + MsgHopCount + '|' + MsgDestID
        + '|' + MsgDestSeqNum + '|' + MsgOrigID + '|' + MsgLifetime;

    /**
     * Save the current time as the time of the last hello message that was
     * sent.
     */
    HelloSentAt = this.CurrentTick;

    Msg = new Message(Message.BCAST_STRING, this.att.id, MsgStr);

    /**
     * Place the message into the txQueue.
     */
    sendMessage(Msg);

    OutputHandler.dispatch(DARSEvent.outDebug(MsgStr));

  }

  /**
   * Receive Narrative Message.
   * 
   * @author kresss
   * 
   * @param message
   *          Narrative message received from network.
   */
  void receiveNarrative(Message message) {

    /**
     * NARR Message Format
     * 
     * TYPE|FLAGS|TTL|DESTID|ORIGID|TEXT
     * 
     */

    /**
     * The message that will be sent if the message needs forwarded.
     */
    Message Msg;

    /**
     * MsgStr will hold the message that is sent into the network.
     */
    String MsgStr = "";

    /**
     * Message Properties
     */
    String MsgType;
    String MsgFlags;
    int MsgTTL;
    String MsgOrigID;
    String MsgDestID;
    String MsgText;

    /**
     * Route Table Entry used to get the destination ID info in our Route Table.
     */
    RouteEntry DestEntry;

    /**
     * Array to hold Message Fields
     */
    String MsgArray[];

    /**
     * Split Message into fields based on '|' delimiters and store in MsgArray.
     */
    MsgArray = message.message.split("\\|");

    /**
     * Store message fields into local variables. Yes this is not really needed
     * but I (SAK) think it makes the code more readable.
     * 
     * Note: Skip MsgArray[0] - Message Type.
     */
    MsgType = MsgArray[0];
    MsgFlags = MsgArray[1];
    MsgTTL = Integer.parseInt(MsgArray[2]);
    MsgDestID = MsgArray[3];
    MsgOrigID = MsgArray[4];
    MsgText = MsgArray[5];

    /**
     * Check to see if this node is the final destination of the message. If it
     * is great if not we need to forward it.
     */
    if (this.att.id.equals(MsgDestID)) {
      /**
       * The message has reached its final destination. Consider it delivered.
       */
      OutputHandler.dispatch(DARSEvent.outMsgRecieved(MsgOrigID, MsgDestID,
          MsgText));
      return;
    }

    /**
     * Need to forward the message on.
     */

    /**
     * Check to see if the destination node ID is in our Route Table.
     */
    if (RouteTable.containsKey(MsgDestID)) {
      /**
       * Get the Route Entry.
       */
      DestEntry = RouteTable.get(MsgDestID);

      /**
       * Create the message string that will be sent.
       */
      MsgStr = MsgType + '|' + MsgFlags + '|' + MsgTTL + '|' + MsgDestID + '|'
          + MsgOrigID + '|' + MsgText;

      /**
       * The destination is in our RouteTable. Make sure that the route is valid
       * and not too old.
       */
      if ((DestEntry.getState() == RouteEntry.StateFlags.VALID)
          && (DestEntry.getLifetime() >= this.CurrentTick)) {

        /**
         * Create the message to be sent.
         */
        Msg = new Message(DestEntry.getNextHopIP(), this.att.id, MsgStr);
        sendMessage(Msg);

        OutputHandler.dispatch(DARSEvent.outDebug(this.att.id
            + " Forwarded Narrative Message: " + MsgStr));
        /**
         * Done processing this request.
         */
        return;

      } else {
        /**
         * The route is stale. Repair it.
         * 
         * During this time the message will need to be 'queued' on the
         * WaitQueue.
         */
        sendRREQ(MsgDestID);
        addMessageToWaitQueue(MsgOrigID, MsgDestID, MsgStr);
      }

    } else {
      /**
       * This node does not have a route to the desired destination and thus
       * should not have been used in the route. ERROR.
       */
      OutputHandler.dispatch(DARSEvent.outError(this.att.id
          + "Received a narrative message for " + MsgDestID
          + " but has no route to the destination.  Dropping message."));
    }

  }

  /**
   * Add a message (normally a narrative) onto the wait queue for later
   * processing.
   * 
   * @author kresss
   * 
   * @param srcID
   * @param destID
   * @param msgStr
   */
  private void addMessageToWaitQueue(String srcID, String destID, String msgStr) {

    /**
     * Create a new WaitQueueEntry with the message characteristics. Use the
     * PATH_DISCOVERY_TIME as the amount of time that the message is valid for
     * since the reason the message is normally placed onto the wait queue is
     * because the node is waiting on getting a Route Reply back which can take
     * a maximum of PATH_DISCOVERY_TIME to return.
     */
    WaitQueueEntry WaitEntry = new WaitQueueEntry(srcID, destID, msgStr,
        this.CurrentTick + PATH_DISCOVERY_TIME);
    try {
      waitQueue.add(WaitEntry);
    } catch (IllegalStateException exception) {
      OutputHandler.dispatch(DARSEvent.outError(this.att.id
          + " Failed to successfully add a message to the wait queue."));
    }

  }

  /**
   * Check all message in the wait queue and see if we can send them.
   * 
   * @author kresss
   */
  private void processWaitQueue() {

    /**
     * Message object used to build the message that will be sent out.
     */
    Message Msg;
    /**
     * Route Table Entry for looking up Route Entries
     */
    RouteEntry DestEntry;
    /**
     * Wait Queue Entry used for working with the Wait Queue
     */
    WaitQueueEntry WaitEntry;
    /**
     * Iterator for walking through the Wait Queue
     */
    Iterator<WaitQueueEntry> WaitQueueIter;

    WaitQueueIter = this.waitQueue.iterator();

    while (WaitQueueIter.hasNext()) {
      WaitEntry = WaitQueueIter.next();

      /**
       * Check to see if the message is still valid.
       */
      if (WaitEntry.TimeToLive < this.CurrentTick) {
        // TODO: If this node is not the Originator or the message send an error
        // back to the originator. ??

        OutputHandler
            .dispatch(DARSEvent
                .outError("Message lifetime expired while waiting for valid Route. Message: "
                    + WaitEntry.MsgString));
        WaitQueueIter.remove();
      }

      /**
       * Check the status of our Route Entry for the destination node of the
       * message.
       */
      if (this.RouteTable.containsKey(WaitEntry.DestinationID)) {
        DestEntry = this.RouteTable.get(WaitEntry.DestinationID);
        if (DestEntry.getState() == RouteEntry.StateFlags.VALID) {
          /**
           * Have a valid route to the desired destination of this message so
           * send it.
           */
          Msg = new Message(DestEntry.getNextHopIP(), this.att.id,
              WaitEntry.MsgString);
          sendMessage(Msg);

          /**
           * Clear the message off the wait queue.
           */
          WaitQueueIter.remove();
        }
      }
    }
  }

  /**
   * Check all routes in the Route Table for stale routes.
   * 
   * @author kresss
   */
  private void checkRouteTable() {

    /**
     * Iterator and RouteEntry for going through the values in the RouteTable.
     */
    Iterator<RouteEntry> RouteTableIter;
    RouteEntry TempRouteEntry;

    /**
     * Get Iterator for RouteTable and then traverse it.
     */
    RouteTableIter = this.RouteTable.values().iterator();

    /**
     * Check each route entry's Lifetime and State.
     */
    while (RouteTableIter.hasNext()) {
      TempRouteEntry = RouteTableIter.next();

      /**
       * See if the lifetime of a route has expired.
       */
      if (TempRouteEntry.getLifetime() <= this.CurrentTick) {
        /**
         * The lifetime has expired, so based on the current State of the route
         * determine the next action.
         */

        /**
         * For Routes marked as VALID, Expire them. All other states are a
         * result of this node being in a waiting position for this route. The
         * outside stimulus has not occurred with in the time out so remove the
         * route entry.
         */
        if (TempRouteEntry.getState() == RouteEntry.StateFlags.VALID) {
          TempRouteEntry.setState(RouteEntry.StateFlags.EXPIRED);
          TempRouteEntry.setLifetime(this.CurrentTick + DELETE_PERIOD);
          sendRERR(TempRouteEntry.getDestIP());
        } else {
          RouteTableIter.remove();
        }
      }
    }
  }

  /**
   * Implements the getAttributes function that is defined in the Node Class.
   * 
   * This function will return the attributes that are defined in the Node
   * class.
   * 
   * Note, this returns a copy of the node attributes. Not a reference to the
   * attributes object itself.
   * 
   * @author mayk
   * 
   * @return NodeAttributes
   */
  public NodeAttributes getAttributes() {
    return new NodeAttributes(att);
  }

  /**
   * Implements the setAttributes function that is defined in the Node Class.
   * 
   * This function will update the attributes for the node.
   * 
   * Note, to be sure that no outside entity can modify the node attributes
   * object belonging to this instance, this method invokes the copy constructor
   * of node attributes.
   * 
   * @author mayk
   * 
   * @param atts
   *          The new attributes for the node.
   */
  public void setAttributes(NodeAttributes atts) {
    this.att = new NodeAttributes(atts);
  }

  /**
   * Implements the setXY function that is defined in the Node class.
   * 
   * Sets the X and Y coordinate for this node.
   */
  public void setXY(int x, int y) {
    this.att.x = x;
    this.att.y = y;
  }

  /**
   * Implements the setRange function that is defined in the Node class.
   * 
   * Sets the range of this node.
   */
  public void setRange(int range) {
    this.att.range = range;
  }

  /**
   * Process an iteration of this node.
   * 
   * This will do all the processing for a node's time interval.
   * 
   * @author kresss
   */
  public void clockTick() {

    /**
     * Increment the CurrentTick for this time quantum.
     */
    CurrentTick++;

    /**
     * Receive and process each message on the Receive Queue.
     */
    while (!rxQueue.isEmpty()) {
      receiveMessage(rxQueue.remove());
    }

    /**
     * Try to send messages that are waiting on RREPs.
     */
    processWaitQueue();

    /**
     * Send Hello Message if it is time.
     */
    sendHello();

    checkRouteTable();
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
  private int                         LastRREQID  = 1;

  /**
   * Node Sequence Number
   * 
   * The node sequence number is a identifier that is unique across all protocol
   * control messages(RREQ, RREP, RERR) for a node. It is incremented
   * immediately before a protocol control message is generated.
   */
  private int                         LastSeqNum  = 1;

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
  private int                         CurrentTick = 0;

  /**
   * Transmit Queue
   * 
   * Queue of messages that are waiting to be transmitted into the network.
   */
  private Queue<Message>              txQueue     = new LinkedList<Message>();

  /**
   * Receive Queue
   * 
   * Queue of messages that have been received from the network.
   */
  private Queue<Message>              rxQueue     = new LinkedList<Message>();

  /**
   * Wait Queue
   * 
   * Queue of message that can not be sent yet. Most likely waiting for a RREP.
   */
  private LinkedList<WaitQueueEntry>  waitQueue   = new LinkedList<WaitQueueEntry>();

  /**
   * Route Request History
   * 
   * This hash map is used to track the last known sequence number request for a
   * given Destination ID.
   * 
   * HashMap <DestID, RREQID>
   */
  private HashMap<String, Integer>    RREQHistory = new HashMap<String, Integer>();

  /**
   * Last Tick That A Hello Message Was Sent At
   */
  private int                         HelloSentAt = 0;

  /**
   * Current Promiscuous mode.
   * 
   * True - Send and Receive
   * 
   * False - Receive (Listen) Only
   */
  private boolean                     Promiscuous = false;

  /**
   * getNodeDialog
   * 
   * This method will construct a JDialog from select node information to return
   * back to the GUI to be displayed.
   * 
   * @param void
   * 
   * @return JDialog
   * 
   */
  public JDialog getNodeDialog() {
    AodvDialog dialog = new AodvDialog(null, this.att.id, this.CurrentTick,
        this.RouteTable);
    return (JDialog) dialog;
  }

  /**
   * updateNodeDialog
   * 
   * This method will update an already constructed JDialog box that is already
   * being displayed by the GUI.
   * 
   * @param JDialog
   * 
   * @return void
   * 
   */
  public void updateNodeDialog(JDialog dialog) {
    // Cast the JDialog into our type
    AodvDialog aodvDlg = (AodvDialog) dialog;
    aodvDlg.updateInformation(this.CurrentTick, this.RouteTable);
  }

}
