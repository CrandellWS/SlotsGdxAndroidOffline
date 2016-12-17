package mobi.square.slots.game.slots.v3.config;

import java.io.IOException;

import mobi.square.slots.enums.Lines;
import mobi.square.slots.enums.RiskType;
import mobi.square.slots.enums.Symbol;
import mobi.square.slots.game.slots.CSlots;
import mobi.square.slots.game.slots.CSymbol;
import mobi.square.slots.game.slots.bonusgames.CUnderwaterLifeSeashells;
import mobi.square.slots.game.slots.config.CEffects;
import mobi.square.slots.game.slots.v3.CSlotsBase;

public class UnderwaterLife extends CSlotsBase {

    private static final int[][] pay_table = {
            // SCATTER
            {0, 0, 0, 0, 0},
            // BONUS
            {0, 0, 0, 0, 0},
            // WILD
            {0, 10, 100, 1000, 10000},
            // N01
            {0, 0, 5, 10, 50},
            // N02
            {0, 0, 5, 15, 75},
            // N03
            {0, 0, 7, 20, 100},
            // N04
            {0, 0, 7, 25, 125},
            // N05
            {0, 0, 10, 30, 150},
            // N06
            {0, 2, 10, 40, 200},
            // N07
            {0, 3, 15, 50, 300},
            // N08
            {0, 4, 20, 60, 400},
            // N09
            {0, 5, 25, 75, 500},
            // N10
            {0, 1, 5, 25, 250}
    };
    // Процент пустых выплат
    private static final double blank_percent = 0.21d;
    // Процент дополнительных нулевых выплат при отрицательном балансе
    private static final double zero_negative_percent = 0.2d;
    // Процент выпадения WILD'ов на выигрышной комбинации
    private static final double wild_percent = 0.03d;
    // Список специальных символов, выпадающих на пустых ячейках
    private static final BlankSymbol[] blank_special_symbols = {
            new BlankSymbol(Symbol.SCATTER, 0.02d),
            new BlankSymbol(Symbol.WILD, 0.01d),
            new BlankSymbol(Symbol.BONUS, 0.047d)//0.035
    };
    // Разрушать линии с указанным количеством символов
    private static final BlankSymbol[] break_long_lines = {
            //new BlankSymbol(Symbol.WILD, 5, 0.01d),
            //new BlankSymbol(Symbol.WILD, 4, 0.04d),
            //new BlankSymbol(Symbol.N09, 5, 0.08d)
    };
    // Индексы барабанов, на которых запрещено выпадение символа WILD
    private static final int[] drop_wild = {};
    // Индексы барабанов, на которых запрещено выпадение символа SCATTER
    private static final int[] drop_scatter = {};
    // Символы, не заменяемые WILD'ом
    private static final Symbol[] non_wild_symbols = {Symbol.SCATTER};
    // Возможные ставки
    private static final int[] valid_bets = {1, 2, 3, 4, 5, 10, 25, 50, 100, 150}; //{ 1, 10, 25, 50, 100, 150 }
    // Возможное количетсво линий
    private static final int[] valid_lines = {1, 5, 10, 15, 20};
    // Количество барабанов (нельзя менять)
    private static final int reels_count = 5;
    // Количество символов на барабанах
    private static final int symbols_on_reel = 3;
    // Умножение ставки по линии с WILD
    private static final int wild_multiplier = 2;
    // Набор игровых линий
    private static final Lines lines_set = Lines.LINES_3X_20_V1;
    // Тип игры на риск
    private static final RiskType risk_type = RiskType.GREATER;
    // Ревизия автомата
    private static final int revision = 2;
    // Название слотов (англ.)
    private static final String title = "underwater_life";
    private static final String background_path = "/SlotsWeb3/resources/img/slots/underwater_life_bg.js";
    private static final String symbols_path = "/SlotsWeb3/resources/img/slots/underwater_life.js";
    private static final String background_hi_path = "/SlotsWeb3/resources/img/slots/underwater_life_bg_hi.js";
    private static final String symbols_hi_path = "/SlotsWeb3/resources/img/slots/underwater_life_hi.js";
    private static int[] pay_sums;
    private static Combination[][] pay_symbols;
    private static Symbol[] blank_symbols = {
            Symbol.N01,
            Symbol.N02,
            Symbol.N03,
            Symbol.N04,
            Symbol.N05,
            Symbol.N06,
            Symbol.N07,
            Symbol.N08,
            Symbol.N09,
            Symbol.N10
    };
    private static Symbol[] symbols = {
            Symbol.SCATTER,
            Symbol.BONUS,
            Symbol.WILD,
            Symbol.N01,
            Symbol.N02,
            Symbol.N03,
            Symbol.N04,
            Symbol.N05,
            Symbol.N06,
            Symbol.N07,
            Symbol.N08,
            Symbol.N09,
            Symbol.N10
    };
    private boolean[][] wilds = null;

    // Logic

    public UnderwaterLife(CSlots parent) {
        super(parent);
    }

    public static void load() {
        try {
            new UnderwaterLife(null).readTable();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean[][] getWilds() {
        if (this.wilds == null)
            this.wilds = new boolean[this.getReelsCount()][this.getSymbolsOnReel()];
        return this.wilds;
    }

    @Override
    public void performLogic() {
        CSymbol[][] previous = super.parent.getPreviousSymbols();
        for (int i = 0; i < this.getReelsCount(); i++) {
            for (int j = 0; j < this.getSymbolsOnReel(); j++) {
                if (previous[i][j].getSymbol() == Symbol.WILD && this.getWilds()[i][j]) {
                    super.parent.getCurrentSymbols()[i][j].setSymbol(Symbol.WILD);
                }
            }
        }
    }

    @Override
    public void checkLogic() {
        // Freeze WILD
        StringBuilder json = new StringBuilder("");
        int free_spins = 0;
        for (int i = 0; i < this.getReelsCount(); i++) {
            for (int j = 0; j < this.getSymbolsOnReel(); j++) {
                if (super.parent.getPreviousSymbols()[i][j].getSymbol() == Symbol.WILD &&
                        this.getWilds()[i][j]) {
                    this.getWilds()[i][j] = false;
                } else if (super.parent.getCurrentSymbols()[i][j].getSymbol() == Symbol.WILD &&
                        !this.getWilds()[i][j]) {
                    if (!json.toString().isEmpty()) json.append(",");
                    json.append(CEffects.getFreezeEffect(j, i, Symbol.WILD, "lights"));
                    this.getWilds()[i][j] = true;
                    free_spins++;
                } else this.getWilds()[i][j] = false;
            }
        }
        super.parent.setEffects(json.toString());
        if (free_spins > 0) {
            super.parent.addFreeSpins(free_spins);
        }
    }

    @Override
    public int checkPayout(int amount) {
        int scatter_count = 0;
        CSymbol[][] symbols = this.parent.getCurrentSymbols();
        for (int j = 0; j < reels_count; j++) {
            for (int k = 0; k < symbols_on_reel; k++) {
                if (symbols[j][k].getSymbol() == Symbol.SCATTER) {
                    scatter_count++;
                }
            }
        }
        if (scatter_count >= 3) {
            switch (scatter_count) {
                case 3:
                    super.parent.addFreeSpins(8);
                    break;
                case 4:
                    super.parent.addFreeSpins(16);
                    break;
                case 5:
                default:
                    super.parent.addFreeSpins(24);
                    break;
            }
        }
        // Check bonus game
        int count, max_count = 0;
        int bet = super.parent.getBet();// * super.parent.getLinesCount();
        CSymbol[][] current = super.parent.getCurrentSymbols();
        int[][] lines = this.getPayLines();
        for (int j = 0; j < lines.length; j++) {
            int[] line = lines[j];
            count = 0;
            for (int i = 0; i < reels_count; i++) {
                if (current[i][line[i]].getSymbol() == Symbol.BONUS) {
                    count++;
                }
            }
            if (count > max_count) {
                max_count = count;
            }
        }
        if (max_count >= 3) {
            switch (max_count) {
                case 3:
                    this.generateBonusGame(20 * bet, 100 * bet);
                    break;
                case 4:
                    this.generateBonusGame(100 * bet, 350 * bet);
                    break;
                case 5:
                default:
                    this.generateBonusGame(200 * bet, 1000 * bet);
                    break;
            }
        }
        return amount;
    }

    // CSlotsBase

    public void generateBonusGame(int min, int max) {
        CUnderwaterLifeSeashells seashells = new CUnderwaterLifeSeashells(super.parent);
        seashells.generate(min, max);
        super.parent.setBonusGameInstance(seashells);
        seashells.save();
    }

    public Symbol[] getSymbols() {
        return symbols;
    }

    public Symbol[] getBlankSymbols() {
        return blank_symbols;
    }

    public int[] getPaySums() {
        return pay_sums;
    }

    public void setPaySums(int[] pay_sums_) {
        pay_sums = pay_sums_;
    }

    public Combination[][] getPaySymbols() {
        return pay_symbols;
    }

    public void setPaySymbols(Combination[][] pay_symbols_) {
        pay_symbols = pay_symbols_;
    }

    // ISlotsConfig

    @Override
    public int[][] getCombinations() {
        return pay_table;
    }

    @Override
    public int[] getValidBets() {
        return valid_bets;
    }

    @Override
    public int[] getValidLines() {
        return valid_lines;
    }

    @Override
    public int getReelsCount() {
        return reels_count;
    }

    @Override
    public int getSymbolsOnReel() {
        return symbols_on_reel;
    }

    @Override
    public Lines getLinesSet() {
        return lines_set;
    }

    @Override
    public RiskType getRiskType() {
        return risk_type;
    }

    @Override
    public int getRevision() {
        return revision;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getBackgroundPath() {
        return background_path;
    }

    @Override
    public String getSymbolsPath() {
        return symbols_path;
    }

    @Override
    public String getBackgroundHiPath() {
        return background_hi_path;
    }

    @Override
    public String getSymbolsHiPath() {
        return symbols_hi_path;
    }

    @Override
    public double getBlankPercent() {
        return blank_percent;
    }

    @Override
    public double getZeroNegativePercent() {
        return zero_negative_percent;
    }

    @Override
    public double getWildPercent() {
        return wild_percent;
    }

    @Override
    public BlankSymbol[] getBlankSpecialSymbols() {
        return blank_special_symbols;
    }

    @Override
    public BlankSymbol[] getBreakLongLines() {
        return break_long_lines;
    }

    @Override
    public int getWildMultiplier() {
        return wild_multiplier;
    }

    @Override
    public int[] getDropWild() {
        return drop_wild;
    }

    @Override
    public int[] getDropScatter() {
        return drop_scatter;
    }

    @Override
    public Symbol[] getNonWildSymbols() {
        return non_wild_symbols;
    }

}
