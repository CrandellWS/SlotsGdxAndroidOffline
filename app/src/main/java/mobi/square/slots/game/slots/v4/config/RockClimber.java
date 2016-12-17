package mobi.square.slots.game.slots.v4.config;

import mobi.square.slots.enums.Lines;
import mobi.square.slots.enums.RiskType;
import mobi.square.slots.enums.Symbol;
import mobi.square.slots.game.slots.CSlots;
import mobi.square.slots.game.slots.CSymbol;
import mobi.square.slots.game.slots.bonusgames.CRockClimberCaves;
import mobi.square.slots.game.slots.v4.CSlotsBase;
import mobi.square.slots.utils.utils;

public class RockClimber extends CSlotsBase {

    // 0, 1, 2, 3, 4, 5 совпадений в линии
    private static final int[][] pay_table = {
            // WILD
            {0, 0, 100, 500, 2000},
            // BONUS
            {0, 0, 0, 0, 0},
            // N01
            {0, 0, 2, 3, 10},
            // N02
            {0, 0, 3, 5, 20},
            // N03
            {0, 0, 5, 10, 50},
            // N04
            {0, 0, 10, 30, 100},
            // N05
            {0, 0, 20, 50, 200},
            // N06
            {0, 0, 30, 100, 500},
            // N07
            {0, 0, 200, 1000, 5000},
    };
    private static final Tapes tapes = new Tapes(new Tape[]{
            new Tape(50, new Symbol[]{
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
                    Symbol.N02
            }),
            new Tape(50, new Symbol[]{
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
                    Symbol.N02
            })
    });
    // Процент пустых выплат
    private static final double blank_percent = 0.25d;
    // Процент дополнительных нулевых выплат при отрицательном балансе
    private static final double zero_negative_percent = 0.35d;
    // Процент выпадения WILD'ов на выигрышной комбинации
    private static final double wild_percent = 0.05d;
    // Индексы барабанов, на которых запрещено выпадение символа WILD
    private static final int[] drop_wild = {};
    // Индексы барабанов, на которых запрещено выпадение символа SCATTER
    private static final int[] drop_scatter = {};
    // Символы, не заменяемые WILD'ом
    private static final Symbol[] non_wild_symbols = {Symbol.SCATTER, Symbol.N07, Symbol.BONUS};
    // Возможные ставки
    private static final int[] valid_bets = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 15, 20, 25, 50, 100};
    // Возможное количетсво линий
    private static final int[] valid_lines = {1, 3, 5, 7, 9};
    // Количество барабанов (нельзя менять)
    private static final int reels_count = 5;
    // Количество символов на барабанах
    private static final int symbols_on_reel = 3;
    // Умножение ставки по линии с WILD
    private static final int wild_multiplier = 1;
    // Набор игровых линий
    private static final Lines lines_set = Lines.LINES_3X_09_V1;
    // Тип игры на риск
    private static final RiskType risk_type = RiskType.GREATER;
    // Ревизия автомата
    private static final int revision = 1;
    // Название слотов (англ.)
    private static final String title = "rockclimber";
    private static final String background_path = "/SlotsWeb3/resources/img/slots/rockclimber_bg.js";
    private static final String symbols_path = "/SlotsWeb3/resources/img/slots/rockclimber_v2.js";
    private static final String background_hi_path = "/SlotsWeb3/resources/img/slots/rockclimber_bg_hi.js";
    private static final String symbols_hi_path = "/SlotsWeb3/resources/img/slots/rockclimber_hi.js";
    private static int[] pay_sums;
    private static Symbol[] blank_symbols = {
            Symbol.N01,
            Symbol.N02,
            Symbol.N03,
            Symbol.N04,
            Symbol.N05,
            Symbol.N06
    };
    private static Symbol[] symbols = {
            Symbol.WILD,
            Symbol.BONUS,
            Symbol.N01,
            Symbol.N02,
            Symbol.N03,
            Symbol.N04,
            Symbol.N05,
            Symbol.N06,
            Symbol.N07,
    };
    private Tape tape;

    public RockClimber(CSlots parent) {
        super(parent);
        this.tape = tapes.getRandomTape();
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
        int count_bonus = 0;
        CSymbol[][] current = super.parent.getCurrentSymbols();
        for (int j = 0; j < current.length; j++) {
            CSymbol[] line = current[j];
            for (int i = 0; i < line.length; i++) {
                if (line[i].getSymbol() == Symbol.BONUS) {
                    count_bonus++;
                }
            }
        }
        if (count_bonus >= 3) {
            for (int j = 0; j < current.length; j++) {
                CSymbol[] line = current[j];
                for (int i = 0; i < line.length; i++) {
                    if (line[i].getSymbol() == Symbol.BONUS) {
                        line[i].setSpecial(true);
                    }
                }
            }
            this.generateBonusGame();
        }

        return amount;
    }

    public void generateBonusGame() {
        CRockClimberCaves game_caves = new CRockClimberCaves(super.parent);
        game_caves.generate();
        super.parent.setBonusGameInstance(game_caves);
    }

    // CSlotsBase

    @Override
    protected int getLinePayout(Symbol[] sequence) {
        int count = 0;
        boolean left = true;
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
                } else if (sequence[i] == Symbol.WILD && !utils.contains(this.getNonWildSymbols(), target)) {
                    wild = true;
                    count++;
                } else break;
            }
        }

        if (count < 3 || target == null) {
            count = 0;
            wild = false;
            left = false;
            target = null;
            for (int i = sequence.length - 1; i >= 0; i--) {
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
                    } else if (sequence[i] == Symbol.WILD && !utils.contains(this.getNonWildSymbols(), target)) {
                        wild = true;
                        count++;
                    } else break;
                }
            }
        }

        super.count = count;
        super.target = target;
        super.left = left;
        if (target != null) {
            count = getPayTable(target)[count - 1];
            if (target != Symbol.WILD && wild) {
                count *= this.getWildMultiplier();
            }
        } else count = 0;
        return count;
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
        return this.tape.getTape(reel);
    }

}
