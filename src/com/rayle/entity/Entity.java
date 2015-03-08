package com.rayle.entity;

import java.io.Serializable;
import java.net.InetAddress;
import java.nio.ByteBuffer;

import com.rayle.Main;
import com.rayle.Tickable;
import com.rayle.map.Locatable;
import com.rayle.map.Location;
import com.rayle.map.Map;
import com.rayle.packet.PacketBytecodeOutgoing;
import com.rayle.packet.Sendable;

public abstract class Entity implements Locatable, Serializable, Sendable, Tickable {

	private static final long serialVersionUID = 6752612263662741503L;
	private static final int MAX_COMBATANTS = 10;
	
	private static int CURRENT_INSTANCE_ID = 0;
	
	protected Location location;
	protected int hp, maxHP;
	protected String[] options;
	protected int instanceID = createInstanceID();
	protected Entity[] combatants = new Entity[MAX_COMBATANTS];
	
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
	
	public Entity[] getCombatants() {
		return combatants;
	}
	
	public void addCombatant(Entity e) {
		if (e.hasMaxCombatants() || this.hasMaxCombatants()) {
			//TODO ERROR REACHED MAX COMBATANTS
		}
		else {
			for (int i = 0; i < MAX_COMBATANTS; i++) {
				if (this.combatants[i] == null) {
					this.combatants[i] = e;
					break;
				}
			}
			for (int i = 0; i < MAX_COMBATANTS; i++) {
				if (e.combatants[i] == null) {
					e.combatants[i] = this;
					break;
				}
			}
		}
	}
	
	public void removeCombatant(Entity e) {
		for (int i = 0; i < MAX_COMBATANTS; i++) {
			if (this.combatants[i].instanceID == e.instanceID) {
				this.combatants[i] = null;
				break;
			}
		}
		for (int i = 0; i < MAX_COMBATANTS; i++) {
			if (e.combatants[i].instanceID == this.instanceID) {
				e.combatants[i] = null;
				break;
			}
		}
	}
	
	public boolean inCombatWith(Entity e) {
		for (Entity c : combatants) {
			if (e.instanceID == c.instanceID) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasMaxCombatants() {
		for (Entity e : combatants) {
			if (e == null) {
				return false;
			}
		}
		return true;
	}
	
	public void damage(int damage) {
		this.hp -= damage;
		ByteBuffer buf = ByteBuffer.allocate(8);
		buf.putInt(instanceID);
		buf.putInt(hp);
		Main.sendToAllInRange(PacketBytecodeOutgoing.ENTITY_HP, buf.array(), getLocation());
		if (this.hp <= 0) {
			die();
		}
	}
	
	public void die() {
		for (Entity e : combatants) {
			if (e != null) {
				e.removeCombatant(this);
			}
		}
		Map.removeEntity(this);
	}
	
	@Override
	public void sendNew(InetAddress ip) {
		ByteBuffer buf;
		buf = ByteBuffer.allocate(8);
		buf.putInt(getLocation().getX());
		buf.putInt(instanceID);
		Main.sendToAllInRange(PacketBytecodeOutgoing.ENTITY_X, buf.array(), getLocation());
		buf.clear();
		buf.putInt(getLocation().getY());
		buf.putInt(instanceID);
		Main.sendToAllInRange(PacketBytecodeOutgoing.ENTITY_Y, buf.array(), getLocation());
		buf.clear();
		buf.putInt(getLocation().getZ());
		buf.putInt(instanceID);
		Main.sendToAllInRange(PacketBytecodeOutgoing.ENTITY_Z, buf.array(), getLocation());
		buf.clear();
		buf.putInt(getHp());
		buf.putInt(instanceID);
		Main.sendToAllInRange(PacketBytecodeOutgoing.ENTITY_HP, buf.array(), getLocation());
		buf.clear();
		buf.putInt(getMaxHP());
		buf.putInt(instanceID);
		Main.sendToAllInRange(PacketBytecodeOutgoing.ENTITY_MAX_HP, buf.array(), getLocation());
	}
	
}
