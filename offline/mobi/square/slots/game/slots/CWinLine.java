package mobi.square.slots.game.slots;

import java.io.Serializable;

import mobi.square.slots.enums.Symbol;

public class CWinLine implements Serializable {
	
	private static final long serialVersionUID = -815126225928434223L;
	
	private CSlots parent = null;
	
	private int index = 0;
	private int count = 0;
	private Symbol symbol = null;
	private boolean left = true;
	private int pay;
	private Multiplier[] multipliers = null;
	private boolean bonus_line = false;
	
	public static class Multiplier implements Serializable{

		private static final long serialVersionUID = 1890236256302543324L;
		
		private int reel;
		private int multiplier;
		
		public Multiplier(int reel, int multiplier) {
			super();
			this.reel = reel;
			this.multiplier = multiplier;
		}
		
		public int getReel() {
			return reel;
		}
		public void setReel(int reel) {
			this.reel = reel;
		}
		public int getMultiplier() {
			return multiplier;
		}
		public void setMultiplier(int multiplier) {
			this.multiplier = multiplier;
		}
	}
	
	public CWinLine(CSlots parent, int index, int count, Symbol symbol) {
		this.parent = parent;
		this.index = index;
		this.count = count;
		this.symbol = symbol;
		this.left = true;
		this.pay = 0;
	}
	
	public CWinLine(CSlots parent, int index, int count, Symbol symbol, boolean left) {
		this.parent = parent;
		this.index = index;
		this.count = count;
		this.symbol = symbol;
		this.left = left;
		this.pay = 0;
	}
	
	// Public Methods
	
	public int getNumber() {
		return this.getIndex() + 1;
	}
	
	public String getName() {
		return this.getSymbol().name().concat("_").concat(String.valueOf(this.getCount()));
	}
	
	// Getters & Setters
	
	public CSlots getParent() {
		return this.parent;
	}
	
	public void setParent(CSlots parent) {
		this.parent = parent;
	}

	public int getIndex() {
		return this.index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getCount() {
		return this.count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public Symbol getSymbol() {
		return this.symbol;
	}

	public void setSymbol(Symbol symbol) {
		this.symbol = symbol;
	}

	public boolean isLeft() {
		return this.left;
	}

	public void setLeft(boolean left) {
		this.left = left;
	}

	public int getPay() {
		return pay;
	}

	public void setPay(int pay) {
		this.pay = pay;
	}

	public Multiplier[] getMultipliers() {
		return multipliers;
	}

	public void setMultipliers(Multiplier[] multipliers) {
		this.multipliers = multipliers;
	}

	public boolean isBonusLine() {
		return bonus_line;
	}

	public void setBonusLine(boolean bonus_line) {
		this.bonus_line = bonus_line;
	}
	
}
