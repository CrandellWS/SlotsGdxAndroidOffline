package mobi.square.slots.game.slots.v4.config;

import mobi.square.slots.enums.Lines;
import mobi.square.slots.enums.RiskType;
import mobi.square.slots.enums.Symbol;
import mobi.square.slots.game.slots.CSlots;
import mobi.square.slots.game.slots.CSymbol;
import mobi.square.slots.game.slots.bonusgames.CChests;
import mobi.square.slots.game.slots.v4.CSlotsBase;

public class Gladiators extends CSlotsBase {

    private static final Symbol[][] tapes = {
            // reel #1
            {
                    Symbol.N06,
                    Symbol.N01,
                    Symbol.N04,
                    Symbol.N02,
                    Symbol.N05,
                    Symbol.N03,
                    Symbol.N07,
                    Symbol.N02,
                    Symbol.WILD,
                    Symbol.N01,
                    Symbol.N04,
                    Symbol.N02,
                    Symbol.N01,
                    //
                    Symbol.N09,
                    //
                    Symbol.N05,
                    Symbol.N03,
                    Symbol.N01,
                    Symbol.N02,
                    Symbol.N06,
                    Symbol.N01,
                    Symbol.N04,
                    Symbol.N03,
                    Symbol.SCATTER,
                    Symbol.N02,
                    Symbol.N05,
                    Symbol.N01,
                    Symbol.N04,
                    Symbol.N03,
                    Symbol.BONUS,
                    Symbol.N02,
                    Symbol.N01,
                    Symbol.N05,
                    Symbol.N03,
                    Symbol.N02,
                    Symbol.WILD,
                    Symbol.N01,
                    Symbol.N04,
                    Symbol.N06,
                    Symbol.N02,
                    Symbol.N01,
                    Symbol.N03,
                    Symbol.N04,
                    Symbol.N01,
                    //
                    //Symbol.WILD,
                    //
                    Symbol.N02,
                    Symbol.N03,
                    Symbol.N01,
                    Symbol.N02,
                    Symbol.N08,
                    Symbol.N01,
                    Symbol.N09,
                    Symbol.SCATTER
            },
            // reel #2
            {
                    Symbol.N06,
                    Symbol.N01,
                    Symbol.N04,
                    Symbol.N02,
                    Symbol.N05,
                    Symbol.N03,
                    Symbol.N07,
                    Symbol.N02,
                    Symbol.N03,
                    //Symbol.WILD,
                    Symbol.N01,
                    Symbol.N04,
                    Symbol.N02,
                    Symbol.N05,
                    Symbol.N03,
                    //
                    Symbol.WILD,
                    //
                    Symbol.N01,
                    Symbol.N02,
                    Symbol.N06,
                    Symbol.N01,
                    Symbol.SCATTER,
                    Symbol.N04,
                    Symbol.N03,
                    Symbol.N02,
                    Symbol.N05,
                    //
                    Symbol.N09,
                    //
                    Symbol.N01,
                    Symbol.N04,
                    Symbol.N03,
                    Symbol.BONUS,
                    Symbol.N02,
                    Symbol.N01,
                    Symbol.N05,
                    Symbol.N03,
                    Symbol.N02,
                    Symbol.WILD,
                    Symbol.N01,
                    Symbol.N04,
                    Symbol.N06,
                    Symbol.N02,
                    Symbol.N01,
                    Symbol.N03,
                    Symbol.N04,
                    Symbol.N01,
                    Symbol.N02,
                    Symbol.N03,
                    Symbol.N01,
                    Symbol.N02,
                    Symbol.N08,
                    Symbol.N01,
                    Symbol.N09,
                    Symbol.SCATTER
            },
            // reel #3
            {
                    Symbol.N06,
                    Symbol.N01,
                    Symbol.N04,
                    Symbol.N02,
                    Symbol.N05,
                    Symbol.N01,
                    Symbol.N03,
                    Symbol.N07,
                    Symbol.N02,
                    Symbol.WILD,
                    Symbol.N01,
                    Symbol.N04,
                    Symbol.N02,
                    Symbol.N05,
                    Symbol.N03,
                    Symbol.N01,
                    Symbol.N02,
                    Symbol.SCATTER,
                    Symbol.N06,
                    Symbol.N01,
                    Symbol.N04,
                    Symbol.N03,
                    Symbol.N02,
                    //
                    Symbol.N09,
                    //
                    Symbol.N05,
                    Symbol.N01,
                    Symbol.N04,
                    Symbol.N03,
                    Symbol.BONUS,
                    Symbol.N02,
                    Symbol.N01,
                    Symbol.N05,
                    Symbol.N03,
                    Symbol.N02,
                    Symbol.WILD,
                    Symbol.N01,
                    Symbol.N04,
                    Symbol.N06,
                    Symbol.N02,
                    Symbol.N01,
                    Symbol.N03,
                    Symbol.N04,
                    Symbol.N01,
                    Symbol.N02,
                    Symbol.N03,
                    Symbol.N01,
                    Symbol.N02,
                    Symbol.N08,
                    Symbol.N01,
                    Symbol.N09,
                    Symbol.N02
            },
            // reel #4
            {
                    Symbol.N06,
                    Symbol.N01,
                    Symbol.N04,
                    Symbol.N02,
                    Symbol.N05,
                    Symbol.N03,
                    Symbol.N07,
                    Symbol.N02,
                    Symbol.WILD,
                    Symbol.N01,
                    Symbol.N04,
                    Symbol.N02,
                    Symbol.N05,
                    Symbol.N03,
                    Symbol.N01,
                    Symbol.N02,
                    Symbol.N06,
                    Symbol.SCATTER,
                    Symbol.N01,
                    Symbol.N04,
                    Symbol.N03,
                    Symbol.N02,
                    Symbol.N05,
                    Symbol.N01,
                    Symbol.N04,
                    Symbol.N03,
                    Symbol.BONUS,
                    Symbol.N02,
                    Symbol.N01,
                    Symbol.N05,
                    Symbol.N03,
                    Symbol.N02,
                    Symbol.WILD,
                    Symbol.N01,
                    Symbol.N04,
                    Symbol.N06,
                    Symbol.N02,
                    Symbol.N01,
                    Symbol.N03,
                    Symbol.N04,
                    //
                    Symbol.N09,
                    //
                    Symbol.N01,
                    Symbol.N02,
                    Symbol.N03,
                    Symbol.N01,
                    Symbol.N02,
                    Symbol.N08,
                    Symbol.N01,
                    Symbol.N09,
                    Symbol.SCATTER
            },
            // reel #5
            {
                    Symbol.N06,
                    Symbol.N01,
                    Symbol.N04,
                    Symbol.N02,
                    Symbol.N05,
                    Symbol.N03,
                    Symbol.N07,
                    Symbol.N02,
                    Symbol.WILD,
                    Symbol.N01,
                    Symbol.N04,
                    Symbol.N02,
                    Symbol.N05,
                    Symbol.N03,
                    Symbol.N01,
                    Symbol.N02,
                    Symbol.N06,
                    Symbol.N01,
                    Symbol.N04,
                    Symbol.N03,
                    Symbol.N02,
                    Symbol.N05,
                    Symbol.N01,
                    Symbol.N04,
                    Symbol.N03,
                    Symbol.BONUS,
                    Symbol.N02,
                    Symbol.N01,
                    Symbol.N05,
                    Symbol.N03,
                    Symbol.N02,
                    Symbol.WILD,
                    Symbol.N01,
                    Symbol.N04,
                    Symbol.N06,
                    Symbol.N02,
                    Symbol.N01,
                    Symbol.N03,
                    Symbol.N04,
                    Symbol.N01,
                    Symbol.N02,
                    Symbol.N03,
                    Symbol.N01,
                    Symbol.N02,
                    Symbol.N08,
                    Symbol.N01,
                    Symbol.N09,
                    Symbol.N02
            }
    };
    // 0, 1, 2, 3, 4, 5 совпадений в линии
    private static final int[][] pay_table = {
            // SCATTER
            {0, 0, 0, 0, 0},
            // BONUS
            {0, 0, 0, 0, 0},
            // WILD
            {0, 0, 150, 750, 2500},
            // N01
            {0, 0, 5, 10, 100},
            // N02
            {0, 0, 5, 15, 100},
            // N03
            {0, 0, 10, 15, 150},
            // N04
            {0, 0, 10, 25, 200},
            // N05
            {0, 0, 15, 50, 250},
            // N06
            {0, 0, 15, 75, 500},
            // N07
            {0, 5, 15, 100, 500},
            // N08
            {0, 10, 40, 300, 750},
            // N09
            {0, 0, 0, 0, 0},
    };
    // Процент пустых выплат
    private static final double blank_percent = 0.25d;
    // Процент дополнительных нулевых выплат при отрицательном балансе
    private static final double zero_negative_percent = 0.2d;
    // Процент выпадения WILD'ов на выигрышной комбинации
    private static final double wild_percent = 0.025d;
    // Индексы барабанов, на которых запрещено выпадение символа WILD
    private static final int[] drop_wild = {};
    // Индексы барабанов, на которых запрещено выпадение символа SCATTER
    private static final int[] drop_scatter = {};
    // Символы, не заменяемые WILD'ом
    private static final Symbol[] non_wild_symbols = {Symbol.SCATTER, Symbol.BONUS, Symbol.N09};
    // Возможные ставки
    private static final int[] valid_bets = {1, 3, 5, 8, 10, 25, 50, 100, 150}; //{ 1, 3, 5, 8, 10, 25, 50, 100, 150 };
    // Возможное количетсво линий
    private static final int[] valid_lines = {1, 5, 10, 15, 20};
    // Количество барабанов (нельзя менять)
    private static final int reels_count = 5;
    // Количество символов на барабанах
    private static final int symbols_on_reel = 3;
    // Умножение ставки по линии с WILD
    private static final int wild_multiplier = 1;
    // Набор игровых линий
    private static final Lines lines_set = Lines.LINES_3X_20_V1;
    // Тип игры на риск
    private static final RiskType risk_type = RiskType.COLOR;
    // Ревизия автомата
    private static final int revision = 1;
    // Название слотов (англ.)
    private static final String title = "gladiators";
    private static final String background_path = "/SlotsWeb3/resources/img/slots/gladiators_bg.js";
    private static final String symbols_path = "/SlotsWeb3/resources/img/slots/gladiators.js";
    private static final String background_hi_path = "/SlotsWeb3/resources/img/slots/gladiators_bg_hi.js";
    private static final String symbols_hi_path = "/SlotsWeb3/resources/img/slots/gladiators_hi.js";
    private static final int free_spins_count = 15;
    private static int[] pay_sums;
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
            Symbol.N09
    };

    // Logic

    public Gladiators(CSlots parent) {
        super(parent);
    }

    public static void load() {
    }

    @Override
    public void performLogic() {
    }

    @Override
    public void checkLogic() {
    }

    @Override
    public int checkPayout(int amount) {
        // Check special symbols
        int scatter_count = 0;
        int gladiator_count = 0;
        CSymbol[][] symbols = this.parent.getCurrentSymbols();
        for (int j = 0; j < reels_count; j++) {
            for (int k = 0; k < symbols_on_reel; k++) {
                if (symbols[j][k].getSymbol() == Symbol.SCATTER) {
                    scatter_count++;
                } else if (symbols[j][k].getSymbol() == Symbol.N09) {
                    gladiator_count++;
                }
            }
        }
        // SCATTER free spins
        if (scatter_count >= 3) {
            this.parent.addFreeSpins(free_spins_count);
        }
        // GLADIATOR multiplier
        if (gladiator_count >= 2) {
            switch (gladiator_count) {
                case 2:
                    amount *= 5;
                    break;
                case 3:
                    amount *= 10;
                    break;
                case 4:
                    amount *= 15;
                    break;
                case 5:
                default:
                    amount *= 20;
                    break;
            }
        }
        // Check bonus game
        int count, max_count = 0;
        int bet = super.parent.getBet() * super.parent.getLinesCount();
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
                    this.generateBonusGame(bet, bet * 3);
                    break;
                case 4:
                    this.generateBonusGame(bet, bet * 4);
                    break;
                case 5:
                default:
                    this.generateBonusGame(bet, bet * 5);
                    break;
            }
        }
        return amount;
    }

    // CSlotsBase

    public void generateBonusGame(int min, int max) {
        CChests chests = new CChests(super.parent);
        chests.generate(min, max);
        super.parent.setBonusGameInstance(chests);
    }

    public int[] getPaySums() {
        return pay_sums;
    }

    public void setPaySums(int[] pay_sums_) {
        pay_sums = pay_sums_;
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

    @Override
    public Symbol[] getTape(int reel) {
        return tapes[reel];
    }

    @Override
    public Symbol[] getSymbols() {
        return symbols;
    }

    @Override
    public Symbol[] getBlankSymbols() {
        return blank_symbols;
    }

}