package dars.gui;

import javax.swing.*;
import javax.swing.border.Border;

import dars.NodeInspector;
import dars.OutputConsumer;
import dars.event.DARSEvent;
import dars.gui.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI extends JFrame implements OutputConsumer {

  /**
   * 
   */
  private static final long  serialVersionUID    = 1L;
  private JPanel             simPanel            = new JPanel();
  private JPanel             logPanel            = new JPanel();
  private JPanel             nodeAttributesPanel = new JPanel();

  private LogArea            logArea             = new LogArea();
  private NodeAttributesArea nodeAttributesArea  = new NodeAttributesArea();
  private SimArea            simArea             = new SimArea();

  // Creating the menu bar and all of its elements
  private JMenuBar menuBar = new JMenuBar(); 
  private JMenu simMenu = new JMenu("Simulation");
  private JMenu newMenu = new JMenu("New");
  private JMenuItem saveMenu = new JMenuItem("Save");
  private JMenuItem aodvMenu = new JMenuItem("AODV");
  private JMenuItem dsdvMenu = new JMenuItem("DSDV");
  private JMenuItem clearMenu = new JMenuItem("Clear");
  private JMenuItem exitMenu = new JMenuItem("Exit");
  private JMenu importMenu = new JMenu("Import");
  private JMenuItem setupMenu = new JMenuItem("Setup");
  private JMenuItem replayMenu = new JMenuItem("Replay");
  private JMenu helpMenu = new JMenu("Help");
  private JMenuItem webMenu = new JMenuItem("Web Reference");
  
  // If someone knows a better way to align this stuff please feel free.
  private JLabel simTypeLabel = new JLabel("                                 Simulation Type: ");
  JLabel typeLabel = new JLabel("    ");
  
  private JButton playButton = new JButton("Play");
  private JButton pauseButton = new JButton("Pause");
  private JButton stopButton = new JButton("Stop");
  
  // Labels slider bar for the speed adjustment
  private JLabel speedLabel = new JLabel("                                                                            Speed           ");
  private ImageIcon minusIcon = new ImageIcon("img/minus.png");
  private JLabel slowerLabel = new JLabel(minusIcon);
  private JSlider slideBar = new JSlider();
  private ImageIcon plusIcon = new ImageIcon("img/plus.png");
  private JLabel fasterLabel = new JLabel(plusIcon);
  
  // Again if someone can do better please do.
  private JLabel spaceLabel = new JLabel("                                      ");
  
  public GUI() {
    
    // Add the sim menu help menu to the menu bar
    menuBar.add(simMenu);
    menuBar.add(helpMenu);
    
    // Add the web help menu to the menu bar
    helpMenu.add(webMenu);
 
    // Add elements to the sim menu and their sub menus
    simMenu.add(newMenu);
    newMenu.add(aodvMenu);
    newMenu.add(dsdvMenu);
    simMenu.add(saveMenu);
    simMenu.add(importMenu);
    importMenu.add(setupMenu);
    importMenu.add(replayMenu);
    simMenu.add(clearMenu);
    simMenu.add(exitMenu);
    
    // Add the simulation type menu lables
    menuBar.add(simTypeLabel);
    menuBar.add(typeLabel);
    menuBar.add(spaceLabel);
    
    // Add the Play, pause, and stop buttons 
    menuBar.add(playButton);
    menuBar.add(pauseButton);
    menuBar.add(stopButton);
    
    // Add the slider bar, set its properties and values.
    menuBar.add(speedLabel);
    menuBar.add(slowerLabel);
    menuBar.add(slideBar);
    slideBar.setSnapToTicks(true);
    slideBar.setMinimum(0);
    slideBar.setMaximum(10);
    slideBar.setValue(5);
    menuBar.add(fasterLabel);
    
    this.setJMenuBar(menuBar);
    

    aodvMenu.addActionListener(
      new ActionListener(){
        public void actionPerformed(ActionEvent e) {
          typeLabel.setText("AODV");
        }
      });
    
    
    
    // Tell this JFrame to exit the program when this window closes
    this.setDefaultCloseOperation(EXIT_ON_CLOSE);

    // Setup a new layout for the outermost part of the frame. We'll use the
    // border layout.
    this.setLayout(new BorderLayout());

    // Allocate as follows:
    /*
     * _________________ | | | | | | | CENTER | <-|-- EAST | | | | | |
     * |_____________|___|
     */
    // Add a center panel, this will serve us merely in a layout capacity.
    JPanel subpanel = new JPanel();
    this.add(subpanel, BorderLayout.CENTER);

    // Add the east panel.
    nodeAttributesPanel.setLayout(new BorderLayout());
    nodeAttributesPanel.add(nodeAttributesArea, BorderLayout.CENTER);
    this.add(nodeAttributesPanel, BorderLayout.EAST);

    /*
     * Elaborate upon the layout of the subpanel. Do this: ______________ | | |
     * | | CENTER | |_____________| | | |__SOUTH______|
     */
    // Use another borderlayout for the subpanel.
    subpanel.setLayout(new BorderLayout());

    // Add the GuiCanvas to the Center part
    simPanel.setLayout(new BorderLayout());
    simPanel.add(simArea, BorderLayout.CENTER);
    subpanel.add(simPanel, BorderLayout.CENTER);

    // Add the Status log panel to the bottom part.
    logPanel.setLayout(new BorderLayout());
    logPanel.add(logArea, BorderLayout.CENTER);
    subpanel.add(logPanel, BorderLayout.SOUTH);

    // initialize communication paths between the gui objects
    coupleComponents();

    // setup the borders
    setBorders();

    // setup the preferred sizes later
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        setSizes();
      }
    });

    // Show everything
    this.setVisible(true);
  }

  private void setSizes() {
    GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment
        .getLocalGraphicsEnvironment();
    Rectangle r = graphicsEnvironment.getMaximumWindowBounds();
    setMaximizedBounds(r);
    Dimension windowSize = new Dimension(r.width, r.height);

    this.setPreferredSize(windowSize);
    logPanel.setPreferredSize(new Dimension((int) (windowSize.width * .8),
        (int) (windowSize.height * .2)));
    simPanel.setPreferredSize(new Dimension((int) (windowSize.width * .8),
        (int) (windowSize.height * .8)));
    nodeAttributesPanel.setPreferredSize(new Dimension(
        (int) (windowSize.width * .2), (int) (windowSize.height)));

    pack();

  }

  private void setBorders() {
    Border raisedBevel, loweredBevel, compound;
    raisedBevel = BorderFactory.createRaisedBevelBorder();
    loweredBevel = BorderFactory.createLoweredBevelBorder();
    compound = BorderFactory.createCompoundBorder(raisedBevel, loweredBevel);
    simPanel.setBorder(compound);
    logPanel.setBorder(compound);
    nodeAttributesPanel.setBorder(compound);

  }

  private void coupleComponents() {

    // The node attributes panel needs to listen for node changes (mouse overs,
    // selects, etc)
    simArea.addNodeListener(nodeAttributesArea);

    // The node attributes panel needs be able to augment the sim area
    // (selecting a new node, etc)
    nodeAttributesArea.setSimArea(simArea);

  }

  public void setNodeInspector(NodeInspector ni) {
    //Give it to the nodeAttributesArea instance
    nodeAttributesArea.setNodeInspector(ni);
    
  }

  private class ThreadSafeConsumer implements Runnable {
    public DARSEvent e;

    public void run() {
      switch (e.eventType) {
      case OUT_ADD_NODE:
        //Add the node
        simArea.addNewNode(e.nodeX, e.nodeY, e.nodeRange, e.nodeId);
        
      case OUT_MOVE_NODE:
        //Move the node
        simArea.moveNode(e.nodeId, e.nodeX, e.nodeY);
        break;
      case OUT_EDIT_NODE:
        //Refresh the node attributes panel
        nodeAttributesArea.setNode(e.nodeId);
        break;
      
      case OUT_DEBUG:
        logArea.appendLog("DEBUG : " + e.informationalMessage);
        break;
      }
    }
  }

  public void consumeOutput(DARSEvent e) {
    // schedule the event to be processed later so as to not disturb the gui's
    // event thread
    ThreadSafeConsumer c = new ThreadSafeConsumer();
    
    /// Copy the event to the thread safe consumer instance
    c.e = e;
    
    //Invoke it later; This will push the runnable instance onto the 
    //Java Event Dispatching thread 
    SwingUtilities.invokeLater(c);

  }


}
