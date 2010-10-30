/**
 * 
 */
package dars;

import java.util.ArrayList;

import logger.Logger;
import dars.event.DARSEvent;

/**
 * @author Mike
 *
 */
public class InputHandler {
  public static void dispatch(DARSEvent e){
	for(InputConsumer c : inputConsumers){
	  c.consumeInput(e);
    }
  } 
    
  public static void addInputConsumer(InputConsumer c) {
	  inputConsumers.add(c);
  }
  
  public static void removeInputConsumer(InputConsumer c) {
	  inputConsumers.remove(c);
  }
  
  private static final ArrayList<InputConsumer> inputConsumers = new ArrayList<InputConsumer>();

}


