/*Author Jagriti
*/
package dars.gui;


import javax.swing.JDialog; 
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import dars.event.DARSEvent;
import dars.proto.aodv.*;


public class GetNodeDialog extends JDialog implements ActionListener
{
  private static final long serialVersionUID = 1L;

  private JLabel sourceNodeLabel = new JLabel("Source Node:");
  private JLabel currentQuantum = new JLabel("Clock Ticks");
  private JLabel sourceLabel;
  private JTextArea nodeMessage;
  private String sourceNode;
   private BorderLayout Layout = new BorderLayout();
   
  
}
