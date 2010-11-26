package dars.proto.dsdv;

import javax.swing.JDialog;

import dars.Message;
import dars.NodeAttributes;
import dars.proto.Node;

public class Dsdv implements Node {

  /**
   * Constructor
   */
  public Dsdv(NodeAttributes atts) {
    //TODO implement constructor
  }
  
  /**
   * Constants needed by DSDV
   */
  
  
  /**
   * Private Member Fields
   */
  
  
  /**
   * Private Member Functions
   */
  
  
  /**
   * Public Member Functions Not part of the Node Interface
   */
  
  
  /** 
   * Public Member Function Required to Implement the Node Interface
   */
  
  @Override
  public void clockTick() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public NodeAttributes getAttributes() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public JDialog getNodeDialog() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean isPromiscuous() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public Message messageToNetwork() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void messageToNode(Message message) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void newNarrativeMessage(String sourceID, String desinationID,
      String messageText) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void setAttributes(NodeAttributes atts) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void setPromiscuity(boolean value) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void setRange(int range) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void setXY(int x, int y) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void updateNodeDialog(JDialog dialog) {
    // TODO Auto-generated method stub
    
  }

}
