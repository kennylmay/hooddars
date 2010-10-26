package dars;

/**
 * @author Kenny
 * 
 */
public class SimEngine {
	/**
	 * Time to wait for an iteration.
	 */
	private int WAIT_TIME = 1;

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

	}

	/**
	 * Function that sets the timer speed
	 * 
	 * This method will allow the adjustment of the time interval(time between
	 * clock ticks) in seconds.
	 * 
	 * @author kennylmay
	 * 
	 * @param speed
	 *            (int) The time in seconds that a simulation should pause
	 *            between ticks.
	 */
	void setSimulationSpeed(int speed) {
		set_speed(speed);
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
	 * Function that sets the timer speed
	 * 
	 * This method will allow the adjustment of the time interval(time between
	 * clock ticks) in seconds.
	 * 
	 * @author kennylmay
	 * 
	 * @param speed
	 *            (int) The time in seconds that a simulation should pause
	 *            between ticks.
	 */
	public void set_speed(int speed) {
		WAIT_TIME = speed;
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
