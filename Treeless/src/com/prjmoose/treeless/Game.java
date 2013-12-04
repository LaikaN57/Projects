package com.prjmoose.treeless;

import java.util.ArrayList;

import com.prjmoose.treeless.entities.Entity;
import com.prjmoose.treeless.entities.Player;

public class Game {
	// World
	private long startTime;

	private Map map;
	private ArrayList<Entity> entities;

	public Game() {
		// Init world
		startTime = System.currentTimeMillis() + 3000;
		
		map = new Map("flatland");
		entities = new ArrayList<Entity>();
		
		entities.add(new Player());
		for(int i = 0; i < 7; i++) {
			//entities.add(new Player("random"));
		}
	}

	public void update(long deltaTime) {
		for(Entity e : entities) {
			e.update(deltaTime);
			
			// TODO check for players that are falling off the map
		}
	}

	public long getStartTime() {
		return startTime;
	}

	public Map getMap() {
		return map;
	}

	public ArrayList<Entity> getEntites() {
		return entities;
	}
}
