package mobi.square.slots.enums;

public enum CardSuit {
	
	CLUBS,		// �����
	DIAMONDS,	// �����
	HEARTS,		// �����
	SPADES;		// ����
	
	public static CardSuit convert(int value) {
    	return CardSuit.class.getEnumConstants()[value];
    }
	
	public String toLowerString() {
		switch (this) {
			case CLUBS:		return "clubs";
			case DIAMONDS:	return "diamonds";
			case HEARTS:	return "hearts";
			case SPADES:	return "spades";
			default:		return "unknown";
		}
	}
	
}
