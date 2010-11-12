/**
 * 
 */
package dars;

import dars.proto.*;
import dars.proto.aodv.NodeDialog;

import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JDialog;

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

public void clear() {
   store.clear();
}


public Node getNode(String nodeId) {
  return store.get(nodeId);
  
}

/**
 * Function will be add a Node to the NodeStore.
 * 
 * This function will add a node to the to the NodeStore. *
 * 
 * @author kennylmay
 * 
 * @param node
 */

public void addNode(Node node) {
      store.put(node.getAttributes().id, node);
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

public NodeDialog getNodeDialog(String nodeId) {
  Node node = store.get(nodeId);
  return node.getNodeDialog();
}

public void updateNodeDialog(String nodeId, NodeDialog dialog) {
  Node node = store.get(nodeId);
  node.updateNodeDialog(dialog);
}

}
