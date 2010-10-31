package dars;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.AbstractQueue;
import javax.swing.Timer;
import dars.NodeStore;
import dars.event.DARSEvent;
import dars.proto.*;
import dars.proto.aodv.Aodv;

/**
 * @author Kenny
 * 
 */
public class SimEngine implements InputConsumer {
/**
 * Time to wait for an iteration.
 */
private int WAIT_TIME = 1000;
/**
 * Boolean that is used to keep track of the state of the simulation
 */
private boolean paused = false;
private NodeStore store = new NodeStore();
private AbstractQueue<Message> messages;

ActionListener engine = new ActionListener() {
   Node node;
   Message mess;
   public void actionPerformed(ActionEvent evt) {
     /// If we have any messages in the queue attempt a delivery
     if (messages.isEmpty() == false){
        mess = messages.poll();

        /// If the message is a broadcast then try to send to everyone
        if (mess.destinationId == Message.BCAST_STRING){
          for (int index = 0; index < store.getNumberOfNodes(); index++) {
            node = store.getNodeByIndex(index);
            
            /// Only allow the nodes in range to hear the broadcast.
            if (canCommuincate(mess.originId, node.getAttributes().id)){
              node.sendRawMessage();
            }
          }
        /// Else if the messages is not a broadcast try to send it to the destination id.
        }else{
           /// If the nodes can talk directly go ahead and send the message;
           if (canCommuincate(mess.originId, mess.destinationId)){
             node.sendRawMessage();
           }
        }  
     }
      /// Issue a clock tick to each node in the node store.
     for (int index = 0; index < store.getNumberOfNodes(); index++) {
       /// Issue a clock tick to each node
        node = store.getNodeByIndex(index);
        node.clockTick();
     }
   }
};

/**
 * Timer that will control the execution of the simulation
 */
Timer timer = new Timer(WAIT_TIME, engine);

/**
 * Function that will start a simulation
 * 
 * This function will be the controlling function for all of the nodes
 * 
 * @author kennylmay
 * 
 * @param
 */
void runSimulation() {
   timer.start();
}

/**
 * Function that sets the timer speed
 * 
 * This method will allow the adjustment of the time interval(time between clock
 * ticks) in seconds.
 * 
 * 
 * @author kennylmay
 * 
 * @param speed
 *           (int) The time in seconds that a simulation should pause between
 *           ticks.
 */
void setSimSpeed(int speed) {
   timer.setDelay(speed);
}

/**
 * Function that will pause a simulation
 * 
 * This function will pause the simulation until the user decides to continue
 * 
 * @author kennylmay
 * 
 * @param
 */
void pauseSimulation() {
   if (paused == false) {
      try {
         timer.wait();
      } catch (InterruptedException e) {
         // / May have to change this catch later
         e.printStackTrace();
      }
      paused = true;
   } else {
      timer.notify();
      paused = false;
   }
}

/**
 * Function that will stop a simulation
 * 
 * This function will stop a simulation indefinitely.
 * 
 * @author kennylmay
 * 
 * @param
 */
void stopSimulation() {
   timer.stop();
}

NodeStore getNodeStore(){
   return store;
}

/**
 * Function that returns the timer speed
 * 
 * This method will return the wait time.
 * 
 * @author kennylmay
 * 
 * @param
 */
public int getSimSpeed() {
   return timer.getDelay();
}

/**
 * Function used to add a message to the queue
 * 
 * This method will return the wait time.
 * 
 * @author kennylmay
 * 
 * @param
 */
public void deliverMessage(Message message){
	messages.add(message);
}

/**
 * This method is used for determining if a can send a message to antoher node
 * 
 * @param node1
 * @param node2
 * 
 * @return boolean
 */
private boolean canCommuincate(String Id1, String Id2){
    NodeAttributes att1 = store.getNodeAttributes(Id1);
	NodeAttributes att2 = store.getNodeAttributes(Id2);
	double distance = Point2D.distanceSq(att1.locationx, att1.locationy, att2.locationx, att2.locationy);
	if (distance > att1.range || distance > att2.range){
	  return false;
	}
	else return true;
}

/**
 * This function will provide a way to determine the type of even that is issued and
 * make a decision as to what to do with the event.
 * 
 * @ author kennylmay
 * 
 * @param DARSEvnt
 * 
 */
@Override
public void consumeInput(DARSEvent e) {
	if (e.eventType == DARSEvent.EventType.IN_START_SIM){
		if (timer.isRunning() == false){
			runSimulation();
		}
	} 
	else if (e.eventType == DARSEvent.EventType.IN_STOP_SIM){
		if (timer.isRunning() == true){
			stopSimulation();
		}
	}
	else if (e.eventType == DARSEvent.EventType.IN_PAUSE_SIM){
		if (timer.isRunning() == true){
			pauseSimulation();
		}
	}
	else if (e.eventType == DARSEvent.EventType.IN_SIM_SPEED){
		WAIT_TIME = e.newSimSpeed;
		setSimSpeed(WAIT_TIME);
	}
	else if (e.eventType == DARSEvent.EventType.IN_ADD_NODE){
	    //Get the node attributes for this input event
	    NodeAttributes ni = e.getNodeAttributes();
        
	    //Assign an ID to the node
	    ni.id = assignNodeId();
	    
	    //Make a new network node with these attributes
	    Node n = makeNetworkNode(ni);
	    
	    //Add it to the node store
        store.addNode(n);
        
        //Dispatch an output event indicating a new node has entered
        //the network.
        OutputHandler.dispatch(DARSEvent.outAddNode(ni));
    }
	else if (e.eventType == DARSEvent.EventType.IN_DEL_NODE){
	    store.deleteNode(e.nodeId);
    }
	else if (e.eventType == DARSEvent.EventType.IN_EDIT_NODE){
      store.setNodeAttributes(e.nodeId, e.getNodeAttributes());
	}
	else if (e.eventType == DARSEvent.EventType.IN_MOVE_NODE){
	  //Get the current attributes of the node
	  NodeAttributes na = store.getNodeAttributes(e.nodeId);
	  
	  //Set the new x and y
	  na.locationx = e.nodeX;
	  na.locationy = e.nodeY;
	  
	  //Set the new attributes
      store.setNodeAttributes(e.nodeId, e.getNodeAttributes());
      
      //Dispatch the moved event
      OutputHandler.dispatch(DARSEvent.outMoveNode(e.nodeId, na.locationx, na.locationy));
    }
	
}

public enum NodeType { AODV, DSDV };
private NodeType nodeType = NodeType.AODV;

public NodeType getNodeType() {
  return nodeType;
}

public void setNodeType(NodeType nt) {
  nodeType = nt;
}

public Node makeNetworkNode(NodeAttributes na) {
  //Make the network node based on what type of node is set
  Node n = null;
  switch(getNodeType()) {
    case AODV: 
      n = new Aodv();
      break;
   
    case DSDV:
      //TODO implement DSDV
      n = null;
      break;
  }
  
  assert(n!=null);
  
  //Set the node attributes
  n.setAttributes(na);
  
  return n;  
}
  
  




  /**
   * assignNodeId method. 
   * 
   *  Assigns a new node id. It uses the private variable currId to keep track
   *  of the next id. The assignment sequence is as follows:
   *  
   *  A......Z
   *  AA....AZ
   *  BA....BZ
   *  ........
   *  AAA..AAZ
   *  ABA..ABZ
   *  ........
   *  
   *  The algorithm used is a modified version of the 
   *  convert decimal to hex algorithm (or any other digit). It
   *  cheats a bit because there is no "zero" digit in the ID 
   *  assigning scheme (Just A-Z).
   * 
   * @ author Mike
   *  
   */
  private String assignNodeId() {
    // Assign a three character ID from A-Z
    //
    int charA = (int) 'A';
    int totalChars = 26;
    String ret = "";

    int remainder;
    int quotient = currId;
    int count = 0;
    while (quotient != 0) {

      // Divide the digit by our alphabet size. The remainder is the digit for
      // this place.
      remainder = quotient % totalChars;
      quotient = quotient / totalChars;

      // Convert the digit to its representation (A-Z)

      // If count is > 0, Cheat and decrement it by one.
      char c;
      if (count > 0) {
        c = (char) (remainder + charA - 1);
      } else {
        c = (char) (remainder + charA);
      }

      count++;
      // Prepend the return string
      ret = c + ret;
    }

    // increment the id
    currId++;

    //if ret is zero len, this must be the first assignment. Set it to "A".
    if(ret.length() == 0) {
      return "A";
    }
    
    return ret;
  }
  private int currId = 0;  
}
