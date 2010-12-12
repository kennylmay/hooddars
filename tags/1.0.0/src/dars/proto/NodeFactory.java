package dars.proto;

import dars.NodeAttributes;
import dars.proto.aodv.Aodv;
import dars.proto.dsdv.Dsdv;

// Add new protocol import here.
// import dars.proto.newproto.NewProto;


/*
 * Node Factory class.
 * Instantiates a new node based on a supplied node type. Also contains 
 * the definitions for node types (NodeType) that are used throughout DARS.
 * 
 * This class must be changed if you want to add a new network protocol. 
 * 
 */
public class NodeFactory {
  // Add the protocol Name to the Enumeration
  // public enum NodeType { AODV, DSDV, NewProtoName };
  public enum NodeType { AODV, DSDV };
  public static Node makeNewNode(NodeType nt, NodeAttributes na) {
    if(nt == null || na == null) {
      return null;
    }
  
    switch(nt) {
    case AODV : return new Aodv(na);
    case DSDV : return new Dsdv(na);
    // Create nodes of the new NodeType.
    // case NewProtoName : return NewProto(na); 
    default   : return null;
    }
  }
}
