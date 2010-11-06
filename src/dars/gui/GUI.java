package dars.gui;

import javax.swing.*;

import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import dars.InputHandler;
import dars.NodeInspector;
import dars.OutputConsumer;
import dars.event.DARSEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

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

  private JPanel             menuPanel           = new JPanel();

  // Creating the menu bar and all of its elements
  private JMenuBar           menuBar             = new JMenuBar();
  private JMenu              simMenu             = new JMenu("Simulation");
  private JMenu              newMenu             = new JMenu("New");
  private JMenuItem          saveMenu            = new JMenuItem("Save");
  private JMenuItem          aodvMenu            = new JMenuItem("AODV");
  private JMenuItem          dsdvMenu            = new JMenuItem("DSDV");
  private JMenuItem          clearMenu           = new JMenuItem("Clear");
  private JMenuItem          exitMenu            = new JMenuItem("Exit");
  private JMenu              importMenu          = new JMenu("Import");
  private JMenuItem          setupMenu           = new JMenuItem("Setup");
  private JMenuItem          replayMenu          = new JMenuItem("Replay");
  private JMenu              helpMenu            = new JMenu("Help");
  private JMenuItem          webMenu             = new JMenuItem(
                                                     "Web Reference");

  // If someone knows a better way to align this stuff please feel free.
  private JLabel             simTypeLabel        = new JLabel(
                                                     "Simulation Type: ");
  JLabel                     typeLabel           = new JLabel("NONE");

  private JPanel             buttonArea          = new JPanel();
  private JButton            playButton          = new JButton("Play");
  private JButton            resumeButton        = new JButton("Resume");
  private JButton            pauseButton         = new JButton("Pause");
  private JButton            stopButton          = new JButton("Stop");

  private JPanel             speedArea           = new JPanel();
  private JPanel             simTypeArea         = new JPanel();

  // Labels slider bar for the speed adjustment
  private JLabel             speedLabel          = new JLabel("Speed");
  private ImageIcon          minusIcon           = new ImageIcon(
                                                     "img/minus.png");
  private JLabel             slowerLabel         = new JLabel(minusIcon);
  private JSlider            slideBar            = new JSlider();
  private ImageIcon          plusIcon            = new ImageIcon("img/plus.png");
  private JLabel             fasterLabel         = new JLabel(plusIcon);

  public GUI() {

    aodvMenu.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        typeLabel.setText("AODV");
        InputHandler.dispatch(DARSEvent.inClearSim());
        playButton.setEnabled(true);
        stopButton.setEnabled(true);
        pauseButton.setEnabled(true);
        simArea.setLocked(false);
      }
    });

    dsdvMenu.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        typeLabel.setText("DSDV");
      }
    });

    clearMenu.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        InputHandler.dispatch(DARSEvent.inClearSim());
      }
    });

    saveMenu.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Log Files", "log");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showSaveDialog(getParent());
        if (returnVal == JFileChooser.APPROVE_OPTION) {

          // Define the new files to be saved.
          File logFile = new File("darslog.tmp");
          File saveFile = new File(chooser.getSelectedFile().getPath()+".log");

          // Initialize the file readers and writers
          FileReader in = null;
          FileWriter out = null;

          // Try to open each file
          try {
            int c;
            in = new FileReader(logFile);
            out = new FileWriter(saveFile);
            // Write each line of the first file to the file chosen.
            while ((c = in.read()) != -1) {
              out.write(c);
            }
            
            // Close both files.
            in.close();
            out.close();

          } catch (FileNotFoundException e1) {
            JOptionPane.showMessageDialog(getParent(),
                "Log file could not be saved at"
                    + chooser.getSelectedFile().getPath());
          } catch (IOException e1) {
            e1.printStackTrace();
          }
        }
      }
    });

    setupMenu.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {

      }
    });

    replayMenu.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        
      }
    });

    exitMenu.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        
      }
    });

    helpMenu.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {

      }
    });

    playButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        InputHandler.dispatch(DARSEvent.inStartSim());
      }
    });

    pauseButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        InputHandler.dispatch(DARSEvent.inPauseSim());
      }
    });

    stopButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        InputHandler.dispatch(DARSEvent.inStopSim());
      }
    });

    resumeButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        InputHandler.dispatch(DARSEvent.inResumeSim());
      }
    });

    slideBar.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent arg0) {
        InputHandler.dispatch(DARSEvent.inSimSpeed(slideBar.getValue()));
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
    initMenuBar();
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

      case OUT_MOVE_NODE:
        // Move the nodenodeSelectorComboBox
        simArea.moveNode(e.nodeId, e.nodeX, e.nodeY);
        break;

      case OUT_SET_NODE_RANGE:
        // Refresh the node attributes panel
        simArea.setNodeRange(e.nodeId, e.nodeRange);
        nodeAttributesArea.setNode(e.nodeId);
        break;

      case OUT_MSG_TRANSMITTED:
        // Animate the event
        simArea.traceMessage(e.sourceId, e.destinationId);
        break;

      case OUT_DEL_NODE:
        // Remove the node
        simArea.deleteNode(e.nodeId);
        nodeAttributesArea.nodeDeleted(e.nodeId);
        break;

      case OUT_DEBUG:
        logArea.appendLog("DEBUG: " + e.informationalMessage);
        break;
      case OUT_ERROR:
        logArea.appendLog("ERROR: " + e.informationalMessage);
        break;

      case OUT_START_SIM:
        playButton.setEnabled(false);
        pauseButton.setEnabled(true);
        stopButton.setEnabled(true);
        break;

      case OUT_STOP_SIM:
        stopButton.setEnabled(false);
        playButton.setEnabled(false);
        pauseButton.setEnabled(false);
        break;

      case OUT_PAUSE_SIM:
        playButton.setEnabled(false);
        stopButton.setEnabled(false);
        pauseButton.setVisible(false);
        resumeButton.setVisible(true);
        break;

      case OUT_RESUME_SIM:
        stopButton.setEnabled(true);
        pauseButton.setVisible(true);
        resumeButton.setVisible(false);
        break;

      case OUT_CLEAR_SIM:
        break;

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

  private void initMenuBar() {

    menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.LINE_AXIS));

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

    menuBar.add(simMenu);
    menuBar.add(helpMenu);

    // Add the simulation type menu lables
    simTypeArea.add(simTypeLabel);
    simTypeArea.add(typeLabel);
    menuPanel.add(simTypeArea);

    // Add the Play, pause, and stop buttons
    buttonArea.add(playButton);
    buttonArea.add(pauseButton);
    buttonArea.add(resumeButton);
    buttonArea.add(stopButton);
    resumeButton.setVisible(false);
    playButton.setEnabled(false);
    stopButton.setEnabled(false);
    pauseButton.setEnabled(false);
    menuPanel.add(buttonArea);

    // Add the slider bar, set its properties and values.
    speedArea.add(speedLabel);
    speedArea.add(fasterLabel);
    speedArea.add(slideBar);
    slideBar.setSnapToTicks(true);
    slideBar.setMinimum(1);
    slideBar.setMaximum(20);
    slideBar.setValue(5);
    speedArea.add(slowerLabel);
    menuPanel.add(speedArea);

    menuPanel.setOpaque(false);
    add(menuPanel, BorderLayout.NORTH);
    this.setJMenuBar(menuBar);
  }

}
