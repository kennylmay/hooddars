package dars;

import javax.swing.JDialog;

import dars.proto.aodv.NodeDialog;

//This interface exposes a way for a user interface 
//to view the attributes of a given node. The node
//store implements this interface.
public interface NodeInspector {
  public NodeAttributes getNodeAttributes(String nodeId);
  
  public NodeDialog getNodeDialog(String nodeId);
  
  public void updateNodeDialog(String nodeId, NodeDialog dialog);
}
