package mobi.square.slots.enums;

public enum RiskType {
	
	COLOR,
	GREATER;
	
	public static RiskType convert(int value) {
    	return RiskType.class.getEnumConstants()[value];
    }
	
}
