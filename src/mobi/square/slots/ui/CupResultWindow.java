package mobi.square.slots.ui;

import mobi.square.slots.api.Connection;
import mobi.square.slots.tools.FontsFactory;
import mobi.square.slots.utils.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class CupResultWindow extends Window implements Resizable {

	private final CupButton close_button;
	private final CupIconButton action_button;
	private final ScoreLabel score_label;
	private final ScoreFrame score_frame;
	private final PixelLabel title_label;
	private final PixelLabel result_label;
	private CupWindowListener listener;

	private CupResultWindow(WindowStyle style, TextureAtlas atlas) {
		super("", style);
		this.listener = null;
		{ // Labels
			LabelStyle ls = new LabelStyle();
			ls.font = FontsFactory.getAsync("Junegull.ttf", 48);
			ls.fontColor = new Color(.13f, .11f, .09f, 1f);
			this.title_label = new PixelLabel(Connection.getString("cup_title"), ls);
			this.title_label.setAlignment(Align.center);
			super.addActor(this.title_label);
			ls = new LabelStyle();
			ls.font = FontsFactory.getAsync("Junegull.ttf", 24);
			ls.fontColor = Color.WHITE;
			this.result_label = new PixelLabel("", ls);
			this.result_label.setAlignment(Align.center);
			super.addActor(this.result_label);
		}
		this.close_button = CupButton.newInstance(atlas, "orange");
		this.close_button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (close_button.isDisabled()) return;
				if (CupResultWindow.this.listener != null) {
					CupResultWindow.this.listener.closeClicked();
				}
			}
		});
		super.addActor(this.close_button);
		this.action_button = CupIconButton.newInstance(atlas, "blue");
		this.action_button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (action_button.isDisabled()) return;
				if (CupResultWindow.this.listener != null) {
					CupResultWindow.this.listener.actionClicked();
				}
			}
		});
		super.addActor(this.action_button);
		this.score_label = ScoreLabel.newInstance(atlas);
		super.addActor(this.score_label);
		this.score_frame = ScoreFrame.newInstance(atlas);
		super.addActor(this.score_frame);
	}

	public static CupResultWindow newInstance(TextureAtlas atlas) {
		WindowStyle style = new WindowStyle();
		style.titleFont = FontsFactory.getAsync("Junegull.ttf", 18);
		style.titleFontColor = Color.WHITE;
		style.background = new TextureRegionDrawable(atlas.findRegion("window_background"));
		CupResultWindow instance = new CupResultWindow(style, atlas);
		instance.setBounds(150f, 70f, 724f, 420f);
		instance.setMovable(false);
		instance.setModal(true);
		instance.close_button.setBounds(52f, 55f, 300f, 55f);
		instance.action_button.setBounds(372f, 55f, 300f, 55f);
		instance.score_label.setBounds(90f, 190f, 540f, 55f);
		instance.score_frame.setBounds(190f, 122f, 340f, 56f);
		instance.title_label.setBounds(0f, 320f, 724f, 90f);
		instance.result_label.setBounds(0f, 260f, 724f, 30f);
		instance.close_button.setTextClose();
		instance.hide();
		return instance;
	}

	@Override
	public void resize(int width, int height) {
		this.close_button.resize(width, height);
		this.action_button.resize(width, height);
		this.score_label.resize(width, height);
		this.score_frame.resize(width, height);
		this.title_label.resize(width, height);
		this.result_label.resize(width, height);
	}

	public void show(int score, int place, int money, int price) {
		if (price > money) {
			this.action_button.setTextBuy();
		} else this.action_button.setTextTryMore(price);
		if (place > 0) {
			this.result_label.setText(Connection.getDefaultI18N().format("cup_result_in_top", Integer.valueOf(place)));
		} else this.result_label.setText(Connection.getString("cup_result_no_top"));
		this.action_button.setDisabled(false);
		this.score_frame.setValue(score);
		super.setVisible(true);
	}

	public void hide() {
		super.setVisible(false);
	}

	public void setListener(CupWindowListener listener) {
		this.listener = listener;
	}

	public interface CupWindowListener {
		public void closeClicked();
		public void actionClicked();
	}

	private static class CupButton extends Button implements Resizable {
		private final PixelLabel label;
		private CupButton(ButtonStyle style) {
			super(style);
			LabelStyle ls = new LabelStyle();
			ls.font = FontsFactory.getAsync("Taurus.ttf", 28);
			ls.fontColor = Color.WHITE;
			this.label = new PixelLabel("", ls);
			this.label.setAlignment(Align.center);
			super.addActor(this.label);
		}
		public static CupButton newInstance(TextureAtlas atlas, String color) {
			ButtonStyle style = new ButtonStyle();
			style.up = new TextureRegionDrawable(atlas.findRegion("button_".concat(color).concat("_active")));
			style.down = new TextureRegionDrawable(atlas.findRegion("button_".concat(color).concat("_pressed")));
			style.disabled = new TextureRegionDrawable(atlas.findRegion("button_".concat(color).concat("_inactive")));
			CupButton instance = new CupButton(style);
			return instance;
		}
		@Override
		public void resize(int width, int height) {
			this.label.resize(width, height);
		}
		@Override
		public void setBounds(float x, float y, float width, float height) {
			this.label.setBounds(0f, 0f, width, height);
			super.setBounds(x, y, width, height);
		}
		public void setTextClose() {
			this.label.setText(Connection.getString("common_close"));
		}
		/*public void setTextTryMore() {
			this.label.setText(Connection.getString("cup_try_more"));
		}
		public void setTextBuy() {
			this.label.setText(Connection.getString("cup_buy_chips"));
		}*/
	}

	private static class CupIconButton extends Button implements Resizable {
		private final PixelLabel label;
		private final DrawableActor icon;
		private final PixelLabel icon_label;
		private final PixelLabel price_label;
		private CupIconButton(ButtonStyle style, TextureAtlas atlas) {
			super(style);
			LabelStyle ls = new LabelStyle();
			ls.font = FontsFactory.getAsync("Taurus.ttf", 28);
			ls.fontColor = Color.WHITE;
			this.label = new PixelLabel("", ls);
			this.label.setAlignment(Align.center);
			super.addActor(this.label);
			ls = new LabelStyle();
			ls.font = FontsFactory.getAsync("Taurus.ttf", 24);
			ls.fontColor = Color.WHITE;
			this.icon_label = new PixelLabel("", ls);
			this.icon_label.setAlignment(Align.right);
			super.addActor(this.icon_label);
			ls = new LabelStyle();
			ls.font = FontsFactory.getAsync("Taurus.ttf", 24);
			ls.fontColor = Color.WHITE;
			this.price_label = new PixelLabel("", ls);
			this.price_label.setAlignment(Align.left);
			super.addActor(this.price_label);
			this.icon = DrawableActor.newInstance(atlas.findRegion("chips_icon"));
			super.addActor(this.icon);
		}
		public static CupIconButton newInstance(TextureAtlas atlas, String color) {
			ButtonStyle style = new ButtonStyle();
			style.up = new TextureRegionDrawable(atlas.findRegion("button_".concat(color).concat("_active")));
			style.down = new TextureRegionDrawable(atlas.findRegion("button_".concat(color).concat("_pressed")));
			style.disabled = new TextureRegionDrawable(atlas.findRegion("button_".concat(color).concat("_inactive")));
			CupIconButton instance = new CupIconButton(style, atlas);
			return instance;
		}
		@Override
		public void resize(int width, int height) {
			this.label.resize(width, height);
			this.icon_label.resize(width, height);
			this.price_label.resize(width, height);
		}
		@Override
		public void setBounds(float x, float y, float width, float height) {
			this.label.setBounds(0f, 0f, width, height);
			this.icon.setBounds(.54f * width, .15f * height, .12f * width, .7f * height);
			this.icon_label.setBounds(0f, 0f, .54f * width, height);
			this.price_label.setBounds(.66f * width, 0f, .34f * width, height);
			super.setBounds(x, y, width, height);
		}
		public void setTextTryMore(int price) {
			this.icon_label.setText(Connection.getString("cup_try_more"));
			this.price_label.setText(String.valueOf(price));
			this.icon.setVisible(true);
			this.icon_label.setVisible(true);
			this.price_label.setVisible(true);
			this.label.setVisible(false);
		}
		public void setTextBuy() {
			this.label.setText(Connection.getString("cup_buy_chips"));
			this.icon.setVisible(false);
			this.icon_label.setVisible(false);
			this.price_label.setVisible(false);
			this.label.setVisible(true);
		}
	}

	private static class ScoreLabel extends BackgroundLabel {
		private ScoreLabel(LabelStyle style, TextureRegion region) {
			super(Connection.getString("cup_final_score"), style, region);
		}
		public static ScoreLabel newInstance(TextureAtlas atlas) {
			LabelStyle style = new LabelStyle();
			style.font = FontsFactory.getAsync("Junegull.ttf", 30);
			style.fontColor = new Color(1f, .84f, 0f, 1f);
			TextureRegion background = atlas.findRegion("timer_title_background");
			ScoreLabel instance = new ScoreLabel(style, background);
			return instance;
		}
	}

	public static class ScoreFrame extends BackgroundLabel {
		private ScoreFrame(LabelStyle style, TextureRegion region) {
			super("", style, region);
		}
		public static ScoreFrame newInstance(TextureAtlas atlas) {
			LabelStyle style = new LabelStyle();
			style.font = FontsFactory.getAsync("whitrabt.ttf", 40);
			style.fontColor = new Color(1f, .84f, 0f, 1f);
			TextureRegion background = atlas.findRegion("timer_background");
			ScoreFrame instance = new ScoreFrame(style, background);
			return instance;
		}
		public void setValue(int value) {
			super.setText(utils.splitNumber(value, 3));
		}
	}

	public static class BlackFrame extends BackgroundLabel {
		private BlackFrame(LabelStyle style, TextureRegion background) {
			super("", style, background);
		}
		public static BlackFrame newInstance(TextureAtlas atlas) {
			LabelStyle style = new LabelStyle();
			style.font = FontsFactory.getAsync("Junegull.ttf", 24);
			style.fontColor = new Color(.39f, .65f, .98f, 1f);
			TextureRegion background = atlas.findRegion("price_background");
			BlackFrame instance = new BlackFrame(style, background);
			return instance;
		}
		public void setValue(int value) {
			super.setText(String.valueOf(value));
		}
	}

}
