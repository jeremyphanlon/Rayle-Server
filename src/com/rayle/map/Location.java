package com.rayle.map;

import java.awt.Point;

public class Location {

	private int x, y, z;
	
	public Location(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getZ() {
		return z;
	}
	
	public double distance(Location l) {
		return new Point(x, y).distance(new Point(l.x, l.y));
	}
	
	public boolean sameHeight(Location l) {
		return (z == l.z);
	}
	
}
