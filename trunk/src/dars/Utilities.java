package dars;

import java.awt.Container;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import dars.event.DARSEvent;
import dars.event.DARSEvent.EventType;
import dars.event.DARSEvent.SimType;
import logger.Logger;

public class Utilities {
  public static void setSwingFont(javax.swing.plaf.FontUIResource f) {
    java.util.Enumeration keys = UIManager.getDefaults().keys();
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
  
  public static DARSEvent.SimType popupAskSimType() {
    //Use refelection to get every sim type enum
    DARSEvent.SimType sTypes[] = getSimTypes();
    
    int answer = JOptionPane.showOptionDialog(null,
                 "Select a simulation type.",
                 "Select a simulation type.",
                 0,
                 JOptionPane.QUESTION_MESSAGE,
                 null,
                 sTypes,
                 sTypes[0]);
    
    DARSEvent.SimType st;
    //Return null if the user closed the dialog box
    if(answer == JOptionPane.CLOSED_OPTION) {
      return null;
    }
    
    //Return their selection
    return sTypes[answer];
  }
  
  
  
  public static void runSaveLogDialog(Container parent) {
    JFileChooser chooser = new JFileChooser();
    FileNameExtensionFilter filter = new FileNameExtensionFilter("Log Files",
        "log");
    chooser.setFileFilter(filter);
    int returnVal = chooser.showSaveDialog(parent);
    if (returnVal == JFileChooser.APPROVE_OPTION) {

      // Determine where the tempfile is
      String tempFile = null;
      File linuxFolder = new File("/tmp/darslog.tmp");
      File windowsFolder = new File("C:\\Windows\\Temp\\darslog.tmp");
      // Check if we are on linux
      if (linuxFolder.exists()) {
        tempFile = "/tmp/darslog.tmp";
      }
      // Otherwise we are on windows
      else if (windowsFolder.exists()) {
        tempFile = "C:\\Windows\\Temp\\darslog.tmp";
      } else {
        JOptionPane.showMessageDialog(null, "Could not open temp file.");
      }

      // Define the new files to be saved.
      File logFile = new File(tempFile);
      File saveFile = new File(chooser.getSelectedFile().getPath() + ".log");

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
        JOptionPane.showMessageDialog(parent, "Log file could not be saved at"
            + chooser.getSelectedFile().getPath());
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    }
  }

  public static SimType[] getSimTypes() {
    Class<DARSEvent> c = DARSEvent.class;
    Field[] fields = c.getFields();
    Class<DARSEvent.SimType> sType =  (Class<DARSEvent.SimType>) fields[10].getType();
    return sType.getEnumConstants();
  }
    
}
