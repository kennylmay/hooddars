package dars;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

import dars.NodeStore;
import dars.proto.*;
/**
 * @author Kenny
 * 
 */
public class SimEngine {
	/**
	 * Time to wait for an iteration.
	 */
	private int WAIT_TIME = 1000;
	/**
	 * Boolean that is used to keep track of the state of the simulation
	 */
    private boolean paused = false;
    private NodeStore<Object> store = new NodeStore<Object>();
    
    ActionListener engine = new ActionListener() {
	      public void actionPerformed(ActionEvent evt) {
	         /// Issue a clock tick to each node in the node store.
	    	 for(int index = 0; index < store.getNumberOfNodes(); index++){
	    		Object i = store.getNodeByIndex(index);
	    	 }
	      }
	};
	
	/**
	 * Timer that will control the execution of the simulation
	 */
	Timer timer = new Timer(WAIT_TIME, engine);
		
	/**
	 * Function that will start a simulation
	 * 
	 * This function will be the controlling function for all of the nodes
	 * 
	 * @author kennylmay
	 * 
	 * @param
	 */
	void runSimulation() {
		timer.start();
	}

	/**
	 * Function that sets the timer speed
	 * 
	 * This method will allow the adjustment of the time interval(time between
	 * clock ticks) in seconds.
	 * 
	 * 
	 * @author kennylmay
	 * 
	 * @param speed
	 *            (int) The time in seconds that a simulation should pause
	 *            between ticks.
	 */
	void setSimulationSpeed(int speed) {
		timer.setDelay(speed);
	}
	
	/**
	 * Function that will pause a simulation
	 * 
	 * This function will pause the simulation until the user decides to
	 * continue
	 * 
	 * @author kennylmay
	 * 
	 * @param
	 */
	void pauseSimluation() {
		if (paused == false){
			try {
				timer.wait();
			} catch (InterruptedException e) {
				/// May have to change this catch later
				e.printStackTrace();
			}
			paused = true;
		}else{
			timer.notify();
			paused = false;
		}
	}

	/**
	 * Function that will stop a simulation
	 * 
	 * This function will stop a simulation indefinitely.
	 * 
	 * @author kennylmay
	 * 
	 * @param
	 */
	void stopSimluation() {
		timer.stop();
	}
	

	/**
	 * Function that returns the timer speed
	 * 
	 * This method will return the wait time.
	 * 
	 * @author kennylmay
	 * 
	 * @param 
	 */
	public int getSimSpeed() {
		return timer.getDelay();
	}
}

