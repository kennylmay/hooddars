package dars.console;

import dars.OutputConsumer;
import dars.event.DARSEvent;

public class Console implements OutputConsumer {

  @Override
  public void consumeOutput(DARSEvent e) {
    System.out.println(e.getLogString());
  }

}
