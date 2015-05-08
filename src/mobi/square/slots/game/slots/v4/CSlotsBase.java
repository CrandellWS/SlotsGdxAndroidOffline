package mobi.square.slots.game.slots.v4;

import java.util.LinkedList;
import java.util.List;

import mobi.square.slots.enums.Symbol;
import mobi.square.slots.game.slots.CSlots;
import mobi.square.slots.game.slots.CSymbol;
import mobi.square.slots.game.slots.CWinLine;
import mobi.square.slots.game.slots.config.ISlotsConfig;
import mobi.square.slots.utils.utils;

public abstract class CSlotsBase implements ISlotsConfig {

	// Процент возвращаемых на счёт игрока денег
	private static final double balance = 0.75d;

	protected transient CSlots parent;
	protected int[] wild_table;
	protected int[] scatter_table;
	protected int[] all_lines;
	protected int total = 0;
	protected int count = 0;
	//protected int max_index = 0;
	protected Symbol target = null;
	protected boolean left = true;
	protected Symbol[][] saved_symbols = null;

	public CSlotsBase(CSlots parent) {
		this.parent = parent;
		this.wild_table = this.getPayTable(Symbol.WILD);
		this.scatter_table = this.getPayTable(Symbol.SCATTER);
	}


	protected static class BlankSymbol {
		public final Symbol symbol;
		public final double chance;
		public final int count;
		public BlankSymbol(Symbol symbol, double chance) {
			this.symbol = symbol;
			this.chance = chance;
			this.count = 0;
		}
		public BlankSymbol(Symbol symbol, int count, double chance) {
			this.symbol = symbol;
			this.chance = chance;
			this.count = count;
		}
	}

	public static class Tapes {
		private int total;
		private Tape[] tapes;
		
		public Tapes(Tape[] tapes) {
			this.tapes = tapes;
			this.total = 0;
			for (int i = 0; i < tapes.length; i++) {
				this.total += tapes[i].getWeight();
			}
		}
		
		public Tape getRandomTape() {
			int r = utils.getRandomInt(this.total);
			int s = 0;
			for (int i = 0; i < this.tapes.length; i++) {
				s += this.tapes[i].weights;
				if (r < s) {
					return this.tapes[i];
				}
			}
			return null;
		}
		
		public static Tapes build(int[] weights, Symbol[][] tapes) {
			if (weights.length != tapes.length) {
				throw new IllegalArgumentException("weights.length != tapes.length");
			}
			int l = weights.length;
			Tape[] ts = new Tape[l];
			for (int i = 0; i < l; i++) {
				ts[i] = new Tape(weights[i], tapes[i]);
			}
			return new Tapes(ts);
		}
	}
	
	public static class Tape {
		private int weights;
		private Symbol[][] tapes;
		
		public Tape(int weight, Symbol[] tape) {
			super();
			this.weights = weight;
			this.tapes = new Symbol[5][]; 
			this.tapes[0] = tape;
			this.tapes[1] = tape;
			this.tapes[2] = tape;
			this.tapes[3] = tape;
			this.tapes[4] = tape;
		}
		
		public Tape(int weight, Symbol[][] tapes) {
			super();
			if (tapes.length != 5) {
				throw new IllegalArgumentException("tapes.length != 5");
			}
			this.weights = weight;
			this.tapes = new Symbol[5][]; 
			this.tapes[0] = tapes[0];
			this.tapes[1] = tapes[1];
			this.tapes[2] = tapes[2];
			this.tapes[3] = tapes[3];
			this.tapes[4] = tapes[4];
		}
		
		public int getWeight() {
			return this.weights;
		}
		
		public Symbol[] getTape(int reel) {
			return this.tapes[reel];
		}
	}
	
	public abstract Symbol[] getSymbols();
	public abstract Symbol[] getBlankSymbols();
	public abstract int[] getPaySums();
	public abstract void setPaySums(int[] pay_sums);
	public abstract void performLogic();
	public abstract void checkLogic();
	public abstract int checkPayout(int amount);
	public abstract Symbol[] getTape(int reel);
	
	// Main Logic

	@Override
	public void spin() {
		this.clearSymbols();
		this.performLogic();
		for (int i = 0; i < this.getReelsCount(); i++) {
			int offset = utils.getRandomIntMTF(this.getTape(i).length);
			for (int j = 0; j < this.getSymbolsOnReel(); j++) {
				this.parent.getCurrentSymbols()[i][j].setSymbol(this.getSymbolOnTape(i, offset, j));
			}
		}
		this.checkLogic();
	}

	@Override
	public void generateSymbols() {
		this.clearSymbols();
		for (int i = 0; i < this.getReelsCount(); i++) {
			int offset = utils.getRandomIntMTF(this.getTape(i).length);
			for (int j = 0; j < this.getSymbolsOnReel(); j++) {
				this.parent.getCurrentSymbols()[i][j].setSymbol(this.getSymbolOnTape(i, offset, j));
			}
		}
	}

	/**
	 * Возвращает рекомендуемую сумму выплаты.
	 * @return int
	 */
	protected int getCurrentPayout() {
		int payout = 0;
		int balance = (int)this.parent.getCurrentPayout();
		boolean forced = false;
		double chance = 0.0d;
		int bet = this.parent.getBet();
		int lines_count = this.parent.getLinesCount();
		int total_bet = bet * lines_count;
		int deadline = total_bet * 5;
		// Low Bet Fix 15.11.2013
		if (total_bet < 10) {
			chance = utils.getRandomDoubleMTF();
			if (chance > total_bet * 0.1d) return 0;
		}
		// Max Lines Fix 15.11.2013
		int[] valid_lines = this.parent.getConfig().getValidLines();
		int max_lines = valid_lines[valid_lines.length - 1];
		if (lines_count < max_lines) {
			final double k = 2.0d;
			chance = utils.getRandomDoubleMTF();
			double l = ((double)(max_lines - lines_count) / (double)max_lines);
			double t = Math.pow(k, l);
			l = k - t;
			if (chance > l) return 0;
		}
		// Random Bet Fix 28.11.2013
		if (!this.parent.isSpinWasFree()) {
			payout = this.getSpecialPayout();
			if (payout > 0) return payout;
		}
		// Common Payout
		chance = utils.getRandomDoubleMTF();
		if (chance > this.getBlankPercent() || forced) {
			if (balance <= deadline) {
				chance = utils.getRandomDoubleMTF();
				if (chance > this.getZeroNegativePercent() || forced) {
					chance = utils.getRandomDoubleMTF();
					int index = this.getBetIndex(lines_count);
					int overhead = chance > 0.9d ? 3 : chance > 0.7d ? 2 : 1;
					int rand = index > 0 ? utils.getRandomInt(index + overhead) : 0;
					payout = this.getPaySums()[rand] * bet;
				} else payout = 0;
			} else {
				chance = utils.getRandomDoubleMTF();
				payout = (int)(chance * this.parent.getCurrentPayout());
			}
		}
		return payout;
	}

	protected int getSpecialPayout() {
		int bet = this.parent.getBet();
		int lines_count = this.parent.getLinesCount();
		int total_bet = bet * lines_count;
		int n;
		double chance;
		// x20
		/*chance = utils.getRandomDoubleMTF();
		n = 160 + (int)(50.0d * chance);
		if (n < this.parent.last_x20_payout) {
			chance = utils.getRandomDoubleMTF();
			this.parent.last_x20_payout = 0;
			return (15 + (int)(10.0d * chance)) * total_bet;
		} else this.parent.last_x20_payout++;
		// x10
		chance = utils.getRandomDoubleMTF();
		n = 100 + (int)(30.0d * chance);
		if (n < this.parent.last_x10_payout) {
			chance = utils.getRandomDoubleMTF();
			this.parent.last_x10_payout = 0;
			return (8 + (int)(4.0d * chance)) * total_bet;
		} else this.parent.last_x10_payout++;*/
		// x5
		chance = utils.getRandomDoubleMTF();
		n = 20 + (int)(20.0d * chance);
		if (n < this.parent.last_x5_payout) {
			chance = utils.getRandomDoubleMTF();
			this.parent.last_x5_payout = 0;
			return (3 + (int)(4.0d * chance)) * total_bet;
		} else this.parent.last_x5_payout++;
		// x2
		/*chance = utils.getRandomDoubleMTF();
		n = 10 + (int)(10.0d * chance);
		if (n < this.parent.last_x2_payout) {
			chance = utils.getRandomDoubleMTF();
			this.parent.last_x2_payout = 0;
			return (1 + (int)(2.0d * chance)) * total_bet;
		} else this.parent.last_x2_payout++;*/
		return 0;
	}

	@Override
	public void checkCombinations(CSlots slots) {
		int pay = 0, amount = 0;
		CSymbol[][] current = slots.getCurrentSymbols();
		int[][] lines = this.getPayLines();
		int bet = slots.getBet();
		int total_bet = bet * this.parent.getLinesCount();
		List<CWinLine> winning_lines = new LinkedList<CWinLine>();
		for (int j = 0; j < lines.length; j++) {
			int[] line = lines[j];
			Symbol[] sequence = new Symbol[this.getReelsCount()];
			for (int i = 0; i < this.getReelsCount(); i++) {
				sequence[i] = current[i][line[i]].getSymbol();
			}
			pay = this.getLinePayout(sequence) * bet;
			if (pay > 0) {
				CWinLine win_line = new CWinLine(slots, j, this.count, this.target, this.left);
				win_line.setPay(pay);
				winning_lines.add(win_line);
				if (win_line.isLeft()) {
					for (this.count--; this.count >= 0; this.count--) {
						current[this.count][line[this.count]].setWin(true);
					}
				} else {
					for (this.count--; this.count >= 0; this.count--) {
						current[this.getReelsCount() - this.count - 1][line[this.count]].setWin(true);
					}
				}
			}
			amount += pay;
		}
		if (winning_lines.size() > 1) {
			this.parent.single_line_row = 0;
		} else if (winning_lines.size() > 0) {
			this.parent.single_line_row++;
		}
		slots.setWinningLines(winning_lines.toArray(new CWinLine[0]));
		amount = this.checkPayout(amount);
		double current_payout = slots.getCurrentPayout();
		if (amount >= total_bet * 20) {
			this.parent.last_x20_payout = 0;
		} else if (amount >= total_bet * 10) {
			this.parent.last_x10_payout = 0;
		} else if (amount >= total_bet * 5) {
			this.parent.last_x5_payout = 0;
		} else if (amount >= total_bet * 2) {
			this.parent.last_x2_payout = 0;
		}
		slots.setAward(slots.getAward() + amount);
		slots.setLastSpinAward(amount);
		slots.setCurrentPayout(current_payout - (double)amount);
	}

	/**
	 * Возвращает приблизительную выплату для указанных символов.
	 * @param symbols - символы;
	 * @param lines - список линий;
	 * @param bet - ставка.
	 * @return int
	 */
	public int checkCombinations(CSymbol[][] symbols, int[][] lines, int bet) {
		int pay = 0, amount = 0;
		for (int j = 0; j < lines.length; j++) {
			int[] line = lines[j];
			Symbol[] sequence = new Symbol[this.getReelsCount()];
			for (int i = 0; i < this.getReelsCount(); i++) {
				sequence[i] = symbols[i][line[i]].getSymbol();
			}
			pay = this.getLinePayout(sequence) * bet;
			amount += pay;
		}
		return amount;
	}


	/**
	 * Возвращает максимальный индекс суммы выплат, не превосходящей ставку.<br>
	 * Используется таблица выплат без учёта ставки. (?)
	 * @param bet - ставка пользователя.
	 * @return int
	 */
	protected int getBetIndex(int bet) {
		int[] pay_sums = this.getPaySums();
		for (int i = 0; i < pay_sums.length; i++) {
			if (pay_sums[i] >= bet) {
				return i > 0 ? i : i - 1;
			}
		}
		return 0;
	}

	/**
	 * Возвращает список активных линий.
	 * @return int[][]
	 */
	protected int[][] getPayLines() {
		int[][] lines = this.parent.getLines().getLines();
		int count = this.parent.getLinesCount();
		int[][] result = new int[count][];
		for (int i = 0; i < count; i++)
			result[i] = lines[i];
		return result;
	}

	/**
	 * Возвращает список активных линий.
	 * @return LinkedList{@literal <}int[]{@literal >}
	 */
	protected List<int[]> getPayLinesList() {
		int[][] lines = this.parent.getLines().getLines();
		List<int[]> result = new LinkedList<int[]>();
		for (int i = 0; i < lines.length; result.add(lines[i++]));
		return result;
	}

	/**
	 * Полная очистка автомата.
	 */
	protected void clearSymbols() {
		CSymbol[][] current = new CSymbol[this.getReelsCount()][];
		for (int i = 0; i < this.getReelsCount(); i++) {
			current[i] = new CSymbol[this.getSymbolsOnReel()];
			for (int j = 0; j < this.getSymbolsOnReel(); j++) {
				current[i][j] = new CSymbol(this.parent, null);
			}
		}
		this.parent.setCurrentSymbols(current);
	}

	/**
	 * Сохранить текущие символы.
	 */
	protected void saveSymbols() {
		this.saved_symbols = new Symbol[this.getReelsCount()][];
		CSymbol[][] current = this.parent.getCurrentSymbols();
		for (int i = 0; i < this.getReelsCount(); i++) {
			this.saved_symbols[i] = new Symbol[this.getSymbolsOnReel()];
			for (int j = 0; j < this.getSymbolsOnReel(); j++) {
				this.saved_symbols[i][j] = current[i][j].getSymbol();
			}
		}
	}

	/**
	 * Восстановить сохранённые символы.
	 */
	protected void restoreSymbols() {
		CSymbol[][] current = new CSymbol[this.getReelsCount()][];
		for (int i = 0; i < this.getReelsCount(); i++) {
			current[i] = new CSymbol[this.getSymbolsOnReel()];
			for (int j = 0; j < this.getSymbolsOnReel(); j++) {
				current[i][j] = new CSymbol(this.parent, this.saved_symbols[i][j]);
			}
		}
		this.parent.setCurrentSymbols(current);
	}

	/**
	 * Возвращает сумму выигрыша по указанной линии без учёта ставки.
	 * @param sequence - комбинация символов.
	 * @return int
	 */
	protected int getLinePayout(Symbol[] sequence) {
		int count = 0;
		boolean wild = false;
		Symbol target = null;
		for (int i = 0; i < sequence.length; i++) {
			if (sequence[i] == null) break;
			if (target == null) {
				if (sequence[i] == Symbol.WILD) {
					wild = true;
					if (this.wild_table[i] > 0) {
						target = Symbol.WILD;
					}
				} else target = sequence[i];
				count++;
			} else {
				if (sequence[i] == target) {
					count++;
				} else if (sequence[i] == Symbol.WILD &&
					!utils.contains(this.getNonWildSymbols(), sequence[i])) {
					wild = true;
					count++;
				} else break;
			}
		}
		this.count = count;
		this.target = target;
		this.left = true;
		if (target != null) {
			count = getPayTable(target)[count - 1];
			if (target != Symbol.WILD && wild) {
				count *= this.getWildMultiplier();
			}
		} else count = 0;
		return count;
	}

	/**
	 * Возвращает суммы выплат для указанного символа.
	 * @param symbol - символ.
	 * @return int[]
	 */
	protected int[] getPayTable(Symbol symbol) {
		int i = 0;
		Symbol[] symbols = this.getSymbols();
		int[][] pay_table = this.getCombinations();
		for (; i < symbols.length && symbols[i] != symbol; i++);
		if (i >= pay_table.length) return new int[this.getReelsCount()];
		return pay_table[i];
	}

	public Symbol getSymbolOnTape(int reel, int offset, int index) {
		return this.getTape(reel)[(offset + index) % this.getTape(reel).length];
	}
	
	// Interface Implementation

	@Override
	public double[] getReel(int index) {
		double[] result = new double[Symbol.getSymbolsCount()];
		Symbol[] symbols = this.getSymbols();
		double percent = 1.0d / symbols.length + 0.01d;
		for (int i = 0; i < symbols.length; i++) {
			result[symbols[i].ordinal()] = percent;
		}
		return result;
	}

	@Override
	public double getPayoutPercent() {
		return balance;
	}

	@Override
	public double getPayoutPercentPeriodic() {
		double balance = this.getPayoutPercent();
		double rad = this.parent.getPeriod() * Math.PI / 180.0d;
		double result = balance + Math.sin(rad) * 0.15d;
		return result;
	}

	@Override
	public double getSymbolChance(int index, Symbol symbol) {
		return 0.0d;
	}

	@Override
	public int getCanvasLinesType() {
		return 1;
	}

	@Override
	public boolean isNewLogic() {
		return true;
	}

	@Override
	public boolean isRequiresVip() {
		return false;
	}

	@Override
	public void changeLinesCount() {
	}
	
	@Override
	public void changeBet() {
	}
}
