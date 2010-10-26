package dars;
//This interface exposes a way for a user interface 
//to view the attributes of a given node. The node
//store implements this interface.
public interface NodeInspector {
  public NodeAttributes getNodeAttributes(String nodeId);
}
