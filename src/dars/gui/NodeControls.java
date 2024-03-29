package dars.gui;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JSpinner;

public interface NodeControls {
  public JSpinner  getXSpinner();
  public JSpinner  getYSpinner();
  public JSpinner  getRangeSpinner();
  public JButton   getNodeAttributesButton();
  public JCheckBox getPromiscuityCheckBox();
  public JComboBox getNodeComboBox();
}
