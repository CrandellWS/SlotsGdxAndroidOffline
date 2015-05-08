package mobi.square.slots.ui.bonus;

import mobi.square.slots.api.Connection;
import mobi.square.slots.config.AppConfig;
import mobi.square.slots.tools.FontsFactory;
import mobi.square.slots.ui.DrawableActor;
import mobi.square.slots.ui.GameHeader;
import mobi.square.slots.ui.PixelLabel;
import mobi.square.slots.ui.Resizable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class ResidentLocksGroup extends Group implements Resizable {

	private final Texture background;
	private final ResidentSafe[] safes;
	private final ResidentAward label;
	private final DrawableActor fire_bottle;
	private final DrawableActor resident;
	private final GameOverWindow game_over;
	private SafeOpenedListener safe_listener;

	private ResidentLocksGroup(Texture background, TextureAtlas atlas, TextureAtlas common) {
		this.safe_listener = null;
		this.background = background;
		this.fire_bottle = DrawableActor.newInstance(new TextureRegionDrawable(atlas.findRegion("fire_bottle")));
		this.resident = DrawableActor.newInstance(new TextureRegionDrawable(common.findRegion("resident")));
		this.game_over = GameOverWindow.newInstance(common);
		this.label = ResidentAward.newInstance(common);
		this.safes = new ResidentSafe[] {
			ResidentSafe.newInstance(atlas),
			ResidentSafe.newInstance(atlas),
			ResidentSafe.newInstance(atlas),
			ResidentSafe.newInstance(atlas)
		};
		for (int i = 0; i < this.safes.length; i++) {
			this.safes[i].addListener(new SafeListener(i) {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					if (!safes[super.index].isOpened() &&
						safe_listener != null) {
						safe_listener.opened(super.index);
					}
				}
			});
			super.addActor(this.safes[i]);
		}
		super.addActor(this.fire_bottle);
		super.addActor(this.resident);
		super.addActor(this.label);
		super.addActor(this.game_over);
	}

	public static ResidentLocksGroup newInstance(Texture background, TextureAtlas atlas, TextureAtlas common) {
		ResidentLocksGroup instance = new ResidentLocksGroup(background, atlas, common);
		instance.setBounds(0f, 0f, AppConfig.VIEWPORT_WIDTH, AppConfig.VIEWPORT_HEIGHT - GameHeader.HEADER_HEIGHT);
		for (int i = 0; i < instance.safes.length; i++)
			instance.safes[i].setBounds(78f + 244f * (float)i, 238f, 212f, 161f);
		instance.fire_bottle.setBounds(478f, 33f, 95f, 161f);
		instance.resident.setBounds(-86f, 0f, 201f, 319f);
		instance.label.setBounds(610f, 430f, 379f, 67f);
		instance.game_over.setBounds(262f, 160f, 500f, 157f);
		instance.game_over.setVisible(false);
		return instance;
	}

	private class SafeListener extends ClickListener {
		protected final int index;
		public SafeListener(int index) {
			super();
			this.index = index;
		}
	}

	public interface SafeOpenedListener {
		public void opened(int index);
	}

	public void setSafeListener(SafeOpenedListener listener) {
		this.safe_listener = listener;
	}

	public void setFireBottleAvailable(boolean available) {
		this.fire_bottle.setVisible(available);
	}

	public void setTotalAward(int award) {
		this.label.setAward(award);
	}

	public void setResidentIndex(int index) {
		this.resident.setX((float)(244 * index - 86));
	}

	public boolean isSafeOpened(int index) {
		return this.safes[index].isOpened();
	}

	public void setSafeOpened(int index, int amount, boolean over) {
		if (this.safes[index].isOpened()) return;
		this.safes[index].open(amount, over);
	}

	public void showGameOverWindow() {
		this.game_over.setTextGameOver();
		this.game_over.setVisible(true);
	}

	public void showSuperGameWindow() {
		this.game_over.setTextSuperGame();
		this.game_over.setVisible(true);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.draw(this.background, super.getX(), super.getY(), super.getWidth(), super.getHeight());
		super.draw(batch, parentAlpha);
	}

	@Override
	public void resize(int width, int height) {
		for (int i = 0; i < this.safes.length; this.safes[i++].resize(width, height));
		this.game_over.resize(width, height);
		this.label.resize(width, height);
	}

	private static class ResidentSafe extends Group implements Resizable {
		private final PixelLabel award;
		private final TextureRegionDrawable closed_tex;
		private final TextureRegionDrawable opened_tex;
		private final TextureRegionDrawable money_tex;
		private final TextureRegionDrawable bomb_active;
		private final TextureRegionDrawable bomb_inactive;
		private int state;
		private ResidentSafe(TextureAtlas atlas) {
			LabelStyle ls = new LabelStyle();
			ls.font = FontsFactory.getAsync("arial.ttf", 22);
			ls.fontColor = Color.WHITE;
			this.award = new PixelLabel("", ls);
			this.award.setAlignment(Align.center, Align.center);
			super.addActor(this.award);
			this.closed_tex = new TextureRegionDrawable(atlas.findRegion("safe_closed"));
			this.opened_tex = new TextureRegionDrawable(atlas.findRegion("safe_opened"));
			this.money_tex = new TextureRegionDrawable(atlas.findRegion("money_prize"));
			this.bomb_active = new TextureRegionDrawable(atlas.findRegion("bomb_active"));
			this.bomb_inactive = new TextureRegionDrawable(atlas.findRegion("bomb_inactive"));
			this.state = 0;
		}
		public static ResidentSafe newInstance(TextureAtlas atlas) {
			ResidentSafe instance = new ResidentSafe(atlas);
			return instance;
		}
		public boolean isOpened() {
			return this.state != 0;
		}
		public void open(int amount, boolean over) {
			this.state = amount > 0 ? 1 : over ? 2 : 3;
			if (amount > 0) this.award.setText(String.valueOf(amount));
		}
		@Override
		public void draw(Batch batch, float parentAlpha) {
			float x = super.getX();
			float y = super.getY();
			float width = super.getWidth();
			float height = super.getHeight();
			if (this.state > 0) {
				this.opened_tex.draw(batch, x, y, width, height);
				switch (this.state) {
					case 1:
						this.money_tex.draw(batch, x + .15f * width, y + .1f * height, .434f * width, .907f * height);
						break;
					case 2:
						this.bomb_active.draw(batch, x + .15f * width, y + .1f * height, .434f * width, .907f * height);
						break;
					case 3:
						this.bomb_inactive.draw(batch, x + .15f * width, y + .1f * height, .434f * width, .907f * height);
						break;
				}
			} else {
				this.closed_tex.draw(batch, x, y, width, height);
			}
			super.draw(batch, parentAlpha);
		}
		@Override
		public void setBounds(float x, float y, float width, float height) {
			super.setBounds(x, y, width, height);
			this.award.setBounds(0f, .4f * height, .65f * width, .5f * height);
		}
		@Override
		public void resize(int width, int height) {
			this.award.resize(width, height);
		}
	}

	public static class ResidentAward extends Group implements Resizable {
		private final TextureRegionDrawable background;
		private final PixelLabel label_title;
		private final PixelLabel label_award;
		private ResidentAward(TextureAtlas atlas) {
			this.background = new TextureRegionDrawable(atlas.findRegion("award_frame"));
			LabelStyle ls = new LabelStyle();
			ls.font = FontsFactory.getAsync("Junegull.ttf", 24);
			ls.fontColor = Color.BLACK;
			this.label_title = new PixelLabel(Connection.getString("common_award"), ls);
			this.label_title.setAlignment(Align.center, Align.center);
			ls = new LabelStyle();
			ls.font = FontsFactory.getAsync("Junegull.ttf", 24);
			ls.fontColor = Color.BLACK;
			this.label_award = new PixelLabel("0", ls);
			this.label_award.setAlignment(Align.center, Align.center);
			super.addActor(this.label_title);
			super.addActor(this.label_award);
		}
		public static ResidentAward newInstance(TextureAtlas atlas) {
			ResidentAward instance = new ResidentAward(atlas);
			return instance;
		}
		public void setAward(int award) {
			this.label_award.setText(String.valueOf(award));
		}
		@Override
		public void draw(Batch batch, float parentAlpha) {
			this.background.draw(batch, super.getX(), super.getY(), super.getWidth(), super.getHeight());
			super.draw(batch, parentAlpha);
		}
		@Override
		public void setBounds(float x, float y, float width, float height) {
			super.setBounds(x, y, width, height);
			this.label_title.setBounds(.136f * width, .04f * height, .314f * width, height);
			this.label_award.setBounds(.45f * width, .04f * height, .42f * width, height);
		}
		@Override
		public void resize(int width, int height) {
			this.label_title.resize(width, height);
			this.label_award.resize(width, height);
		}
	}

	public static class GameOverWindow extends PixelLabel {
		private final TextureRegionDrawable background;
		private final float[] bg_bounds;
		private GameOverWindow(TextureAtlas atlas, LabelStyle style) {
			super("", style);
			this.background = new TextureRegionDrawable(atlas.findRegion("game_over_label"));
			this.bg_bounds = new float[4];
		}
		public static GameOverWindow newInstance(TextureAtlas atlas) {
			LabelStyle style = new LabelStyle();
			style.font = FontsFactory.getAsync("Junegull.ttf", 36);
			style.fontColor = Color.BLACK;
			GameOverWindow instance = new GameOverWindow(atlas, style);
			instance.setAlignment(Align.center, Align.center);
			return instance;
		}
		public void setTextGameOver() {
			super.setText(Connection.getString("bonus_game_over"));
		}
		public void setTextSuperGame() {
			super.setText(Connection.getString("bonus_super_game"));
		}
		public void setTextSuperPrize() {
			super.setText(Connection.getString("bonus_super_prize"));
		}
		@Override
		public void draw(Batch batch, float parentAlpha) {
			this.background.draw(batch, this.bg_bounds[0], this.bg_bounds[1], this.bg_bounds[2], this.bg_bounds[3]);
			super.draw(batch, parentAlpha);
		}
		@Override
		public void setBounds(float x, float y, float width, float height) {
			this.bg_bounds[0] = x;
			this.bg_bounds[1] = y;
			this.bg_bounds[2] = width;
			this.bg_bounds[3] = height;
			super.setBounds(x + .087f * width, y + .287f * height, .81f * width, .428f * height);
		}
	}

}
