package dars.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import dars.InputHandler;
import dars.NodeAttributes;
import dars.NodeInspector;
import dars.event.DARSEvent;

public class NodeAttributesArea extends JPanel implements GNodeListener {

  private JComboBox  nodeSelectorComboBox = new JComboBox();
  private JTextField nodeXField           = new JTextField(4);
  private JTextField nodeYField           = new JTextField(4);
  private JSpinner   nodeRangeSpinner     = new JSpinner(
                                              new SpinnerNumberModel(300, 0,
                                                  1000, 20));

  private boolean blockChangeEvents = false;
  private Vector<String> nodeList = new Vector();
  
  public NodeAttributesArea() {
    // Use a box layout inside a border layout, with an internal flow layout at
    // each vertical item.
    /*
     * _____________________ | ITEM | | | ITEM | | | ITEM | | | .... | |
     * |_______| |
     */
    setLayout(new BorderLayout());
    setPreferredWidth(500);
    JPanel box = new JPanel();
    box.setLayout(new BoxLayout(box, BoxLayout.PAGE_AXIS));

    // setup the node id field and label
    JPanel c;
    c = new JPanel();
    c.setLayout(new FlowLayout(FlowLayout.LEFT, 11, 11));
    c.add(new JLabel("Node ID:"));
    c.add(nodeSelectorComboBox);
    box.add(c);

    // setup the node x and y field
    c = new JPanel();
    c.setLayout(new FlowLayout(FlowLayout.LEFT, 11, 11));
    c.add(new JLabel("X:"));
    c.add(nodeXField);
    c.add(new JLabel("Y:"));
    c.add(nodeYField);
    box.add(c);

    // setup the range field
    c = new JPanel();
    c.setLayout(new FlowLayout(FlowLayout.LEFT, 11, 11));
    c.add(new JLabel("Range:"));
    c.add(nodeRangeSpinner);
    box.add(c);

    add(box, BorderLayout.NORTH);
    setVisible(true);

    // Node combobox action handler
    nodeSelectorComboBox.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent ie) {
        if (nodeSelectorComboBox.getSelectedItem() == null){
          return;
        }
        simArea.selectNode(nodeSelectorComboBox.getSelectedItem().toString());
      }
    });
   
    
    // Range spin button action handler
    nodeRangeSpinner.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent ie) {
        if(blockChangeEvents)
          return;
        InputHandler.dispatch(DARSEvent.inSetNodeRange(nodeSelectorComboBox.getSelectedItem().toString(),
          (Integer) nodeRangeSpinner.getValue()));
      }
    });
    
    // X Text Box Single Handler connected to the "Enter"
    nodeXField.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        // Get the ID off of the Combo Box
        String id = nodeSelectorComboBox.getSelectedItem().toString();
        // Get the current node data and save it off so that it can be 
        // used in case of an invalid entry being entered.
        NodeAttributes att = getAttributes(id);
        int X = att.x;
        int Y = att.y;
        if (id == null)
          return;
        try{
          // Attempt to convert the string to an int if it fails
          // the user messed up and we use the original attributes.
          X = Integer.parseInt(nodeXField.getText());
          Y = Integer.parseInt(nodeYField.getText());
        }catch (NumberFormatException nfe) {
          return;
        }        
        // Dispatch the signal
        InputHandler.dispatch(DARSEvent.inMoveNode(id, X, Y));
      }
    });
    
    nodeYField.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        // Get the ID off of the Combo Box
        String id = nodeSelectorComboBox.getSelectedItem().toString();
        // Get the current node data and save it off so that it can be 
        // used in case of an invalid entry being entered.
        NodeAttributes att = getAttributes(id);
        int X = att.x;
        int Y = att.y;
        if (id == null)
          return;
        try{
          // Attempt to convert the string to an int if it fails
          // the user messed up and we use the original attributes
          X = Integer.parseInt(nodeXField.getText());
          Y = Integer.parseInt(nodeYField.getText());
        }catch (NumberFormatException nfe) {
          return;
        }        
        // Dispatch the signal
        InputHandler.dispatch(DARSEvent.inMoveNode(id, X, Y));
      }
    });
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

  public void nodeAdded(String nodeId) {
    nodeSelectorComboBox.addItem(nodeId);
    nodeList.add(nodeId);
  }

  public void nodeDeleted(String nodeId) {
    nodeSelectorComboBox.removeItem(nodeId);
    nodeList.remove(nodeId);
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
  public void nodeSelected(GNode gnode) {
    NodeAttributes ni = getAttributes(gnode.getId());
    if (ni != null) {
      setAttributes(ni);
      nodeSelectorComboBox.setSelectedItem(gnode.getId());
    }
  }

  private NodeAttributes getAttributes(String id) {
    // Use the node inspector interface to view the properties of the node
    return nodeInspector.getNodeAttributes(id);
  }

  private void setAttributes(NodeAttributes n) {
    blockChangeEvents = true;
    nodeSelectorComboBox.setSelectedItem(n.id);
    nodeXField.setText(Integer.toString(n.x));
    nodeYField.setText(Integer.toString(n.y));
    nodeRangeSpinner.setValue(n.range);
    blockChangeEvents = false;
  }

  public void setNodeInspector(NodeInspector ni) {
    this.nodeInspector = ni;
  }

  public void setNode(String nodeId) {

  }
  
  public Vector<String> getNodeList(){
    return nodeList;
  }

  public void clear(){
    nodeSelectorComboBox.removeAllItems();
    nodeXField.setText("");
    nodeYField.setText("");
    nodeList.clear();
  }
  
  public void selectNodeById(String id) {
    setAttributes(getAttributes(id));
  }
  private NodeInspector nodeInspector;
}
