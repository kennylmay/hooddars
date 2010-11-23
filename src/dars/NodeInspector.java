package dars;

import javax.swing.JDialog;

import dars.proto.aodv.AodvDialog;

//This interface exposes a way for a user interface 
//to view the attributes of a given node. The simulation
//engine implements this interface.
public interface NodeInspector {
  public NodeAttributes getNodeAttributes(String nodeId);
  
  public JDialog getNodeDialog(String nodeId);
  
  public void updateNodeDialog(String nodeId, JDialog dialog);
}
