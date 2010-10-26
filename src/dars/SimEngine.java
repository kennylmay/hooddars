package dars;

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
	private int WAIT_TIME = 1;
	NodeStore node_store = new NodeStore();
		
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
		Node node;
		int index = 1;
		while(node_store.actionsLeft()){
			node = node_store.getNodeByIndex(index);
			// This needs to be a function that a node understands as a second passed.
			node.notify();
			index++;
			/// If we have gone through the list then reset the index.
			if (index > node_store.getNumberOfNodes()){
				index = 1;
	            /// If you want to wait only once per iteration move the try catch here.
			}
			
			try {
				Thread.currentThread();
				Thread.sleep(WAIT_TIME);
			} catch (InterruptedException e) {
				/// See what kind of crap this causes
				e.printStackTrace();
			}
		}
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
		WAIT_TIME = speed;
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
	public int get_speed() {
		return WAIT_TIME;
	}

}

