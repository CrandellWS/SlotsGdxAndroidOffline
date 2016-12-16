package mobi.square.slots.enums;

public enum Lines {
	
	LINES_3X_20_V1,
	LINES_3X_99_V1,
	LINES_3X_09_V1,
	LINES_4X_50_V1;

	public int getLinesCount() {
		switch (this) {
			case LINES_3X_20_V1:	return 20;
			case LINES_3X_99_V1:	return 99;
			case LINES_4X_50_V1:	return 50;
			case LINES_3X_09_V1:	return 9;
			default:				return  0;
		}
	}

}
