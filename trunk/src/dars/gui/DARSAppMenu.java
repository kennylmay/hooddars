package dars.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import dars.InputHandler;
import dars.NodeAttributes;
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
  private JPanel             menuPanel           = new JPanel();
  private SimArea            simArea;

  public DARSAppMenu() {
    aodvMenu.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        typeLabel.setText("AODV");
        InputHandler.dispatch(DARSEvent.inNewSim(DARSEvent.SimType.AODV));
        playButton.setEnabled(true);
        stopButton.setEnabled(true);
        pauseButton.setEnabled(true);
        randomizeMenu.setEnabled(true);
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
        int returnVal = chooser.showSaveDialog(menuBar.getParent());
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
            JOptionPane.showMessageDialog(menuBar.getParent(),
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
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog(menuBar.getParent());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
          /// Some call to load the simulation setup
        }
      }
    });

    replayMenu.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog(menuBar.getParent());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
          /// Some Call to Load the simulation replay
        }
      }
    });
    
    randomizeMenu.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Dimension dim = simArea.getSize();
        Random r = new Random();
        NodeAttributes att = new NodeAttributes();
        double X = dim.getWidth();
        double Y = dim.getHeight();
        int numberOfNodes = 0;
        String input = JOptionPane.showInputDialog(null, "How many nodes would you like to randomly add?");
        
        try{
          numberOfNodes = Integer.parseInt(input);
        }catch (NumberFormatException nfe) {
          JOptionPane.showMessageDialog(null, "Invalide Entry, Numeric Only.");
          return;
        }  
      
        for (int i = 1; i <= numberOfNodes; i++){
          att.range = r.nextInt(1000);
          att.x = r.nextInt((int)X);
          att.y = r.nextInt((int)Y);
          InputHandler.dispatch(DARSEvent.inAddNode(att));
        }
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
    
    menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.LINE_AXIS));

    // Add the web help menu to the menu bar
    helpMenu.add(webMenu);

    // Add elements to the sim menu and their sub menus
    simMenu.add(newMenu);
    newMenu.add(aodvMenu);
    newMenu.add(dsdvMenu);
    simMenu.add(saveMenu);
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
    
  }
  
  public void simStarted() {
    playButton.setEnabled(false);
    pauseButton.setEnabled(true);
    stopButton.setEnabled(true);   
  }
  
  public void simStopped() {
    stopButton.setEnabled(false);
    playButton.setEnabled(false);
    pauseButton.setEnabled(false);
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
    randomizeMenu.setEnabled(false);
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
  
}
