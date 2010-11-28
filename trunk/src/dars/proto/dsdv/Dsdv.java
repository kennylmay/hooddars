package dars.proto.dsdv;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

import javax.swing.JDialog;

import dars.Message;
import dars.NodeAttributes;
import dars.OutputHandler;
import dars.event.DARSEvent;
import dars.proto.Node;
import dars.proto.aodv.AodvDialog;
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
  public static final int             UPDATE_INTERVAL = 25;

  /**
   * Maximum Size of Network Protocol Data Unit(NPDU)
   * 
   * This is basically the maximum number of route updates that can be sent in
   * one message.
   */
  public static final int             MAX_NPDU        = 10;

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
  private int                         CurrentTick     = 0;

  /**
   * Node Sequence Number
   * 
   * The node sequence number is a identifier that is unique across all protocol
   * control messages(RREQ, RREP, RERR) for a node. It is incremented
   * immediately before a protocol control message is generated.
   */
  private int                         LastSeqNum      = 1;

  /**
   * Last Tick that a Route Table Update Message was Sent
   */
  private int                         LastUpdate      = 0;

  /**
   * Last Tick that a Route Table Full Dump Update Message was Sent
   */
  private int                         LastFullUpdate  = -1;

  /**
   * Route Table
   */
  private HashMap<String, RouteEntry> RouteTable      = new HashMap<String, RouteEntry>();

  /**
   * Transmit Queue
   * 
   * Queue of messages that are waiting to be transmitted into the network.
   */
  private Queue<Message>              txQueue         = new LinkedList<Message>();

  /**
   * Receive Queue
   * 
   * Queue of messages that have been received from the network.
   */
  private Queue<Message>              rxQueue         = new LinkedList<Message>();

  /**
   * Private Member Functions
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

    if (!this.att.isPromiscuous) {
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
   * Send Route Updates
   * 
   * @author kresss
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

    this.LastUpdate = this.CurrentTick;

    /**
     * If more routes have changed since the last full update than can be sent
     * out in one update message then send out a full update, otherwise send out
     * an incremental update.
     */
    if (countChangesSinceTick(this.LastFullUpdate) > MAX_NPDU) {
      /**
       * Send Full Update
       */
      this.LastFullUpdate = this.CurrentTick;

    } else {
      /**
       * Send Incremental Update
       */
      sendIncrUpdates();

    }

  }

  /**
   * Send an Full Updates Message
   * 
   * Only send update entries for all route entries in the route table. This
   * will span multiple route update messages.
   * 
   * @author kresss
   */
  void sendFullUpdates() {

    // TODO: THIS NEEDS UNIT TESTED.

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
    String MsgType = "RTUP";
    String MsgFlags = "";
    int MsgDestCount = 0;
    String MsgDestEntries = "|"; /* DESTID1|SEQ1|HOPCOUNT1|... */

    /**
     * Iterator and RouteEntry for going through the values in the RouteTable.
     */
    Iterator<RouteEntry> RouteTableIter;
    RouteEntry TempRouteEntry;

    /**
     * Get Iterator for RouteTable and then traverse it looking for entries that
     * are newer than tick.
     */
    RouteTableIter = RouteTable.values().iterator();

    while (RouteTableIter.hasNext()) {
      TempRouteEntry = RouteTableIter.next();

      /**
       * Add this route entry to the Destination Entries List.
       */
      MsgDestCount++;
      MsgDestEntries = MsgDestEntries + '|' + TempRouteEntry.getDestIP() + '|'
          + TempRouteEntry.getSeqNum() + '|' + TempRouteEntry.getHopCount();

      /**
       * If the update message is full then send it and start a new message.
       */
      if (MsgDestCount == MAX_NPDU) {

        /**
         * Build the Message string. Note: MsgDestEntries starts with a '|' so
         * one is not inserted here.
         */
        MsgStr = MsgType + '|' + MsgFlags + '|' + MsgDestCount + MsgDestEntries;

        Msg = new Message(Message.BCAST_STRING, this.att.id, MsgStr);

        sendMessage(Msg);

        /**
         * Reset the message properties before continuing the loop.
         */
        MsgDestCount = 0;
        MsgDestEntries = "|";
      }
    }

    /**
     * If we have a partial update message left, send it.
     */
    if (MsgDestCount > 0) {
      /**
       * Build the Message string. Note: MsgDestEntries starts with a '|' so one
       * is not inserted here.
       */
      MsgStr = MsgType + '|' + MsgFlags + '|' + MsgDestCount + MsgDestEntries;

      Msg = new Message(Message.BCAST_STRING, this.att.id, MsgStr);

      sendMessage(Msg);

    }
  }

  /**
   * Send an Incremental Updates Message
   * 
   * Only send update entries for routes that have changed since the last full
   * update message.
   * 
   * @author kresss
   */
  void sendIncrUpdates() {

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
    String MsgType = "RTUP";
    String MsgFlags = "";
    int MsgDestCount = 0;
    String MsgDestEntries = ""; /* DESTID1|SEQ1|HOPCOUNT1|... */

    /**
     * Iterator and RouteEntry for going through the values in the RouteTable.
     */
    Iterator<RouteEntry> RouteTableIter;
    RouteEntry TempRouteEntry;

    /**
     * Get Iterator for RouteTable and then traverse it looking for entries that
     * are newer than tick.
     */
    RouteTableIter = RouteTable.values().iterator();

    while (RouteTableIter.hasNext()) {
      TempRouteEntry = RouteTableIter.next();

      /**
       * If this entry has been updated since LastFullUpdate then include it.
       */
      if (TempRouteEntry.getInstTime() > this.LastFullUpdate) {
        MsgDestCount++;
        MsgDestEntries = MsgDestEntries + '|' + TempRouteEntry.getDestIP()
            + '|' + TempRouteEntry.getSeqNum() + '|'
            + TempRouteEntry.getHopCount();
      }
    }

    /**
     * Build the Message string. Note: MsgDestEntries starts with a '|' so one
     * is not inserted here.
     */
    MsgStr = MsgType + '|' + MsgFlags + '|' + MsgDestCount + MsgDestEntries;

    Msg = new Message(Message.BCAST_STRING, this.att.id, MsgStr);

    sendMessage(Msg);

  }

  /**
   * Receive Route Updates
   * 
   * Process an update message and update the route table as needed.
   * 
   * @author kresss
   */
  void receiveUpdates(Message message) {
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

    /**
     * Process each entry in the update message.
     */

    /**
     * When two routes to a destination received from two different neighbors
     * Choose the one with the greatest destination sequence number If equal,
     * choose the smallest hop-count
     */

    /**
     * Message Properties
     */
    String MsgType;
    String MsgFlags;
    int MsgDestCount;

    /**
     * Message destination entries will be processed one at a time using these
     * variables.
     */
    String MsgDestEntryID;
    int MsgDestEntrySeq;
    int MsgDestEntryHopCount;

    /**
     * RouteEntry for building new and update old route entries.
     */
    RouteEntry TempRouteEntry;

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
    MsgDestCount = Integer.parseInt(MsgArray[2]);

    for (int i = 0; i < MsgDestCount; i++) {

      /**
       * MsgArray Index is calculated by the following formula.
       * 
       * i * n + b + j where
       * 
       * i = Index of the Destination in DestEntryList
       * 
       * n = Number of fields in a DestEntryList Entry(ID, Seq, HopCount)
       * 
       * b = Number of fields before the DestEntryList
       * 
       * j = Index of the desired field in the DestEntry(ID = 0, Seq = 1,
       * HopCount = 2)
       * 
       */
      MsgDestEntryID = MsgArray[i * 3 + 3];
      MsgDestEntrySeq = Integer.parseInt(MsgArray[i * 3 + 4]);
      MsgDestEntryHopCount = Integer.parseInt(MsgArray[i * 3 + 5]);

      /**
       * If the destination is not already in the route table, add it.
       */
      if (!this.RouteTable.containsKey(MsgDestEntryID)) {
        TempRouteEntry = new RouteEntry(MsgDestEntryID, MsgDestEntrySeq,
            MsgDestEntryHopCount, message.originId, this.CurrentTick);
        this.RouteTable.put(MsgDestEntryID, TempRouteEntry);
      } else {
        /**
         * Update the existing route entry if it needs it.
         */
        TempRouteEntry = this.RouteTable.get(MsgDestEntryID);

        /**
         * If the update's sequence number is newer than update ours.
         */
        if (TempRouteEntry.getSeqNum() < MsgDestEntrySeq) {
          TempRouteEntry.setHopCount(MsgDestEntryHopCount);
          TempRouteEntry.setNextHopIP(message.originId);
          TempRouteEntry.setSeqNum(MsgDestEntrySeq);
          TempRouteEntry.setInstTime(this.CurrentTick);

          this.RouteTable.put(MsgDestEntryID, TempRouteEntry);
        } else {
          /**
           * If the sequence number in the update is the same as ours and the
           * hop count is less then update.
           */
          if ((TempRouteEntry.getSeqNum() == MsgDestEntrySeq)
              && (TempRouteEntry.getHopCount() > MsgDestEntryHopCount)) {
            TempRouteEntry.setHopCount(MsgDestEntryHopCount);
            TempRouteEntry.setNextHopIP(message.originId);
            TempRouteEntry.setInstTime(this.CurrentTick);

            this.RouteTable.put(MsgDestEntryID, TempRouteEntry);
          }
        }

      }

    }
  }

  /**
   * Get the number of route table changes since the given tick..
   * 
   * 
   * 
   * @author kresss
   * 
   * @param tick
   *          Clock Tick that we will compare the route entry install times
   *          against.
   * 
   * @return The number of Route Table changes since the given tick.
   */
  private int countChangesSinceTick(int tick) {

    /**
     * Counting Variable.
     */
    int count = 0;

    /**
     * Iterator and RouteEntry for going through the values in the RouteTable.
     */
    Iterator<RouteEntry> RouteTableIter;
    RouteEntry TempRouteEntry;

    /**
     * Get Iterator for RouteTable and then traverse it looking for entries that
     * are newer than tick.
     */
    RouteTableIter = RouteTable.values().iterator();

    while (RouteTableIter.hasNext()) {
      TempRouteEntry = RouteTableIter.next();

      /**
       * If this entry has been updated since tick then count it.
       */
      if (TempRouteEntry.getInstTime() > tick) {
        count++;
      }
    }

    return (count);
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
  @Override
  public Message messageToNetwork() {

    Message Msg;
    String MsgType;

    try {
      Msg = txQueue.remove();
    } catch (NoSuchElementException exception) {
      return (null);
    }

    /**
     * Get the message type.
     * 
     * Message type is always the first token in the message string. Split on
     * the '|' and get the first item in the resultant Array of Strings.
     */
    MsgType = Msg.message.split("\\|")[0];

    // TODO: Replace this terrible list of if statements with a switch
    // statement once Java 7 is released. Java 7 supposedly has the ability
    // to switch on strings.

    /**
     * Switch on the Message type to send the DARS in events.
     */
    if (MsgType.equals("RTUP")) {
      OutputHandler.dispatch(DARSEvent.outControlMsgTransmitted(this.att.id,
          Msg));
    } else if (MsgType.equals("NARR")) {
      OutputHandler.dispatch(DARSEvent.outNarrMsgTransmitted(this.att.id, Msg));
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
  @Override
  public void messageToNode(Message message) {

    String MsgType;

    try {
      rxQueue.add(message);
    } catch (IllegalStateException exception) {
      OutputHandler
          .dispatch(DARSEvent
              .outError(this.att.id
                  + " Failed to successfully receive message due to a full receive queue."));
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

    if (MsgType.equals("RTUP")) {
      receiveUpdates(message);
      OutputHandler.dispatch(DARSEvent.outControlMsgReceived(this.att.id,
          message));
    } else if (MsgType.equals("NARR")) {
      // receiveNarrative(message);
      OutputHandler
          .dispatch(DARSEvent.outNarrMsgReceived(this.att.id, message));
    }

  }

  @Override
  public void newNarrativeMessage(String sourceID, String desinationID,
      String messageText) {
    // TODO Auto-generated method stub

  }

  @Override
  public void clockTick() {

    /**
     * Increment the CurrentTick for this time quantum.
     */
    this.CurrentTick++;

    if (this.CurrentTick >= (this.LastUpdate + this.UPDATE_INTERVAL)) {
      sendUpdates();
    }

  }

  /**
   * getNodeDialog
   * 
   * This method will construct a JDialog from select node information to return
   * back to the GUI to be displayed.
   * 
   * @return JDialog
   * 
   */
  @Override
  public JDialog getNodeDialog() {
    DsdvDialog dialog = new DsdvDialog(null, this.att.id, this.CurrentTick,
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
   */
  @Override
  public void updateNodeDialog(JDialog dialog) {
    // Cast the JDialog into our type
    DsdvDialog dsdvDlg = (DsdvDialog) dialog;
    dsdvDlg.updateInformation(this.CurrentTick, this.RouteTable);
  }

  /**
   * **************************************************************************
   * *** Public Member Functions - Implement Node Interface
   * **************************************************************************
   */

}
