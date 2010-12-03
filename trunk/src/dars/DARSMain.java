/**
 * 
 */
package dars;

import java.awt.SplashScreen;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import dars.console.Console;
import dars.event.DARSEvent;
import dars.gui.GUI;
import dars.gui.ImageFactory;
import dars.logger.Logger;

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

    //Init images
    ImageFactory.checkInit();
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
    } catch (Exception e) {
      Utilities.showError("A fatal error was encountered while trying to load the GUI. Please file a bug report.");
      System.exit(1);
    }
    
    GUI g = gs.getGui();
    

    // Setup the node inpsector for the gui. This gives the gui a backdoor into the 
    // simulation, where it can view node attributes
    g.setNodeInspector(s);
    
    // Name the GUI as an output consumer
    OutputHandler.addOutputConsumer(g);
    
    System.gc();
    
    //sleep if the splash screen is up
    if(SplashScreen.getSplashScreen() != null ) {
      try {
        Thread.sleep(2000);
      } catch (InterruptedException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
      
    }
    g.setVisible(true);
  
  }
}
