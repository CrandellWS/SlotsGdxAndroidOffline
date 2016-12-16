package mobi.square.slots.game.slots.v4.config;

import java.util.ArrayList;

import mobi.square.slots.enums.BonusGame;
import mobi.square.slots.enums.Lines;
import mobi.square.slots.enums.RiskType;
import mobi.square.slots.enums.Symbol;
import mobi.square.slots.game.slots.CSlots;
import mobi.square.slots.game.slots.CSymbol;
import mobi.square.slots.game.slots.bonusgames.CBookOfRaSlot;
import mobi.square.slots.game.slots.config.CEffects;
import mobi.square.slots.game.slots.v4.CSlotsBase;
import mobi.square.slots.utils.utils;

public class BookOfRa extends CSlotsBase {

    private static final Symbol[] tape = {
            Symbol.N01,
            Symbol.WILD,
            Symbol.N02,
            Symbol.N04,
            Symbol.N03,
            Symbol.N01,
            Symbol.N07,
            Symbol.N04,
            Symbol.N01,
            Symbol.N03,
            Symbol.N05,
            Symbol.N02,
            Symbol.N06,
            Symbol.N01,
            Symbol.N07,
            Symbol.N03,
            Symbol.N02,
            Symbol.N08,
            Symbol.N03,
            Symbol.N04,
            Symbol.N02,
            Symbol.N08,
            Symbol.N05,
            Symbol.N03,
            Symbol.N09,
            Symbol.N02,
            Symbol.N01,
            Symbol.N04,
            Symbol.N05,
            Symbol.N06,
            Symbol.N03,
            Symbol.N07,
            Symbol.N01,
            Symbol.N02,
            Symbol.N06,
            Symbol.N05,
            Symbol.N08,
            Symbol.N04,
            Symbol.N06,
            Symbol.N09,
            Symbol.N03,
            Symbol.N02,
            Symbol.N07,
    };
    private static final int[][] pay_table = {
            // WILD
            {0, 0, 18, 180, 1800},
            // N01
            {0, 0, 5, 25, 100},
            // N02
            {0, 0, 5, 25, 100},
            // N03
            {0, 0, 5, 25, 100},
            // N04
            {0, 0, 5, 40, 150},
            // N05
            {0, 0, 5, 40, 150},
            // N06
            {0, 5, 30, 100, 750},
            // N07
            {0, 5, 30, 100, 750},
            // N08
            {0, 5, 40, 400, 2000},
            // N09
            {0, 10, 100, 1000, 5000},
    };
    // Процент пустых выплат
    private static final double blank_percent = 0.25d;
    // Процент дополнительных нулевых выплат при отрицательном балансе
    private static final double zero_negative_percent = 0.3d;
    // Процент выпадения WILD'ов на выигрышной комбинации
    private static final double wild_percent = 0.07d;
    // Индексы барабанов, на которых запрещено выпадение символа WILD
    private static final int[] drop_wild = {};
    // Индексы барабанов, на которых запрещено выпадение символа SCATTER
    private static final int[] drop_scatter = {};
    // Символы, не заменяемые WILD'ом
    private static final Symbol[] non_wild_symbols = {};
    // Возможные ставки
    private static final int[] valid_bets = {1, 2, 3, 4, 5, 10, 15, 20, 30, 40, 50, 100};
    // Возможное количетсво линий
    private static final int[] valid_lines = {1, 3, 5, 7, 9};
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
    private static final String title = "book_of_ra";
    private static final String background_path = "/SlotsWeb3/resources/img/slots/book_of_ra_bg.js";
    private static final String symbols_path = "/SlotsWeb3/resources/img/slots/book_of_ra_v2.js";
    private static final String background_hi_path = "/SlotsWeb3/resources/img/slots/book_of_ra_bg_hi.js";
    private static final String symbols_hi_path = "/SlotsWeb3/resources/img/slots/book_of_ra_hi_v2.js";
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
    };
    // Logic
    private boolean bonus_mode = false;
    private Symbol bonus_symbol;
    private String effects;
    private Symbol[][] symbols2 = null;
    private ArrayList<SuperLine> super_lines;
    private boolean required_update_symbols = false;

    public BookOfRa(CSlots parent) {
        super(parent);
    }

    public static void load() {
    }

    private static int[] getPays(Symbol symbol) {
        switch (symbol) {
            case WILD:
                return pay_table[0];
            case N01:
                return pay_table[1];
            case N02:
                return pay_table[2];
            case N03:
                return pay_table[3];
            case N04:
                return pay_table[4];
            case N05:
                return pay_table[5];
            case N06:
                return pay_table[6];
            case N07:
                return pay_table[7];
            case N08:
                return pay_table[8];
            case N09:
                return pay_table[9];
            default:
                return pay_table[0];
        }
    }

    public Symbol[][] getSymbols2() {
        if (this.symbols2 == null) {
            this.symbols2 = new Symbol[this.getReelsCount()][];
            for (int i = 0; i < this.symbols2.length; i++) {
                this.symbols2[i] = new Symbol[this.getSymbolsOnReel()];
            }
        }
        return this.symbols2;
    }

    @Override
    public void performLogic() {
        if (this.isBonusMode() && this.parent.getBonusGameType() != BonusGame.BOOK_OF_RA_SLOT) {
            this.setBonusMode(false);
        }
    }

    public void generateBonusGame() {
        CBookOfRaSlot slot = new CBookOfRaSlot(this.parent);
        slot.generate(this);
        this.parent.setBonusGameInstance(slot);
    }

    @Override
    public void checkLogic() {
        this.setEffects("");
        int wild_count = 0;
        for (int i = 0; i < this.getReelsCount(); i++) {
            for (int j = 0; j < this.getSymbolsOnReel(); j++) {
                CSymbol symbol = this.parent.getCurrentSymbols()[i][j];
                if (symbol.getSymbol() == Symbol.WILD) {
                    wild_count += 1;
                }
            }
        }
        if (wild_count >= 3) {
            for (int i = 0; i < this.getReelsCount(); i++) {
                for (int j = 0; j < this.getSymbolsOnReel(); j++) {
                    CSymbol symbol = this.parent.getCurrentSymbols()[i][j];
                    if (symbol.getSymbol() == Symbol.WILD) {
                        symbol.setSpecial(true);
                    }
                }
            }

            this.parent.addFreeSpins(10);

            if (!this.isBonusMode()) {
                this.generateBonusGame();
            }
        }
    }

    @Override
    public int checkPayout(int amount) {
        if (this.isBonusMode()) {
            this.setRequiredUpdateSymbols(false);
            ArrayList<SuperLine> super_lines = this.getSuperLines();
            super_lines.clear();
            int bonus_symbols = 0;
            for (int i = 0; i < this.getReelsCount(); i++) {
                for (int j = 0; j < this.getSymbolsOnReel(); j++) {
                    CSymbol symbol = this.parent.getCurrentSymbols()[i][j];
                    if (symbol.getSymbol() == this.getBonusSymbol()) {
                        bonus_symbols += 1;
                        break;
                    }
                }
            }
            int[] pays = getPays(this.getBonusSymbol());
            if (bonus_symbols > 0 && pays[bonus_symbols - 1] > 0) {
                this.setRequiredUpdateSymbols(true);
                ArrayList<String> effects = new ArrayList<String>(this.getReelsCount());
                Symbol[][] symbols2 = this.getSymbols2();
                for (int i = 0; i < this.getReelsCount(); i++) {
                    String e = CEffects.checkMultiSymbolVertical(this.parent, this.getBonusSymbol(), i);
                    if (!e.isEmpty()) {
                        effects.add(e);
                        for (int j = 0; j < this.getSymbolsOnReel(); j++) {
                            symbols2[i][j] = this.getBonusSymbol();
                        }
                    } else {
                        for (int j = 0; j < this.getSymbolsOnReel(); j++) {
                            symbols2[i][j] = this.parent.getCurrentSymbols()[i][j].getSymbol();
                        }
                    }
                    this.setEffects(utils.implode(effects, ","));
                }
                // compute award
                int[][] lines = this.getPayLines();
                for (int i = 0; i < lines.length; i++) {
                    int[] line = lines[i];
                    SuperLine super_line = new SuperLine();
                    int rect_count = 0;
                    for (int j = 0; j < line.length; j++) {
                        super_line.getPositions()[j] = line[j];
                        if (symbols2[j][line[j]] == this.getBonusSymbol()) {
                            super_line.getRectangles()[j] = true;
                            rect_count += 1;
                        } else {
                            super_line.getRectangles()[j] = false;
                        }
                    }
                    if (rect_count >= 0 && pays[rect_count - 1] > 0) {
                        int pay = pays[rect_count - 1] * this.parent.getBet();
                        super_line.setAward(pay);
                        amount += pay;
                        super_lines.add(super_line);
                    }
                }
            }
        }
        return amount;
    }

    // CSlotsBase

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

    @Override
    public int[][] getCombinations() {
        return pay_table;
    }

    // ISlotsConfig

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

    public boolean isBonusMode() {
        return bonus_mode;
    }

    public void setBonusMode(boolean bonus_mode) {
        this.bonus_mode = bonus_mode;
    }

    public Symbol getBonusSymbol() {
        return bonus_symbol;
    }

    public void setBonusSymbol(Symbol bonus_symbol) {
        this.bonus_symbol = bonus_symbol;
    }

    public String getEffects() {
        return effects;
    }

    public void setEffects(String effects) {
        this.effects = effects;
    }

    public boolean isRequiredUpdateSymbols() {
        return required_update_symbols;
    }

    public void setRequiredUpdateSymbols(boolean required_update_symbols) {
        this.required_update_symbols = required_update_symbols;
    }

    public ArrayList<SuperLine> getSuperLines() {
        if (this.super_lines == null) {
            this.super_lines = new ArrayList<BookOfRa.SuperLine>();
        }
        return super_lines;
    }

    @Override
    public Symbol[] getTape(int reel) {
        return tape;
    }

    public static class SuperLine {
        private int[] positions;
        private boolean[] rectangles;
        private int award;

        public SuperLine() {
            this.positions = new int[reels_count];
            this.rectangles = new boolean[reels_count];
            this.award = 0;
        }

        public int[] getPositions() {
            return positions;
        }

        public boolean[] getRectangles() {
            return rectangles;
        }

        public int getAward() {
            return this.award;
        }

        public void setAward(int award) {
            this.award = award;
        }
    }


}
