package com.prjmoose.treeless.views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import com.prjmoose.treeless.Game;

public class PlayerInterfacePanel extends JPanel implements KeyListener, MouseListener, MouseMotionListener {
	public static final int DEFAULT_WIDTH = 800;
	public static final int DEFAULT_HEIGHT = 640;
	public static final int DEFUALT_FPS = 30;

	public static final int SCROLL_MARGIN = 20;
	public static final double SCROLL_VELOCITY = 5;
	
	public static final int VISIBLE_LIGHT_LEVEL = 0;
	public static final int LOW_LIGHT_LEVEL = 127;
	public static final int HIDDEN_LIGHT_LEVEL = 255;
	

	private Point location;
	
	private int fps;

	private boolean scrolling;
	private double scrollDirection;
	
	// Local World
	private Game localWorld;

	/**
	 * Create the panel.
	 */
	public PlayerInterfacePanel(Game localWorld) {
		super();

		this.localWorld = localWorld;
		
		location = new Point(0, 0);
		fps = DEFUALT_FPS;
		scrolling = false;

		setIgnoreRepaint(true);
		setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

		setFocusable(true);
		requestFocus();
		
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	public void update(long deltaTime) {
		if(scrolling) {
			double x = location.getX() + Math.cos(scrollDirection) * SCROLL_VELOCITY;
			double y = location.getY() + Math.sin(scrollDirection) * SCROLL_VELOCITY;
			
			location.setLocation(x, y);
		}
		
		// fix camera to map bounds
	}

	public void render() {
		if(!isShowing()) {
			return;
		}
		
		// Create frame buffer and graphics painter
		BufferedImage frameBuffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) frameBuffer.getGraphics();
		
		// blank screen
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		// draw map
		
		// draw entities by depth
		
		// render sight

		// draw scroll direction
		if(scrolling) {
			double x = Math.cos(scrollDirection) * 100;
			double y = Math.sin(scrollDirection) * 100;

			g.setColor(Color.RED);
			g.drawLine(getWidth() / 2, getHeight() / 2, (int) (getWidth() / 2 + x), (int) (getHeight() / 2 + y));
		}
		
		// draw hud
		g.setColor(Color.GRAY);
		// Boxes
		g.drawRect((int) (getWidth() * 0.05), (int) (getHeight() * 0.05), (int) (getWidth() * 0.9), (int) (getHeight() * 0.9));
		g.drawRect((int) (getWidth() * 0.1), (int) (getHeight() * 0.1), (int) (getWidth() * 0.8), (int) (getHeight() * 0.8));
		// Hatch marks
		g.drawLine((int) (getWidth() / 2), 0, (int) (getWidth() / 2), (int) (getHeight() * 0.2)); // North Line
		g.drawLine(0, (int) getHeight() / 2, (int) (getWidth() * 0.2), (int) (getHeight() / 2)); // West Line
		g.drawLine((int) (getWidth() * 0.8), (int) (getHeight() / 2), (int) (getWidth()), (int) (getHeight() / 2)); // East Line
		g.drawLine((int) (getWidth() / 2), (int) (getHeight() * 0.8), (int) (getWidth() / 2), (int) (getHeight())); // South Line
		// Cross
		g.drawLine((int) (getWidth() / 2), (int) (getHeight() * 0.4), (int) (getWidth() / 2), (int) (getHeight() * 0.6)); // vert
		g.drawLine((int) (getWidth() * 0.4), (int) (getHeight() / 2), (int) (getWidth() * 0.6), (int) (getHeight() / 2)); // horz
		
		// draw info
		g.setColor(Color.white);
		g.drawString(getHeight() + " x " + getWidth() + " @ " + getFPS(), 10, 22);
		g.drawString("(" + location.getX() + ", " + location.getY() + ")", 10, 34);
		
		// Clean up frame buffer graphics painter
		g.dispose();

		// Create screen graphics painter
		Graphics2D g2 = (Graphics2D) getGraphics();

		// Draw frame buffer to screen
		g2.drawImage(frameBuffer, 0, 0, null);
	
		// Clean up screen graphics painter
		g2.dispose();
	}
	
	public void renderMap(Graphics2D g) {
		// just render all tiles for now
	}
	
	public void renderEntities(Graphics2D g) {
		//for(Entity e : localWorld.getEntites()) {
		//	if(e instanceof Player) {
				// This is how to render a player
		//	}
		//}
	}

	public void setScrollDirection(double newScrollDirection) {
		scrolling = true;
		scrollDirection = newScrollDirection;
	}

	public int getFPS() {
		return fps;
	}
	
	public void setFPS(int newFPS) {
		if (newFPS > 0) {
			fps = newFPS;
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// if mouse in scroll margin
		if(e.getX() < SCROLL_MARGIN ||
			e.getX() > getWidth() - SCROLL_MARGIN ||
			e.getY() < SCROLL_MARGIN ||
			e.getY() > getHeight() - SCROLL_MARGIN) {
			// scroll based on mouse location
			setScrollDirection(Math.atan2(e.getY() - getHeight() / 2, e.getX() - getWidth() / 2));
		} else {
			// stop scrolling
			scrolling = false;
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		switch(e.getButton()) {
			// Select any visible element that is selectable
			case MouseEvent.BUTTON1:
				break;
			// Attack any visible element that is attackable except for self
			case MouseEvent.BUTTON3:
				break;
			default:
				break;
		}
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseMoved(e);
	}

	// NOT USED
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
