package etf.dotsandboxes.rs160333d;

import javax.swing.*;
import javax.swing.SpringLayout.Constraints;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class DotsAndBoxes {
	
	private JFrame frame;
	
	private String blue_player, red_player;
	private PlayerType blue_player_type, red_player_type;
	
	private File selectedFile;
	
	String[] players = {"Izaberite igraca", "Covek", "Racunar"};
	
	int length, height;
	
	JTextField m = new JTextField();
	JTextField n = new JTextField();
	JTextField depth_tree = new JTextField();
	
	public static int depth_tree_value = 0;
	
	private JLabel error;
	
	int is_error = 0;
	
	JComboBox<String> player_red, player_blue;
	
	boolean player_1_pc = false;
	boolean player_2_pc = false;
	
	private int level = 0;
	
	boolean step_by_step = false;
	
	private JRadioButton[] butt_level;
	ButtonGroup group_level;
	
	private JRadioButton[] butt_mode;
	ButtonGroup group_mode;
	
	
	private boolean startGame;
	
	
	public DotsAndBoxes() {
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		player_red = new JComboBox<String>(players);
		player_blue = new JComboBox<String>(players);
		player_blue.setOpaque(true);
		player_red.setOpaque(true);
		player_blue.setFont(new Font("Tahoma", Font.PLAIN, 14));;
		player_red.setFont(new Font("Tahoma", Font.PLAIN, 14 ));
	
		butt_level = new JRadioButton[3];
		group_level = new ButtonGroup();
		for(int i=0; i<3; i++) {
			String level = String.valueOf(i+1);
			butt_level[i] = new JRadioButton(level);
			butt_level[i].setEnabled(false);
			group_level.add(butt_level[i]);
		}
		
		butt_mode = new JRadioButton[2];
		group_mode = new ButtonGroup();
		butt_mode[0] = new JRadioButton("Korak po korak");
		butt_mode[1] = new JRadioButton("Bez koraka");
		butt_mode[0].setEnabled(false);
		butt_mode[1].setEnabled(false);
		group_mode.add(butt_mode[0]);
		group_mode.add(butt_mode[1]);
	}
	
	
	public JLabel make_empty_label(Dimension dim) {
		JLabel label = new JLabel();
		label.setPreferredSize(dim);
		return label;
	}
	
	
	public void initialize_GUI() {
		JPanel grid = new JPanel(new GridBagLayout());
		GridBagConstraints grid_constraints = new GridBagConstraints();
		grid_constraints.gridx = 0;
		grid_constraints.gridy = 0;
		
		JLabel title = new JLabel(new ImageIcon(getClass().getResource("dots_and_boxes.png")));
		grid.add(title, grid_constraints);
		
		++grid_constraints.gridy;
		grid.add(make_empty_label(new Dimension(100,25)), grid_constraints);
		
		GridLayout grid_layout = new GridLayout(2, 2);
		grid_layout.setHgap(20);
		grid_layout.setVgap(10);
		
		JPanel players = new JPanel(grid_layout);
		players.setPreferredSize(new Dimension(600, 60));
		players.add(new JLabel(new ImageIcon(getClass().getResource("plavi.png"))));
		players.add(new JLabel(new ImageIcon(getClass().getResource("crveni.png"))));
		players.add(player_blue);
		players.add(player_red);
		
		
		ActionListener pcActionListener1 = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String s = (String) player_blue.getSelectedItem();
				if(s == "Racunar") {
					player_1_pc = true;
				}
				else {
					player_1_pc = false;
				}
				if(player_1_pc || player_2_pc) {
					depth_tree.setEnabled(true);
					depth_tree.setBackground(Color.WHITE);
					for(int i=0; i<3; i++) {
						butt_level[i].setEnabled(true);
					}
					for(int i=0; i<2; i++) {
						butt_mode[i].setEnabled(true);
					}
				}
				else {
					depth_tree.setEnabled(false);
					depth_tree.setBackground(null);
					for(int i=0; i<3; i++) {
						butt_level[i].setEnabled(false);
					}
					for(int i=0; i<2; i++) {
						butt_mode[i].setEnabled(false);
					}
				}
			}
		};
		
		
		ActionListener pcActionListener2 = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String s = (String) player_red.getSelectedItem();
				if(s == "Racunar") {
					player_2_pc = true;
				}
				else {
					player_2_pc = false;
				}
				if(player_1_pc || player_2_pc) {
					depth_tree.setEnabled(true);
					depth_tree.setBackground(Color.WHITE);
					for(int i=0; i<3; i++) {
						butt_level[i].setEnabled(true);
					}
					for(int i=0; i<2; i++) {
						butt_mode[i].setEnabled(true);
					}
				}
				else {
					depth_tree.setEnabled(false);
					depth_tree.setBackground(null);
					for(int i=0; i<3; i++) {
						butt_level[i].setEnabled(false);
					}
					for(int i=0; i<2; i++) {
						butt_mode[i].setEnabled(false);
					}
				}
			}
		};
		
		
		player_blue.addActionListener(pcActionListener1);
		player_red.addActionListener(pcActionListener2);
		
		++grid_constraints.gridy;
		grid.add(make_empty_label(new Dimension(100,25)), grid_constraints);
		
		
		++grid_constraints.gridy;
		grid.add(players, grid_constraints);
		
		++grid_constraints.gridy;
		grid.add(make_empty_label(new Dimension(100,25)), grid_constraints);
		
		
		JPanel depth_computer = new JPanel(grid_layout);
		depth_computer.add(new JLabel("Dubina razvijanja stabla igre: "));
		depth_computer.add(depth_tree);
		depth_tree.setEnabled(false);
		depth_tree.setBackground(null);
		++grid_constraints.gridy;
		grid.add(depth_computer, grid_constraints);
		
		
		++grid_constraints.gridy;
		grid.add(make_empty_label(new Dimension(100,25)), grid_constraints);
		
		++grid_constraints.gridy;
		grid.add(new JLabel("Nivo: "), grid_constraints);
		
		JPanel levelPanel = new JPanel(new GridLayout(1,3));
		for(int i=0; i<3; i++) {
			levelPanel.add(butt_level[i]);
			butt_level[i].setHorizontalAlignment(SwingConstants.RIGHT);
		}
		group_level.clearSelection();
		++grid_constraints.gridy;
		grid.add(levelPanel, grid_constraints);
		
		++grid_constraints.gridy;
		grid.add(make_empty_label(new Dimension(100,25)), grid_constraints);
		
		++grid_constraints.gridy;
		grid.add(new JLabel("Rezim: "), grid_constraints);
		
		JPanel modePanel = new JPanel(new GridLayout(2,1));
		for(int i=0; i<2; i++) {
			modePanel.add(butt_mode[i]);
			butt_mode[i].setHorizontalAlignment(SwingConstants.LEFT);
		}
		group_mode.clearSelection();
		++grid_constraints.gridy;
		grid.add(modePanel, grid_constraints);
		
		++grid_constraints.gridy;
		grid.add(make_empty_label(new Dimension(100,25)), grid_constraints);
		
		JPanel board_width = new JPanel(grid_layout);
		board_width.add(new JLabel("Duzina: "));
		board_width.add(m);
		board_width.add(new JLabel("Sirina: "));
		board_width.add(n);
		++grid_constraints.gridy;
		grid.add(board_width, grid_constraints);
		
		
		++grid_constraints.gridy;
        grid.add(make_empty_label(new Dimension(100, 30)), grid_constraints);

        
        JButton butt_choose_file = new JButton("Ucitaj igru iz fajla");
        butt_choose_file.addActionListener(choose_file);
        ++grid_constraints.gridy;
        grid.add(butt_choose_file, grid_constraints);

        ++grid_constraints.gridy;
        grid.add(make_empty_label(new Dimension(100, 30)), grid_constraints);
        
		error = new JLabel(" ", SwingConstants.CENTER);
		error.setForeground(Color.RED);
		++grid_constraints.gridy;
		grid.add(error, grid_constraints);
		
		++grid_constraints.gridy;
        grid.add(make_empty_label(new Dimension(500, 30)), grid_constraints);
        
        JButton butt_submit = new JButton("Zapocni igru");
        butt_submit.addActionListener(submitListener);
        ++grid_constraints.gridy;
        grid.add(butt_submit, grid_constraints);
        
        ++grid_constraints.gridy;
        grid.add(make_empty_label(new Dimension(500, 25)), grid_constraints);
		
		
        frame.setTitle("Dots & Boxes");
		frame.setContentPane(grid);
		frame.pack();
		frame.setVisible(true);
		
		startGame = false;
		while(!startGame) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		new Game(this, frame, length, height, red_player_type, blue_player_type, red_player, blue_player, level, depth_tree_value, selectedFile, step_by_step);
	}
	
	private ActionListener choose_file = new ActionListener() {
		public void actionPerformed(ActionEvent actionEvent) {
			JFileChooser j = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
			int r = j.showOpenDialog(null);
			if(r == JFileChooser.APPROVE_OPTION) {
				selectedFile = j.getSelectedFile();
				System.out.print("PUTANJA DO FAJLA: ");
				System.out.println(selectedFile.getAbsolutePath());
			}
		}
	};
	
	
	private ActionListener submitListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            int red_index = player_red.getSelectedIndex();
            int blue_index = player_blue.getSelectedIndex();
            if(red_index==0 || blue_index==0) {
                error.setText("Izaberite igrace!");
                is_error = 1;
                return;
            }
            else {
                error.setText("");
                is_error = 0;
                blue_player = players[blue_index];
                red_player = players[red_index];
                if(blue_index == 2) blue_player_type = getPlayerType();
                if(red_index == 2) red_player_type = getPlayerType();
            }
            
            if(m.getText().isEmpty()) {
            	length = 2;
            }
            else {
            	String a = m.getText();
            	length = Integer.parseInt(a);
            }
            if(n.getText().isEmpty()) {
            	height = 2;
            }
            else {
            	String b = n.getText();
            	height = Integer.parseInt(b);
            }
            
            
            if(player_1_pc || player_2_pc) {
	            for(int i=0; i<3; i++) {
	                if(butt_level[i].isSelected()) {
	                	level = i+1;
	                }
	            }
	            
	            is_error = 0;
	            
	            if(level == 0) {
	            	error.setText("Izaberite nivo!");
	            	is_error = 1;
	            }
	            
	            if(depth_tree.getText().isEmpty()) {
	            	error.setText("Izaberite dubinu razvijanja stabla igre!");
	            	is_error = 1;
	            }
	            else {
	            	String value = depth_tree.getText();
	            	depth_tree_value = Integer.parseInt(value);
	            }
	            
	            if(butt_mode[0].isSelected()) {
	            	step_by_step = true;
	            }
            }
            
            if(is_error == 0) {
            	System.out.println("IGRA --------");
	            System.out.print("Duzina: ");
	            System.out.println(length);
	            System.out.print("Sirina: ");
	            System.out.println(height);
	            System.out.print("Nivo: ");
	            System.out.println(level);
	            System.out.printf("Dubina razvijanja stabla: ");
	            System.out.println(depth_tree_value);
	            System.out.printf("Korak po korak: ");
	            System.out.println(step_by_step);
	            startGame = true;
            }
        }
	};
        
	
	private PlayerType getPlayerType() {
		for(int i=0; i<3; i++) {
			if(butt_level[i].isSelected()) {
				level = i+1;
			}
		}
		
		if(level == 1) return new RandomPlayer();
		if(level == 2) return new AlphaBetaPlayer();
		if(level == 3) return new OptimizedAlphaBetaPlayer();
		else return new RandomPlayer();
	}
	
	
	public static void main(String[] args) {
		new DotsAndBoxes().initialize_GUI();
	}
}
