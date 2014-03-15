package dars.gui;

import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import dars.Defaults;
import dars.InputHandler;
import dars.NodeAttributes;
import dars.NodeInspector;
import dars.event.DARSEvent;

public class NodeAttributesArea extends JPanel implements GNodeListener,
    NodeControls {

  private JComboBox<String>                nodeSelectorComboBox      = new JComboBox<String>();
  private JSpinner                 nodeRangeSpinner          = new JSpinner(
                                                                 new SpinnerNumberModel(
                                                                     0, -19,
                                                                     1000, 20));

  private JSpinner                 XSpinner                  = new JSpinner(
                                                                 new SpinnerNumberModel(
                                                                     0, -10,
                                                                     9999, 10));

  private JSpinner                 YSpinner                  = new JSpinner(
                                                                 new SpinnerNumberModel(
                                                                     0, -10,
                                                                     9999, 10));

  private JButton                  nodeAttributesButton      = new JButton(
                                                                 "Attributes");
  private JCheckBox                dropMessagesCheckBox      = new JCheckBox(
                                                                 "Drop Mess.");
  private JCheckBox                promiscuousModeCheckBox   = new JCheckBox(
                                                                 "Listen Mode");
  private JPanel                   overrideHopsPanel         = new JPanel();
  private JCheckBox                overrideHopsCheckBox      = new JCheckBox(
                                                                 "Set Hops");
  private JCheckBox                changeNarrMessageCheckBox = new JCheckBox(
                                                                 "Change Mess.");
  private JSpinner                 overrideHopsSpinner       = new JSpinner(
                                                                 new SpinnerNumberModel(
                                                                     1, -1, 99,
                                                                     1));
  private JCheckBox                dontExpireRoutesCheckBox  = new JCheckBox("No Rt Expiration.");
  private JCheckBox                replayMessagesCheckBox = new JCheckBox("Replay Mess.");

  private boolean                  blockChangeEvents         = false;
  private Vector<String>           nodeList                  = new Vector<String>();
  private HashMap<String, JDialog> openNodeDialogs           = new HashMap<String, JDialog>();

  public NodeAttributesArea() {

    // Set locked mode
    setLock(true);

    // Set keyboard shortcuts
    nodeAttributesButton.setMnemonic(KeyEvent.VK_A);
    promiscuousModeCheckBox.setMnemonic(KeyEvent.VK_M);
    dropMessagesCheckBox.setMnemonic(KeyEvent.VK_D);
    overrideHopsCheckBox.setMnemonic(KeyEvent.VK_O);
    changeNarrMessageCheckBox.setMnemonic(KeyEvent.VK_C);
    dontExpireRoutesCheckBox.setMnemonic(KeyEvent.VK_E);
    replayMessagesCheckBox.setMnemonic(KeyEvent.VK_R);

    FlowLayout flow = new FlowLayout();
    flow.setAlignment(FlowLayout.LEFT);
    flow.setVgap(0);
    flow.setHgap(0);
    overrideHopsPanel.setLayout(flow);
    overrideHopsCheckBox.setFont(Defaults.BOLDFACED_FONT);
    overrideHopsPanel.add(overrideHopsCheckBox);
    overrideHopsPanel.add(overrideHopsSpinner);
   
    // Add action handlers
    nodeSelectorComboBox.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent ie) {
        if (blockChangeEvents) {
          return;
        }

        if (nodeSelectorComboBox.getSelectedItem() == null) {
          setLock(true);
          return;
        }

        setAttributes(getAttributes((nodeSelectorComboBox.getSelectedItem()
            .toString())));

        if (!lockedReplayMode) {
          setLock(false);
        }

        simArea.selectNode(nodeSelectorComboBox.getSelectedItem().toString());

      }
    });

    // Range spin button action handler
    nodeRangeSpinner.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent ie) {
        if (blockChangeEvents)
          return;

        if (nodeSelectorComboBox.getSelectedItem() == null) {
          return;
        }

        // Enforce minumum range of zero
        int range = (Integer) nodeRangeSpinner.getValue();
        range = Math.max(0, range);

        InputHandler.dispatch(DARSEvent.inSetNodeRange(nodeSelectorComboBox
            .getSelectedItem().toString(), range));
      }
    });

    // X Text Box Single Handler connected to the "Enter"
    XSpinner.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent ie) {
        if (blockChangeEvents) {
          return;
        }
        // Get the ID off of the Combo Box
        String id = nodeSelectorComboBox.getSelectedItem().toString();
        // Get the current node data and save it off so that it can be
        // used in case of an invalid entry being entered.
        NodeAttributes att = getAttributes(id);
        int X = att.x;
        int Y = att.y;
        if (id == null)
          return;
        try {
          // Attempt to convert the string to an int if it fails
          // the user messed up and we use the original attributes.
          X = Integer.parseInt(XSpinner.getValue().toString());
          Y = Integer.parseInt(YSpinner.getValue().toString());
        } catch (NumberFormatException nfe) {
          return;
        }
        // Enforce boundaries
        Point p = simArea.getBoundedNodePoint(new Point(X, Y));

        // Dispatch the signal
        InputHandler.dispatch(DARSEvent.inMoveNode(id, p.x, p.y));

      }
    });

    YSpinner.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent ie) {
        if (blockChangeEvents) {
          return;
        }

        // Get the ID off of the Combo Box
        String id = nodeSelectorComboBox.getSelectedItem().toString();
        // Get the current node data and save it off so that it can be
        // used in case of an invalid entry being entered.
        NodeAttributes att = getAttributes(id);
        int X = att.x;
        int Y = att.y;
        if (id == null)
          return;
        try {
          // Attempt to convert the string to an int if it fails
          // the user messed up and we use the original attributes
          X = Integer.parseInt(XSpinner.getValue().toString());
          Y = Integer.parseInt(YSpinner.getValue().toString());
        } catch (NumberFormatException nfe) {
          return;
        }

        // Enforce boundaries
        Point p = simArea.getBoundedNodePoint(new Point(X, Y));

        // Dispatch the signal
        InputHandler.dispatch(DARSEvent.inMoveNode(id, p.x, p.y));
      }
    });

    nodeAttributesButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        // If the attributes button is clicked while nothing is selected return
        if (nodeSelectorComboBox.getSelectedItem() == null) {
          return;
        }
        // If the node attributes window has already been opened check to see if
        // it
        // is still visible
        if (openNodeDialogs.containsKey((nodeSelectorComboBox.getSelectedItem()
            .toString()))) {
          JDialog dialog = openNodeDialogs.get((nodeSelectorComboBox
              .getSelectedItem().toString())); 
          // If it is not still visible, show it.
          if (dialog.isVisible() == false) {
            dialog.setVisible(true);
            return;
          }
          return;
        }
        JDialog dialog = nodeInspector.getNodeDialog(nodeSelectorComboBox
            .getSelectedItem().toString());
        dialog.setVisible(true);
        openNodeDialogs.put(nodeSelectorComboBox.getSelectedItem().toString(),
            dialog);
      }
    });

    promiscuousModeCheckBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        if (blockChangeEvents) {
          return;
        }
        if (nodeSelectorComboBox.getSelectedItem() == null) {
          return;
        }
               
        InputHandler.dispatch(DARSEvent.inSetNodePromiscuity(
            nodeSelectorComboBox.getSelectedItem().toString(),
            promiscuousModeCheckBox.isSelected()));
      }
    });

    dropMessagesCheckBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        if (blockChangeEvents) {
          return;
        }
        if (nodeSelectorComboBox.getSelectedItem() == null) {
          return;
        }
                
        InputHandler.dispatch(DARSEvent.inSetNodeDropMessages(
            nodeSelectorComboBox.getSelectedItem().toString(),
            dropMessagesCheckBox.isSelected()));
      }
    });
    
    replayMessagesCheckBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        if (blockChangeEvents) {
          return;
        }
        if (nodeSelectorComboBox.getSelectedItem() == null) {
          return;
        }
                
        InputHandler.dispatch(DARSEvent.inSetNodeReplayMessages(
            nodeSelectorComboBox.getSelectedItem().toString(),
            replayMessagesCheckBox.isSelected()));
      }
    });

    dontExpireRoutesCheckBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        if (blockChangeEvents) {
          return;
        }
        if (nodeSelectorComboBox.getSelectedItem() == null) {
          return;
        }
                
        InputHandler.dispatch(DARSEvent.inSetNodeNoRouteTimeout(
            nodeSelectorComboBox.getSelectedItem().toString(),
            dontExpireRoutesCheckBox.isSelected()));
      }
    });
    
    overrideHopsCheckBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        if (blockChangeEvents) {
          return;
        }
        if (nodeSelectorComboBox.getSelectedItem() == null) {
          return;
        }

        InputHandler.dispatch(DARSEvent.inSetNodeOverrideHops(
            nodeSelectorComboBox.getSelectedItem().toString(),
            overrideHopsCheckBox.isSelected(),
            (Integer) overrideHopsSpinner.getValue()));
      }
    });

    overrideHopsSpinner.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent arg0) {
        if (blockChangeEvents) {
          return;
        }
        if (nodeSelectorComboBox.getSelectedItem() == null) {
          return;
        }

        InputHandler.dispatch(DARSEvent.inSetNodeOverrideHops(
            nodeSelectorComboBox.getSelectedItem().toString(),
            overrideHopsCheckBox.isSelected(),
            (Integer) overrideHopsSpinner.getValue()));

      }
    });

    changeNarrMessageCheckBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        if (blockChangeEvents) {
          return;
        }
        if (nodeSelectorComboBox.getSelectedItem() == null) {
          return;
        }

        InputHandler.dispatch(DARSEvent.inSetNodeChangeMessages(
            nodeSelectorComboBox.getSelectedItem().toString(),
            changeNarrMessageCheckBox.isSelected()));
      }
    });
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
    JDialog jd = openNodeDialogs.get(nodeId);
    if (jd == null) {
      return;
    }
    openNodeDialogs.remove(nodeId);
    jd.dispose();
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
      // update the component
      setAttributes(ni);
    }
  }

  private NodeAttributes getAttributes(String id) {
    // Use the node inspector interface to view the properties of the node
    return nodeInspector.getNodeAttributes(id);
  }

  private void setAttributes(NodeAttributes n) {
    if (n == null) {
      return;
    }
    blockChangeEvents = true;

    nodeSelectorComboBox.setSelectedItem(n.id);
    XSpinner.setValue(n.x);
    YSpinner.setValue(n.y);
    nodeRangeSpinner.setValue(n.range);
    promiscuousModeCheckBox.setSelected(n.isPromiscuous);
    dropMessagesCheckBox.setSelected(n.isDroppingMessages);
    overrideHopsCheckBox.setSelected(n.isOverridingHops);
    overrideHopsSpinner.setValue(n.hops);
    changeNarrMessageCheckBox.setSelected(n.isChangingMessages);
    replayMessagesCheckBox.setSelected(n.isReplayingMessages);
    dontExpireRoutesCheckBox.setSelected(n.isNotExpiringRoutes);

    // Cludge alert. Reset the settings based on locked replay mode
    nodeRangeSpinner.setEnabled(!lockedReplayMode);
    promiscuousModeCheckBox.setEnabled(!lockedReplayMode);
    changeNarrMessageCheckBox.setEnabled(!lockedReplayMode);
    dropMessagesCheckBox.setEnabled(!lockedReplayMode);
    dontExpireRoutesCheckBox.setEnabled(!lockedReplayMode);
    replayMessagesCheckBox.setEnabled(!lockedReplayMode);
    XSpinner.setEnabled(!lockedReplayMode);
    YSpinner.setEnabled(!lockedReplayMode);
    
    overrideHopsCheckBox.setEnabled(!lockedReplayMode);
    overrideHopsSpinner.setEnabled(!lockedReplayMode);
    blockChangeEvents = false;
  }

  public void setNodeInspector(NodeInspector ni) {
    this.nodeInspector = ni;
  }

  public Vector<String> getNodeList() {
    return nodeList;
  }

  public void clear() {
    blockChangeEvents = true;
    nodeSelectorComboBox.removeAllItems();
    XSpinner.setValue(0);
    YSpinner.setValue(0);
    nodeList.clear();
    promiscuousModeCheckBox.setSelected(false);
    dropMessagesCheckBox.setSelected(false);
    nodeRangeSpinner.setValue(0);
    overrideHopsCheckBox.setSelected(false);
    overrideHopsSpinner.setValue(1);
    changeNarrMessageCheckBox.setSelected(false);
    dontExpireRoutesCheckBox.setSelected(false);
    replayMessagesCheckBox.setSelected(false);
    
    String nodeId;
    JDialog dialog;
    Iterator<String> iter = openNodeDialogs.keySet().iterator();
    while (iter.hasNext()) {
      nodeId = iter.next();
      dialog = openNodeDialogs.get(nodeId);
      if (dialog.isVisible() == true) {
        dialog.setVisible(false);
      }
    }
    openNodeDialogs.clear();

    blockChangeEvents = false;
  }

  public void setNodeById(String id) {
    setAttributes(getAttributes(id));
  }

  boolean isLocked = false;

  public void setLock(boolean isLocked) {
    // lock every field
    this.isLocked = isLocked;
    nodeSelectorComboBox.setEnabled(!isLocked);
    XSpinner.setEnabled(!isLocked);
    YSpinner.setEnabled(!isLocked);
    nodeRangeSpinner.setEnabled(!isLocked);
    promiscuousModeCheckBox.setEnabled(!isLocked);
    dropMessagesCheckBox.setEnabled(!isLocked);
    changeNarrMessageCheckBox.setEnabled(!isLocked);
    nodeAttributesButton.setEnabled(!isLocked);
    overrideHopsCheckBox.setEnabled(!isLocked);
    overrideHopsSpinner.setEnabled(!isLocked);
    promiscuousModeCheckBox.setEnabled(!isLocked);
    dontExpireRoutesCheckBox.setEnabled(!isLocked);
    replayMessagesCheckBox.setEnabled(!isLocked);
  }

  public void simPaused() {

  }

  public void simStopped() {
    clear();
    setLock(true);
  }

  public void openNodeDialog(String nodeID) {
    // If the node attributes window is already open return
    if (openNodeDialogs.containsKey(nodeID)) {
      return;
    }
    JDialog dialog = nodeInspector.getNodeDialog(nodeID);
    dialog.setVisible(true);
    openNodeDialogs.put(nodeID, dialog);
  }

  public void updateNodeDialogs() {
    String nodeId;
    JDialog dialog;
    Iterator<String> iter = openNodeDialogs.keySet().iterator();
    while (iter.hasNext()) {
      nodeId = iter.next();
      dialog = openNodeDialogs.get(nodeId);
      if (dialog.isVisible() == false) {
        iter.remove();
        continue;
      }
      nodeInspector.updateNodeDialog(nodeId, dialog);
    }
  }

  private NodeInspector nodeInspector;

  private boolean       lockedReplayMode = false;

  public void setLockedReplayMode(boolean b) {
    // lock/unlock all modifiers
    XSpinner.setEnabled(!b);
    YSpinner.setEnabled(!b);
    nodeRangeSpinner.setEnabled(!b);
    promiscuousModeCheckBox.setEnabled(!b);
    dropMessagesCheckBox.setEnabled(!b);
    changeNarrMessageCheckBox.setEnabled(!b);
    overrideHopsCheckBox.setEnabled(!b);
    overrideHopsSpinner.setEnabled(!b);
    dontExpireRoutesCheckBox.setEnabled(!b);
    replayMessagesCheckBox.setEnabled(!b);
    lockedReplayMode = b;
  }

  @Override
  public JSpinner getXSpinner() {
    return XSpinner;
  }

  @Override
  public JSpinner getYSpinner() {
    return YSpinner;
  }

  @Override
  public JSpinner getRangeSpinner() {
    return nodeRangeSpinner;
  }

  @Override
  public JButton getNodeAttributesButton() {
    return nodeAttributesButton;
  }

  @Override
  public JCheckBox getPromiscuityCheckBox() {
    return promiscuousModeCheckBox;
  }

  @Override
  public JCheckBox getDropMessagesCheckBox() {
    return dropMessagesCheckBox;
  }

  @Override
  public JComboBox<String> getNodeComboBox() {
    return nodeSelectorComboBox;
  }
  @Override
  public JCheckBox getChangeNarrMessageCheckBox() {
    return changeNarrMessageCheckBox;
  }
  
  @Override
  public JCheckBox getReplayMessageCheckBox() {
    return replayMessagesCheckBox;
  }
  
  @Override
  public JCheckBox getDontExpireRoutesCheckBox() {
    return dontExpireRoutesCheckBox;
  }
  
  @Override
  public void nodeSetMalicious(GNode node) {
  }

  @Override
  public JPanel getOverrideHopsJPanel() {
    return overrideHopsPanel;
  }
  
  @Override
  public JCheckBox getOverrideHopsCheckBox() {
    return overrideHopsCheckBox;
  }
  
  @Override
  public JSpinner getOverrideHopsSpinner() {
    return overrideHopsSpinner;
  }
  

}
