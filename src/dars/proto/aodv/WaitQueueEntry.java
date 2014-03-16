package dars.proto.aodv;

/**
 * Entry Class for the Message Wait Queue
 * 
 * @author kresss
 * 
 */
class WaitQueueEntry {

  String SourceID;
  // type.
  String DestinationID;
  String MsgString;
  int    TimeToLive;
  boolean ReplayMessage;

  /**
   * Constructor with all normal fields defined.
   * 
   * @author kresss
   * 
   * @param sourceID
   * @param destinationID
   * @param msgString
   * @param timeToLive
   */
  WaitQueueEntry(String sourceID, String destinationID, String msgString,
      int timeToLive) {
    super();
    SourceID = sourceID;
    DestinationID = destinationID;
    MsgString = msgString;
    TimeToLive = timeToLive;
    ReplayMessage = false;
  }
  
  /**
   * Constructor with all fields defined. This includes the malicious replay message.
   * 
   * @author kresss
   * 
   * @param sourceID
   * @param destinationID
   * @param msgString
   * @param timeToLive
   * @param replayMessage
   */
  WaitQueueEntry(String sourceID, String destinationID, String msgString,
      int timeToLive, boolean replayMessage) {
    super();
    SourceID = sourceID;
    DestinationID = destinationID;
    MsgString = msgString;
    TimeToLive = timeToLive;
    ReplayMessage = replayMessage;
  }
  
}
