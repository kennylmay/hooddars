package dars.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.util.*;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import dars.gui.Connections.Animator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerListener;

public class Connections extends JPanel implements ComponentListener {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private static Connections getInstance() {
    return inst;
  }
  private static Connections inst;
  public Connections(JLayeredPane parent) {
    inst = this;
    parent.add(this, JLayeredPane.DEFAULT_LAYER);
    parent.addComponentListener(this);
    setOpaque(false);
    setLocation(0,0);
    Animator.start();
  }

  public void paintComponent(Graphics g) {

    Iterator<Connection> i = connStore.iterator();
    while (i.hasNext()) {

      Connection c = i.next();
      if (c.shouldDie()) {
        // remove it from the store
        i.remove();
        continue;
      }

      // draw the connection
      int x1, y1, x2, y2;
      x1 = c.fromNode.getCenter().x;
      y1 = c.fromNode.getCenter().y;
      x2 = c.toNode.getCenter().x;
      y2 = c.toNode.getCenter().y;
      Animator.draw(g, c.color, x1, y1, x2, y2);
    }

  }

  private List<Connection> connStore = new LinkedList<Connection>();

  public void traceMsg(GNode A, GNode B, Color color) {
    Connection c = new Connection(A, B, color);
    connStore.add(c);
  }

  static class Animator {
    static public int counter            = 1;
    static final int  countMax           = 20;
    static public int lifeTimeCounter    = 0;
    static final int  lifeTimeCounterMax = 32768;
    static Timer      timer              = new Timer(100, new ActionListener() {

                                           public void actionPerformed(
                                               ActionEvent arg0) {
                                             // update the counters
                                             Animator.counter++;
                                             Animator.lifeTimeCounter++;
                                             if (Animator.counter == countMax) {
                                               Animator.counter = 1;
                                             }
                                             if (Animator.lifeTimeCounter == lifeTimeCounterMax) {
                                               Animator.lifeTimeCounter = 0;
                                             }
                                             //repaint the connections panel
                                               Connections.getInstance().repaint();
                                           }
                                         });

    static void start() {
      timer.start();
    }

    static void stop() {
      timer.stop();
    }


    
    static void draw(Graphics g, Color c,int x1, int y1, int x2, int y2) {

      g.setColor(c);
      g.drawLine(x1, y1, x2, y2);
      double stepX = (double) (x1 - x2) / countMax;
      double stepY = (double) (y1 - y2) / countMax;

      g.fillRect(x1 - (int) (stepX * counter), y1 - (int) (stepY * counter), 3,
          3);
    }

    static int lifetime;
    private static int simSpeed;
    public static void setSimSpeed(int speed) {
      //There's no science here, I've just been guesstimating to arrive at this multiplier.
      Animator.lifetime = speed * 4 + 15;
    }
  }

  class Connection {
    GNode     fromNode;
    GNode     toNode;
    int       dieCount;
    int       startCount;
    boolean   marked2Die = false;
    Color     color;
    public boolean shouldDie() {
      if (Animator.lifeTimeCounter >= dieCount
          || Animator.lifeTimeCounter < startCount || marked2Die)
        return true;
      else
        return false;
    }

    Connection(GNode fromNode, GNode toNode, Color color) {
      this.fromNode = fromNode;
      this.toNode = toNode;
      this.color = color;
      startCount = Animator.lifeTimeCounter;
      dieCount = startCount + Animator.lifetime;
      if (Animator.lifeTimeCounter + Animator.lifetime > Animator.lifeTimeCounterMax) {
        dieCount = Animator.lifetime - Animator.lifeTimeCounterMax;
      }

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
   System.out.println("size updated.");
   this.setSize(getParent().getSize()); 
  }
  
  @Override 
  public void componentHidden(ComponentEvent arg0) {
    updateSize();
    
  }

  @Override
  public void componentMoved(ComponentEvent arg0) {
    updateSize();
    
  }

  @Override
  public void componentResized(ComponentEvent arg0) {
    updateSize();
    
  }

  @Override
  public void componentShown(ComponentEvent arg0) {
    updateSize();
    
  }

}
