package etf.dotsandboxes.rs160333d;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

public class RandomPlayer extends PlayerType {
	
	private boolean step_by_step;
	
	@Override
	public Edge getNextEdge(Board board, int color) {
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
		
		if(!moves_to_make_point.isEmpty()) possible_moves = moves_to_make_point;
		
		return possible_moves.get(new Random().nextInt(possible_moves.size()));
	}
}
