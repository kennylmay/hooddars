/**
 * 
 */
package dars.gui;

import java.util.ArrayList;
import javax.swing.*;

import dars.InputHandler;
import dars.Message;
import dars.event.DARSEvent;
import java.awt.*;
import java.awt.event.*;
/**
 * @author Jagriti
 *
 */
public class SendNodeMessageDialog
{
  private boolean returnval = false;
  private String sourceId;
  private String m;
  private String destinationId;
  JTextArea textArea;
  JFrame frame;
  JButton button1;
  JButton button2;
  
  public boolean run(String fromNodeId, ArrayList<String> nodeIdList) 
  {
    
    //Build a dialog
    //Filter out fromId from the dialog   
    //run the dialog
    //dispatch a sendNodeMsg if OK, return true
    //else return false
    //placeholder
    
    frame = new JFrame("Send Message to a Node");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    frame.setSize(400,300);
    
    JPanel northP = new JPanel();   
    JPanel southP = new JPanel(); 
    JPanel centerP = new JPanel();
    Dimension panelD = new Dimension(50,30);
    panelD.setSize(60,40);

    northP.setPreferredSize(panelD);   
    northP.setMaximumSize(panelD);   
    southP.setPreferredSize(panelD);   
    southP.setMaximumSize(panelD);   
    centerP.setPreferredSize(panelD);   
    centerP.setMaximumSize(panelD); 
    
    textArea = new JTextArea("",20, 30);
    JLabel label2 = new JLabel("Enter your Message Here : ");

    Container c = frame.getContentPane();

    button1 = new JButton("OK");
    button2 = new JButton("Cancel");
    
    button1.addActionListener(new ActionListener() {
      
      public void actionPerformed(ActionEvent e)
      {
        m = textArea.getText();
        InputHandler.dispatch(DARSEvent.inSendMsg(m,sourceId ,destinationId ));
        returnval = true;
        frame.dispose();
      }
      });      
    
    button2.addActionListener(new ActionListener() {
      
      public void actionPerformed(ActionEvent e)
      {
        frame.dispose();
      }
      }); 
   
    centerP.add(label2);
    southP.add(button1); 
    southP.add(button2);
    centerP.add(textArea); 
    
    c.add(northP,"North");   
    c.add(southP,"South");  
    c.add(centerP,"Center");
    
    frame.show(); 
    
    return returnval;
    
  }
}
  