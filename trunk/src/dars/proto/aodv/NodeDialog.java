/*Author Jagriti
 */
package dars.proto.aodv;

import javax.swing.JDialog;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import java.util.HashMap;
import java.util.Iterator;

public class NodeDialog extends JDialog {
  private static final long serialVersionUID      = 1L;
  private JLabel            sourceNodeLabel       = new JLabel("Source Node:");
  private JLabel            currentNodeTick       = new JLabel("Clock Ticks:");
  private JLabel            nodeInfo              = new JLabel(
                                                      "Route Table:");
  private JTextArea         nodeRouteTable        = new JTextArea();
  private JLabel            sourceLabel;
  private JLabel           TimeLabel;

  // Creating the border layout

  private BorderLayout      Layout                = new BorderLayout();
  private GridLayout        nodeInfoLayout        = new GridLayout(3, 2);
  private BorderLayout      nodeRouteTableLayout  = new BorderLayout();
  private BorderLayout      nodeTimeLayout        = new BorderLayout();
  private BorderLayout      nodeSouceLayout       = new BorderLayout();

  // creating panels
  private JPanel            Panel                 = new JPanel(Layout);
  private JPanel            nodeInfoPanel         = new JPanel(nodeInfoLayout);
  private JPanel            nodeSourcePanel       = new JPanel(nodeSouceLayout);
  private JPanel            nodeTimePanel         = new JPanel(nodeTimeLayout);
  private JPanel            routePanel            = new JPanel(
                                                      nodeRouteTableLayout);

  public NodeDialog(JFrame frame, String SourceId, int timeTick, HashMap<String, RouteEntry> routeTable) {
    super(frame, true);

    sourceLabel = new JLabel(SourceId);

    // Set the route entry box options
    nodeRouteTable.setRows(20);
    nodeRouteTable.setColumns(40);
    nodeRouteTable.setLineWrap(true);
    nodeRouteTable.setAutoscrolls(true);
    nodeRouteTable.setEditable(false);

    // Set the time tick label options
    String timeTickString = ""+timeTick;
    TimeLabel = new JLabel(timeTickString);
   
    getContentPane().add(Panel);

    // Creating borders
    Border raisedBevel, loweredBevel, compound;
    raisedBevel = BorderFactory.createRaisedBevelBorder();
    loweredBevel = BorderFactory.createLoweredBevelBorder();
    compound = BorderFactory.createCompoundBorder(raisedBevel, loweredBevel);

    // Give the border to the message box component
    nodeRouteTable.setBorder(compound);

    // Add the component to the message Panel
    routePanel.add(nodeInfo, BorderLayout.NORTH);
    routePanel.add(nodeRouteTable, BorderLayout.CENTER);

    // Add component to the node time information panel
    nodeTimePanel.add(TimeLabel, BorderLayout.EAST);
    nodeTimePanel.add(currentNodeTick, BorderLayout.WEST);

    // Add component to the node time information panel
    nodeSourcePanel.add(sourceLabel, BorderLayout.EAST);
    nodeSourcePanel.add(sourceNodeLabel, BorderLayout.WEST);

    // Adding to the Grid
    nodeInfoPanel.add(nodeSourcePanel);
    nodeInfoPanel.add(nodeTimePanel);

    // Add all the individual panels to the main panel
    Panel.add(nodeInfoPanel, BorderLayout.NORTH);
    Panel.add(routePanel, BorderLayout.CENTER);
  
    nodeRouteTable.setText(formatRouteTable(routeTable));

    // Display the Panel
    this.pack();
    this.setModal(false);
    this.setAlwaysOnTop(true);
    this.setLocationRelativeTo(frame);
  }

  void updateInformation(int currentTick, HashMap<String, RouteEntry> routeTable) {
    nodeRouteTable.setText(formatRouteTable(routeTable));
    String timeTick = "" + currentTick;
    TimeLabel.setText(timeTick);
  }

  private String formatRouteTable(HashMap<String, RouteEntry> routeTable){
    Iterator<String> iter = routeTable.keySet().iterator();
    String sourId;
    RouteEntry entry;
    String routeTableText = "DESTINATION\tHOP COUNT\tNEXT HOP\tSTATE\tSEQ #\n";

    while(iter.hasNext()){
      sourId = iter.next();
      entry = routeTable.get(sourId);
      routeTableText += entry.getDestIP()+"\t" + entry.getHopCount()  + "\t" + 
      entry.getNextHopIP()+ "\t" + entry.getState() + "\t" + entry.getSeqNum() + "\n";
    }
    return routeTableText;
  }
}
