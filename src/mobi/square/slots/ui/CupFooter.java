package mobi.square.slots.ui;

import mobi.square.slots.api.Connection;
import mobi.square.slots.config.AppConfig;
import mobi.square.slots.containers.UserInfo;
import mobi.square.slots.tools.FontsFactory;
import mobi.square.slots.ui.GameFooter.PayoutLabel;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class CupFooter extends Group {

	public static final float FOOTER_HEIGHT = 95f;

	private final TextureRegionDrawable background;
	private final SpinButton spin_button;
	private final PayoutLabel payout_label;
	private final SpinsFrame spins_frame;
	private final LeaderBoard leader_board;
	private final PrizeFrame prize_frame;

	private Runnable spin_clicked;
	private Runnable auto_clicked;
	private Runnable auto_canceled;
	private boolean touch_down;

	private CupFooter(TextureAtlas atlas, TextureAtlas autospin_atlas, TextureAtlas cup_atlas) {
		super();
		this.background = new TextureRegionDrawable(atlas.findRegion("panel_background"));
		this.spin_button = SpinButton.newInstance(atlas, autospin_atlas);
		this.payout_label = new PayoutLabel(atlas);
		this.spins_frame = SpinsFrame.newInstance(cup_atlas);
		this.leader_board = LeaderBoard.newInstance(cup_atlas);
		this.prize_frame = PrizeFrame.newInstance(cup_atlas);
		super.addActor(this.spin_button);
		super.addActor(this.payout_label);
		super.addActor(this.spins_frame);
		super.addActor(this.leader_board);
		super.addActor(this.prize_frame);
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

	public static CupFooter newInstance(TextureAtlas atlas, TextureAtlas autospin_atlas, TextureAtlas cup_atlas) {
		CupFooter instance = new CupFooter(atlas, autospin_atlas, cup_atlas);
		instance.setBounds(0f, 0f, AppConfig.VIEWPORT_WIDTH, FOOTER_HEIGHT);
		instance.spin_button.setBounds(AppConfig.VIEWPORT_WIDTH - 40f - 175f, -50f, 175f, 175f);
		instance.payout_label.setBounds(520f, 52f, 261f, 32f);
		instance.spins_frame.setBounds(520f, 12f, 261f, 32f);
		instance.leader_board.setBounds(265f, 6f, 243f, 80f);
		instance.prize_frame.setBounds(12f, 1f, 243f, 85f);
		instance.payout_label.setText(Connection.getString("cup_current_score"));
		return instance;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		this.background.draw(batch, super.getX(), super.getY(), super.getWidth(), super.getHeight());
		super.draw(batch, parentAlpha);
	}

	public void resize(int width, int height) {
		float ppu_x = (float)width / (float)AppConfig.VIEWPORT_WIDTH;
		float ppu_y = (float)height / (float)AppConfig.VIEWPORT_HEIGHT;
		float button_size = 175f * ppu_y;
		float new_width = button_size / ppu_x;
		super.setBounds(0f, 0f, AppConfig.VIEWPORT_WIDTH, FOOTER_HEIGHT);
		this.spin_button.setBounds(AppConfig.VIEWPORT_WIDTH - 40f - 175f, -50f, new_width, 175f);
		this.spin_button.resize(width, height);
		this.payout_label.resize(width, height);
		this.spins_frame.resize(width, height);
		this.leader_board.resize(width, height);
		this.prize_frame.resize(width, height);
	}

	public void setLeaders(UserInfo[] users) {
		this.leader_board.fillList(users);
	}

	public void setPrizePool(int money) {
		this.prize_frame.setValue(money);
	}

	public void setSpinsCount(int spins) {
		this.spins_frame.setValue(spins);
	}

	public void setScore(int score, boolean flash) {
		this.payout_label.setMoney(score, flash);
	}

	public void addScore(int score) {
		this.payout_label.addMoney(score);
	}

	public void changeScore(int score) {
		this.payout_label.changeMoney(score);
	}

	public boolean isAutoMode() {
		return this.spin_button.isAutoMode();
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

	public void disableSpinButton(boolean disabled) {
		this.spin_button.setDisabled(disabled);
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

	private static class SpinsFrame extends Group implements Resizable {
		private final DrawableActor background;
		private final PixelLabel title;
		private final PixelLabel value;
		private SpinsFrame(TextureAtlas atlas) {
			super();
			this.background = DrawableActor.newInstance(atlas.findRegion("spins_frame"));
			super.addActor(this.background);
			LabelStyle ls = new LabelStyle();
			ls.font = FontsFactory.getAsync("Junegull.ttf", 18);
			ls.fontColor = new Color(1f, .84f, 0f, 1f);
			this.title = new PixelLabel(Connection.getString("cup_current_spins"), ls);
			this.title.setAlignment(Align.center);
			super.addActor(this.title);
			ls = new LabelStyle();
			ls.font = FontsFactory.getAsync("Junegull.ttf", 22);
			ls.fontColor = new Color(.54f, .98f, .44f, 1f);
			this.value = new PixelLabel("50", ls);
			this.value.setAlignment(Align.center);
			super.addActor(this.value);
		}
		public static SpinsFrame newInstance(TextureAtlas atlas) {
			SpinsFrame instance = new SpinsFrame(atlas);
			return instance;
		}
		@Override
		public void resize(int width, int height) {
			this.title.resize(width, height);
			this.value.resize(width, height);
		}
		@Override
		public void setBounds(float x, float y, float width, float height) {
			super.setBounds(x, y, width, height);
			this.background.setBounds(0f, 0f, width, height);
			this.title.setBounds(0f, 0f, .74f * width, height);
			this.value.setBounds(.74f * width, 0f, .26f * width, height);
		}
		public void setValue(int value) {
			this.value.setText(String.valueOf(value));
		}
	}

	private static class LeaderBoard extends Group implements Resizable {
		private final DrawableActor background;
		private final PixelLabel[] place;
		private final PixelLabel[] score;
		private LeaderBoard(TextureAtlas atlas) {
			super();
			this.background = DrawableActor.newInstance(atlas.findRegion("leaders_frame"));
			super.addActor(this.background);
			this.place = new PixelLabel[3];
			this.score = new PixelLabel[3];
			for (int i = 0; i < 3; i++) {
				LabelStyle ls = new LabelStyle();
				ls.font = FontsFactory.getAsync("Junegull.ttf", 18);
				ls.fontColor = i == 0 ? new Color(1f, .47f, 0f, 1f) : new Color(.62f, .62f, .62f, 1f);
				this.place[i] = new PixelLabel(String.valueOf(i + 1).concat(" место"), ls);
				super.addActor(this.place[i]);
				ls = new LabelStyle();
				ls.font = FontsFactory.getAsync("whitrabt.ttf", 21);
				ls.fontColor = new Color(.48f, .73f, 1f, 1f);
				this.score[i] = new PixelLabel("10 000 000", ls);
				this.score[i].setAlignment(Align.right);
				super.addActor(this.score[i]);
			}
		}
		public static LeaderBoard newInstance(TextureAtlas atlas) {
			LeaderBoard instance = new LeaderBoard(atlas);
			return instance;
		}
		@Override
		public void resize(int width, int height) {
			for (int i = 0; i < 3; i++) {
				this.place[i].resize(width, height);
				this.score[i].resize(width, height);
			}
		}
		@Override
		public void setBounds(float x, float y, float width, float height) {
			super.setBounds(x, y, width, height);
			this.background.setBounds(0f, 0f, width, height);
			for (int i = 0; i < 3; i++) {
				this.place[i].setBounds(.05f * width, (.65f - (float)i * .28f) * height, .4f * width, .3f * height);
				this.score[i].setBounds(.45f * width, (.65f - (float)i * .28f) * height, .5f * width, .3f * height);
			}
		}
		public void fillList(UserInfo[] users) {
			if (users == null) users = new UserInfo[0];
			for (int i = 0; i < 3; i++) {
				if (i < users.length) {
					this.score[i].setText(String.valueOf(users[i].getScore()));
					this.place[i].setVisible(true);
					this.score[i].setVisible(true);
				} else {
					this.place[i].setVisible(false);
					this.score[i].setVisible(false);
				}
			}
		}
	}

	private static class PrizeFrame extends Group implements Resizable {
		private final DrawableActor background;
		private final PixelLabel title;
		private final PixelLabel value;
		private PrizeFrame(TextureAtlas atlas) {
			super();
			this.background = DrawableActor.newInstance(atlas.findRegion("prize_frame"));
			super.addActor(this.background);
			LabelStyle ls = new LabelStyle();
			ls.font = FontsFactory.getAsync("Taurus.ttf", 24);
			ls.fontColor = new Color(.1f, .1f, .1f, 1f);
			this.title = new PixelLabel(Connection.getString("cup_prize"), ls);
			this.title.setAlignment(Align.center);
			super.addActor(this.title);
			ls = new LabelStyle();
			ls.font = FontsFactory.getAsync("Junegull.ttf", 28);
			ls.fontColor = Color.GREEN;
			this.value = new PixelLabel("1 000 000", ls);
			this.value.setAlignment(Align.center);
			super.addActor(this.value);
		}
		public static PrizeFrame newInstance(TextureAtlas atlas) {
			PrizeFrame instance = new PrizeFrame(atlas);
			return instance;
		}
		@Override
		public void resize(int width, int height) {
			this.title.resize(width, height);
			this.value.resize(width, height);
		}
		@Override
		public void setBounds(float x, float y, float width, float height) {
			super.setBounds(x, y, width, height);
			this.background.setBounds(0f, 0f, width, height);
			this.title.setBounds(0f, .5f * height, width, .5f * height);
			this.value.setBounds(0f, .1f * height, width, .4f * height);
		}
		public void setValue(int value) {
			this.value.setText(String.valueOf(value));
		}
	}

}
