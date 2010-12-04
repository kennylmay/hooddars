/*Author kennylmay
 */
package dars.gui;

import javax.swing.JDialog;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

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