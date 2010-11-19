/**
 * 
 */
package logger;

import java.io.*;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.Queue;

import dars.InputHandler;
import dars.NodeAttributes;
import dars.OutputHandler;
import dars.event.DARSEvent;

/**
 * @author misael.marin
 *
 */
public class Parser 
{
  
  static private boolean isSetupEvent(DARSEvent d) {
    //Setup events are Q=0 and most IN_... types
    if( ! d.currentQuantum.equals(BigInteger.ZERO)) {
      return false;
    }
    
    switch(d.eventType) {
    case IN_ADD_NODE:
    case IN_DEL_NODE:
    case IN_MOVE_NODE:
    case IN_SET_NODE_RANGE:
    case IN_SET_NODE_PROMISCUITY:
    case IN_INSERT_MESSAGE:
      return true;
    }
    return false;
  }
  
  public static Queue<DARSEvent> parseSetup(String logFileLocation) {
    
    //Open up the file
    FileReader LogFile = null;
    try {
      LogFile = new FileReader(logFileLocation);
    } catch (FileNotFoundException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    BufferedReader input =  new BufferedReader(LogFile);
    
    
    Queue<DARSEvent> Q = new LinkedList<DARSEvent>();
    DARSEvent d;
    String line;
    
    //Make sure this is a valid DARS Log file by matching the first line
    //with the header of current DARSEvents
    try {
      line = input.readLine();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return null;
    }
    
    if( ! DARSEvent.getLogHeader().equals(line)) {
      System.out.println("Error: Not a valid DARS file");
      return null;
    }
    
    //Get every DARSEvents in the file that is a setup event.
    try {
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
  
  
  public static void parseReplay(String logFileLocation) {
    DARSEvent d;
  ///Read in log file
    try {
      //use buffering, reading one line at a time
      //FileReader always assumes default encoding is OK!
      FileReader LogFile = new FileReader(logFileLocation);
      BufferedReader input =  new BufferedReader(LogFile);
      try {
        String line = "";
        int lineNumber = 1;
        //go through lines
        while ((line = input.readLine()) != null){
          //if incorrect format, throw exception
          if(lineNumber == 1)
          { 
            if(!line.equals(DARSEvent.getLogHeader()))
            {
              System.out.println("Error reading file");
              throw new Exception("Error: The CSV file is either corrupt or is in an incompatible format. Line # " + lineNumber);
            }
          }
          else
          {
           //Parse and dispatch the log's dars events
            d = DARSEvent.parseLogString(line);
            InputHandler.dispatch(d);
          }
          //increment line number
          lineNumber++;
        }
      }
      finally {
        input.close();
      }
    }
    catch (Exception ex){
      ex.printStackTrace();
    }
  }
  
  static void waiting (int n){
    
    long t0, t1;
  
    t0 =  System.currentTimeMillis();
  
    do{
        t1 = System.currentTimeMillis();
    }
    while ((t1 - t0) < (n * 1000));
  }
}
