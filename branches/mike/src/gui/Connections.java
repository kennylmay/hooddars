package gui;

import java.awt.Graphics;
import java.util.*;
import javax.swing.*;
import java.awt.*;

public class Connections extends JPanel {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;


  Connections(JLayeredPane p) {
    //Set this size, position
    this.setSize(new Dimension(1,1));
    this.setLocation(new Point(0,0));  
    this.jlp_ = p;

 
    //Add this JPanel, move it to the back. 
    //This will allow us to draw behind existing elements.
    p.add(this);
    p.moveToBack(this);

    //Setup connStore
    connStore = new HashSet<HashSet<GNode>>();

  }
  @Override
  public void paintComponent(Graphics g) {
    System.out.println("painting");
    //reset the size bounds; size of window may have changed
    this.setSize(new Dimension(jlp_.getWidth(),jlp_.getHeight())); 
    Iterator<HashSet<GNode> > i = connStore.iterator();
    while(i.hasNext()) {
      HashSet<GNode> s = i.next();
      
      Iterator<GNode> ii = s.iterator();
      //make sure that we have TWO elements in the set
      assert(s.size() == 2);
      GNode A = ii.next();
      GNode B = ii.next();
      
      int x1,y1,x2,y2;
      Point p = A.getCenter();
      x1 = p.x; y1 = p.y;
      p = B.getCenter();
      x2 = p.x; y2 = p.y;

      g.drawLine(x1,y1,x2,y2);
      
    }

  }

  public void addConn(GNode A, GNode B) {
    HashSet<GNode> ns = new HashSet<GNode>();
    ns.add(A);
    ns.add(B);
    connStore.add(ns);
  }
 
  public void delConn(GNode A, GNode B) {

  }
   
  public void dropConn(GNode n) {

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

  
  private HashSet< HashSet<GNode>> connStore;
}
