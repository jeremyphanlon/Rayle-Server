package com.rayle.entity;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.nio.ByteBuffer;

import com.rayle.Main;
import com.rayle.combat.CombatStyle;
import com.rayle.map.Location;
import com.rayle.packet.PacketBytecodeOutgoing;

public class NPC extends Entity {

	private static final long serialVersionUID = -4800019365650310643L;
	
	private String name;
	private int id;
	private int level;
	private CombatStyle combatStyle;
	
	public NPC(int id) {
		this.id = id;
		this.location = new Location(0, 0, 0);
		
		File f = new File(Main.DB_PATH + "npcDefinitions/" + id);
		
		if (f.exists()) {
			try {
				String contents = new java.util.Scanner(f).useDelimiter("\\Z").next();
				String[] data = contents.split("\n");
				
				for (String field : data) {
					String[] s = field.split("=");
					String name = s[0].trim();
					String value = s[1].trim();
					setField(name, value);
				}
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		else {
			throw new RuntimeException("Invalid NPC ID [" + id + "]. Definition Not Found.");
		}
	}
	
	public NPC(int id, Location l) {
		this(id);
		this.location = l;
	}
	
	public String getName() {
		return name;
	}
	
	public int getID() {
		return id;
	}
	
	public int getLevel() {
		return level;
	}
	
	public CombatStyle getCombatStyle() {
		return combatStyle;
	}

	private void setField(String name, String value) {
		if (name.equalsIgnoreCase("name")) {
			this.name = value;
		}
		else if (name.equalsIgnoreCase("hp")) {
			this.hp = Integer.parseInt(value);
			this.maxHP = hp;
		}
		else if (name.equalsIgnoreCase("combat style")) {
			this.combatStyle = CombatStyle.parse(value);
		}
		else if (name.equalsIgnoreCase("level")) {
			this.level = Integer.parseInt(value);
		}
		else if (name.equalsIgnoreCase("options")) {
			this.options = value.split(",");
			
			for (int i = 0; i < this.options.length; i++) {
				this.options[i] = this.options[i].trim();
			}
			
		}
		else {
			throw new RuntimeException("Unknown field in NPC Definition [id=" + this.id + "]");
		}
	}

	@Override
	public void send(InetAddress ip) {
		ByteBuffer buf = ByteBuffer.allocate(4);
		buf.putInt(instanceID);
		Main.sendToAllInRange(PacketBytecodeOutgoing.NEW_NPC, buf.array(), getLocation());
		
		super.send(ip);
		
		buf = ByteBuffer.allocate(8);
		buf.putInt(instanceID);
		buf.putInt(this.id);
		Main.sendToAllInRange(PacketBytecodeOutgoing.NPC_ID, buf.array(), getLocation());
		buf.clear();
		buf.putInt(instanceID);
		buf.putInt(this.level);
		Main.sendToAllInRange(PacketBytecodeOutgoing.NPC_LEVEL, buf.array(), getLocation());
		buf = ByteBuffer.allocate(5);
		buf.clear();
		buf.putInt(instanceID);
		buf.put(this.combatStyle.getBytecode());
		Main.sendToAllInRange(PacketBytecodeOutgoing.NPC_COMBAT_STYLE, buf.array(), getLocation());
		
		buf = ByteBuffer.allocate(4 + this.name.length());
		buf.putInt(instanceID);
		buf.put(this.name.getBytes());
		Main.sendToAllInRange(PacketBytecodeOutgoing.NPC_NAME, buf.array(), getLocation());
	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub

	}

}
