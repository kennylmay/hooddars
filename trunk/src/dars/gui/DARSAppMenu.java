package dars.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Queue;
import java.util.Random;

import javax.swing.BorderFactory;
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
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
import dars.replayer.Replayer.ReplayMode;
import dars.replayer.Replayer.ReplayerListener;

public class DARSAppMenu implements ReplayerListener, ComponentListener {
//Creating the  bar and all of its elements
  private JMenuBar           menuBar             = new JMenuBar();
  private JMenu              simMenu             = new JMenu("Simulation");
  private JMenu              newMenu             = new JMenu("New");
  private JMenu              createNetworkMenu   = new JMenu("Create Network");
  private JMenu              modeMenu            = new JMenu("Mode");
  private JMenu              controlMenu            = new JMenu("Control");
  private JMenuItem          saveMenuItem        = new JMenuItem("Save As...");
  private JMenuItem          saveScreenMenuItem  = new JMenuItem("Save Screen...");
  private JMenuItem          clearNodesMenuItem       = new JMenuItem("Clear Nodes");
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
  private JLabel             typeLabel           = new JLabel("Simulation type: ");
  private JLabel             modeLabel           = new JLabel("Mode: ");
  private JLabel             engineStatusLabel      = new JLabel("Engine status: ");
  private JLabel             simModeLabel           = new JLabel();
  private JLabel             simTypeLabel           = new JLabel();
  private JLabel             simEngineStatusLabel   = new JLabel();

  private JPanel             buttonArea          = new JPanel();
  private ImageIcon          playIcon            = new ImageIcon(getClass().getResource("/play.png"));
  private ImageIcon          pauseIcon           = new ImageIcon(getClass().getResource("/pause.png"));
  private ImageIcon          stopIcon            = new ImageIcon(getClass().getResource("/stop.png"));
  private ImageIcon          hoverPlayIcon       = new ImageIcon(getClass().getResource("/hoverplay.png"));
  private ImageIcon          hoverPauseIcon      = new ImageIcon(getClass().getResource("/hoverpause.png"));
  private ImageIcon          hoverStopIcon       = new ImageIcon(getClass().getResource("/hoverstop.png"));
  private JButton            playButton          = new JButton(playIcon);
  private JButton            resumeButton        = new JButton(pauseIcon);
  private JButton            pauseButton         = new JButton(pauseIcon);
  private JButton            stopButton          = new JButton(stopIcon);
   
  private JCheckBoxMenuItem  debugCheckBox       = new JCheckBoxMenuItem("Debug Enabled");
  private JCheckBoxMenuItem  graphicsCheckBox    = new JCheckBoxMenuItem("Graphics Enabled");

  private JPanel             speedArea           = new JPanel();
  private JPanel             simTypeArea         = new JPanel();
  private JPanel             statusPanel         = new JPanel();
  private JPanel             controlPanel         = new JPanel();

  // Labels slider bar for the speed adjustment
  private JLabel             speedLabel          = new JLabel("Simulation Speed");
  private JSlider            slideBar            = new JSlider(JSlider.HORIZONTAL, 1, 20, 10);
  private JPanel             menuPanel           = new JPanel();
  private SimArea            simArea;
  private LogArea            logArea;
  private JPanel         currentQuantumArea     = new JPanel();
  private static JLabel         currentQuantumLabel     = new JLabel();
  private JProgressBar   replayPBar              = new JProgressBar();
  private GUI                guiInstance; 
  private Replayer           replayer = null;
  
  public DARSAppMenu(GUI g) {

    guiInstance = g;
    
    //listen for component events so we can resize the menu panel later
    g.addComponentListener(this);
    
    //Make the menubar slightly darker.
    float[] rgba = menuBar.getBackground().getRGBComponents(null);
    for(int i =0;i <4; i++){ rgba[i] -= .08f; if(rgba[i] < 0f) rgba[i]=0f;}
    menuBar.setBackground(new Color(rgba[0], rgba[1], rgba[2],rgba[3]));
    
    menuPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0 ,0));

    
    
    
    // Add the readme help  to the  bar
    helpMenu.add(readmeMenuItem);
    helpMenu.add(aboutMenuItem);

    // Add elements to the create network 
    createNetworkMenu.add(addSingleNodeMenuItem);
    createNetworkMenu.add(loadTopologyMenuItem);
    createNetworkMenu.add(addMultipleNodesMenuItem);
    createNetworkMenu.add(deleteNodeMenuItem);
 
    // Add elements to the mode 
    modeMenu.add(debugCheckBox);
    modeMenu.add(graphicsCheckBox);
    
    // Add elements to the control 
    controlMenu.add(playMenuItem);
    controlMenu.add(pauseMenuItem);
    controlMenu.add(resumeMenuItem);
    controlMenu.add(stopMenuItem);
    
    // Add elements to the sim  and their sub menus
    simMenu.add(newMenu);
    simMenu.add(createNetworkMenu);
    
    //Add node  items dynamically using reflection
    addNewSimMenuItems(newMenu);
    simMenu.add(saveMenuItem);
    simMenu.add(saveScreenMenuItem);
    
    graphicsCheckBox.setState(true);
    addMultipleNodesMenuItem.setEnabled(false);
    addSingleNodeMenuItem.setEnabled(false);
    deleteNodeMenuItem.setEnabled(false);
    simMenu.add(importMenuItem);
    simMenu.add(clearNodesMenuItem);
    simMenu.add(exitMenuItem);

    menuBar.add(simMenu);
    menuBar.add(modeMenu);
    menuBar.add(controlMenu);
    menuBar.add(helpMenu);

    // Add the simulation type  labels
    JPanel simModeArea = new JPanel();
    JPanel simEngineArea = new JPanel();
    
    simTypeArea.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
    simModeArea.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
    simEngineArea.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
    typeLabel.setFont(Defaults.BOLDFACED_FONT);
    modeLabel.setFont(Defaults.BOLDFACED_FONT);
    engineStatusLabel.setFont(Defaults.BOLDFACED_FONT);
    
    simModeArea.add(modeLabel);
    simModeArea.add(simModeLabel);
    simTypeArea.add(typeLabel);
    simTypeArea.add(simTypeLabel);
    simEngineArea.add(engineStatusLabel);
    simEngineArea.add(simEngineStatusLabel);
    
    JPanel statusSubPanel = new JPanel();
    statusPanel.setLayout(new GridLayout(2,1, 0, 5));
    statusSubPanel.setLayout(new GridLayout(2,2, 0, 5));
    replayPBar.setVisible(false);
    replayPBar.setString("Replay Progress");
    replayPBar.setStringPainted(true);
    statusSubPanel.add(simTypeArea); statusSubPanel.add(simEngineArea);
    statusSubPanel.add(simModeArea); statusSubPanel.add(currentQuantumArea);
    
    statusPanel.add(statusSubPanel);
    statusPanel.add(replayPBar);
    
    
    statusPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
        "Status", 
        TitledBorder.CENTER, TitledBorder.TOP, Defaults.BOLDFACED_FONT) );
    
    // Add the Play, pause, and stop buttons
    controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 11, 2));
   

    customizeButton(stopButton);
    customizeButton(playButton);
    customizeButton(pauseButton);
    customizeButton(resumeButton);
    
    stopButton.setRolloverIcon(hoverStopIcon);
    playButton.setRolloverIcon(hoverPlayIcon);
    pauseButton.setRolloverIcon(hoverPauseIcon);
    resumeButton.setRolloverIcon(hoverPauseIcon);

    buttonArea.add(stopButton);
    buttonArea.add(pauseButton);
    buttonArea.add(resumeButton);
    buttonArea.add(playButton);
    
    controlPanel.add(buttonArea);
    controlPanel.add(speedArea);
    
    controlPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
        "Simulation Controls", 
        TitledBorder.CENTER, TitledBorder.TOP, Defaults.BOLDFACED_FONT) );

    menuPanel.add(statusPanel);
    menuPanel.add(controlPanel);

    
    resumeButton.setVisible(false);
    resumeMenuItem.setVisible(false);
    playButton.setEnabled(false);
    playMenuItem.setEnabled(false);
    stopButton.setEnabled(false);
    stopMenuItem.setEnabled(false);
    pauseButton.setEnabled(false);
    pauseMenuItem.setEnabled(false);
    
    // Add the quantums elapsed area
    currentQuantumArea.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
    JLabel qLabel = new JLabel("Current Quantum: ");
    qLabel.setFont(Defaults.BOLDFACED_FONT);
    currentQuantumArea.add(qLabel);
    
    currentQuantumArea.add(currentQuantumLabel);
    
    // Add the slider bar, set its properties and values.
    JPanel sliderArea = new JPanel();
    sliderArea.add(slideBar);
    JPanel subPanel = new JPanel();
    subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.PAGE_AXIS));
    speedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    speedLabel.setFont(Defaults.BOLDFACED_FONT);
    subPanel.add(speedLabel);
    subPanel.add(sliderArea);
    
    speedArea.setLayout(new FlowLayout(FlowLayout.LEFT, 11, 0));
    speedArea.add(subPanel);
    
    slideBar.setSnapToTicks(true);
    slideBar.setPaintTicks(true);
    slideBar.setMinorTickSpacing(1);
    Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
    labelTable.put( new Integer( 1 ), new JLabel("Slower") );
    labelTable.put( new Integer( 20 ), new JLabel("Faster") );
    slideBar.setLabelTable(labelTable);
    slideBar.setPaintLabels(true);

    
    menuPanel.setOpaque(false);
    menuPanel.setVisible(true);
    
    // Setup a few simple keyboard shortcuts
    // These respond when ctrl is held
    saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
    saveScreenMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
    debugCheckBox.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));
    
    exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
    graphicsCheckBox.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.CTRL_MASK));
    clearNodesMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
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
    
    clearNodesMenuItem.addActionListener(new ActionListener() {
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

          //Ask the user what mode of replay they want
          ReplayMode mode = Replayer.askReplayMode();
          if(mode == null) {
            return;
          }
          
          //Instantiate a new replayer with the replay events
          //Name the gui as the replayerListener.
          replayer = new Replayer(Q, (Replayer.ReplayerListener)guiInstance.getReplayerListener(), mode);
          
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
      InputHandler.dispatch(DARSEvent.inNewSim(nodeType));
    }
  }
  
  private void addNewSimMenuItems(JMenu parentMenu) {
    //get sim types
    NodeType[] nTypes = Utilities.getNodeTypes();
    
    for(NodeType nt : nTypes) {
      //Make a new  item
      JMenuItem simTypeMenuItem = new JMenuItem(nt.toString());
      
      //Add the event handler
      simTypeMenuItem.addActionListener(new NewSimClickHandler(nt));
      
      //Add it to the parent 
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
    
    //Update the engine label
    simEngineStatusLabel.setText("Running");
  }
 
  public void newSim(NodeType nodeType) {
    //Disable/enable  items
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
    quantums = 0;
    currentQuantumLabel.setText(Long.toString(quantums));
    
    //Show the new sim label
    simTypeLabel.setText(nodeType.toString());
    
    //Show the sim mode; Normal by default. 
    simModeLabel.setText("Normal");
    
    //Show engine status; Stopped by default
    simEngineStatusLabel.setText("Stopped");
    
    //Make sure the replaybar is hidden
    replayPBar.setVisible(false);
  }
  
  public void simStopped() {
    stopButton.setEnabled(false);
    stopMenuItem.setEnabled(false);
    playButton.setEnabled(false);
    playMenuItem.setEnabled(false);
    pauseButton.setEnabled(false);
    pauseMenuItem.setEnabled(false);
    
    //Enable/disable  items
    newMenu.setEnabled(true);
    importMenuItem.setEnabled(true);
    addMultipleNodesMenuItem.setEnabled(false);
    addSingleNodeMenuItem.setEnabled(false);
    deleteNodeMenuItem.setEnabled(false);
    
    //If the replayer is running, abort it.
    if(replayer != null && replayer.isRunning()) {
      replayer.abort();
    }
    
    //Update the engine label
    simEngineStatusLabel.setText("Stopped");
    
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
    
    //Update the engine label
    simEngineStatusLabel.setText("Paused");
  }
  
  public void simResumed() {
    stopButton.setEnabled(true);
    stopMenuItem.setEnabled(true);
    pauseButton.setVisible(true);
    pauseMenuItem.setVisible(true);
    resumeButton.setVisible(false);
    resumeMenuItem.setVisible(false);
    //Update the engine label
    simEngineStatusLabel.setText("Running");
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
 
  private long quantums = 0;
  
  public void quantumElapsed() {
    quantums += 1;
    currentQuantumLabel.setText(Long.toString(quantums));
    if(replayPBar.isVisible()) {
      replayPBar.setValue((int)quantums);
    }
    
  }  
  
  @Override
  public void replayerStarted(Queue<DARSEvent> Q, Replayer instance) {
    if(Q.size() == 0) return;
    
    replayPBar.setVisible(true);
    
    //Get the last event of the replay. Use it to determine the upper bound for the progress bar.
    DARSEvent e = null;
    Iterator<DARSEvent> i = Q.iterator();
    while(i.hasNext()) {
      e = i.next();
    }

    replayPBar.setMaximum((int)e.currentQuantum);
    
    //Set the mode
    if(instance.getMode() == ReplayMode.LOCKED) {
      simModeLabel.setText("Locked replay mode");
    }
    else {
      simModeLabel.setText("Interactive replay mode");
    }
    
  }
  
  

  public void setSimModeLabel(String modeText) {
    simModeLabel.setText(modeText);
  }
  
  @Override
  public void replayerFinished(boolean aborted) {
   //hide the replay progress bar
   replayPBar.setVisible(false);
   replayPBar.setValue(0);

  }


  public void setLockedReplayMode(boolean b) {
    
  }

  @Override
  public void componentHidden(ComponentEvent arg0) {
    resizeMenuPanel();
  }

  @Override
  public void componentMoved(ComponentEvent arg0) {
    resizeMenuPanel();
  }

  @Override
  public void componentResized(ComponentEvent arg0) {
    resizeMenuPanel();
  }

  @Override
  public void componentShown(ComponentEvent arg0) {
    resizeMenuPanel();
  }

  private void resizeMenuPanel() {
    statusPanel.setPreferredSize(new Dimension(500, 
        Math.max(statusPanel.getPreferredSize().height, controlPanel.getPreferredSize().height)));
    controlPanel.setPreferredSize(new Dimension(controlPanel.getPreferredSize().width, 
        Math.max(statusPanel.getPreferredSize().height, controlPanel.getPreferredSize().height)));
    
    menuPanel.invalidate();
    
  }

  private void customizeButton(JButton b) {
    Color transparent = new Color(0,0,0,0);
    b.setOpaque(false);
    b.setBackground(transparent);
    b.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
    b.setRolloverEnabled(true);
    b.setFocusable(false);
    
  }

}

