/**
 * 
 */
package dars.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * @author Mike
 *
 */
public class LogArea extends javax.swing.JPanel {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public void appendLog(String log) {
    textArea.append(log);
  }
  

  private JScrollPane jsp;
  private JTextArea textArea = new JTextArea();
  
  public LogArea() {
    //Setup the text area.
    configureTextArea(textArea);  
    
    //Pack the text area into a scroll pane
    jsp = new JScrollPane(textArea);
    jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
   
    setLayout(new BorderLayout());
    
    add(jsp, BorderLayout.CENTER);
    
  }
  

  private void configureTextArea(JTextArea ta) {
    //not editable
    ta.setEditable(false);
    


    
  
  }
  
  //Add a text area
  
}
