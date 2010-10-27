package dars;

import dars.event.DARSEvent;

/**
 * @author Mike
 *
 */
public interface OutputConsumer {
   public void consume(DARSEvent e);
}
