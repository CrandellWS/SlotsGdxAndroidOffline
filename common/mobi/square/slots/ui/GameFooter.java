package mobi.square.slots.ui;

import mobi.square.slots.api.Connection;
import mobi.square.slots.config.AppConfig;
import mobi.square.slots.stages.Basic;
import mobi.square.slots.tools.FontsFactory;
import mobi.square.slots.utils.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

public class GameFooter extends Group {

	public static final float FOOTER_HEIGHT = 95f;

	private final TextureRegionDrawable background;
	private final RiskButton risk_button;
	private final SpinButton spin_button;
	private final PayoutButton payout_button;
	private final BetCombo bet_combo;
	private final BetCombo lines_combo;
	private final PayoutLabel payout_label;
	private final TotalBetBox total_bet;
	private final MaxBetButton maxbet_button;
	private final BetWindow bet_window;
	private final BetWindow lines_window;

	private Runnable spin_clicked;
	private Runnable auto_clicked;
	private Runnable auto_canceled;
	private boolean touch_down;

	private GameFooter(Basic parent, TextureAtlas atlas, TextureAtlas autospin_atlas) {
		super();
		this.background = new TextureRegionDrawable(atlas.findRegion("panel_background"));
		this.risk_button = RiskButton.newInstance(atlas);
		this.spin_button = SpinButton.newInstance(atlas, autospin_atlas);
		this.payout_button = new PayoutButton(atlas);
		this.bet_combo = new BetCombo(atlas);
		this.lines_combo = new BetCombo(atlas);
		this.payout_label = new PayoutLabel(atlas);
		this.total_bet = new TotalBetBox(atlas);
		this.maxbet_button = new MaxBetButton(atlas);
		this.bet_window = BetWindow.newInstance(atlas);
		this.lines_window = BetWindow.newInstance(atlas);
		super.addActor(this.risk_button);
		super.addActor(this.spin_button);
		super.addActor(this.payout_button);
		super.addActor(this.bet_combo);
		super.addActor(this.lines_combo);
		super.addActor(this.payout_label);
		super.addActor(this.total_bet);
		super.addActor(this.maxbet_button);
		super.addActor(this.bet_window);
		super.addActor(this.lines_window);
		this.touch_down = false;
		this.spin_clicked = null;
		this.auto_clicked = null;
		this.auto_canceled = null;
		this.spin_button.addListener(new ActorGestureListener() {
			@Override
			public boolean longPress(Actor actor, float x, float y) {
				touch_down = false;
				if (spin_button.isDisabled())
					return super.longPress(actor, x, y);
				if (!spin_button.isAutoMode()) {
					spin_button.setAutoMode(true);
					if (auto_clicked != null) {
						auto_clicked.run();
					}
				}
				return super.longPress(actor, x, y);
			}
			@Override
			public void touchDown(InputEvent event, float x, float y, int pointer, int button) {
				touch_down = true;
				super.touchDown(event, x, y, pointer, button);
			}
		});
		this.spin_button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (spin_button.isDisabled()) return;
				if (touch_down) {
					if (spin_button.isAutoMode()) {
						spin_button.setAutoMode(false);
						if (auto_canceled != null) {
							auto_canceled.run();
						}
					} else {
						if (spin_clicked != null) {
							spin_clicked.run();
						}
					}
				}
			}
		});
	}

	public static GameFooter newInstance(Basic parent, TextureAtlas atlas, TextureAtlas autospin_atlas) {
		GameFooter instance = new GameFooter(parent, atlas, autospin_atlas);
		instance.setBounds(0f, 0f, AppConfig.VIEWPORT_WIDTH, FOOTER_HEIGHT);
		instance.risk_button.setBounds(40f, -50f, 175f, 175f);
		instance.spin_button.setBounds(AppConfig.VIEWPORT_WIDTH - 40f - 175f, -50f, 175f, 175f);
		instance.payout_button.setBounds(222f, 4f, 100f, 84f);
		instance.bet_combo.setBounds(332f, 4f, 124f, 44f);
		instance.lines_combo.setBounds(470f, 4f, 124f, 44f);
		instance.payout_label.setBounds(332f, 55f, 261f, 32f);
		instance.total_bet.setBounds(608f, 4f, 84f, 82f);
		instance.maxbet_button.setBounds(702f, 4, 96f, 82f);
		instance.lines_combo.setButtonTextLines();
		instance.bet_combo.setButtonTextBet();
		return instance;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		this.background.draw(batch, super.getX(), super.getY(), super.getWidth(), super.getHeight());
		super.draw(batch, parentAlpha);
	}

	public void resize(int width, int height) {
		float x_aspect = (float)width / AppConfig.VIEWPORT_WIDTH;
		float y_aspect = (float)height / AppConfig.VIEWPORT_HEIGHT;
		float button_size = 175f * y_aspect;
		float new_width = button_size / x_aspect;
		float offset = 175f - new_width;
		super.setBounds(0f, 0f, AppConfig.VIEWPORT_WIDTH, FOOTER_HEIGHT);
		this.risk_button.setBounds(40f + offset, -50f, new_width, 175f);
		this.spin_button.setBounds(AppConfig.VIEWPORT_WIDTH - 40f - 175f, -50f, new_width, 175f);
		this.risk_button.resize(width, height);
		this.spin_button.resize(width, height);
		this.payout_button.resize(width, height);
		this.maxbet_button.resize(width, height);
		this.bet_combo.resize(width, height);
		this.lines_combo.resize(width, height);
		this.payout_label.resize(width, height);
		this.total_bet.resize(width, height);
		this.bet_window.resize(width, height);
		this.lines_window.resize(width, height);
	}

	public RiskButton getRiskButton() {
		return this.risk_button;
	}

	public void addAwardMoney(int money) {
		this.payout_label.addMoney(money);
	}

	public void setAwardMoney(int money, boolean flash) {
		this.payout_label.setMoney(money, flash);
	}

	public void changeAwardMoney(int money) {
		this.payout_label.changeMoney(money);
	}

	public MaxBetButton getMaxBetButton() {
		return this.maxbet_button;
	}

	public PayoutButton getPayoutButton() {
		return this.payout_button;
	}

	public BetCombo getBetCombo() {
		return this.bet_combo;
	}

	public BetCombo getLinesCombo() {
		return this.lines_combo;
	}

	public BetWindow getBetWindow() {
		return this.bet_window;
	}

	public BetWindow getLinesWindow() {
		return this.lines_window;
	}

	public boolean isAutoMode() {
		return this.spin_button.isAutoMode();
	}

	public int getTotalBet() {
		return this.bet_combo.getCurrentValue() * this.lines_combo.getCurrentValue();
	}

	public void updateTotalBet() {
		int value = this.bet_combo.getCurrentValue() * this.lines_combo.getCurrentValue();
		this.total_bet.value.setText(utils.splitNumber(value, 3));
	}

	public void setFreeSpins(int free_spins) {
		this.spin_button.setFreeSpins(free_spins);
	}

	public void setSpinClickedTask(Runnable task) {
		this.spin_clicked = task;
	}

	public void setAutoClickedTask(Runnable task) {
		this.auto_clicked = task;
	}

	public void setAutoClickedCanceled(Runnable task) {
		this.auto_canceled = task;
	}

	public void disableRiskButton(boolean disabled) {
		this.risk_button.setDisabled(disabled);
	}

	public void disableSpinButton(boolean disabled) {
		this.spin_button.setDisabled(disabled);
	}

	public void disableBetCombo(boolean disabled) {
		this.bet_combo.button.setDisabled(disabled);
	}

	public void disableLinesCombo(boolean disabled) {
		this.lines_combo.button.setDisabled(disabled);
	}

	public void disableMaxbetButton(boolean disabled) {
		this.maxbet_button.setDisabled(disabled);
	}

	public void setAutoMode(boolean value) {
		this.spin_button.setAutoMode(value);
	}

	public void setSpinButtonDisabled(boolean disabled) {
		this.spin_button.setDisabled(disabled);
	}

	public void setSpinSwitchLabels(boolean state) {
		this.spin_button.setSwitchLabels(state);
	}

	public class PayoutButton extends Button implements Resizable {
		private final PixelLabel label;
		private PayoutButton(TextureAtlas atlas) {
			super(new ButtonStyle());
			ButtonStyle style = super.getStyle();
			style.up = new TextureRegionDrawable(atlas.findRegion("button_payout_active"));
			style.down = new TextureRegionDrawable(atlas.findRegion("button_payout_pressed"));
			style.disabled = new TextureRegionDrawable(atlas.findRegion("button_payout_inactive"));
			style.pressedOffsetY = -2f;
			LabelStyle ls = new LabelStyle();
			ls.font = FontsFactory.getAsync("Taurus.ttf", 20);
			this.label = new PixelLabel(Connection.getString("footer_button_paytable"), ls);
			this.label.setAlignment(Align.center, Align.center);
			super.addActor(this.label);
		}
		@Override
		public void resize(int width, int height) {
			this.label.setBounds(0f, 0f, super.getWidth(), super.getHeight());
			this.label.resize(width, height);
		}
	}

	public class BetCombo extends Group implements Resizable {
		private final TextureRegionDrawable background;
		private final Button button;
		private final PixelLabel button_label;
		private final PixelLabel value;
		private int[] valid_values;
		private int active_index;
		public BetCombo(TextureAtlas atlas) {
			this.active_index = 0;
			this.valid_values = new int[0];
			this.background = new TextureRegionDrawable(atlas.findRegion("combo_background"));
			ButtonStyle style = new ButtonStyle();
			style.up = new TextureRegionDrawable(atlas.findRegion("button_combo_active"));
			style.down = new TextureRegionDrawable(atlas.findRegion("button_combo_pressed"));
			style.disabled = new TextureRegionDrawable(atlas.findRegion("button_combo_inactive"));
			style.pressedOffsetY = -1f;
			this.button = new Button(style);
			this.button.setBounds(0f, 0f, super.getWidth() * .6f, super.getHeight());
			LabelStyle ls = new LabelStyle();
			ls.fontColor = Color.WHITE;
			ls.font = FontsFactory.getAsync("Taurus.ttf", 16);
			this.button_label = new PixelLabel("", ls);
			this.button_label.setAlignment(Align.center, Align.center);
			this.button.addActor(this.button_label);
			ls = new LabelStyle();
			ls.fontColor = Color.WHITE;
			ls.font = FontsFactory.getAsync("Junegull.ttf", 22);
			this.value = new PixelLabel("55", ls);
			this.value.setAlignment(Align.center, Align.center);
			super.addActor(this.button);
			super.addActor(this.value);
		}
		@Override
		public void draw(Batch batch, float parentAlpha) {
			float height = super.getHeight();
			this.background.draw(batch, super.getX(), super.getY() + height * .05f, super.getWidth(), height * .9f);
			super.draw(batch, parentAlpha);
		}
		@Override
		public void setBounds(float x, float y, float width, float height) {
			super.setBounds(x, y, width, height);
			this.button.setBounds(0f, 0f, width * .6f, height);
		}
		public boolean isDisabled() {
			return this.button.isDisabled();
		}
		public void setDisabled(boolean disabled) {
			this.button.setDisabled(disabled);
		}
		public boolean addListener(EventListener listener) {
			return this.button.addListener(listener);
		}
		public void setValidValues(int[] values, int index) {
			this.valid_values = values.clone();
			this.setActiveIndex(index);
		}
		public int getCurrentValue() {
			return this.valid_values[this.active_index];
		}
		public void setActiveIndex(int index) {
			this.active_index = index >= 0 && index < this.valid_values.length ? index : 0;
			this.value.setText(String.valueOf(this.getCurrentValue()));
		}
		void setButtonTextBet() {
			this.setButtonText(Connection.getString("footer_combo_bet"));
		}
		void setButtonTextLines() {
			this.setButtonText(Connection.getString("footer_combo_lines"));
		}
		void setButtonText(String text) {
			this.button_label.setText(text);
			this.button_label.setBounds(0f, 0f, this.button.getWidth(), this.button.getHeight());
		}
		@Override
		public void resize(int width, int height) {
			float l_width = super.getWidth();
			float l_height = super.getHeight();
			this.value.setBounds(l_width * .6f, 0, l_width * .4f, l_height);
			this.value.resize(width, height);
			this.button_label.resize(width, height);
		}
	}

	private class TotalBetBox extends Group implements Resizable {
		private final PixelLabel value;
		private final PixelLabel label;
		private final TextureRegionDrawable background;
		public TotalBetBox(TextureAtlas atlas) {
			this.background = new TextureRegionDrawable(atlas.findRegion("totalbet_background"));
			LabelStyle ls = new LabelStyle();
			ls.font = FontsFactory.getAsync("Junegull.ttf", 24);
			ls.fontColor = Color.WHITE;
			this.value = new PixelLabel("0", ls);
			this.value.setAlignment(Align.center, Align.center);
			super.addActor(this.value);
			ls = new LabelStyle();
			ls.font = FontsFactory.getAsync("Taurus.ttf", 18);
			ls.fontColor = new Color(.443f, .733f, .992f, 1f);
			this.label = new PixelLabel(Connection.getString("footer_total_bet"), ls);
			this.label.setAlignment(Align.center, Align.center);
			super.addActor(this.label);
		}
		@Override
		public void draw(Batch batch, float parentAlpha) {
			this.background.draw(batch, super.getX(), super.getY(), super.getWidth(), super.getHeight());
			super.draw(batch, parentAlpha);
		}
		@Override
		public void resize(int width, int height) {
			float l_width = super.getWidth();
			float l_height = super.getHeight();
			this.label.setBounds(0f, l_height * .15f, l_width, l_height * .35f);
			this.value.setBounds(0f, l_height * .55f, l_width, l_height * .35f);
			this.label.resize(width, height);
			this.value.resize(width, height);
		}
	}

	public static class PayoutLabel extends Group implements Resizable {
		private final TextureRegionDrawable background;
		private final TextureRegionDrawable background_flashed;
		private final PixelLabel label;
		private final PixelLabel value;
		private final Color NORMAL;
		private final Color FLASHED;
		private final int MIN_CHANGE_VALUE;
		private final float MAX_CHANGE_TIME;
		private final float FLASH_TIME;
		private boolean flashed;
		private float flash_time;
		private int flash_count;
		private int target_money;
		private int change_speed;
		private int money;
		public PayoutLabel(TextureAtlas atlas) {
			this.FLASH_TIME = .2f;
			this.MAX_CHANGE_TIME = 1.0f;
			this.MIN_CHANGE_VALUE = 100;
			this.NORMAL = new Color(.725f, .922f, 1f, 1f);
			this.FLASHED = new Color(.98f, .988f, .212f, 1f);
			this.flashed = false;
			this.flash_time = 0f;
			this.flash_count = 0;
			this.background = new TextureRegionDrawable(atlas.findRegion("payout_background_normal"));
			this.background_flashed = new TextureRegionDrawable(atlas.findRegion("payout_background_flashed"));
			LabelStyle ls = new LabelStyle();
			ls.fontColor = Color.BLACK;
			ls.font = FontsFactory.getAsync("Junegull.ttf", 15);
			this.label = new PixelLabel(Connection.getString("common_award"), ls);
			this.label.setAlignment(Align.center, Align.center);
			super.addActor(this.label);
			ls = new LabelStyle();
			ls.fontColor = this.NORMAL;
			ls.font = FontsFactory.getAsync("Junegull.ttf", 20);
			this.value = new PixelLabel(utils.splitNumber(1125312, 3), ls);
			this.value.setAlignment(Align.center, Align.center);
			super.addActor(this.value);
		}
		@Override
		public void act(float delta) {
			super.act(delta);
			this.flash_time -= delta;
			if (this.flash_time < 0f)
				this.flash_time = 0f;
			if (this.flashed) {
				if (this.flash_time <= 0f) {
					this.flash_time = FLASH_TIME;
					this.setFlashed(false);
				}
			}
			if (this.target_money != this.money) {
				if (this.flash_time <= 0f && (this.target_money > this.money || this.flash_time > 0)) {
					this.flash_time = FLASH_TIME;
					this.setFlashed(true);
					this.flash_count--;
				}
				if (this.change_speed == 0)
					this.change_speed = this.evalChangeSpeed(this.money, this.target_money);
				int value = (int)((float)this.change_speed * delta);
				this.money += value != 0 ? value : this.change_speed < 0 ? -1 : 1;
				if (this.change_speed < 0) {
					if (this.money <= this.target_money) {
						this.money = this.target_money;
						this.change_speed = 0;
					}
				} else {
					if (this.money >= this.target_money) {
						this.money = this.target_money;
						this.change_speed = 0;
					}
				}
				this.value.setText(utils.splitNumber(this.money, 3));
			} else if (this.flash_count > 0) {
				if (this.flash_time <= 0f) {
					this.flash_time = FLASH_TIME;
					this.setFlashed(true);
					this.flash_count--;
				}
			}
		}
		@Override
		public void draw(Batch batch, float parentAlpha) {
			if (this.flashed) {
				this.background_flashed.draw(batch, super.getX(), super.getY(), super.getWidth(), super.getHeight());
			} else {
				this.background.draw(batch, super.getX(), super.getY(), super.getWidth(), super.getHeight());
			}
			super.draw(batch, parentAlpha);
		}
		public void setText(String text) {
			this.label.setText(text);
		}
		private void setFlashed(boolean flashed) {
			this.value.setColor(flashed ? FLASHED : NORMAL);
			this.flashed = flashed;
		}
		public void setMoney(int money, boolean flash) {
			this.money = money;
			this.target_money = money;
			this.change_speed = 0;
			if (flash && money > 0) this.flash_count = 3;
			this.value.setText(utils.splitNumber(money, 3));
		}
		public void changeMoney(int money) {
			this.target_money = money;
			this.change_speed = this.evalChangeSpeed(this.money, money);
		}
		public void addMoney(int money) {
			this.setMoney(this.money + money, true);
		}
		private int evalChangeSpeed(int current, int target) {
			int value = (int)((float)(target - current) / MAX_CHANGE_TIME);
			if (value < 0) {
				return value > -MIN_CHANGE_VALUE ? -MIN_CHANGE_VALUE : value;
			} else if (value > 0) {
				return value < MIN_CHANGE_VALUE ? MIN_CHANGE_VALUE : value;
			} else return 0;
		}
		@Override
		public void resize(int width, int height) {
			float l_width = super.getWidth();
			float l_height = super.getHeight();
			this.label.setBounds(0f, 0f, l_width * .4f, l_height);
			this.value.setBounds(l_width * .46f, 0f, l_width * .5f, l_height);
			this.label.resize(width, height);
			this.value.resize(width, height);
		}
	}

	public class MaxBetButton extends Button implements Resizable {
		private final PixelLabel max_label;
		private final PixelLabel bet_label;
		private MaxBetButton(TextureAtlas atlas) {
			super(new ButtonStyle());
			super.getStyle().up = new TextureRegionDrawable(atlas.findRegion("button_maxbet_active"));
			super.getStyle().down = new TextureRegionDrawable(atlas.findRegion("button_maxbet_pressed"));
			super.getStyle().disabled = new TextureRegionDrawable(atlas.findRegion("button_maxbet_inactive"));
			super.getStyle().pressedOffsetY = -2f;
			LabelStyle ls = new LabelStyle();
			ls.font = FontsFactory.getAsync("Junegull.ttf", 30);
			ls.fontColor = new Color(1f, 0.867f, 0f, 1f);
			this.max_label = new PixelLabel("MAX", ls);
			this.max_label.setAlignment(Align.center, Align.center);
			super.addActor(this.max_label);
			ls = new LabelStyle();
			ls.font = FontsFactory.getAsync("Taurus.ttf", 20);
			ls.fontColor = new Color(1f, 0.867f, 0f, 1f);
			this.bet_label = new PixelLabel(Connection.getString("footer_bet_label"), ls);
			this.bet_label.setAlignment(Align.center, Align.center);
			super.addActor(this.bet_label);
		}
		@Override
		public void resize(int width, int height) {
			float l_width = super.getWidth();
			float l_height = super.getHeight();
			this.max_label.setBounds(0f, l_height * .5f, l_width, l_height * .35f);
			this.bet_label.setBounds(0f, l_height * .15f, l_width, l_height * .35f);
			this.max_label.resize(width, height);
			this.bet_label.resize(width, height);
		}
	}

	public static class BetWindow extends Window implements Resizable {
		private final TextureRegionDrawable button_active;
		private final TextureRegionDrawable button_inactive;
		private final ScrollPane scroll;
		private final Table table;
		private BetClickHandler handler;
		private BetWindow(TextureAtlas atlas, WindowStyle style) {
			super("", style);
			this.button_active = new TextureRegionDrawable(atlas.findRegion("combo_window_button_active"));
			this.button_inactive = new TextureRegionDrawable(atlas.findRegion("combo_window_button_inactive"));
			this.table = new Table();
			this.scroll = new ScrollPane(this.table);
			this.scroll.setScrollingDisabled(false, true);
			this.scroll.setFadeScrollBars(false);
			super.addActor(this.scroll);
			this.handler = null;
			this.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					if (event.getTarget() == BetWindow.this) hide();
				}
			});
		}
		public static BetWindow newInstance(TextureAtlas atlas) {
			WindowStyle style = new WindowStyle();
			style.background = new TextureRegionDrawable(atlas.findRegion("combo_window_background"));
			style.titleFont = FontsFactory.getAsync("Taurus.ttf", 20);
			style.titleFontColor = Color.WHITE;
			BetWindow instance = new BetWindow(atlas, style);
			instance.setVisible(false);
			instance.setMovable(false);
			instance.setModal(true);
			instance.setBounds(200f, 100f, 624f, 110f);
			instance.scroll.setBounds(31f, 0f, 560f, 110f);
			return instance;
		}
		public void show(int[] values, int index, BetClickHandler click_handler) {
			this.table.clear();
			this.handler = click_handler;
			for (int i = 0; i < values.length; i++) {
				BetButton button = BetButton.newInstance(this.button_active, this.button_inactive, String.valueOf(values[i]));
				button.setDisabled(index == i);
				button.addListener(new BetClickListener(i) {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						hide();
						if (handler != null) {
							handler.clicked(super.index);
						}
					}
				});
				Cell<?> cell = this.table.add(button);
				if (i < values.length - 1) {
					cell.padRight(2f);
				}
			}
			this.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			super.setVisible(true);
		}
		public void hide() {
			super.setVisible(false);
			this.table.clear();
		}
		@Override
		@SuppressWarnings("rawtypes")
		public void resize(int width, int height) {
			Array<Cell> cells = this.table.getCells();
			float x_aspect = (float)width / (float)AppConfig.VIEWPORT_WIDTH;
			float y_aspect = (float)height / (float)AppConfig.VIEWPORT_HEIGHT;
			float cell_height = 108f;
			float cell_width = cell_height * y_aspect / x_aspect;
			for (Cell cell : cells) {
				cell.size(cell_width, cell_height);
				if (cell.getActor() instanceof BetButton) {
					BetButton button = (BetButton)cell.getActor();
					button.resize(width, height, cell_width, cell_height);
				}
			}
			this.scroll.invalidate();
			this.table.invalidate();
			super.invalidate();
		}
		public interface BetClickHandler {
			public void clicked(int index);
		}
		private class BetClickListener extends ClickListener {
			private final int index;
			public BetClickListener(int index) {
				super();
				this.index = index;
			}
		}
		private static class BetButton extends Button {
			private final PixelLabel label;
			public BetButton(ButtonStyle style, String text) {
				super(style);
				LabelStyle ls = new LabelStyle();
				ls.font = FontsFactory.getAsync("Junegull.ttf", 36);
				ls.fontColor = Color.WHITE;
				this.label = new PixelLabel(text, ls);
				this.label.setAlignment(Align.center, Align.center);
				super.addActor(this.label);
			}
			public static BetButton newInstance(TextureRegionDrawable active, TextureRegionDrawable inactive, String text) {
				ButtonStyle style = new ButtonStyle();
				style.up = inactive;
				style.down = active;
				style.disabled = active;
				BetButton instance = new BetButton(style, text);
				return instance;
			}
			public void resize(int width, int height, float cell_width, float cell_height) {
				this.label.setBounds(0f, 0f, cell_width, .55f * cell_height);
				this.label.resize(width, height);
			}
		}
	}

}
