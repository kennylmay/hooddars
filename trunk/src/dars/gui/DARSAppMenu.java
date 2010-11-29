package dars.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.math.BigInteger;
import java.util.Hashtable;
import java.util.Queue;
import java.util.Random;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import dars.Defaults;
import dars.InputHandler;
import dars.NodeAttributes;
import dars.Utilities;
import dars.event.DARSEvent;
import dars.logger.Logger;
import dars.logger.Parser;
import dars.proto.NodeFactory.NodeType;
import dars.replayer.Replayer;

public class DARSAppMenu{
//Creating the menu bar and all of its elements
  private JMenuBar           menuBar             = new JMenuBar();
  private JMenu              simMenu             = new JMenu("Simulation");
  private JMenu              newMenu             = new JMenu("New");
  private JMenu              createNetworkMenu   = new JMenu("Create Network");
  private JMenu              modeMenu            = new JMenu("Mode");
  private JMenu              controlMenu            = new JMenu("Control");
  private JMenuItem          saveMenuItem        = new JMenuItem("Save As...");
  private JMenuItem          saveScreenMenuItem  = new JMenuItem("Save Screen...");
  private JMenuItem          clearMenuItem       = new JMenuItem("Clear");
  private JMenuItem          exitMenuItem        = new JMenuItem("Exit");
  private JMenuItem          importMenuItem      = new JMenuItem("Import for Replay...");
  private JMenuItem          playMenuItem        = new JMenuItem("Play");
  private JMenuItem          pauseMenuItem        = new JMenuItem("Pause");
  private JMenuItem          resumeMenuItem        = new JMenuItem("Resume");
  private JMenuItem          stopMenuItem        = new JMenuItem("Stop");
  
  private JMenu              helpMenu            = new JMenu("Help");
  private JMenuItem          readmeMenuItem            = new JMenuItem("Getting Started");
  private JMenuItem          aboutMenuItem            = new JMenuItem("About");
  private JMenuItem          addSingleNodeMenuItem = new JMenuItem("Add Single Node");
  private JMenuItem          deleteNodeMenuItem    = new JMenuItem("Delete Selected Node");
  private JMenuItem          addMultipleNodesMenuItem  = new JMenuItem("Add Multiple Nodes");
  private JMenuItem          loadTopologyMenuItem  = new JMenuItem("Load Topology from File...");
  // If someone knows a better way to align this stuff please feel free.
  private JLabel             simTypeLabel        = new JLabel(
                                                     "Simulation Type: ");
  JLabel                     typeLabel           = new JLabel("NONE");

  private JPanel             buttonArea          = new JPanel();
  private ImageIcon          playIcon           = new ImageIcon(getClass().getResource("/play.png"));
  private ImageIcon          pauseIcon           = new ImageIcon(getClass().getResource("/pause.png"));
  private ImageIcon          stopIcon           = new ImageIcon(getClass().getResource("/stop.png"));
  private JButton            playButton          = new JButton(playIcon);
  private JButton            resumeButton        = new JButton(pauseIcon);
  private JButton            pauseButton         = new JButton(pauseIcon);
  private JButton            stopButton          = new JButton(stopIcon);
  
  private JCheckBoxMenuItem  debugCheckBox       = new JCheckBoxMenuItem("Debug Enabled");
  private JCheckBoxMenuItem  graphicsCheckBox    = new JCheckBoxMenuItem("Graphics Enabled");

  private JPanel             speedArea           = new JPanel();
  private JPanel             simTypeArea         = new JPanel();

  // Labels slider bar for the speed adjustment
  private JLabel             speedLabel          = new JLabel("Simulation Speed");
  private JSlider            slideBar            = new JSlider(JSlider.HORIZONTAL, 1, 20, 10);
  private JPanel             menuPanel           = new JPanel();
  private SimArea            simArea;
  private LogArea            logArea;
  private GUI                gui;
  private JPanel         currentQuantumArea     = new JPanel();
  private JLabel         currentQuantumLabel     = new JLabel();
  
  public DARSAppMenu(GUI g) {

    this.gui = g;
    
    menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.LINE_AXIS));

    // Add the readme help menu to the menu bar
    helpMenu.add(readmeMenuItem);
    helpMenu.add(aboutMenuItem);

    // Add elements to the create network menu
    createNetworkMenu.add(addSingleNodeMenuItem);
    createNetworkMenu.add(loadTopologyMenuItem);
    createNetworkMenu.add(addMultipleNodesMenuItem);
    createNetworkMenu.add(deleteNodeMenuItem);
 
    // Add elements to the mode menu
    modeMenu.add(debugCheckBox);
    modeMenu.add(graphicsCheckBox);
    
    // Add elements to the control menu
    controlMenu.add(playMenuItem);
    controlMenu.add(pauseMenuItem);
    controlMenu.add(resumeMenuItem);
    controlMenu.add(stopMenuItem);
    
    // Add elements to the sim menu and their sub menus
    simMenu.add(newMenu);
    simMenu.add(createNetworkMenu);
    
    //Add node menu items dynamically using reflection
    addNewSimMenuItems(newMenu);
    simMenu.add(saveMenuItem);
    simMenu.add(saveScreenMenuItem);
    
    graphicsCheckBox.setState(true);
    addMultipleNodesMenuItem.setEnabled(false);
    addSingleNodeMenuItem.setEnabled(false);
    deleteNodeMenuItem.setEnabled(false);
    simMenu.add(importMenuItem);
    simMenu.add(clearMenuItem);
    simMenu.add(exitMenuItem);

    menuBar.add(simMenu);
    menuBar.add(modeMenu);
    menuBar.add(controlMenu);
    menuBar.add(helpMenu);

    // Add the simulation type menu lables
    simTypeArea.add(simTypeLabel);
    simTypeArea.add(typeLabel);
    menuPanel.add(simTypeArea);

    // Add the Play, pause, and stop buttons
    buttonArea.add(stopButton);
    buttonArea.add(pauseButton);
    buttonArea.add(resumeButton);
    buttonArea.add(playButton);   
    
    resumeButton.setVisible(false);
    resumeMenuItem.setVisible(false);
    playButton.setEnabled(false);
    playMenuItem.setEnabled(false);
    stopButton.setEnabled(false);
    stopMenuItem.setEnabled(false);
    pauseButton.setEnabled(false);
    pauseMenuItem.setEnabled(false);
    
    menuPanel.add(buttonArea);
    
    // Add the quantums elapsed area
    currentQuantumArea.add(new JLabel("  Current Quantum: "));
    currentQuantumLabel.setText("0"); 
    currentQuantumArea.add(currentQuantumLabel);
    
    // Add the slider bar, set its properties and values.
    JPanel sliderArea = new JPanel();
    sliderArea.add(slideBar);
    JPanel subPanel = new JPanel();
    subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.PAGE_AXIS));
    speedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    subPanel.add(speedLabel);
    subPanel.add(sliderArea);
    
    speedArea.add(subPanel);
    slideBar.setSnapToTicks(true);
    slideBar.setPaintTicks(true);
    slideBar.setMinorTickSpacing(1);
    Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
    labelTable.put( new Integer( 1 ), new JLabel("Slower") );
    labelTable.put( new Integer( 20 ), new JLabel("Faster") );
    slideBar.setLabelTable(labelTable);
    slideBar.setPaintLabels(true);

    
    menuPanel.add(speedArea);
    menuPanel.add(currentQuantumArea);    
    menuPanel.setOpaque(false);
    menuPanel.setVisible(true);
    
    // Setup a few simple keyboard shortcuts
    // These respond when ctrl is held
    saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
    saveScreenMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
    debugCheckBox.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));
    
    exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
    graphicsCheckBox.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.CTRL_MASK));
    clearMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
    importMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK));
    addSingleNodeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
    
    // Responds no matter what
    readmeMenuItem.setAccelerator(KeyStroke.getKeyStroke("F1"));
    aboutMenuItem.setAccelerator(KeyStroke.getKeyStroke("F2"));
    deleteNodeMenuItem.setAccelerator(KeyStroke.getKeyStroke("DELETE"));
    
    // Responds if alt is held
    playMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.ALT_MASK));
    pauseMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.ALT_MASK));
    stopMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK));
    resumeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.ALT_MASK));
    
    clearMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        InputHandler.dispatch(DARSEvent.inClearSim());
      }
    });
      
    saveMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Utilities.runSaveLogDialog(menuBar.getParent());
      }
    });
    
    
    saveScreenMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {     
        Utilities.captureScreen(simArea.getParent());
      }
    });

    debugCheckBox.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent arg0) {
        logArea.setDEBUG(debugCheckBox.getState());
      }      
    });
    
    graphicsCheckBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        simArea.setGraphicsEnabled(graphicsCheckBox.getState());
      }
    });
    
    addSingleNodeMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        InputHandler.dispatch(DARSEvent.inAddNode(Defaults.X,Defaults.Y,Defaults.RANGE, Defaults.IS_PROMISCUOUS));
      }
    });
    
    deleteNodeMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String selectedNode = simArea.getSelectedNodeID();
        if (selectedNode == null){
          return;
        }
        InputHandler.dispatch(DARSEvent.inDeleteNode(selectedNode));
      }
    });
    
    loadTopologyMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(       
            "Log Files", "log");
            chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(menuBar.getParent());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
           String name = chooser.getSelectedFile().getPath();
           
           //Parse the setup events into memory
           Queue<DARSEvent> Q = Parser.parseSetup(name);
           
           if(Q == null) {
             Utilities.showError("Log file can not be parsed.");
             return;
           }
           
           //Okay. New simulation. Have to ask the user what type of sim they want..
           NodeType nt = Utilities.popupAskNodeType();
           if(nt == null) {
             //User canceled..
             return;
           }
           InputHandler.dispatch(DARSEvent.inNewSim(nt));
           
           //Dispatch every event in the Q
           for(DARSEvent d : Q) {
             InputHandler.dispatch(d);
           }
        }
      }
    });
    
    importMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(       
            "Log Files", "log");
            chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(menuBar.getParent());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
          
          String name = chooser.getSelectedFile().getPath();
          
          //Parse the replay events into memory
          Queue<DARSEvent> Q = Parser.parseReplay(name);
          
          if(Q == null) {
            Utilities.showError("Log file can not be parsed.");
            return;
          }
          
          //Okay. New simulation. Have to ask the user what type of sim they want..
          NodeType nt = Utilities.popupAskNodeType();
          if(nt == null) {
            //User canceled..
            return;
          }
          
          //Start a new simualation
          InputHandler.dispatch(DARSEvent.inNewSim(nt));

          
          //Instantiate a new replayer with the replay events
          //Name the gui as the replayerListener.
          Replayer r = new Replayer(Q, (Replayer.ReplayerListener)gui);
          
          JOptionPane.showMessageDialog(simArea, "The replay has been sucessfully loaded. \n" +
                                                 "Please select \"Play\" from the menu bar to begin.");
          
          
        }
      }
    });
    
    addMultipleNodesMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Dimension dim = simArea.getSize();
        Random r = new Random();
        NodeAttributes att;
        double X = simArea.maxNodePoint().x;
        double Y = simArea.maxNodePoint().y;
        int numberOfNodes = 0;
        String input = JOptionPane.showInputDialog(null, "How many nodes would you like to add?");

        // If they hit cancel return.
        if (input == null){
          return;
        }
        
        try{
          numberOfNodes = Integer.parseInt(input);
        }catch (NumberFormatException nfe) {
          JOptionPane.showMessageDialog(null, "Invalide Entry, Numeric Only.");
          return;
        }  
      
        for (int i = 1; i <= numberOfNodes; i++){
          int range = r.nextInt(400) + 50; // Min range of 50
          int x = r.nextInt((int)X);
          int y = r.nextInt((int)Y);
          InputHandler.dispatch(DARSEvent.inAddNode(x,y,range, Defaults.IS_PROMISCUOUS));
        }
       }
      
    });

    exitMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        // Before we exit make sure to clean up the temporary log file.
        Logger logger = Logger.getInstance();
        logger.deleteLogFile();
        System.exit(0);
      }
    });

    readmeMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        new TextWindow("/README.txt");     
      }
    });
    
    aboutMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        new TextWindow("/ABOUT.txt");     
      }
    });

    playButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        InputHandler.dispatch(DARSEvent.inStartSim(getSlideBarSpeed()));
      }
    });
    
    playMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        InputHandler.dispatch(DARSEvent.inStartSim(getSlideBarSpeed()));
      }
    });

    pauseButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        InputHandler.dispatch(DARSEvent.inPauseSim());
      }
    });
    
    pauseMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        InputHandler.dispatch(DARSEvent.inPauseSim());
      }
    });

    stopButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        InputHandler.dispatch(DARSEvent.inStopSim());
      }
    });
    
    stopMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        InputHandler.dispatch(DARSEvent.inStopSim());
      }
    });

    resumeButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        InputHandler.dispatch(DARSEvent.inResumeSim());
      }
    });
    
    resumeMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        InputHandler.dispatch(DARSEvent.inResumeSim());
      }
    });

    slideBar.addChangeListener(new ChangeListener() {
      private int lastVal = 0;
      public void stateChanged(ChangeEvent arg0) {
        int val = getSlideBarSpeed();
        if(lastVal == val) {
          return;
        }
        lastVal = val;
        InputHandler.dispatch(DARSEvent.inSimSpeed(val));
      }
    });
  }

  private int getSlideBarSpeed() {
    //Interpret the slidebar speed
    return slideBar.getMaximum() - slideBar.getValue() + 1;
  }
  class NewSimClickHandler implements ActionListener {

    private NodeType nodeType;
    NewSimClickHandler(NodeType nodeType) {
      this.nodeType = nodeType;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
      //Dispatch the new sim event
      typeLabel.setText(nodeType.toString());
      InputHandler.dispatch(DARSEvent.inNewSim(nodeType));
    }
  }
  
  private void addNewSimMenuItems(JMenu parentMenu) {
    //get sim types
    NodeType[] nTypes = Utilities.getNodeTypes();
    
    for(NodeType nt : nTypes) {
      //Make a new menu item
      JMenuItem simTypeMenuItem = new JMenuItem(nt.toString());
      
      //Add the event handler
      simTypeMenuItem.addActionListener(new NewSimClickHandler(nt));
      
      //Add it to the parent menu
      parentMenu.add(simTypeMenuItem);
    }
  }

  public void simStarted() {
    //Disable the play button, enable the pause/stop buttons
    playButton.setEnabled(false);
    playMenuItem.setEnabled(false);
    pauseButton.setEnabled(true);
    pauseMenuItem.setEnabled(true);
    stopButton.setEnabled(true);   
    stopMenuItem.setEnabled(true);
  }
 
  public void newSim() {
    //Disable/enable menu items
    newMenu.setEnabled(false);
    importMenuItem.setEnabled(false);
    addMultipleNodesMenuItem.setEnabled(true);
    addSingleNodeMenuItem.setEnabled(true);
    deleteNodeMenuItem.setEnabled(true);
    
    //Enable the Play button, disable tstop and pause
    stopButton.setEnabled(false);
    stopMenuItem.setEnabled(false);
    playButton.setEnabled(true);
    playMenuItem.setEnabled(true);
    pauseButton.setEnabled(false);
    pauseMenuItem.setEnabled(false);

    //Zero out the current quantum
    quantums = BigInteger.ZERO;
    currentQuantumLabel.setText(quantums.toString());   
  }
  
  public void simStopped() {
    stopButton.setEnabled(false);
    stopMenuItem.setEnabled(false);
    playButton.setEnabled(false);
    playMenuItem.setEnabled(false);
    pauseButton.setEnabled(false);
    pauseMenuItem.setEnabled(false);
    
    //Enable/disable menu items
    newMenu.setEnabled(true);
    importMenuItem.setEnabled(true);
    addMultipleNodesMenuItem.setEnabled(false);
    addSingleNodeMenuItem.setEnabled(false);
    deleteNodeMenuItem.setEnabled(false);
    
  }
  
  public void simPaused() {
    playButton.setEnabled(false);
    playMenuItem.setEnabled(false);
    stopButton.setEnabled(false);
    stopMenuItem.setEnabled(false);
    pauseButton.setVisible(false);
    pauseMenuItem.setVisible(false);
    resumeButton.setVisible(true);
    resumeMenuItem.setVisible(true);
  }
  
  public void simResumed() {
    stopButton.setEnabled(true);
    stopMenuItem.setEnabled(true);
    pauseButton.setVisible(true);
    pauseMenuItem.setVisible(true);
    resumeButton.setVisible(false);
    resumeMenuItem.setVisible(false);
  }
  
  public JMenuBar getMenuBar() {
    return menuBar;
  }
  
  public JPanel getActionPanel() {
    return menuPanel;
  }
  
  
  public void setSimArea(SimArea simArea) {
    this.simArea = simArea;
  }
  
  public void setLogArea(LogArea logArea){
    this.logArea = logArea;
  }  
 
  private BigInteger quantums = BigInteger.ZERO;
  
  public void quantumElapsed() {
    quantums = quantums.add(BigInteger.ONE);
    currentQuantumLabel.setText(quantums.toString());
  }  
}
