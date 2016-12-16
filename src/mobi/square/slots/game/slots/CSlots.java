package mobi.square.slots.game.slots;

import mobi.square.slots.api.Connection;
import mobi.square.slots.enums.BonusGame;
import mobi.square.slots.enums.CardRank;
import mobi.square.slots.enums.CardSuit;
import mobi.square.slots.enums.SlotsType;
import mobi.square.slots.enums.Symbol;
import mobi.square.slots.error.StringCodeException;
import mobi.square.slots.game.slots.CWinLine.Multiplier;
import mobi.square.slots.game.slots.bonusgames.*;
import mobi.square.slots.game.slots.config.*;
import mobi.square.slots.game.slots.lines.CLines;
import mobi.square.slots.game.slots.lines.ILines;
import mobi.square.slots.game.slots.risk.CRiskColor;
import mobi.square.slots.game.slots.risk.CRiskGreater;
import mobi.square.slots.game.user.CUser;
import mobi.square.slots.logger.Log;
import mobi.square.slots.utils.utils;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CSlots implements Serializable {
	
	private static final long serialVersionUID = -3390609294865683865L;
	
	private transient CUser parent = null;
	
	private int bet = 1;
	private int lines_count = 1;
	private int award = 0;
	private int extra_award = 0;
	private int extra_spins = 0;
	private int free_spins = 0;
	private boolean risk_game = false;
	
	private BonusGame bonus_game_type = BonusGame.NONE;
	private SlotsType slots_type = SlotsType.GARAGE;
	private String effects = null;
	
	// New Logic
	private double current_payout_1 = 0.0d;
	private double current_payout_2 = 0.0d;
	private double current_payout_3 = 0.0d;
	private double current_payout_4 = 0.0d;
	private double current_payout_5 = 0.0d;
	private int total_payout = 0;
	private int total_spend = 0;
	private double period = 0;
	// 28.11.2013
	public int last_x2_payout = 0;
	public int last_x5_payout = 0;
	public int last_x10_payout = 0;
	public int last_x20_payout = 0;
	
	// Risk
	private CRiskColor risk_color = null;
	private CRiskGreater risk_greater = null;
	
	// Containers
	private CWinLine[] winning_lines = null;
	
	// Bonus Game
	private transient IBonusGame bonus_game = null;
	
	// Cache
	private transient boolean free_spin = false;
	private transient ILines lines = null;
	private transient ISlotsConfig config = null;
	private transient CSymbol[][] current_symbols = null;
	private transient CSymbol[][] previous_symbols = null;
	private transient boolean bet_changed = false;
	private transient int last_spin_award = 0;
	public transient int single_line_row = 0;
	public transient int total_free_spins = 0;
	public transient int last_target_payout = 0;
	
	// Slots List
	private static List<Slots> slots_list = null;
	
	static { loadSlotsList(); }
	
	// Constructors

	public CSlots(CUser parent) {
		this.parent = parent;
	}
	
	// Map data <=> Object 
	
	public void pack(Map<String, String> strings, Map<String, Integer> integers) {
		integers.put("bet", this.bet);
		integers.put("lines_count", this.lines_count);
		integers.put("award", this.award);
		integers.put("extra_award", this.extra_award);
		integers.put("extra_spins", this.extra_spins);
		integers.put("free_spins", this.free_spins);
		integers.put("risk_game", this.risk_game ? 1 : 0);
		integers.put("cp1", (int)this.current_payout_1);
		integers.put("cp2", (int)this.current_payout_2);
		integers.put("cp3", (int)this.current_payout_3);
		integers.put("cp4", (int)this.current_payout_4);
		integers.put("cp5", (int)this.current_payout_5);
		integers.put("tp", this.total_payout);
		integers.put("ts", this.total_spend);
		integers.put("lp2", this.last_x2_payout);
		integers.put("lp5", this.last_x5_payout);
		integers.put("lp10", this.last_x10_payout);
		integers.put("lp20", this.last_x20_payout);
		strings.put("bonus_game_type", this.bonus_game_type != null ? this.bonus_game_type.toString() : BonusGame.NONE.toString());
		strings.put("slots_type", this.slots_type != null ? this.slots_type.toString() : SlotsType.GARAGE.toString());
		List<CCard> cards = this.risk_color != null ? this.risk_color.getLastCards() : new LinkedList<CCard>();
		integers.put("cards", cards.size());
		for (int i = 0; i < 4; i++) {
			CCard card = i >= cards.size() ? null : cards.get(i);
			if (card != null) {
				strings.put("card".concat(String.valueOf(i)), card.getRank().toString().concat("_").concat(card.getSuit().toString()));
			} else strings.put("card".concat(String.valueOf(i)), null);
		}
	}
	
	public void unpack(Map<String, String> strings, Map<String, Integer> integers) {
		this.bet = this.value(integers, "bet", this.bet);
		this.lines_count = this.value(integers, "lines_count", this.lines_count);
		this.award = this.value(integers, "award", this.award);
		this.extra_award = this.value(integers, "extra_award", this.extra_award);
		this.extra_spins = this.value(integers, "extra_spins", this.extra_spins);
		this.free_spins = this.value(integers, "free_spins", this.free_spins);
		this.risk_game = this.value(integers, "risk_game", this.risk_game ? 1 : 0) > 0 ? true : false;
		this.current_payout_1 = (double)this.value(integers, "cp1", (int)this.current_payout_1);
		this.current_payout_2 = (double)this.value(integers, "cp2", (int)this.current_payout_2);
		this.current_payout_3 = (double)this.value(integers, "cp3", (int)this.current_payout_3);
		this.current_payout_4 = (double)this.value(integers, "cp4", (int)this.current_payout_4);
		this.current_payout_5 = (double)this.value(integers, "cp5", (int)this.current_payout_5);
		this.total_payout = this.value(integers, "tp", this.total_payout);
		this.total_spend = this.value(integers, "ts", this.total_spend);
		this.last_x2_payout = this.value(integers, "lp2", this.last_x2_payout);
		this.last_x5_payout = this.value(integers, "lp5", this.last_x5_payout);
		this.last_x10_payout = this.value(integers, "lp10", this.last_x10_payout);
		this.last_x20_payout = this.value(integers, "lp20", this.last_x20_payout);
		if (strings.get("bonus_game_type") != null) {
			try {
				this.bonus_game_type = BonusGame.valueOf(strings.get("bonus_game_type"));
			} catch (Exception e) { this.bonus_game_type = BonusGame.NONE; }
		} else this.bonus_game_type = BonusGame.NONE;
		if (strings.get("slots_type") != null) {
			try {
				this.slots_type = SlotsType.valueOf(strings.get("slots_type"));
			} catch (Exception e) { this.slots_type = SlotsType.GARAGE; }
		} else this.slots_type = SlotsType.GARAGE;
		int cards = this.value(integers, "cards", 0);
		this.risk_color = new CRiskColor(this);
		List<CCard> cards_list = new LinkedList<CCard>();
		for (int i = 0; i < cards; i++) {
			String card = strings.get("card".concat(String.valueOf(i)));
			if (card != null) {
				String[] chunks = card.split("_");
				if (chunks.length != 2) break;
				try {
					cards_list.add(new CCard(CardSuit.valueOf(chunks[1]), CardRank.valueOf(chunks[0])));
				} catch (Exception e) { Log.log(e); }
			} else break;
		}
		this.risk_color.setLastCards(cards_list);
	}
	
	private int value(Map<String, Integer> map, String key, int value) {
		if (map == null) return value;
		if (key == null) return value;
		Integer integer = map.get(key);
		return integer != null ? integer.intValue() : value;
	}
	
	// Public Methods
	
	public String getTitle() {
		return this.getSlotsType().toString();
	}
	
	/**
	 * Запуск машины
	 * @throws StringCodeException
	 */
	public boolean spin() throws StringCodeException {
		this.setEffects(null);
		
		if (this.getExtraSpins() > 0) {
			this.setExtraSpins(this.getExtraSpins() - 1);
			this.setSpinWasFree(this.isFreeSpin());
		} else if (!this.isFreeSpin()) {
			int user_bet = this.getBet() * this.getLinesCount();
			
			if(!utils.gameUnlocked){
		
				utils.numberSpinDone ++;
				utils.accumalatedBet += user_bet;
				Connection.getWrapper().saveDataPixtel();
				//System.out.println("making Spin " + utils.numberSpinDone + " " +  user_bet);
			}
			
			this.setSpinWasFree(false);
			this.takeAward();
			if (user_bet > this.getParent().getMoney())
				throw new StringCodeException("err_not_enough_money");
			this.getParent().getController().withdrawMoney(user_bet);
			if (this.getConfig().isNewLogic()) {
				this.setTotalSpend(this.getTotalSpend() + user_bet);
				this.addPeriod(1.0d);
				this.setCurrentPayout(this.getCurrentPayout() + (double)user_bet * this.getConfig().getPayoutPercentPeriodic());
			}
		} else {
			System.out.println("here 4");
			this.setFreeSpins(this.getFreeSpins() - 1);
			this.setSpinWasFree(true);
		}
		if (this.isBetChanged()) {
			System.out.println("here 3");
			this.setPreviousSymbols(this.getEmptySymbols());
			this.setBetChanged(false);
		} else {
			System.out.println("here 2");
			this.setPreviousSymbols(this.getCurrentSymbols());
		}
		if (this.getConfig().isNewLogic()) {
			System.out.println("here 1");
			this.getConfig().spin();
		} else {
			
			System.out.println("here should change the symbol Spin");
			this.setCurrentSymbols(this.generateSymbols());
		}
		
		this.getConfig().checkCombinations(this);
		return true;
	}
	
	/**
	 * Забрать награду
	 */
	public void takeAward() {
		this.getParent().getController().addMoney(this.getAward());
		this.setAward(0);
		this.setRiskGame(false);
		this.setBonusGameType(BonusGame.NONE);
		//this.getParent().getController().save();
	}
	
	public void addPeriod(double value) {
		this.period += value;
		while (this.period >= 360.0d) {
			this.period -= 360.0d;
		}
		while (this.period < 0.0d) {
			this.period += 360.0d;
		}
	}
	
	/**
	 * Является ли следующий спин бонусным
	 * @return true/false
	 */
	public boolean isFreeSpin() {
		return this.getFreeSpins() > 0;
	}
	
	// JavaScript Methods
	
	public String getJSON() {
		StringBuilder json = new StringBuilder("{");
		
		// Header
		json.append("\"credit\":").append(this.getParent().getMoney()).append(",");
		json.append("\"award\":").append(this.getAward()).append(",");
		json.append("\"slot\":\"").append(this.getTitle()).append("\",");
		json.append("\"bet\":").append(this.getBet()).append(",");
		json.append("\"lines_count\":").append(this.getLinesCount()).append(",");
		json.append("\"free_spins\":").append(this.getFreeSpins()).append(",");
		json.append("\"auto_spins\":").append(!this.isBonusGame() && this.isCanSpin()).append(",");
		json.append("\"force_spin\":").append(this.getExtraSpins() > 0).append(",");
		
		// Symbols
		json.append("\"symbols\":");
		json.append(this.getSymbolsJSON());
		json.append(",");
		
		// Lines
		json.append("\"lines\":[");
		for (int i = 0; i < this.getWinningLines().length; i++) {
			CWinLine line = this.getWinningLines()[i];
			int[] indexes = this.getLines().getLines()[line.getIndex()];
			json.append("{\"symbols\":[");
			json.append(indexes[0]);
			for (int j = 1; j < indexes.length; j++)
				json.append(",").append(indexes[j]);
			json.append("],\"count\":");
			json.append(line.getCount());
			json.append(",\"symbol\":\"");
			json.append(line.getSymbol().toString());
			json.append("\",\"left\":");
			json.append(line.isLeft());
			json.append(",\"color\":\"");
			json.append(this.getLines().getColors()[line.getIndex()]);
			json.append("\",\"multiplers\":[");
			if (line.getMultipliers() != null) {
				for (int j = 0; j < line.getMultipliers().length; j++) {
					Multiplier multiplier = line.getMultipliers()[j];
					json.append("{\"reel\":");
					json.append(multiplier.getReel());
					json.append(",\"multiplier\":");
					json.append(multiplier.getMultiplier());
					if (j < line.getMultipliers().length - 1) {
						json.append("},");
					} else {
						json.append("}");
					}
				}
			}
			// json.append("]");
			json.append("],\"bonus_line\":");
			json.append(line.isBonusLine());
			if (i >= this.getWinningLines().length - 1) {
				json.append("}");
			} else json.append("},");
		}
		json.append("],");
		
		// Special Highlights
		json.append("\"specials\":[");
		for (int i = 0; i < this.getConfig().getReelsCount(); i++) {
			json.append("[");
			for (int j = 0; j < this.getConfig().getSymbolsOnReel(); j++) {
				json.append(this.getCurrentSymbols()[i][j].isSpecial());
				if (j < this.getConfig().getSymbolsOnReel() - 1) {
					json.append(",");
				}
			}
			if (i < this.getConfig().getReelsCount() - 1) {
				json.append("],");
			} else json.append("]");
		}
		json.append("],");
		
		// Buttons
		json.append("\"buttons\":{");
		json.append("\"betLess\":").append(this.isCanBetLess()).append(",");
		json.append("\"betMore\":").append(this.isCanBetMore()).append(",");
		json.append("\"linesLess\":").append(this.isCanLinesLess()).append(",");
		json.append("\"linesMore\":").append(this.isCanLinesMore()).append(",");
		json.append("\"betMax\":").append(this.isCanBetMax()).append(",");
		json.append("\"bonus\":").append(this.isBonusGame()).append(",");
		json.append("\"risk\":").append(this.isCanRisk()).append(",");
		json.append("\"lobby\":").append(this.isCanChangeRoom()).append(",");
		json.append("\"take\":").append( !this.isCanSpin() && this.getAward() > 0 ).append(",");
		json.append("\"spin\":").append(this.isCanSpin());
		json.append("},");
		
		// Effects
		json.append("\"effects\":[");
		json.append(this.getEffects());
		json.append("]");
		
		// End
		json.append("}");
		return json.toString();
	}
	
	public String getLinesJSON() {
		StringBuilder json = new StringBuilder();
		json.append("[");
		int lines_count = this.getLinesCount();
		int total = this.getLines().getLines().length;
		if (lines_count > total) lines_count = total;
		for (int i = 0; i < lines_count; i++) {
			int[] indexes = this.getLines().getLines()[i];
			json.append("{\"symbols\":[");
			json.append(indexes[0]);
			for (int j = 1; j < indexes.length; j++)
				json.append(",").append(indexes[j]);
			json.append("],\"color\":\"");
			json.append(this.getLines().getColors()[i]);
			json.append("\",\"count\":0");
			if (i >= lines_count - 1) {
				json.append("}");
			} else json.append("},");
		}
		json.append("]");
		return json.toString();
	}
	
	public String getSymbolsJSON() {
		StringBuilder json = new StringBuilder();
		json.append("[");
		for (int i = 0; i < this.getConfig().getReelsCount(); i++) {
			json.append("[");
			for (int j = 0; j < this.getConfig().getSymbolsOnReel(); j++) {
				json.append("\"");
				json.append(this.getCurrentSymbols()[i][j].getSymbol().toString());
				if (j >= this.getConfig().getSymbolsOnReel() - 1) {
					json.append("\"");
				} else json.append("\",");
			}
			if (i >= this.getConfig().getReelsCount() - 1) {
				json.append("]");
			} else json.append("],");
		}
		json.append("]");
		return json.toString();
	}
	
	// Bets & Lines
	
	/**
	 * Увеличить ставку
	 */
	public void betMore() {
		for (int i = 0; i < this.getValidBets().length; i++) {
			if (this.getValidBets()[i] > this.getBet()) {
				if (this.isMoneyEnough(this.getValidBets()[i], this.getLinesCount()))
					this.setBet(this.getValidBets()[i]);
				return;
			}
		}
	}
	
	/**
	 * Уменьшить ставку
	 */
	public void betLess() {
		if (!this.isCanBetLess()) return;
		for (int i = this.getValidBets().length - 1; i >= 0; i--) {
			if (this.getValidBets()[i] < this.getBet()) {
				this.setBet(this.getValidBets()[i]);
				return;
			}
		}
	}
	
	/**
	 * Увеличить количество линий
	 */
	public void linesMore() {
		for (int i = 0; i < this.getValidLines().length; i++) {
			if (this.getValidLines()[i] > this.getLinesCount()) {
				if (this.isMoneyEnough(this.getBet(), this.getValidLines()[i]))
					this.setLinesCount(this.getValidLines()[i]);
				return;
			}
		}
	}
	
	/**
	 * Уменьшить количество линий
	 */
	public void linesLess() {
		if (!this.isCanLinesLess()) return;
		for (int i = this.getValidLines().length - 1; i >= 0; i--) {
			if (this.getValidLines()[i] < this.getLinesCount()) {
				this.setLinesCount(this.getValidLines()[i]);
				return;
			}
		}
	}
	
	/**
	 * Можно ли увеличить ставку
	 * @return true/false
	 */
	public boolean isCanBetMore() {
		if (this.isFreeSpin()) return false;
		if (this.isBonusGame()) return false;
		for (int i = 0; i < this.getValidBets().length; i++) {
			if (this.getValidBets()[i] > this.getBet()) {
				if (this.isMoneyEnough(this.getValidBets()[i], this.getLinesCount())) {
					return true;
				} else return false;
			}
		}
		return false;
	}
	
	/**
	 * Можно ли уменьшить ставку
	 * @return true/false
	 */
	public boolean isCanBetLess() {
		if (this.isFreeSpin()) return false;
		if (this.isBonusGame()) return false;
		if (this.getBet() <= this.getValidBets()[0])
			return false;
		else return true;
	}
	
	/**
	 * Можно ли менять ставку
	 * @return true/false
	 */
	public boolean isCanChangeBet() {
		return this.isCanBetLess() || this.isCanBetMore();
	}
	
	/**
	 * Можно ли увеличить количество линий
	 * @return true/false
	 */
	public boolean isCanLinesMore() {
		if (this.isFreeSpin()) return false;
		if (this.isBonusGame()) return false;
		for (int i = 0; i < this.getValidLines().length; i++) {
			if (this.getValidLines()[i] > this.getLinesCount()) {
				if (this.isMoneyEnough(this.getBet(), this.getValidLines()[i])) {
					return true;
				} else return false;
			}
		}
		return false;
	}
	
	/**
	 * Можно ли уменьшить количество линий
	 * @return true/false
	 */
	public boolean isCanLinesLess() {
		if (this.isFreeSpin()) return false;
		if (this.isBonusGame()) return false;
		if (this.getLinesCount() <= this.getValidLines()[0])
			return false;
		else return true;
	}
	
	/**
	 * Можно ли менять количество линий
	 * @return true/false
	 */
	public boolean isCanChangeLines() {
		return this.isCanLinesLess() || this.isCanLinesMore();
	}
	
	/**
	 * Можео ли сыграть на максимальную ставку
	 * @return true/false
	 */
	public boolean isCanBetMax() {
		if (this.isFreeSpin()) return false;
		if (this.isBonusGame()) return false;
		return true;
	}
	
	/**
	 * Можно ли выбрать другую комнату
	 * @return true/false
	 */
	public boolean isCanChangeRoom() {
		if (this.isFreeSpin()) {
			if (isCupSlots(this.getSlotsType())) {
				this.setFreeSpins(0);
			} else return false;
		}
		/*if (this.isBonusGame()) {
			if (isCupSlots(this.getSlotsType())) {
				this.setBonusGameType(BonusGame.NONE);
			} else return false;
		}*/
		return true; 
	}
	
	/**
	 * Задать максимально возможную ставку и количество линий
	 */
	public void maxBet() {
		
		int minimal_bet = this.getValidBets()[0];
		int lines_count = this.getValidLines()[0];
		for (int i = 0; i < this.getValidLines().length; i++) {
			if (this.getValidLines()[i] * minimal_bet <= this.getParent().getMoney()) {
				lines_count = this.getValidLines()[i];
			} else break;
		}
		if (this.getLinesCount() != lines_count)
			this.setLinesCount(lines_count);
		for (int i = 0; i < this.getValidBets().length; i++) {
			if (lines_count * this.getValidBets()[i] <= this.getParent().getMoney()) {
				minimal_bet = this.getValidBets()[i];
			} else break;
		}
		if (this.getBet() != minimal_bet) {
			this.setBet(minimal_bet);
		}
	}
	
	/**
	 * Задать минимально возможную ставку и количество линий
	 */
	public void minBet() {
		int minimal_bet = this.getValidBets()[0];
		int lines_count = this.getValidLines()[0];
		this.setBet(minimal_bet);
		this.setLinesCount(lines_count);
	}
	
	/**
	 * Хватает ли денег на игру
	 * @return true/false
	 */
	public boolean isMoneyEnough() {
		if (this.isFreeSpin()) return true;
		int user_bet = this.getBet() * this.getLinesCount();
		if (this.getParent().getMoney() < user_bet) {
			return false;
		} else return true;
	}
	
	/**
	 * Можно ли начать игру
	 * @return true/false
	 */
	public boolean isCanSpin() {
		if (this.isBonusGame()) {
			return true;
		}
		
		System.out.println("can spin " +  this.isMoneyEnough());
		return this.isMoneyEnough();
	}
	
	/**
	 * Можно ли начать игру на риск
	 * @return true/false
	 */
	public boolean isCanRisk() {
		if (this.isBonusGame()) return false;
		if (this.isFreeSpin()) return false;
		return this.getAward() > 0;
	}
	
	/**
	 * Добавить свободные вращения.
	 * @param spins - количество.
	 */
	public void addFreeSpins(int spins) {
		this.setFreeSpins(this.getFreeSpins() + spins);
		this.total_free_spins += spins;
	}
	
	public void setSlotsType(SlotsType slots_type) {
		if (this.slots_type == slots_type) return;
		this.setBonusGameType(BonusGame.NONE);
		this.setFreeSpins(0);
		this.setExtraSpins(0);
		this.setExtraAward(0);
		this.takeAward();
		this.slots_type = slots_type;
		this.current_symbols = null;
		this.setConfig(null);
		this.setLines(null);
		this.setBet(this.getConfig().getValidBets()[0]);
		this.setLinesCount(this.getConfig().getValidLines()[this.getConfig().getValidLines().length - 1]);
		if (this.getConfig().isNewLogic())
			this.getConfig().generateSymbols();
		this.bet_changed = false;
	}
	
	public List<Slots> getSlotsList() {
		if (slots_list == null)
			loadSlotsList();
		return slots_list;
	}
	
	public void plusCurrentPayout() {
		this.current_payout_1 = this.current_payout_1 > 0 ? this.current_payout_1 : 0.0d;
		this.current_payout_2 = this.current_payout_2 > 0 ? this.current_payout_2 : 0.0d;
		this.current_payout_3 = this.current_payout_3 > 0 ? this.current_payout_3 : 0.0d;
		this.current_payout_4 = this.current_payout_4 > 0 ? this.current_payout_4 : 0.0d;
		this.current_payout_5 = this.current_payout_5 > 0 ? this.current_payout_5 : 0.0d;
	}
	
	public void clearCurrentPayout() {
		this.current_payout_1 = 0.0d;
		this.current_payout_2 = 0.0d;
		this.current_payout_3 = 0.0d;
		this.current_payout_4 = 0.0d;
		this.current_payout_5 = 0.0d;
		this.total_free_spins = 0;
	}
	
	// Static Methods
	
	public static ISlotsConfig getConfig(CSlots slots, SlotsType type) {
		switch (type) {
			case FAIRYTALE:				return new mobi.square.slots.game.slots.v3.config.Fairytale(slots);
			case UNDERWATER_LIFE:		return new mobi.square.slots.game.slots.v3.config.UnderwaterLife(slots);
			case GLADIATORS:			return new mobi.square.slots.game.slots.v4.config.Gladiators(slots);
			case CRAZY_MONKEY:			return new mobi.square.slots.game.slots.v4.config.CrazyMonkey(slots);
			case GARAGE:				return new mobi.square.slots.game.slots.v4.config.Garage(slots);
			case MONEY_GAME:			return new mobi.square.slots.game.slots.v4.config.MoneyGame(slots);
			case RESIDENT:				return new mobi.square.slots.game.slots.v4.config.Resident(slots);
			case BOOK_OF_RA:			return new mobi.square.slots.game.slots.v4.config.BookOfRa(slots);
			case ROCKCLIMBER:			return new mobi.square.slots.game.slots.v4.config.RockClimber(slots);
			default:					return null;
		}
	}
	
	public static boolean isCupSlots(SlotsType type) {
		switch (type) {
			case JACK_BEANSTALK:	return true;
			case JACK_HAMMER:		return true;
			case MYTHIC_MAIDEN:		return true;
			case GOLD:				return true;
			default:				return false;
		}
	}
	
	// Private Methods
	
	/**
	 * Генерация символов с поправкой на счёт<br>
	 * пользователя и ежедневный бонус.
	 * @return CSymbol[][]
	 */
	protected CSymbol[][] generateSymbols2() {
		
		int mod = 0;
		double chance = 0.0d;
		int money = this.getParent().getMoney();
		CSymbol[][] symbols = null;
		
		// Модификатор шанса генерации
		if (money > 100000) {
			mod = money / 100000;
			if (mod > 10) mod = 10;
		} else mod = 0;
		
		// Генерируем символы, но не более 10 раз
		for (int i = 0; i < 10; i++) {
			
			int max_count = 0;
			symbols = this.generateSymbols();
			if (mod <= 0) break;
			
			// Находим максимальную длину цепочки символов
			for (int j = this.getLinesCount() - 1; j >= 0; j--) {
				int count = 0;
				int[] line = this.getLines().getLines()[j];
				Symbol symbol = symbols[0][line[0]].getSymbol();
				for (int k = 1; k < line.length; k++) {
					if (symbol == Symbol.WILD &&
						symbols[k][line[k]].getSymbol() != Symbol.WILD) {
						symbol = symbols[k][line[k]].getSymbol();
					}
					if (symbols[k][line[k]].getSymbol() == symbol ||
						symbols[k][line[k]].getSymbol() == Symbol.WILD) {
						count = k + 1;
					} else break;
				}
				if (count > max_count) {
					max_count = count;
				}
			}
			
			// Рассчитываем шанс генерации
			if (max_count >= 5) {
				chance = mod * 0.08d;
			} else if (max_count >= 4) {
				chance = mod * 0.04d;
			} else chance = 0.0d;
			
			// Проверяем шанс генерации
			if (chance > 0.0d) {
				if (chance < utils.getRandomDoubleMTF()) {
					break;
				}
			} else break;
			
		}
		
		return symbols;
	}
	
	/**
	 * Генерация символов на барабанах.<br>
	 * Отключены призовые игры при бонусных спинах.
	 * @return CSymbol[][]
	 */
	private CSymbol[][] generateSymbols() {
		int reels_count = this.getConfig().getReelsCount();
		int symbols_on_reel = this.getConfig().getSymbolsOnReel();
		CSymbol[][] symbols = new CSymbol[reels_count][];
		for (int i = 0; i < reels_count; i++) {
			symbols[i] = new CSymbol[symbols_on_reel];
			for (int j = 0; j < symbols_on_reel; j++) {
				double luck = utils.getRandomDoubleMTF();
				double[] reel = this.getConfig().getReel(i);
				double chance = 0.0;
				for (int k = 0; k < Symbol.getSymbolsCount(); k++) {
					chance += reel[k];
					if (luck < chance) {
						symbols[i][j] = new CSymbol(this, Symbol.convert(k));
						// Запрещаем выпадение призовой
						// игры во время бонусных вращений
						if (this.isFreeSpin() &&
							symbols[i][j].getSymbol() == Symbol.BONUS) {
							luck = utils.getRandomDoubleMTF();
							chance = 0.0;
							k = -1;
						} else break;
					}
				}
			}
		}
		return symbols;
	}
	
	private static synchronized void loadSlotsList() {
		if (slots_list != null) return;
		slots_list = new LinkedList<Slots>();
		Slots slots;
		
		slots = new Slots();
		slots.setType(SlotsType.FAIRYTALE);
		slots.setMinLevel(1);
		slots.setRoom(1);
		slots_list.add(slots);
		
		slots = new Slots();
		slots.setType(SlotsType.UNDERWATER_LIFE);
		slots.setMinLevel(1);
		slots.setRoom(1);
		slots_list.add(slots);
		
		slots = new Slots();
		slots.setType(SlotsType.GLADIATORS);
		slots.setMinLevel(1);
		slots.setRoom(1);
		slots_list.add(slots);
		
		slots = new Slots();
		slots.setType(SlotsType.BOOK_OF_RA);
		slots.setMinLevel(1);
		slots.setRoom(1);
		slots_list.add(slots);
		
		slots = new Slots();
		slots.setType(SlotsType.RESIDENT);
		slots.setMinLevel(1);
		slots.setRoom(1);
		slots_list.add(slots);
		
		slots = new Slots();
		slots.setType(SlotsType.GARAGE);
		slots.setMinLevel(1);
		slots.setRoom(1);
		slots_list.add(slots);
		
		slots = new Slots();
		slots.setType(SlotsType.CRAZY_MONKEY);
		slots.setMinLevel(1);
		slots.setRoom(1);
		slots_list.add(slots);
		
		slots = new Slots();
		slots.setType(SlotsType.MONEY_GAME);
		slots.setMinLevel(1);
		slots.setRoom(1);
		slots_list.add(slots);
		
		slots = new Slots();
		slots.setType(SlotsType.ROCKCLIMBER);
		slots.setMinLevel(1);
		slots.setRoom(1);
		slots_list.add(slots);
	}
	
	/**
	 * Хватает ли денег на игру
	 * @param bet - ставка
	 * @param lines - количество линий
	 * @return true/false
	 */
	private boolean isMoneyEnough(int bet, int lines) {
		int user_bet = bet * lines;
		if (this.getParent().getMoney() < user_bet)
			return false;
		else return true;
	}
	
	private CSymbol[][] getEmptySymbols() {
		int reels_count = this.getConfig().getReelsCount();
		int symbols_on_reel = this.getConfig().getSymbolsOnReel();
		CSymbol[][] symbols = new CSymbol[reels_count][];
		for (int i = 0; i < reels_count; i++) {
			symbols[i] = new CSymbol[symbols_on_reel];
			for (int j = 0; j < symbols_on_reel; j++) {
				symbols[i][j] = new CSymbol(this, Symbol.N01);
			}
		}
		return symbols;
	}
	
	// Getters & Setters
	
	public CUser getParent() {
		return this.parent;
	}

	public void setParent(CUser parent) {
		this.parent = parent;
	}

	public ISlotsConfig getConfig() {
		if (this.config == null)
			this.config = getConfig(this, this.getSlotsType());
		return this.config;
	}

	public void setConfig(ISlotsConfig config) {
		this.config = config;
	}

	public ILines getLines() {
		if (this.lines == null)
			this.lines = CLines.newLines(this);
		return this.lines;
	}

	public void setLines(ILines lines) {
		if (!this.isFreeSpin()) {
			this.lines = lines;
		}
	}

	/**
	 * Текущая ставка пользователя.
	 * @return int
	 */
	public int getBet() {
		return this.bet;
	}

	/**
	 * Текущая ставка пользователя.
	 * @param bet - int.
	 */
	public void setBet(int bet) {
		if (!this.isFreeSpin()) {
			this.bet_changed = true;
			this.bet = bet;
			this.getConfig().changeBet();
		}
	}

	/**
	 * Количество активных линий.
	 * @return int
	 */
	public int getLinesCount() {
		return this.lines_count;
	}

	/**
	 * Количество активных линий.
	 * @param lines_count - int.
	 */
	public void setLinesCount(int lines_count) {
		this.bet_changed = true;
		this.lines_count = lines_count;
		this.getConfig().changeLinesCount();
	}

	public CSymbol[][] getCurrentSymbols() {
		if (this.current_symbols == null)
			this.current_symbols = this.generateSymbols();
		return this.current_symbols;
	}
	
	public void setCurrentSymbols(CSymbol[][] current_symbols) {
		this.current_symbols = current_symbols;
	}

	/**
	 * Текущая награда.
	 * @return int
	 */
	public int getAward() {
		return this.award;
	}

	/**
	 * Текущая награда.
	 * @param award - int.
	 */
	public void setAward(int award) {
		this.award = award;
	}

	public void addAward(int award) {
		this.award += award;
	}
	
	/**
	 * Бесплатные вращения.
	 * @return int
	 */
	public int getFreeSpins() {
		return this.free_spins;
	}

	/**
	 * Бесплатные вращения
	 * @param free_spins - int.
	 */
	public void setFreeSpins(int free_spins) {
		this.free_spins = free_spins;
	}

	public CWinLine[] getWinningLines() {
		if (this.winning_lines == null)
			this.winning_lines = new CWinLine[0];
		return this.winning_lines;
	}

	public void setWinningLines(CWinLine[] winning_lines) {
		this.winning_lines = winning_lines;
	}

	public int[] getValidBets() {
		return this.getConfig().getValidBets();
	}

	public int[] getValidLines() {
		return this.getConfig().getValidLines();
	}

	/**
	 * Зашёл ли пользователь в риск.
	 * @return true/false
	 */
	public boolean isRiskGame() {
		return this.risk_game;
	}

	/**
	 * Зашёл ли пользователь в риск.
	 * @param risk_game - boolean
	 */
	public void setRiskGame(boolean risk_game) {
		this.risk_game = risk_game;
	}

	public SlotsType getSlotsType() {
		if (this.slots_type == null)
			this.slots_type = SlotsType.GLADIATORS;
		return this.slots_type;
	}
	
	public void setSlotsType(int slots_type) {
		this.setSlotsType(SlotsType.convert(slots_type));
	}

	public CSymbol[][] getPreviousSymbols() {
		if (this.previous_symbols == null)
			this.previous_symbols = this.generateSymbols();
		return this.previous_symbols;
	}

	public void setPreviousSymbols(CSymbol[][] previous_symbols) {
		this.previous_symbols = previous_symbols;
	}

	public String getEffects() {
		if (this.effects == null)
			this.effects = "";
		return this.effects;
	}

	public void setEffects(String effects) {
		this.effects = effects;
	}

	/**
	 * Является ли текущее вращение бонусным.
	 * @return true/false
	 */
	public boolean isSpinWasFree() {
		return this.free_spin;
	}

	/**
	 * Является ли текущее вращение бонусным.
	 * @param free_spin - boolean.
	 */
	public void setSpinWasFree(boolean free_spin) {
		this.free_spin = free_spin;
	}

	/**
	 * Принудительные вращения, не снимают деньги и бонусные спины.
	 * @return int
	 */
	public int getExtraSpins() {
		return this.extra_spins;
	}

	/**
	 * Принудительные вращения, не снимают деньги и бонусные спины.
	 * @param extra_spins - int.
	 */
	public void setExtraSpins(int extra_spins) {
		this.extra_spins = extra_spins;
	}

	/**
	 * Дополнительная награда.
	 * @return int
	 */
	public int getExtraAward() {
		return this.extra_award;
	}

	/**
	 * Дополнительная награда.
	 * @param extra_award - int.
	 */
	public void setExtraAward(int extra_award) {
		this.extra_award = extra_award;
	}
	
	public CRiskColor getRiskColor() {
		if (this.risk_color == null)
			this.risk_color = new CRiskColor(this);
		this.risk_color.setParent(this);
		return this.risk_color;
	}

	public void setRiskColor(CRiskColor risk_color) {
		this.risk_color = risk_color;
	}

	public CRiskGreater getRiskGreater() {
		if (this.risk_greater == null)
			this.risk_greater = new CRiskGreater(this);
		this.risk_greater.setParent(this);
		return this.risk_greater;
	}

	public void setRiskGreater(CRiskGreater risk_greater) {
		this.risk_greater = risk_greater;
	}

	public boolean isBetChanged() {
		return this.bet_changed;
	}

	public void setBetChanged(boolean bet_changed) {
		this.bet_changed = bet_changed;
	}

	public double getCurrentPayout() {
		int bet = this.getBet() * this.getLinesCount();
		if (bet > 1000) {
			return this.current_payout_5;
		} else if (bet > 200) {
			return this.current_payout_4;
		} else if (bet > 70) {
			return this.current_payout_3;
		} else if (bet > 30) {
			return this.current_payout_2;
		} else {
			return this.current_payout_1;
		}
	}

	public void setCurrentPayout(double current_payout) {
		int bet = this.getBet() * this.getLinesCount();
		if (bet > 1000) {
			this.current_payout_5 = current_payout;
		} else if (bet > 200) {
			this.current_payout_4 = current_payout;
		} else if (bet > 70) {
			this.current_payout_3 = current_payout;
		} else if (bet > 30) {
			this.current_payout_2 = current_payout;
		} else {
			this.current_payout_1 = current_payout;
		}
	}

	// Плавающий баланс пользователя, не открывать.
	// Заменяет методы getCurrentPayout() и setCurrentPayout(double).

	/*private static final int[] payout_range = { 0, 30, 70, 200, 1000 };
	private double[] payout_array = new double[payout_range.length];

	private double[] getPayoutArray() {
		if (this.payout_array == null)
			this.payout_array = this.payoutArrayFromValues();
		if (this.payout_array.length != payout_range.length)
			this.payout_array = this.rearrangePayoutArray();
		return this.payout_array;
	}

	public double getCurrentPayout() {
		double[] payout_array = this.getPayoutArray();
		int bet = this.getBet() * this.getLinesCount();
		int i;
		for (i = payout_range.length - 1; i >= 0; i--) {
			if (bet > payout_range[i]) break;
		}
		return payout_array[i > 0 ? i : 0];
	}

	private double[] payoutArrayFromValues() {
		double[] result = new double[payout_range.length];
		if (payout_range.length > 0)
			result[0] = this.current_payout_1;
		if (payout_range.length > 1)
			result[1] = this.current_payout_2;
		if (payout_range.length > 2)
			result[2] = this.current_payout_3;
		if (payout_range.length > 3)
			result[3] = this.current_payout_4;
		if (payout_range.length > 4) {
			result[4] = this.current_payout_5;
		} else {
			result[3] += this.current_payout_5;
		}
		return result;
	}

	private double[] rearrangePayoutArray() {
		if (this.payout_array == null)
			return this.payoutArrayFromValues();
		double[] result = new double[payout_range.length];
		for (int i = 0; i < this.payout_array.length; i++) {
			if (i >= result.length) {
				result[result.length - 1] += this.payout_array[i];
			} else result[i] = this.payout_array[i];
		}
		return result;
	}

	public void setCurrentPayout(double current_payout) {
		int bet = this.getBet() * this.getLinesCount();
		int i;
		for (i = payout_range.length - 1; i >= 0; i--) {
			if (bet > payout_range[i]) break;
		}
		if (i < 0) i = 0;
		double[] payout_array = this.getPayoutArray();
		payout_array[i] = current_payout;
		if (i < payout_range.length - 1) {
			double min_value = (double)(-100 * payout_range[i + 1]);
			while (payout_array[i] < min_value) {
				payout_array[i + 1] += min_value;
				payout_array[i] -= min_value;
			}
			double max_value = (double)(10 * payout_range[i + 1]);
			if (payout_array[i] > 0 && payout_array[i + 1] < 0) {
				payout_array[i + 1] += max_value;
				payout_array[i] -= max_value;
			}
		}
	}*/

	public int getTotalPayout() {
		return this.total_payout;
	}

	public void setTotalPayout(int total_payout) {
		this.total_payout = total_payout;
	}

	public int getTotalSpend() {
		return this.total_spend;
	}

	public void setTotalSpend(int total_spend) {
		this.total_spend = total_spend;
	}
	
	public int getBalance1() {
		return (int)this.current_payout_1;
	}
	
	public int getBalance2() {
		return (int)this.current_payout_2;
	}
	
	public int getBalance3() {
		return (int)this.current_payout_3;
	}
	
	public int getBalance4() {
		return (int)this.current_payout_4;
	}
	
	public int getBalance5() {
		return (int)this.current_payout_5;
	}
	
	public double getPeriod() {
		return this.period;
	}
	
	public void setPeriod(double period) {
		this.period = period;
	}
	
	public BonusGame getBonusGameType() {
		if (this.bonus_game_type == null || this.bonus_game == null || this.bonus_game.getType() != this.bonus_game_type) {
			this.bonus_game_type = BonusGame.NONE;
		}
		return this.bonus_game_type;
	}

	public void setBonusGameType(BonusGame bonus_game_type) {
		this.bonus_game_type = bonus_game_type;
	}
	
	public boolean isBonusGame() {
		return this.getBonusGameType() != BonusGame.NONE;
	}
	
	public void setBonusGameInstance(IBonusGame bonus_game) {
		this.bonus_game = bonus_game;
		if (bonus_game != null) {
			this.setBonusGameType(bonus_game.getType());
		} else {
			this.setBonusGameType(BonusGame.NONE);
		}
	}

	public IBonusGame getBonusGameInstance() {
		if (this.bonus_game != null)
			this.bonus_game.setParent(this);
		return this.bonus_game;
	}

	public int getLastSpinAward() {
		return this.last_spin_award;
	}

	public void setLastSpinAward(int last_spin_award) {
		this.last_spin_award = last_spin_award;
	}
	
	public int getTotalBet() {
		return this.getLinesCount() * this.getBet();
	}

}
