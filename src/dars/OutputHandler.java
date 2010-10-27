/** 
 * 
 */
package dars;

import java.util.ArrayList;

import dars.event.DARSEvent;

/**
 * @author Mike
 *
 */
public class OutputHandler {
  //List of consumers
  private static ArrayList<OutputConsumer> consumers = new ArrayList<OutputConsumer>();

  public static void removeConsumer(OutputConsumer c) {
    consumers.remove(c);
  }

  public static void addConsumer(OutputConsumer c) {
    consumers.add(c);
  }
  
  public static void dispatch(DARSEvent e){
	  for(OutputConsumer c : consumers){
		  c.consume(e);
	  }
  }
  
}
