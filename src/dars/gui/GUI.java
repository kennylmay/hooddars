package dars.gui;

import javax.swing.*;

import javax.swing.border.Border;

import dars.Defaults;
import dars.Message;
import dars.NodeInspector;
import dars.OutputConsumer;
import dars.Utilities;
import dars.event.DARSEvent;
import dars.logger.Logger;
import dars.replayer.Replayer.ReplayerListener;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GUI extends JFrame implements OutputConsumer, ReplayerListener {

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

  private DARSAppMenu        menuArea            = new DARSAppMenu(this);

  public GUI() {
    super("DARS Version 1.0");

    // Close the program when the frame is exited
    this.addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent e){
        // Before we exit make sure to clean up the temporary log file.
        Logger logger = Logger.getInstance();
        logger.deleteLogFile();
        System.exit(0);
      }
    });
    
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
    menuArea.setLogArea(logArea);
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
        logArea.appendLog("INFO: " + e.informationalMessage, e.currentQuantum);
        break;

      case OUT_MOVE_NODE:
        //Move the node
        simArea.moveNode(e.nodeId, e.nodeX, e.nodeY);
        
        //select the node
        simArea.selectNode(e.nodeId);
        nodeAttributesArea.selectNodeById(e.nodeId);
        
        //show the event in the visual log
        logArea.appendLog("INFO: " + e.informationalMessage, e.currentQuantum);
        break;

      case OUT_SET_NODE_RANGE:
        // Refresh the node attributes panel
        nodeAttributesArea.setNode(e.nodeId);
        simArea.setNodeRange(e.nodeId, e.nodeRange);
        logArea.appendLog("INFO: " + e.informationalMessage, e.currentQuantum);
        break;
    
      case OUT_MSG_RECEIVED:
        // Refresh the node attributes panel
        JOptionPane.showMessageDialog(null, "Successful Message Transmission!\n" +
                                            "Source Node: "+ e.sourceId + "\n" + 
                                            "Destination Node: " + e.destinationId + "\n" +
                                            "Message: " + e.transmittedMessage);
        break;
  
      case OUT_NARRMSG_RECEIVED:
        // Animate the event
        simArea.traceMessage(e.sourceId, e.destinationId, Defaults.NARRMSG_COLOR,3,5,1);
        logArea.appendLog("INFO: " + e.informationalMessage, e.currentQuantum);
        
        
        break;

      
      case OUT_NARRMSG_TRANSMITTED:
      case OUT_CONTROLMSG_TRANSMITTED:
        //If the destination is BROADCAST, animate it.
        if(e.destinationId.equals(Message.BCAST_STRING)){
          simArea.nodeBroadcast(e.sourceId);
          logArea.appendLog("INFO: " + e.informationalMessage, e.currentQuantum);
        }
        break;
        
        
      case OUT_CONTROLMSG_RECEIVED:
        // Animate the event
        simArea.traceMessage(e.sourceId, e.destinationId, Defaults.CNTRLMSG_COLOR,1, 1, 0);
        logArea.appendLog("INFO: " + e.informationalMessage, e.currentQuantum);
        break;

        
      case OUT_DEL_NODE:
        // Remove the node
        simArea.deleteNode(e.nodeId);
        nodeAttributesArea.nodeDeleted(e.nodeId);
        logArea.appendLog("INFO: " + e.informationalMessage, e.currentQuantum);
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
        //Set the starting simulation speed
        simArea.setSimSpeed(e.newSimSpeed);
        logArea.appendLog("INFO: " + e.informationalMessage, e.currentQuantum);
        break;

      case OUT_STOP_SIM:
        //Notify the menu that the sim has stopped
        menuArea.simStopped();
        simArea.simStopped();
        nodeAttributesArea.simStopped();
        logArea.appendLog("INFO: " + e.informationalMessage, e.currentQuantum);
        
        //Prompt the user to save the log
        int ret = JOptionPane.showConfirmDialog(null,
            "Simulation Completed. Would you like to save the log file?", 
            "Simulation Completed.", JOptionPane.YES_NO_OPTION);
        if(ret == JOptionPane.YES_OPTION) {
          Utilities.runSaveLogDialog(simArea);
        }
            
        break;

      case OUT_PAUSE_SIM:
        //Notify the menu the the sim has paused
        menuArea.simPaused();
        simArea.simPaused();
        nodeAttributesArea.simPaused();
        logArea.appendLog("INFO: " + e.informationalMessage, e.currentQuantum);
        break;

      case OUT_RESUME_SIM:
        //Notify the menu that the sim has resumed
        menuArea.simResumed();
        logArea.appendLog("INFO: " + e.informationalMessage, e.currentQuantum);
        break;

      case OUT_CLEAR_SIM:
        //Clear the sim area.
        nodeAttributesArea.clear();
        simArea.clear();
        logArea.clear();
        logArea.appendLog("INFO: " + e.informationalMessage, e.currentQuantum);
        break;
     
      case OUT_NEW_SIM:
        //Clear the sim area.
        simArea.clear();
        nodeAttributesArea.clear();
        //Unlock the sim area
        simArea.setLocked(false);
        
        
        //Let the menu area know that a new sim has been created
        menuArea.newSim();
        logArea.appendLog("INFO: " + e.informationalMessage, e.currentQuantum);

      }
    }
  }

  public void consumeOutput(DARSEvent e) {
    // schedule the event to be processed later so as to not disturb the gui's
    // event thread
    ThreadSafeConsumer c = new ThreadSafeConsumer();

    // Copy the event to the thread safe consumer instance
    c.e = e;

    // Invoke it later; This will push the runnable instance onto the
    // Java Event Dispatching thread
    SwingUtilities.invokeLater(c);

  }

  private void attachMenus() {
    add(menuArea.getActionPanel(), BorderLayout.NORTH);
    this.setJMenuBar(menuArea.getMenuBar());
  }

  @Override
  public void replayerStarted() {
    // TODO Auto-generated method stub
    //NOTE: must use SwingUtilities.invokeLater() here. Replayer is
    //NOT on the gui thread
    
  }

  @Override
  public void replayerFinished() {
    // TODO Auto-generated method stub
    //NOTE: must use SwingUtilities.invokeLater() here. Replayer is
    //NOT on the gui thread
    
    
  }

}
