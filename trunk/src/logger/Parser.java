/**
 * 
 */
package logger;

import java.io.*;

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
