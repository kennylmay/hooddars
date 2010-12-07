/**
 * 
 */
package dars.gui;

import java.awt.BorderLayout;
import java.math.BigInteger;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import dars.Defaults;
import dars.Utilities;

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
  static private String topLine = null;
  public void appendLog(String log, long quantum) {
    
    //Construct the log line
    String line = ("T:" + Utilities.timeStamp() + " Q" + quantum + " : " + log + newline);

    //Maintain the buffer
    clampBuffer(line.length());
    
    //Append
    textArea.append(line);
    
    //Move the caret to chase the log as it grows downward
    textArea.setCaretPosition(textArea.getDocument().getLength());

    
  }
  
  void clampBuffer(int incomingDataSize)
  {
     Document doc = textArea.getDocument();
     //If the document is > buf size, trunc it down to 50 percent of buf size.
     int overLength = doc.getLength() + incomingDataSize - Defaults.LOG_AREA_BUF_SIZE;

     if (overLength > 0)
     {
        try {
          //Chomp off from 0 to the end of the line where offset LOG_AREA_BUF_SIZE/2 falls.
          doc.remove(0, textArea.getLineEndOffset(textArea.getLineOfOffset(Defaults.LOG_AREA_BUF_SIZE/2)) );
        } catch (BadLocationException e) {
          Utilities.showError("An error occurred while truncating the buffer in the LogArea. Please file a bug report.");
          System.exit(1);
        }
     }
  }


  private JScrollPane jsp;
  private JTextArea textArea = new JTextArea();
  
  public LogArea() {
    
    setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
        "Console", 
        TitledBorder.CENTER, TitledBorder.TOP, Defaults.BOLDFACED_FONT) );
    
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

  
  //Add a text area
  
}
