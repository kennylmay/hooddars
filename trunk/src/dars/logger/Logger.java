/**
 * 
 */
package dars.logger;

import dars.InputConsumer;
import dars.OutputConsumer;
import dars.Utilities;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;


import dars.event.DARSEvent;

/**
 * @author Mike Very basic logger. To use, reference the log method in a static
 *         context i.e. Logger.log(). Use a DARSEvent as the only parameter.
 *         Logger relies on the getLogString() functionality provided by the
 *         DARSEvent. The logger is a primary consumer of events dispatched
 *         through the output handler. As such, it implements the DARSConsumer
 *         interface. Use the getInstance() method to reference the logger in a
 *         DARSConsumer context.
 */
public class Logger implements OutputConsumer, InputConsumer {

  public static String newline       = System.getProperty("line.separator");

  public static synchronized void log(DARSEvent e) {

    // if file handle is not init, do it
    if (fstream == null) {
      try {
        fstream = new FileWriter(Utilities.getTmpLogPath());
      } catch (IOException e2) {
        Utilities.showError("(Fatal) Could not write to the DARS temporary file due to an IO exception :" + e2.getMessage());
        System.exit(1);
      }
      out = new BufferedWriter(fstream);
      // append the head of the DARS log file
      try {
        out.append(DARSEvent.getLogHeader() + newline);
      } catch (IOException e1) {
        Utilities.showError("(Fatal) Could not write to the DARS temporary file due to an IO exception :" + e1.getMessage());
        System.exit(1);
      }
      
      //Arrange for the file to be deleted on exit
      File tmpFile = new File(Utilities.getTmpLogPath());
      tmpFile.deleteOnExit();
    }

    try {
      out.append(e.getLogString());
    } catch (IOException e1) {
      Utilities.showError("(Fatal) Could not write to the DARS temporary file due to an IO exception :" + e1.getMessage());
      System.exit(1);
    }

  }

  public void deleteLogFile() {
    // Make sure the file handle is closed.
    closeLogFile();
    File tmp = null;
    try {
      tmp = new File(Utilities.getTmpLogPath());
      if (tmp.exists()) {
        tmp.delete();
      }
    } catch (Exception e) {
      // Fail quietly since the file doesn't exist yet
    }

  }

  private void closeLogFile() {
    if (fstream != null) {
      try {
        if (out != null) {
          out.flush();
        }
        fstream.close();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    fstream = null;
  }

  public void flushLogFile() {
    if (fstream != null) {
      try {
        if (out != null) {
          out.flush();
        }
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    fstream = null;
  }

  // Fulfills the DARSConsumer contract
  public void consumeOutput(DARSEvent e) {

    // log the event
    Logger.log(e);

    switch (e.eventType) {
    case OUT_NEW_SIM:
      // Delete the log file if it exists.
      deleteLogFile();
      break;

    case OUT_STOP_SIM:
      // Close the log file
      closeLogFile();
      break;
    }

  }

  public static Logger getInstance() {
    return instance_;
  }

  private static Logger         instance_ = new Logger();
  private static FileWriter     fstream;
  private static BufferedWriter out;

  private Logger() {
  }

  @Override
  public void consumeInput(DARSEvent e) {
    Logger.log(e);

  };
}
