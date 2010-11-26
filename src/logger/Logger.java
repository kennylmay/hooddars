/**
 * 
 */
package logger;
import dars.InputConsumer;
import dars.OutputConsumer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;

import javax.swing.JOptionPane;

import dars.event.DARSEvent;


/**
 * @author Mike
 * Very basic logger. To use, reference the log
 * method in a static context i.e. Logger.log().
 * Use a DARSEvent as the only parameter. 
 * Logger relies on the getLogString() functionality 
 * provided by the DARSEvent. The logger is 
 * a primary consumer of events dispatched through
 * the output handler. As such, it implements the
 * DARSConsumer interface. Use the getInstance()
 * method to reference the logger in a DARSConsumer
 * context.
 */
public class Logger implements OutputConsumer, InputConsumer {
  static String tempFile = null;
  static File linuxFolder = new File("/tmp");
  static File windowsFolder = new File("C:\\Windows\\Temp");
  
  public static String newline = System.getProperty("line.separator");
  public static synchronized void log(DARSEvent e){
	  
    
	//if file handle is not init, do it
    if(fstream == null ) {
      try {  
        // Check if we are on linux
        if (linuxFolder.exists()){
          tempFile = "/tmp/darslog.tmp";
        }
        // Otherwise we are on windows
        else if( windowsFolder.exists()){
          tempFile = "C:\\Windows\\Temp\\darslog.tmp";
        }
        else{
          JOptionPane.showMessageDialog(null, "Could not write temp file.");
        }
        fstream = new FileWriter(tempFile);
       } 
      catch (IOException e1) {
	     e1.printStackTrace();
	     System.exit(1);
	  }
      out = new BufferedWriter(fstream);
      //append the head of the DARS log file
      try {
		out.append(DARSEvent.getLogHeader() + newline);
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
    }
   
    try {
	  out.append(e.getLogString());
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	
  }
 
  public void deleteLogFile() {
    //Make sure the file handle is closed.
    closeLogFile();
    File tmp = new File(tempFile);
    if (tmp.exists()){
      tmp.delete();
    }
    
  }
  
  private void closeLogFile() {
    if(fstream != null) {
      try {
        if(out != null) { 
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
    if(fstream != null) {
      try {
        if(out != null) { 
          out.flush();
        }
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    fstream = null;
  }

  //Fulfills the DARSConsumer contract
  public void consumeOutput(DARSEvent e) {
    
    //log the event
    Logger.log(e);
    
    switch(e.eventType) {
    case OUT_NEW_SIM:
      //Delete the log file if it exists.
      deleteLogFile();
      break;
      
    case OUT_STOP_SIM:
      //Close the log file
      closeLogFile();
      break;
    }
    

  }
  
  public static Logger getInstance() {
	  return instance_;
  }
  
  private static Logger instance_ = new Logger();
  private static FileWriter fstream;
  private static BufferedWriter out;
  private Logger() { }

@Override
public void consumeInput(DARSEvent e) {
	Logger.log(e);
	
};
}
