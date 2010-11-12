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
import javax.swing.JTextField;
import javax.swing.border.Border;
import dars.event.DARSEvent;
import dars.proto.*;
import java.awt.Component;


public class GetNodeDialog extends JDialog
{
  private static final long serialVersionUID = 1L;
  private JButton closebutton = new JButton("Close");
  private JLabel sourceNodeLabel = new JLabel("Source Node:");
  private JLabel currentNodeTick = new JLabel("Clock Ticks");
  private JLabel nodeInfo = new JLabel("Node Information:");
  private JTextArea nodeMessage = new JTextArea();
  private JLabel sourceLabel;
  private String sourceNode;
  private JTextField TimeLabel;
  private int timeofNode;
  // Creating the border layout
  
  private BorderLayout Layout = new BorderLayout();
  private GridLayout nodeInfoLayout = new GridLayout(2,2);
  private BorderLayout buttonLayout = new BorderLayout();
  private BorderLayout NodemessageLayout = new BorderLayout();
  private BorderLayout nodeTimeLayout = new BorderLayout();
    // creating panels
    private JPanel Panel = new JPanel(Layout);
    private JPanel nodeInfoPanel = new JPanel(nodeInfoLayout);
    private JPanel buttonPanel = new JPanel(buttonLayout);
    private JPanel nodeTimePanel = new JPanel(nodeTimeLayout);
    private JPanel messagePanel = new JPanel(NodemessageLayout);
  
    public GetNodeDialog(JFrame frame, String SourceId, int timeTick)
    {
      
    sourceNode = SourceId;
    sourceLabel = new JLabel(SourceId);
    nodeMessage.setRows(20);
    nodeMessage.setColumns(30);
    nodeMessage.setLineWrap(true);
    nodeMessage.setAutoscrolls(true);
    nodeMessage.setText("");
    timeofNode = timeTick;
    TimeLabel= new JTextField(timeTick);
    
    
    //Adding to the Grid
    
    nodeInfoPanel.add(sourceNodeLabel);
    nodeInfoPanel.add(sourceLabel);
    nodeTimePanel.add(TimeLabel);
    nodeTimePanel.add(currentNodeTick);

    //Creating borders
    
    Border raisedBevel, loweredBevel, compound;
    raisedBevel = BorderFactory.createRaisedBevelBorder();
    loweredBevel = BorderFactory.createLoweredBevelBorder();
    compound = BorderFactory.createCompoundBorder(raisedBevel, loweredBevel);
  
 // Give the border to the message box component
    nodeMessage.setBorder(compound);
         
    // Add the component to the message Panel
    messagePanel.add(nodeInfo, BorderLayout.NORTH);
    messagePanel.add(nodeMessage, BorderLayout.CENTER);
    
    // Add the buttons to the buttons panel
    buttonPanel.add(closebutton, BorderLayout.CENTER); 
    
    //Add component to the node time information panel
    nodeTimePanel.add(TimeLabel, BorderLayout.NORTH);
    nodeTimePanel.add(currentNodeTick, BorderLayout.SOUTH);
   
    
    // Add all the individual panels to the main panel
    Panel.add(nodeInfoPanel, BorderLayout.NORTH);
    Panel.add(messagePanel,BorderLayout.CENTER); 
    Panel.add(buttonPanel, BorderLayout.SOUTH);
    
    // Add action listeners to the buttons
   // closebutton.addActionListener(this);

      
    // Display the Panel
    pack();
    setLocationRelativeTo(frame);
    setVisible(true);
}

}

