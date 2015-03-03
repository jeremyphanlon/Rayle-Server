package com.rayle.packet;

public enum PacketBytecodeOutgoing {

	SEND_MESSAGE(0, 0),
	FAILED_LOGIN(1, 0), SUCCESSFUL_LOGIN(1, 1),
	
	ENTITY_HP(2, 0), ENTITY_MAX_HP(2, 1), ENTITY_X(2, 2), ENTITY_Y(2, 3), ENTITY_Z(2, 4),
		NEW_PLAYER(3, 0), PLAYER_NAME(3, 1),
	
	CREATE_ACCOUNT_ERROR(10, 0), CREATE_ACCOUNT_SUCCESS(10, 1);
	
	
	public static int length = 2;
	
	byte b1;
	byte b2;
	
	PacketBytecodeOutgoing(int b1, int b2) {
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
	
	
}
