package com.prjmoose.treeless;

import java.util.ArrayList;

import com.prjmoose.treeless.entities.Entity;

public class Game {
	// World
	private long startTime;

	private Map map;
	private ArrayList<Entity> entities;

	public Game() {
		// Init world
		startTime = System.currentTimeMillis() + 10000;
		
		map = new Map("flatland");
		//entities = new ArrayList<Entity>();
		
		//entities.add(new Player("svc"));
		for(int i = 0; i < 7; i++) {
			//entities.add(new Player("random"));
		}
	}

	public void update(long deltaTime) {
		
	}

	public long getStartTime() {
		return startTime;
	}

	public Map getMap() {
		return map;
	}
}
