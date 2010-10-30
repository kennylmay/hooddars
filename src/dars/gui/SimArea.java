package dars.gui;

import javax.swing.*;
import java.awt.event.*;
import java.util.*;



public class SimArea extends JLayeredPane {

/**
   * 
   */
  private static final long serialVersionUID = 1L;


///////////////////////////////Constructor
  public SimArea() {
    setLayout(null);

    addMouseListener(new PopClickListener());
   
    setVisible(true);
 
    connMap = new Connections(this);

    addNewNodeReq(100,100);        
    addNewNodeReq(300,300);       
    addNewNode(100,400,"AA");
    Random r = new Random();
    for(int i=1; i<20; i++){
      addNewNodeReq( r.nextInt(400), r.nextInt(400));
    }
  }

class NodeActionHandler implements GNodeListener{

  public void nodeMoved(GNode n, int x, int y) {
    //issue a move node request
    moveNodeReq(n.getId(), x, y);
    
    //propagate the signal to other listeners
    for(GNodeListener g : nodeListeners){
      g.nodeMoved(n,x,y);
    }
  }
  

  public void nodeEntered(GNode n) {
    
    //propagate the signal to other listeners
    for(GNodeListener g : nodeListeners){
      g.nodeEntered(n);
    }
  }
  
  public void nodeExited(GNode n) {
    
    //propagate the signal to other listeners
    for(GNodeListener g : nodeListeners){
      g.nodeExited(n);
    }
  }
  
  public void nodeSelected(GNode n) {
    
    //propagate the signal to other listeners
    for(GNodeListener g : nodeListeners){
      g.nodeSelected(n);
    }
  }
  
  public void nodePopupEvent(GNode n,int x,int y){
    EditNodePopup edit_menu = new EditNodePopup();
    edit_menu.gnode = n;
    edit_menu.show(n, x, y);
    
    //propagate the signal to other listeners
    for(GNodeListener g : nodeListeners){
      g.nodePopupEvent(n,x,y);
    }

  }
  
  
}

  public String getSelectedNodeID() {
	if(GNode.SelectedNode != null) {
		return GNode.SelectedNode.getId();
	}
	else return null;
  }
  
  public boolean setSelectedNode(String id) {
    //Get the node by id
	 GNode n = getGNode(id);
	 
	 //If n is null, node not found. return false.
	 if(n==null) {
		 return false;
	 }
	 
	 //Otherwise, select the node, return true.
	 n.select();
	 return true;
  }
  
  private ArrayList<GNodeListener> nodeListeners = new ArrayList<GNodeListener>();
  public void addNodeListener(GNodeListener gl) {
	  nodeListeners.add(gl);
  }
  
  public void removeNodeSelectedListener(GNodeListener gl) {
	  nodeListeners.remove(gl);
  }
  



  private GNode getGNode(String id) {
    //return a null reference if we don't find it
    if( ! gnodemap.containsKey(id)) {
      return null;
    }

    return gnodemap.get(id);
  }


  //This function will send a request for a new node to the input handler eventually.
  private void addNewNodeReq(int x, int y) {
    addNewNode(x,y, Character.toString(c++));
  } 

  //This function will send a request to del a node to the input handler eventually.
  private void deleteNodeReq(String id) {
    deleteNode(id);
  }
  
  

  //This function will send a request to move a node to the input handler eventually.
  private void moveNodeReq(String id, int x, int y) {
    moveNode(id,x,y);
    //connMap.repaint();
  }


  public void moveNode(String id, int x, int y) {
    //Get the gnode from the map
    GNode gnode = getGNode(id);
    
    //If it doesn't exist, theres a problem
    assert(gnode != null);
 
    //move the x y coords
     gnode.setXY(x,y); 
  }

  public void deleteNode(String id) {
    //Get the gnode
    GNode gnode = getGNode(id);
    
    //If it doesn't exist, there's a problem
    assert(gnode != null);
   
    //Remove it from the layeredPanel
    this.remove(this.getIndexOf(gnode));
   
    //remove it from the map
    gnodemap.remove(id);
   
    //cleanup the gnode itself
    gnode.cleanup();
    
    gnode = null;
    this.invalidate();
    this.repaint();
  }

  //This function adds a node to the GUI. It's assumed that the node now exists in the simulator.  
  public void addNewNode(int x, int y, String id) {
    //instantiate a new GNode
    GNode node = new  GNode(id,this,x ,y );
    
    //add it to the gnode map
    gnodemap.put(id, node);
    
    //add it to the canvas
    this.add(node, JLayeredPane.PALETTE_LAYER);
    
    //add our node listener 
    node.addListener(new NodeActionHandler());
 
   

  }


//////////////////////////Data



  private TreeMap<String, GNode> gnodemap = new TreeMap<String, GNode>();

  private Connections connMap;

  //Testing purposes only
  private char c ='A';

  
  //Inner classes

  //Pop up menu for adding nodes
  class AddNodePopup extends JPopupMenu implements ActionListener {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    JMenuItem anItem1;
    JMenuItem anItem2;
    int x, y;
    public AddNodePopup(){
      anItem1 = new JMenuItem("Add a new node");
      anItem2 = new JMenuItem("Something else");
      anItem1.addActionListener(this);
      add(anItem1);
      add(anItem2);
    }

    public void actionPerformed(ActionEvent e) {
      addNewNodeReq(this.x,this.y);
    }
  }

  //Pop up menu for editing/deleting nodes
  class EditNodePopup extends JPopupMenu {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    JMenuItem edit_item;
    JMenuItem delete_item;
    GNode gnode;
    public EditNodePopup(){
      edit_item = new JMenuItem("Edit node");
      edit_item.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              //edit node action goes here
           }      
      });
    
      delete_item = new JMenuItem("Delete node");
      delete_item.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              deleteNodeReq(gnode.getId());
           }      
      });
      add(edit_item);
      add(delete_item);
    }
 
   
  }

  //Listener for spawning new pop menus
  class PopClickListener extends MouseAdapter {
    @Override
    public void mousePressed(MouseEvent e){
      if (e.isPopupTrigger()) {
        doPop(e);
      }
    }
    //override
    @Override
    public void mouseReleased(MouseEvent e){
      if (e.isPopupTrigger()) {
        doPop(e);
      }
    }

    private void doPop(MouseEvent e){
      //Show the "Add Node" menu.
      AddNodePopup menu = new AddNodePopup();
      menu.x = e.getX();
      menu.y = e.getY();
      menu.show(e.getComponent(), e.getX(), e.getY());
    }
  }

  

 
} 
