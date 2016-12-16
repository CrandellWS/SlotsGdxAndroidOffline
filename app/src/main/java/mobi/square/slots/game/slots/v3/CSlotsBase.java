package mobi.square.slots.game.slots.v3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import mobi.square.slots.enums.Symbol;
import mobi.square.slots.game.slots.CSlots;
import mobi.square.slots.game.slots.CSymbol;
import mobi.square.slots.game.slots.CWinLine;
import mobi.square.slots.game.slots.config.ISlotsConfig;
import mobi.square.slots.game.slots.lines.CLines;
import mobi.square.slots.utils.IntMap;
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

    public abstract Symbol[] getSymbols();

    public abstract Symbol[] getBlankSymbols();

    public abstract int[] getPaySums();

    public abstract void setPaySums(int[] pay_sums);

    public abstract Combination[][] getPaySymbols();

    public abstract void setPaySymbols(Combination[][] pay_symbols);

    public abstract void performLogic();

    public abstract void checkLogic();

    public abstract int checkPayout(int amount);

    public abstract BlankSymbol[] getBlankSpecialSymbols();

    public abstract BlankSymbol[] getBreakLongLines();

    @Override
    public void spin() {
        this.clearSymbols();
        this.performLogic();
        this.saveSymbols();
        int payout = this.getCurrentPayout();
        int min = this.parent.getBet() * this.parent.getLinesCount() * 3;
        int avg = this.parent.getBet() * this.parent.getLinesCount() * 10;
        int max = this.parent.getBet() * this.parent.getLinesCount() * 40;
        boolean low_balance = this.parent.getCurrentPayout() < -avg;
        this.parent.last_target_payout = payout;
        //System.out.println("---> Target: " + payout);
        final int tries = 8;
        for (int i = 0; i <= tries; i++) {
            this.generatePaySymbols(payout);
            this.generateBlankSymbols();
            this.breakLongLines();
            if (i >= tries) break;
            int award = this.checkCombinations(this.parent.getCurrentSymbols(), this.getPayLines(), this.parent.getBet());
            if (award > max || (low_balance && award > avg) ||
                    (this.parent.isSpinWasFree() && award > min)) {
                /*System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> WARNING");
				if (low_balance)
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> Low balance");*/
                this.restoreSymbols();
            } else break;
        }
        this.checkLogic();
    }

    @Override
    public void generateSymbols() {
        this.clearSymbols();
        this.saveSymbols();
        int payout = this.getCurrentPayout();
        int min = this.parent.getBet() * this.parent.getLinesCount() * 3;
        int avg = this.parent.getBet() * this.parent.getLinesCount() * 10;
        int max = this.parent.getBet() * this.parent.getLinesCount() * 40;
        boolean low_balance = this.parent.getCurrentPayout() < -avg;
        this.parent.last_target_payout = payout;
        final int tries = 8;
        for (int i = 0; i <= tries; i++) {
            this.generatePaySymbols(payout);
            this.generateBlankSymbols();
            this.breakLongLines();
            if (i >= tries) break;
            int award = this.checkCombinations(this.parent.getCurrentSymbols(), this.getPayLines(), this.parent.getBet());
            if (award > max || (low_balance && award > avg) ||
                    (this.parent.isSpinWasFree() && award > min)) {
                this.restoreSymbols();
            } else break;
        }
    }

    // Main Logic

    /**
     * Возвращает рекомендуемую сумму выплаты.
     *
     * @return int
     */
    protected int getCurrentPayout() {
        int payout = 0;
        int balance = (int) this.parent.getCurrentPayout();
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
            double l = ((double) (max_lines - lines_count) / (double) max_lines);
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
                payout = (int) (chance * this.parent.getCurrentPayout());
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
        n = 20 + (int) (20.0d * chance);
        if (n < this.parent.last_x5_payout) {
            chance = utils.getRandomDoubleMTF();
            this.parent.last_x5_payout = 0;
            return (3 + (int) (4.0d * chance)) * total_bet;
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
        slots.setCurrentPayout(current_payout - (double) amount);
    }

    /**
     * Возвращает приблизительную выплату для указанных символов.
     *
     * @param symbols - символы;
     * @param lines   - список линий;
     * @param bet     - ставка.
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
     * Генерация символов, подходящих по сумме выплат.
     *
     * @param amount - сумма выплат.
     */
    protected void generatePaySymbols(int amount) {

        if (amount <= 0) return;
        List<int[]> lines = this.getPayLinesList();
        int rest_amount = amount;
        int index, pay_index;
        int[] line = null;
        double chance;
        List<Combination> fit_combinations;
        int bet = this.parent.getBet();
        CSymbol[][] current = this.parent.getCurrentSymbols();
        List<Symbol> exclusions = new LinkedList<Symbol>();

        for (int j = 0; j < 20; j++) {

            // Random Payout
            pay_index = this.getMaxPayIndex(rest_amount);
            //System.out.print("Max Index: " + pay_index);
            if (j > 0) {
                pay_index = pay_index < 0 ? -1 : pay_index > 0 ? utils.getRandomInt(pay_index + 1) : 0;
            } else {
                pay_index = utils.getRandomInt(pay_index - 1, pay_index + 1);
            }
            if (pay_index < 0 && rest_amount == amount && amount > 0) pay_index = 0;
            //System.out.print("; Final Index: " + pay_index);
            //System.out.println(pay_index >= 0 ? " (" + this.getPaySums()[pay_index] + ")" : "");
            if (pay_index < 0) return;

            // Fit Combination
            chance = utils.getRandomDoubleMTF();
            boolean wild = chance <= this.getWildPercent();
            if (j > 0) {
                fit_combinations = this.getFitCombinationsRange(current, pay_index, wild, exclusions);
            } else {
                fit_combinations = this.getFitCombinations(current, pay_index, wild, exclusions);
            }
            index = 0;
            for (Combination c : fit_combinations) {
                index += c.lines.length;
            }
            //System.out.print("Combinations: " + index);
            if (index > 0) {
                index = utils.getRandomIntMTF(index);
            } else return;
            //System.out.println("; Generated: " + index);

            // Place Symbols
            int total = 0;
            Combination combination = null;
            for (Combination c : fit_combinations) {
                if (total + c.lines.length > index) {
                    combination = c;
                    line = lines.get(c.lines[index - total]);
                    break;
                } else total += c.lines.length;
            }
            if (combination == null) return;
            rest_amount -= combination.payout * bet;
            //System.out.println("Symbol: " + combination.symbol.toString() + "; Lines: " + combination.lines.length + "; Payout: " + combination.payout);
            //index = utils.getRandomIntMTF(combination.lines.length);
            //line = lines.get(combination.lines[index]);
            chance = utils.getRandomDoubleMTF();
            if (chance > 0.3d) exclusions.add(combination.symbol);
            for (int i = 0; i < line.length; i++) {
                current[i][line[i]].setSymbol(combination.row[i]);
            }

			/*index = fit_combinations.size();
			System.out.println("Combinations: " + index);
			if (index > 0) {
				index = utils.getRandomInt(index);
			} else return;

			// Place Symbols
			Combination combination = fit_combinations.get(index);
			rest_amount -= combination.payout * bet;
			System.out.println("Symbol: " + combination.symbol.toString() + "; Lines: " + combination.lines.length + "; Payout: " + combination.payout);
			index = utils.getRandomIntMTF(combination.lines.length);
			System.out.println("Generated: " + index);
			line = lines.get(combination.lines[index]);
			chance = utils.getRandomDoubleMTF();
			if (chance > 0.3d) exclusions.add(combination.symbol);
			for (int i = 0; i < line.length; i++) {
				current[i][line[i]].setSymbol(combination.row[i]);
			}*/
        }
    }

    /**
     * Возвращает максимальный индекс суммы выплат.
     *
     * @param amount - сумма выплат.
     * @return int
     */
    protected int getMaxPayIndex(int amount) {
        int max_index = -1;
        int[] pay_sums = this.getPaySums();
        int payout = 0;
        int bet = this.parent.getBet();
        for (int i = 0; i < pay_sums.length; i++) {
            payout = pay_sums[i] * bet;
            if (payout > amount) {
                max_index = (payout - amount > amount - payout) ? i - 1 : i;
                break;
            } else max_index = i;
        }
        return max_index;
    }

    /**
     * Возвращает максимальный индекс суммы выплат, не превосходящей ставку.<br>
     * Используется таблица выплат без учёта ставки. (?)
     *
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
     * Возвращает подходящие комбинации символов, включая допустимые линии.
     *
     * @param current    - текущие символы на автомате;
     * @param max_index  - максимальный индекс таблицы выплат;
     * @param wild       - должен ли присутствовать WILD;
     * @param exclusions - список символов-исключений.
     * @return LinkedList{@literal <}Combination{@literal >}
     */
    protected List<Combination> getFitCombinationsRange(CSymbol[][] current, int max_index, boolean wild, List<Symbol> exclusions) {
        List<Combination> result = new LinkedList<Combination>();
        for (int i = 0; i <= max_index; i++) {
            result.addAll(this.getFitCombinations(current, i, wild, exclusions));
        }
        return result;
    }

    /**
     * Возвращает подходящие комбинации символов, включая допустимые линии.
     *
     * @param current    - текущие символы на автомате;
     * @param pay_index  - индекс таблицы выплат;
     * @param wild       - должен ли присутствовать WILD;
     * @param exclusions - список символов-исключений.
     * @return LinkedList{@literal <}Combination{@literal >}
     */
    protected List<Combination> getFitCombinations(CSymbol[][] current, int pay_index, boolean wild, List<Symbol> exclusions) {
        int REELS_COUNT = this.getReelsCount();
        int active_lines = this.parent.getLinesCount();
        int[][] pay_lines = this.parent.getLines().getLines();
        List<Combination> result = new LinkedList<Combination>();
        Combination[] combinations = this.getPaySymbols()[pay_index];
        int[] drop_wild = this.getDropWild();
        int[] drop_scatter = this.getDropScatter();
        int[] lines, line;
        int i, j, k, scatters;
        boolean fit, has_wild;
        if (exclusions == null)
            exclusions = new LinkedList<Symbol>();
        for (i = 0; i < combinations.length; i++) {
            if (exclusions.contains(combinations[i].symbol)) continue;
            Combination combination = new Combination(
                    combinations[i].payout, combinations[i].symbol, combinations[i].row, null
            );
            lines = combinations[i].lines;
            for (k = 0; k < lines.length; k++) {
                if (active_lines <= lines[k]) continue;
                line = pay_lines[lines[k]];
                fit = true;
                has_wild = false;
                scatters = 0;
                for (j = 0; j < REELS_COUNT; j++) {
                    if (current[j][line[j]].getSymbol() != null &&
                            current[j][line[j]].getSymbol() != combinations[i].row[j]) {
                        fit = false;
                        break;
                    }
                    if (combinations[i].row[j] == Symbol.SCATTER) {
                        scatters++;
                    }
                    if (combinations[i].row[j] == Symbol.WILD) {
                        has_wild = true;
                    }
                }
                for (j = 0; j < drop_wild.length; j++) {
                    if (combinations[i].row[drop_wild[j]] == Symbol.WILD) {
                        fit = false;
                        break;
                    }
                }
                for (j = 0; j < drop_scatter.length; j++) {
                    if (combinations[i].row[drop_scatter[j]] == Symbol.SCATTER) {
                        fit = false;
                        break;
                    }
                }
                if (scatters > 0) {
                    if (this.scatter_table[scatters - 1] > 0) {
                        fit = false;
                    }
                }
                if (fit && (!wild ^ has_wild)) {
                    combination.addLine(lines[k]);
                }
            }
            if (combination.lines != null) {
                result.add(combination);
            }
        }
        return result;
    }

    /**
     * Заполняет пустые ячейки символами так, чтобы избежать лишних выплат.
     */
    protected void generateBlankSymbols() {
        int index;
        double chance;
        List<Symbol> symbols;
        int REELS_COUNT = this.getReelsCount();
        int SYMBOLS_COUNT = this.getSymbolsOnReel();
        Symbol[] blank_symbols = this.getBlankSymbols();
        CSymbol[][] current = this.parent.getCurrentSymbols();
        // Build symbols count map
        Symbol symbol;
        IntMap<Symbol> mult = new IntMap<Symbol>();
        for (int i = 0; i < REELS_COUNT; i++) {
            for (int j = 0; j < SYMBOLS_COUNT; j++) {
                symbol = current[i][j].getSymbol();
                if (symbol == null) continue;
                mult.addValue(symbol, 1);
            }
        }
        // Fill all empty slots
        for (int i = 0; i < REELS_COUNT; i++) {
            for (int j = 0; j < SYMBOLS_COUNT; j++) {
                if (current[i][j].getSymbol() != null) continue;
                { // Special Blank Symbols
                    for (BlankSymbol blank : this.getBlankSpecialSymbols()) {
                        if (blank.symbol == Symbol.SCATTER) {
                            if (utils.contains(this.getDropScatter(), i)) {
                                continue;
                            }
                        } else if (blank.symbol == Symbol.WILD) {
                            if (utils.contains(this.getDropWild(), i)) {
                                continue;
                            }
                        }
                        if (blank.count > 0) {
                            if (mult.getValue(Symbol.WILD) >= blank.count) {
                                continue;
                            }
                        }
                        chance = utils.getRandomDoubleMTF();
                        if (chance < blank.chance) {
                            current[i][j].setSymbol(blank.symbol);
                            mult.addValue(blank.symbol, 1);
                            break;
                        }
                    }
                    if (current[i][j].getSymbol() != null) continue;
                }
                symbols = new LinkedList<Symbol>();
                for (int k = 0; k < blank_symbols.length; k++) {
                    // Many symbols
                    if (mult.getValue(blank_symbols[k]) >= 2) continue;
                    // Left Side
                    if (i > 0) {
                        // Middle
                        if (current[i - 1][j].getSymbol() == blank_symbols[k]) continue;
                        // Top
                        if (j > 0 && current[i - 1][j - 1].getSymbol() == blank_symbols[k])
                            continue;
                        // Bottom
                        if (j < SYMBOLS_COUNT - 1 && current[i - 1][j + 1].getSymbol() == blank_symbols[k])
                            continue;
                    }
                    // Right Side
                    if (i < REELS_COUNT - 1) {
                        // Middle
                        if (current[i + 1][j].getSymbol() == blank_symbols[k]) continue;
                        // Top
                        if (j > 0 && current[i + 1][j - 1].getSymbol() == blank_symbols[k])
                            continue;
                        // Bottom
                        if (j < SYMBOLS_COUNT - 1 && current[i + 1][j + 1].getSymbol() == blank_symbols[k])
                            continue;
                    }
                    // Add Symbol
                    symbols.add(blank_symbols[k]);
                }
                // Place Symbol
                index = symbols.size();
                if (index > 0) {
                    index = utils.getRandomInt(index);
                    symbol = symbols.get(index);
                } else {
                    index = utils.getRandomInt(blank_symbols.length);
                    symbol = blank_symbols[index];
                }
                current[i][j].setSymbol(symbol);
                mult.addValue(symbol, 1);
            }
        }
    }

    /**
     * Разрушает заданные в конфиге линии.
     */
    protected void breakLongLines() {
        Symbol[] symbols;
        int REELS_COUNT = this.getReelsCount();
        CSymbol[][] current = this.parent.getCurrentSymbols();
        BlankSymbol[] break_lines = this.getBreakLongLines();
        int[][] lines = this.getPayLines();
        for (int i = 0; i < lines.length; i++) {
            symbols = new Symbol[REELS_COUNT];
            for (int j = 0; j < REELS_COUNT; j++) {
                symbols[j] = current[j][lines[i][j]].getSymbol();
            }
            this.getLinePayout(symbols);
            for (int j = 0; j < break_lines.length; j++) {
                if (this.target == break_lines[j].symbol &&
                        this.count >= break_lines[j].count) {
                    double chance = utils.getRandomDoubleMTF();
                    if (chance < break_lines[j].chance) continue;
                    int index = 5;
                    Symbol symbol = break_lines[j].symbol;
                    Symbol[] blank_symbols = this.getBlankSymbols();
                    while (break_lines[j].symbol == symbol && index-- > 0) {
                        symbol = blank_symbols[utils.getRandomInt(blank_symbols.length)];
                    }
                    for (index = break_lines[j].count - 1; index >= 0; index--) {
                        if (this.saved_symbols[index][lines[i][index]] == null) {
                            current[index][lines[i][index]].setSymbol(symbol);
                            break;
                        }
                    }
                    //index = break_lines[j].count - 1;
                    //index = 2;
                    //System.out.println(current[index][lines[i][index]].getSymbol() + " replaced by symbol " + symbol.toString());
                    //current[index][lines[i][index]].setSymbol(symbol);
                }
            }
        }
    }

    /**
     * Возвращает список активных линий.
     *
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
     *
     * @return LinkedList{@literal <}int[]{@literal >}
     */
    protected List<int[]> getPayLinesList() {
        int[][] lines = this.parent.getLines().getLines();
        List<int[]> result = new LinkedList<int[]>();
        for (int i = 0; i < lines.length; result.add(lines[i++])) ;
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
     * Генерация таблицы выплат без учёта ставки.
     */
    public void generateTable() {
        this.total = 0;
        int[] row = new int[this.getReelsCount()];
        Combination[] variations = new Combination[utils.pow(this.getSymbols().length, this.getReelsCount())];
        this.all_lines = new int[this.getLinesSet().getLinesCount()];
        for (int i = 0; i < this.all_lines.length; i++) this.all_lines[i] = i;
        { // Check constants, too low values brings death
            double value = this.getPayoutPercent();
            if (value < 0.7d) {
                System.out.println(this.getTitle().concat(" warning! Payout percent is too low, min value is 0.7"));
            }
            value = this.getBlankPercent();
            if (value < 0.2d) {
                System.out.println(this.getTitle().concat(" error! Blank percent is too low, min value is 0.2"));
                return;
            }
            value = this.getZeroNegativePercent();
            if (value < 0.1d) {
                System.out.println(this.getTitle().concat(" error! Zero negative percent is too low, min value is 0.1"));
                return;
            }
        }
        long time = System.currentTimeMillis();
        this.generate(variations, row, 0);
        System.out.print(this.getTitle() + ": " + variations.length + " -> ");
        variations = this.stripDuplicates(variations);
        variations = this.splitTable(variations);
        System.out.print(variations.length);
        this.sortTable(variations);
        this.compactTable(variations);
        time = System.currentTimeMillis() - time;
        System.out.println(" (" + time + " ms)");
		/*if (this.getTitle() == "garage") {
			int[][] lines = CLines.newLines(this.getLinesSet()).getLines();
			for (int i = 0; i < this.getPaySums().length; i++) {
				for (int j = 0; j < this.getPaySymbols()[i].length; j++) {
					System.out.println("Symbol: " + this.getPaySymbols()[i][j].symbol.toString() + "; Payout: " + this.getPaySums()[i]);
					for (int k = 0; k < this.getPaySymbols()[i][j].row.length; k++) {
						System.out.print(this.getPaySymbols()[i][j].row[k] + " ");
					}
					System.out.println("");
					for (int k = 0; k < this.getPaySymbols()[i][j].lines.length; k++) {
						System.out.print(" > ");
						int[] line = lines[getPaySymbols()[i][j].lines[k]];
						for (int m = 0; m < line.length; m++) {
							System.out.print(line[m] + " ");
						}
						System.out.println("");
					}
				}
			}
		}*/
		/*for (int i = 0; i < this.getPaySums().length; i++) {
			System.out.println(this.getPaySums()[i]);
		}*/
    }

    /**
     * Рекурсивная генерация всех комбинаций символов на линии.
     *
     * @param variations - массив всех комбинаций;
     * @param row        - массив для текущей комбинации;
     * @param pos        - текущая позиция в массиве.
     */
    protected void generate(Combination[] variations, int[] row, int pos) {
        int reels_count = this.getReelsCount();
        if (pos == reels_count) {
            int payout = 0;
            Symbol[] symbols = new Symbol[reels_count];
            for (int i = 0; i < reels_count; i++)
                symbols[i] = this.getSymbols()[row[i]];
            payout = this.getLinePayout(symbols);
            if (this.left) {
                for (int i = this.count; i < reels_count; symbols[i++] = null) ;
            } else {
                for (int i = 0; i < reels_count - this.count; symbols[i++] = null) ;
            }
            variations[this.total++] = new Combination(
                    payout, this.target, symbols, null
            );
            return;
        }
        for (int i = 0; i < this.getSymbols().length; i++) {
            row[pos] = i;
            generate(variations, row, pos + 1);
        }
    }

    // Table Generator

    /**
     * Удалить дублирующиеся комбинации.
     *
     * @param variations - массив всех комбинаций.
     * @return Combination[]
     */
    protected Combination[] stripDuplicates(Combination[] variations) {
        List<Combination> result = new LinkedList<Combination>();
        for (int i = 0; i < variations.length; i++) {
            if (variations[i].symbol == Symbol.SCATTER)
                continue;
            if (variations[i].payout > 0) {
                boolean found = false;
                for (Combination combination : result) {
                    if (combination.equals(variations[i])) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    result.add(new Combination(variations[i].payout, variations[i].symbol, variations[i].row.clone(), null));
                }
            }
        }
        return result.toArray(new Combination[0]);
    }

    /**
     * Группирует комбинации, срабатывающие на несколько линий.
     *
     * @param variations - исходный массив комбинаций.
     * @return <b>Combination[]</b> - конечный массив комбинаций.
     */
    protected Combination[] splitTable(Combination[] variations) {
        int i, j, k, total;
        int reels_count = this.getReelsCount();
        int[][] lines = CLines.newLines(this.getLinesSet()).getLines();
        int max_line = this.getValidLines()[this.getValidLines().length - 1];
        List<Combination> non_zero = new LinkedList<Combination>();
        for (i = 0; i < variations.length; i++) {
            if (variations[i].payout == 0 ||
                    variations[i].lines != null) {
                continue;
            }
            // Remove short combinations
            for (j = 0, total = 0; j < variations[i].row.length; j++) {
                if (variations[i].row[j] != null) total++;
            }
            if (total < 3) continue;
            // Split all lines
            Combination[] new_array = new Combination[max_line];
            for (j = 0; j < max_line; j++) {
                int payout = 0;
                for (k = 0; k < max_line; k++) {
                    Symbol[] symbols = new Symbol[reels_count];
                    for (int m = 0; m < reels_count; m++) {
                        if (lines[j][m] == lines[k][m]) {
                            symbols[m] = variations[i].row[m];
                        } else symbols[m] = null;
                    }
                    payout += this.getLinePayout(symbols);
                }
                new_array[j] = new Combination(payout, variations[i].symbol, variations[i].row, new int[]{j});
            }
            // Compact equal lines
            List<Combination> new_list = new LinkedList<Combination>();
            for (j = 0; j < new_array.length; j++) {
                Combination c = new_array[j];
                Combination listed = null;
                for (k = 0; k < new_list.size(); k++) {
                    listed = new_list.get(k);
                    if (listed.payout != c.payout) {
                        listed = null;
                    } else break;
                }
                if (listed == null) {
                    new_list.add(new Combination(c.payout, c.symbol, c.row.clone(), c.lines.clone()));
                } else listed.addLines(c.lines);
            }
            non_zero.addAll(new_list);
        }
        return non_zero.toArray(new Combination[0]);
    }

    /**
     * Объединяет подобные комбинации в один список.
     *
     * @param variations - список комбинаций.
     * @return Combination[]
     */
    @Deprecated
    protected Combination[] mergeTable(Combination[] variations) {
        IntMap<String> map = new IntMap<String>();
        for (int i = 0; i < variations.length; i++) {
            String key = "";
            for (int j = 0; j < variations[i].row.length; j++) {
                if (variations[i].row[j] != null) {
                    key = key.concat(variations[i].row[j].toString());
                } else key = key.concat("N00");
            }
            key = key.concat("_").concat(String.valueOf(variations[i].payout));
            if (map.contains(key)) {
                variations[map.getValue(key)].addLines(variations[i].lines);
            } else map.setValue(key, i);
        }
        int i = 0;
        Combination[] result = new Combination[map.size()];
        Set<String> keys = map.keySet();
        for (String key : keys) {
            result[i++] = variations[map.getValue(key)];
        }
        return result;
    }

    /**
     * Возвращает сумму выигрыша по указанной линии без учёта ставки.
     *
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
     * Сортировка всех комбинаций по возрастанию выплат.
     *
     * @param variations - массив всех комбинаций.
     */
    protected void sortTable(Combination[] variations) {
        Arrays.sort(variations, new Comparator<Combination>() {
            @Override
            public int compare(Combination o1, Combination o2) {
                int s1 = o1.payout;
                int s2 = o2.payout;
                if (s1 > s2) {
                    return 1;
                } else if (s1 < s2) {
                    return -1;
                } else return 0;
            }
        });
    }

    /**
     * Разбиение выплат на диапазоны по сумме.
     *
     * @param variations - отсортированный массив всех комбинаций;
     */
    protected void compactTable(Combination[] variations) {
        int count = 0;
        for (int i = 0; i < variations.length; i++) {
            int start = variations[i].payout;
            count++;
            for (int j = i + 1; j < variations.length; j++) {
                if (start != variations[j].payout) {
                    i = j - 1;
                    break;
                } else if (j == variations.length - 1) {
                    i = j;
                    break;
                }
            }
        }
        int[] pay_sums = new int[count];
        Combination[][] pay_symbols = new Combination[count][];
        List<Combination> vars;
        count = 0;
        for (int i = 0; i < variations.length; i++) {
            int start = variations[i].payout;
            vars = new LinkedList<Combination>();
            vars.add(variations[i]);
            for (int j = i + 1; j < variations.length; j++) {
                if (start != variations[j].payout) {
                    i = j - 1;
                    break;
                } else if (j == variations.length - 1) {
                    i = j;
                    break;
                } else {
                    vars.add(variations[j]);
                }
            }
            pay_symbols[count] = vars.toArray(new Combination[0]);
            pay_sums[count++] = start;
        }
        this.setPaySymbols(pay_symbols);
        this.setPaySums(pay_sums);
    }

    /**
     * Чтение таблицы комбинаций из файла.
     *
     * @throws IOException
     */
    protected void readTable() throws IOException {
        long time = System.currentTimeMillis();
        String filename = "conf/".concat(this.getTitle()).concat(".sum");
        FileHandle file = Gdx.files.internal(filename);
        BufferedReader reader = file.reader(100000);
        System.out.println(this.getTitle() + " parsed in " + (System.currentTimeMillis() - time) + " ms");
        String line;
        int line_n = 0;
        line = reader.readLine();
        line_n++;
        int count = Integer.parseInt(line);
        int[] pay_sums = new int[count];
        Combination[][] pay_symbols = new Combination[count][];
        line = reader.readLine();
        line_n++;
        String[] chunks = line.split(",");
        if (chunks.length != count) {
            System.out.println("Error in line " + line_n);
            return;
        }
        for (int i = 0; i < count; i++) {
            pay_sums[i] = Integer.parseInt(chunks[i]);
        }
        for (int i = 0; i < count; i++) {
            line = reader.readLine();
            line_n++;
            int size = Integer.parseInt(line);
            pay_symbols[i] = new Combination[size];
            for (int j = 0; j < size; j++) {
                line = reader.readLine();
                line_n++;
                chunks = line.split(",");
                if (chunks.length < 4) {
                    System.out.println("Error in line " + line_n);
                    return;
                }
                int payout, offset;
                Symbol symbol;
                Symbol[] row;
                int[] lines;
                symbol = Symbol.valueOf(chunks[0]);
                payout = Integer.parseInt(chunks[1]);
                lines = new int[Integer.parseInt(chunks[2])];
                for (int k = 0; k < lines.length; k++) {
                    lines[k] = Integer.parseInt(chunks[k + 3]);
                }
                offset = lines.length + 3;
                row = new Symbol[Integer.parseInt(chunks[offset])];
                for (int k = 0; k < row.length; k++) {
                    String data = chunks[k + offset + 1];
                    if (data != null &&
                            !data.isEmpty() &&
                            !data.equals("null")) {
                        row[k] = Symbol.valueOf(data);
                    } else row[k] = null;
                }
                pay_symbols[i][j] = new Combination(payout, symbol, row, lines);
            }
        }
        this.setPaySymbols(pay_symbols);
        this.setPaySums(pay_sums);
    }

    /**
     * Возвращает суммы выплат для указанного символа.
     *
     * @param symbol - символ.
     * @return int[]
     */
    protected int[] getPayTable(Symbol symbol) {
        int i = 0;
        Symbol[] symbols = this.getSymbols();
        int[][] pay_table = this.getCombinations();
        for (; i < symbols.length && symbols[i] != symbol; i++) ;
        if (i >= pay_table.length) return new int[this.getReelsCount()];
        return pay_table[i];
    }

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

    // Interface Implementation

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

    protected class Combination {
        public int payout;
        public Symbol symbol;
        public Symbol[] row;
        public int[] lines;

        public Combination(int payout, Symbol symbol, Symbol[] row, int[] lines) {
            this.payout = payout;
            this.symbol = symbol;
            this.lines = lines;
            this.row = row;
        }

        public void addLine(int line) {
            if (this.lines == null)
                this.lines = new int[0];
            int i;
            int[] result = new int[this.lines.length + 1];
            for (i = 0; i < this.lines.length; i++)
                result[i] = this.lines[i];
            result[i] = line;
            this.lines = result;
        }

        public void addLines(int[] lines) {
            if (this.lines == null)
                this.lines = new int[0];
            int i, j = 0;
            int[] result = new int[this.lines.length + lines.length];
            for (i = 0; i < this.lines.length; i++)
                result[j++] = this.lines[i];
            for (i = 0; i < lines.length; i++)
                result[j++] = lines[i];
            this.lines = result;
        }

        public boolean equals(Combination combination) {
            int reels_count = getReelsCount();
            for (int i = 0; i < reels_count; i++) {
                if (this.row[i] != combination.row[i]) {
                    return false;
                }
            }
            return true;
        }
    }
}
