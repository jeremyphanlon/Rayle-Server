package com.rayle.packet;

public enum PacketBytecodeIncoming {

	LOGIN_ATTEMPT(0, 1),
	CREATE_ACCOUNT_ATTEMPT(10, 0);

	
	
	public static int length = 2;
	
	byte b1;
	byte b2;
	
	PacketBytecodeIncoming(int b1, int b2) {
		this.b1 = (byte) b1;
		this.b2 = (byte) b2;
	}
	
	public byte[] getData() {
		return new byte[]{b1, b2};
	}
	
	public boolean matchesData(byte[] data) {
		if (data.length != length) {
			return false;
		}
		else {
			
			for (int i = 0; i < length; i++) {
				if (data[i] != getData()[i]) {
					return false;
				}
			}
			
			return true;
		}
	}
	
	public static PacketBytecodeIncoming read(byte[] packetBytecode) {
		if (packetBytecode.length != length) {
			return null;
		}
		else {
			for (PacketBytecodeIncoming pb : PacketBytecodeIncoming.values()) {
				if (pb.matchesData(packetBytecode)) {
					return pb;
				}
			}
		}
		
		return null;
	}
	
	
}
