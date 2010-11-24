/**
 * 
 */
package dars.gui;

import java.awt.BorderLayout;
import java.math.BigInteger;

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
  public static String newline = System.getProperty("line.separator");
  
  /**
   * Debug flag to determine whether or not to show debug messages
   * in the log.
   */
  private boolean DEBUG;
  
  static private int counter = 0;
  public void appendLog(String log, BigInteger quantum) {
    if (log.contains("DEBUG") && DEBUG == false){
      return;
    }
    textArea.append("Q " + quantum + " : " + log); 
    textArea.append(newline);
    textArea.setCaretPosition(textArea.getDocument().getLength());

    //Every 1000 row inserts, truncate the visible log
    if(++counter % 1000 == 0) {
        textArea.setText("");
       //textArea.replaceRange("",0, textArea.getDocument().getLength());
      }
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
  
  public void clear(){
    textArea.setText("");
  }


  /**
   * Set whether to display debug messages.
   * @param dEBUG the dEBUG to set
   */
  public void setDEBUG(boolean dEBUG) {
    DEBUG = dEBUG;
  }


  /**
   * Return whether or not DEBUG is set
   * @return the dEBUG
   */
  public boolean isDEBUG() {
    return DEBUG;
  }
  
  //Add a text area
  
}
