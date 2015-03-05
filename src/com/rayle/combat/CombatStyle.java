package com.rayle.combat;

public enum CombatStyle {

	MELEE, MAGIC; //TODO add one for bows and arrows, crossbows, etc.
	
	public byte getBytecode() {
		return ((byte) this.ordinal());
	}
	
	public static CombatStyle parse(String s) {
		for (CombatStyle cs : CombatStyle.values()) {
			if (s.equalsIgnoreCase(cs.toString())) {
				return cs;
			}
		}
		return null;
	}
	
}
