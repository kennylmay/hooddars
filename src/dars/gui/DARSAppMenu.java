package dars.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import replayer.Replayer;

import logger.Parser;

import dars.InputHandler;
import dars.NodeAttributes;
import dars.Utilities;
import dars.event.DARSEvent;

public class DARSAppMenu  {
//Creating the menu bar and all of its elements
  private JMenuBar           menuBar             = new JMenuBar();
  private JMenu              simMenu             = new JMenu("Simulation");
  private JMenu              newMenu             = new JMenu("New");
  private JMenuItem          saveMenu            = new JMenuItem("Save");
  private JMenuItem          aodvMenu            = new JMenuItem("AODV");
  private JMenuItem          dsdvMenu            = new JMenuItem("DSDV");
  private JMenuItem          clearMenu           = new JMenuItem("Clear");
  private JMenuItem          exitMenu            = new JMenuItem("Exit");
  private JMenu              importMenu          = new JMenu("Import");
  private JMenuItem          randomizeMenu           = new JMenuItem("Randomize");
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
  private JButton            playButton          = new JButton("Play ");
  private JButton            resumeButton        = new JButton("Resume");
  private JButton            pauseButton         = new JButton("Pause");
  private JButton            stopButton          = new JButton("Stop");
  
  private JCheckBoxMenuItem  debugCheckBox       = new JCheckBoxMenuItem("Debug Enabled");
  private JCheckBoxMenuItem  graphicsCheckBox    = new JCheckBoxMenuItem("Graphics Enabled");

  private JPanel             speedArea           = new JPanel();
  private JPanel             simTypeArea         = new JPanel();

  // Labels slider bar for the speed adjustment
  private JLabel             speedLabel          = new JLabel("Speed");
  private ImageIcon          minusIcon           = new ImageIcon(getClass().getResource("/minus.png"));
  private JLabel             slowerLabel         = new JLabel(minusIcon);
  private JSlider            slideBar            = new JSlider();
  private ImageIcon          plusIcon            = new ImageIcon(getClass().getResource("/plus.png"));
  private JLabel             fasterLabel         = new JLabel(plusIcon);
  private JPanel             menuPanel           = new JPanel();
  private SimArea            simArea;
  private LogArea            logArea;
  private GUI                gui;
  private JPanel         currentQuantumArea     = new JPanel();
  private JLabel         currentQuantumLabel     = new JLabel();
  
  
  
  public DARSAppMenu(GUI g) {

    this.gui = g;
    
    menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.LINE_AXIS));

    // Add the web help menu to the menu bar
    helpMenu.add(webMenu);

    // Add elements to the sim menu and their sub menus
    simMenu.add(newMenu);
    newMenu.add(aodvMenu);
    newMenu.add(dsdvMenu);
    simMenu.add(saveMenu);
    simMenu.add(debugCheckBox);
    simMenu.add(graphicsCheckBox);
    graphicsCheckBox.setState(true);
    randomizeMenu.setEnabled(false);
    simMenu.add(randomizeMenu);
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
    
    // Add the quantums elapsed area
    currentQuantumArea.add(new JLabel("  Current Quantum: "));
    currentQuantumArea.add(currentQuantumLabel);
    
    // Add the slider bar, set its properties and values.
    speedArea.add(speedLabel);
    speedArea.add(fasterLabel);
    speedArea.add(slideBar);
    slideBar.setSnapToTicks(true);
    slideBar.setMinimum(1);
    slideBar.setMaximum(20);
    slideBar.setValue(5);
    speedArea.add(slowerLabel);
    speedArea.add(currentQuantumArea);
    menuPanel.add(speedArea);
    
    menuPanel.setOpaque(false);
 
    aodvMenu.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        typeLabel.setText("AODV");
        //Need to define the speed.
        InputHandler.dispatch(DARSEvent.inSimSpeed(slideBar.getValue()));
        InputHandler.dispatch(DARSEvent.inNewSim(DARSEvent.SimType.AODV));

      }
    });
    
    dsdvMenu.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        typeLabel.setText("DSDV");
      }
    });

    clearMenu.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        InputHandler.dispatch(DARSEvent.inStopSim());
        InputHandler.dispatch(DARSEvent.inClearSim());
      }
    });

    saveMenu.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Utilities.runSaveLogDialog(menuBar.getParent());
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
    
    setupMenu.addActionListener(new ActionListener() {
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
           Object[] options = {"AODV", "DSDV"};
           int answer = JOptionPane.showOptionDialog(simArea,
                        "Select a simulation type.",
                        "Select a simulation type.",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                       options,
                     options[0]);
           DARSEvent.SimType st;
           if(answer == 0) {
             st = DARSEvent.SimType.AODV;
           } else {
             st = DARSEvent.SimType.DSDV;
           }

           //Start a new simualation, with type AODV
           InputHandler.dispatch(DARSEvent.inNewSim(st));
           
           //Dispatch every event in the Q
           for(DARSEvent d : Q) {
             InputHandler.dispatch(d);
           }
        }
      }
    });
    
    replayMenu.addActionListener(new ActionListener() {
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
          Object[] options = {"AODV", "DSDV"};
          int answer = JOptionPane.showOptionDialog(simArea,
                       "Select a simulation type.",
                       "Select a simulation type.",
                       JOptionPane.YES_NO_OPTION,
                       JOptionPane.QUESTION_MESSAGE,
                       null,
                      options,
                    options[0]);
          DARSEvent.SimType st;
          if(answer == 0) {
            st = DARSEvent.SimType.AODV;
          } else {
            st = DARSEvent.SimType.DSDV;
          }

          //Start a new simualation
          InputHandler.dispatch(DARSEvent.inNewSim(st));

          
          //Instantiate a new replayer with the replay events
          //Name the gui as the replayerListener.
          Replayer r = new Replayer(Q, (Replayer.ReplayerListener)gui);
          
          JOptionPane.showMessageDialog(simArea, "The replay has been sucessfully loaded. \n" +
                                                 "Please select \"Play\" from the menu bar to begin.");
          
          
        }
      }
    });
    
    randomizeMenu.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Random r = new Random();
        NodeAttributes att = new NodeAttributes();
        double X = simArea.maxNodePoint().x;
        double Y = simArea.maxNodePoint().y;
        int numberOfNodes = 0;
        String input = JOptionPane.showInputDialog(null, "How many nodes would you like to randomly add?");
        
        try{
          numberOfNodes = Integer.parseInt(input);
        }catch (NumberFormatException nfe) {
          JOptionPane.showMessageDialog(null, "Invalide Entry, Numeric Only.");
          return;
        }  
      
        for (int i = 1; i <= numberOfNodes; i++){
          att.range = r.nextInt(400) + 50; // Min range of 50
          att.x = r.nextInt((int)X);
          att.y = r.nextInt((int)Y);
          InputHandler.dispatch(DARSEvent.inAddNode(att));
        }
       }
      
    });

    exitMenu.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        System.exit(0);
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
    
  }
  
  public void simStarted() {
    //Disable the play button, enable the pause/stop buttons
    playButton.setEnabled(false);
    pauseButton.setEnabled(true);
    stopButton.setEnabled(true);   
    
    

  }
 
  public void newSim() {
    //Disable/enable menu items
    newMenu.setEnabled(false);
    importMenu.setEnabled(false);
    randomizeMenu.setEnabled(true);
    
    //Enable the Play button, disable tstop and pause
    stopButton.setEnabled(false);
    playButton.setEnabled(true);
    pauseButton.setEnabled(false);
    
    
    //Zero out the current quantum
    quantums = BigInteger.ZERO;
    currentQuantumLabel.setText(quantums.toString());
    
    
  }
  
  public void simStopped() {
    stopButton.setEnabled(false);
    playButton.setEnabled(false);
    pauseButton.setEnabled(false);
    
    //Enable/disable menu items
    newMenu.setEnabled(true);
    importMenu.setEnabled(true);
    
  }
  
  public void simPaused() {
    playButton.setEnabled(false);
    stopButton.setEnabled(false);
    pauseButton.setVisible(false);
    resumeButton.setVisible(true);
  }
  
  public void simResumed() {
    stopButton.setEnabled(true);
    pauseButton.setVisible(true);
    resumeButton.setVisible(false);
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
