package etf.dotsandboxes.rs160333d;

import javax.swing.*;
import javax.swing.SpringLayout.Constraints;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Game {
	
	private int length, height;
	
	private int level, depth_tree_value;
	
	private int turn;
	
	private boolean mouse_disable = false;
	
	DotsAndBoxes parent;
	
	PlayerType blue_player_type, red_player_type, current_player_type;
	String red_player, blue_player;
	
	private JFrame frame;
	private JLabel lab_red_score, lab_blue_score, lab_message;
	
	public static JLabel next_step_message;
	
	private Board board;
	
	private JLabel[][] horizontal_edge, vertical_edge, box;
	private boolean[][] isHotizontalEdgeSet, isVerticalEdgeSet;
	
	public GridBagConstraints grid_constraints;
	public JPanel grid;
	
	boolean back = false;
	
	public static boolean step_by_step;
	public static boolean performNextStep = false;
	
	private JLabel curr_player;
	
	private PrintWriter writer;
	
	public Game(DotsAndBoxes parent, JFrame frame, int _length, int _height, PlayerType red_player_type, PlayerType blue_player_type, String red_player, String blue_player, int level, int depth_tree_value, File selectedFile, boolean step_by_step) {
		if(selectedFile != null) {
			try {
				parseFileDimension(selectedFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		this.step_by_step = step_by_step;
		
		this.parent = parent;
		this.frame = frame;
		if(selectedFile == null) this.length = _length;
		if(selectedFile == null) this.height = _height;
		this.red_player = red_player;
		this.blue_player = blue_player;
		this.red_player_type = red_player_type;
		this.blue_player_type = blue_player_type;
		this.level = level;
		this.depth_tree_value = depth_tree_value;
		
		horizontal_edge = new JLabel[length][height+1];
		vertical_edge = new JLabel[length+1][height];
		box = new JLabel[length][height];
		
		isHotizontalEdgeSet = new boolean [length][height+1];
		isVerticalEdgeSet = new boolean [length+1][height];
		
		lab_message = new JLabel();
		Game.next_step_message = new JLabel();
		
		try {
			writer = new PrintWriter("dots_and_boxes_output.txt", "UTF-8");
			String dimensions = this.length + " " + this.height;
			writer.println(dimensions);
			writer.flush();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		initialize_Game(selectedFile);
		
		writer.close();
	}
	
	
	public void parseFileDimension(File selectedFile) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(selectedFile));
		
		String line;
		if((line = br.readLine()) != null) {
			String[] fields = line.split(" ");
			length = Integer.parseInt(fields[0]);
			height = Integer.parseInt(fields[1]);
		}
		
		System.out.print("DIMENZIJE UCITANE IZ FAJLA: ");
		System.out.print(length + " ");
		System.out.println(height);
	}
	
	
	public void parseFileMoves(File selectedFile) throws IOException {
		System.out.println("PARSIRAM POTEZE");
		BufferedReader br = new BufferedReader(new FileReader(selectedFile));
		
		String line;
		line = br.readLine();
		while((line = br.readLine()) != null) {
			char first = line.charAt(0);
			char second = line.charAt(1);
			int ascii1 = first;
			int ascii2 = second;
			boolean isHorizontal = false;
			if((ascii1 >= 65) && (ascii1 <= 90)) {
				isHorizontal = false;
			}
			else {
				isHorizontal = true;
			}
			int x, y;
			if(isHorizontal) {
				y = ascii1 - 48;
				x = ascii2 - 65;
			}
			else {
				y = ascii1 - 65;
				x = ascii2 - 48;
			}
			
			System.out.println("Linija " + x + " " + y + isHorizontal);
			
			Edge edge = new Edge(x, y, isHorizontal);
			
			triggerEdge(edge);
		}
	}
	
	public static String parseMovesToFile(Edge edge) throws IOException {
		String outputLine = "";
		
		int x = edge.getX();
		int y = edge.getY();
		boolean isHorizontal = edge.getIsHorizontal();
		
		char first;
		char second;
		
		if(isHorizontal) {
			second = (char) (65 + y);
			first = (char) (48 + x);
		}
		else {
			first = (char) (65 + x);
			second = (char) (48 + y);
		}
		
		outputLine += first;
		outputLine += second;
		
		//System.out.println("Linija u fajl: " + outputLine);
		
		return outputLine;
	}
	
	
	public JLabel make_empty_label(Dimension dim) {
		JLabel label = new JLabel();
		label.setPreferredSize(dim);
		return label;
	}
	
	
	//DRAW SHAPES
	private JLabel drawDot() {
		JLabel label = new JLabel();
		label.setPreferredSize(new Dimension(10, 10));
		label.setBackground(Color.WHITE);
		label.setOpaque(true);
		return label;
	}
	
	private JLabel drawBox() {
		JLabel label = new JLabel();
		label.setPreferredSize(new Dimension(50, 50));
		label.setBackground(new Color(160, 205, 250));
		label.setOpaque(true);
		return label;
	}
	
	
	private JLabel drawHorizontalEdge() {
		JLabel label = new JLabel();
		label.setPreferredSize(new Dimension(50, 10));
		label.setBackground(new Color(160, 205, 250));
		label.setOpaque(true);
		label.addMouseListener(mouseListener);
		return label;
	}
	
	private JLabel drawVerticalEdge() {
		JLabel label = new JLabel();
		label.setPreferredSize(new Dimension(10, 50));
		label.setBackground(new Color(160, 205, 250));
		label.setOpaque(true);
		label.addMouseListener(mouseListener);
		return label;
	}
	
	
	private Edge getEventSource(Object object) {
        for(int i=0; i<length; i++)
            for(int j=0; j<(height+1); j++)
                if(horizontal_edge[i][j] == object)
                    return new Edge(i,j,true);
        for(int i=0; i<(length+1); i++)
            for(int j=0; j<height; j++)
                if(vertical_edge[i][j] == object)
                    return new Edge(i,j,false);
        return new Edge();
    }
	
	
	//MOUSE LISTENER
	private MouseListener mouseListener = new MouseListener() {
        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
            if(mouse_disable) return;
            triggerEdge(getEventSource(mouseEvent.getSource()));
        }

        @Override
        public void mousePressed(MouseEvent mouseEvent) {

        }

        @Override
        public void mouseReleased(MouseEvent mouseEvent) {

        }

        @Override
        public void mouseEntered(MouseEvent mouseEvent) {
            if(mouse_disable) return;
            Edge triggered_edge = getEventSource(mouseEvent.getSource());
            int x=triggered_edge.getX();
            int y=triggered_edge.getY();
            if(triggered_edge.getIsHorizontal()) {
                if(isHotizontalEdgeSet[x][y]) return;
                horizontal_edge[x][y].setBackground((turn == Board.RED) ? Color.RED : Color.BLUE);
            }
            else {
                if(isVerticalEdgeSet[x][y]) return;
                vertical_edge[x][y].setBackground((turn == Board.RED) ? Color.RED : Color.BLUE);
            }
        }

        @Override
        public void mouseExited(MouseEvent mouseEvent) {
            if(mouse_disable) return;
            Edge location = getEventSource(mouseEvent.getSource());
            int x=location.getX();
            int y=location.getY();
            if(location.getIsHorizontal()) {
                if(isHotizontalEdgeSet[x][y]) return;
                horizontal_edge[x][y].setBackground(new Color(160, 205, 250));;
            }
            else {
                if(isVerticalEdgeSet[x][y]) return;
                vertical_edge[x][y].setBackground(new Color(160, 205, 250));;
            }
        }
    };
    
    
    private void triggerEdge(Edge triggered_edge) {
    	int x = triggered_edge.getX();
    	int y = triggered_edge.getY();
    	ArrayList<Point> points;
    	System.out.println("Edge: " + x + " " + y + triggered_edge.getIsHorizontal());
    	if(triggered_edge.getIsHorizontal()) {
    		if(isHotizontalEdgeSet[x][y]) return;
    		points = board.setHorizontalEdge(x, y, turn);
    		horizontal_edge[x][y].setBackground(Color.lightGray);
    		isHotizontalEdgeSet[x][y] = true;
    	}
    	else {
    		if(isVerticalEdgeSet[x][y]) return;
    		points = board.setVerticalEdge(x, y, turn);
    		vertical_edge[x][y].setBackground(Color.lightGray);
    		isVerticalEdgeSet[x][y] = true;
    	}
    	
    	for(Point point : points) {
    		box[point.x][point.y].setBackground((turn == Board.RED) ? Color.RED : Color.BLUE);
    	}
    	
    	lab_blue_score.setText(String.valueOf(board.getBlueScore()));
    	lab_red_score.setText(String.valueOf(board.getRedScore()));
    	
    	if(board.isOver()) {
    		int winner = board.getWinner();
    		if(winner == Board.RED) {
    			curr_player.setText(" ");
    			lab_message.setText("Crveni igrac je pobedio!");
    			lab_message.setForeground(Color.RED);
    		}
    		else if(winner == Board.BLUE) {
    			curr_player.setText(" ");
    			lab_message.setText("Plavi igrac je pobedio!");
    			lab_message.setForeground(Color.BLUE);
    		}
    		else {
    			curr_player.setText(" ");
    			lab_message.setText("Rezultat je neresen!");
    			lab_message.setForeground(Color.BLACK);
    		}
    	}
    	
    	if(points.isEmpty()) {
    		if(turn == Board.BLUE) {
    			current_player_type = red_player_type;
    			lab_message.setText("CRVENI");
    			lab_message.setForeground(Color.RED);
    			turn = Board.RED;
    		}
    		else {
    			current_player_type = blue_player_type;
    			lab_message.setText("PLAVI");
    			lab_message.setForeground(Color.BLUE);
    			turn = Board.BLUE;
    		}
    	}
    	
    	try {
			writer.println(parseMovesToFile(triggered_edge));
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    //BACK BUTTON LISTENER
    private ActionListener butt_backListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			back = true;
		}
	};
	
	//BACK BUTTON LISTENER
	 private ActionListener butt_nextStepListener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Game.performNextStep = true;
			}
		};
    
    
    public ArrayList<JPanel> initializeGamePanel(FlowLayout panel_flow_layout) {
    	ArrayList<JPanel> panels = new ArrayList<JPanel>();
    	for(int i=0; i<2*height + 1; i++) {
        	JPanel panel = new JPanel(panel_flow_layout);
        	if(i % 2 == 0) {
        		panel.add(drawDot());
        		for(int j=0; j<length; j++) {
        			horizontal_edge[j][i/2] = drawHorizontalEdge();
        			panel.add(horizontal_edge[j][i/2]);
        			panel.add(drawDot());
        		}
        	}
        	else {
        		for(int j=0; j<length; j++) {
        			vertical_edge[j][i/2] = drawVerticalEdge();
        			panel.add(vertical_edge[j][i/2]);
        			box[j][i/2] = drawBox();
        			panel.add(box[j][i/2]);
        		}
        		vertical_edge[length][i/2] = drawVerticalEdge();
        		panel.add(vertical_edge[length][i/2]);
        	}
        	panels.add(panel);  	
        }
    	
    	return panels;
    }
	
	
	public void initialize_Game(File selectedFile) {
		board = new Board(length, height);
		
		turn = Board.BLUE;
		current_player_type = blue_player_type;
		
		grid = new JPanel(new GridBagLayout());
		grid_constraints = new GridBagConstraints();
		grid_constraints.gridx = 0;
		grid_constraints.gridy = 00;
		grid.add(make_empty_label(new Dimension(100,25)), grid_constraints);
		
		JLabel title = new JLabel(new ImageIcon(getClass().getResource("game.png")));
		grid.add(title, grid_constraints);
		
		GridLayout players_layout = new GridLayout(2,2);
		players_layout.setHgap(50);
		JPanel players = new JPanel(players_layout);
		players.setPreferredSize(new Dimension(600, 60));
		players.add(new JLabel(new ImageIcon(getClass().getResource("plavi.png"))));
		players.add(new JLabel(new ImageIcon(getClass().getResource("crveni.png"))));
		players.add(new JLabel("<html><font color='blue'>" + blue_player, SwingConstants.CENTER));
		players.add(new JLabel("<html><font color='red'>" + red_player, SwingConstants.CENTER));
        ++grid_constraints.gridy;
        grid.add(players, grid_constraints);
        
        ++grid_constraints.gridy;
        
        GridLayout score_layout = new GridLayout(1,2);
        score_layout.setHgap(320);
        JPanel score = new JPanel(score_layout);
        lab_red_score = new JLabel("0", SwingConstants.CENTER);
        lab_blue_score = new JLabel("0", SwingConstants.CENTER);
        lab_red_score.setForeground(Color.RED);
        lab_blue_score.setForeground(Color.BLUE);
        score.add(lab_blue_score);
        score.add(lab_red_score);
        ++grid_constraints.gridy;
        grid.add(score, grid_constraints);
        
        ++grid_constraints.gridy;
        grid.add(make_empty_label(new Dimension(100,25)), grid_constraints);

        FlowLayout panel_flow_layout = new FlowLayout(FlowLayout.CENTER, 0, 0);
        
        ArrayList<JPanel> panels = initializeGamePanel(panel_flow_layout);
        
        int i=0;
        
        while(i < panels.size()) {
        	++grid_constraints.gridy;
            grid.add(panels.get(i), grid_constraints);
            i++;
        }
        
        ++grid_constraints.gridy;
        grid.add(make_empty_label(new Dimension(100,60)), grid_constraints);
        
        curr_player = new JLabel("Trenutno igra:");
        ++grid_constraints.gridy;
        grid.add(curr_player, grid_constraints);
        
        ++grid_constraints.gridy;
        grid.add(make_empty_label(new Dimension(100,20)), grid_constraints);

        ++grid_constraints.gridy;
        lab_message.setText("PLAVI");
        lab_message.setForeground(Color.BLUE);
        grid.add(lab_message, grid_constraints);
        
        if(step_by_step) {
	        ++grid_constraints.gridy;
	        grid.add(make_empty_label(new Dimension(100,40)), grid_constraints);
	        
	        ++grid_constraints.gridy;
	        Game.next_step_message.setText(" ");
	        Game.next_step_message.setForeground(Color.orange);
	        grid.add(Game.next_step_message, grid_constraints);
	        
	        JButton butt_next_step = new JButton("Sledeci korak");
	        butt_next_step.setPreferredSize(new Dimension(120, 20));
	        butt_next_step.addActionListener(butt_nextStepListener);
	        ++grid_constraints.gridy;
	        grid.add(butt_next_step, grid_constraints);
	        
	        ++grid_constraints.gridy;
	        grid.add(make_empty_label(new Dimension(100,30)), grid_constraints);
        }
        
        ++grid_constraints.gridy;
        grid.add(make_empty_label(new Dimension(100,15)), grid_constraints);
        
        JButton butt_back_to_main = new JButton("Nazad");
        butt_back_to_main.setPreferredSize(new Dimension(150, 30));
        butt_back_to_main.addActionListener(butt_backListener);
        ++grid_constraints.gridy;
        grid.add(butt_back_to_main, grid_constraints);
        
        ++grid_constraints.gridy;
        grid.add(make_empty_label(new Dimension(100,50)), grid_constraints);
        
        
        frame.getContentPane().removeAll();
        frame.revalidate();
        frame.repaint();
        frame.setContentPane(grid);
        frame.pack();
        frame.setVisible(true);

		if(selectedFile != null) {
			try {
				parseFileMoves(selectedFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
        
        while(!board.isOver()) {
        	if(back) break;
        	if(current_player_type != null) {
        		mouse_disable = true;
        		triggerEdge(current_player_type.getNextEdge(board, turn));
        	}
        	else {
        		mouse_disable = false;
        	}
        	try {
        		Thread.sleep(100);
        	} catch(InterruptedException e) {
        		e.printStackTrace();
        	}
        }
        
        
        while(!back) {
        	try {
        		Thread.sleep(100);
        	} catch(InterruptedException e) {
        		e.printStackTrace();
        	}
        }
        
        parent.initialize_GUI();
        
	}

}
