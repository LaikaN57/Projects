package com.prjmoose.treeless.entities;

import com.prjmoose.treeless.Point3D;

public abstract class Entity {
	protected Point3D location;	// z is relative to the bottom of the entity (ie the foot or wheels)

	// r is w and d
	protected double radius;
	protected double height;
	protected double direction;
	
	public Point3D getLocation() {
		return location;
	}
	
	public double getRadius() {
		return radius;
	}
	
	public double getHeight() {
		return height;
	}
	
	public double getDirection() {
		return direction;
	}
	
	public abstract void update(long deltaTime);
}
