package dars.gui;


import javax.swing.*;

import dars.gui.*;

import java.awt.*;
public class DARSApp extends JFrame {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public static void main(String args[]) {
    new DARSApp();
  }
  
  DARSApp() {
    this.pack();
    // Tell this JFrame to exit the program when this window closes
    this.setDefaultCloseOperation(EXIT_ON_CLOSE);

    // Set the size of this window.
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    this.setSize(screenSize.width, screenSize.height);

    // Setup a new layout for the outermost part of the frame. We'll use the border layout.
    this.setLayout(new BorderLayout());
    
    // Allocate as follows:
    /*
         _________________
        |             |   |
        |             |   |
        |  CENTER     | <-|-- EAST
        |             |   |
        |             |   |
        |_____________|___|

    */
    // Add a center panel, this will serve us merely in a layout capacity.
    JPanel subpanel = new JPanel();
    this.add(subpanel, BorderLayout.CENTER);
    
    
    // Add the east panel. This is a placeholder for the "Node Information" panel.
    JPanel node_info_panel = new JPanel();
    node_info_panel.setPreferredSize(new Dimension(300, 100));
    node_info_panel.add(new JButton("ASDF"));

    this.add(new JScrollPane(node_info_panel), BorderLayout.EAST);
   
    /* Elaborate upon the layout of the subpanel. Do this:
         ______________
        |             |
        |             |
        |  CENTER     |
        |_____________|
        |             |
        |__SOUTH______|

   */
   //Use another borderlayout for the subpanel.
   subpanel.setLayout(new BorderLayout());

   //Add the GuiCanvas to the Center part
   subpanel.add(new JScrollPane(new SimArea()), BorderLayout.CENTER);
   
   //Add the Status log to the bottom part. This is a placeholder for the Status Log panel.
   JScrollPane slog_panel = new JScrollPane();
   JButton tst = new JButton("LOL");
   slog_panel.add(tst);
   slog_panel.setPreferredSize(new Dimension(1,200));
   subpanel.add(slog_panel, BorderLayout.SOUTH);

   //Show everything
   this.setVisible(true);
  }
  

};

