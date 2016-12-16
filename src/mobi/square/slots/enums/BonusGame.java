package mobi.square.slots.enums;

public enum BonusGame {
	
	NONE,
	CHESTS,
	UNDERWATER_LIFE_SEASHELLS,
	FAIRYTALE_KEGS,
	GARAGE_BOXES,
	GARAGE_LOCKS,
	INDIA_BASKETS,
	MONKEY_ROPES,
	RESIDENT_SAFES,
	CHRISTMAS_TREE,
	BOOK_OF_RA_SLOT,
	VALENTINES_DAY_HEARTS,
	RICHES_OF_RA_STATUETTE,
	FRUIT_COCKTAIL_BONUS_GAME,
	GARAGE_DELUXE_BOXES,
	GARAGE_DELUXE_LOCKS,
	LUCKY_ANGLER_CHESTS,
	MONKEY_ROPES_DELUXE,
	FAIRY_LAND_LILIES,
	LUCKY_HAUNTER_COVERS,
	ROCKCLIMBER_CAVES;
	
	public static BonusGame convert(int value) {
    	return BonusGame.class.getEnumConstants()[value];
    }
	
	public String toLowerCase() {
		return this.toString().toLowerCase();
	}

}
