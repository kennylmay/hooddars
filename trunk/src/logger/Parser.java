/**
 * 
 */
package logger;

import java.io.*;

import dars.NodeAttributes;
import dars.OutputHandler;
import dars.event.DARSEvent;

/**
 * @author misael.marin
 *
 */
public class Parser 
{
    public static void parse(String logFileLocation) 
    {
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
          while (( line = input.readLine()) != null){
            String[] details = line.split(",");

            //if incorrect format, throw exception
            if(details.length != 10)
            {
              System.out.println("Error reading file");
              //throw new Exception("Error: The CSV file is either corrupt or is in an incompatible format. Line # " + lineNumber);
            }
            
            if(lineNumber > 1) //skipping the header line
            {   //Parse and dispatch the log's dars events
              dispatchEvent(details);
              waiting(1); //waiting 1 second between each event.
            }
            //increment line number
            lineNumber++;
          }
        }
        finally {
          input.close();
        }
      }
      catch (IOException ex){
        ex.printStackTrace();
      }
      
    
    } 
    static void dispatchEvent(String[] lineEvent) {
      //lineEvents array = eventType,nodeId,sourceId,destinationId,payload,informationalMessage,newSimSpeed,nodeX,nodeY,nodeRange
      try
      {
        //dispatch each dars event
        if(lineEvent[0].equals("OUT_ADD_NODE"))
        { 
          //waiting(1); //waiting 1 second between each event.
          NodeAttributes newNode = new NodeAttributes();
          newNode.id = lineEvent[1];
          newNode.x = Integer.parseInt(lineEvent[7]);
          newNode.y = Integer.parseInt(lineEvent[8]);
          newNode.range = Integer.parseInt(lineEvent[9]);
          
          OutputHandler.dispatch(DARSEvent.outAddNode(newNode));
        }
        if(lineEvent[0].equals("OUT_MOVE_NODE"))
        {
          //waiting(1); //waiting 1 second between each event.
          OutputHandler.dispatch(DARSEvent.outMoveNode(lineEvent[1],Integer.parseInt(lineEvent[7]),Integer.parseInt(lineEvent[8])));
          
        }
        if(lineEvent[0].equals("OUT_DEL_NODE"))
        {
          //waiting(1); //waiting 1 second between each event.
          OutputHandler.dispatch(DARSEvent.outDeleteNode(lineEvent[1]));
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
