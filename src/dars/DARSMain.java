/**
 * 
 */
package dars;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import console.Console;
import logger.Logger;
import dars.event.DARSEvent;
import dars.gui.GUI;

/**
 * @author Mike
 * 
 */
public class DARSMain {

  public DARSMain() { };
  /**
   * @param args
   */
  public static void main(String[] args) {
    
    Utilities.setSwingFont(Defaults.FONT);
    
    // Setup the logger to consume DARSEvents from both the input handler and
    // the output handler. From henceforth, all DARSEvents that pass through
    // the Input and Output handlers will be logged.
    InputHandler.addInputConsumer(Logger.getInstance());
    OutputHandler.addOutputConsumer(Logger.getInstance());

    // Instantiate the simulator engine
    SimEngine s = new SimEngine();

    // Make the time keeping component of the simulator engine viewable to DARSEvents
    DARSEvent.setSimTimeKeeper( (SimulationTimeKeeper) s);
      
    // Name the simulator engine as an input consumer
     InputHandler.addInputConsumer(s);

    // Instantiate the gui SYNCHRONOUSLY on the event dispatching thread
     GUIStarter gs = new GUIStarter();
     try {
      SwingUtilities.invokeAndWait(gs);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    GUI g = gs.getGui();
    

    // Setup the node inpsector for the gui. This gives the gui a backdoor into the 
    // simulation, where it can view node attributes
    g.setNodeInspector(s);
    
    // Name the GUI as an output consumer
    OutputHandler.addOutputConsumer(g);
    OutputHandler.addOutputConsumer(new Console());
  }
}
