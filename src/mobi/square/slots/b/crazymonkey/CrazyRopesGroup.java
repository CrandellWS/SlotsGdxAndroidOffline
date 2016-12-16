package mobi.square.slots.b.crazymonkey;

import mobi.square.slots.classes.Machine;
import mobi.square.slots.config.AppConfig;
import mobi.square.slots.ui.DrawableActor;
import mobi.square.slots.ui.GameHeader;
import mobi.square.slots.ui.IntClickListener;
import mobi.square.slots.ui.PixelLabel;
import mobi.square.slots.ui.Resizable;
import mobi.square.slots.utils.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

public class CrazyRopesGroup extends Group implements Resizable {

	private final MonkeyAnim monkey;
	private final DrawableActor background;
	private final PixelLabel[] awards;
	private final CrazyItem item;
	private final Rope[] ropes;
	
	private int index;
	private int award;
	private CrazyMonkeyListener listener;

	private CrazyRopesGroup(Texture background, TextureAtlas atlas) {
		this.background = DrawableActor.newInstance(background);
		super.addActor(this.background);
		this.item = CrazyItem.newInstance(atlas);
		this.item.setListener(new ItemListener() {
			@Override
			public void done() {
				awards[index].setVisible(true);
				if (award <= 0) {
					if (monkey.helmeted) {
						monkey.helmeted = false;
					} else monkey.playBoomAction();
				}
				if (listener != null) {
					listener.done();
				}
			}
		});
		super.addActor(this.item);
		this.ropes = new Rope[] {
			Rope.newInstance(atlas),
			Rope.newInstance(atlas),
			Rope.newInstance(atlas),
			Rope.newInstance(atlas),
			Rope.newInstance(atlas)
		};
		for (int i = 0; i < this.ropes.length; i++) {
			this.ropes[i].addListener(new IntClickListener(i) {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					if (listener != null) {
						listener.clicked(super.value);
					}
				}
			});
			super.addActor(this.ropes[i]);
		}
		this.awards = new PixelLabel[] {
			PixelLabel.newInstance("", "arialbd.ttf", 38f, new Color(1f, 1f, 0f, 1f), Align.center),
			PixelLabel.newInstance("", "arialbd.ttf", 38f, new Color(1f, 1f, 0f, 1f), Align.center),
			PixelLabel.newInstance("", "arialbd.ttf", 38f, new Color(1f, 1f, 0f, 1f), Align.center),
			PixelLabel.newInstance("", "arialbd.ttf", 38f, new Color(1f, 1f, 0f, 1f), Align.center),
			PixelLabel.newInstance("", "arialbd.ttf", 38f, new Color(1f, 1f, 0f, 1f), Align.center)
		};
		for (int i = 0; i < this.awards.length; super.addActor(this.awards[i++]));
		this.monkey = MonkeyAnim.newInstance(atlas);
		this.monkey.setListener(new MonkeyListener() {
			@Override
			public void ropeUsed() {
				ropes[index].setVisible(false);
				item.setToRope(index);
			}
			@Override
			public void placed() {
				if (award > 0) {
					item.showBanana();
					monkey.playEatAction();
				} else item.showDeath();
			}
		});
		super.addActor(this.monkey);
		this.listener = null;
		this.index = 0;
		this.award = 0;
	}

	public static CrazyRopesGroup newInstance(Texture background, TextureAtlas atlas) {
		CrazyRopesGroup instance = new CrazyRopesGroup(background, atlas);
		instance.setBounds(0f, 0f, AppConfig.VIEWPORT_WIDTH, AppConfig.VIEWPORT_HEIGHT - GameHeader.HEADER_HEIGHT);
		for (int i = 0; i < instance.ropes.length; i++) {
			instance.ropes[i].setPosition(240f + i * 130f, 116f);
			instance.awards[i].setBounds(200f + i * 130f, 350f, 60f, 50f);
			instance.awards[i].setVisible(false);
		}
		instance.monkey.setBounds(100f, 40f, 210f, 230f);
		return instance;
	}

	@Override
	public void resize(int width, int height) {
		for (int i = 0; i < this.awards.length; this.awards[i++].resize(width, height));
	}

	@Override
	public void setBounds(float x, float y, float width, float height) {
		super.setBounds(x, y, width, height);
		this.background.setBounds(0f, 0f, width, height);
	}

	public void gotoRope(int index) {
		this.monkey.gotoRope(index);
		this.index = index;
	}

	public void useRope(int award) {
		this.monkey.playRopeAction(award);
		this.awards[this.index].setText(String.valueOf(award));
		this.award = award;
	}

	public void setHelmeted(boolean helmet) {
		this.monkey.setHelmeted(helmet);
	}

	public void setAwards(int[] awards) {
		if (awards == null) return;
		for (int i = 0; i < awards.length && i < this.awards.length; i++) {
			if (awards[i] >= 0) {
				this.awards[i].setText(String.valueOf(awards[i]));
				this.awards[i].setVisible(true);
				this.ropes[i].setVisible(false);
			} else {
				this.awards[i].setVisible(false);
				this.ropes[i].setVisible(true);
			}
		}
	}

	public void setListener(CrazyMonkeyListener listener) {
		this.listener = listener;
	}

	public interface CrazyMonkeyListener {
		public void clicked(int index);
		public void done();
	}

	private interface MonkeyListener {
		public void ropeUsed();
		public void placed();
	}

	private interface ItemListener {
		public void done();
	}

	public static class MonkeyAnim extends Actor {
		private static final float[][] ASPECT = {
			{ 0f, 0f, 1f, 1f },
			{ 0f, 0f, 1f, 1f },
			{ 0f, 0f, 1f, 1f },
			{ 0f, 0f, 1f, 1f },
			{ 0f, 0f, 1f, 1f },
			{ 0f, 0f, 1f, 1f },
			{ 0f, 0f, .841f, 1.176f },
			{ -.012f, -.004f, 1.325f, 1.04f },
			{ -.14f, 0f, 1.224f, 1.481f }
		};
		private static final float[] TIME = { .35f, .2f, .2f, .4f, .2f, .3f, .1f, .1f, .2f };
		private static final int[] RANDOMS = { 1, 2 };
		private final float[] bounds;
		private final TextureRegion[][] monkey;
		private final TextureRegion[][] helmet;
		private boolean helmeted;
		private boolean stop;
		private int state;
		private int frame;
		private int loop;
		private float time;
		private MonkeyListener listener;
		private MonkeyAnim(TextureAtlas atlas) {
			this.time = 0f;
			this.loop = 4;
			this.state = 0;
			this.frame = 0;
			this.stop = false;
			this.helmeted = false;
			this.bounds = new float[4];
			this.monkey = new TextureRegion[][] {
				Machine.loadTextures(atlas, "a000", 4, 0),
				Machine.loadTextures(atlas, "a001", 6, 0),
				Machine.loadTextures(atlas, "a002", 6, 0),
				Machine.loadTextures(atlas, "a003", 2, 0),
				Machine.loadTextures(atlas, "a004", 4, 0),
				Machine.loadTextures(atlas, "a005", 2, 0),
				Machine.loadTextures(atlas, "a006", 16, 0),
				Machine.loadTextures(atlas, "a007", 13, 0),
				Machine.loadTextures(atlas, "a008", 11, 0)
			};
			this.helmet = new TextureRegion[][] {
				Machine.loadTextures(atlas, "c000", 4, 0),
				Machine.loadTextures(atlas, "c001", 6, 0),
				Machine.loadTextures(atlas, "c002", 6, 0),
				Machine.loadTextures(atlas, "c003", 2, 0),
				Machine.loadTextures(atlas, "c004", 4, 0),
				Machine.loadTextures(atlas, "c005", 2, 0),
				Machine.loadTextures(atlas, "c006", 16, 0),
				Machine.loadTextures(atlas, "c007", 13, 0),
				null
			};
			this.listener = null;
		}
		public static MonkeyAnim newInstance(TextureAtlas atlas) {
			MonkeyAnim instance = new MonkeyAnim(atlas);
			return instance;
		}
		@Override
		public void act(float delta) {
			if (this.stop) return;
			this.time += delta;
			while (this.time >= TIME[this.state]) {
				this.time -= TIME[this.state];
				this.frame++;
				if (this.state == 7 && this.frame == 6 && this.listener != null) {
					this.listener.ropeUsed();
				}
				if (this.frame >= this.monkey[this.state].length) {
					this.frame = 0;
					switch (this.state) {
						case 0:
							this.loop--;
							if (this.loop <= 0) {
								this.state = RANDOMS[utils.getRandom(RANDOMS.length)];
								this.loop = utils.getRandom(1, 2);
							}
							break;
						case 1:
						case 2:
							this.loop();
							break;
						case 3:
						case 5:
							break;
						case 4:
							this.state = 5;
							break;
						case 6:
							this.state = 0;
							break;
						case 7:
							this.state = 0;
							this.setX(super.getX() + 89f);
							if (this.listener != null) {
								this.listener.placed();
							}
							break;
						case 8:
							this.frame = 6;
							break;
						default:
							this.state = 0;
							break;
					}
				}
			}
		}
		@Override
		public void draw(Batch batch, float parentAlpha) {
			batch.draw(
				this.monkey[this.state][this.frame],
				this.bounds[0] + this.bounds[2] * ASPECT[this.state][0],
				this.bounds[1] + this.bounds[3] * ASPECT[this.state][1],
				this.bounds[2] * ASPECT[this.state][2],
				this.bounds[3] * ASPECT[this.state][3]
			);
			if (this.helmeted && this.state < 8) {
				batch.draw(
					this.helmet[this.state][this.frame],
					this.bounds[0] + this.bounds[2] * ASPECT[this.state][0],
					this.bounds[1] + this.bounds[3] * ASPECT[this.state][1],
					this.bounds[2] * ASPECT[this.state][2],
					this.bounds[3] * ASPECT[this.state][3]
				);
			}
		}
		@Override
		public void setBounds(float x, float y, float width, float height) {
			this.bounds[0] = x;
			this.bounds[1] = y;
			this.bounds[2] = width;
			this.bounds[3] = height;
			super.setBounds(x, y, width, height);
		}
		@Override
		public void setPosition(float x, float y) {
			this.bounds[0] = x;
			this.bounds[1] = y;
			super.setPosition(x, y);
		}
		@Override
		public void setX(float x) {
			this.bounds[0] = x;
			super.setX(x);
		}
		public void setListener(MonkeyListener listener) {
			this.listener = listener;
		}
		public void gotoRope(int index) {
			this.setX(22f + (float)index * 130f);
		}
		public void setHelmeted(boolean value) {
			this.helmeted = value;
		}
		public void playChooseAction() {
			this.time = 0f;
			this.frame = 0;
			this.state = 3;
		}
		public void playSuperWinAction() {
			this.time = 0f;
			this.frame = 0;
			this.state = 4;
			this.stop = true;
		}
		public void playSuperLossAction() {
			this.time = 0f;
			this.frame = 0;
			this.state = 4;
		}
		public void playEatAction() {
			this.time = 0f;
			this.frame = 0;
			this.state = 6;
		}
		public void playRopeAction(int award) {
			this.time = 0f;
			this.frame = 0;
			this.state = 7;
		}
		public void playBoomAction() {
			this.time = 0f;
			this.frame = 0;
			this.state = 8;
		}
		private void loop() {
			this.loop--;
			if (this.loop <= 0) {
				this.state = 0;
				this.loop = utils.getRandom(3, 5);
			}
		}
	}

	private static class Rope extends Group {
		private static final float WIDTH = 16f;
		private static final float HEIGHT = 400f;
		private static final float ASPECT_MIDDLE = 0.42f;
		private static final float ASPECT_BOTTOM = 3.46f;
		private final TextureRegion repeat;
		private final TextureRegion knot;
		private Rope(TextureAtlas atlas) {
			this.repeat = atlas.findRegion("rope_repeat");
			this.knot = atlas.findRegion("rope_knot");
		}
		public static Rope newInstance(TextureAtlas atlas) {
			Rope instance = new Rope(atlas);
			instance.setBounds(0f, 0f, WIDTH, HEIGHT);
			return instance;
		}
		@Override
		public Actor hit(float x, float y, boolean touchable) {
			return x >= -30f && x < super.getWidth() + 30f && y >= 0 && y < super.getHeight() ? this : null;
		}
		@Override
		public void setBounds(float x, float y, float width, float height) {
			super.setBounds(x - width / 2f, y, width, height);
			this.update(width, height);
		}
		@Override
		public void setPosition(float x, float y) {
			super.setPosition(x - super.getWidth() / 2f, y);
			this.update(super.getWidth(), super.getHeight());
		}
		private void update(float width, float height) {
			super.clearChildren();
			DrawableActor actor;
			// Get bounds
			float h_middle = width * ASPECT_MIDDLE;
			float h_bottom = width * ASPECT_BOTTOM;
			float mid = height - h_bottom;
			int count = (int)Math.ceil(mid / h_middle);
			{ // Bottom
				actor = DrawableActor.newInstance(this.knot);
				actor.setBounds(0f, 0f, width, h_bottom);
				super.addActor(actor);
			}
			// Middle
			for (int i = 0; i < count; i++) {
				actor = DrawableActor.newInstance(this.repeat);
				actor.setBounds(0f, (float)i * h_middle + h_bottom, width, h_middle);
				super.addActor(actor);
			}
		}
	}

	private static class CrazyItem extends Actor {
		private static final float TIME = .2f;
		private final TextureRegion[][] frames;
		private ItemListener listener;
		private float time;
		private int frame;
		private int state;
		private CrazyItem(TextureAtlas atlas) {
			this.frames = new TextureRegion[][] {
				Machine.loadTextures(atlas, "item_banana", 4, 0),
				Machine.loadTextures(atlas, "item_brick", 4, 0),
				Machine.loadTextures(atlas, "item_anvil", 4, 0),
				Machine.loadTextures(atlas, "item_coco", 4, 0)
			};
			this.listener = null;
			this.frame = 0;
			this.state = 0;
			this.time = 0f;
		}
		public static CrazyItem newInstance(TextureAtlas atlas) {
			CrazyItem instance = new CrazyItem(atlas);
			instance.hide();
			return instance;
		}
		@Override
		public void act(float delta) {
			if (this.state >= 0) {
				float y = super.getY();
				y -= delta * 300f;
				if (y < 260f) {
					this.state = -1;
					if (this.listener != null) {
						this.listener.done();
					}
				} else super.setY(y);
				this.time += delta;
				while (this.time >= TIME) {
					this.time -= TIME;
					this.frame++;
					if (this.frame > 3) {
						this.frame = 0;
					}
				}
			}
		}
		@Override
		public void draw(Batch batch, float parentAlpha) {
			if (this.state >= 0 && this.state < 4) {
				batch.draw(
					this.frames[this.state][this.frame],
					super.getX(),
					super.getY(),
					super.getWidth(),
					super.getHeight()
				);
			}
		}
		public void setListener(ItemListener listener) {
			this.listener = listener;
		}
		public void setToRope(int index) {
			super.setBounds(170f + index * 130f, 516f, 90f, 180f);
		}
		public void showBanana() {
			this.state = 0;
		}
		public void showDeath() {
			this.state = utils.getRandom(1, 3);
		}
		public void hide() {
			this.state = -1;
		}
	}

}
