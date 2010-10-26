/**
 * 
 */
package logger;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import dars.DARSEvent;
/**
 * @author Mike
 *
 */
public class Logger {
  public static void log(DARSEvent e){
	//if file handle is not init, do it
    if(fstream == null) {
      try {
        fstream = new FileWriter("darslog.tmp");
       } 
      catch (IOException e1) {
	     e1.printStackTrace();
	     System.exit(1);
	  }
      out = new BufferedWriter(fstream);
    }
    
    try {
	  out.append(e.getLogString());
	} catch (IOException e1) {
	  e1.printStackTrace();
	  System.exit(1);	  
	}
	
  }
  
  private static FileWriter fstream;
  private static BufferedWriter out;
}
