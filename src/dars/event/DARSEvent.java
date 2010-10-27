/**
 * 
 */
package dars.event;
import dars.NodeAttributes;
import dars.Message;

/**
 * @author Mike
 * DARSEvent is a Mondo-big class that is
 * used throughout DARS. 
 */
public class DARSEvent {
	
  private enum EventType { OUTPUT, INPUT, INFORM };
  private enum InputType { ADD_NODE, MOVE_NODE, DEL_NODE, EDIT_NODE, SEND_MSG};
  private enum OutputType { ADD_NODE, MOVE_NODE, DEL_NODE, EDIT_NODE, NODE_MSG };
  private enum NodeMsgType { MSG_SENT, MSG_RCVD, TABLE_ADD, TABLE_DEL, HELLO, GENERIC};
  private enum InformType { ERROR, DEBUG, USER };
  
  private EventType eventGroup;
  private InputType inputType;
  private OutputType outputType;
  private InformType informType; 
  
  //hide the default constructor
  private DARSEvent() { };
  
  public static class Input {
	public static DARSEvent makeAddNode(NodeAttributes n) {
		//stub
		return new DARSEvent();
	}
	
	public static DARSEvent makeMoveNode(String id,int x, int y) {
		//stub
		return new DARSEvent();
	}
	
	public static DARSEvent makeDeleteNode(String id) {
		//stub
		return new DARSEvent();
	}
	
	public static DARSEvent makeEditNode(String id, NodeAttributes n) {
		//stub
		return new DARSEvent();	
	}
	
	public static DARSEvent makeSendMessage( String sourceId, String destinationId, Message m) {
		//stub
		return new DARSEvent();
	}
  }
 
  public static class Output {
	 public static DARSEvent makeAddNode(NodeAttributes n) {
      //stub
	  return new DARSEvent();
	}
		
	public static DARSEvent makeMoveNode(String id) {
		//stub
		return new DARSEvent();
	}
	
	public static DARSEvent makeDeleteNode(String id) {
		//stub
		return new DARSEvent();
	}
	public static DARSEvent makeEditNode(String id, NodeAttributes n) {
		//stub
		return new DARSEvent();	
	}  
	
	public static class NodeMsg {
		
		public static DARSEvent makeMsgSent(String srcId, String destId) {
	      //stub
		  return new DARSEvent();
		}
		
		public static DARSEvent makeMsgRcvd(String srcId, String destId) {
		  //stub
		  return new DARSEvent();
		}
		
		public static DARSEvent makeTableAdd(String srcId, String addedNodeId) {
		  //stub
		  return new DARSEvent();
		}
		
		public static DARSEvent makeTableDel(String srcId, String removedNodeId) {
			//stub
			return new DARSEvent();	
		}
		
		public static DARSEvent makeHello() {
			//stub
			return new DARSEvent();
		}
		
		public static DARSEvent makeGeneric(String srcid, String message) {
			//stub
			return new DARSEvent(); 		
		}
	}
	
	public static class Inform {
		public static DARSEvent makeError() {
		   //stub
			return new DARSEvent();
		}
		
		public static DARSEvent makeDebug() {
		   //stub
		   return new DARSEvent();
		}
		
		public static DARSEvent makeUser() {
		   //stub
		   return new DARSEvent();
		}
	}
		
		
    }
  
  
  
  //extracts a log entry from this event.
  public String getLogString() {
    //stub
	return "asdf";
  }
}
