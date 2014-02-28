/**
 * 
 */
package dars.logger;

import java.io.*;
import java.util.LinkedList;
import java.util.Queue;
import dars.event.DARSEvent;

/**
 * @author misael.marin
 *
 */
public class Parser 
{
  
  static private boolean isReplayEvent(DARSEvent d) {
    switch(d.eventType) {
    case IN_ADD_NODE:
    case IN_DEL_NODE:
    case IN_MOVE_NODE:
    case IN_SET_NODE_RANGE:
    case IN_SET_NODE_PROMISCUITY:
    case IN_SET_NODE_DROP_MESSAGES:
    case IN_SET_NODE_CHANGE_MESSAGES:
    case IN_SET_OVERRIDE_HOPS:
    case IN_SET_HOPS_COUNT:
    case IN_CLEAR_SIM:
    case IN_INSERT_MESSAGE: 
    case IN_STOP_SIM:
      return true;
    }
    return false;    
  }
  
  static private boolean isSetupEvent(DARSEvent d) {
    //Setup events are Q=0 and most IN_... types
    if( d.currentQuantum != 0) {
      return false;
    }

    if(isReplayEvent(d)) {
      return true;
    }
    
    return false;
  }
  
  private static BufferedReader getBufferedReader(String file) {
    //Open up the file
    FileReader LogFile = null;
    try {
      LogFile = new FileReader(file);
    } catch (FileNotFoundException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    return new BufferedReader(LogFile); 
  }
  
  
  private static boolean isValidDARSLogFile(String logFileLocation)  {
    BufferedReader input = getBufferedReader(logFileLocation);
    String line = "";
    //Make sure this is a valid DARS Log file by matching the first line
    //with the header of current DARSEvents
    try {
      line = input.readLine();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    try {
      input.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    if( ! DARSEvent.getLogHeader().equals(line)) {

      return false;
    }
    return true;
  }
  
  public static Queue<DARSEvent> parseReplay(String logFileLocation) {
    BufferedReader input = getBufferedReader(logFileLocation);
    Queue<DARSEvent> Q = new LinkedList<DARSEvent>();
    String line = "";
    DARSEvent d;
    if(!isValidDARSLogFile(logFileLocation)) {
      return null;
    }
    

    
  //Get every DARSEvents in the file that is a Replay event.
    try {
      //Read past the first line
      line = input.readLine();
      
      while((line = input.readLine()) != null) {
        d = DARSEvent.parseLogString(line);
        if(isReplayEvent(d)) {
          Q.add(d);
        }
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return Q;
  }
  
  public static Queue<DARSEvent> parseSetup(String logFileLocation) {
    BufferedReader input = getBufferedReader(logFileLocation);
    Queue<DARSEvent> Q = new LinkedList<DARSEvent>();
    DARSEvent d;
    String line;
    
    if(!isValidDARSLogFile(logFileLocation)) {
       return null;
     }
    
    //Get every DARSEvents in the file that is a setup event.
    try {
      //Read past the first line
      line = input.readLine();
      
      while((line = input.readLine()) != null) {
        d = DARSEvent.parseLogString(line);
        if(isSetupEvent(d)) {
          Q.add(d);
        }
        
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    return Q;
  }
  
}
