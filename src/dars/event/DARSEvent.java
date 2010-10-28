/**
 * 
 */
package dars.event;
import dars.NodeAttributes;
import dars.Message;

/**
 * @author Mike
 * DARSEvent is a Mondo-big class that is
 * used throughout DARS. 
 */
public abstract class DARSEvent {
	
  
  //extracts a log entry from this event.
  public String getLogString() {
    return null;
    
  }
}
