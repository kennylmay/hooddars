package dars.gui;
import java.awt.image.*;
import java.awt.Font;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageFactory {
  static private BufferedImage nodeImg_;
  static private BufferedImage hoveredNodeImg_;
  static private BufferedImage selectedNodeImg_;
  static private BufferedImage ghostedNodeImg_;
  
  //set the static instance of the node image. If it's already set, return a ref to it.
  static public BufferedImage getNodeImg() {
    //return the instance
    if(nodeImg_ != null) {
      return nodeImg_;
    }
    
    //otherwise initialize all images
    init();
    return nodeImg_;
  }

  static public BufferedImage getHoveredNodeImg() {
    //return the instance
    if(hoveredNodeImg_ != null) {
      return hoveredNodeImg_;
    }
    
    //otherwise initialize all images
    init();
    return hoveredNodeImg_;
  }

  static public BufferedImage getSelectedNodeImg() {
    //return the instance
    if(selectedNodeImg_ != null) {
      return selectedNodeImg_;
    }
    
    //otherwise initialize all images
    init();
    return selectedNodeImg_;
  }

  static public BufferedImage getGhostedNodeImg() {
    //return the instance
    if(ghostedNodeImg_ != null) {
      return ghostedNodeImg_;
    }
    
    //otherwise initialize all images
    init();
    return ghostedNodeImg_;
  }

  static private ImageFactory instance = new ImageFactory();
  static private void init() {
    //initalize images
    try {
      nodeImg_ = ImageIO.read(instance.getClass().getResource("/node.png"));
      hoveredNodeImg_  = getHoverImg(nodeImg_);
      selectedNodeImg_ = getSelectedImg(nodeImg_);
      ghostedNodeImg_ =  getTransparentImg(nodeImg_, 0.5f);

    } catch(Exception e) {
      System.out.println("FAIL in image init");
      System.exit(1);
    }

  }



  //Transformation functions
  //Function that creates a new transparent image from a given image 
  static public BufferedImage getTransparentImg( BufferedImage src, float alpha) {
    BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(),
                                           BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = dest.createGraphics();
    int rule = AlphaComposite.SRC_OVER;
    AlphaComposite ac = AlphaComposite.getInstance(rule, alpha);
    g2.setComposite(ac);
    g2.drawImage(src, null, 0, 0);
    g2.dispose();
    return dest;
  }

  //Function that creates a hover image counterpart for a given image
  static public BufferedImage getHoverImg( BufferedImage src) {
    BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(),
                                           BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = dest.createGraphics();
    g.drawImage(src,null,0,0);
    RescaleOp rescaleOp = new RescaleOp(1.5f, 0.0f, null);
    rescaleOp.filter(dest,dest);
    return dest;
  }

  //Function that creates a selected image counterpart for a given image
  static public BufferedImage getSelectedImg(BufferedImage src1) {
    BufferedImage src = getHoverImg(src1);
    BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(),
                                           BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = dest.createGraphics();
    g.setColor(new Color(0,0,255,20)); //blue + 30% transparency
    g.drawImage(src, null, 0,0);
    g.fillRect(0,0,dest.getWidth(null), dest.getHeight(null));
     
 
    g.setColor(new Color(0,0,255,255));
    for(int i=0; i<3; i++) {
      g.drawRoundRect(i, i ,dest.getWidth(null) - 2*i -1, dest.getHeight(null) - 2*i -1 ,10,10);
    }

    return dest;
  }

  
  static Font f = new Font("arial", Font.PLAIN, 12);
  //Function that draws a letter directly onto a buffered image
  static public void drawNodeID(Graphics g1, String id, Rectangle r){
    //get the center
    Graphics2D g = (Graphics2D)g1;
   
    g.drawRect(r.x,r.y,r.width,r.height);
    
    g.setColor(Color.BLACK);
   
    g.setFont(f);
    FontMetrics fm = g.getFontMetrics();
    g.drawString(id, 
                 r.x + r.width/2 - fm.stringWidth(id)/2,
                 r.y + r.height/2 + fm.getAscent() /2 - 1 );

    
  }
}
