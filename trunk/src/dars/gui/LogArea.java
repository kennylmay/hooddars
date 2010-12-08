/**
 * 
 */
package dars.gui;

import java.awt.BorderLayout;
import java.math.BigInteger;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

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
  public void appendLog(String logType, String log, long quantum) {
    
    //Construct the log line
    String boldPart = Utilities.timeStamp() + " Q" + quantum + " : " + logType + " : ";
    String normalPart = log + newline;

    Document doc = textArea.getDocument();
    try {
      doc.insertString(doc.getLength(), boldPart, boldAttribute);
      doc.insertString(doc.getLength(), normalPart, normalAttribute);
    } catch (BadLocationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    //Move the caret to chase the log as it grows downward
    textArea.setCaretPosition(textArea.getDocument().getLength());
    
    //Maintain the buffer
    clampBuffer();
    
  }
  
  void clampBuffer()
  {
     Document doc = textArea.getDocument();
     //If the document is > buf size, trunc it down to 50 percent of buf size.
     int overLength = doc.getLength() - Defaults.LOG_AREA_BUF_SIZE;

     if (overLength > 0)
     {
       
        try {
          //Chomp off from 0 to the end of the line where offset LOG_AREA_BUF_SIZE/2 falls.
          String chompLine = doc.getText(Defaults.LOG_AREA_BUF_SIZE/2, 150);
          int chompStop = Defaults.LOG_AREA_BUF_SIZE/2 + chompLine.indexOf(newline) + 1;
          
          doc.remove(0, chompStop + 1);
        } catch (BadLocationException e) {
          Utilities.showError("An error occurred while truncating the buffer in the LogArea. Please file a bug report.");
          System.exit(1);
       }
     }
  }


  private JScrollPane jsp;
  private JTextPane textArea = new JTextPane();
  
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
  

  private SimpleAttributeSet boldAttribute = new SimpleAttributeSet();
  private SimpleAttributeSet normalAttribute = new SimpleAttributeSet();
  private void configureTextArea(JEditorPane ta) {
    StyleConstants.setBold(boldAttribute, true);
    StyleConstants.setFontFamily(boldAttribute, Defaults.FONT.getFamily());
    StyleConstants.setFontFamily(normalAttribute, Defaults.FONT.getFamily());
    
    //not editable
    ta.setEditable(false); 
    
  
  }
  
  public void clear(){
    textArea.setText("");
  }

  
  //Add a text area
  
}
