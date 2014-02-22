/**
 * 
 */
package dars.event;

import java.lang.reflect.Field;

import dars.NodeAttributes;
import dars.Message;
import dars.SimulationTimeKeeper;
import dars.Utilities;
import dars.proto.NodeFactory;
import dars.proto.NodeFactory.NodeType;

/**
 * @author Mike
 */
public class DARSEvent {
  private static String newline = System.getProperty("line.separator");
  public enum EventType {
    // Input event types
    IN_ADD_NODE, IN_MOVE_NODE, IN_DEL_NODE, IN_SET_NODE_RANGE, IN_SET_NODE_PROMISCUITY, IN_SIM_SPEED, 
    IN_START_SIM, IN_PAUSE_SIM, IN_RESUME_SIM, IN_STOP_SIM, IN_CLEAR_SIM, IN_NEW_SIM, IN_INSERT_MESSAGE,
    IN_SET_NODE_DROP_MESSAGES,IN_SET_OVERRIDE_HOPS,IN_SET_HOPS_COUNT,
    
    // Output event types
    OUT_ADD_NODE, OUT_MOVE_NODE, OUT_DEL_NODE, OUT_SET_NODE_RANGE, OUT_SET_NODE_PROMISCUITY,  
    OUT_MSG_TRANSMITTED,  OUT_DEBUG, OUT_ERROR, OUT_START_SIM, OUT_PAUSE_SIM, OUT_RESUME_SIM, 
    OUT_STOP_SIM, OUT_SIM_SPEED, OUT_NEW_SIM, OUT_INSERT_MESSAGE, OUT_NARRMSG_RECEIVED, 
    OUT_CONTROLMSG_RECEIVED, OUT_NARRMSG_TRANSMITTED, OUT_CONTROLMSG_TRANSMITTED, 
    OUT_QUANTUM_ELAPSED, OUT_CLEAR_SIM, OUT_MSG_RECEIVED, OUT_NODE_INFO, OUT_SET_NODE_DROP_MESSAGES,
    OUT_NARRMSG_DROPPED, OUT_SET_OVERRIDE_HOPS, OUT_SET_HOPS_COUNT
  };

  public EventType            eventType;
  public String               nodeId;
  public String               sourceId;
  public String               destinationId;
  public String               informationalMessage;
  public String               transmittedMessage;
  public int                  newSimSpeed;
  public int                  nodeX;
  public int                  nodeY;
  public int                  nodeRange;
  public NodeFactory.NodeType nodeType;
  public long                 currentQuantum;
  public boolean              isPromiscuous;
  public boolean              isDroppingMessages;
  public boolean              isMalicious;
  public boolean              isOverridingHops;
  public int                  hops;
  private String              droppedMessage;
  

  //Provided for convenience.
  public NodeAttributes getNodeAttributes() {
    return new NodeAttributes(nodeId, nodeX, nodeY, nodeRange, isPromiscuous, isDroppingMessages, isOverridingHops, hops);
  }
  
//Provided for convenience.
  public void setNodeAttributes(NodeAttributes n) {
    nodeX = n.x;
    nodeY = n.y;
    nodeRange = n.range;
    nodeId = n.id;
    isPromiscuous = n.isPromiscuous;
  }


  private static SimulationTimeKeeper simTimeKeeper;
  public static void setSimTimeKeeper(SimulationTimeKeeper s) {
    simTimeKeeper = s;
  }
  
  // Hide the default constructor. DARSEvents can only be made through the
  // supplied functions that follow.
  private DARSEvent() {
    //If the time keeper is set, view the current time from it.
    if(simTimeKeeper != null) {
      currentQuantum = simTimeKeeper.getTime();
    }
  }

  public static DARSEvent inInsertMessage(Message message) {
    DARSEvent e = new DARSEvent();
    e.transmittedMessage = message.message;
    e.sourceId = message.originId;
    e.destinationId = message.destinationId;
    e.eventType = EventType.IN_INSERT_MESSAGE;
    return e;
  }
  
  public static DARSEvent outInsertMessage(String sourceID, String destID, String message) {
    DARSEvent e = new DARSEvent();
    e.eventType = EventType.OUT_INSERT_MESSAGE;
    e.informationalMessage = "User message inserted into the network. Source ID: " + sourceID + " Dest ID: " + destID + " Message: " + message;
    return e;
  }
  
  public static DARSEvent inClearSim() {
    DARSEvent e = new DARSEvent();
    e.eventType = EventType.IN_CLEAR_SIM;
    return e;
  }
  
  public static DARSEvent inNewSim(NodeType nt) {
    DARSEvent e = new DARSEvent();
    e.eventType = EventType.IN_NEW_SIM;
    e.nodeType = nt;
    return e;
  }
  
  public static DARSEvent outClearSim() {
    DARSEvent e = new DARSEvent();
    e.eventType = EventType.OUT_CLEAR_SIM;
    e.informationalMessage = "Nodes cleared.";
    return e;
  }
  
  public static DARSEvent inStartSim(int simSpeed) {
    DARSEvent e = new DARSEvent();
    e.eventType = EventType.IN_START_SIM;
    e.newSimSpeed = simSpeed;
    return e;
  }

  public static DARSEvent outStartSim(int simSpeed) {
    DARSEvent e = new DARSEvent();
    e.eventType = EventType.OUT_START_SIM;
    e.newSimSpeed = simSpeed;
    e.informationalMessage = "Simulation Started.";
    return e;
  }
  
  public static DARSEvent inStopSim() {
    DARSEvent e = new DARSEvent();
    e.eventType = EventType.IN_STOP_SIM; 
    return e;
  }
  
  public static DARSEvent outStopSim() {
    DARSEvent e = new DARSEvent();
    e.eventType = EventType.OUT_STOP_SIM;
    e.informationalMessage = "Simulation Stopped.";
    return e;
  }
  
  public static DARSEvent outNewSim(NodeType nt) {
    DARSEvent e = new DARSEvent();
    e.eventType = EventType.OUT_NEW_SIM;
    e.nodeType   = nt;
    e.informationalMessage = "New " + nt + " Simulation Created.";
    return e;
  }
  
  public static DARSEvent inPauseSim() {
    DARSEvent e = new DARSEvent();
    e.eventType = EventType.IN_PAUSE_SIM;
    return e;
  }
  
  public static DARSEvent outPauseSim() {
    DARSEvent e = new DARSEvent();
    e.eventType = EventType.OUT_PAUSE_SIM;
    e.informationalMessage = "Simulation Paused.";
    return e;
  }
  
  public static DARSEvent inResumeSim() {
    DARSEvent e = new DARSEvent();
    e.eventType = EventType.IN_RESUME_SIM;
    return e;
  }
  
  public static DARSEvent outResumeSim() {
    DARSEvent e = new DARSEvent();
    e.eventType = EventType.OUT_RESUME_SIM;
    e.informationalMessage = "Simulation Resumed.";
    return e;
  }
    
  public static DARSEvent inAddNode(int x, int y, int range, boolean isPromiscuous) {
    DARSEvent e = new DARSEvent();
    e.eventType = EventType.IN_ADD_NODE;
    e.nodeX = x;
    e.nodeY = y;
    e.nodeRange = range;
    e.isPromiscuous = isPromiscuous;
    return e;
  }

  public static DARSEvent inDeleteNode(String id) {
    DARSEvent e = new DARSEvent();
    e.eventType = EventType.IN_DEL_NODE;
    e.nodeId = id;
    return e;
  }

  public static DARSEvent inSetNodeRange(String id, int newRange) {
    DARSEvent e = new DARSEvent();
    e.eventType = EventType.IN_SET_NODE_RANGE;
    e.nodeId = id;
    e.nodeRange = newRange;
    return e;
  }
  

  public static DARSEvent inSimSpeed(int newSpeed) {
    DARSEvent e = new DARSEvent();
    e.eventType = EventType.IN_SIM_SPEED;
    e.newSimSpeed = newSpeed;
    return e;
  }
  
  public static DARSEvent outSimSpeed(int newSpeed) {
    DARSEvent e = new DARSEvent();
    e.eventType = EventType.OUT_SIM_SPEED;
    e.newSimSpeed = newSpeed;
    e.informationalMessage = "Simulation Speed Set: " + newSpeed + ".";
    return e;
  }

  public static DARSEvent inSetNodePromiscuity(String id, boolean isPromiscuous) {
    DARSEvent e = new DARSEvent();
    e.eventType = EventType.IN_SET_NODE_PROMISCUITY;
    e.isPromiscuous = isPromiscuous;
    e.nodeId = id;
    return e;
  }
  
  public static DARSEvent inSetNodeDropMessages(String id, boolean isDroppingMessages) {
    DARSEvent e = new DARSEvent();
    e.eventType = EventType.IN_SET_NODE_DROP_MESSAGES;
    e.isDroppingMessages = isDroppingMessages;
    e.nodeId = id;
    return e;
  }
  
  public static DARSEvent inSetNodeOverrideHops(String id, boolean isOverridingHops, int hops) {
    DARSEvent e = new DARSEvent();
    e.eventType = EventType.IN_SET_OVERRIDE_HOPS;
    e.isOverridingHops = isOverridingHops;
    e.hops = hops;
    e.nodeId = id;
    return e;
  }
  
  public static DARSEvent inSetNodeOverrideHopCount(String id, boolean isOverridingHops, int hops) {
    DARSEvent e = new DARSEvent();
    e.eventType = EventType.IN_SET_HOPS_COUNT;
    e.isOverridingHops = isOverridingHops;
    e.hops = hops;
    e.nodeId = id;
    return e;
  }
  
  public static DARSEvent inMoveNode(String id, int x, int y) {
    DARSEvent e = new DARSEvent();
    e.eventType = EventType.IN_MOVE_NODE;
    e.nodeId = id;
    e.nodeX = x;
    e.nodeY = y;
    return e;
  }

  
  public static DARSEvent outAddNode(NodeAttributes n) {
    DARSEvent d = new DARSEvent();
    d.eventType = EventType.OUT_ADD_NODE;
    d.setNodeAttributes(n);
    d.informationalMessage = "Node Added: " + n.id + ".";
    return d;
    
  }

  public static DARSEvent outMoveNode(String id, int x, int y) {
    DARSEvent d = new DARSEvent();
    d.eventType = EventType.OUT_MOVE_NODE;
    d.nodeId = id;
    d.nodeX = x;
    d.nodeY = y;
    d.informationalMessage = "Node " + id + " moved to X:" + x +" Y:" + y + ".";
    return d;
  }

  public static DARSEvent outDeleteNode(String id) {
    DARSEvent d = new DARSEvent();
    d.eventType = EventType.OUT_DEL_NODE;
    d.nodeId = id;
    d.informationalMessage = "Node Deleted: " + id;
    return d;
  }

  public static DARSEvent outSetNodeRange(String id, int newRange) {
    DARSEvent e = new DARSEvent();
    e.eventType = EventType.OUT_SET_NODE_RANGE;
    e.nodeId = id;
    e.nodeRange = newRange;
    e.informationalMessage = "Node " + id + "'s range changed to " + newRange + ".";
    return e;
  }

  public static DARSEvent outSetNodePromiscuity(String id, boolean isPromiscuous) {
    DARSEvent e = new DARSEvent();
    e.eventType = EventType.OUT_SET_NODE_PROMISCUITY;
    e.isPromiscuous = isPromiscuous;
    e.nodeId = id;
    String status;
    if(isPromiscuous) status = "enabled";
    else status = "disabled";
    e.informationalMessage = "Node " + id + " " + status + " promiscuous mode.";
    if (e.isPromiscuous || e.isDroppingMessages){
       e.isMalicious = true;
    }
    return e;
  }
  
  public static DARSEvent outSetNodeDropMessages(String id, boolean isDroppingMessages) {
    DARSEvent e = new DARSEvent();
    e.eventType = EventType.OUT_SET_NODE_DROP_MESSAGES;
    e.nodeId = id;
    e.isDroppingMessages = isDroppingMessages;
    String status;
    if(isDroppingMessages) status = "enabled";
    else status = "disabled";
    e.informationalMessage = "Node " + id + " " + status + " dropping messages.";
    if (e.isPromiscuous || e.isDroppingMessages){
      e.isMalicious = true;
    }
    return e;
  }
  
  public static DARSEvent outSetOverRideHops(String id, boolean isOverridingHops, int hops) {
    DARSEvent e = new DARSEvent();
    e.eventType = EventType.OUT_SET_OVERRIDE_HOPS;
    e.nodeId = id;
    e.isOverridingHops = isOverridingHops;
    String status;
    e.hops = hops;
    if(isOverridingHops) status = "enabled";
    else status = "disabled";
    e.informationalMessage = "Node " + id + " " + status + " overriding the number of hops to destination.  Number of hops: "  + hops;
    if (e.isPromiscuous || e.isDroppingMessages || e.isOverridingHops){
      e.isMalicious = true;
    }
    return e;
  }
  
  public static DARSEvent outSetOverRideHopCount(String id, boolean isOverridingHops, int hops) {
    DARSEvent e = new DARSEvent();
    e.eventType = EventType.OUT_SET_HOPS_COUNT;
    e.nodeId = id;
    e.isOverridingHops = isOverridingHops;
    String status;
    e.hops = hops;
    if(isOverridingHops) status = "enabled";
    else status = "disabled";
    e.informationalMessage = "Node " + id + " " + status + " overriding the number of hops to destination. Number of hops: "  + hops;
    if (e.isPromiscuous || e.isDroppingMessages || e.isOverridingHops){
      e.isMalicious = true;
    }
    return e;
  }
  
  public static DARSEvent outMsgRecieved(String sourceId, String destId, String message) {
    DARSEvent e = new DARSEvent();
    e.eventType = EventType.OUT_MSG_RECEIVED;
    e.sourceId = sourceId;
    e.destinationId = destId;
    e.transmittedMessage = message;
    e.informationalMessage = "Node " + sourceId + " successfuly sent a message to Node " + destId;
    return e;
  }
  
  public static DARSEvent outMsgTransmitted(String sourceId, String destId, String message) {
    DARSEvent e = new DARSEvent();
    e.eventType = EventType.OUT_MSG_TRANSMITTED;
    e.sourceId = sourceId;
    e.destinationId = destId;
    e.transmittedMessage = message;
    e.informationalMessage = "Node " + sourceId + " transmitted a message to Node " + destId;
    return e;
  }
  


  public static DARSEvent outError(String informationalMessage) {
    DARSEvent d = new DARSEvent();
    d.eventType = EventType.OUT_ERROR;
    d.informationalMessage = informationalMessage;
    return d;
  }

  public static DARSEvent outDebug(String informationalMessage) {
    
    DARSEvent d = new DARSEvent();
    d.eventType = EventType.OUT_DEBUG;
    d.informationalMessage = informationalMessage;
    return d;
  }

  static DARSEvent outInformation(String informationalMessage) {
    // stub
    return new DARSEvent();
  }

  public static DARSEvent outControlMsgTransmitted(String sourceId, Message msg) {
    DARSEvent d = new DARSEvent();
    d.eventType = EventType.OUT_CONTROLMSG_TRANSMITTED;
    d.sourceId = msg.originId;
    d.destinationId = msg.destinationId;
    d.transmittedMessage = msg.message;
    d.informationalMessage = d.sourceId + " transmitted control message to " + d.destinationId + " : " + msg.message;
    return d;
  }

  public static DARSEvent outNarrMsgTransmitted(String sourceId, Message msg) {
    DARSEvent d = new DARSEvent();
    d.eventType = EventType.OUT_NARRMSG_TRANSMITTED;
    d.sourceId = msg.originId;
    d.destinationId = msg.destinationId;
    d.transmittedMessage = msg.message;
    d.informationalMessage = d.sourceId + " transmitted narrative message to " + d.destinationId + " : " + msg.message;
    return d;
  }
  
  public static DARSEvent outNarrMsgDropped(String nodeId, Message msg) {
    DARSEvent d = new DARSEvent();
    d.eventType = EventType.OUT_NARRMSG_DROPPED;
    d.sourceId = msg.originId;
    d.destinationId = msg.destinationId;
    d.droppedMessage = msg.message;
    d.informationalMessage = nodeId + " dropped narrative message " + msg.message;
    return d;
  }
  
  public static DARSEvent outControlMsgReceived(String sourceId, Message msg) {
    DARSEvent d = new DARSEvent();
    d.eventType = EventType.OUT_CONTROLMSG_RECEIVED;
    d.sourceId = msg.originId;
    d.destinationId = sourceId;
    d.transmittedMessage = msg.message;
    d.informationalMessage = d.sourceId + " received control message from " + d.destinationId + " : " + msg.message;
    return d;
  }

  public static DARSEvent outNarrMsgReceived(String sourceId, Message msg) {
    DARSEvent d = new DARSEvent();
    d.eventType = EventType.OUT_NARRMSG_RECEIVED;
    d.sourceId = msg.originId;
    d.destinationId = sourceId;
    d.transmittedMessage = msg.message;
    d.informationalMessage = d.sourceId + " received narrative message from " + d.destinationId + " : " + msg.message;
    return d;
  }
  
  
  public static DARSEvent outNodeInfo(String infoMsg) {
    DARSEvent e = new DARSEvent();
    e.eventType = EventType.OUT_NODE_INFO;
    e.informationalMessage = infoMsg;
    return e;
  }
  
  public static DARSEvent outQuantumElapsed() {
    DARSEvent e = new DARSEvent();
    e.eventType = EventType.OUT_QUANTUM_ELAPSED;
    return e;
  }

  static final Class<DARSEvent> c = DARSEvent.class;
  static final Field[] fields = c.getFields();
  public String getLogString() {
    StringBuilder sb = new StringBuilder();
    // proposed format of log string is:
    // comma separated values, with public fields of DARSEvent printed out in
    // order
 
    //Trunc the string builder
    sb.setLength(0);
    
    for (Field f : fields) {
      Object obj;
      try {
        obj = f.get(this);
        if (obj != null) {
          sb.append(obj.toString() + ",");
        } else {
          sb.append(",");
        }
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
        Utilities.showError("An error occurred while trying to serialize an event. Please file a bug report");
        System.exit(1);
      } catch (IllegalAccessException e) {
        e.printStackTrace();
        Utilities.showError("An error occurred while trying to serialize an event. Please file a bug report");
        System.exit(1);
      }

    }
    // remove trailing comma
    sb.deleteCharAt(sb.length()-1);
    
    // add a newline char
    sb.append(newline);
    return sb.toString();
  }

  public static String getLogHeader() {
    // use reflection to get each field name
    Class<DARSEvent> c = DARSEvent.class;
    Field[] fields = c.getFields();
    String ret = "";
    for (Field f : fields) {
      ret += f.getName() + ",";
    }
    // remove trailing comma
    ret = ret.substring(0, ret.length() - 1);
    return ret;
  }

  private static EventType getEventTypeFromString(String str) {
    // use reflection to get each field
    for(EventType e : EventType.values()) {
      if(e.toString().equals(str)) return e;  
    }
    return null;
  }
    
  public static NodeType parseNodeType(String str) {
    //Get each possible node type
    NodeType[] nTypes =  Utilities.getNodeTypes();
    
    //For each nType..
    for(NodeType nt : nTypes){
      if(str.equals(nt.toString())) {
        return nt;
      }
    }
    
    //No match
    return null;
  }
    
  
  public static DARSEvent parseLogString(String lineEvent) {
    DARSEvent e = new DARSEvent();
    try
    {
      String[] details = lineEvent.split(",");
      
      e.eventType = getEventTypeFromString(details[0]);
      if(e.eventType == null) {
        //Must have event type field.
        return null;
      }
      
      e.nodeId = details[1];
      e.sourceId = details[2];
      e.destinationId = details[3];
      e.informationalMessage = details[4];
      e.transmittedMessage = details[5];
      e.newSimSpeed = Integer.parseInt(details[6]);
      e.nodeX = Integer.parseInt(details[7]);
      e.nodeY = Integer.parseInt(details[8]);
      e.nodeRange = Integer.parseInt(details[9]);
      e.nodeType = parseNodeType(details[10]);
      e.currentQuantum = Long.parseLong(details[11]);
      e.isPromiscuous = Boolean.parseBoolean(details[12]);
      e.isDroppingMessages = Boolean.parseBoolean(details[13]);
      e.isMalicious = Boolean.parseBoolean(details[14]);
      e.isOverridingHops = Boolean.parseBoolean(details[15]);
      e.hops = Integer.parseInt(details[16]);
      
    }
    catch (Exception ex){
      ex.printStackTrace();
      return null;
    }

    return e;
  }

}
