package com.rayle;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

import com.rayle.entity.Player;
import com.rayle.packet.PacketBytecodeIncoming;
import com.rayle.packet.PacketBytecodeOutgoing;

import static com.rayle.packet.PacketBytecodeIncoming.*;

public class Main {
	
	public static final String DB_PATH = "/Users/jeremy/Desktop/.rayleServer/";
	
	private static final long TICK_TIME = 100; //ms
	private static final int PORT = 2632;
	private static final int CLIENT_PORT = 2631;
	private static final int PACKET_SIZE = 1024;
	
	public static volatile ArrayList<Player> PLAYERS = new ArrayList<Player>();
	
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
				PLAYERS.add(p);
			}
		}
		else if (CREATE_ACCOUNT_ATTEMPT.equals(pb)) {
			String[] str = new String(data).split(":");
			String username = str[0];
			String password = str[1];
			
			Player.createAccount(username, password, ip);
		}
		
		
	}

	private static void processTick() {
		//TODO
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
	
}
