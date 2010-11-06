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

    //reset the size bounds; size of window may have changed
    //this.setSize(new Dimension(jlp_.getWidth(),jlp_.getHeight()));
    
    Iterator<Connection> i = connStore.iterator();
    while(i.hasNext()) {

      Connection c = i.next();
      if(c.shouldDie) {
        i.remove();
        continue;
      }
      //Draw a line from fromNode to
      int x1,x2,y1,y2;
      x1 = c.fromNode.getCenter().x;
      y1 = c.fromNode.getCenter().y;
      x2 = c.toNode.getCenter().x;
      y2 = c.toNode.getCenter().y;
      
      
      Animator.drawDirectedPath(g,x1,y1,x2,y2);
      
      
    }

  }
  
  static class Animator {
    static int counter = 1;
    static final int countMax = 60;
    
    static void drawDirectedPath(Graphics g, int x1, int y1, int x2, int y2) {
      
      
      g.drawLine(x1,y1,x2,y2);
      double stepX =  (double)(x1-x2) / countMax;
      double stepY =  (double)(y1-y2) / countMax;
      
      
      Point a = new Point(x1,y1);
      Point b = new Point(x2,y2);
      
     
      g.fillRect(x1 - (int)( stepX * counter),
                 y1 - (int)( stepY * counter),
                 10,10);
      counter++;
      if(counter == countMax) {
        counter = 1;
      }
    }
  }

  public void traceMsg(GNode A, GNode B, int lifetime) {
    Connection c = new Connection(A,B);
    removeConn(c);
    connStore.add(c);
    
    Timer t = new Timer(lifetime, new Destroyer(c));
    t.setRepeats(false);
    t.setInitialDelay(lifetime);
    t.start();
  }
 
  public class Destroyer implements ActionListener{

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
    GNode fromNode;
    GNode toNode;
    volatile boolean shouldDie = false;
    Connection(GNode fromNode, GNode toNode) {
      this.fromNode = fromNode;
      this.toNode = toNode;
    }

    public boolean equals(Object b) {
      Connection B = (Connection)b;
      if(fromNode != B.fromNode) return false;
      if(toNode != B.toNode) return false;
      return true;
    }
  }
  

   
  public void dropConns(GNode n) {
    for(Connection conn : connStore) {
      if(conn.fromNode == n || conn.toNode == n) {
        conn.shouldDie = true;
      }
    }    
  }
  
  public void removeConn(Connection c) {
    for(Connection conn : connStore) {
      if(conn.equals(c)) {
        conn.shouldDie = true;
        return;
      }
    }
    
  }

  public void dropAll() {
    for(Connection conn : connStore) {
      conn.shouldDie = true;
    }
    
  }
   
  private ArrayList<Connection> connStore = new ArrayList<Connection>();
}
