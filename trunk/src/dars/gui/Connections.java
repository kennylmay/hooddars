package dars.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.util.*;
import javax.swing.Timer;

import dars.gui.Connections.Animator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Connections {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public Connections() {
    Animator.start();
  }

  public void draw(Graphics g) {

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
      Animator.draw(g, x1, y1, x2, y2);
    }

  }

  private ArrayList<Connection> connStore = new ArrayList<Connection>();

  public void traceMsg(GNode A, GNode B) {
    Connection c = new Connection(A, B);
    connStore.add(c);
  }

  static class Animator {
    static public int counter            = 1;
    static final int  countMax           = 30;
    static public int lifeTimeCounter    = 0;
    static final int  lifeTimeCounterMax = 32768;
    static Timer      timer              = new Timer(200, new ActionListener() {

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
                                           }
                                         });

    static void start() {
      timer.start();
    }

    static void stop() {
      timer.stop();
    }

    static void draw(Graphics g, int x1, int y1, int x2, int y2) {

      g.drawLine(x1, y1, x2, y2);
      double stepX = (double) (x1 - x2) / countMax;
      double stepY = (double) (y1 - y2) / countMax;

      g.setColor(Color.BLUE);
      g.fillRect(x1 - (int) (stepX * counter), y1 - (int) (stepY * counter), 3,
          3);
    }

  }

  class Connection {
    GNode     fromNode;
    GNode     toNode;
    int       dieCount;
    int       startCount;
    boolean   marked2Die = false;
    final int lifetime   = 20;

    public boolean shouldDie() {
      if (Animator.lifeTimeCounter >= dieCount
          || Animator.lifeTimeCounter < startCount || marked2Die)
        return true;
      else
        return false;
    }

    Connection(GNode fromNode, GNode toNode) {
      this.fromNode = fromNode;
      this.toNode = toNode;
      startCount = Animator.lifeTimeCounter;
      dieCount = startCount + lifetime;
      if (Animator.lifeTimeCounter + lifetime > Animator.lifeTimeCounterMax) {
        dieCount = lifetime - Animator.lifeTimeCounterMax;
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

}
