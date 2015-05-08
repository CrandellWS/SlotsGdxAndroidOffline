package mobi.square.slots.ui.bonus;

import mobi.square.slots.api.Connection;
import mobi.square.slots.config.AppConfig;
import mobi.square.slots.tools.FontsFactory;
import mobi.square.slots.ui.GameHeader;
import mobi.square.slots.ui.PixelLabel;
import mobi.square.slots.ui.Resizable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class CrazyMonkeyRopesGroup extends Group implements Resizable {

	private final Texture background;
	private final CrazyMonkeyHerself monkey;
	private final CrazyMonkeyLiana[] lianes;
	private final CrazyMonkeyAward label;
	private final GameOverWindow game_over;
	private RopeOpenedListener listener;

	private static final float[] MONKEY_X = { 90f, 250f, 400f, 560f, 716f };
	private static final float[] MONKEY_Y = { 10f, 0f, 0f, 10f, 10f };

	private CrazyMonkeyRopesGroup(Texture background, TextureAtlas atlas, TextureAtlas common) {
		super();
		this.background = background;
		this.lianes = new CrazyMonkeyLiana[5];
		for (int i = 0; i < this.lianes.length; i++) {
			this.lianes[i] = CrazyMonkeyLiana.newInstance(atlas, i);
			this.lianes[i].addListener(new RopeListener(i) {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					if (listener != null && !lianes[super.index].isOpened()) {
						monkey.setPosition(MONKEY_X[super.index], MONKEY_Y[super.index]);
						listener.opened(super.index);
					}
				}
			});
			super.addActor(this.lianes[i]);
		}
		this.monkey = CrazyMonkeyHerself.newInstance(common);
		this.game_over = GameOverWindow.newInstance(common);
		this.label = CrazyMonkeyAward.newInstance(common);
		super.addActor(this.monkey);
		super.addActor(this.label);
		super.addActor(this.game_over);
		this.listener = null;
	}

	public static CrazyMonkeyRopesGroup newInstance(Texture background, TextureAtlas atlas, TextureAtlas common) {
		CrazyMonkeyRopesGroup instance = new CrazyMonkeyRopesGroup(background, atlas, common);
		instance.setBounds(0f, 0f, AppConfig.VIEWPORT_WIDTH, AppConfig.VIEWPORT_HEIGHT - GameHeader.HEADER_HEIGHT);
		for (int i = 0; i < instance.lianes.length; i++)
			instance.lianes[i].setBounds(180f + 160f * (float)i, instance.getHeight() - 420f, 28f, 475f);
		instance.monkey.setBounds(-50f, -100f, 204f, 322f);
		instance.label.setBounds(257f, instance.getHeight() - 85f, 509f, 105f);
		instance.game_over.setBounds(230f, 154f, 564f, 236f);
		instance.game_over.setVisible(false);
		return instance;
	}

	private class RopeListener extends ClickListener {
		protected final int index;
		public RopeListener(int index) {
			super();
			this.index = index;
		}
	}

	public interface RopeOpenedListener {
		public void opened(int index);
	}

	public void setRopeListener(RopeOpenedListener listener) {
		this.listener = listener;
	}

	public void setTotalAward(int award) {
		this.label.setAward(award);
	}

	public void open(int index, int award) {
		this.lianes[index].open(award);
	}

	public void setMonkeyInHelmet(boolean in_helmet) {
		this.monkey.setInHelmet(in_helmet);
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
		for (int i = 0; i < this.lianes.length; this.lianes[i++].resize(width, height));
		this.game_over.resize(width, height);
		this.label.resize(width, height);
	}

	public static class CrazyMonkeyAward extends Group implements Resizable {
		private final TextureRegionDrawable background;
		private final PixelLabel label_title;
		private final PixelLabel label_award;
		private CrazyMonkeyAward(TextureAtlas atlas) {
			this.background = new TextureRegionDrawable(atlas.findRegion("award_frame"));
			LabelStyle ls = new LabelStyle();
			ls.font = FontsFactory.getAsync("Junegull.ttf", 32);
			ls.fontColor = new Color(.188f, .247f, 0f, 1f);
			this.label_title = new PixelLabel(Connection.getString("common_award"), ls);
			this.label_title.setAlignment(Align.center, Align.center);
			ls = new LabelStyle();
			ls.font = FontsFactory.getAsync("Junegull.ttf", 32);
			ls.fontColor = new Color(.137f, 0f, 0f, 1f);
			this.label_award = new PixelLabel("0", ls);
			this.label_award.setAlignment(Align.center, Align.center);
			super.addActor(this.label_title);
			super.addActor(this.label_award);
		}
		public static CrazyMonkeyAward newInstance(TextureAtlas atlas) {
			CrazyMonkeyAward instance = new CrazyMonkeyAward(atlas);
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
			this.label_title.setBounds(.094f * width, 0f, .4f * width, .84f * height);
			this.label_award.setBounds(.5f * width, 0f, .36f * width, .84f * height);
		}
		@Override
		public void resize(int width, int height) {
			this.label_title.resize(width, height);
			this.label_award.resize(width, height);
		}
	}

	private static class CrazyMonkeyLiana extends Group implements Resizable {
		private final PixelLabel label;
		private final TextureRegionDrawable background;
		private final TextureRegionDrawable banana_tex;
		private final TextureRegionDrawable anvil_tex;
		private int state;
		private CrazyMonkeyLiana(TextureAtlas atlas, int index) {
			super();
			LabelStyle ls = new LabelStyle();
			ls.font = FontsFactory.getAsync("arial.ttf", 22);
			ls.fontColor = Color.WHITE;
			this.label = new PixelLabel("", ls);
			this.label.setAlignment(Align.center, Align.center);
			super.addActor(this.label);
			this.background = new TextureRegionDrawable(atlas.findRegion("liana", index));
			this.banana_tex = new TextureRegionDrawable(atlas.findRegion("prize_banana"));
			this.anvil_tex = new TextureRegionDrawable(atlas.findRegion("prize_anvil"));
			this.state = 0;
		}
		public static CrazyMonkeyLiana newInstance(TextureAtlas atlas, int index) {
			CrazyMonkeyLiana instance = new CrazyMonkeyLiana(atlas, index);
			return instance;
		}
		public boolean isOpened() {
			return this.state != 0;
		}
		public void open(int award) {
			if (this.state != 0) return;
			if (award > 0) {
				this.label.setText(String.valueOf(award));
				this.state = 1;
			} else this.state = 2;
		}
		@Override
		public Actor hit(float x, float y, boolean touchable) {
			if (touchable && super.getTouchable() != Touchable.enabled) return null;
			float width = super.getWidth();
			float height = super.getHeight();
			return x >= -2f * width && x < 3f * width && y >= 0 && y < height ? this : null;
		}
		@Override
		public void draw(Batch batch, float parentAlpha) {
			float x = super.getX();
			float y = super.getY();
			float width = super.getWidth();
			float height = super.getHeight();
			if (this.state == 1) {
				this.banana_tex.draw(batch, x - 2f * width, y + .4f * height, 4.536f * width, .217f * height);
			} else if (this.state == 2) {
				this.anvil_tex.draw(batch, x - 2f * width, y + .4f * height, 4.536f * width, .217f * height);
			} else this.background.draw(batch, x, y, width, height);
			super.draw(batch, parentAlpha);
		}
		@Override
		public void setBounds(float x, float y, float width, float height) {
			super.setBounds(x, y, width, height);
			this.label.setBounds(0f, .6f * height, width, .05f * height);
		}
		@Override
		public void resize(int width, int height) {
			this.label.resize(width, height);
		}
	}

	public static class CrazyMonkeyHerself extends Actor {
		private final TextureRegionDrawable monkey;
		private final TextureRegionDrawable helmet;
		private final float[] bounds;
		private boolean in_helmet;
		private CrazyMonkeyHerself(TextureAtlas atlas) {
			super();
			this.monkey = new TextureRegionDrawable(atlas.findRegion("monkey"));
			this.helmet = new TextureRegionDrawable(atlas.findRegion("helmet"));
			this.bounds = new float[4];
			this.in_helmet = true;
		}
		public static CrazyMonkeyHerself newInstance(TextureAtlas atlas) {
			CrazyMonkeyHerself instance = new CrazyMonkeyHerself(atlas);
			return instance;
		}
		public void setInHelmet(boolean in_helmet) {
			this.in_helmet = in_helmet;
		}
		@Override
		public void draw(Batch batch, float parentAlpha) {
			float x = super.getX();
			float y = super.getY();
			float width = super.getWidth();
			float height = super.getHeight();
			this.monkey.draw(batch, x, y, width, height);
			if (this.in_helmet) {
				this.helmet.draw(batch, this.bounds[0], this.bounds[1], this.bounds[2], this.bounds[3]);
			}
		}
		@Override
		public void setBounds(float x, float y, float width, float height) {
			super.setBounds(x, y, width, height);
			this.bounds[0] = x + .375f * width;
			this.bounds[1] = y + .9f * height;
			this.bounds[2] = .315f * width;
			this.bounds[3] = .124f * height;
		}
		@Override
		public void setPosition(float x, float y) {
			super.setPosition(x, y);
			this.bounds[0] = x + .375f * super.getWidth();
			this.bounds[1] = y + .9f * super.getHeight();
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
			style.fontColor = new Color(0.188f, 0.251f, 0f, 1f);
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
			super.setBounds(x + .087f * width, y + .32f * height, .81f * width, .428f * height);
		}
	}

}
