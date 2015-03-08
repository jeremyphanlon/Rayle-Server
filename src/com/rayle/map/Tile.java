package com.rayle.map;

import com.rayle.entity.Entity;

public class Tile {
	
	private static final int MAX_ENTITIES = 20;
	
	private Entity[] entities = new Entity[MAX_ENTITIES];
	
	public void addEntity(Entity e) {
		if (hasMaxEntities()) {
			//TODO Error Entity cannot be added here
		}
		for (int i = 0; i < MAX_ENTITIES; i++) {
			if (entities[i] == null) {
				entities[i] = e;
				return;
			}
		}
	}
	
	public void removeEntity(Entity e) {
		for (int i = 0; i < MAX_ENTITIES; i++) {
			if (entities[i].getInstanceID() == e.getInstanceID()) {
				entities[i] = null;
				return;
			}
		}
	}
	
	public boolean hasMaxEntities() {
		for (int i = 0; i < MAX_ENTITIES; i++) {
			if (entities[i] == null) {
				return true;
			}
		}
		return false;
	}
	
}
