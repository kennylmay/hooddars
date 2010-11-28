package dars;

import java.awt.Color;
import java.awt.Font;

import javax.swing.plaf.FontUIResource;

public class Defaults {
  public static final int X = 100;
  public static final int Y = 100;
  public static final boolean IS_PROMISCUOUS = false;
  public static final int RANGE = 150;
  public static final FontUIResource FONT = new FontUIResource(new Font("tahoma", Font.PLAIN, 12 ));
  public static final Color NARRMSG_COLOR = new Color(255,100,100);
  public static final Color CNTRLMSG_COLOR = new Color(100,100,255);
  public static final Color BROADCAST_COLOR = new Color(100,255,100);
  public static final Color RANGE_COLOR = new Color(150,150,150);
  public static final Color SELECTED_RANGE_COLOR = new Color(255,153,51); 
  public static final Font NODEID_FONT = new Font("tahoma", Font.BOLD, 12);
  public static final int SELECTED_NODE_RANGE_INDICATOR_THICKNESS = 3;
  public static final int BROADCAST_THICKNESS = 2;
  public static final String TITLE_STRING = "Dynamic Ad-hoc Routing Simulator";
}
