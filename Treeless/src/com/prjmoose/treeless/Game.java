package com.prjmoose.treeless;

public class Game {
	// World
	private long startTime;

	private Map map;
	//private ArrayList<Entity> entities;

	public Game() {
		// Init world
		startTime = System.currentTimeMillis() + 10000;
		
		//map = new Map(this, "flatland");
		//entities = new ArrayList<Entity>();
		
		//entities.add(new Player("svc"));
		for(int i = 0; i < 7; i++) {
			//entities.add(new Player("random"));
		}
	}

	public void update(long deltaTime) {
		
		// TODO Update entities and check collisions and stuff
	}

	public long getStartTime() {
		return startTime;
	}
}
