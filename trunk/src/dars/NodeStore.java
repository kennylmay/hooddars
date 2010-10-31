/**
 * 
 */
package dars;

import dars.proto.*;
import dars.proto.aodv.Aodv;

import java.util.HashMap;
import java.util.Iterator;

/**
 * @author Kenny
 * 
 */
public class NodeStore implements NodeInspector {

// Hash map of nodes.
private HashMap<String, Node> store = new HashMap<String, Node>();

// Fulfills the "Node Inspector" contract.
public NodeAttributes getNodeAttributes(String nodeId) {
   Node node = store.get(nodeId);
   
   if(node == null) {
     return null;
   }
   return node.getAttributes();
}

// Sets attributes for a given node
public void setNodeAttributes(String nodeId, NodeAttributes nodeAttributes) {
   Node node = store.get(nodeId);
   node.setAttributes(nodeAttributes);
}

/**
 * Function will be add a Node to the NodeStore.
 * 
 * This function will add a node to the to the NodeStore. *
 * 
 * @author kennylmay
 * 
 * @param nodeId, simulationType, NodeAttributes
 */

public void addNode(String nodeId, String simulationType, NodeAttributes nodeAttributes) {
   Node node;
   if (simulationType == "AODV"){
      node = new Aodv();
      node.setAttributes(nodeAttributes);
      store.put(nodeId, node);
   }else if (simulationType == "DSDV"){
      node = null;
   }
}

/**
 * Function will be delete a Node from the NodeStore.
 * 
 * This function will delete a node from the to the NodeStore.
 * 
 * @author kennylmay
 * 
 * @param name
 */
public void deleteNode(String id) {
   store.remove(id);
}

// Returns an iterator for all of the nodes in the store.
public Iterator<Node> getNodes() {
   return store.values().iterator();
}

/**
 * Function will return a node based on its index.
 * 
 * This function will return a node based on its index in the HashMap. This
 * function operates on the vector as if it is NOT zero indexed.
 * 
 * @author kennylmay
 * 
 * @param int index
 * 
 * @return Template T
 */
public Node getNodeByIndex(int index) {
   return (store.get(index));
}

/**
 * Function will return the number of nodes.
 * 
 * This function will return the number of nodes in the NodeStore.
 * 
 * @author kennylmay
 * 
 * @param
 */
public int getNumberOfNodes() {
   // /Add one because the Vector is zero indexed.
   return store.size() + 1;
}

}
