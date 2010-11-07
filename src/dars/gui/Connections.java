package dars.gui;

import java.awt.Graphics;
import java.util.*;
import javax.swing.*;
import javax.swing.Timer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Connections {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public void draw(Graphics g) {

    Iterator<Connection> i = connStore.iterator();
    while (i.hasNext()) {

      Connection c = i.next();
      if (c.shouldDie) {
        // stop the animation
        c.animator.stop();

        // remove it from the store
        i.remove();
        continue;
      }

      // draw the connection
      c.animator.draw(g);
    }

  }

  public void traceMsg(GNode A, GNode B, int lifetime) {
    Connection c = new Connection(A, B);
    removeConn(c);
    connStore.add(c);
    c.animator.start();
    Timer t = new Timer(lifetime, new Destroyer(c));
    t.setRepeats(false);
    t.setInitialDelay(lifetime);
    t.start();
  }

  public class Destroyer implements ActionListener {

    Connection c;

    public Destroyer(Connection c) {
      this.c = c;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      removeConn(c);
    }
  }

  class Connection {
    GNode            fromNode;
    GNode            toNode;
    int              counter   = 1;
    volatile boolean shouldDie = false;

    Connection(GNode fromNode, GNode toNode) {
      this.fromNode = fromNode;
      this.toNode = toNode;
    }

    public boolean equals(Object b) {
      Connection B = (Connection) b;
      if (fromNode != B.fromNode)
        return false;
      if (toNode != B.toNode)
        return false;
      return true;
    }

    private Animator animator = new Animator();

    class Animator implements ActionListener {
      int       counter  = 1;
      final int countMax = 30;
      Timer     timer    = new Timer(100, this);

      void start() {
        timer.start();
      }

      void stop() {
        timer.stop();
      }

      void draw(Graphics g) {

        // Draw a line from fromNode to
        int x1, x2, y1, y2;
        x1 = fromNode.getCenter().x;
        y1 = fromNode.getCenter().y;
        x2 = toNode.getCenter().x;
        y2 = toNode.getCenter().y;

        g.drawLine(x1, y1, x2, y2);
        double stepX = (double) (x1 - x2) / countMax;
        double stepY = (double) (y1 - y2) / countMax;

        Point a = new Point(x1, y1);
        Point b = new Point(x2, y2);

        g.fillRect(x1 - (int) (stepX * counter), y1 - (int) (stepY * counter),
            10, 10);
      }

      @Override
      public void actionPerformed(ActionEvent arg0) {
        // update the counter
        counter++;

        //System.out.println("updating..");
        if (counter == countMax) {
          counter = 1;
        }
      }
    }
  }

  public void dropConns(GNode n) {
    for (Connection conn : connStore) {
      if (conn.fromNode == n || conn.toNode == n) {
        conn.shouldDie = true;
      }
    }
  }

  public void removeConn(Connection c) {
    for (Connection conn : connStore) {
      if (conn.equals(c)) {
        conn.shouldDie = true;
        return;
      }
    }

  }

  public void dropAll() {
    for (Connection conn : connStore) {
      conn.shouldDie = true;
    }

  }

  private ArrayList<Connection> connStore = new ArrayList<Connection>();
}
