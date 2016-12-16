package mobi.square.slots.game.slots.config;

import mobi.square.slots.enums.Lines;
import mobi.square.slots.enums.RiskType;
import mobi.square.slots.enums.Symbol;
import mobi.square.slots.game.slots.CSlots;

public interface ISlotsConfig {

    double[] getReel(int index);

    int[][] getCombinations();

    void spin();

    void generateSymbols();

    void checkCombinations(CSlots slots);

    double getSymbolChance(int index, Symbol symbol);

    int getRevision();

    int getReelsCount();

    int getSymbolsOnReel();

    int[] getValidBets();

    int[] getValidLines();

    String getTitle();

    String getBackgroundPath();

    String getSymbolsPath();

    String getBackgroundHiPath();

    String getSymbolsHiPath();

    Lines getLinesSet();

    int getCanvasLinesType();

    RiskType getRiskType();

    int getWildMultiplier();

    /**
     * Использует ли автомат новую логику подбора комбинаций.
     *
     * @return true/false
     */
    boolean isNewLogic();

    /**
     * Возвращает процент выплат на автомате.
     *
     * @return double [0..1]
     */
    double getPayoutPercent();

    double getPayoutPercentPeriodic();

    /**
     * Возвращает минимальный процент нулевых выплат.
     *
     * @return double [0..1]
     */
    double getBlankPercent();

    /**
     * Возвращает процент дополнительных нулевых выплат при отрицательном балансе.
     *
     * @return double [0..1]
     */
    double getZeroNegativePercent();

    /**
     * Возвращает шанс выпадения WILD на выигрышной линии.
     *
     * @return double [0..1]
     */
    double getWildPercent();

    /**
     * Возвращает индексы запрещённых для символа WILD барабанов.
     *
     * @return int[]
     */
    int[] getDropWild();

    /**
     * Возвращает индексы запрещённых для символа SCATTER барабанов.
     *
     * @return int[]
     */
    int[] getDropScatter();

    /**
     * Возвращает символы, не заменяемые WILD'ом.
     *
     * @return Symbol[]
     */
    Symbol[] getNonWildSymbols();

    /**
     * Автомат доступен только для VIP пользователей
     *
     * @return true/false
     */
    boolean isRequiresVip();

    void changeLinesCount();

    void changeBet();
}
