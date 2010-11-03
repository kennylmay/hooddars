package dars.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import dars.InputHandler;
import dars.NodeAttributes;
import dars.NodeInspector;
import dars.SimEngine;
import dars.event.DARSEvent;

public class NodeAttributesArea extends JPanel implements GNodeListener {

  private JComboBox  nodeSelectorComboBox = new JComboBox();
  private JTextField nodeXField           = new JTextField(4);
  private JTextField nodeYField           = new JTextField(4);
  private JSpinner   nodeRangeSpinner     = new JSpinner(
                                              new SpinnerNumberModel(300, 0,
                                                  1000, 1));

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
        simArea.selectNode(nodeSelectorComboBox.getSelectedItem().toString());
      }
    });
   
    // Range spin button action handler
    nodeRangeSpinner.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent ie) {
        String id = nodeSelectorComboBox.getSelectedItem().toString();
        NodeAttributes att = getAttributes(id);
        att.range = (Integer) nodeRangeSpinner.getValue();
        InputHandler.dispatch(DARSEvent.inEditNode(id, att));
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
  }

  public void nodeDeleted(String nodeId) {
    nodeSelectorComboBox.removeItem(nodeId);
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
    nodeSelectorComboBox.setSelectedItem(n.id);
    nodeXField.setText(Integer.toString(n.x));
    nodeYField.setText(Integer.toString(n.y));
    nodeRangeSpinner.setValue(n.range);
  }

  public void setNodeInspector(NodeInspector ni) {
    this.nodeInspector = ni;
  }

  public void setNode(String nodeId) {

  }

  private NodeInspector nodeInspector;
}
