package mobi.square.slots.game.slots.bonusgames;

import java.util.HashMap;
import java.util.Map;

import mobi.square.slots.api.Connection;
import mobi.square.slots.enums.BonusGame;
import mobi.square.slots.game.slots.CSlots;
import mobi.square.slots.utils.utils;
import mobi.square.slots.utils.json.JsonNode;

public class CGarageBoxes extends CBonusGame {

	private static final long serialVersionUID = -4912233846086530830L;
	
	private int[] left_awards;
	private int[] middle_awards;
	private int[] right_awards;
	private int left_awards_position;
	private int middle_awards_position;
	private int right_awards_position;
	private ContentType left_content_type;
	private ContentType middle_content_type;
	private ContentType right_content_type;
	
	private static final int awards_count = 5;
	private static final int boxes_count = 5;
	
	private transient CBoxContent[] boxes;
	private transient boolean left_line_win = false;
	private transient boolean middle_line_win = false;
	private transient boolean right_line_win = false;
	private transient int opened_count;
	
	private static final int[] multipliers = { 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 5, 10, 15, 20, 25 };
	
	private static final ContentType[] contents = { 
		ContentType.LANTERN,
		ContentType.JIGSAW,
		ContentType.HAMMER,
		ContentType.WRENCH,
		ContentType.DISC 
	};
	
	public enum ContentType {
		POLICEMAN,
		LANTERN,
		JIGSAW,
		HAMMER,
		WRENCH,
		DISC,
		UNKNOWN;
		
		public ContentType convert(int id) {
			return ContentType.class.getEnumConstants()[id];
		}
	}
	
	public static class CBoxContent extends CBox {
		private static final long serialVersionUID = -8562248762963846372L;
		
		private ContentType content;
		
		public CBoxContent(ContentType content, int amount, int index) {
			super(amount, index);
			this.setContent(content);
		}
		public ContentType getContent() {
			return content;
		}
		public void setContent(ContentType content) {
			this.content = content;
		}
	}

	
	public CGarageBoxes(CSlots parent) {
		super(parent);
	}

	private int computeBonusAward(int[] awards) {
		int award = 0;
		for (int i = 0; i < awards.length; i++) {
			award += awards[i];
		}
		return award;
	}
	
	@Override
	public void proc(int index) {
		if (index < 0 || index > 4 || this.isOver()) {
			return;
		}
		CBoxContent box = this.getBoxes()[index];
		if (box.isOpened()) {
			return;
		}
		box.setOpened(true);
		this.opened_count += 1;
		if (box.getContent() == ContentType.POLICEMAN) {
			setOver(true);
		} else {
			this.award += box.getAmount();
			ContentType content = box.getContent();
			if (content == this.getLeftContentType()) {
				if (this.left_awards_position >= 0) {
					this.left_awards[this.left_awards_position--] = box.getAmount();
					if (this.left_awards_position == -1) {
						this.award += this.computeBonusAward(left_awards);
						this.setLeftLineWin(true);
					}
				}
			} else if (content == this.getMiddleContentType()) {
				if (this.middle_awards_position >= 0) {
					this.middle_awards[this.middle_awards_position--] = box.getAmount();
					if (this.middle_awards_position == -1) {
						this.award += this.computeBonusAward(middle_awards);
						this.setMiddleLineWin(true);
					}
				}
			} else if (content == this.getRightContentType()) {
				if (this.right_awards_position >= 0) {
					this.right_awards[this.right_awards_position--] = box.getAmount();
					if (this.right_awards_position == -1) {
						this.award += this.computeBonusAward(right_awards);
						this.setRightLineWin(true);
					}
				}
			}
		}
		if (this.opened_count == boxes_count) {
			this.setOver(true);
		}
	}

	@Override
	public void end() {
		this.award = 0;
		if (this.left_awards_position == -1 || this.middle_awards_position == -1 || this.right_awards_position == -1) {
			this.generateBonusLines();
		}
		this.save();
	}

	public void generate(int bet) {
		CGarageBoxes game = this.load();
		if (game == null) {
			this.generateBonusLines();
			this.save();
		} else {
			this.left_awards = game.left_awards;
			this.middle_awards = game.middle_awards;
			this.right_awards = game.right_awards;
			this.left_awards_position = game.left_awards_position;
			this.middle_awards_position = game.middle_awards_position;
			this.right_awards_position = game.right_awards_position;
			this.left_content_type = game.left_content_type;
			this.middle_content_type = game.middle_content_type;
			this.right_content_type = game.right_content_type;
		}
		
		CBoxContent[] boxes_array = new CBoxContent[boxes_count]; 
		for (int i = 0; i < boxes_count; i++) {
			int mult = CBonusGame.getRandomMultiplier(multipliers);
			if (mult == 0) {
				boxes_array[i] = new CBoxContent(ContentType.POLICEMAN, 0, i);
			} else {
				boxes_array[i] = new CBoxContent(contents[utils.getRandomIntMTF(contents.length)], mult * this.getParent().getTotalBet(), i);
			}
		}
		this.opened_count = 0;
		this.setBoxes(boxes_array);
		setOver(false);
	}
	
	private void generateBonusLines() {
		this.left_awards = new int[awards_count];
		this.middle_awards = new int[awards_count];
		this.right_awards = new int[awards_count];
		this.left_awards_position = awards_count - 1;
		this.middle_awards_position = awards_count - 1;
		this.right_awards_position = awards_count - 1;
		this.setLeftContentType(ContentType.WRENCH);
		this.setMiddleContentType(ContentType.HAMMER);
		this.setRightContentType(ContentType.JIGSAW);
	}

	@Override
	public BonusGame getType() {
		return BonusGame.GARAGE_BOXES;
	}

	public CBoxContent[] getBoxes() {
		return this.boxes;
	}

	public void setBoxes(CBoxContent[] boxes) {
		this.boxes = boxes;
	}
	public ContentType getLeftContentType() {
		return this.left_content_type;
	}

	public void setLeftContentType(ContentType left_content_type) {
		this.left_content_type = left_content_type;
	}

	public ContentType getMiddleContentType() {
		return this.middle_content_type;
	}

	public void setMiddleContentType(ContentType middle_content_type) {
		this.middle_content_type = middle_content_type;
	}

	public ContentType getRightContentType() {
		return this.right_content_type;
	}

	public void setRightContentType(ContentType right_content_type) {
		this.right_content_type = right_content_type;
	}
	
	public int[] getLeftAwards() {
		return this.left_awards;
	}
	
	public int[] getMiddleAwards() {
		return this.middle_awards;
	}
	
	public int[] getRightAwards() {
		return this.right_awards;
	}
	
	public int getSumLeftAwards() {
		return sum(this.left_awards);
	}
	
	public int getSumMiddleAwards() {
		return sum(this.middle_awards);
	}
	
	public int getSumRightAwards() {
		return sum(this.right_awards);
	}
	
	public int getLeftAwardsCount() {
		int count = (awards_count - (this.left_awards_position + 1)); 
		return count > 5 ? 5 : count;
	}
	
	public int getMiddleAwardsCount() {
		int count = (awards_count - (this.middle_awards_position + 1));
		return count > 5 ? 5 : count;
	}
	
	public int getRightAwardsCount() {
		int count = (awards_count - (this.right_awards_position + 1));
		return count > 5 ? 5 : count;
	}
	
	@Override
	public void save() {
		Map<String, String> strings = new HashMap<String, String>();
		Map<String, Integer> integers = new HashMap<String, Integer>();
		for (int i = 0; i < awards_count; i++) {
			integers.put("garage_l".concat(String.valueOf(i)), this.left_awards[i]);
			integers.put("garage_m".concat(String.valueOf(i)), this.middle_awards[i]);
			integers.put("garage_r".concat(String.valueOf(i)), this.right_awards[i]);
		}
		integers.put("garage_lp", this.left_awards_position);
		integers.put("garage_mp", this.middle_awards_position);
		integers.put("garage_rp", this.right_awards_position);
		Connection.getWrapper().writeData(strings, integers);
	}
	
	private CGarageBoxes load() {
		CGarageBoxes instance = new CGarageBoxes(null);
		instance.generateBonusLines();
		Map<String, String> strings = new HashMap<String, String>();
		Map<String, Integer> integers = new HashMap<String, Integer>();
		for (int i = 0; i < awards_count; i++) {
			integers.put("garage_l".concat(String.valueOf(i)), instance.left_awards[i]);
			integers.put("garage_m".concat(String.valueOf(i)), instance.middle_awards[i]);
			integers.put("garage_r".concat(String.valueOf(i)), instance.right_awards[i]);
		}
		integers.put("garage_lp", instance.left_awards_position);
		integers.put("garage_mp", instance.middle_awards_position);
		integers.put("garage_rp", instance.right_awards_position);
		Connection.getWrapper().readData(strings, integers);
		for (int i = 0; i < awards_count; i++) {
			instance.left_awards[i] = integers.get("garage_l".concat(String.valueOf(i)));
			instance.middle_awards[i] = integers.get("garage_m".concat(String.valueOf(i)));
			instance.right_awards[i] = integers.get("garage_r".concat(String.valueOf(i)));
		}
		instance.left_awards_position = integers.get("garage_lp");
		instance.middle_awards_position = integers.get("garage_mp");
		instance.right_awards_position = integers.get("garage_rp");
		return instance;
	}
	
	@Override
	public HashMap<String, JsonNode> getStateJson() {
		HashMap<String, JsonNode> json = super.getStateJson();
		java.util.List<JsonNode> boxes = new java.util.ArrayList<JsonNode>(5);
		for (int i = 0; i < 5; i++) {
			CBoxContent box = this.getBoxes()[i];
			HashMap<String, JsonNode> json_box = new HashMap<String, JsonNode>();
			json_box.put("index", new JsonNode(box.getIndex()));
			json_box.put("amount", new JsonNode(box.isOpened() ? box.getAmount() : 0));
			json_box.put("opened", new JsonNode(box.isOpened()));
			json_box.put("content", new JsonNode(box.isOpened() ? box.getContent().toString() : ContentType.UNKNOWN.toString()));
			boxes.add(new JsonNode(json_box));
		}
		json.put("boxes", new JsonNode(boxes));
		json.put("left_awards", new JsonNode(this.getLeftAwards()));
		json.put("middle_awards", new JsonNode(this.getMiddleAwards()));
		json.put("right_awards", new JsonNode(this.getRightAwards()));
		
		json.put("left_content", new JsonNode(this.getLeftContentType().toString()));
		json.put("middle_content", new JsonNode(this.getMiddleContentType().toString()));
		json.put("right_content", new JsonNode(this.getRightContentType().toString()));
		
		return json;
	}

	public boolean isLeftLineWin() {
		return this.left_line_win;
	}

	public void setLeftLineWin(boolean left_line_win) {
		this.left_line_win = left_line_win;
	}

	public boolean isMiddleLineWin() {
		return this.middle_line_win;
	}

	public void setMiddleLineWin(boolean middle_line_win) {
		this.middle_line_win = middle_line_win;
	}

	public boolean isRightLineWin() {
		return this.right_line_win;
	}

	public void setRightLineWin(boolean right_line_win) {
		this.right_line_win = right_line_win;
	}
	
	private static int sum(int[] array) {
		int sum = 0;
		for (int i = 0; i < array.length; i++) {
			sum += array[i];
		}
		return sum;
	}

}
