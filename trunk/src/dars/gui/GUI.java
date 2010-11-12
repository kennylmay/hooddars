package dars.gui;

import javax.swing.*;

import javax.swing.border.Border;
import dars.NodeInspector;
import dars.OutputConsumer;
import dars.event.DARSEvent;
import java.awt.*;

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

  private DARSAppMenu        menuArea            = new DARSAppMenu();

  


  public GUI() {



   

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
    
    //attach the menu area. 
    attachMenus();
    
    this.add(subpanel, BorderLayout.CENTER);

    // Add the east panel.
    nodeAttributesPanel.setLayout(new BorderLayout());
    nodeAttributesPanel.add(nodeAttributesArea, BorderLayout.CENTER);
    

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
    logPanel.add(nodeAttributesPanel, BorderLayout.EAST);
    subpanel.add(logPanel, BorderLayout.SOUTH);

    // initialize communication paths between the gui objects
    coupleComponents();

    // setup the borders
    setBorders();

    // setup the sizes of the panels
    setSizes();

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
    simPanel.setPreferredSize(new Dimension((int) (windowSize.width),
        (int) (windowSize.height * .8)));
    nodeAttributesPanel.setPreferredSize(new Dimension(
        (int) (windowSize.width * .2), (int) (windowSize.height * .2)));

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
   
    // The node attributes panel and DARSAppMenu needs be able to augment the sim area
    // (selecting a new node, etc)
    nodeAttributesArea.setSimArea(simArea);
    menuArea.setSimArea(simArea);
    simArea.setNodeAttributesArea(nodeAttributesArea);

  }

  public void setNodeInspector(NodeInspector ni) {
    // Give it to the nodeAttributesArea instance
    nodeAttributesArea.setNodeInspector(ni);

  }

  private class ThreadSafeConsumer implements Runnable {
    public DARSEvent e;

    public void run() {
      switch (e.eventType) {
      case OUT_ADD_NODE:
        // Add the node
        simArea.addNewNode(e.nodeX, e.nodeY, e.nodeRange, e.nodeId);
        nodeAttributesArea.nodeAdded(e.nodeId);
        
        //select the node
        simArea.selectNode(e.nodeId);
        nodeAttributesArea.selectNodeById(e.nodeId);
        
        
        break;

      case OUT_MOVE_NODE:
        // Move the nodenodeSelectorComboBox
        simArea.moveNode(e.nodeId, e.nodeX, e.nodeY);
        break;

      case OUT_SET_NODE_RANGE:
        // Refresh the node attributes panel
        nodeAttributesArea.setNode(e.nodeId);
        simArea.setNodeRange(e.nodeId, e.nodeRange);
        break;

      case OUT_MSG_TRANSMITTED:
        // Animate the event
          simArea.traceMessage(e.sourceId, e.destinationId, Color.BLUE);
        break;

      case OUT_NODE_BROADCAST:
        // Animate the event
        simArea.nodeBroadcast(e.nodeId);
        break;
        
      case OUT_DEL_NODE:
        // Remove the node
        simArea.deleteNode(e.nodeId);
        nodeAttributesArea.nodeDeleted(e.nodeId);
        break;

      case OUT_DEBUG:
        logArea.appendLog("DEBUG: " + e.informationalMessage, e.currentQuantum);
        break;
      case OUT_ERROR:
        logArea.appendLog("ERROR: " + e.informationalMessage, e.currentQuantum);
        break;
        
      case OUT_QUANTUM_ELAPSED:
        menuArea.quantumElapsed();
        nodeAttributesArea.updateNodeDialogs();
        break;

      case OUT_SIM_SPEED: 
        simArea.setSimSpeed(e.newSimSpeed);
        break;
        
      case OUT_START_SIM:
        //Notify the menu that a sim has started
        menuArea.simStarted();
        break;

      case OUT_STOP_SIM:
        //Notify the menu that the sim has stopped
        menuArea.simStopped();
        simArea.simStopped();
        break;

      case OUT_PAUSE_SIM:
        //Notify the menu the the sim has paused
        menuArea.simPaused();
        simArea.simPaused();
        break;

      case OUT_RESUME_SIM:
        //Notify the menu that the sim has resumed
        menuArea.simResumed();
        break;

      case OUT_CLEAR_SIM:
        //Clear the sim area.
        nodeAttributesArea.clear();
        simArea.clear();
        logArea.clear();
        
        break;
     
      case OUT_NEW_SIM:
        //Clear the sim area.
        simArea.clear();
        
        //Unlock the sim area
        simArea.setLocked(false);
        
        //Let the menu area know that a new sim has been created
        menuArea.newSim();

      }
    }
  }

  public void consumeOutput(DARSEvent e) {
    // schedule the event to be processed later so as to not disturb the gui's
    // event thread
    ThreadSafeConsumer c = new ThreadSafeConsumer();

    // / Copy the event to the thread safe consumer instance
    c.e = e;

    // Invoke it later; This will push the runnable instance onto the
    // Java Event Dispatching thread
    SwingUtilities.invokeLater(c);

  }

  private void attachMenus() {
    add(menuArea.getActionPanel(), BorderLayout.NORTH);
    this.setJMenuBar(menuArea.getMenuBar());
  }

}
