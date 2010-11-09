package dars.gui;

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

  public static String      newline          = System
                                                 .getProperty("line.separator");

  static public GNode       SelectedNode;

  // /Constructor
  public GNode(String id, int x, int y, int range, JLayeredPane layeredPane) {
    // Copy in the id, coordinates, range
    id_ = id;
    this.layeredPane = layeredPane;
    this.range = range;

    // Setup the default graphic, state
    img_ = ImageFactory.getNodeImg();

    setOpaque(false);

    // Setup the bounds given by x and y, and the size of the node
    setSize(new Dimension(img_.getWidth(null), img_.getHeight(null)));
    setLocation(new Point(x, y));

    // Add the internal mouse listeners
    addMouseListener(new GNodeMouseListener());
    addMouseMotionListener(new GNodeMouseMotionListener());

    rangeIndicator = new RangeIndicator(this);
  }

  private RangeIndicator rangeIndicator;

  public void cleanup() {

    if (rangeIndicator != null) {
      rangeIndicator.setVisible(false);
      layeredPane.remove(rangeIndicator);
      rangeIndicator = null;
    }

  }
 
  private Rectangle r = new Rectangle();
  @Override
  public void paintComponent(Graphics g) {

    // Draw the graphic
    g.drawImage(img_, 0, 0, null);

    // Draw the node id onto the graphic
    r.setRect(9, 4, 17, 12);
    ImageFactory.drawNodeID(g, id_, r);

  }

  // /Functions
  public void addListener(GNodeListener l) {
    this.listeners.add(l);
  }

  public void removeListener(GNodeListener l) {
    this.listeners.remove(l);
  }

  public void broadcast() {
    rangeIndicator.fireBroadcast();
  }

  // Select
  public void select() {
    // unselect the currently selected node
    GNode tmp = GNode.SelectedNode;
    if (GNode.SelectedNode != null) {
      GNode.SelectedNode.unselect();
    }

    isSelected = true;
    GNode.SelectedNode = this;
    // System.out.println("selecting a node..");
    this.img_ = ImageFactory.getSelectedNodeImg();

    if (tmp != null) {
      tmp.repaint();
    }

    this.repaint();
    // show the range indicator
    // rangeIndicator.setFill(true);

    // layeredPane.repaint();
  }

  // Unselect
  public void unselect() {
    // System.out.println("unselecting a node..");
    isSelected = false;
    GNode.SelectedNode = null;
    this.img_ = ImageFactory.getNodeImg();

    // hide the range indicator
    // rangeIndicator.setFill(false);
    // layeredPane.repaint();
    this.repaint();

  }

  public void setXY(int x, int y) {
    // Set the new location of the canvas
    setLocation(new Point(x, y));

    // If we have a range indicator, update that too
    if (rangeIndicator != null) {
      rangeIndicator.setCenter(getCenter());
    }
  }

  public void setRange(int range) {
    this.range = range;
    rangeIndicator.resize();
  }

  public int getRange() {
    return range;
  }

  public String getId() {
    return this.id_;
  }

  public Point getCenter() {
    return new Point(getX() + img_.getWidth() / 2, getY() + img_.getHeight()
        / 2);
  }

  private void setEntered(boolean entered) {
    // If the node is selected, don't draw a hover image
    if (isSelected) {
      isEntered = entered;
      return;
    }

    if (entered) {
      isEntered = true;

      // Set entered status
      isEntered = true;

      // Set image to hover image
      img_ = ImageFactory.getHoveredNodeImg();
    }
    // unset hover image
    else {
      img_ = ImageFactory.getNodeImg();
    }
    this.repaint();
  }

  // ID of the node
  private String                      id_;

  private boolean                     isEntered, isSelected;

  // DraggedNode boolean. Will be set true if the node is currently being
  // dragged
  boolean                             isDragged;

  private JLayeredPane                layeredPane;

  private DraggedGNode                draggedGNode;

  private BufferedImage               img_      = null;

  private final Vector<GNodeListener> listeners = new Vector<GNodeListener>();

  private boolean                     isClicked = false;

  private class GNodeMouseListener extends MouseAdapter {
    @Override
    public void mousePressed(MouseEvent e) {
      // Invalidate any other drag notions initially
      isClicked = false;

      // If this is a popup event, notify the handlers and return
      if (e.isPopupTrigger()) {
        // System.out.println("Popup event");
        for (GNodeListener l : listeners) {
          l.nodePopupEvent((GNode) e.getSource(), e.getX(), e.getY());

        }
        return;
      }

      // If it's a non popup event right click, just return out
      if (e.getButton() == MouseEvent.BUTTON3) {
        return;
      }

      // Mark this node as selected
      select();

      // Notify the handlers
      for (GNodeListener l : listeners) {
        l.nodeSelected((GNode) e.getSource());
      }

      isClicked = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
      if (e.isPopupTrigger()) {
        // System.out.println("Popup event");
        for (GNodeListener l : listeners) {
          l.nodePopupEvent((GNode) e.getSource(), e.getX(), e.getY());
        }

        return;
      }

      // If its a right click, return
      if (e.getButton() == MouseEvent.BUTTON3) {
        return;
      }

      // If no node was being dragged, no work to do.
      if (draggedGNode == null) {
        return;
      }

      // Notify the handlers of the node movement
      for (GNodeListener l : listeners) {
        l.nodeMoved((GNode) e.getSource(), draggedGNode.getX(),
            draggedGNode.getY());
      }

      System.out.println("Moving node.");
      // Remove the dragged node.
      draggedGNode.cleanup();
      draggedGNode = null;
      isClicked = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
      // Set state to entered
      setEntered(true);

      // invalidate the parent container

      // Notify the handlers
      for (GNodeListener l : listeners) {
        l.nodeEntered((GNode) e.getSource());
      }
    }

    @Override
    public void mouseExited(MouseEvent e) {
      // Set state to not entered
      setEntered(false);

      // Notify the handler
      for (GNodeListener l : listeners) {
        l.nodeExited((GNode) e.getSource());
      }
    }
  }

  private class GNodeMouseMotionListener extends MouseMotionAdapter {
    @Override
    public void mouseDragged(MouseEvent e) {

      // If its a right click, return
      if (!isClicked) {
        return;
      }

      // System.out.println("Node dragged");
      // If this is the first time through, create a new dragged node.
      if (draggedGNode == null) {
        draggedGNode = new DraggedGNode((GNode) e.getSource());
        // System.out.println("Adding new Dragged Node");
      }

      // System.out.printf("Mouse X: %d Y: %d", e.getX(), e.getY());
      // Update the dragged node's position.
      draggedGNode.moveXYOffset(e.getX(), e.getY());

    }
  }

  private class DraggedGNode extends JPanel {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    DraggedGNode(GNode parent) {
      // Copy in attributes
      parent_ = parent;

      // Setup the ghosted node graphic
      img_ = ImageFactory.getGhostedNodeImg();

      // Set the initial coordinates
      moveXYOffset(parent.getX(), parent.getY());
      setSize(new Dimension(img_.getWidth(null), img_.getHeight(null)));

      // Add this canvas to the parental container at the popup level
      parent.layeredPane.add(this, JLayeredPane.POPUP_LAYER);

      setOpaque(false);
      // System.out.println("Creating new drag node..");
    }

    @Override
    public void paintComponent(Graphics g) {
      // Draw the graphic
      g.drawImage(img_, 0, 0, null);

    }

    public void moveXYOffset(int x, int y) {
      int xOffset = parent_.getX() + x - img_.getWidth(null) / 2;
      int yOffset = parent_.getY() + y - img_.getHeight(null) / 2;
      setLocation(xOffset, yOffset);
    }

    public void cleanup() {
      // Remove this canvas from the parent container
      layeredPane.remove(this);
      layeredPane.repaint();

    }

    private BufferedImage img_;

    private GNode         parent_;

  }

  static class BCastAnimator {
    static int            counter        = 11;
    static final int      countMax       = 32768;
    static Timer          animationTimer = new Timer(100, new ActionListener() {
                                           @Override
                                           public void actionPerformed(
                                               ActionEvent arg0) {
                                             BCastAnimator.counter++;
                                             if (BCastAnimator.counter > BCastAnimator.countMax) {
                                               BCastAnimator.counter = 0;
                                             }
                                           }
                                         });

    public static boolean isStarted;

    static void start() {
      isStarted = true;
      animationTimer.start();
    }

    static final int totalSteps = 30;
    static int       curStep    = 1;

    public static void draw(Graphics g, RangeIndicator ri) {
      // System.out.println("animating");
      Graphics2D g2 = (Graphics2D) g;
      // Draw the graphic
      int dimX, dimY;
      dimX = ri.parent_.getRange() * 2;
      dimY = dimX;

      int x = dimX / 2;
      int y = dimY / 2;

      int bCastRadius = (int) (ri.parent_.getRange() * ((double) ri.curStep() / (double) ri.totalSteps));
      int bCastX = x - bCastRadius;
      int bCastY = y - bCastRadius;

      g2.setColor(Color.GREEN);
      // draw a 3 pixel circle
      g2.drawOval(bCastX, bCastY, bCastRadius * 2, bCastRadius * 2);
      g2.drawOval(bCastX + 1, bCastY + 1, bCastRadius * 2, bCastRadius * 2 - 1);
      g2.drawOval(bCastX + 2, bCastY + 2, bCastRadius * 2, bCastRadius * 2 - 2);
    }
  }

  private class RangeIndicator extends JPanel {
    private static final long serialVersionUID = 1L;
    private GNode             parent_;

    RangeIndicator(GNode parent) {
      // Copy in attributes
      parent_ = parent;

      // Add this canvas to the parental container at the lowest layer
      parent.layeredPane.add(this, JLayeredPane.PALETTE_LAYER);

      // System.out.println("Creating new range indicator");
      // get the range of the parent node
      setOpaque(false);
      int range = parent_.getRange();

      // set the size accordingly
      setSize(new Dimension(range * 2, range * 2));

      // set the center
      this.setCenter(parent_.getCenter());

      // Make sure the animation counter is running
      if (!BCastAnimator.isStarted) {
        BCastAnimator.start();
      }
    }

    public void fireBroadcast() {
      startStep = BCastAnimator.counter;
    }

    private boolean isFilled;

    @Override
    public void paintComponent(Graphics g) {

      // System.out.println("painting ranger");
      Graphics2D g2 = (Graphics2D) g;
      // Draw the graphic
      g2.setColor(Color.BLACK);
      g2.drawOval(0, 0, parent_.getRange() * 2 - 2, parent_.getRange() * 2 - 2);

      if (isActive()) {
        BCastAnimator.draw(g, this);
      }

      if (isFilled) {
        g2.setColor(new Color(20, 20, 0, 20));
        g2.fillOval(0, 0, parent_.getRange() * 2 - 2,
            parent_.getRange() * 2 - 2);
      }

    }

    int startStep;
    int totalSteps = 10;

    public boolean isActive() {
      return (BCastAnimator.counter - startStep < totalSteps && BCastAnimator.counter > startStep);
    }

    public int curStep() {
      return BCastAnimator.counter - startStep;
    }

    public void setCenter(Point p) {
      setLocation(p.x - parent_.getRange(), p.y - parent_.getRange());
    }

    public void resize() {
      // resize this panel to accommodate the new range
      int range = parent_.getRange();
      setSize(new Dimension(new Dimension(range * 2, range * 2)));

      // reset the location
      setCenter(parent_.getCenter());

    }

  }

  private int range;

}
