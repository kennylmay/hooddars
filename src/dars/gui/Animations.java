package dars.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Animations extends JPanel implements ComponentListener,
    ActionListener {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  Timer                     repaintTimer     = new Timer(100, this);

  public void actionPerformed(ActionEvent e) {
    repaint();
  }

  public Animations(JLayeredPane parent) {
    parent.add(this, JLayeredPane.DEFAULT_LAYER);
    parent.addComponentListener(this);
    setOpaque(false);
    setLocation(0, 0);
    repaintTimer.start();
  }

  public void traceMessage(GNode a, GNode b, Color color, int longevityFactor) {

    Connection c = new Connection(a, b, color, longevityFactor);
    connStore.add(c);
  }

  public void nodeBroadcast(GNode gnode) {
    RangeIndicator ri = riStore.get(gnode);
    
    if(ri == null) return;
    
    ri.fireBroadcast();
  }


  public void start() {
    animationTimer.start();
  }

  public void stop() {
    animationTimer.stop();
  }

  public void setFPS(int fps) {
    repaintTimer.setDelay(1000/fps); 
  }

  
  LinkedList<Connection>         connStore = new LinkedList<Connection>();
  HashMap<GNode, RangeIndicator> riStore   = new HashMap<GNode, RangeIndicator>();

  LinkedList<Connection>         topList   = new LinkedList<Connection>();
  public void paintComponent(Graphics g) {

    //clear out the top list
    topList.clear();
    
    // Draw the message tracing animation
    Iterator<Connection> i = connStore.iterator();
    while (i.hasNext()) {

      Connection c = i.next();
      if (c.shouldDie()) {
        // remove it from the store
        i.remove();
        continue;
      }

      // Draw the connection

      if(c.color == Color.BLUE) {
        int x1, y1, x2, y2;
        x1 = c.fromNode.getCenter().x;
        y1 = c.fromNode.getCenter().y;
        x2 = c.toNode.getCenter().x;
        y2 = c.toNode.getCenter().y;
        drawConn(g, c.color, x1, y1, x2, y2);
      }
      else {
        topList.add(c);
      }
    }
    //Draw the topList connections
    for(Connection c : topList) {
      int x1, y1, x2, y2;
      x1 = c.fromNode.getCenter().x;
      y1 = c.fromNode.getCenter().y;
      x2 = c.toNode.getCenter().x;
      y2 = c.toNode.getCenter().y;
      drawConn(g, c.color, x1, y1, x2, y2);
    }
      
    // Draw the range indicators and broadcasts
    Iterator<RangeIndicator> j = riStore.values().iterator();
    while (j.hasNext()) {
      RangeIndicator ri = j.next();

      // Draw the range ring
      ri.drawRange(g);

      if (ri.isActive()) {
        ri.drawBroadcast(g);
      }

    }
    

  }



  private Timer animationTimer = new Timer(33, new ActionListener() {
                                 public void actionPerformed(ActionEvent e) {
                                   anicount++;
                                 }
                               });
  static int    anicount       = 0;
  static int    connLifeTime   = 0;

  public static void setSimSpeed(int speed) {
    // There's no science here, I've just been guesstimating to arrive at this
    // multiplier.
    connLifeTime = speed * 4 + 60;
  }

  private static int xPoints[] = new int[4];
  private static int yPoints[] = new int[4];
  static public void drawThickLine(
      Graphics g, int x1, int y1, int x2, int y2, int thickness, Color c) {
      // The thick line is in fact a filled polygon
      g.setColor(c);
      int dX = x2 - x1;
      int dY = y2 - y1;
      // line length
      double lineLength = Math.sqrt(dX * dX + dY * dY);

      double scale = (double)(thickness) / (2 * lineLength);

      // The x,y increments from an endpoint needed to create a rectangle...
      double ddx = -scale * (double)dY;
      double ddy = scale * (double)dX;
      ddx += (ddx > 0) ? 0.5 : -0.5;
      ddy += (ddy > 0) ? 0.5 : -0.5;
      int dx = (int)ddx;
      int dy = (int)ddy;

      // Now we can compute the corner points...
      int xPoints[] = new int[4];
      int yPoints[] = new int[4];

      xPoints[0] = x1 + dx; yPoints[0] = y1 + dy;
      xPoints[1] = x1 - dx; yPoints[1] = y1 - dy;
      xPoints[2] = x2 - dx; yPoints[2] = y2 - dy;
      xPoints[3] = x2 + dx; yPoints[3] = y2 + dy;

      g.fillPolygon(xPoints, yPoints, 4);
   }

  
  static void drawConn(Graphics g, Color c, int x1, int y1, int x2, int y2) {

    g.setColor(c);
    int fatness = 5;
    if(c != Color.BLUE) {
      drawThickLine(g,x1, y1, x2, y2, fatness, c);
    }
    else {
      fatness = 1;
      g.drawLine(x1, y1, x2, y2);
    }

    
    double stepX = (double) (x1 - x2) / 60;
    double stepY = (double) (y1 - y2) / 60;


    g.fillRect(x1 - (int) (stepX * (anicount % 60)), y1
        - (int) (stepY * (anicount % 60)), 3+fatness*2, 3+fatness*2);
  }

  @Override
  public void componentHidden(ComponentEvent arg0) {
    // TODO Auto-generated method stub
    updateSize();

  }

  @Override
  public void componentMoved(ComponentEvent arg0) {
    updateSize();
    // TODO Auto-generated method stub

  }

  @Override
  public void componentResized(ComponentEvent arg0) {
    updateSize();
  }

  @Override
  public void componentShown(ComponentEvent arg0) {
    updateSize();
    // TODO Auto-generated method stub

  }


  private static Color defaultRangeColor = new Color(150,150,150);  
  private static Color selectedRangeColor = new Color(255,153,51);
  
  class Connection {
    GNode   fromNode;
    GNode   toNode;
    int     dieCount;
    int     startCount;
    boolean marked2Die = false;
    Color   color;

    public boolean shouldDie() {
      if (Animations.anicount >= dieCount || Animations.anicount < startCount
          || marked2Die)
        return true;
      else
        return false;
    }

    Connection(GNode fromNode, GNode toNode, Color color, int longevityFactor) {
      this.fromNode = fromNode;
      this.toNode = toNode;
      this.color = color;
      startCount = Animations.anicount;
      dieCount = startCount + Animations.connLifeTime * longevityFactor;
    }

    public boolean equals(Object b) {
      Connection B = (Connection) b;
      if (fromNode != B.fromNode)
        return false;
      if (toNode != B.toNode)
        return false;
      return true;
    }
  }

  public void dropConns(GNode n) {
    for (Connection conn : connStore) {
      if (conn.fromNode == n || conn.toNode == n) {
        conn.marked2Die = true;
      }
    }
  }

  public void removeConn(Connection c) {
    for (Connection conn : connStore) {
      if (conn.equals(c)) {
        conn.marked2Die = true;
        return;
      }
    }

  }

  public void dropAll() {
    for (Connection conn : connStore) {
      conn.marked2Die = true;
    }
  }

  public void updateSize() {
    this.setSize(getParent().getSize());
  }

  class RangeIndicator {
    GNode parent_;

    RangeIndicator(GNode parent) {
      // Copy in attributes
      parent_ = parent;
    }

    public void fireBroadcast() {
      startStep = Animations.anicount;
    }

    public void drawRange(Graphics g) {

      // System.out.println("painting ranger");
      Graphics2D g2 = (Graphics2D) g;

      // Draw the graphic
      if (parent_.isSelected()) {
        g2.setColor(selectedRangeColor);
        g2.drawOval(parent_.getCenter().x - parent_.getRange()+2,
            parent_.getCenter().y - parent_.getRange()+2,
            parent_.getRange() * 2 - 6, parent_.getRange() * 2 - 6);
        g2.drawOval(parent_.getCenter().x - parent_.getRange()+1,
            parent_.getCenter().y - parent_.getRange()+1,
            parent_.getRange() * 2 - 4, parent_.getRange() * 2 - 4);
        g2.drawOval(parent_.getCenter().x - parent_.getRange(),
            parent_.getCenter().y - parent_.getRange(),
            parent_.getRange() * 2 - 2, parent_.getRange() * 2 - 2);
      } else {
        g2.setColor(defaultRangeColor);
        g2.drawOval(parent_.getCenter().x - parent_.getRange(),
            parent_.getCenter().y - parent_.getRange(),
            parent_.getRange() * 2 - 2, parent_.getRange() * 2 - 2);
      }
    }

    public void drawBroadcast(Graphics g) {
      Graphics2D g2 = (Graphics2D) g;
      // Draw the graphic

      int bCastRadius = (int) (parent_.getRange() * ((double) curStep() / (double) totalSteps));
      int bCastX =  parent_.getCenter().x - bCastRadius;
      int bCastY =  parent_.getCenter().y - bCastRadius;

      g2.setColor(Color.GREEN);
      // draw a 3 pixel circle
      g2.drawOval(bCastX, bCastY, bCastRadius * 2, bCastRadius * 2);
      g2.drawOval(bCastX + 1, bCastY + 1, bCastRadius * 2, bCastRadius * 2 - 1);
      g2.drawOval(bCastX + 2, bCastY + 2, bCastRadius * 2, bCastRadius * 2 - 2);
    }

    int startStep;
    int totalSteps = 30;

    public boolean isActive() {
      return (Animations.anicount - startStep < totalSteps && Animations.anicount > startStep);
    }

    public int curStep() {
      return Animations.anicount - startStep;
    }

  }

  public void addRangeIndicator(GNode g) {
    riStore.put(g, new RangeIndicator(g));
  }

  public void removeRangeIndicator(GNode g) {

    RangeIndicator ri = riStore.get(g);
    riStore.remove(g);
  }

}
