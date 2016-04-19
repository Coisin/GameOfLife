import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class Game extends JFrame{
	
	JPanel gamePanel, mainPanel, navPanel;
	
	final int cellSize = 15;
	final int mapWidth = 30;
	final int mapHeight = 30;
	
	final int width = cellSize * mapWidth;
	final int height = cellSize * mapHeight;
	
	boolean[][] current, next, saved;
	
	Image offScrImg;
	Graphics imgG;
	
	boolean play;
	
	public static void main(String args[]) {
		
		new Game();
		
	}
	
	public Game() {
		
		gamePanel = new JPanel();
		navPanel = new JPanel();
		mainPanel = new JPanel();
		
		final JButton playButton = new JButton("Play");
		JButton saveButton = new JButton("Save");
		JButton restoreButton = new JButton("Restore");
		JButton resetButton = new JButton("Reset");
		
		navPanel.add(playButton);
		navPanel.add(saveButton);
		navPanel.add(restoreButton);
		navPanel.add(resetButton);
		mainPanel.add(navPanel);
		mainPanel.add(gamePanel);
		this.add(mainPanel);
		
		navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.LINE_AXIS));
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		
		gamePanel.setPreferredSize(new Dimension(width, height));
		gamePanel.setMaximumSize(new Dimension(width, height));
		gamePanel.setMinimumSize(new Dimension(width, height));
		
		current = new boolean[mapHeight][mapWidth];
		next = new boolean[mapHeight][mapWidth];
		saved = new boolean[mapHeight][mapWidth];
		
		gamePanel.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				
				if(play) {
					System.out.println("The Game Is Currently Active.. Click Pause");
					return;
				}
				
				int posX = e.getX() / cellSize;
				int posY = e.getY() / cellSize;
				current[posY][posX] = !current[posY][posX];
			}
			
		} );
		
		gamePanel.setFocusable(true);
		gamePanel.requestFocus();
		
		this.pack();
		this.setDefaultCloseOperation(this.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		
		playButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				play = !play;
				
				if(play)playButton.setText("Pause");
				else playButton.setText("Play");
				
			}
		});
		
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for(int i = 0;i<next.length;i++)
					for(int j = 0;j<next[i].length;j++)
						saved[i][j]=current[i][j];
			}
		});
		
		restoreButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for(int i = 0;i<next.length;i++)
					for(int j = 0;j<next[i].length;j++)
						current[i][j]=saved[i][j];
			}
		});
		
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				current = new boolean[mapHeight][mapWidth];
			}
		});
		
		offScrImg = createImage(width, height);
		imgG = offScrImg.getGraphics();
		
		Thread loop = new Thread() {
			public void run() {
				while(true) {
					try {
						Thread.sleep(100);
					}catch(Exception e){
						e.printStackTrace();
					}
					repaint();
				}
			}
		};
		
		loop.start();
		
	}
	
	public boolean determine(int y, int x) {
		
		int neighbours = 0;
		
		if(x > 0) {
			
			if(current[y][x - 1])neighbours++;
			if(y > 0 && current[y - 1][x - 1])neighbours++;
			if(y < mapHeight - 1 && current[y + 1][x - 1])neighbours++;
			
		}
		
		if(x < mapWidth - 1) {
			
			if(current[y][x + 1])neighbours++;
			if(y > 0 && current[y - 1][x + 1])neighbours++;
			if(y < mapHeight - 1 && current[y + 1][x + 1])neighbours++;
			
		}
		
		if(y > 0 && current[y - 1][x])neighbours++;
		if(y < mapHeight - 1 && current[y + 1][x])neighbours++;
		
		if(neighbours == 3)return true;
		if(neighbours == 2 && current[y][x])return true;
		
		return false;
		
	}
	
	public void repaint() {
		
		if(play) {
			for(int i = 0;i<mapHeight;i++) {
				for(int j = 0;j<mapWidth;j++) {
					next[i][j] = determine(i, j);
				}
			}
			
			for(int i = 0;i<mapHeight;i++) {
				for(int j = 0;j<mapWidth;j++) {
					current[i][j] = next[i][j];
				}
			}
		}
		
		for(int i = 0;i<mapHeight;i++) {
			for(int j = 0;j<mapWidth;j++) {
				Color color = Color.red;
				if(current[i][j]) {
					color = Color.yellow;
				}
				imgG.setColor(color);
				int x = j * cellSize;
				int y = i * cellSize;
				
				imgG.fillRect(x, y, cellSize, cellSize);
				
			}
			
		}
		
		imgG.setColor(Color.black);
		
		for(int i = 0;i<mapHeight;i++) {
			int y = i * cellSize;
			imgG.drawLine(0, y, width, y);
		}
		
		for(int i = 0;i<mapWidth;i++) {
			int x = i * cellSize;
			imgG.drawLine(x, 0, x, height);
		}
		
		gamePanel.getGraphics().drawImage(offScrImg, 0, 0, width, height, null);
		
	}
	
}