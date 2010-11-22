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
import java.io.InputStreamReader;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class HelpWindow extends JDialog {
  private static final long serialVersionUID = 1L;

  // Creating the border layout
  private BorderLayout      Layout           = new BorderLayout();
  private JPanel            Panel            = new JPanel(Layout);
  private JScrollPane       scroller;
  private JTextArea         text             = new JTextArea();

  public HelpWindow() {

    // Attempt to open the README File
    File readmeFile = new File("README.txt");
    BufferedReader buffIn = null;
    String buffer = "";

    // Attempt to create a buffered reader with the tar file
    try {
      buffIn = new BufferedReader(new InputStreamReader(new FileInputStream(
          readmeFile)));
    } catch (FileNotFoundException e1) {
      JOptionPane.showMessageDialog(null,
          "The README file could not be opened.");
    }

    // Continue to read the file until the end is reached.
    while (buffer != null) {
      try {
        buffer = buffIn.readLine();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      if (buffer != null){
        text.append(buffer + "\n");
      }
    }
    
    this.setPreferredSize(new Dimension(800, 600));
    getContentPane().add(Panel);
    
    // Add the text area to the scroller
    scroller = new JScrollPane(text);
    Panel.add(scroller, BorderLayout.CENTER);
        
    // Set the default dimension of the node attributes window
    this.pack();
    this.setModal(true);
    this.setAlwaysOnTop(true);
    this.setVisible(true);
  }
}