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
  public static String newline = System.getProperty("line.separator");
  public static void log(DARSEvent e){
	  
    
	//if file handle is not init, do it
    if(fstream == null ) {
      try {
        fstream = new FileWriter("darslog.tmp");
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
 
  private void deleteLogFile() {
    //Make sure the file handle is closed.
    closeLogFile();
    
    //Delete the file
    File tmp = new File("darslog.tmp");
    tmp.delete();
    
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
