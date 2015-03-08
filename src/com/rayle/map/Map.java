package com.rayle.map;

import com.rayle.entity.Entity;

public class Map {

	public static final int BUFFER_DISTANCE = 50;
	
	private static final int X_SIZE = 1000;
	private static final int Y_SIZE = 1000;
	private static final int Z_SIZE = 1;
	
	private static Tile[][][] TILES = new Tile[X_SIZE][Y_SIZE][Z_SIZE];

	public static Location getNewPlayerStartLocation() {
		return new Location(0, 0, 0);
	}
	
	public static void removeEntity(Entity e) {
		Location l = e.getLocation();
		TILES[l.getX()][l.getY()][l.getZ()].removeEntity(e);
	}
	
}
