package mobi.square.slots.game.slots.bonusgames;

import java.util.HashMap;

import mobi.square.slots.enums.BonusGame;
import mobi.square.slots.game.slots.CSlots;
import mobi.square.slots.utils.json.JsonNode;

public class CResidentSafes extends CBonusGame {

	private static final long serialVersionUID = 1970247922417701522L;

	private boolean super_game;
	private static final int[] multipliers_safe = { 0, 0, 0, 0, 0, 20, 15, 10, 5, 5 };
	private static final int[] multipliers_super_game = { 0, 0, 0, 0, 250, 200, 100, 100, 50, 50 };

	private static final int safes_count = 4;
	private int opened_count;
	private CBox[] safes;
	private BoxState left_box_state;
	private BoxState right_box_state;
	private boolean extinguisher;

	public CResidentSafes(CSlots parent) {
		super(parent);
	}

	@Override
	public void proc(int index) {
		if (this.isOver() || (this.isSuperGame() && (index < 0 || index > 1)) || index < 0 || index > 3) {
			return;
		}
		if (isSuperGame()) {
			int mult = CBonusGame.getRandomMultiplier(multipliers_super_game);
			if (mult > 0) {
				if (index == 0) {
					this.setLeftBoxState(BoxState.OPENED_WIN);
					this.setRightBoxState(BoxState.OPENED_FAIL);
				} else {
					this.setLeftBoxState(BoxState.OPENED_FAIL);
					this.setRightBoxState(BoxState.OPENED_WIN);
				}
				this.award += (this.getParent().getBet() * this.getParent().getLinesCount()) * mult;
			} else {
				if (index == 0) {
					this.setLeftBoxState(BoxState.OPENED_FAIL);
					this.setRightBoxState(BoxState.OPENED_WIN);
				} else {
					this.setLeftBoxState(BoxState.OPENED_WIN);
					this.setRightBoxState(BoxState.OPENED_FAIL);
				}
			}
			this.setOver(true);
		} else {
			//if (!this.safes[index].current) return;
			CBox safe = this.getSafes()[index];
			if (safe.isOpened()) {
				return;
			}
			for (int i = this.getSafes().length - 1; i >= 0; i--) {
				this.getSafes()[i].current = i == index;
			}
			int mult = CBonusGame.getRandomMultiplier(multipliers_safe);
			safe.setOpened(true);
			this.opened_count += 1;
			
			if (mult > 0) {
				int amount = this.getParent().getLinesCount() * this.getParent().getBet() * mult;
				this.award += amount;
				safe.setAmount(amount);
				/*if (index < 3) {
					this.safes[index].current = false;
					this.safes[index + 1].current = true;
				}*/
			} else {
				safe.setAmount(0);
				if (this.isExtinguisher()) {
					this.setExtinguisher(false);
					/*if (index < 3) {
						this.safes[index].current = false;
						this.safes[index + 1].current = true;
					}*/
				} else {
					this.setOver(true);
				}
			}
			
			if (this.opened_count == safes_count && !this.isOver()) {
				this.setSuperGame(true);
			}
		}

	}

	public void generate(boolean extinguisher) {
		CBox[] safes = new CBox[safes_count];
		for (int i = 0; i < safes_count; i++) {
			safes[i] = new CBox(0, i);
		}
		safes[0].current = true;
		this.setSafes(safes);
		this.opened_count = 0;
		this.setLeftBoxState(BoxState.CLOSED);
		this.setRightBoxState(BoxState.CLOSED);
		this.setExtinguisher(extinguisher);
		this.setOver(false);
	}
	
	@Override
	public void end() {}

	@Override
	public BonusGame getType() {
		return BonusGame.RESIDENT_SAFES;
	}

	public boolean isSuperGame() {
		return this.super_game;
	}

	public void setSuperGame(boolean super_game) {
		this.super_game = super_game;
	}

	public CBox[] getSafes() {
		return safes;
	}

	public void setSafes(CBox[] safes) {
		this.safes = safes;
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

	@Override
	public HashMap<String, JsonNode> getStateJson() {
		HashMap<String, JsonNode> json = super.getStateJson();
		json.put("super_game", new JsonNode(this.isSuperGame()));
		java.util.List<JsonNode> ropes = new java.util.ArrayList<JsonNode>(safes_count);
		for (int i = 0; i < safes_count; i++) {
			CBox box = this.getSafes()[i];
			HashMap<String, JsonNode> json_rope = new HashMap<String, JsonNode>();
			json_rope.put("index", new JsonNode(box.getIndex()));
			json_rope.put("amount", new JsonNode(box.isOpened() ? box.getAmount() : 0));
			json_rope.put("current", new JsonNode(box.isCurrent()));
			json_rope.put("opened", new JsonNode(box.isOpened()));
			ropes.add(new JsonNode(json_rope));
		}
		json.put("safes", new JsonNode(ropes));
		json.put("left_box", new JsonNode(this.getLeftBoxState().toString()));
		json.put("right_box", new JsonNode(this.getRightBoxState().toString()));
		json.put("extinguisher", new JsonNode(this.isExtinguisher()));
		return json;
	}

	public boolean isExtinguisher() {
		return extinguisher;
	}

	public void setExtinguisher(boolean extinguisher) {
		this.extinguisher = extinguisher;
	}

}
