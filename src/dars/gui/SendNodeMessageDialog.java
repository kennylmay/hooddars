/**
 * 
 */
package dars.gui;

import java.util.ArrayList;
import javax.swing.*;

import dars.InputHandler;
import dars.Message;
import dars.event.DARSEvent;
import dars.proto.Node;

import java.awt.*;
import java.awt.event.*;
/**
 * @author Jagriti
 *
 */
public class SendNodeMessageDialog {

 
  static boolean run(String fromNodeId, ArrayList<String> nodeIdList) {
    
    //Build a dialog
    
    //Filter out fromId from the dialog
    
    //run the dialog
    
    //dispatch a sendNodeMsg if OK, return true
    
    //else return false
    
    //placeholder
    return true;
  
  
                     



}
 
   }

 
  
  class box   
  {   
      private String sourceId;
      private Message m;
      private String destinationId;
      public box()   
      {     
      
          JFrame frame = new JFrame("Send Message to a Node");
          frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
          
          JTextArea area = new JTextArea("",5,6);
          
          
          frame.setSize(400,300);   
          JPanel northP = new JPanel();   
          JPanel southP = new JPanel(); 
          JPanel centerP = new JPanel();
          Dimension panelD = new Dimension(50,30);
          JButton button1 = new JButton("OK");
          JButton button2 = new JButton("Cancel");
          panelD.setSize(60,40);

          northP.setPreferredSize(panelD);   
          northP.setMaximumSize(panelD);   
          southP.setPreferredSize(panelD);   
          southP.setMaximumSize(panelD);   
          centerP.setPreferredSize(panelD);   
          centerP.setMaximumSize(panelD); 
          JTextArea textArea = new JTextArea("",20, 30);
          JLabel label2 = new JLabel("Enter your Message Here : ");

          centerP.add(label2);
          southP.add(button1); 
          southP.add(button2);
          centerP.add(textArea); 
          
          
          
          Container c = frame.getContentPane();   
          c.add(northP,"North");   
          c.add(southP,"South");  
          c.add(centerP,"Center");
          
          frame.show();   
       
                  
      }   
  public void actionPerformed(ActionEvent e){
        String actionCommand = e.getActionCommand();
  if(actionCommand.equals("Ok"))
     
   InputHandler.dispatch(DARSEvent.inSendMsg(m,sourceId ,destinationId ));
   else if (actionCommand.equals("Cancel"));
       
         
    }
  }
  