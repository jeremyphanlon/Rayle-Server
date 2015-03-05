package com.rayle.entity;

import java.io.Serializable;

import com.rayle.Tickable;
import com.rayle.map.Locatable;
import com.rayle.map.Location;
import com.rayle.packet.Sendable;

public abstract class Entity implements Locatable, Serializable, Sendable, Tickable {

	private static final long serialVersionUID = 6752612263662741503L;
	
	private static int CURRENT_INSTANCE_ID = 0;
	
	protected Location location;
	protected int hp, maxHP;
	protected String[] options;
	protected int instanceID = createInstanceID();
	
	protected int createInstanceID() {
		CURRENT_INSTANCE_ID++;
		return CURRENT_INSTANCE_ID;
	}

	public Location getLocation() {
		return location;
	}
	
	public void setLocation(Location location) {
		this.location = location;
	}

	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public int getMaxHP() {
		return maxHP;
	}

	public void setMaxHP(int maxHP) {
		this.maxHP = maxHP;
	}
	
	public String[] getOptions() {
		return options;
	}
	
	public void setOptions(String[] options) {
		this.options = options;
	}

	public int getInstanceID() {
		return instanceID;
	}

	public void setInstanceID(int instanceID) {
		this.instanceID = instanceID;
	}
	

	
}
