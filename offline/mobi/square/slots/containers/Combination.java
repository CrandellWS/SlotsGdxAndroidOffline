package mobi.square.slots.containers;

import mobi.square.slots.enums.Symbol;

public class Combination {
	
	private Symbol symbol = null;
	private int row = 0;
	private int count = 0;
	private int award = 0;
	
	// Public Methods
	
	public String getName() {
		return this.getSymbol().name().concat("_").concat(String.valueOf(this.getRow()));
	}
	
	public void addCount(int value) {
		this.setCount(this.getCount() + value);
	}
	
	// Getters & Setters
	
	public Symbol getSymbol() {
		return this.symbol;
	}
	
	public void setSymbol(Symbol symbol) {
		this.symbol = symbol;
	}

	public int getRow() {
		return this.row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCount() {
		return this.count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getAward() {
		return this.award;
	}

	public void setAward(int award) {
		this.award = award;
	}
	
}
