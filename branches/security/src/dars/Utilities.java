package dars;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Queue;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import dars.event.DARSEvent;
import dars.gui.GUI;
import dars.gui.ImageFactory;
import dars.logger.Logger;
import dars.logger.Parser;
import dars.proto.NodeFactory.NodeType;
import dars.replayer.Replayer;
import dars.replayer.Replayer.ReplayMode;

public class Utilities {
  static private Utilities instance = new Utilities();

  public enum scenarioType {
    HOP_OVERRIDE, DROP_NARR, NO_ROUTE_EXPIRE, CHANGE_NARR, REPLAY_NARR, COMBINATION, CHALLENGE_SOLUTION_1, CHALLENGE_SOLUTION_2,
  };

  public enum challengeNumber {
    CHALLENGE_1, CHALLENGE_2
  };

  public static void setSwingFont(javax.swing.plaf.FontUIResource f) {
    java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
    while (keys.hasMoreElements()) {
      Object key = keys.nextElement();
      Object value = UIManager.get(key);
      if (value instanceof javax.swing.plaf.FontUIResource)
        UIManager.put(key, f);
    }
  }

  public static void showError(String error) {
    JOptionPane.showMessageDialog(null, error, "Error",
        JOptionPane.ERROR_MESSAGE);
  }

  public static void showInfo(String info, String title) {
    JOptionPane.showMessageDialog(null, info, title,
        JOptionPane.INFORMATION_MESSAGE);
  }

  public static void loadScenario(scenarioType type, GUI g) {
    String osname = System.getProperty("os.name");
    String filename;

    if (osname.contains("Windows")) {
      filename = ".\\scenarios\\";
    } else {
      filename = "./scenarios/";
    }

    switch (type) {
    case HOP_OVERRIDE:
      JOptionPane
          .showMessageDialog(
              null,
              "The fake hop count attack can be used to direct network traffic away from or towards \n"
                  + " a malicious node. The malicious node in this attack will lie about the hop counts \n"
                  + "to other nodes in the network. Under normal circumstances a node will choose the \n"
                  + "shortest route to a destination based on the hop count to that destination. As \n"
                  + "routing tables are built only the best routes to each node are kept. If node is \n"
                  + "attempting to direct all network traffic through it in order to drop or read network \n"
                  + "traffic it can lie and say that every other node is only one hop away. All other \n"
                  + "nodes surrounding this this malicious node will then begin to forward messages \n"
                  + "through this lying node. On the other hand it can lie about the hop counts and say \n"
                  + "that all nodes within range is an infinity number of hops away so that other nodes \n"
                  + "attempting to send messages will pick a different rout. This would be helpful for the \n"
                  + "malicious node to save its own bandwidth, but bad for the overall network performance by \n"
                  + "causing more traffic to flow through potentially worse routes.",
              "Hop Override Scenario", JOptionPane.INFORMATION_MESSAGE);
      filename += "HopOverrideScenario.scn";

      break;

    case DROP_NARR:
      JOptionPane
          .showMessageDialog(
              null,
              "Message dropping attacks are implemented by a malcious node by dropping some or all \n"
                  + "messages that pass through it to artiffically degrade network reliablity. Under \n"
                  + "normal operating conditions in an ad-hoc network nodes will need to pass \n"
                  + "information to its peer nodes to establish routing tables to later pass real \n"
                  + "information. A malicious node however will pass this routing information around \n"
                  + "normally in order to construct its own routing information as well as make itself \n"
                  + "known in the surrounding network. As nodes begin to send real message information \n"
                  + "around the network however the malicous node will drop some or all of the traffic \n"
                  + "while continuing to respond to rout information traffic. This will cause an artifical \n"
                  + "black hole of sorts for real message information.",
              "Drop Narrative Message Scenario",
              JOptionPane.INFORMATION_MESSAGE);
      filename += "DropNarrScenario.scn";
      break;

    case NO_ROUTE_EXPIRE:
      JOptionPane
          .showMessageDialog(
              null,
              "The no timeout attack is used by malicious nodes to lie about the presence of other \n"
                  + "nodes in the network. Under normal operating conditions in a network when a node \n"
                  + "comes within range of other nodes its presence is announced in a broadcast message \n"
                  + "and other nodes are notified that the new node should be added to their respective \n"
                  + "routing tables. Consequently when a node leaves the network and it no longer responds \n"
                  + "to network requests it is deleted from all other node's routing tables based on a set \n"
                  + "timeout. However, a malicious node can exploit this concept by lying about being able \n"
                  + "to communicate with a node that has left the network by never expiring the route in \n"
                  + "its own routing table. When a neighboring node requests all the routes that the malicious \n"
                  + "node knows of it will respond that the missing node is still there and able to be \n"
                  + "reached through the missing node. This allows a malicious node to continue to gather \n"
                  + "messages for a node that has already left the network.",
              "No Route Expiration", JOptionPane.INFORMATION_MESSAGE);
      filename += "NoRouteExpireScenario.scn";
      break;

    case CHANGE_NARR:
      JOptionPane
          .showMessageDialog(
              null,
              "The message tampering attack in mobile ad-hoc networks poses a serious security risk \n"
                  + "in the real world. In a normal wireless network all nodes communicate directly with a \n"
                  + "wireless access point. These messages can be intercepted by other users within wireless \n"
                  + "range. However, typically these access points employ some type of wireless security such \n"
                  + "as WPA. This keeps other users from viewing their peers network traffic. In ad-hoc networks \n"
                  + "on the other hand each user must rely on their peers around them to relay a message. This \n"
                  + "means they need to be able to read certain parts of the message to be able to know how to \n"
                  + "route it. The actual contents of the network packets can be encrypted to hide message \n"
                  + "information, but distrubuting an encryption key can be difficult to do when you relying \n"
                  + "on the same potentially malicious nodes to transfer your key. Not only can the contents of \n"
                  + "messages be changed but also packet header information containing network critical information \n"
                  + "such as destination, hop count, and sequence number can also be manipulated causing disruption \n"
                  + "of the network.", "Change Narritive Message Scenario",
              JOptionPane.INFORMATION_MESSAGE);
      filename += "ChangeNarrScenario.scn";
      break;

    case REPLAY_NARR:
      JOptionPane
          .showMessageDialog(
              null,
              "The message replay attack can be used in conjunction with the message dropping attack, \n"
                  + "but it doesn't necessarily need to. In a normal network a node will receive a message to be \n"
                  + "passed to another node and immediately send the message on without any further action. A \n"
                  + "malicious node however can store the message and resend it at a later time. The node can drop \n"
                  + "the original message and send the new message at a large delay creating an artificial delay \n"
                  + "in the network or it can send the original message and the new message as a second copy \n"
                  + "confusing the recipient.",
              "Replay Narrative Message Scenario",
              JOptionPane.INFORMATION_MESSAGE);
      filename += "ReplayNarrScenario.scn";
      break;

    case COMBINATION:
      JOptionPane
          .showMessageDialog(
              null,
              "The combination scenario uses to hop override attack to force almost all messages to \n"
                  + "traverse the malcious node while changing all the messages that is receives. It will then \n"
                  + "use the replay attack to send the incorrect message at a later date. The routes will not \n"
                  + "expire for any nodes that the malcious node is aware of so future messages to nodes that \n"
                  + "have been removed will still continue to flow through the malacious node.",
              "Combination Scenario", JOptionPane.INFORMATION_MESSAGE);
      filename += "CombinationScenario.scn";
      break;
    }
    loadReplayFile(filename, g);

  }

  public static void loadChallengeScenario(challengeNumber number, GUI g) {
    String osname = System.getProperty("os.name");
    String filename;

    if (osname.contains("Windows")) {
      filename = ".\\scenarios\\";
    } else {
      filename = "./scenarios/";
    }

    switch (number) {
    case CHALLENGE_1:
      JOptionPane
          .showMessageDialog(
              null,
              "The goal of challenge 1 is to use the minimum number of malacious nodes to intercept and \n"
                  + "change all messages in the network. The nodes should not be moved from their beginning locations.\n"
                  + "The solutions for each protocol will vary.",
              "Challenge 1", JOptionPane.INFORMATION_MESSAGE);
      filename += "Challenge_1_Scenario.scn";
      break;
    case CHALLENGE_2:
      JOptionPane
          .showMessageDialog(
              null,
              "The combination scenario uses to hop override attack to force almost all messages to \n"
                  + "traverse the malcious node while changing all the messages that is receives. It will then \n"
                  + "use the replay attack to send the incorrect message at a later date. The routes will not \n"
                  + "expire for any nodes that the malcious node is aware of so future messages to nodes that \n"
                  + "have been removed will still continue to flow through the malacious node.",
              "Challenge 2", JOptionPane.INFORMATION_MESSAGE);
      filename += "Challenge_2_Scenario.scn";
      break;
    }
    // Okay. New simulation. Have to ask the user what type of sim they want..
    NodeType nodeType = Utilities.popupAskNodeType();
    if (nodeType == null) {
      // User canceled..
      return;
    }
    InputHandler.dispatch(DARSEvent.inNewSim(nodeType));
    // Parse the setup events into memory
    Queue<DARSEvent> Q = Parser.parseSetup(filename);

    if (Q == null) {
      Utilities.showError("Log file can not be parsed.");
      return;
    }

    // Dispatch every event in the Q
    for (DARSEvent d : Q) {
      InputHandler.dispatch(d);
    }
  }

  public static void loadReplayFile(String fileName, GUI g) {
    Queue<DARSEvent> Q = Parser.parseReplay(fileName);

    if (Q == null) {
      Utilities.showError("Log file can not be parsed.");
      return;
    }

    // Okay. New simulation. Have to ask the user what type of sim they want..
    NodeType nt = Utilities.popupAskNodeType();
    if (nt == null) {
      // User canceled..
      return;
    }
    // Start a new simualation
    InputHandler.dispatch(DARSEvent.inNewSim(nt));

    // Ask the user what mode of replay they want
    ReplayMode mode = Replayer.askReplayMode();
    if (mode == null) {
      return;
    }

    // Instantiate a new replayer with the replay events
    // Name the gui as the replayerListener.
    GUI guiInstance = g;
    Replayer replayer = new Replayer(Q,
        (Replayer.ReplayerListener) guiInstance.getReplayerListener(), mode);

  }

  public static void loadReplayFile(String fileName, GUI g, ReplayMode mode) {
    Queue<DARSEvent> Q = Parser.parseReplay(fileName);

    if (Q == null) {
      Utilities.showError("Log file can not be parsed.");
      return;
    }

    // Okay. New simulation. Have to ask the user what type of sim they want..
    NodeType nt = Utilities.popupAskNodeType();
    if (nt == null) {
      // User canceled..
      return;
    }
    // Start a new simualation
    InputHandler.dispatch(DARSEvent.inNewSim(nt));

    // Instantiate a new replayer with the replay events
    // Name the gui as the replayerListener.
    GUI guiInstance = g;
    Replayer replayer = new Replayer(Q,
        (Replayer.ReplayerListener) guiInstance.getReplayerListener(), mode);

  }

  public static String popupAskUser(String question, String[] answers,
      String title) {
    int answer = JOptionPane.showOptionDialog(null, question, title, 0,
        JOptionPane.QUESTION_MESSAGE, null, answers, answers[0]);
    // Return null if the user closed the dialog box
    if (answer == JOptionPane.CLOSED_OPTION) {
      return null;
    }

    // Return their selection
    return answers[answer];

  }

  public static NodeType popupAskNodeType() {
    // Get every node type
    NodeType nTypes[] = getNodeTypes();

    int answer = JOptionPane.showOptionDialog(null,
        "Select a simulation type.", "Select a simulation type.", 0,
        JOptionPane.QUESTION_MESSAGE, null, nTypes, nTypes[0]);

    // Return null if the user closed the dialog box
    if (answer == JOptionPane.CLOSED_OPTION) {
      return null;
    }

    // Return their selection
    return nTypes[answer];
  }

  public static String getTmpLogPath() {
    String tmpDir = System.getProperty("java.io.tmpdir");

    // On some JVMs, a trailing file separator doesn't exist. Correct this.
    if (!tmpDir.endsWith(System.getProperty("file.separator"))) {
      tmpDir = tmpDir + System.getProperty("file.separator");
    }

    return tmpDir + "darslog.tmp";
  }

  public static void runSaveLogDialog(Container parent) {
    JFileChooser chooser = new JFileChooser();
    FileNameExtensionFilter filter = new FileNameExtensionFilter("Log Files",
        "log");
    chooser.setFileFilter(filter);
    int returnVal = chooser.showSaveDialog(parent);
    if (returnVal == JFileChooser.APPROVE_OPTION) {

      // Define the new files to be saved.
      File logFile = new File(getTmpLogPath());
      File saveFile;
      if (!chooser.getSelectedFile().getPath().endsWith(".log")) {
        saveFile = new File(chooser.getSelectedFile().getPath() + ".log");
      } else {
        saveFile = new File(chooser.getSelectedFile().getPath());
      }

      if (!logFile.exists()) {
        JOptionPane.showMessageDialog(parent, "There is nothing to save yet.");
        return;
      }

      // Check to see if we will overwrite the file
      if (saveFile.exists()) {
        int overwrite = JOptionPane.showConfirmDialog(null,
            "File already exists, do you want to overwrite?");
        if (overwrite == JOptionPane.CANCEL_OPTION
            || overwrite == JOptionPane.CLOSED_OPTION
            || overwrite == JOptionPane.NO_OPTION) {
          return;
        }
      }

      // Initialize the file readers and writers
      FileReader in = null;
      FileWriter out = null;

      // Try to open each file
      try {
        int c;
        // Make sure everything has been flushed out of the buffer
        // and has been written to the temporary file.
        Logger logger = Logger.getInstance();
        logger.flushLogFile();

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
        showError("Log file could not be saved at "
            + chooser.getSelectedFile().getPath());
      } catch (IOException e1) {
        showError("Log file could not be saved due to an IO error.");
      }
    }
  }

  private static final NodeType[] nodeTypes = NodeType.values();

  public static NodeType[] getNodeTypes() {
    return nodeTypes;
  }

  static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

  public static String timeStamp() {
    Calendar cal = Calendar.getInstance();
    return sdf.format(cal.getTime());
  }

  public static void captureScreen(Component Area) {

    // Find out where the user would like to save their screen shot
    String fileName = null;
    JFileChooser chooser = new JFileChooser();
    FileNameExtensionFilter filter = new FileNameExtensionFilter(
        "Screen Shots", "png");
    chooser.setFileFilter(filter);
    int returnVal = chooser.showSaveDialog(null);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File saveFile = new File(chooser.getSelectedFile().getPath() + ".png");
      fileName = saveFile.toString();

      // Check to see if we will overwrite the file
      if (saveFile.exists()) {
        int overwrite = JOptionPane.showConfirmDialog(null,
            "File already exists, do you want to overwrite?");
        if (overwrite == JOptionPane.CANCEL_OPTION
            || overwrite == JOptionPane.CLOSED_OPTION
            || overwrite == JOptionPane.NO_OPTION) {
          return;
        }
      }
    }
    // If they didn't hit approve, return
    else {
      return;
    }

    // Determine the exact coordinates of the screen that is to be captured
    Dimension screenSize = Area.getSize();
    Rectangle screenRectangle = new Rectangle();
    screenRectangle.height = screenSize.height;
    screenRectangle.width = screenSize.width;
    screenRectangle.x = Area.getLocationOnScreen().x;
    screenRectangle.y = Area.getLocationOnScreen().y;

    // Here we have to make the GUI Thread sleep for 1/4 of a second
    // just to give the save dialog enough time to close off of the
    // screen. On slower computers they were capturing the screen
    // before the dialog was out of the way.
    try {
      Thread.currentThread();
      Thread.sleep(250);
    } catch (InterruptedException e1) {
      e1.printStackTrace();
    }

    // Attempt to capture the screen at the defined location.
    try {
      Robot robot = new Robot();
      BufferedImage image = robot.createScreenCapture(screenRectangle);
      ImageIO.write(image, "png", new File(fileName));
    } catch (AWTException e) {
      e.printStackTrace();
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, "Could not save screen shoot at: "
          + fileName);
      e.printStackTrace();
    }
  }

}
