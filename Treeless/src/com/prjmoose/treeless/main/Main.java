package com.prjmoose.treeless.main;

import java.awt.EventQueue;
import java.awt.GraphicsEnvironment;

import javax.swing.JFrame;

import java.awt.CardLayout;
import java.io.IOException;

import com.prjmoose.treeless.views.SplashPanel;

import javax.swing.JPanel;
import javax.swing.JButton;

public class Main {
	private static Main window;
	private static GameLoop gameLoop;

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					window = new Main();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		// Keep main thread around as a command interface
		while(true) {
			try {
				switch(System.in.read()) {
					// Get time
					case 't':
						long time = System.currentTimeMillis();
						long gSec = time / 60000;
						double sec = (double) (time % 60000) / 1000;
						System.out.printf("Time: %d:%f\n", gSec, sec);
						break;
					case 's':
						if(gameLoop != null) {
							Thread gameLoopThread = gameLoop.getThread();

							if(gameLoopThread != null) {
								gameLoop.stop();

								try {
										gameLoopThread.join();
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
						break;
					// Emergency quit
					case 'q':
						if(gameLoop != null) {
							Thread gameLoopThread = gameLoop.getThread();

							if(gameLoopThread != null) {
								gameLoop.stop();

								try {
										gameLoopThread.join();
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}

						System.exit(0);
					// Ignore new line and space characters
					case ' ':
					case '\n':
					case '\r':
						break;
					default:
						System.out.printf("Unknown command\n");
						break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void setGameLoop(GameLoop gameLoop) {
		Main.gameLoop = gameLoop;
	}

	/**
	 * Create the application.
	 */
	public Main() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		int screenWidth = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getWidth();
		int screenHeight = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getHeight();
		
		frame = new JFrame();
		//frame.setBounds(100, 100, 450, 300);
		frame.setBounds((screenWidth - 1024) / 2, (screenHeight - 768) / 2, 1024 + 8, 768 + 27);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new CardLayout(0, 0));
		
		SplashPanel splashPanel = new SplashPanel();
		frame.add(splashPanel, "name_650037754452961");
	}

}
