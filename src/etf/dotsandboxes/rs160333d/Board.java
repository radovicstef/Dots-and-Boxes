package etf.dotsandboxes.rs160333d;

import java.awt.*;
import java.util.ArrayList;
import java.util.logging.Level;

public class Board implements Cloneable {
	
	final static int RED = 0;
	final static int BLUE  = 1;
	final static int BLACK = 2;
	final static int BLANK = 3;
	final static int WHITE = 4;
	
	public int[][] horizontal_edge;
	public int[][] vertical_edge;
	public int[][] box;
	
	private int length, height, red_score, blue_score;
	
	public Board(int length, int height) {
		horizontal_edge = new int [length][height+1];
		vertical_edge = new int[length+1][height];
		box = new int[length][height];
		
		fill(horizontal_edge, BLANK);
		fill(vertical_edge, BLANK);
		fill(box, BLANK);
		
		this.length = length;
		this.height = height;
		
		red_score = 0;
		blue_score = 0;
	}
	
	
	private void fill(int[][] array, int value) {
		for(int i=0; i<array.length; i++) {
			for(int j=0; j<array[i].length; j++) {
				array[i][j] = value;
			}
		}
	}
	
	public int getLength() {return length;}
	public int getHeight() {return height;}
	
	public int getRedScore() {
		return red_score;
	}
	
	public int getBlueScore() {
		return blue_score;
	}
	
	public int getScore(int color) {
		if(color == RED) return red_score;
		else return blue_score;
	}
	
	public static int switchColor(int color) {
		if(color == RED) return BLUE;
		else return RED;
	}
	
	public boolean isOver() {
		if((red_score + blue_score) == length*height) {
			return true;
		}
		else {
			return false;
		}
	}
	
	
	public int getWinner() {
		if(blue_score > red_score) return BLUE;
		else if(red_score > blue_score) return RED;
		else return BLANK;
	}
	
	
	public ArrayList<Edge> getPossibleMoves(){
		ArrayList<Edge> moves = new ArrayList<Edge>();
		for(int i=0; i<length; i++) {
			for(int j=0; j<(height+1); j++) {
				if(horizontal_edge[i][j] == BLANK)
					moves.add(new Edge(i, j, true));
			}
		}
		
		for(int i=0; i<(length+1); i++) {
			for(int j=0; j<height; j++) {
				if(vertical_edge[i][j] == BLANK)
					moves.add(new Edge(i, j, false));
			}
		}
		
		return moves;
	}
	
	
	public ArrayList<Edge> getPossibleMovesButNotThree(boolean optimized){
		//System.out.println("BIRAJ");
		ArrayList<Edge> moves = new ArrayList<Edge>();
		for(int i=0; i<length; i++) {
			for(int j=0; j<(height+1); j++) {
				boolean add = false;
				if(horizontal_edge[i][j] == BLANK) {
					add = true;
				}
				if(j<height) {
					int num = 0;
					if(vertical_edge[i][j]==BLACK) num++;
					if(vertical_edge[i+1][j]==BLACK) num++;
					if(horizontal_edge[i][j+1]==BLACK) num++;
					if(num>=2) {
						add = false;
					}
				}
				if(j>0) {
					int num = 0;
					if(vertical_edge[i][j-1]==BLACK) num++;
					if(vertical_edge[i+1][j-1]==BLACK) num++;
					if(horizontal_edge[i][j-1]==BLACK) num++;
					if(num>=2) {
						add = false;
					}
				}
				if(add) {
					moves.add(new Edge(i, j, true));
				}
			}
		}
		
		for(int i=0; i<(length+1); i++) {
			for(int j=0; j<height; j++) {
				boolean add = false;
				if(vertical_edge[i][j] == BLANK) {
					add = true;
				}
				if(i<length) {
					int num = 0;
					if(horizontal_edge[i][j]==BLACK) num++;
					if(horizontal_edge[i][j+1]==BLACK) num++;
					if(vertical_edge[i+1][j]==BLACK) num++;
					if(num>=2) {
						add = false;
					}
				}
				if(i>0) {
					int num = 0;
					if(horizontal_edge[i-1][j]==BLACK) num++;
					if(horizontal_edge[i-1][j+1]==BLACK) num++;
					if(vertical_edge[i-1][j]==BLACK) num++;
					if(num>=2) {
						add = false;
					}
				}
				if(add) {
					moves.add(new Edge(i, j, false));
				}
			}
		}
		
		if(moves.isEmpty()) {
			//System.out.println("Nema bez 3 linije");
			if(optimized) {
				System.out.println("Optimized");
				//moves.add(findingChains());
				moves = this.getPossibleMovesOptimized();
				System.out.println("Finish optimized");
			}
			else {
				moves = this.getPossibleMovesOptimized();
			}
		}
		
		return moves;
	}
	
	
	public ArrayList<Edge> getPossibleMovesOptimized() {
		ArrayList<Edge> moves = new ArrayList<Edge>();
		
		for(int i=0; i<length; i++) {
			for(int j=0; j<(height+1); j++) {
				if(horizontal_edge[i][j]==BLANK) {
					moves.add(new Edge(i, j, true));
				}
				else if(j<height) {
					if(horizontal_edge[i][j+1]==BLANK) {
						moves.add(new Edge(i, j+1, true));
					}
					else if(vertical_edge[i][j]==BLANK) {
						moves.add(new Edge(i, j, false));
					}
					else if (vertical_edge[i+1][j]==BLANK) {
						moves.add(new Edge(i+1, j, false));
					}
				}
				else if(j>0) {
					if(vertical_edge[i][j-1]==BLANK) {
						moves.add(new Edge(i, j-1, false));
					}
					else if(vertical_edge[i+1][j-1]==BLANK) {
						moves.add(new Edge(i+1, j-1, false));
					}
					else if(horizontal_edge[i][j-1]==BLANK) {
						moves.add(new Edge(i, j-1, true));
					}
				}
			}
		}
		
		return moves;
	}
	
	
	public ArrayList<Point> setHorizontalEdge(int x, int y, int color){
		horizontal_edge[x][y] = BLACK;
		ArrayList<Point> points = new ArrayList<Point>();
		if(y<height && vertical_edge[x][y]==BLACK && vertical_edge[x+1][y]==BLACK && horizontal_edge[x][y+1]==BLACK) {
			box[x][y] = color;
			points.add(new Point(x, y));
			if(color == RED) red_score++;
			else blue_score++;
		}
		if(y>0 && vertical_edge[x][y-1]==BLACK && vertical_edge[x+1][y-1]==BLACK && horizontal_edge[x][y-1]==BLACK) {
			box[x][y-1] = color;
			points.add(new Point(x, y-1));
			if(color == RED) red_score++;
			else blue_score++;
		}
		
		return points;
	}
	
	
	public ArrayList<Point> setVerticalEdge(int x, int y, int color){
		vertical_edge[x][y] = BLACK;
		ArrayList<Point> points = new ArrayList<Point>();
		if(x<length && horizontal_edge[x][y]==BLACK && horizontal_edge[x][y+1]==BLACK && vertical_edge[x+1][y]==BLACK) {
			box[x][y] = color;
			points.add(new Point(x,y));
			if(color == RED) red_score++;
			else blue_score++;
		}
		if(x>0 && horizontal_edge[x-1][y]==BLACK && horizontal_edge[x-1][y+1]==BLACK && vertical_edge[x-1][y]==BLACK) {
			box[x-1][y]=color;
			points.add(new Point(x-1, y));
			if(color == RED) red_score++;
			else blue_score++;
		}
		
		return points;
	}
	
	
	public boolean setHorizontalEdgeTemp(int x, int y, int color){
		horizontal_edge[x][y] = BLACK;
		boolean ret = false;
		if(y<height && vertical_edge[x][y]==BLACK && vertical_edge[x+1][y]==BLACK && horizontal_edge[x][y+1]==BLACK) {
			box[x][y] = color;
			System.out.println("Postavljen box " + color);
			if(color == RED) red_score++;
			else blue_score++;
			ret = true;
		}
		if(y>0 && vertical_edge[x][y-1]==BLACK && vertical_edge[x+1][y-1]==BLACK && horizontal_edge[x][y-1]==BLACK) {
			box[x][y-1] = color;
			System.out.println("Postavljen box " + color);
			if(color == RED) red_score++;
			else blue_score++;
			ret = true;
		}
		
		return ret;
	}
	
	
	public boolean setVerticalEdgeTemp(int x, int y, int color){
		vertical_edge[x][y] = BLACK;
		boolean ret = false;
		if(x<length && horizontal_edge[x][y]==BLACK && horizontal_edge[x][y+1]==BLACK && vertical_edge[x+1][y]==BLACK) {
			box[x][y] = color;
			System.out.println("Postavljen box " + color);
			if(color == RED) red_score++;
			else blue_score++;
			ret = true;
		}
		if(x>0 && horizontal_edge[x-1][y]==BLACK && horizontal_edge[x-1][y+1]==BLACK && vertical_edge[x-1][y]==BLACK) {
			box[x-1][y]=color;
			System.out.println("Postavljen box " + color);
			if(color == RED) red_score++;
			else blue_score++;
			ret = true;
		}
		
		return ret;
	}
	
	
	public void resetHorizontalEdge(int x, int y, int color){
		horizontal_edge[x][y] = BLANK;
		if(y<height && vertical_edge[x][y]==BLACK && vertical_edge[x+1][y]==BLACK && horizontal_edge[x][y+1]==BLACK) {
			box[x][y] = BLANK;
			System.out.println("Obrisan box " + color);
			if(color == RED) red_score--;
			else blue_score--;
		}
		if(y>0 && vertical_edge[x][y-1]==BLACK && vertical_edge[x+1][y-1]==BLACK && horizontal_edge[x][y-1]==BLACK) {
			box[x][y-1] = BLANK;
			System.out.println("Obrisan box " + color);
			if(color == RED) red_score--;
			else blue_score--;
		}
	}
	
	
	public void resetVerticalEdge(int x, int y, int color){
		vertical_edge[x][y] = BLANK;
		if(x<length && horizontal_edge[x][y]==BLACK && horizontal_edge[x][y+1]==BLACK && vertical_edge[x+1][y]==BLACK) {
			box[x][y] = BLANK;
			System.out.println("Obrisan box " + color);
			if(color == RED) red_score--;
			else blue_score--;
		}
		if(x>0 && horizontal_edge[x-1][y]==BLACK && horizontal_edge[x-1][y+1]==BLACK && vertical_edge[x-1][y]==BLACK) {
			box[x-1][y]=BLANK;
			System.out.println("Obrisan box " + color);
			if(color == RED) red_score--;
			else blue_score--;
		}
	}
	
	private ArrayList<Integer> edge_to_give_chain_x;
	private ArrayList<Integer> edge_to_give_chain_y;
	private ArrayList<Boolean> edge_to_give_chain_isHorizontal;
	private ArrayList<Integer> chainLength;
	
	private int[][] boxesToCheck;
	
	private int chainsize = 0;
	
	private Edge edgeToTrigger = new Edge();
	
	public int checkNumOfLongChains() {
		edge_to_give_chain_x = new ArrayList<>();
		edge_to_give_chain_y = new ArrayList<>();
		edge_to_give_chain_isHorizontal = new ArrayList<>();
		chainLength = new ArrayList<>();
		
		boxesToCheck = new int[length][height];
		
		int num_of_long_chains = 0;
		
		for(int i=0; i<length; i++) {
			for(int j=0; j<height; j++) {
				if(box[i][j] == BLANK) {
					boxesToCheck[i][j] = 1;
				}
				else {
					boxesToCheck[i][j] = 0;
				}
			}
		}
		
		//INDEX FOR BOX
		int index_x = 0;
		int index_y = 0;
		
		while(true) {
			int zeros = 0;
			loop: 
			for(int i=0; i<length; i++) {
				for(int j=0; j<height; j++) {
					if(boxesToCheck[i][j] == 1) {
						System.out.println("BOX " + i + " " + j);
						index_x = i;
						index_y = j;
						break loop;
					}
					else {
						zeros++;
					}
				}
			}
			if(zeros == length * height) break;
			
			chainsize = 0;
			goNext(index_x, index_y);
			
			if(chainsize > 2) num_of_long_chains++;
			
		}
		
		return num_of_long_chains;
	}
	
	
	public void goNext(int index_x, int index_y) {
		System.out.println("INDEKSI " + index_x + " " + index_y);
		if(boxesToCheck[index_x][index_y] == 1) {
			boxesToCheck[index_x][index_y] = 0;
			System.out.println("BOX PONISTEN " + index_x + " " + index_y);
			chainsize++;
			if(index_y >= length || index_x >= height+1) {
				
			}
			else if(horizontal_edge[index_y][index_x] != BLACK) {
				edgeToTrigger.setX(index_y);
				edgeToTrigger.setY(index_x);
				edgeToTrigger.setIsHorizontal(true);
				if(index_x > 0) {
					goNext(index_x - 1, index_y);
				}
			}
			if(index_y >= length || index_x + 1 >= height+1) {
				
			}
			else if(horizontal_edge[index_y][index_x+1] != BLACK) {
				edgeToTrigger.setX(index_y);
				edgeToTrigger.setY(index_x+1);
				edgeToTrigger.setIsHorizontal(true);
				if(index_x+1 < length) {
					goNext(index_x+1, index_y);
				}
			}
			if(index_y >= length + 1 || index_x >= height) {
				
			}
			else if(vertical_edge[index_y][index_x] != BLACK) {
				edgeToTrigger.setX(index_y);
				edgeToTrigger.setY(index_x);
				edgeToTrigger.setIsHorizontal(false);
				if(index_y > 0) {
					goNext(index_x, index_y - 1);
				}
			}
			if(index_y+1 >= length + 1 || index_x >= height ) {
				
			}
			else if(vertical_edge[index_y+1][index_x] != BLACK) {
				edgeToTrigger.setX(index_y+1);
				edgeToTrigger.setY(index_x);
				edgeToTrigger.setIsHorizontal(false);
				if(index_y+1 < height) {
					goNext(index_x, index_y + 1);
				}
			}
		}
		else {
			return;
		}
	}
	
	
}
