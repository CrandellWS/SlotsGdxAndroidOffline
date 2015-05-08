package mobi.square.slots.game.slots.config;

import mobi.square.slots.enums.Lines;
import mobi.square.slots.enums.RiskType;
import mobi.square.slots.enums.Symbol;
import mobi.square.slots.game.slots.CSlots;

public interface ISlotsConfig {

	public double[] getReel(int index);
	public int[][] getCombinations();

	public void spin();
	public void generateSymbols();
	public void checkCombinations(CSlots slots);
	public double getSymbolChance(int index, Symbol symbol);

	public int getRevision();
	public int getReelsCount();
	public int getSymbolsOnReel();
	public int[] getValidBets();
	public int[] getValidLines();
	public String getTitle();
	public String getBackgroundPath();
	public String getSymbolsPath();
	public String getBackgroundHiPath();
	public String getSymbolsHiPath();
	public Lines getLinesSet();
	public int getCanvasLinesType();
	public RiskType getRiskType();
	public int getWildMultiplier();

	/**
	 * Использует ли автомат новую логику подбора комбинаций.
	 * @return true/false
	 */
	public boolean isNewLogic();
	/**
	 * Возвращает процент выплат на автомате.
	 * @return double [0..1]
	 */
	public double getPayoutPercent();
	double getPayoutPercentPeriodic();
	/**
	 * Возвращает минимальный процент нулевых выплат.
	 * @return double [0..1]
	 */
	public double getBlankPercent();
	/**
	 * Возвращает процент дополнительных нулевых выплат при отрицательном балансе.
	 * @return double [0..1]
	 */
	public double getZeroNegativePercent();
	/**
	 * Возвращает шанс выпадения WILD на выигрышной линии.
	 * @return double [0..1]
	 */
	public double getWildPercent();
	/**
	 * Возвращает индексы запрещённых для символа WILD барабанов.
	 * @return int[]
	 */
	public int[] getDropWild();
	/**
	 * Возвращает индексы запрещённых для символа SCATTER барабанов.
	 * @return int[]
	 */
	public int[] getDropScatter();
	/**
	 * Возвращает символы, не заменяемые WILD'ом.
	 * @return Symbol[]
	 */
	public Symbol[] getNonWildSymbols();
	
	/**
	 * Автомат доступен только для VIP пользователей
	 * @return true/false
	 */
	public boolean isRequiresVip();

	public void changeLinesCount();
	
	public void changeBet();
}
