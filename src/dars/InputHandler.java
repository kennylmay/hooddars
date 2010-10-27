/**
 * 
 */
package dars;

import logger.Logger;
import dars.event.DARSEvent;

/**
 * @author Mike
 *
 */
public  class InputHandler {
  public static void moveNode(String nodeId, int x, int y){
      Logger.log(DARSEvent.Input.makeMoveNode(nodeId,x,y));
    
  }  
  
  public static void deleteNode(String nodeId){
	  Logger.log(DARSEvent.Input.makeDeleteNode(nodeId));  
  }
  
  public static void setNodeAttributes(String nodeId, dars.NodeAttributes nodeAttributes) {
	  Logger.log(DARSEvent.Input.makeEditNode(nodeId, nodeAttributes)); 
  }
  
  public static void addNode(int x, int y, dars.NodeAttributes nodeAttributes){
	  Logger.log(DARSEvent.Input.makeAddNode(nodeAttributes));
  }
  
  public static void sendMessage(String sourceId, String destId, dars.Message message) {
	  Logger.log(DARSEvent.Input.makeSendMessage(sourceId, destId, message));
  }
  
 
  

}


