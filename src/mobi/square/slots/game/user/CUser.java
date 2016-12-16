package mobi.square.slots.game.user;

import java.io.Serializable;

import mobi.square.slots.config.ApiConfig;
import mobi.square.slots.game.controller.CJsonController;
import mobi.square.slots.game.slots.CSlots;
import mobi.square.slots.utils.utils;

public class CUser implements Serializable {

	private static final long serialVersionUID = 1L;

	private String UID = null;
	private CSlots slots = null;
	private int money = 0;

	// Bonus
	private int hourly_bonus_time = 0;
	private int super_bonus_count = 0;
	private int roulette_time = 0;
	private int roulette_multipler = 0;
	private int roulette_position = 0;
	private int roulette_bonus_spins = 0;

	// Controllers

	private transient CUserController controller = null;
	private transient CJsonController json_controller = null;

	// Constructors

	public CUser(String UID) {
		this.UID = UID;
	}

	// Public Methods

	// Getters & Setters

	public String getId() {
		return this.UID;
	}

	public String getUID() {
		return this.UID;
	}

	public void setUID(String UID) {
		this.UID = UID;
	}

	public CUserController getController() {
		if (this.controller == null)
			this.controller = new CUserController(this);
		this.controller.setParent(this);
		return this.controller;
	}

	public void setController(CUserController controller) {
		this.controller = controller;
	}

	public CSlots getSlots() {
		if (this.slots == null)
			this.slots = new CSlots(this);
		this.slots.setParent(this);
		return this.slots;
	}

	public void setSlots(CSlots slots) {
		this.slots = slots;
	}

	public CJsonController getJsonController() {
		if (this.json_controller == null)
			this.json_controller = new CJsonController(this);
		return this.json_controller;
	}

	public void save() {
		this.getController().save();
	}

	public int getMoney() {
		return this.money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	// Bonus

	public int getHourlyBonusTime() {
		return this.hourly_bonus_time;
	}

	public void setHourlyBonusTime(int hourly_bonus_time) {
		this.hourly_bonus_time = hourly_bonus_time;
	}

	public int getSuperBonusCount() {
		return this.super_bonus_count;
	}

	public void setSuperBonusCount(int super_bonus_count) {
		this.super_bonus_count = super_bonus_count;
	}

	public int getRouletteTime() {
		return this.roulette_time;
	}

	public void setRouletteTime(int roulette_time) {
		this.roulette_time = roulette_time;
	}

	public int getRouletteMultipler() {
		if (utils.getTimestamp() >= this.getRouletteTime() + ApiConfig.RULETTE_RESET_TIME)
			this.roulette_multipler = 1;
		if (this.roulette_multipler == 0)
			this.roulette_multipler = 1;
		return this.roulette_multipler;
	}

	public void setRouletteMultipler(int roulette_multipler) {
		this.roulette_multipler = roulette_multipler;
	}

	public int getRoulettePosition() {
		return this.roulette_position;
	}

	public void setRoulettePosition(int roulette_position) {
		this.roulette_position = roulette_position;
	}

	public int getRouletteBonusSpins() {
		return this.roulette_bonus_spins;
	}

	public void setRouletteBonusSpins(int roulette_bonus_spins) {
		this.roulette_bonus_spins = roulette_bonus_spins;
	}

}
