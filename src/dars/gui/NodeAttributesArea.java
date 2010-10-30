package dars.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

public class NodeAttributesArea extends JPanel implements GNodeListener {

	private JTextField nodeIdField         = new JTextField(3);
	private JComboBox nodeSelectorComboBox = new JComboBox();
	private JTextField nodeXField          = new JTextField(4);
	private JTextField nodeYField          = new JTextField(4);
    private JSpinner nodeRangeSpinner      = new JSpinner(new SpinnerNumberModel(300, 0, 1000,1));
    
	public NodeAttributesArea() {
		//Use a box layout inside a border layout, with an internal flow layout at each vertical item.
		/* _____________________
		 * |  ITEM |  |
		 * |  ITEM |  |
		 * |  ITEM |  |
		 * |  .... |  |
		 * |_______|  |
		 */
		setLayout(new BorderLayout());
		setPreferredWidth(500);
		JPanel box = new JPanel();
		box.setLayout(new BoxLayout(box, BoxLayout.PAGE_AXIS));
				
		
		//setup the node id field and label
		JPanel c;
		c = new JPanel();
		c.setLayout(new FlowLayout(FlowLayout.LEFT, 11, 11));
		c.add(new JLabel("Node ID:"));
		c.add(nodeIdField);
		box.add(c);
		
		//setup the node x and y field
		c = new JPanel();
		c.setLayout(new FlowLayout(FlowLayout.LEFT, 11, 11));
		c.add(new JLabel("X:"));
		c.add(nodeXField);
		c.add(new JLabel("Y:"));
		c.add(nodeYField);
		box.add(c);
		
		//setup the range field
		c = new JPanel();
		c.setLayout(new FlowLayout(FlowLayout.LEFT, 11, 11));
		c.add(new JLabel("Range:"));
		c.add(nodeRangeSpinner);
		box.add(c);
		
		//setup the node spinner
		c = new JPanel();
		c.setLayout(new FlowLayout(FlowLayout.LEFT, 11, 11));
		c.add(new JLabel("Select a different node:"));
		c.add(nodeSelectorComboBox);
		box.add(c);
		
		add(box, BorderLayout.NORTH);
		setVisible(true);
		
	}
	private void setPreferredWidth(int i) {
		// TODO Auto-generated method stub
		
	}
	private void setWidth(int i) {
		// TODO Auto-generated method stub
		
	}
	private static final long serialVersionUID = 1L;

	public void setSimArea(SimArea simArea) {
	  this.simArea = simArea;
	}
	private SimArea simArea;

  @Override
  public void nodeEntered(GNode node) {
    // TODO Auto-generated method stub
    
  }
  @Override
  public void nodeExited(GNode node) {
    // TODO Auto-generated method stub
    
  }
  @Override
  public void nodeMoved(GNode node, int new_x, int new_y) {
    // TODO Auto-generated method stub
    
  }
  @Override
  public void nodePopupEvent(GNode node, int x, int y) {
    // TODO Auto-generated method stub
    
  }
  @Override
  public void nodeSelected(GNode node) {
    // TODO Auto-generated method stub
    
  }
}
