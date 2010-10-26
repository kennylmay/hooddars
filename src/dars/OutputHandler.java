/** 
 * 
 */
package dars;

import java.util.ArrayList;

/**
 * @author Mike
 *
 */
public class OutputHandler {
  //List of consumers
  private static ArrayList<DARSConsumer> consumers = new ArrayList<DARSConsumer>();

  public static void removeConsumer(DARSConsumer c) {
    consumers.remove(c);
  }

  public static void addConsumer(DARSConsumer c) {
    consumers.add(c);
  }
  
  public static void dispatch(DARSEvent e){
	  for(DARSConsumer c : consumers){
		  c.consume(e);
	  }
  }
  
}
