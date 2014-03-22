package dars.proto;

/**
 * Entry Class for the Message Wait Queue
 * 
 * @author kresss
 * 
 */
public class WaitQueueEntry {

  public String SourceID;
  // type.
  public String DestinationID;
  public String MsgString;
  public int    TimeToLive;
  public boolean ReplayMessage;

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
  public WaitQueueEntry(String sourceID, String destinationID, String msgString,
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
  public WaitQueueEntry(String sourceID, String destinationID, String msgString,
      int timeToLive, boolean replayMessage) {
    super();
    SourceID = sourceID;
    DestinationID = destinationID;
    MsgString = msgString;
    TimeToLive = timeToLive;
    ReplayMessage = replayMessage;
  }
  
}
