package etf.dotsandboxes.rs160333d;

public class Edge {
	
	private int x, y;
	private boolean isHorizontal;
	
	Edge(){
		x = y = -1;
		isHorizontal = false;
	}
	
	Edge(int x, int y, boolean isHorizontal){
		this.x = x;
		this.y = y;
		this.isHorizontal = isHorizontal;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public boolean getIsHorizontal() {
		return isHorizontal;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public void setIsHorizontal(boolean isHorizontal) {
		this.isHorizontal = isHorizontal;
	}
	
}
