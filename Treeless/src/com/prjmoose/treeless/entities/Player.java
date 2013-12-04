package com.prjmoose.treeless.entities;

import com.prjmoose.treeless.Point3D;

public class Player extends Entity {
	private Point3D targetLocation;
	
	private double velocity = 5;
	
	public Player() {
		location = new Point3D(67, 34, 0);
		radius = 24;
		height = 64;
	}
	
	public void update(long deltaTime) {
		// compute new direction
		if(targetLocation != null) {
			if(location.distance(targetLocation) < velocity) {
				location.move(targetLocation);
				targetLocation = null;
			} else {
				// calc direction
				direction = Math.atan2(targetLocation.getY() - location.getY(), targetLocation.getX() - location.getX());
				location.translate(Math.cos(direction) * velocity, Math.sin(direction) * velocity, 0);
			}
		}
	}

	public void setTargetLocation(Point3D pt) {
		targetLocation = pt;
	}
}
