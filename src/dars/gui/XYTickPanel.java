package dars.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import dars.Defaults;
public class XYTickPanel extends JPanel implements  ComponentListener {
  private static final long serialVersionUID = 1L;

  public XYTickPanel(JLayeredPane parent) {
    setLocation(0,0);  
    parent.addComponentListener(this);
    setOpaque(false);
  }
  
  private static Font f = Defaults.FONT;
  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Dimension size = getSize();
    int maxX, maxY;
    maxX = size.width;
    maxY = size.height;
    
    g.setColor(Color.BLACK);
    
    //Draw the X ticks
    for(int i = 0; i < maxX; i += 5) {
      if( i % 100 == 0 && i != 0) {
        g.drawLine(i, 0, i, 10);
        g.setFont(f);
        FontMetrics fm = g.getFontMetrics();
        
        g.drawString(Integer.toString(i), 
            i - (fm.stringWidth(Integer.toString(i)) / 2),
            10 + fm.getAscent());
      }
      else if( i % 50 == 0) {
        g.drawLine(i, 0, i, 6);
      }
      else {
        g.drawLine(i, 0, i, 3);
      }
    }
    
    //Draw the Y ticks
    for(int i = 0; i < maxY; i += 5) {
      if( i % 100 == 0 && i !=0) {
        g.drawLine(0,i,10,i);
        g.setFont(f);
        FontMetrics fm = g.getFontMetrics();
        
        g.drawString(Integer.toString(i), 
            10 + 2,
            i + fm.getAscent() / 2);
      }
      else if( i % 50 == 0) {
       g.drawLine(0, i, 6, i);
      }
      else {
        g.drawLine(0,i,3,i);
      }
      
    }
    
  }
  
  @Override
  public void componentHidden(ComponentEvent arg0) {
    this.setSize(getParent().getSize());
  }

  @Override
  public void componentMoved(ComponentEvent arg0) {
    this.setSize(getParent().getSize());
  }

  @Override
  public void componentResized(ComponentEvent arg0) {
    this.setSize(getParent().getSize());
  }

  @Override
  public void componentShown(ComponentEvent arg0) {
    this.setSize(getParent().getSize());
  }

}
