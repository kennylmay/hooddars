/**
 * 
 */
package dars;

import java.util.HashMap;
import java.util.Iterator;

/**
 * @author Kenny
 *
 */
public class NodeStore<T> implements NodeInspector {
  //Hash map of nodes.
  private HashMap<String,T> store;
  
  //Fulfills the "Node Inspector" contract.
  public NodeAttributes getNodeAttributes(String nodeId){
	//stub
    return new NodeAttributes();
  }
 
  //Sets attributes for a given node
  public void setNodeAttributes(String id, NodeAttributes nodeAttributes) {
	  
  }
  
  //Adds a new node. Will send an event to the OutputHandler.
  public void addNode(NodeAttributes nodeAttributes) {
	  
  }
  
  //Deletes an existing node. Will send an event to the OutputHandler.
  public void deleteNode(String id){
	  
  }
  
  //Returns an iterator for all of the nodes in the store.
  public Iterator<T> getNodes() {
	  return store.values().iterator();
  }
  
}
