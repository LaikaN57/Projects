package com.prjmoose.treeless.views;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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
import com.prjmoose.treeless.Point3D;
import com.prjmoose.treeless.entities.Entity;
import com.prjmoose.treeless.entities.Player;
import com.prjmoose.treeless.main.Utils;

public class PlayerInterfacePanel extends JPanel implements Runnable, KeyListener, MouseListener, MouseMotionListener {
	public static final int DEFAULT_WIDTH = 800;
	public static final int DEFAULT_HEIGHT = 640;
	public static final int DEFUALT_FPS = 30;

	public static final int SCROLL_MARGIN = 50;
	public static final double SCROLL_VELOCITY = 20;
	
	public static final int VISIBLE_LIGHT_LEVEL = 0;
	public static final int LOW_LIGHT_LEVEL = 127;
	public static final int HIDDEN_LIGHT_LEVEL = 255;
	
	public static final int MINI_MAP_SIZE = 256;

	private Point location;
	
	private int fps;

	private boolean scrolling;
	private double scrollDirection;

	private volatile Thread thisThread;

	// Local World
	private Game localWorld;
	
	// loaded graphics
	private BufferedImage[] mapSprites;
	private BufferedImage miniMapSprite;
	
	private BufferedImage cachedMap;

	private long lastDeltaTime;
	
	private Entity selectedEntity;
	
	private boolean debugHudEnabled;

	/**
	 * Create the panel.
	 */
	public PlayerInterfacePanel(Game localWorld) {
		super();

		this.localWorld = localWorld;
		
		location = new Point(0, 0);
		fps = DEFUALT_FPS;
		scrolling = false;
		debugHudEnabled = true;

		setIgnoreRepaint(true);
		setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

		setFocusable(true);
		requestFocus();

		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		
		try {
			mapSprites = localWorld.getMap().loadSpriteSheet();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    public Thread start() {
    	if(thisThread != null) {
    		return thisThread;
    	}

    	thisThread = new Thread(this);
    	thisThread.start();
    	
    	return thisThread;
    }

	@Override
	public void run() {
		long lastTime;
		long startTime;
		long deltaTime;
		long endTime;
		long totalTime;
		long waitTime;
		long overTime = 0;
		
		startTime = System.nanoTime();

		while (thisThread == Thread.currentThread()) {
	        try {
				// Start loop timer
	        	lastTime = startTime;
				startTime = System.nanoTime();
				deltaTime = startTime - lastTime;

				if(isShowing()) {
					lastDeltaTime = deltaTime;
					
					// do scroll
					if(scrolling) {
						double x = location.getX() + Math.cos(scrollDirection) * SCROLL_VELOCITY;
						double y = location.getY() + Math.sin(scrollDirection) * SCROLL_VELOCITY;
						
						location.setLocation(x, y);
						
						cachedMap = null;
					}
					
					// fix x if map is smaller or if map went off edge
					if(localWorld.getMap().getWidth() * localWorld.getMap().getTileSize() < getWidth()) {
						location.x = -(getWidth() - localWorld.getMap().getWidth() * localWorld.getMap().getTileSize()) / 2;
						
						cachedMap = null;
					} else if(location.x < 0) {
						location.x = 0;
						
						cachedMap = null;
					} else if(location.x + getWidth() > localWorld.getMap().getWidth() * localWorld.getMap().getTileSize()) {
						location.x = localWorld.getMap().getWidth() * localWorld.getMap().getTileSize() - getWidth();
						
						cachedMap = null;
					}

					// fix y if map is smaller or if map went off edge
					if(localWorld.getMap().getHeight() * localWorld.getMap().getTileSize() < getHeight()) {
						location.y = -(getHeight() - localWorld.getMap().getHeight() * localWorld.getMap().getTileSize()) / 2;
						
						cachedMap = null;
					} else if(location.y < 0) {
						location.y = 0;
						
						cachedMap = null;
					} else if(location.y + getHeight() > localWorld.getMap().getHeight() * localWorld.getMap().getTileSize()) {
						location.y = localWorld.getMap().getHeight() * localWorld.getMap().getTileSize() - getHeight();
						
						cachedMap = null;
					}
					
					// clear map cache is size changed
					if((cachedMap != null) && (cachedMap.getWidth() != getWidth() || cachedMap.getHeight() != getHeight())) {
						cachedMap = null;
					}

					render();
				}

				// End loop timer
				endTime = System.nanoTime();
	
				// Calculate waitTime
				totalTime = (endTime - startTime) / 1000000;
				waitTime = 1000 / getFPS() - totalTime;
	
				// Protect from negative waitTimes
				if(waitTime < 0) {
					overTime += -waitTime;
					System.out.printf("CameraLoop took %d ms (%d ms over target). Total over time: %d ms\n", totalTime, -waitTime, overTime);
					continue;
				} else if(waitTime > 0 && overTime > 0) {
					waitTime--;
					overTime--;
				}

				// Wait for the rest of the update period
				try {
					Thread.sleep(waitTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// FIXME This is supposed to be InterruptedException
	        } catch (Exception e){
				e.printStackTrace();
	        }
		}
	}

	public void stop() {
		thisThread = null;
	}

	public Thread getThread() {
		return thisThread;
	}

	public void render() {
		// Create frame buffer and graphics painter
		BufferedImage frameBuffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) frameBuffer.getGraphics();
		
		// blank screen
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		// draw map
		// render a new map
		if(cachedMap == null) {
			cachedMap = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
			renderMap(g);
		}
		
		// draw the cached map
		g.drawImage(cachedMap, 0, 0, null);
		
		// draw entities by depth
		renderEntities(g);
		
		// render sight
		
		// draw player hud
		renderPlayerHud(g);

		// draw debug hud
		if(debugHudEnabled) {
			renderDebugHud(g);
		}
		
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
		Graphics2D mapPainter = (Graphics2D) cachedMap.getGraphics();

		if(mapSprites != null) {
			int xStart = location.x / localWorld.getMap().getTileSize();
			int yStart = location.y / localWorld.getMap().getTileSize();
			int xEnd = Math.min((location.x + getWidth()) / localWorld.getMap().getTileSize() + 1, localWorld.getMap().getWidth());
			int yEnd = Math.min((location.y + getHeight()) / localWorld.getMap().getTileSize() + 1, localWorld.getMap().getHeight());
			
			// TODO check for map smaller than view port
			
			for(int y = yStart; y < yEnd; y++) {
				for(int x = xStart; x < xEnd; x++) {
					mapPainter.drawImage(mapSprites[localWorld.getMap().getTileAt(x, y).getSpriteIndex()],
							x * localWorld.getMap().getTileSize() - location.x,
							y * localWorld.getMap().getTileSize() - location.y,
							null);
				}
			}
		} else {
			mapPainter.setColor(Color.MAGENTA);
			mapPainter.fillRect(0, 0, getWidth(), getHeight());
		}
		
		mapPainter.dispose();
	}
	
	public void renderEntities(Graphics2D g) {
		for(Entity e : localWorld.getEntites()) {
			// TODO check if entity is even on screen
			
			if(e instanceof Player) {
				// draw selection box
				if(e.equals(selectedEntity)) {
					g.setColor(Color.BLUE);
					g.setStroke(new BasicStroke(3));
					g.drawOval(
							(int) (e.getLocation().getX() - e.getRadius() - location.x),
							(int) (e.getLocation().getY() - e.getRadius() - location.y),
							(int) (e.getRadius() * 2), (int) (e.getRadius() * 2 * 0.9));
					// TODO fix this math
	
					g.setStroke(new BasicStroke(1));	// RESET Stroke
				}

				// draw magenta box at player x y z
				g.setColor(Color.MAGENTA);
				g.fillRect(
						(int) (e.getLocation().getX() - e.getRadius() - location.x),
						(int) (e.getLocation().getY() - e.getLocation().getZ() - e.getHeight() - location.y),
						(int) (e.getRadius() * 2), (int) (e.getHeight()));
				
			}
		}
	}
	
	public void renderMiniMap(Graphics2D g) {
		// scale the map and view
		int scale = Math.max(localWorld.getMap().getWidth() * localWorld.getMap().getTileSize(), localWorld.getMap().getHeight() * localWorld.getMap().getTileSize()) / MINI_MAP_SIZE;
		
		// map
		int mapW = localWorld.getMap().getWidth() * localWorld.getMap().getTileSize() / scale;
		int mapH = localWorld.getMap().getHeight() * localWorld.getMap().getTileSize() / scale;
		int mapX = (MINI_MAP_SIZE - mapW) / 2;
		int mapY = (MINI_MAP_SIZE - mapH) / 2;
		
		// view
		int viewX = location.x / scale;
		int viewY = location.y / scale;
		int viewW = getWidth() / scale;
		int viewH = getHeight() / scale;
		
		// draw minimap frame
		g.setColor(Color.MAGENTA);
		g.fillRect(getWidth() - MINI_MAP_SIZE, getHeight() - MINI_MAP_SIZE, MINI_MAP_SIZE, MINI_MAP_SIZE);
		
		// draw total map area
		g.setColor(Color.BLACK);
		g.fillRect(getWidth() - MINI_MAP_SIZE + mapX, getHeight() - MINI_MAP_SIZE + mapY, mapW, mapH);
		
		// draw viewable area
		g.setColor(Color.WHITE);
		g.setStroke(new BasicStroke(3));
		g.drawRect(getWidth() - MINI_MAP_SIZE + 1 + mapX + viewX, getHeight() - MINI_MAP_SIZE + 1 + mapY + viewY, viewW - 3, viewH - 3); // TODO Check stroke
		
		g.setStroke(new BasicStroke(1));
	}
	
	public void renderPlayerHud(Graphics2D g) {
		if(localWorld.getStartTime() >= System.currentTimeMillis()) {
			g.setColor(Color.RED);
			g.setFont(new Font(null, Font.BOLD, 72));
			g.drawString("PAUSED", (getWidth() - 300) / 2, (getHeight()) / 2);
			
			g.setFont(new Font(null));
			// TODO fix reset
		}
		
		renderMiniMap(g);
	}

	public void renderDebugHud(Graphics2D g) {
		// draw scroll direction
		if(scrolling) {
			double x = Math.cos(scrollDirection) * 100;
			double y = Math.sin(scrollDirection) * 100;

			g.setColor(Color.RED);
			g.drawLine(getWidth() / 2, getHeight() / 2, (int) (getWidth() / 2 + x), (int) (getHeight() / 2 + y));
		}
		
		// draw hud
		g.setColor(Color.LIGHT_GRAY);
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
		g.setColor(new Color(0, 0, 0, 128));
		g.fillRect(0, 0, getWidth(), 100);
		
		g.setColor(Color.white);
		g.drawString("Window size: " + getWidth() + " x " + getHeight() + " @ " + getFPS(), 2, 15);
		g.drawString("Cursor location: (" + location.getX() + ", " + location.getY() + ")", 2, 31);
		g.drawString("Map size (tiles): " + localWorld.getMap().getWidth() + " x " + localWorld.getMap().getHeight() + " @ " + localWorld.getMap().getTileSize(), 2, 47);
		g.drawString("Map size (pixels): " + localWorld.getMap().getWidth() * localWorld.getMap().getTileSize() + " x " + localWorld.getMap().getHeight() * localWorld.getMap().getTileSize(), 2, 63);
		g.drawString("Time delta: " + lastDeltaTime / 1000000, 2, 79);		
		g.drawString("Mem: " + (long) Math.floor(Runtime.getRuntime().totalMemory() / 1024) + " KB", 2, 95);
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
		//System.out.println(e);

		switch(e.getButton()) {
			// Select any visible element that is selectable
			case MouseEvent.BUTTON1:
				// if hit mini map bounding box
				if(e.getX() >= getWidth() - MINI_MAP_SIZE &&
						e.getX() < getWidth() &&
						e.getY() >= getHeight() - MINI_MAP_SIZE &&
						e.getY() < getHeight()) {
					
					// scale the map and view
					int scale = Math.max(localWorld.getMap().getWidth() * localWorld.getMap().getTileSize(), localWorld.getMap().getHeight() * localWorld.getMap().getTileSize()) / MINI_MAP_SIZE;

					// map
					int mapW = localWorld.getMap().getWidth() * localWorld.getMap().getTileSize() / scale;
					int mapH = localWorld.getMap().getHeight() * localWorld.getMap().getTileSize() / scale;
					int mapX = (MINI_MAP_SIZE - mapW) / 2;
					int mapY = (MINI_MAP_SIZE - mapH) / 2;
					
					// if hit actual mini map (not just the map frame)
					if(e.getX() >= getWidth() - MINI_MAP_SIZE + mapX &&
							e.getX() < getWidth() - MINI_MAP_SIZE + mapX + mapW &&
							e.getY() >= getHeight() - MINI_MAP_SIZE + mapY &&
							e.getY() < getHeight() - MINI_MAP_SIZE + mapY + mapH) {

						int hitX = e.getX() - (getWidth() - MINI_MAP_SIZE + mapX);
						int hitY = e.getY() - (getHeight() - MINI_MAP_SIZE + mapY);

						// FIXME Screen flicker when clicking in movement region.
						location.setLocation(
								Utils.limit(hitX * scale - getWidth()/2, 0, localWorld.getMap().getWidth() * localWorld.getMap().getTileSize() - getWidth()),
								Utils.limit(hitY * scale - getHeight()/2, 0, localWorld.getMap().getHeight() * localWorld.getMap().getTileSize() - getHeight()));
						cachedMap = null;
					}
				}
				break;
			// Attack any visible element that is attackable except for self
			case MouseEvent.BUTTON3:
				((Player) localWorld.getEntites().get(0)).setTargetLocation(new Point3D(e.getX() + location.getX(), e.getY() + location.getY(), 0));
				break;
			default:
				break;
		}
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		System.out.println(e);
		
		switch(e.getKeyCode()) {
			// Enable/Disable DebugHud
			case KeyEvent.VK_BACK_QUOTE:
				debugHudEnabled = !debugHudEnabled;
				break;
			default:
				break;
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseMoved(e);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		mouseMoved(e);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		mouseMoved(e);
	}

	// NOT USED
	@Override
	public void mouseClicked(MouseEvent arg0) {
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
