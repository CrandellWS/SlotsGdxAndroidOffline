package mobi.square.slots.enums;

public enum CardColor {
	
	BLACK,
	RED;
	
	public static CardColor convert(int value) {
    	return CardColor.class.getEnumConstants()[value];
    }
	
	public String toLowerString() {
		switch (this) {
			case BLACK:	return "black";
			case RED:	return "red";
			default:	return "unknown";
		}
	}
	
}
