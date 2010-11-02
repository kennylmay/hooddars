package dars.error;

/* @ author Jagriti
 * This function shows a error message box when the user tries to add node outside the 
 * Simulation area 
 */
import javax.swing.JOptionPane;

import dars.NodeAttributes;

import dars.gui.SimArea;

public class InputError {
  public void NodeAttributes(NodeAttributes ni)
  { if(ni.range > SimArea.WIDTH ){JOptionPane.showMessageDialog
    (null, "Node createdoutside the Simulation Area");

}

}
}