package com.rayle.entity;

import java.io.Serializable;

import com.rayle.map.Locatable;

public abstract class Entity implements Locatable, Serializable {

	private static final long serialVersionUID = 6752612263662741503L;
	
	private static int CURRENT_INSTANCE_ID = 0;
	
	protected int x, y, z;
	protected int hp, maxHP;
	protected String[] options;
	protected int instanceID = createInstanceID();
	
	protected int createInstanceID() {
		CURRENT_INSTANCE_ID++;
		return CURRENT_INSTANCE_ID;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
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
