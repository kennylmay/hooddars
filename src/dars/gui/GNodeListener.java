package dars.gui;


public interface GNodeListener {
	public void nodeEntered(GNode node);
	public void nodeExited(GNode node);
	public void nodeMoved(GNode node, int new_x, int new_y);
	public void nodePopupEvent(GNode node, int x, int y);
	public void nodeSelected(GNode node);
	public void nodeSetMalicious(GNode node);
} 
