package dars.replayer;
import java.util.Iterator;
import java.util.Queue;

import javax.swing.JOptionPane;

import dars.InputHandler;
import dars.OutputConsumer;
import dars.OutputHandler;
import dars.Utilities;
import dars.event.DARSEvent;

public class Replayer implements OutputConsumer {

  public interface ReplayerListener {
    void replayerStarted(Queue<DARSEvent> Q, Replayer instance);
    void replayerFinished(boolean aborted);
  }
  private Queue<DARSEvent> replayEvents;
  private ReplayerListener replayerListener;
  private final ReplayMode mode;

  public ReplayMode getMode() {
    return mode;
  }
  
  public Replayer(Queue<DARSEvent> replayEvents, ReplayerListener rl, ReplayMode mode) {
    this.mode = mode;
    
    //Add this as an output consumer
    OutputHandler.addOutputConsumer(this);
    this.replayEvents = replayEvents;
    this.replayerListener = rl;
    
    //Fire off events at the zero quantum.
    dispatchEventsAtQuantum(replayEvents, 0);
    rl.replayerStarted(replayEvents,this);
    
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
  
  private void finish() {
    if(!isRunning) {
      return;
    }
    isRunning = false;
    //Signal that the replayer is finished.
    replayerListener.replayerFinished(false);
    OutputHandler.removeOutputConsumer(this);
  }
  
  public void abort() {
    if(!isRunning) {
      return;
    }
    isRunning = false;
    //Signal that the replayer is finished with aborted flag.
    replayerListener.replayerFinished(true);
    OutputHandler.removeOutputConsumer(this);
    
  }
  
  private boolean isRunning = true;
  public boolean isRunning() {
    return isRunning;
  }
  
  @Override
  public void consumeOutput(DARSEvent e) {
    switch(e.eventType) {
    case OUT_QUANTUM_ELAPSED:
      //If theres no more events in the Q, remove this as an output consumer
      if(replayEvents.size() == 0) {
        finish();
      }
      System.out.println("dispatching events at quantum: " + e.currentQuantum);
      dispatchEventsAtQuantum(this.replayEvents, e.currentQuantum);
    }
  }
  
  public enum ReplayMode { LOCKED, INTERACTIVE};
  public static ReplayMode askReplayMode(){
  //Ask the user what type of replay they would like to run; locked or interactive
    Object answers[] = {"Locked (Default mode)", "Interactive"};
    int answer = JOptionPane.showOptionDialog(null,
        "Would you like to run the replay in interactive or locked mode?", "Select replay mode.", 0,
        JOptionPane.QUESTION_MESSAGE, null, answers, answers[0]);

    // Return null if the user closed the dialog box
    if (answer == JOptionPane.CLOSED_OPTION) {
      return null;
    }

    // Return their selection
    return ReplayMode.values()[answer];
    
  }
  
}