package dars;

import dars.event.DARSEvent;

/**
 * @author Mike
 *
 */
public interface OutputConsumer {
   public void consumeOutput(DARSEvent e);
}
