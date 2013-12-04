package com.prjmoose.treeless;

public class Point3D {
	public double x;
	public double y;
	public double z;

	public Point3D() {
		this(0, 0, 0);
	}

	public Point3D(Point3D p) {
		this(p.x, p.y, p.z);
	}

	public Point3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}
	
	public void move(Point3D p) {
		move(p.x, p.y, p.z);
	}

	public void move(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void translate(Point3D p) {
		translate(p.x, p.y, p.z);
	}

	public void translate(double dx, double dy, double dz) {
		this.x += dx;
		this.y += dy;
		this.z += dz;
	}

	public double distance(Point3D pt) {
		return distance(pt.getX(), pt.getY(), pt.getZ());
	}

	public double distance(double px, double py, double pz) {
		px -= getX();
		py -= getY();
		pz -= getZ();
		return Math.sqrt(px * px + py * py + pz * pz);
	}

	public boolean equals(Object obj) {
		if (obj instanceof Point3D) {
			Point3D pt = (Point3D) obj;
			return (getX() == pt.getX()) && (getY() == pt.getY()) && (getZ() == pt.getZ());
		}
		return super.equals(obj);
	}

	public String toString() {
		return getClass().getName() + "[x=" + x + ",y=" + y + ",z=" + z + "]";
	}
}