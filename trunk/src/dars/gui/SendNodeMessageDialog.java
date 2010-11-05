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
  public static void inSendMsg(Message m, String sourceId, String destinationId)
  {
    }
  
   }

class Dialogbox1 extends JFrame implements ActionListener{
  /**
     * 
     */
    private static final long serialVersionUID = 1L;
  public static final int WIDTH = 600;
  public static final int Height =400;
  public static final int Lines =30;
  public static final int CHAR_PER_LINE = 20;
  public static String sourceId;
  public static Message m;
  public static String destinationId;
 // private String button22 = "Enter the Message";

  private JTextArea theText;
  private int String;
   public Dialogbox1()
   { setSize(WIDTH , HEIGHT );
   setTitle("MEssage Box");
   JFrame frame = new JFrame();
   frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   Container contentPane = getContentPane();
   contentPane.setLayout(new BorderLayout());
   JPanel buttonpanel = new JPanel();
   buttonpanel.setBackground(Color.WHITE);
   buttonpanel.setLayout(new FlowLayout());
   JButton button1 = new JButton("OK");
   button1.addActionListener(this);
   buttonpanel.add(button1);
    JButton button2 = new JButton("Cancel");
    button2.addActionListener(this);
    buttonpanel.add(button2);
   contentPane.add(buttonpanel, BorderLayout.SOUTH);
   JPanel textpanel = new JPanel();
   // JLabel textpane = new JLabel("Enter the Message");
   // textpane.add(textpane,BorderLayout.WEST);
   textpanel.setBackground(Color.gray);
   theText = new JTextArea(Lines, CHAR_PER_LINE);
   theText.setBackground(Color.WHITE);
   textpanel.add(theText);
   contentPane.add(textpanel,BorderLayout.CENTER);
   }
   
  public void actionPerformed(ActionEvent e){
    String actionCommand = e.getActionCommand();
    if(actionCommand.equals("Ok"))
      InputHandler.dispatch(DARSEvent.inSendMsg(m, sourceId, destinationId));
    else if (actionCommand.equals("Cancel"));
       
         
    }
  public void ComboBoxDemo() {
    
    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());

    

    //Create the combo box, select the nodeId.
    
    JComboBox nodeList = new JComboBox();
    Node node = null;
    nodeList.setSelectedItem(node.getAttributes().id);
    nodeList.addActionListener(this);


  
  }
  public void actionPerformed1(ActionEvent e) {
    JComboBox cb = (JComboBox)e.getSource();
    String nodeList = (String)cb.getSelectedItem();
   


}

}
  