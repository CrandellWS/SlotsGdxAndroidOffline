package mobi.square.slots.ui;

import mobi.square.slots.api.Connection;
import mobi.square.slots.classes.Card;
import mobi.square.slots.classes.Card.CardFlippedListener;
import mobi.square.slots.config.AppConfig;
import mobi.square.slots.containers.CardInfo;
import mobi.square.slots.tools.FontsFactory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class RiskGreaterGroup extends Group implements Resizable {

	private final TextureAtlas cards_atlas;
	private final Texture background;
	private final TextureRegionDrawable background_cards;
	private final TextureRegionDrawable background_chips;
	private final RiskTitleFrame title_frame;
	private final RiskAwardFrame bank_frame;
	private final RiskAwardFrame double_frame;
	private final RiskMainButton left_button;
	private final RiskMainButton right_button;
	private final Card[] cards;

	private RiskGreaterListener listener;
	private int choice_index;

	private CardFlippedListener choice_flipped = new CardFlippedListener() {
		@Override
		public void flipped() {
			if (listener != null) {
				listener.choiceCardOpened();
			}
		}
	};

	private CardFlippedListener all_flipped = new CardFlippedListener() {
		@Override
		public void flipped() {
			if (listener != null) {
				listener.choiceCardsOpened();
			}
		}
	};

	private CardFlippedListener game_flipped = new CardFlippedListener() {
		@Override
		public void flipped() {
			if (listener != null) {
				if (cards[4].isOpened()) {
					listener.gameCardOpened();
				} else {
					listener.gameCardClosed();
				}
			}
		}
	};

	private class CardListener extends ClickListener {
		protected final int index;
		public CardListener(int index) {
			this.index = index;
		}
	}

	public interface RiskGreaterListener {
		public void gameCardOpened();
		public void gameCardClosed();
		public void choiceCardClicked(int index);
		public void choiceCardOpened();
		public void choiceCardsOpened();
	}

	private RiskGreaterGroup(Texture background, TextureAtlas cards_atlas, TextureAtlas atlas) {
		this.listener = null;
		this.choice_index = 0;
		this.cards_atlas = cards_atlas;
		this.background = background;
		this.background_cards = new TextureRegionDrawable(atlas.findRegion("background_cards"));
		this.background_chips = new TextureRegionDrawable(atlas.findRegion("background_chips"));
		this.title_frame = RiskTitleFrame.newInstance(atlas);
		this.bank_frame = RiskAwardFrame.newInstance(atlas);
		this.double_frame = RiskAwardFrame.newInstance(atlas);
		this.left_button = RiskMainButton.newInstance(atlas, true);
		this.right_button = RiskMainButton.newInstance(atlas, false);
		this.cards = new Card[] {
			Card.newInstance(this.cards_atlas),
			Card.newInstance(this.cards_atlas),
			Card.newInstance(this.cards_atlas),
			Card.newInstance(this.cards_atlas),
			Card.newInstance(this.cards_atlas)
		};
		for (int i = 0; i < this.cards.length; super.addActor(this.cards[i++]));
		for (int i = 0; i < 4; i++) {
			this.cards[i].addListener(new CardListener(i) {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					if (listener != null) {
						listener.choiceCardClicked(super.index);
					}
				}
			});
		}
		this.cards[4].setCardFlippedListener(this.game_flipped);
		super.addActor(this.title_frame);
		super.addActor(this.double_frame);
		super.addActor(this.bank_frame);
		super.addActor(this.left_button);
		super.addActor(this.right_button);
	}

	public static RiskGreaterGroup newInstance(Texture background, TextureAtlas cards_atlas, TextureAtlas atlas) {
		RiskGreaterGroup instance = new RiskGreaterGroup(background, cards_atlas, atlas);
		instance.setBounds(0f, 0f, AppConfig.VIEWPORT_WIDTH, AppConfig.VIEWPORT_HEIGHT - GameHeader.HEADER_HEIGHT);
		instance.title_frame.setBounds(228f, 436f, 568f, 70f);
		instance.bank_frame.setBounds(228f, 270f, 132f, 132f);
		instance.double_frame.setBounds(664f, 270f, 132f, 132f);
		instance.left_button.setBounds(0f, 130f, 176f, 127f);
		instance.right_button.setBounds(848f, 130f, 176f, 127f);
		instance.bank_frame.setTextBank();
		instance.double_frame.setTextDouble();
		for (int i = 0; i < 4; i++)
			instance.cards[i].setBounds(224f + 152f * i, 30f, 120f, 172);
		instance.cards[4].setBounds(447f, 214f, 130f, 190f);
		return instance;
	}

	public void setTextRiskRules() {
		this.title_frame.setTextGreaterRules();
	}

	public void setTextRiskWin() {
		this.title_frame.setTextRiskWin();
	}

	public void setTextRiskLose() {
		this.title_frame.setTextRiskLose();
	}

	public void setAward(int award) {
		this.bank_frame.setAward(award);
		this.double_frame.setAward(2 * award);
	}

	public void setChoiceCards(CardInfo[] cards) {
		if (cards == null) return;
		for (int i = 0; i < cards.length; i++) {
			this.cards[i].setCard(this.cards_atlas, cards[i].getSuit(), cards[i].getRank(), cards[i].getColor());
		}
	}

	public void showChoiceCard(int index) {
		for (int i = 0; i < 4; this.cards[i++].setCardFlippedListener(null));
		this.cards[index].setCardFlippedListener(this.choice_flipped);
		this.cards[index].open();
		this.choice_index = index;
	}

	public void showChoiceCards() {
		for (int i = 0; i < 4; i++) {
			if (i == this.choice_index) {
				this.cards[i].setCardFlippedListener(null);
				continue;
			}
			this.cards[i].open();
		}
		this.cards[this.choice_index == 3 ? 2 : 3].setCardFlippedListener(this.all_flipped);
	}

	public void closeChoiceCards() {
		for (int i = 0; i < 4; this.cards[i++].close());
	}

	public void showGameCard(CardInfo card) {
		this.cards[4].setCard(this.cards_atlas, card.getSuit(), card.getRank(), card.getColor());
		this.cards[4].open();
	}

	public void closeGameCard() {
		this.cards[4].close();
	}

	public void setListener(RiskGreaterListener listener) {
		this.listener = listener;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		float x = super.getX();
		float y = super.getY();
		float width = super.getWidth();
		float height = super.getHeight();
		batch.draw(this.background, x, y, width, height);
		this.background_cards.draw(batch, x, y + .51f * height, .196f * width, .566f * height);
		this.background_chips.draw(batch, x + .836f * width, y + .58f * height, .164f * width, .504f * height);
		super.draw(batch, parentAlpha);
	}

	@Override
	public void resize(int width, int height) {
		this.title_frame.resize(width, height);
		this.bank_frame.resize(width, height);
		this.double_frame.resize(width, height);
		this.left_button.resize(width, height);
		this.right_button.resize(width, height);
	}

	public RiskMainButton getLeftButton() {
		return this.left_button;
	}

	public RiskMainButton getRightButton() {
		return this.right_button;
	}

	public static class RiskTitleFrame extends Group implements Resizable {
		private final PixelLabel top_label;
		private final PixelLabel top_win_label;
		private final PixelLabel top_lose_label;
		private final PixelLabel bottom_label;
		private final PixelLabel middle_label;
		private final TextureRegionDrawable background;
		private static final Color DEFAULT_COLOR;
		private static final Color WIN_COLOR;
		private static final Color LOSE_COLOR;
		static {
			DEFAULT_COLOR = new Color(.85f, .808f, .094f, 1f);
			WIN_COLOR = new Color(.286f, .859f, .133f, 1f);
			LOSE_COLOR = new Color(1f, .259f, 0f, 1f);
		}
		private RiskTitleFrame(TextureAtlas atlas) {
			LabelStyle style = new LabelStyle();
			style.font = FontsFactory.getAsync("Taurus.ttf", 28);
			style.fontColor = DEFAULT_COLOR;
			this.top_label = new PixelLabel("", style);
			this.top_label.setAlignment(Align.center, Align.center);
			super.addActor(this.top_label);
			style = new LabelStyle();
			style.font = FontsFactory.getAsync("Taurus.ttf", 28);
			style.fontColor = WIN_COLOR;
			this.top_win_label = new PixelLabel("", style);
			this.top_win_label.setAlignment(Align.center, Align.center);
			super.addActor(this.top_win_label);
			style = new LabelStyle();
			style.font = FontsFactory.getAsync("Taurus.ttf", 28);
			style.fontColor = LOSE_COLOR;
			this.top_lose_label = new PixelLabel("", style);
			this.top_lose_label.setAlignment(Align.center, Align.center);
			super.addActor(this.top_lose_label);
			style = new LabelStyle();
			style.font = FontsFactory.getAsync("Taurus.ttf", 20);
			style.fontColor = DEFAULT_COLOR;
			this.bottom_label = new PixelLabel("", style);
			this.bottom_label.setAlignment(Align.center, Align.center);
			super.addActor(this.bottom_label);
			style = new LabelStyle();
			style.font = FontsFactory.getAsync("Taurus.ttf", 24);
			style.fontColor = DEFAULT_COLOR;
			this.middle_label = new PixelLabel("", style);
			this.middle_label.setAlignment(Align.center, Align.center);
			super.addActor(this.middle_label);
			this.background = new TextureRegionDrawable(atlas.findRegion("title_frame"));
		}
		public static RiskTitleFrame newInstance(TextureAtlas atlas) {
			RiskTitleFrame instance = new RiskTitleFrame(atlas);
			return instance;
		}
		public void setTextGreaterRules() {
			this.top_label.setVisible(false);
			this.top_win_label.setVisible(false);
			this.top_lose_label.setVisible(false);
			this.bottom_label.setVisible(false);
			this.middle_label.setVisible(true);
			this.middle_label.setText(Connection.getString("risk_rules_greater"));
		}
		public void setTextColorRules() {
			this.top_label.setVisible(true);
			this.top_win_label.setVisible(false);
			this.top_lose_label.setVisible(false);
			this.bottom_label.setVisible(true);
			this.middle_label.setVisible(false);
			this.top_label.setText(Connection.getString("risk_rules_color_1"));
			this.bottom_label.setText(Connection.getString("risk_rules_color_2"));
		}
		public void setTextRiskWin() {
			this.top_label.setVisible(false);
			this.top_win_label.setVisible(true);
			this.top_lose_label.setVisible(false);
			this.bottom_label.setVisible(true);
			this.middle_label.setVisible(false);
			this.top_win_label.setText(Connection.getString("risk_result_win_1"));
			this.bottom_label.setText(Connection.getString("risk_result_win_2"));
		}
		public void setTextRiskLose() {
			this.top_label.setVisible(false);
			this.top_win_label.setVisible(false);
			this.top_lose_label.setVisible(true);
			this.bottom_label.setVisible(true);
			this.middle_label.setVisible(false);
			this.top_lose_label.setText(Connection.getString("risk_result_lose_1"));
			this.bottom_label.setText(Connection.getString("risk_result_lose_2"));
		}
		@Override
		public void draw(Batch batch, float parentAlpha) {
			this.background.draw(batch, super.getX(), super.getY(), super.getWidth(), super.getHeight());
			super.draw(batch, parentAlpha);
		}
		@Override
		public void setBounds(float x, float y, float width, float height) {
			super.setBounds(x, y, width, height);
			this.top_label.setBounds(0f, .44f * height, width, .5f * height);
			this.top_win_label.setBounds(0f, .44f * height, width, .5f * height);
			this.top_lose_label.setBounds(0f, .44f * height, width, .5f * height);
			this.bottom_label.setBounds(0f, .08f * height, width, .4f * height);
			this.middle_label.setBounds(0f, 0f, width, height);
		}
		@Override
		public void resize(int width, int height) {
			this.top_label.resize(width, height);
			this.top_win_label.resize(width, height);
			this.top_lose_label.resize(width, height);
			this.bottom_label.resize(width, height);
			this.middle_label.resize(width, height);
		}
	}

	public static class RiskAwardFrame extends Group implements Resizable {
		private final PixelLabel top_label;
		private final PixelLabel bottom_label;
		private final TextureRegionDrawable background;
		private static final Color TEXT_COLOR;
		static {
			TEXT_COLOR = new Color(.85f, .808f, .094f, 1f);
		}
		private RiskAwardFrame(TextureAtlas atlas) {
			LabelStyle style = new LabelStyle();
			style.font = FontsFactory.getAsync("Junegull.ttf", 22);
			style.fontColor = TEXT_COLOR;
			this.top_label = new PixelLabel("", style);
			this.top_label.setAlignment(Align.center, Align.center);
			super.addActor(this.top_label);
			style = new LabelStyle();
			style.font = FontsFactory.getAsync("Taurus.ttf", 24);
			style.fontColor = Color.WHITE;
			this.bottom_label = new PixelLabel("", style);
			this.bottom_label.setAlignment(Align.center, Align.center);
			super.addActor(this.bottom_label);
			this.background = new TextureRegionDrawable(atlas.findRegion("award_background"));
		}
		public static RiskAwardFrame newInstance(TextureAtlas atlas) {
			RiskAwardFrame instance = new RiskAwardFrame(atlas);
			return instance;
		}
		public void setTextBank() {
			this.top_label.setText(Connection.getString("risk_award_now"));
		}
		public void setTextDouble() {
			this.top_label.setText(Connection.getString("risk_award_double"));
		}
		public void setAward(int award) {
			this.bottom_label.setText(String.valueOf(award));
		}
		@Override
		public void draw(Batch batch, float parentAlpha) {
			this.background.draw(batch, super.getX(), super.getY(), super.getWidth(), super.getHeight());
			super.draw(batch, parentAlpha);
		}
		@Override
		public void setBounds(float x, float y, float width, float height) {
			super.setBounds(x, y, width, height);
			this.top_label.setBounds(0f, .5f * height, width, .5f * height);
			this.bottom_label.setBounds(0f, .0f, width, .5f * height);
		}
		@Override
		public void resize(int width, int height) {
			this.top_label.resize(width, height);
			this.bottom_label.resize(width, height);
		}
	}

	public static class RiskMainButton extends Button implements Resizable {
		private final PixelLabel label;
		private RiskMainButton(ButtonStyle style) {
			super(style);
			LabelStyle ls = new LabelStyle();
			ls.font = FontsFactory.getAsync("Junegull.ttf", 22);
			ls.fontColor = Color.WHITE;
			this.label = new PixelLabel("", ls);
			this.label.setAlignment(Align.center, Align.center);
			super.addActor(this.label);
		}
		public static RiskMainButton newInstance(TextureAtlas atlas, boolean left) {
			ButtonStyle style = new ButtonStyle();
			if (left) {
				AtlasRegion region = atlas.findRegion("main_button_active");
				region = new AtlasRegion(region.getTexture(), region.getRegionX(), region.getRegionY(), region.getRegionWidth(), region.getRegionHeight());
				region.flip(true, false);
				style.up = new TextureRegionDrawable(region);
				region = atlas.findRegion("main_button_pressed");
				region = new AtlasRegion(region.getTexture(), region.getRegionX(), region.getRegionY(), region.getRegionWidth(), region.getRegionHeight());
				region.flip(true, false);
				style.down = new TextureRegionDrawable(region);
				region = atlas.findRegion("main_button_inactive");
				region = new AtlasRegion(region.getTexture(), region.getRegionX(), region.getRegionY(), region.getRegionWidth(), region.getRegionHeight());
				region.flip(true, false);
				style.disabled = new TextureRegionDrawable(region);
			} else {
				style.up = new TextureRegionDrawable(atlas.findRegion("main_button_active"));
				style.down = new TextureRegionDrawable(atlas.findRegion("main_button_pressed"));
				style.disabled = new TextureRegionDrawable(atlas.findRegion("main_button_inactive"));
			}
			style.pressedOffsetY = -3f;
			RiskMainButton instance = new RiskMainButton(style);
			if (left) {
				instance.setTextRepeat();
			} else {
				instance.setTextTake();
			}
			return instance;
		}
		public void setTextRepeat() {
			this.label.setText(Connection.getString("risk_button_repeat"));
		}
		public void setTextTake() {
			this.label.setText(Connection.getString("risk_button_take_money"));
		}
		public void setTextOver() {
			this.label.setText(Connection.getString("risk_button_leave"));
		}
		@Override
		public void setBounds(float x, float y, float width, float height) {
			this.label.setBounds(0f, 0f, width, height);
			super.setBounds(x, y, width, height);
		}
		@Override
		public void resize(int width, int height) {
			this.label.resize(width, height);
		}
	}

}
