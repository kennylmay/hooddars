package dars.gui;
import javax.swing.*;
import dars.OutputConsumer;
import dars.event.DARSEvent;
import dars.gui.*;
import java.awt.*;

public class GUI extends JFrame
implements OutputConsumer {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private LogPanel logPanel = new LogPanel();
  private NodeAttributesPanel nodeAttributesPanel = new NodeAttributesPanel();
  
  
  public GUI() {
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
    this.add(nodeAttributesPanel, BorderLayout.EAST);
   
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
   slog_panel.add(logPanel);
   slog_panel.setPreferredSize(new Dimension(1,200));
   subpanel.add(slog_panel, BorderLayout.SOUTH);

   //Show everything
   this.setVisible(true);
  }


@Override
public void consume(DARSEvent e) {
	// TODO Auto-generated method stub
	
}
  

};

