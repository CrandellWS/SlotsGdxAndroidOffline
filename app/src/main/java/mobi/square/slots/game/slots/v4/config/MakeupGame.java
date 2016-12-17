package mobi.square.slots.game.slots.v4.config;

import mobi.square.slots.enums.Lines;
import mobi.square.slots.enums.RiskType;
import mobi.square.slots.enums.Symbol;
import mobi.square.slots.game.slots.CSlots;
import mobi.square.slots.game.slots.CSymbol;
import mobi.square.slots.game.slots.v4.CSlotsBase;

public class MakeupGame extends CSlotsBase {

    public static final Symbol[][] tapes = {
            {
                    Symbol.WILD,
                    Symbol.N10,
                    Symbol.N01,
                    Symbol.N07,
                    Symbol.N11,
                    Symbol.N10,
                    Symbol.N05,
                    Symbol.N02,
                    Symbol.N03,
                    Symbol.N07,
                    Symbol.N08,
                    Symbol.N06,
                    Symbol.SCATTER,
                    Symbol.N03,
                    Symbol.N04,
                    Symbol.N09,
                    Symbol.N10,
                    Symbol.N11,
                    Symbol.N09,
                    Symbol.N03,
                    Symbol.N02,
                    Symbol.N07,
                    Symbol.N06,
                    Symbol.N03,
                    Symbol.N04,
                    Symbol.N01,
                    Symbol.N03,
                    Symbol.N04,
                    //
                    Symbol.N02,
                    //
                    Symbol.N08,
                    Symbol.N07
            },
            {
                    Symbol.WILD,
                    Symbol.N01,
                    Symbol.SCATTER,
                    Symbol.N08,
                    Symbol.N10,
                    Symbol.N11,
                    Symbol.N01,
                    Symbol.N03,
                    Symbol.N07,
                    Symbol.N06,
                    //
                    //Symbol.SCATTER,
                    //
                    Symbol.N05,
                    Symbol.N03,
                    Symbol.N01,
                    Symbol.N08,
                    Symbol.N07,
                    Symbol.N09,
                    Symbol.N03,
                    Symbol.N02,
                    Symbol.N09,
                    //
                    Symbol.N01,
                    //
                    Symbol.N03,
                    Symbol.N10,
                    Symbol.N08,
                    Symbol.N09,
                    Symbol.N06,
                    Symbol.N03,
                    Symbol.N08,
                    Symbol.N01,
                    Symbol.N05,
                    Symbol.N04,
                    Symbol.N03
            },
            {
                    Symbol.WILD,
                    Symbol.N10,
                    Symbol.N02,
                    Symbol.N04,
                    Symbol.N11,
                    Symbol.N08,
                    Symbol.N05,
                    Symbol.N02,
                    Symbol.N04,
                    Symbol.N06,
                    Symbol.N02,
                    Symbol.SCATTER,
                    Symbol.N09,
                    Symbol.N11,
                    Symbol.N06,
                    Symbol.N01,
                    Symbol.N02,
                    Symbol.N04,
                    Symbol.N07,
                    Symbol.N06,
                    //
                    Symbol.N01,
                    //
                    Symbol.N02,
                    Symbol.N05,
                    Symbol.N09,
                    Symbol.N04,
                    Symbol.N08,
                    Symbol.N11,
                    Symbol.N04,
                    Symbol.N02,
                    Symbol.N03,
                    Symbol.N04
            },
            {
                    Symbol.WILD,
                    Symbol.N08,
                    Symbol.N03,
                    Symbol.N05,
                    Symbol.N01,
                    Symbol.N06,
                    Symbol.N07,
                    Symbol.N11,
                    //
                    //Symbol.SCATTER,
                    //
                    Symbol.N10,
                    Symbol.N05,
                    Symbol.N04,
                    Symbol.N05,
                    Symbol.N01,
                    Symbol.N02,
                    Symbol.N03,
                    Symbol.N09,
                    Symbol.N05,
                    Symbol.N08,
                    Symbol.N07,
                    Symbol.N01,
                    Symbol.N05,
                    Symbol.N09,
                    Symbol.SCATTER,
                    Symbol.N06,
                    Symbol.N10,
                    Symbol.N11,
                    //
                    Symbol.N02,
                    //
                    Symbol.N07,
                    Symbol.N06,
                    Symbol.N02,
                    Symbol.N01
            },
            {
                    Symbol.WILD,
                    Symbol.N09,
                    Symbol.N10,
                    Symbol.N03,
                    Symbol.N04,
                    Symbol.N08,
                    Symbol.N11,
                    Symbol.N10,
                    Symbol.N07,
                    Symbol.N02,
                    Symbol.N06,
                    Symbol.N08,
                    Symbol.N03,
                    //
                    //Symbol.SCATTER,
                    //
                    Symbol.N11,
                    Symbol.N02,
                    Symbol.N06,
                    Symbol.N04,
                    //
                    Symbol.N02,
                    //
                    Symbol.N05,
                    Symbol.N07,
                    Symbol.N09,
                    Symbol.N04,
                    Symbol.N03,
                    Symbol.N09,
                    Symbol.N11,
                    Symbol.N10,
                    Symbol.N07,
                    Symbol.N05,
                    Symbol.N01,
                    Symbol.N06,
                    Symbol.N02,
                    Symbol.N04,
                    Symbol.N05,
                    Symbol.N10,
                    Symbol.N08,
                    Symbol.N02,
                    Symbol.N09,
                    Symbol.N06,
                    Symbol.N01,
                    Symbol.N03,
                    Symbol.N11,
                    Symbol.N07,
                    Symbol.SCATTER,
                    Symbol.N10,
                    Symbol.N08
            }
    };
    public static final int[] pay_scatter = {0, 2, 5, 20, 500};
    // 0, 1, 2, 3, 4, 5 совпадений в линии
    private static final int[][] pay_table = {
            // SCATTER
            {0, 0, 0, 0, 0},
            // WILD
            {0, 10, 250, 2500, 9000},
            // N01 9
            {0, 2, 5, 25, 100},
            // N02 10
            {0, 0, 5, 25, 100},
            // N03 J
            {0, 0, 5, 25, 100},
            // N04 Q
            {0, 0, 5, 25, 100},
            // N05 K
            {0, 0, 10, 50, 125},
            // N06 A
            {0, 0, 10, 50, 125},
            // N07 монета
            {0, 0, 15, 75, 250},
            // N08 кошелек
            {0, 0, 15, 75, 250},
            // N09 доллары
            {0, 0, 20, 100, 400},
            // N10 сумка
            {0, 2, 25, 125, 750},
            // N11 мешок
            {0, 2, 25, 125, 750}
    };
    // the percentage of empty payments
    private static final double blank_percent = 0.05d;
    // Percentage additional payments with zero negative balance
    private static final double zero_negative_percent = 0.2d;
    // The percentage loss WILD's on the winning combination
    private static final double wild_percent = 0.04d;
    // Indices of reels on which symbols prohibited loss WILD symbol
    private static final int[] drop_wild = {};
    // Indices of reels on which symbols prohibited loss SCATTER
    private static final int[] drop_scatter = {};
    //  Characters that are not replaceable
    private static final Symbol[] non_wild_symbols = {Symbol.SCATTER};
    // Bet rates
    private static final int[] valid_bets = {1, 2, 5, 10, 20, 30, 50, 100}; //{ 1, 2, 5, 10, 20, 30, 50, 100, 200, 500 };
    // number of line options
    private static final int[] valid_lines = {1, 3, 5, 7, 9};
    // Number of drums/reels (can not be changed)
    private static final int reels_count = 5;
    // The number of symbols on the reels
    private static final int symbols_on_reel = 3;
    // Multiplication rates on line with WILD
    private static final int wild_multiplier = 2;
    // Set paylines
    private static final Lines lines_set = Lines.LINES_3X_09_V1;
    // Game type risks
    private static final RiskType risk_type = RiskType.COLOR;
    // Revision Version
    private static final int revision = 1;
    // Name slots (Eng.)
    private static final String title = "makeup_game";
    private static final String background_path = "/SlotsWeb3/resources/img/slots/makeup_game_bg.js";
    private static final String symbols_path = "/SlotsWeb3/resources/img/slots/makeup_game.js";
    private static final String background_hi_path = "/SlotsWeb3/resources/img/slots/makeup_game_bg_hi.js";
    private static final String symbols_hi_path = "/SlotsWeb3/resources/img/slots/makeup_game_hi.js";
    private static int[] pay_sums;
    private static Symbol[] blank_symbols = {
            Symbol.N01,
            Symbol.N02,
            Symbol.N03,
            Symbol.N04,
            Symbol.N05,
            Symbol.N06,
            //Symbol.N07,
            Symbol.N08,
            Symbol.N09,
            Symbol.N10,
            Symbol.N11
    };
    private static Symbol[] symbols = {
            Symbol.SCATTER,
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
            Symbol.N10,
            Symbol.N11
    };

    public MakeupGame(CSlots parent) {
        super(parent);
    }

    // Logic

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
        int scatter_count = 0;
        CSymbol[][] symbols = this.parent.getCurrentSymbols();
        for (int j = 0; j < reels_count; j++) {
            for (int k = 0; k < symbols_on_reel; k++) {
                if (symbols[j][k].getSymbol() == Symbol.SCATTER) {
                    scatter_count++;
                }
            }
        }
        if (scatter_count > 0) {
            amount += pay_scatter[scatter_count - 1] * this.parent.getBet() * this.parent.getLinesCount();
            if (scatter_count >= 3) {
                super.parent.addFreeSpins(15);
            }
        }
        if (super.parent.isSpinWasFree()) {
            amount *= 3;
        }
        return amount;
    }

    // CSlotsBase

    @Override
    protected int getSpecialPayout() {
        return 0;
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

}
