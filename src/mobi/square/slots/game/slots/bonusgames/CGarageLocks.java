package mobi.square.slots.game.slots.bonusgames;

import java.io.Serializable;
import java.util.HashMap;

import mobi.square.slots.enums.BonusGame;
import mobi.square.slots.game.slots.CSlots;
import mobi.square.slots.utils.utils;
import mobi.square.slots.utils.json.JsonNode;

public class CGarageLocks extends CBonusGame {

	private static final long serialVersionUID = 6145130742002973673L;

	private Lock[] locks;
	private boolean super_key_used;
	private int current;
	private transient boolean win = false;
	
	// Общее кол-во замков
	public static final int locks_count = 5;
	// Множетели ставки
	public static final double chance = 0.50;
	public static final int[] multipliers = { 5, 10, 10, 15, 20 };
	public static final int multipliers_bet_final = 250;
	
	public static class Lock implements Serializable {
		
		private static final long serialVersionUID = -6136874099570676030L;
		
		private int award;
		private boolean opened;
		private boolean current;
		private BoxState left_box_state;
		private BoxState right_box_state;
		
		public Lock(int award, boolean opened) {
			super();
			this.award = award;
			this.opened = opened;
			this.current = false;
		}
		
		public int getAward() {
			return award;
		}
		public void setAward(int award) {
			this.award = award;
		}
		public boolean isOpened() {
			return opened;
		}
		public void setOpened(boolean opened) {
			this.opened = opened;
		}
		public boolean isCurrent() {
			return current;
		}
		private void setCurrent(boolean current) {
			this.current = current;
		}
		public BoxState getLeftBoxState() {
			return left_box_state;
		}
		public void setLeftBoxState(BoxState left_box_state) {
			this.left_box_state = left_box_state;
		}
		public BoxState getRightBoxState() {
			return right_box_state;
		}
		public void setRightBoxState(BoxState right_box_state) {
			this.right_box_state = right_box_state;
		}
	}
	
	public CGarageLocks(CSlots parent) {
		super(parent);
	}
	
	public Lock[] getLocks() {
		return locks;
	}
	
	public int getCurrent() {
		return current;
	}

	public void setCurrent(int current) {
		locks[this.current].setCurrent(false);
		locks[current].setCurrent(true);
		this.current = current;
	}

	public boolean isSuperKeyUsed() {
		return super_key_used;
	}

	public void setSuperKeyUsed(boolean super_key_used) {
		this.super_key_used = super_key_used;
	}
	
	public void generate() {
		locks = new Lock[locks_count];
		for (int i = 0; i < locks_count; i++) {
			locks[i] = new Lock(0, false);
			locks[i].setLeftBoxState(BoxState.CLOSED);
			locks[i].setRightBoxState(BoxState.CLOSED);
		}
		setSuperKeyUsed(false);
		setCurrent(locks_count - 1);
		setOver(false);
	}
	
	public static int getMultiplierBetFinal() {
		return multipliers_bet_final;
	}
	
	public void nextLock() {
		if (this.getCurrent() != 0) {
			setCurrent(this.getCurrent() - 1);
		}
	}
	
	public boolean isAllOpened() {
		return this.getCurrent() == 0 && this.getCurrentLock().isOpened();
	}
	public Lock getCurrentLock() {
		return this.getLocks()[this.getCurrent()];
	}
	
	@Override
	public void proc(int index) {
		if (index < 0 || index > 2 || isOver()) {
			return;
		}
		int award = 0;
		int mult = CBonusGame.getRandomMultiplier(multipliers);
		
		if (index == 0) {
			if (!this.isSuperKeyUsed()) {
				award = mult * this.getParent().getTotalBet();
				this.getCurrentLock().setOpened(true);
				this.getCurrentLock().setAward(award);
				this.setSuperKeyUsed(true);
				if (utils.getRandomBoolean(0.5d)) {
					this.getCurrentLock().setLeftBoxState(BoxState.OPENED_WIN);
					this.getCurrentLock().setRightBoxState(BoxState.OPENED_FAIL);
				} else {
					this.getCurrentLock().setLeftBoxState(BoxState.OPENED_FAIL);
					this.getCurrentLock().setRightBoxState(BoxState.OPENED_WIN);
				}
				this.nextLock();
			} 
		} else {
			boolean opened = utils.getRandomBoolean(chance);
			if (opened) {
				award = mult * this.getParent().getTotalBet();
				this.getCurrentLock().setOpened(true);
				this.getCurrentLock().setAward(award);
				if (index == 1) {
					this.getCurrentLock().setLeftBoxState(BoxState.OPENED_WIN);
					this.getCurrentLock().setRightBoxState(BoxState.OPENED_FAIL);
				} else if (index ==2) {
					this.getCurrentLock().setLeftBoxState(BoxState.OPENED_FAIL);
					this.getCurrentLock().setRightBoxState(BoxState.OPENED_WIN);
				}
				this.nextLock();
			} else {
				if (index == 1) {
					this.getCurrentLock().setLeftBoxState(BoxState.OPENED_FAIL);
					this.getCurrentLock().setRightBoxState(BoxState.OPENED_WIN);
				} else if (index ==2) {
					this.getCurrentLock().setLeftBoxState(BoxState.OPENED_WIN);
					this.getCurrentLock().setRightBoxState(BoxState.OPENED_FAIL);
				}
				this.setOver(true);
			}
			
		}
		if (this.isAllOpened()) {
			award += this.getParent().getBet() * getMultiplierBetFinal();
			this.setOver(true);
			this.setWin(true);
		}
		this.award += award;
	}

	@Override
	public BonusGame getType() {
		return BonusGame.GARAGE_LOCKS;
	}
	
	
	@Override
	public void end() {
	}

	@Override
	public HashMap<String, JsonNode> getStateJson() {
		HashMap <String, JsonNode> json = super.getStateJson();
		
		Lock[] locks = this.getLocks();
		java.util.List<JsonNode> json_locks = new java.util.ArrayList<JsonNode>(locks.length);
		for (int i = 0; i < locks.length; i++) {
			Lock lock = locks[i];
			HashMap<String, JsonNode> json_lock = new HashMap<String, JsonNode>();
			json_lock.put("award", new JsonNode(lock.isOpened() ? lock.getAward() : 0));
			json_lock.put("opened", new JsonNode(lock.isOpened()));
			json_lock.put("current", new JsonNode(lock.isCurrent()));
			json_lock.put("left_state", new JsonNode(lock.getLeftBoxState().toString()));
			json_lock.put("right_state", new JsonNode(lock.getRightBoxState().toString()));
			json_locks.add(new JsonNode(json_lock));
		}
		json.put("locks", new JsonNode(json_locks));
		json.put("super_key_used", new JsonNode(this.isSuperKeyUsed()));
		return json;
	}

	public boolean isWin() {
		return this.win;
	}

	public void setWin(boolean win) {
		this.win = win;
	}

}
