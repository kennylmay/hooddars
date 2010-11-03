/**
 * 
 */
package dars.event;

import java.lang.reflect.Field;

import dars.NodeAttributes;
import dars.Message;

/**
 * @author Mike
 */
public class DARSEvent {
  private static String newline = System.getProperty("line.separator");
  public enum EventType {
    // Input event types
    IN_ADD_NODE, IN_MOVE_NODE, IN_DEL_NODE, IN_EDIT_NODE, IN_SEND_MSG, IN_SIM_SPEED, 
    IN_START_SIM, IN_PAUSE_SIM, IN_RESUME_SIM, IN_STOP_SIM, IN_SET_PROTOCOL,
    // Output event types
    OUT_ADD_NODE, OUT_MOVE_NODE, OUT_DEL_NODE, OUT_EDIT_NODE, OUT_NODE_DATA_RECEIVED, 
    OUT_NODE_INFORM, OUT_DEBUG, OUT_ERROR, OUT_INFORM, OUT_START_SIM, OUT_PAUSE_SIM,
    OUT_RESUME_SIM, OUT_STOP_SIM, OUT_SIM_SPEED
  };

  public EventType      eventType;
  public String         nodeId;
  public String         sourceId;
  public String         destinationId;
  public String         payload;
  public String         informationalMessage;
  public int            newSimSpeed;
  public int            nodeX;
  public int            nodeY;
  public int            nodeRange;
  public Message         NewMessage;
  
  public NodeAttributes getNodeAttributes() {
    NodeAttributes n = new NodeAttributes();
    n.x = nodeX;
    n.y = nodeY;
    n.range = nodeRange;
    n.id = nodeId;
    return n;
  }
  
  public void setNodeAttributes(NodeAttributes n) {
    nodeX = n.x;
    nodeY = n.y;
    nodeRange = n.range;
    nodeId = n.id;
    
  }

  // Hide the default constructor. DARSEvents can only be made through the
  // supplied functions that follow.
  private DARSEvent() {
  };

  public static DARSEvent inStartSim() {
    DARSEvent e = new DARSEvent();
    e.eventType = EventType.IN_START_SIM;
    return e;
  }

  public static DARSEvent outStartSim() {
    DARSEvent e = new DARSEvent();
    e.eventType = EventType.OUT_START_SIM;
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
    return e;
  }
  
  static DARSEvent inResumeSim() {
    DARSEvent e = new DARSEvent();
    e.eventType = EventType.IN_RESUME_SIM;
    return e;
  }
  
  public static DARSEvent outResumeSim() {
    DARSEvent e = new DARSEvent();
    e.eventType = EventType.OUT_RESUME_SIM;
    return e;
  }
    
  public static DARSEvent inAddNode(NodeAttributes n) {
    DARSEvent e = new DARSEvent();
    e.eventType = EventType.IN_ADD_NODE;
    e.setNodeAttributes(n);
    return e;
  }

  public static DARSEvent inDeleteNode(String id) {
    DARSEvent e = new DARSEvent();
    e.eventType = EventType.IN_DEL_NODE;
    e.nodeId = id;
    return e;
  }

  public static DARSEvent inEditNode(String id, NodeAttributes n) {
    DARSEvent e = new DARSEvent();
    e.eventType = EventType.IN_EDIT_NODE;
    e.nodeId = id;
    e.nodeX = n.x;
    e.nodeY = n.y;
    return e;
  }
  
  static DARSEvent inSendMsg(Message m, String sourceId, String destinationId) {
    DARSEvent c = new DARSEvent();
    c.eventType = EventType.IN_SEND_MSG;
    c.NewMessage = m;
    c.sourceId = sourceId;
    c.destinationId = destinationId;
    
    return c;
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
    return d;
    
  }

  public static DARSEvent outMoveNode(String id, int x, int y) {
    DARSEvent d = new DARSEvent();
    d.eventType = EventType.OUT_MOVE_NODE;
    d.nodeId = id;
    d.nodeX = x;
    d.nodeY = y;
    return d;
  }

  public static DARSEvent outDeleteNode(String id) {
    DARSEvent d = new DARSEvent();
    d.eventType = EventType.OUT_DEL_NODE;
    d.nodeId = id;
    return d;
  }

  public static DARSEvent outEditNode(String id, NodeAttributes n) {
    DARSEvent e = new DARSEvent();
    e.eventType = EventType.OUT_EDIT_NODE;
    e.nodeId = id;
    e.nodeX = n.x;
    e.nodeY = n.y;
    e.nodeRange = n.range;
    return e;
  }

  static DARSEvent outNodeInform(String sourceID, String informationalMessage) {
    // stub
    return new DARSEvent();
  }

  static DARSEvent outNodeDataReceived(String sourceId, String destinationId,
      String data) {
    // stub
    return new DARSEvent();
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

  // extracts a log entry from this event.
  public String getLogString() {

    // proposed format of log string is:
    // comma separated values, with public fields of DARSEvent printed out in
    // order

    // use reflection to get each field
    Class<DARSEvent> c = DARSEvent.class;
    Field[] fields = c.getFields();

    String ret = "";
    for (Field f : fields) {
      Object obj;
      ;
      try {
        f.setAccessible(true);
        obj = f.get(this);
        if (obj != null) {
          ret += obj.toString() + ",";
        } else {
          ret += ",";
        }
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
        System.exit(1);
      } catch (IllegalAccessException e) {
        e.printStackTrace();
        System.exit(1);
      }

    }
    // remove trailing comma
    ret = ret.substring(0, ret.length() - 1);
    
    // add a newline char
    ret += newline;
    return ret;
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

  public DARSEvent parseLogString(String str) {

    // stub
    return null;
  }

}
