package gui;

import net.java.balloontip.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Vector;

public class GNode extends JPanel {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public static String newline = System.getProperty("line.separator");
  static public GNode SelectedNode;
  ///Constructor
  public GNode(String id, JLayeredPane layeredPane, int x, int y) {
    //Copy in the id, coordinates
    id_  = id;
    this.layeredPane = layeredPane;
   
    //Setup the default graphic, state
    img_ = ImageFactory.getNodeImg();

    //Setup the bounds given by x and y, and the size of the node
    setSize(new Dimension(img_.getWidth(null), img_.getHeight(null)));
    setLocation(new Point(x,y));
  
    //Add the internal mouse listeners
    addMouseListener(new GNodeMouseListener());
    addMouseMotionListener(new GNodeMouseMotionListener());
  }

  @Override
  public void paintComponent(Graphics g) {
    //Draw the graphic 
    g.drawImage(img_,  0, 0,null);
  }
    
  ///Functions
  public void addListener(GNodeListener l) {
    this.listeners.add(l);
  }

  public void removeListener(GNodeListener l){
	  this.listeners.remove(l);
  }
  //Select
  public void  select() {
    //unselect the currently selected node
    if(GNode.SelectedNode != null) {
      GNode.SelectedNode.unselect();
    }

    isSelected = true;
    GNode.SelectedNode = this;
    System.out.println("selecting a node..");
    this.img_ = ImageFactory.getSelectedNodeImg();
    layeredPane.repaint();
  }

  //Unselect
  public void  unselect() {
    System.out.println("unselecting a node..");
    isSelected=false;
    GNode.SelectedNode = null;
    this.img_ = ImageFactory.getNodeImg();
    layeredPane.repaint(); 
   
  }


  public void setXY(int x, int y) {
    //Set the bounds of the canvas
    setLocation(new Point(x , y));
  }

  public String getId() {
    return this.id_;
  }

  public Point getCenter() {
    return new Point(getX() + img_.getWidth()/2, getY() + img_.getHeight()/2);
  }

  
  private void setEntered(boolean entered) {
   if(entered) {
     //Set entered status
     isEntered = true;
     
     //Set image to hover image
     img_ = ImageFactory.getHoveredNodeImg();
   }
    else {
      isEntered = false;
      if(isSelected) {
        img_ = ImageFactory.getSelectedNodeImg();
      } else {
        img_ = ImageFactory.getNodeImg();
      }
    }
    
    //repaint this component
    repaint();
  }

  public void echo(String msg){
    //if balloon tip is not init, init it.
    if(bt!=null) {
      initBalloonTip();
    }
    
    //Prepend the message to the balloon's text
    bt_text.prepend(msg);
    
    //Get the current time
    BTLastUpdate = System.currentTimeMillis() / 1000L;
    
    //Schedule a closeIfInactive call; This will close the tip if it is inactive
    //for some amount of time.
    javax.swing.Timer t = new Timer(2000, new ActionListener() {
      public void actionPerformed(ActionEvent e){
       btCloseIfInactive();  
      }
    });
    t.setRepeats(false);
    t.start();
  }
  
  private void initBalloonTip(){
    //setup the balloon tip style
    net.java.balloontip.styles.ModernBalloonStyle style = 
      new net.java.balloontip.styles.ModernBalloonStyle(
          10, 10, Color.WHITE, new Color(230,230,230), Color.BLUE);
    
    bt = new BalloonTip(this, bt_text, style,
                        BalloonTip.Orientation.LEFT_ABOVE,
                        BalloonTip.AttachLocation.ALIGNED,
                        20,10, true);
    bt.setPadding(5);
    bt.setVisible(true);
  }
  
  private void btCloseIfInactive() {
    
    
  }
  
  
  //ID of the node
  private String id_;
  private boolean isEntered, isSelected;

  //DraggedNode boolean. Will be set true if the node is currently being dragged
  boolean isDragged;
  private JLayeredPane layeredPane;
  private DraggedGNode draggedGNode;
  private BufferedImage img_;
  private final Vector<GNodeListener> listeners = new Vector<GNodeListener>();
  private BalloonTip bt;
  private BTTextArea bt_text = new BTTextArea();
  private long BTLastUpdate = 0;
  private boolean isClicked=false;
  //Inner classes
  ////////////////////////////////////////////////////////////
  private class BTTextArea extends JTextArea {
    public void prepend(String msg) {
      setText(msg + newline + getText());
    }
  }
  private class GNodeMouseListener extends MouseAdapter {
    @Override
    public void mousePressed(MouseEvent e) {
      //If this is a popup event, notify the handlers and return
      if(e.isPopupTrigger()) {
    	for(GNodeListener l : listeners) {
          l.nodePopupEvent((GNode)e.getSource(), e.getX(),e.getY());
        }
        return;
      }     

      //If it's a non popup event right click, just return out
      if(e.getButton() == MouseEvent.BUTTON3) {
        return;
      }
  
      //Mark this node as selected
      select();

      //Notify the handlers
      for(GNodeListener l : listeners) {
        l.nodeSelected( (GNode) e.getSource());
      }
     
      isClicked=true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
      //If its a right click, return
      if(e.getButton() == MouseEvent.BUTTON3) {
        return;
      }
      
   
      //If no node was being dragged, no work to do.
      if(draggedGNode == null) {
        return;
      }
      
      //Notify the handlers of the node movement
      for(GNodeListener l : listeners) {
        l.nodeMoved((GNode) e.getSource(), draggedGNode.getX(), draggedGNode.getY());
      }

      //Remove the dragged node.
      draggedGNode.cleanup();
      layeredPane.repaint(); 
      draggedGNode = null;
      isClicked = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
      //Set state to entered
      setEntered(true);
    
      //invalidate the parent container
      layeredPane.repaint();
      
      //Notify the handlers
      for(GNodeListener l : listeners) {
        l.nodeEntered( (GNode) e.getSource() );
      }
    }
  
    @Override
    public void mouseExited(MouseEvent e) {
      //Set state to not entered
      setEntered(false);
   
      //Notify the handler
      for(GNodeListener l : listeners) {
        l.nodeExited( (GNode) e.getSource() );
      }
    }
  }

  private class GNodeMouseMotionListener extends MouseMotionAdapter {
      @Override
      public void mouseDragged(MouseEvent e) {
        //If its a right click, return
        if(!isClicked) {
          System.out.println("Node dragged AVOIDED");
          return;
        }
        

        System.out.println("Node dragged");
        //If this is the first time through, create a new dragged node.
        if(draggedGNode == null) {
        draggedGNode = new DraggedGNode( (GNode) e.getSource() );
        System.out.println("Adding new Dragged Node");
      }

       System.out.printf("Mouse X: %d Y: %d", e.getX(), e.getY());
      //Update the dragged node's position.
      draggedGNode.moveXYOffset(e.getX(),e.getY());
          
      //Invalidate the parent component, this will cause a redraw hopefully
      draggedGNode.repaint();
      layeredPane.repaint();      
    }
  }
    
  private class DraggedGNode extends JPanel {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    DraggedGNode(GNode parent) {
      //Copy in attributes
      parent_ = parent;

      //Setup the ghosted node graphic
      img_ = ImageFactory.getGhostedNodeImg();
      
      //Set the initial coordinates
      moveXYOffset(parent.getX(), parent.getY());
      setSize(new Dimension(img_.getWidth(null),  img_.getHeight(null)));      

      //Add this canvas to the parental container at the popup level
      parent.layeredPane.add(this,JLayeredPane.POPUP_LAYER);

      System.out.println("Creating new drag node..");
    }
    @Override
    public void paintComponent(Graphics g) {
     //Draw the graphic 
     g.drawImage(img_,  0, 0,null);
   
    }


    public void moveXYOffset(int x, int y) {
      int xOffset = parent_.getX() + x - img_.getWidth(null)/2;
      int yOffset = parent_.getY() + y - img_.getHeight(null)/2;
      setLocation(new Point(xOffset, yOffset));
    }
   
    public void cleanup() {
    //Remove this canvas from the parent container
      layeredPane.remove( layeredPane.getIndexOf(this) );
         
    }
    
    private BufferedImage img_;
    private GNode parent_;
      
  }
  
  
}


  

