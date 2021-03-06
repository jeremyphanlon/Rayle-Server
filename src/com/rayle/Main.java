package com.rayle;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

import com.rayle.entity.Player;
import com.rayle.map.Locatable;
import com.rayle.map.Location;
import com.rayle.map.Map;
import com.rayle.packet.PacketBytecodeIncoming;
import com.rayle.packet.PacketBytecodeOutgoing;
import com.rayle.packet.Sendable;

import static com.rayle.packet.PacketBytecodeIncoming.*;

public class Main {
	
	public static final String DB_PATH = System.getProperty("user.home") + "/Desktop/.rayleServer/";
	
	private static final long TICK_TIME = 100; //ms
	private static final int PORT = 2632;
	private static final int CLIENT_PORT = 2631;
	private static final int PACKET_SIZE = 1024;
	
	private static final int MAX_PLAYERS = 2048;
	public static volatile Player[] PLAYERS = new Player[MAX_PLAYERS];
	
	private static volatile ArrayList<DatagramPacket> PACKETS_TO_SEND = new ArrayList<DatagramPacket>();
	private static volatile ArrayList<DatagramPacket> PACKETS_TO_RECEIVE = new ArrayList<DatagramPacket>();
	
	private static DatagramSocket SOCKET;
		static {
			try {
				SOCKET = new DatagramSocket(PORT);
			} catch (SocketException e) {
				e.printStackTrace();
			}
		}
	private static Thread RECEIVE_THREAD;
	private static Thread SEND_THREAD;
	private static Thread RECEIVE_HANDLER_THREAD;

	public static void main(String[] args) {
		startServer();
		
		while (true) {
			long startTime = System.currentTimeMillis();
			processTick();
			long sleepTime = TICK_TIME-(System.currentTimeMillis()-startTime);
			
			if (sleepTime > 0) {
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private static void startServer() {
		RECEIVE_THREAD = new Thread(new Runnable() {

			@Override
			public void run() {
				
		            while (true) {
		            	DatagramPacket packet = new DatagramPacket( new byte[PACKET_SIZE], PACKET_SIZE ) ;

			            try {
							SOCKET.receive(packet) ;
						} catch (IOException e) {
							e.printStackTrace();
						}

			            PACKETS_TO_RECEIVE.add(packet);
		            }
		           
			}
			
		});
		RECEIVE_THREAD.start();
		
		RECEIVE_HANDLER_THREAD = new Thread(new Runnable() {

			@Override
			public void run() {
				
				while (true) {
					if (PACKETS_TO_RECEIVE.size() > 0) {
						DatagramPacket packet = PACKETS_TO_RECEIVE.get(0);
						PACKETS_TO_RECEIVE.remove(0);

						packetReceived(packet);

					}
				}
				
			}
			
		});
		RECEIVE_HANDLER_THREAD.start();
		
		SEND_THREAD = new Thread(new Runnable() {

			@Override
			public void run() {
				
				while (true) {
					if (PACKETS_TO_SEND.size() > 0) {
						DatagramPacket packet = PACKETS_TO_SEND.get(0);
						PACKETS_TO_SEND.remove(0);
						try {
							SOCKET.send(packet);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				
			}
			
		});
		SEND_THREAD.start();
	}
		
	private synchronized static void packetReceived(DatagramPacket packet) {
		
		InetAddress ip = packet.getAddress();
		
		byte[] packetBytecode = new byte[PacketBytecodeIncoming.length];
		byte[] data = new byte[packet.getData().length - PacketBytecodeIncoming.length];
		
		for (int i = 0; i < PacketBytecodeIncoming.length; i++) {
			packetBytecode[i] = packet.getData()[i];
		}
		
		for (int i = PacketBytecodeIncoming.length; i < packet.getData().length; i++) {
			data[i - PacketBytecodeIncoming.length] = packet.getData()[i];
		}
		
		PacketBytecodeIncoming pb = PacketBytecodeIncoming.read(packetBytecode);
		
		if (LOGIN_ATTEMPT.equals(pb)) {
			String[] str = new String(data).split(":");
			String username = str[0];
			String password = str[1];

			Player p = Player.login(username, password, ip);
			
			if (p != null) {
				addPlayer(p);
			}
		}
		else if (CREATE_ACCOUNT_ATTEMPT.equals(pb)) {
			String[] str = new String(data).split(":");
			String username = str[0];
			String password = str[1];
			
			Player.createAccount(username, password, ip);
		}
		
		
	}
	
	public static Player getPlayerByIP(InetAddress ip) {
		for (Player p : PLAYERS) {
			if (p != null) {
				if (p.getIP().equals(ip)) {
					return p;
				}
			}
		}
		return null;
	}
	
	public static boolean isServerFull() {
		for (int i = 0; i < PLAYERS.length; i++) {
			if (PLAYERS[i] == null) {
				return false;
			}
		}
		return true;
	}
	
	public static void addPlayer(Player p) {
		for (int i = 0; i < PLAYERS.length; i++) {
			if (PLAYERS[i] == null) {
				PLAYERS[i] = p;
				return;
			}
		}
	}
	
	public static void removePlayer(Player p) {
		for (int i = 0; i < PLAYERS.length; i++) {
			if ((PLAYERS[i] != null) && (PLAYERS[i].getName().equals(p.getName()))) {
				PLAYERS[i] = null;
				return;
			}
		}
	}

	private static void processTick() {
		for (Player p : PLAYERS) {
			if (p != null) {
				p.tick();
			}
		}
	}
	
	public static void send(DatagramPacket packet) {
		PACKETS_TO_SEND.add(packet);
	}
	
	public static void send(InetAddress ip, PacketBytecodeOutgoing pb, byte[] data) {
		byte[] b = new byte[PacketBytecodeOutgoing.length + data.length];
		
		for (int i = 0; i < PacketBytecodeOutgoing.length; i++) {
			b[i] = pb.getData()[i];
		}
		for (int i = 0; i < data.length; i++) {
			b[i+PacketBytecodeOutgoing.length] = data[i];
		}
		
		send(new DatagramPacket(b, b.length, ip, CLIENT_PORT));
	}
	
	public static void sendToAll(PacketBytecodeOutgoing pb, byte[] data) {
		for (Player p : PLAYERS) {
			if (p != null) {
				send(p.getIP(), pb, data);
			}
		}
	}
	
	public static void sendToAllInRange(PacketBytecodeOutgoing pb, byte[] data, Location l) {
		for (Player p : PLAYERS) {
			if (p != null) {
				if (p.getLocation().distance(l) <= Map.BUFFER_DISTANCE) {
					send(p.getIP(), pb, data);
				}
			}
		}
	}
	
	public static void sendNewToAllInRange(Sendable s, Locatable l) {
		for (Player p : PLAYERS) {
			if (p != null) {
				if (p.getLocation().distance(l.getLocation()) <= Map.BUFFER_DISTANCE) {
					s.sendNew(p.getIP());
				}
			}
		}
	}
	
}
