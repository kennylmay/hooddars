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


  Connections(JLayeredPane p) {

  }

  public void draw(Graphics g) {

    //reset the size bounds; size of window may have changed
    //this.setSize(new Dimension(jlp_.getWidth(),jlp_.getHeight()));
    
    Iterator<Connection> i = connStore.iterator();
    while(i.hasNext()) {

      Connection c = i.next();
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
    ActionListener destroyer = new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        
      }
    };
    
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

  }
  
  public void removeConn(Connection c) {
    for(Connection conn : connStore) {
      if(conn.equals(c)) {
        connStore.remove(conn);
        return;
      }
    }
    
  }

  
    /**
   * Draws an arrow on the given Graphics2D context
   * @param g The Graphics2D context to draw on
   * @param x The x location of the "tail" of the arrow
   * @param y The y location of the "tail" of the arrow
   * @param xx The x location of the "head" of the arrow
   * @param yy The y location of the "head" of the arrow
   */
  private void drawArrow( Graphics2D g, int x, int y, int xx, int yy )
  {
    float arrowWidth = 10.0f ;
    float theta = 0.423f ;
    int[] xPoints = new int[ 3 ] ;
    int[] yPoints = new int[ 3 ] ;
    float[] vecLine = new float[ 2 ] ;
    float[] vecLeft = new float[ 2 ] ;
    float fLength;
    float th;
    float ta;
    float baseX, baseY ;

    xPoints[ 0 ] = xx ;
    yPoints[ 0 ] = yy ;

    // build the line vector
    vecLine[ 0 ] = (float)xPoints[ 0 ] - x ;
    vecLine[ 1 ] = (float)yPoints[ 0 ] - y ;

    // build the arrow base vector - normal to the line
    vecLeft[ 0 ] = -vecLine[ 1 ] ;
    vecLeft[ 1 ] = vecLine[ 0 ] ;

    // setup length parameters
    fLength = (float)Math.sqrt( vecLine[0] * vecLine[0] + vecLine[1] * vecLine[1] ) ;
    th = arrowWidth / ( 2.0f * fLength ) ;
    ta = arrowWidth / ( 2.0f * ( (float)Math.tan( theta ) / 2.0f ) * fLength ) ;

    // find the base of the arrow
    baseX = ( xPoints[ 0 ] - ta * vecLine[0]);
    baseY = ( yPoints[ 0 ] - ta * vecLine[1]);

    // build the points on the sides of the arrow
    xPoints[ 1 ] = (int)( baseX + th * vecLeft[0] );
    yPoints[ 1 ] = (int)( baseY + th * vecLeft[1] );
    xPoints[ 2 ] = (int)( baseX - th * vecLeft[0] );
    yPoints[ 2 ] = (int)( baseY - th * vecLeft[1] );

    g.drawLine( x, y, (int)baseX, (int)baseY ) ;
    g.fillPolygon( xPoints, yPoints, 3 ) ;
  }
 
  private JLayeredPane jlp_; 

  
  private ArrayList<Connection> connStore = new ArrayList<Connection>();
}
