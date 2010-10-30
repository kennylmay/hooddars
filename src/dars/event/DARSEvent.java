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

  public enum EventType {
    // Input event types
    IN_ADD_NODE, IN_MOVE_NODE, IN_DEL_NODE, IN_EDIT_NODE, IN_SEND_MSG, IN_SIM_SPEED, 
    IN_START_SIM, IN_PAUSE_SIM, IN_RESUME_SIM, IN_STOP_SIM, IN_SET_PROTOCOL,
    // Output event types
    OUT_ADD_NODE, OUT_MOVE_NODE, OUT_DEL_NODE, OUT_EDIT_NODE, OUT_NODE_DATA_RECEIVED, 
    OUT_NODE_INFORM, OUT_DEBUG, OUT_ERROR, OUT_INFORM
  };

  public EventType eventType;
  public String    nodeId;
  public String    sourceId;
  public String    destinationId;
  public String    payload;
  public String    informationalMessage;
  public int       newSimSpeed;

  // Hide the default constructor. DARSEvents can only be made through the
  // supplied functions that follow.
  private DARSEvent() {
  };

  public static DARSEvent inAddNode(NodeAttributes n) {

    return new DARSEvent();
  }

  static DARSEvent inDeleteNode(String id) {
    // stub
    return new DARSEvent();
  }

  static DARSEvent inEditNode(String id, NodeAttributes n) {
    // stub
    return new DARSEvent();
  }

  static DARSEvent inSendMsg(Message m, String sourceId, String destinationId) {
    // stub
    return new DARSEvent();
  }

  public static DARSEvent inSimSpeed(int newSpeed) {
    // stub
    DARSEvent e = new DARSEvent();
    e.eventType = EventType.IN_SIM_SPEED;
    e.newSimSpeed = newSpeed;
    return e;

  }

  static DARSEvent outAddNode(NodeAttributes n) {
    // stub
    return new DARSEvent();
  }

  static DARSEvent outMoveNode(String id) {
    // stub
    return new DARSEvent();
  }

  static DARSEvent outDeleteNode(String id) {
    // stub
    return new DARSEvent();
  }

  static DARSEvent outEditNode(String id, NodeAttributes n) {
    // stub
    return new DARSEvent();
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

  static DARSEvent outError(String informationalMessage) {
    // stub
    return new DARSEvent();
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
