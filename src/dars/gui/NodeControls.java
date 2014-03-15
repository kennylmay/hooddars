package dars.gui;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;

public interface NodeControls {
  public JSpinner  getXSpinner();
  public JSpinner  getYSpinner();
  public JSpinner  getRangeSpinner();
  public JButton   getNodeAttributesButton();
  public JCheckBox getPromiscuityCheckBox();
  public JComboBox<?> getNodeComboBox();
  public JCheckBox getDropMessagesCheckBox();
  public JPanel    getOverrideHopsJPanel();
  public JCheckBox getChangeNarrMessageCheckBox();
  public JCheckBox getReplayMessageCheckBox();
  public JCheckBox getDontExpireRoutesCheckBox();
  public JCheckBox getOverrideHopsCheckBox();
  public JSpinner getOverrideHopsSpinner();
} 