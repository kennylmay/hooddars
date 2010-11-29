package dars.replayer;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.Queue;

import dars.InputHandler;
import dars.OutputConsumer;
import dars.OutputHandler;
import dars.Utilities;
import dars.event.DARSEvent;

public class Replayer implements OutputConsumer {

  public interface ReplayerListener {
    void replayerStarted();
    void replayerFinished();
  }
  private Queue<DARSEvent> replayEvents;
  private ReplayerListener replayerListener;
  
  public Replayer(Queue<DARSEvent> replayEvents, ReplayerListener rl) {
    //Add this as an output consumer
    OutputHandler.addOutputConsumer(this);
    this.replayEvents = replayEvents;
    this.replayerListener = rl;
    
    //Fire off events at the zero quantum.
    dispatchEventsAtQuantum(replayEvents, 0);
    rl.replayerStarted();
    
  }
  
  private void dispatchEventsAtQuantum(Queue<DARSEvent> Q, long quantum) {
     Iterator<DARSEvent> iter = Q.iterator();
     while(iter.hasNext()) {
       DARSEvent d = iter.next();
       
       
       //If this quantum is greater than specified quantum, break out
       //This assumes the list is ordered. If it isn't, change this routine!
       if(d.currentQuantum > quantum) {
         return;
       }

       //If this quantum is = to the specified quantum, pull it off the Q and dispatch it
       if(d.currentQuantum == quantum) {
         iter.remove();
         InputHandler.dispatch(d);
         continue;
       }
       
       //If we get here, theres a bug
       Utilities.showError("Impossible sequence in replayer dispatch events. Please file a bug report.");
       System.exit(1);
     }
  }
  
  @Override
  public void consumeOutput(DARSEvent e) {
    switch(e.eventType) {
    case OUT_QUANTUM_ELAPSED:
      //If theres no more events in the Q, remove this as an output consumer
      if(replayEvents.size() == 0) {
        replayerListener.replayerFinished();
        OutputHandler.removeOutputConsumer(this);
      }
      System.out.println("dispatching events at quantum: " + e.currentQuantum);
      dispatchEventsAtQuantum(this.replayEvents, e.currentQuantum);
    }
  }
  
}