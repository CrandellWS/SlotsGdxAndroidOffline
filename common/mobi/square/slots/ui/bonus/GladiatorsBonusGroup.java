package mobi.square.slots.ui.bonus;

import mobi.square.slots.api.Connection;
import mobi.square.slots.config.AppConfig;
import mobi.square.slots.tools.FontsFactory;
import mobi.square.slots.ui.GameHeader;
import mobi.square.slots.ui.PixelLabel;
import mobi.square.slots.ui.Resizable;
import mobi.square.slots.utils.utils;

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

public class GladiatorsBonusGroup extends Group implements Resizable {

	private final Texture background;
	private final GladiatorsBox[] boxes;
	private final GladiatorsAward award_frame;
	private final GameOverWindow game_over;
	private BoxOpenedListener listener;

	private static final int PRIZE_COUNT = 7;
	private static final int GAME_OVER_COUNT = 2;

	private GladiatorsBonusGroup(Texture background, TextureAtlas atlas) {
		super();
		this.listener = null;
		this.background = background;
		TextureRegionDrawable barrel = new TextureRegionDrawable(atlas.findRegion("box"));
		this.boxes = new GladiatorsBox[9];
		for (int i = 0; i < 9; i++) {
			this.boxes[i] = GladiatorsBox.newInstance(barrel);
			this.boxes[i].addListener(new BarrelClickListener(i) {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					if (listener != null && boxes[super.index].closed) {
						listener.opened(super.index);
					}
				}
			});
			super.addActor(this.boxes[i]);
		}
		this.award_frame = GladiatorsAward.newInstance(atlas);
		this.game_over = GameOverWindow.newInstance(atlas);
		super.addActor(this.award_frame);
		super.addActor(this.game_over);
	}

	public static GladiatorsBonusGroup newInstance(Texture background, TextureAtlas atlas) {
		GladiatorsBonusGroup instance = new GladiatorsBonusGroup(background, atlas);
		instance.setBounds(0f, 0f, AppConfig.VIEWPORT_WIDTH, AppConfig.VIEWPORT_HEIGHT - GameHeader.HEADER_HEIGHT);
		instance.award_frame.setBounds(318f, 0f, 388f, 58f);
		instance.boxes[0].setBounds(102f, 220f, 160f, 144f);
		instance.boxes[1].setBounds(262f, 270f, 160f, 144f);
		instance.boxes[2].setBounds(592f, 270f, 160f, 144f);
		instance.boxes[3].setBounds(752f, 220f, 160f, 144f);
		instance.boxes[4].setBounds(82f, 30f, 180f, 162f);
		instance.boxes[5].setBounds(252f, 100f, 180f, 162f);
		instance.boxes[6].setBounds(422f, 170f, 180f, 162f);
		instance.boxes[7].setBounds(592f, 100f, 180f, 162f);
		instance.boxes[8].setBounds(762f, 30f, 180f, 162f);
		instance.game_over.setBounds(316f, 160f, 391f, 179f);
		instance.game_over.setVisible(false);
		return instance;
	}

	public void setListener(BoxOpenedListener listener) {
		this.listener = listener;
	}

	public void open(int index, int award) {
		if (index < 0 || index > 8) return;
		if (this.boxes[index].closed) {
			this.boxes[index].open(award > 0 ? utils.getRandom(PRIZE_COUNT) + 1 : utils.getRandom(GAME_OVER_COUNT) + 1, award);
		}
	}

	public void setAward(int award) {
		this.award_frame.setAward(award);
	}

	public void showGameOverWindow() {
		this.game_over.setVisible(true);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.draw(this.background, super.getX(), super.getY(), super.getWidth(), super.getHeight());
		super.draw(batch, parentAlpha);
	}

	@Override
	public void resize(int width, int height) {
		for (int i = 0; i < 9; this.boxes[i++].resize(width, height));
		this.award_frame.resize(width, height);
		this.game_over.resize(width, height);
	}

	public interface BoxOpenedListener {
		public void opened(int index);
	}

	public class BarrelClickListener extends ClickListener {
		protected int index;
		public BarrelClickListener(int index) {
			this.index = index;
		}
	}

	private static class GladiatorsBox extends Group implements Resizable {
		private boolean closed;
		private TextureRegionDrawable prize;
		private final TextureRegionDrawable box;
		private final PixelLabel award_label;
		private GladiatorsBox(TextureRegionDrawable box) {
			this.closed = true;
			this.box = box;
			this.prize = null;
			LabelStyle ls = new LabelStyle();
			ls.font = FontsFactory.getAsync("arial.ttf", 28);
			ls.fontColor = Color.WHITE;
			this.award_label = new PixelLabel("", ls);
			this.award_label.setAlignment(Align.center, Align.center);
			super.addActor(this.award_label);
		}
		public static GladiatorsBox newInstance(TextureRegionDrawable barrel) {
			GladiatorsBox instance = new GladiatorsBox(barrel);
			instance.close();
			return instance;
		}
		public void close() {
			this.closed = true;
		}
		public void open(int index, int award) {
			TextureAtlas atlas = Connection.getManager().get("atlas/GladiatorsBonus.pack", TextureAtlas.class);
			this.prize = new TextureRegionDrawable(atlas.findRegion(award > 0 ? "box_prize" : "box_over", index));
			this.award_label.setText(String.valueOf(award));
			this.award_label.setVisible(award > 0);
			this.closed = false;
		}
		@Override
		public void draw(Batch batch, float parentAlpha) {
			if (this.closed) {
				if (this.box != null) {
					this.box.draw(batch, super.getX(), super.getY(), super.getWidth(), super.getHeight());
				}
			} else {
				if (this.prize != null) {
					this.prize.draw(batch, super.getX(), super.getY(), super.getWidth(), super.getHeight());
				}
			}
			super.draw(batch, parentAlpha);
		}
		@Override
		public void setBounds(float x, float y, float width, float height) {
			super.setBounds(x, y, width, height);
			this.award_label.setBounds(0f, .75f * height, width, .25f * height);
		}
		@Override
		public void resize(int width, int height) {
			this.award_label.resize(width, height);
		}
	}

	private static class GladiatorsAward extends Group implements Resizable {
		private final TextureRegionDrawable background;
		private final PixelLabel label_title;
		private final PixelLabel label_award;
		private GladiatorsAward(TextureAtlas atlas) {
			this.background = new TextureRegionDrawable(atlas.findRegion("award_frame"));
			LabelStyle ls = new LabelStyle();
			ls.font = FontsFactory.getAsync("Junegull.ttf", 24);
			ls.fontColor = new Color(.13f, .13f, .13f, 1f);
			this.label_title = new PixelLabel(Connection.getString("common_award"), ls);
			this.label_title.setAlignment(Align.center, Align.center);
			ls = new LabelStyle();
			ls.font = FontsFactory.getAsync("Junegull.ttf", 24);
			ls.fontColor = new Color(.18f, 0f, 0f, 1f);
			this.label_award = new PixelLabel("0", ls);
			this.label_award.setAlignment(Align.center, Align.center);
			super.addActor(this.label_title);
			super.addActor(this.label_award);
		}
		public static GladiatorsAward newInstance(TextureAtlas atlas) {
			GladiatorsAward instance = new GladiatorsAward(atlas);
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
			this.label_title.setBounds(.136f * width, .12f * height, .314f * width, height);
			this.label_award.setBounds(.48f * width, .12f * height, .4f * width, height);
		}
		@Override
		public void resize(int width, int height) {
			this.label_title.resize(width, height);
			this.label_award.resize(width, height);
		}
	}

	private static class GameOverWindow extends BasicGameOverWindow {
		protected GameOverWindow(TextureAtlas atlas, LabelStyle style) {
			super(atlas, style);
		}
		public static GameOverWindow newInstance(TextureAtlas atlas) {
			LabelStyle style = new LabelStyle();
			style.font = FontsFactory.getAsync("Junegull.ttf", 34);
			style.fontColor = new Color(.13f, .13f, .13f, 1f);
			GameOverWindow instance = new GameOverWindow(atlas, style);
			return instance;
		}
		@Override
		public void setLabelBounds(float x, float y, float width, float height) {
			super.setLabelBounds(x + .1f * width, y + .3f * height, .81f * width, .47f * height);
		}
	}

}
