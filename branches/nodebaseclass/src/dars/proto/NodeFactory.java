package dars.proto;

import dars.NodeAttributes;
import dars.proto.aodv.Aodv;
import dars.proto.dsdv.Dsdv;

/*
 * Node Factory class.
 * Instantiates a new node based on a supplied node type. Also contains 
 * the definitions for node types (NodeType) that are used throughout DARS.
 * 
 * This class must be changed if you want to add a new network protocol. 
 * 
 */
public class NodeFactory {
  public enum NodeType { AODV, DSDV };
  public static Node makeNewNode(NodeType nt, NodeAttributes na) {
    switch(nt) {
    case AODV : return new Aodv(na);
    case DSDV : return new Dsdv(na);
    default   : return null;
    }
  }
}
