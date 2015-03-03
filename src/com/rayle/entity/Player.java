package com.rayle.entity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Scanner;

import com.rayle.Main;
import com.rayle.db.Saveable;
import com.rayle.packet.PacketBytecodeOutgoing;
import com.rayle.packet.Sendable;

public class Player extends Entity implements Saveable<Player>, Sendable {
	
	public static final String ACCOUNT_DB_PATH = com.rayle.Main.DB_PATH + "accounts/";

	private static final long serialVersionUID = 1847254033253881089L;
	
	private String name;
	private transient InetAddress ip;
	
	public String getName() {
		return name;
	}
	
	public void setName(String s) {
		this.name = s;
	}
	
	public InetAddress getIP() {
		return ip;
	}
	
	public void sendMessage(String s) {
		com.rayle.Main.send(ip, PacketBytecodeOutgoing.SEND_MESSAGE, s.getBytes());
	}
	
	public void save() {
		try
	      {
	         FileOutputStream fileOut =
	         new FileOutputStream(ACCOUNT_DB_PATH + this.name + "/data");
	         ObjectOutputStream out = new ObjectOutputStream(fileOut);
	         out.writeObject(this);
	         out.close();
	         fileOut.close();
	      }catch(IOException i)
	      {
	          i.printStackTrace();
	      }
	}
	
	public Player load() {
		try
	      {
	         FileInputStream fileIn = new FileInputStream(ACCOUNT_DB_PATH + this.name + "/data");
	         ObjectInputStream in = new ObjectInputStream(fileIn);
	         Player p = (Player) in.readObject();
	         in.close();
	         fileIn.close();
	         
	         return p;
	      }catch(Exception e)
	      {
	         e.printStackTrace();
	      }
		
		return null;
	}
	

	@Override
	public void send(InetAddress ip) {
		ByteBuffer buf = ByteBuffer.allocate(4);
		buf.putInt(getInstanceID());
		Main.send(ip, PacketBytecodeOutgoing.NEW_PLAYER, buf.array());
		
		buf = ByteBuffer.allocate(8);
		buf.putInt(getX());
		buf.putInt(instanceID);
		Main.send(ip, PacketBytecodeOutgoing.ENTITY_X, buf.array());
		buf.clear();
		buf.putInt(getY());
		buf.putInt(instanceID);
		Main.send(ip, PacketBytecodeOutgoing.ENTITY_Y, buf.array());
		buf.clear();
		buf.putInt(getZ());
		buf.putInt(instanceID);
		Main.send(ip, PacketBytecodeOutgoing.ENTITY_Z, buf.array());
		buf.clear();
		buf.putInt(getHp());
		buf.putInt(instanceID);
		Main.send(ip, PacketBytecodeOutgoing.ENTITY_HP, buf.array());
		buf.clear();
		buf.putInt(getMaxHP());
		buf.putInt(instanceID);
		Main.send(ip, PacketBytecodeOutgoing.ENTITY_MAX_HP, buf.array());
		buf = ByteBuffer.allocate(4 + this.name.length());
		buf.putInt(instanceID);
		buf.put(this.name.getBytes());
		Main.send(ip, PacketBytecodeOutgoing.PLAYER_NAME, buf.array());
	}
	
	public static Player getDefaultPlayer() {
		Player p = new Player();
		p.name = "<Default Player>";
		p.hp = 10;
		p.maxHP = 10;
		p.x = 0;
		p.y = 0; 
		p.z = 0;
		return p;
	}
	
	public static void createAccount(String username, String password, InetAddress ip) {
		if ((username.matches(".+") && password.matches(".+")) == false) {
			Main.send(ip, PacketBytecodeOutgoing.CREATE_ACCOUNT_ERROR, ("Username must be between 3 and 20 characters and contain only alphanumeric characters and underscores. Password must be between 3 and 20 characters.").getBytes());
		}
		else if (new File(ACCOUNT_DB_PATH + username).exists()) {
			Main.send(ip, PacketBytecodeOutgoing.CREATE_ACCOUNT_ERROR, ("An account with the name '" + username + "' already exists.").getBytes());
		}
		else {
			
			File f = new File(ACCOUNT_DB_PATH + username);
			f.mkdirs();
			File pass = new File(ACCOUNT_DB_PATH + username + "/password");
			try {
				FileOutputStream fos = new FileOutputStream(pass);
				fos.write(password.getBytes());
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
			Player p = getDefaultPlayer();
			p.name = username;
			p.save();
			Main.send(ip, PacketBytecodeOutgoing.CREATE_ACCOUNT_SUCCESS, ("Account created successfully. Please login.").getBytes());
		}
	}
	
	public static Player login(String username, String password, InetAddress ip) {	
		File f = new File(ACCOUNT_DB_PATH + username + "/password");
		if (f.exists()) {
			try {
				String savedPassword = new Scanner(f).useDelimiter("\\Z").next();
				
				if (savedPassword.equals(password)) {
					Main.send(ip, PacketBytecodeOutgoing.SUCCESSFUL_LOGIN, new byte[0]);
					Player p = new Player();
					p.name = username;
					Player player = p.load();
					player.ip = ip;
					player.send(ip);
					return player;
				}
				else {
					Main.send(ip, PacketBytecodeOutgoing.FAILED_LOGIN, ("Invalid username or password.").getBytes());
					return null;
				}
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return null;
			}
		}
		else {
			Main.send(ip, PacketBytecodeOutgoing.FAILED_LOGIN, ("The account '" + username + "' doesn't exist.").getBytes());
			return null;
		}
	}

}
