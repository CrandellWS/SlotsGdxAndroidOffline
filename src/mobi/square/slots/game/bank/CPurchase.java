package mobi.square.slots.game.bank;

public class CPurchase {

	private String type;
	private int amount;
	private int discount;
	private int gold;
	private int bonus_gold;
	private boolean recommended;

	// Constructors

	public CPurchase() {
		this.type = null;
		this.amount = 0;
		this.discount = 0;
		this.gold = 0;
		this.bonus_gold = 0;
		this.recommended = false;
	}

	// Getters & Setters

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getAmount() {
		return this.amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getDiscount() {
		return this.discount;
	}

	public void setDiscount(int discount) {
		this.discount = discount;
	}

	public int getGold() {
		return this.gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public int getBonusGold() {
		return this.bonus_gold;
	}

	public void setBonusGold(int bonus_gold) {
		this.bonus_gold = bonus_gold;
	}

	public boolean isRecommended() {
		return this.recommended;
	}

	public void setRecommended(boolean recommended) {
		this.recommended = recommended;
	}

}
