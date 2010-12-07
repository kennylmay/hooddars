/** 
 * 
 */
package dars;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import dars.event.DARSEvent;

/**
 * @author Mike
 * 
 */

public class OutputHandler {

  // List of consumers
  private static CopyOnWriteArrayList<OutputConsumer> consumers = new CopyOnWriteArrayList<OutputConsumer>();

  public static void removeOutputConsumer(OutputConsumer c) {
    consumers.remove(c);
  }

  public static void addOutputConsumer(OutputConsumer c) {
    consumers.add(c);
  }

  public static void dispatch(DARSEvent e) {
    //Apply event filter
    if(filteredEvents.contains(e.eventType)) {
      return;
    }
    for (OutputConsumer c : consumers) {
      c.consumeOutput(e);
    }
  }
  
  private static final CopyOnWriteArraySet<DARSEvent.EventType> filteredEvents = new CopyOnWriteArraySet<DARSEvent.EventType>();
  public static void addFilteredEvent(DARSEvent.EventType eType) {
    filteredEvents.add(eType);
  }
  
  public static void removeFilteredEvent(DARSEvent.EventType eType) {
    filteredEvents.remove(eType);
  }

}
