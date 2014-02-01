/*Author kennylmay
 */
package dars.gui;

import javax.swing.JDialog;
import java.io.IOException;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

public class HTMLWindow extends JDialog {
  private static final long serialVersionUID = 1L;
  public HTMLWindow(){
    JEditorPane jep = new JEditorPane();
    jep.setEditable(false);   
    
    try {
      jep.setPage("http://dars.sourceforge.net/");
    }
    catch (IOException e) {
      jep.setContentType("text/html");
      jep.setText("<html>Error: Could not load help website!</html>");
    } 
     
    JScrollPane scrollPane = new JScrollPane(jep);     
    JFrame f = new JFrame("Getting Started");
 
    f.getContentPane().add(scrollPane);
    f.setSize(1024, 768);
    f.setVisible(true);
  }
}