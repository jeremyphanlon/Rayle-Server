package com.rayle.packet;

public enum PacketBytecodeOutgoing {

	SEND_MESSAGE(0, 0),
	FAILED_LOGIN(1, 0), SUCCESSFUL_LOGIN(1, 1), LOGOUT(1, 2),
	
	ENTITY_HP(2, 0), ENTITY_MAX_HP(2, 1), ENTITY_X(2, 2), ENTITY_Y(2, 3), ENTITY_Z(2, 4), ENTITY_ANIMATION_ID(2, 5), ENTITY_MODEL_ID(2, 6),
			ENTITY_OPTION(2, 7), // right-click options (byte optionIndex, String optionName)
		NEW_PLAYER(3, 0), PLAYER_NAME(3, 1), PLAYER_LEVEL(3, 2), 
			PLAYER_LEAVE(3, 3), // A Player teleports or logs out (if it is a teleport, a NEW_PLAYER is sent when the player lands)
		NEW_NPC(4, 0), NPC_NAME(4, 1), NPC_LEVEL(4, 2), NPC_ID(4, 3), NPC_COMBAT_STYLE(4, 4) ,
			NPC_DIALOG(4, 5), //Data about what the NPC or the Player says to each other
		
	NEW_GAME_OBJECT(5, 0), 
		GAME_OBJECT_ID(5, 1), 
		GAME_OBJECT_X(5, 2), 
		GAME_OBJECT_Y(5, 3), 
		GAME_OBJECT_Z(5, 4), 
		GAME_OBJECT_ANIMATION_ID(5, 5), 
		GAME_OBJECT_MODEL_ID(5, 6), 
		GAME_OBJECT_OPTION(5, 7), // right-click options (byte optionIndex, String optionName)
	
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
