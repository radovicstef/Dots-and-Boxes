package etf.dotsandboxes.rs160333d;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

public class OptimizedAlphaBetaPlayer extends PlayerType {
	
	private int depth_tree_value;
	private Edge idealEdge;
	double alpha = Double.NEGATIVE_INFINITY;
	double beta = Double.POSITIVE_INFINITY;
	
	private boolean step_by_step;
	
	private boolean making_point = false;
	
	
	public ArrayList<Edge> getBox(Board board) {
		ArrayList<Edge> moves_to_make_point = new ArrayList<Edge>();
		ArrayList<Edge> possible_moves = board.getPossibleMoves();
		for(Edge move : possible_moves) {
			int x = move.getX();
			int y = move.getY();
			if(move.getIsHorizontal()) {
				if(y<board.getHeight() && board.vertical_edge[x][y]==Board.BLACK && board.vertical_edge[x+1][y]==Board.BLACK && board.horizontal_edge[x][y+1]==Board.BLACK) {
					moves_to_make_point.add(move);
				}
				if(y>0 && board.vertical_edge[x][y-1]==Board.BLACK && board.vertical_edge[x+1][y-1]==Board.BLACK && board.horizontal_edge[x][y-1]==Board.BLACK) {
					moves_to_make_point.add(move);
				}
			}
			else {
				if(x<board.getLength() && board.horizontal_edge[x][y]==Board.BLACK && board.horizontal_edge[x][y+1]==Board.BLACK && board.vertical_edge[x+1][y]==Board.BLACK) {
					moves_to_make_point.add(move);
				}
				if(x>0 && board.horizontal_edge[x-1][y]==Board.BLACK && board.horizontal_edge[x-1][y+1]==Board.BLACK && board.vertical_edge[x-1][y]==Board.BLACK) {
					moves_to_make_point.add(move);
				}
			}
		}
		
		return moves_to_make_point;
	}
	
	
	public Edge getNextEdge(Board board, int color) {
		System.out.println("OPTIMIZED ALPHA-BETA PLAYER ------");
		Game.performNextStep = false;
		this.depth_tree_value = DotsAndBoxes.depth_tree_value;
		System.out.print("Nivo razvijanja stabla: ");
		System.out.println(this.depth_tree_value);
		this.step_by_step = Game.step_by_step;
		if(!board.isOver()) {
			ArrayList<Edge> moves_to_make_point = new ArrayList<Edge>();
			ArrayList<Edge> possible_moves = board.getPossibleMoves();
			
			moves_to_make_point = this.getBox(board);
			
			if(!moves_to_make_point.isEmpty()) {
				System.out.println("a-b ZATVARA KVADRAT");
				possible_moves = moves_to_make_point;
				Game.next_step_message.setText("Racunar zatvara kvadrat");
				making_point = true;
				return possible_moves.get(new Random().nextInt(possible_moves.size()));
			}
			else {
				maxValue(board, depth_tree_value, alpha, beta, color, true);
			}
		}
		if(idealEdge != null) {
			return idealEdge;
		}
		else {
			System.out.println("randomPotez");
			return board.getPossibleMovesButNotThree(true).get(new Random().nextInt(board.getPossibleMovesButNotThree(true).size()));
		}
	}
	
	
	public double maxValue(Board board, int level, double alpha, double beta, int color, boolean original_call) {
		//System.out.println("maxValue");
		Edge bestEdge = null;
		Board game_board = board;
		
		if(game_board.isOver() || level == 0) {
			//System.out.println("maxValue END");
			if(color == game_board.RED) {
				int heuristic_function;
				heuristic_function = 5*(game_board.getRedScore() - game_board.getBlueScore());
				int num_of_long_chains = board.checkNumOfLongChains();
				if(num_of_long_chains % 2 == 1) {
					heuristic_function += 50;
				}
				return heuristic_function;
			}
			else {
				int heuristic_function;
				heuristic_function = 5*(game_board.getBlueScore() - game_board.getRedScore());
				int num_of_long_chains = board.checkNumOfLongChains();
				if(num_of_long_chains % 2 == 0) {
					heuristic_function += 50;
				}
				return heuristic_function;
			}
		}
		else {
			ArrayList<Edge> moves_to_make_point = this.getBox(game_board);
			ArrayList<Edge> edges;
			if(!moves_to_make_point.isEmpty() && !original_call) {
				edges = moves_to_make_point;
			}
			else {
				edges = game_board.getPossibleMovesButNotThree(true);
			}
			if(edges.size() > 1) {
				Collections.shuffle(edges);
			}
			String comment;
			for(Edge e : edges) {
				//System.out.println("!!!!!!edge");
				comment = "";
				double val;
				int x = e.getX();
				int y = e.getY();
				boolean keep_playing = false;
				//move
				if(e.getIsHorizontal()) {
					keep_playing = game_board.setHorizontalEdgeTemp(x, y, color);
				}
				else {
					keep_playing = game_board.setVerticalEdgeTemp(x, y, color);
				}
				//System.out.println("Call min Value");
				if(keep_playing) {
					val = this.maxValue(board, level, alpha, beta, color, false);
				}
				else {
				val = this.minValue(board, level-1, alpha, beta, color);
				}
				//System.out.print("min -- ");
				//System.out.println(val);
				//undo the move
				if(e.getIsHorizontal()) {
					game_board.resetHorizontalEdge(x, y, color);
				}
				else {
					game_board.resetVerticalEdge(x, y, color);
				}
				if(val > alpha) {
					alpha = val;
					bestEdge = e;	
				}
				comment = val + "   " + "alfa: " + alpha + "	" + "   beta: " + beta;
				if(alpha >= beta) {
					comment+= " ODSECANJE";
				}
				if(step_by_step && original_call) {
					String move = "";
					try {
						move = Game.parseMovesToFile(e);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					System.out.print("Ivica: " + move);
					System.out.print(" -- ");
					System.out.print("Funkcija procene: ");
					System.out.println(comment);
					
					if(!making_point) Game.next_step_message.setText("Ivica: " + move + " -- " + "Funkcija procene: " + comment);
					making_point = false;
					
					System.out.println("------------------");
					while(Game.performNextStep == false) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					
					if(making_point) Game.next_step_message.setText("Ivica: " + move + " -- " + "Funkcija procene: " + comment);
					
					Game.performNextStep = false;
				}
				if(alpha >= beta) {
					//System.out.println("Cut /maxValue");
					return beta;
				}
			}
		}
		
		idealEdge = bestEdge;
		return alpha;
	}
	
	
	public double minValue(Board board, int level, double alpha, double beta, int oponent_color) {
		//System.out.println("minValue");
		Edge bestEdge =null;
		Board game_board = board;
		int color = game_board.BLANK;
		if(oponent_color == game_board.RED) {
			color = game_board.BLUE;
			//System.out.println(color);
		}
		else {
			color = game_board.RED;
			//System.out.println(color);
		}
		
		if(game_board.isOver() || level == 0) {
			if(oponent_color == game_board.RED) {
				int heuristic_function;
				heuristic_function = 5*(game_board.getRedScore() - game_board.getBlueScore());
				int num_of_long_chains = board.checkNumOfLongChains();
				if(num_of_long_chains % 2 == 1) {
					heuristic_function += 50;
				}
				return heuristic_function;
			}
			else {
				int heuristic_function;
				heuristic_function = 5*(game_board.getBlueScore() - game_board.getRedScore());
				int num_of_long_chains = board.checkNumOfLongChains();
				if(num_of_long_chains % 2 == 0) {
					heuristic_function += 50;
				}
				return heuristic_function;
			}
		}
		else {
			ArrayList<Edge> moves_to_make_point = this.getBox(game_board);
			ArrayList<Edge> edges;
			if(!moves_to_make_point.isEmpty()) {
				edges = moves_to_make_point;
			}
			else {
				edges = game_board.getPossibleMovesButNotThree(true);
			}
			if(edges.size() > 1) {
				Collections.shuffle(edges);
			}
			for(Edge e : edges) {
				double val;
				int x = e.getX();
				int y = e.getY();
				boolean keep_playing = false;
				//move
				if(e.getIsHorizontal()) {
					keep_playing = game_board.setHorizontalEdgeTemp(x, y, color);
				}
				else {
					keep_playing = game_board.setVerticalEdgeTemp(x, y, color);
				}
				if(keep_playing) {
					val = this.minValue(board, level, alpha, beta, oponent_color);
				}
				else {
					val = this.maxValue(board, level-1, alpha, beta, oponent_color, false);
					//System.out.print("max -- ");
					//System.out.println(val);
				}
				//undo the move
				if(e.getIsHorizontal()) {
					game_board.resetHorizontalEdge(x, y, color);
				}
				else {
					game_board.resetVerticalEdge(x, y, color);
				}
				if(val < beta) {
					beta = val;
					bestEdge = e;
				}
				if(beta <= alpha) {
					return alpha;
				}
			}
		}
		
		idealEdge = bestEdge;
		return beta;
	}

}
