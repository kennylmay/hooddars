/**
 * 
 */
package dars.proto;

/**
 * Location Class.
 * 
 * This is a relatively simple class to hold a nodes location. Initially it will
 * contain an X and Y coordinate that will relate to a coordinate plain but
 * later could be explained.
 * 
 * @author kresss
 * 
 */
public class Location {

	/**
	 * Simple setter function for X coordinate.
	 * 
	 * @author kresss
	 * 
	 * @param x New value for X coordinate.
	 */
	public void setX(int x) {
		X = x;
	}

	/**
	 * Simple getter function for X coordinate.
	 * 
	 * @author kresss
	 * 
	 * @return Returns current value of X coordinate.
	 */
	public int getX() {
		return X;
	}

	/**
	 * Simple setter function for Y coordinate.
	 * 
	 * @author kresss
	 * 
	 * @param y New value for Y coordinate.
	 */
	public void setY(int y) {
		Y = y;
	}

	/**
	 * Simple getter function for Y coordinate.
	 * 
	 * @author kresss
	 * 
	 * @return Returns current value of Y coordinate.
	 */
	public int getY() {
		return Y;
	}


	/**
	 * Set function to set both X and Y coordinates at the same time.
	 * 
	 * @author kresss
	 * 
	 * @param x New value for X coordinate.
	 * @param y New value for Y coordinate.
	 */
	public void setXY(int x, int y) {
		X = x;
		Y = y;
	}

	private int X;
	private int Y;

}
