package mobi.square.slots.b.rockclimber;

import java.util.LinkedList;
import java.util.List;

import mobi.square.slots.api.Connection;
import mobi.square.slots.classes.Machine;
import mobi.square.slots.config.AppConfig;
import mobi.square.slots.tools.FontsFactory;
import mobi.square.slots.ui.DrawableActor;
import mobi.square.slots.ui.GameHeader;
import mobi.square.slots.ui.IntClickListener;
import mobi.square.slots.ui.PixelLabel;
import mobi.square.slots.ui.Resizable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class RockClimberGroup extends Group implements Resizable {

	private static final int SECTIONS_COUNT = 2;
	private static final float FALL_SPEED = 220f;
	private static final float CLIMB_SPEED = 100f;
	private static final float SECTION_HEIGHT = 576f;
	private static final float POSITION_OFFSET = -80f;
	private static final float[] CLIMBER_X = { 120f, 290f, 460f, 630f, 800f, 200f };
	private static final float CLIMBER_Y = 230f + POSITION_OFFSET;

	private final RockClimberAward award;
	private final DrawableActor[] arrows;
	private final DrawableActor footprints;
	private final DrawableActor fade;
	private final Platforms platforms;
	private final Climber climber;
	private final Slope slope;
	private final Caves caves;
	private final Ropes ropes;

	private final boolean[] next_ropes;
	private boolean in_progress;
	private boolean played;
	private boolean failed;
	private boolean win;
	private int climb;
	private int index;
	private int fade_state;
	private float position;
	private float arrows_time;
	private RockClimberListener listener;

	private RockClimberGroup(TextureAtlas atlas, Texture slope, Texture peak) {
		this.listener = null;
		this.next_ropes = new boolean[] { true, true, true, true, true };
		this.arrows_time = 0f;
		this.in_progress = false;
		this.played = false;
		this.failed = false;
		this.win = false;
		this.fade_state = 0;
		this.position = 0f;
		this.climb = 0;
		this.index = 0;
		// Actors
		this.slope = Slope.newInstance(slope, peak);
		super.addActor(this.slope);
		this.platforms = Platforms.newInstance(atlas);
		super.addActor(this.platforms);
		this.caves = Caves.newInstance(atlas);
		super.addActor(this.caves);
		this.ropes = Ropes.newInstance(atlas);
		super.addActor(this.ropes);
		this.footprints = DrawableActor.newInstance(atlas.findRegion("footprints"));
		super.addActor(this.footprints);
		this.climber = Climber.newInstance(atlas);
		super.addActor(this.climber);
		TextureRegion arrow = atlas.findRegion("arrow");
		this.arrows = new DrawableActor[] {
			DrawableActor.newInstance(arrow),
			DrawableActor.newInstance(arrow),
			DrawableActor.newInstance(arrow),
			DrawableActor.newInstance(arrow),
			DrawableActor.newInstance(arrow)
		};
		for (int i = 0; i < this.arrows.length; i++) {
			this.arrows[i].addListener(new IntClickListener(i) {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					ropeClicked(super.value);
				}
			});
			super.addActor(this.arrows[i]);
		}
		this.award = RockClimberAward.newInstance(atlas);
		super.addActor(this.award);
		this.fade = DrawableActor.newInstance(atlas.findRegion("black_fade"));
		super.addActor(this.fade);
		// Listeners
		this.ropes.setListener(new RopeClickListener() {
			@Override
			public void clicked(int index) {
				ropeClicked(index);
			}
		});
		this.caves.setListener(new BeastHitListener() {
			@Override
			public void hit() {
				// TODO sound
				climber.playFall();
				climb = -1;
			}
		});
	}

	public static RockClimberGroup newInstance(TextureAtlas atlas, Texture slope, Texture peak) {
		RockClimberGroup instance = new RockClimberGroup(atlas, slope, peak);
		instance.setBounds(0f, 0f, AppConfig.VIEWPORT_WIDTH, AppConfig.VIEWPORT_HEIGHT - GameHeader.HEADER_HEIGHT);
		instance.footprints.setBounds(530f, 40f, 190f, 250f);
		instance.footprints.setVisible(false);
		instance.fade.setBounds(0f, 0f, AppConfig.VIEWPORT_WIDTH, AppConfig.VIEWPORT_HEIGHT - GameHeader.HEADER_HEIGHT);
		instance.fade.setVisible(false);
		instance.platforms.createNext();
		instance.ropes.createNext(new boolean[] { true, true, true, true, true });
		instance.award.setBounds(200f, 15f, 624f, 90f);
		instance.climber.setPosition(CLIMBER_X[5], CLIMBER_Y);
		instance.changePosition(POSITION_OFFSET);
		for (int i = 0; i < 5; i++)
			instance.arrows[i].setBounds(180f + (float)i * 170f, 340f, 50f, 60f);
		return instance;
	}

	@Override
	public void act(float delta) {
		if (!this.in_progress) {
			this.arrows_time += delta;
			if (this.arrows_time >= 1f) {
				this.arrows_time = 0f;
				for (int i = 0; i < this.arrows.length; i++) {
					if (!this.next_ropes[i]) continue;
					this.arrows[i].setVisible(!this.arrows[i].isVisible());
				}
			}
		}
		if (this.fade_state > 0) {
			this.fade.alpha += delta * .5f;
			if (this.fade.alpha >= 1f) {
				this.fade.alpha = 1f;
				this.fade_state = -1;
				this.position -= 100f;
				this.changePosition(-100f);
				this.footprints.setVisible(true);
				this.climber.setBounds(505f, 280f, 70f, 100f);
				this.climber.playFlag();
				this.climber.pause();
			}
		} else if (this.fade_state < 0) {
			this.fade.alpha -= delta * .8f;
			if (this.fade.alpha <= 0f) {
				this.fade.alpha = 0f;
				this.fade_state = 0;
				this.climber.play();
				if (this.listener != null) {
					this.listener.win();
				}
			}
		}
		if (this.climb > 0) {
			float value = -CLIMB_SPEED * delta;
			float target = -2f * SECTION_HEIGHT;
			float remain = target - this.position;
			if (remain > -580f && this.failed) {
				if (!this.played) {
					this.caves.playAction(this.index);
					this.played = true;
				}
			} else if (remain > -100f) {
				value /= 2f;
				if (!this.played) {
					this.climber.playSuccess();
					this.played = true;
				}
			}
			if (remain > value) {
				this.changePosition(remain);
				this.position = 0f;
				this.in_progress = false;
				this.climb = 0;
				// TODO sound
				if (this.listener != null) {
					this.listener.success();
				}
				if (this.win) {
					this.fade_state = 1;
					this.fade.alpha = 0f;
					this.fade.setVisible(true);
				}
			} else {
				this.changePosition(value);
				this.position += value;
			}
		} else if (this.climb < 0) {
			float value = FALL_SPEED * delta;
			float target = 0f;
			float remain = target - this.position;
			if (remain < value) {
				this.changePosition(remain);
				this.position = target;
				this.climb = 0;
				this.climber.playDrop();
				if (this.listener != null) {
					this.listener.fail();
				}
			} else {
				this.changePosition(value);
				this.position += value;
			}
		}
		super.act(delta);
	}

	@Override
	public void resize(int width, int height) {
		this.award.resize(width, height);
	}

	public void startClimb(int[] ropes, boolean win) {
		this.ropes.setState(ropes);
		this.caves.createNext(new boolean[] {
			ropes[0] == 1,
			ropes[1] == 1,
			ropes[2] == 1,
			ropes[3] == 1,
			ropes[4] == 1
		});
		if (!win) {
			this.platforms.createNext();
			for (int i = 0; i < 5; i++)
				this.next_ropes[i] = this.index != i && ropes[i] != 0;
			this.ropes.createNext(this.next_ropes);
		} else {
			for (int i = 0; i < 5; this.next_ropes[i++] = false);
			this.slope.showPeak();
		}
		this.failed = ropes[this.index] == 1;
		this.climber.playClimb();
		this.played = false;
		this.win = win;
		this.climb = 1;
	}

	public void setAward(int award) {
		this.award.setAward(award);
	}

	private void changePosition(float delta) {
		this.platforms.changePosition(delta);
		this.slope.changePosition(delta);
		this.caves.changePosition(delta);
		this.ropes.changePosition(delta);
	}

	private void ropeClicked(int index) {
		if (this.in_progress) return;
		this.climber.setX(CLIMBER_X[index]);
		RockClimberGroup.this.index = index;
		for (int i = 0; i < this.arrows.length; this.arrows[i++].setVisible(false));
		this.arrows_time = 1f;
		// TODO sound
		if (this.listener != null) {
			this.listener.clicked(index);
			this.in_progress = true;
		}
	}

	public void setListener(RockClimberListener listener) {
		this.listener = listener;
	}

	public interface RockClimberListener {
		public void clicked(int index);
		public void success();
		public void fail();
		public void win();
	}
	private interface BeastHitListener {
		public void hit();
	}
	private interface RopeClickListener {
		public void clicked(int index);
	}

	private static class Ropes extends Group {
		private static final float OFFSET_Y = 250f;
		private static final float[] OFFSET_X = { 170f, 340f, 510f, 680f, 850f };
		private final Rope rope;
		private final List<Rope[]> list;
		private float position;
		private RopeClickListener listener;
		private Ropes(TextureAtlas atlas) {
			this.rope = Rope.newInstance(atlas);
			this.list = new LinkedList<Rope[]>();
			this.position = 0f;
		}
		public static Ropes newInstance(TextureAtlas atlas) {
			Ropes instance = new Ropes(atlas);
			instance.setBounds(0f, 0f, AppConfig.VIEWPORT_WIDTH, AppConfig.VIEWPORT_HEIGHT - GameHeader.HEADER_HEIGHT);
			return instance;
		}
		public void createNext(boolean[] active) {
			Rope[] ropes = {
				Rope.newInstance(this.rope),
				Rope.newInstance(this.rope),
				Rope.newInstance(this.rope),
				Rope.newInstance(this.rope),
				Rope.newInstance(this.rope)
			};
			for (int i = 0; i < ropes.length && i < active.length; i++) {
				ropes[i].setVisible(active[i]);
				ropes[i].addListener(new IntClickListener(i) {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						if (listener != null) {
							listener.clicked(super.value);
						}
					}
				});
				super.addActor(ropes[i]);
			}
			this.list.add(ropes);
		}
		public void setState(int[] state) {
			if (this.list.size() < 1)
				this.createNext(new boolean[] { true, true, true, true, true });
			Rope[] ropes = this.list.get(this.list.size() - 1);
			for (int i = 0; i < ropes.length && i < state.length; i++) {
				switch (state[i]) {
					case 0:
						ropes[i].setVisible(false);
						break;
					case 1:
						ropes[i].setVisible(true);
						ropes[i].setToCave();
						break;
					case 2:
						ropes[i].setVisible(true);
						ropes[i].setToPlatform();
						break;
				}
			}
		}
		public void changePosition(float delta) {
			this.position += delta;
			if (this.position > 0f)
				this.position = 0f;
			this.update();
		}
		public void update() {
			int count = this.list.size();
			for (int i = 0; i < count; i++) {
				Rope[] ropes = this.list.get(i);
				for (int j = 0; j < ropes.length; j++) {
					ropes[j].setPosition(OFFSET_X[j], this.position + (float)i * (float)SECTIONS_COUNT * SECTION_HEIGHT + OFFSET_Y);
				}
			}
		}
		public void setListener(RopeClickListener listener) {
			this.listener = listener;
		}
	}

	private static class Caves extends Group {
		private static final float WIDTH = 110f;
		private static final float HEIGHT = 140f;
		private static final float OFFSET_Y = 1050f;
		private static final float[] OFFSET_X = { 110f, 280f, 450f, 620f, 790f };
		private final Cave cave;
		private final List<Cave[]> list;
		private final BeastHitListener handler;
		private BeastHitListener listener;
		private float position;
		private Caves(TextureAtlas atlas) {
			this.cave = Cave.newInstance(atlas);
			this.list = new LinkedList<Cave[]>();
			this.position = 0f;
			this.handler = new BeastHitListener() {
				@Override
				public void hit() {
					if (Caves.this.listener != null) {
						Caves.this.listener.hit();
					}
				}
			};
			this.listener = null;
		}
		public static Caves newInstance(TextureAtlas atlas) {
			Caves instance = new Caves(atlas);
			instance.setBounds(0f, 0f, AppConfig.VIEWPORT_WIDTH, AppConfig.VIEWPORT_HEIGHT - GameHeader.HEADER_HEIGHT);
			return instance;
		}
		public void createNext(boolean[] active) {
			Cave[] caves = {
				Cave.newInstance(this.cave.getTextures()),
				Cave.newInstance(this.cave.getTextures()),
				Cave.newInstance(this.cave.getTextures()),
				Cave.newInstance(this.cave.getTextures()),
				Cave.newInstance(this.cave.getTextures())
			};
			for (int i = 0; i < caves.length && i < active.length; i++) {
				caves[i].setActive(active[i]);
				caves[i].setListener(this.handler);
				super.addActor(caves[i]);
			}
			this.list.add(caves);
		}
		public void changePosition(float delta) {
			this.position += delta;
			if (this.position > 0f)
				this.position = 0f;
			this.update();
		}
		public void update() {
			int count = this.list.size();
			for (int i = 0; i < count; i++) {
				Cave[] caves = this.list.get(i);
				for (int j = 0; j < caves.length; j++) {
					caves[j].setBounds(OFFSET_X[j], this.position + (float)(i * SECTIONS_COUNT) * SECTION_HEIGHT + OFFSET_Y, WIDTH, HEIGHT);
				}
			}
		}
		public void playAction(int index) {
			this.list.get(this.list.size() - 1)[index].playAction();
		}
		public void setListener(BeastHitListener listener) {
			this.listener = listener;
		}
	}

	private static class Platforms extends Group {
		private static final float WIDTH = 960f;
		private static final float HEIGHT = 250f;
		private static final float OFFSET_X = 30f;
		private static final float OFFSET_Y = 40f;
		private final TextureRegion texture;
		private final List<DrawableActor> list;
		private float position;
		private Platforms(TextureAtlas atlas) {
			this.texture = atlas.findRegion("platform");
			this.list = new LinkedList<DrawableActor>();
			this.position = 0f;
		}
		public static Platforms newInstance(TextureAtlas atlas) {
			Platforms instance = new Platforms(atlas);
			return instance;
		}
		public void changePosition(float delta) {
			this.position += delta;
			if (this.position > 0f)
				this.position = 0f;
			this.update();
		}
		public void createNext() {
			DrawableActor actor = DrawableActor.newInstance(this.texture);
			super.addActor(actor);
			this.list.add(actor);
			this.update();
		}
		private void update() {
			int count = this.list.size();
			for (int i = 0; i < count; i++) {
				this.list.get(i).setBounds(OFFSET_X, this.position + OFFSET_Y + (float)i * (float)SECTIONS_COUNT * SECTION_HEIGHT, WIDTH, HEIGHT);
			}
		}
	}

	private static class Rope extends Group {
		private static final float WIDTH = 40f;
		private static final float HEIGHT_NONE = 600f;
		private static final float HEIGHT_CAVE = 850f;
		private static final float HEIGHT_PLATFORM = 1130f;
		private static final float ASPECT_TOP = 1.11f;
		private static final float ASPECT_MIDDLE = .2f;
		private static final float ASPECT_BOTTOM = .53f;
		private final TextureRegion top;
		private final TextureRegion middle;
		private final TextureRegion bottom;
		private Rope(Rope rope) {
			this.top = rope.top;
			this.middle = rope.middle;
			this.bottom = rope.bottom;
		}
		private Rope(TextureAtlas atlas) {
			this.top = atlas.findRegion("rope_top");
			this.middle = atlas.findRegion("rope_middle");
			this.bottom = atlas.findRegion("rope_bottom");
		}
		public static Rope newInstance(Rope rope) {
			Rope instance = new Rope(rope);
			instance.setBounds(0f, 0f, WIDTH, HEIGHT_NONE);
			return instance;
		}
		public static Rope newInstance(TextureAtlas atlas) {
			Rope instance = new Rope(atlas);
			instance.setBounds(0f, 0f, WIDTH, HEIGHT_NONE);
			return instance;
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
		public void setToCave() {
			super.setHeight(HEIGHT_CAVE);
			this.update(super.getWidth(), HEIGHT_CAVE);
		}
		public void setToPlatform() {
			super.setHeight(HEIGHT_PLATFORM);
			this.update(super.getWidth(), HEIGHT_PLATFORM);
		}
		private void update(float width, float height) {
			super.clearChildren();
			DrawableActor actor;
			// Get bounds
			float h_top = width * ASPECT_TOP;
			float h_middle = width * ASPECT_MIDDLE;
			float h_bottom = width * ASPECT_BOTTOM;
			float mid = height - h_top - h_bottom;
			int count = (int)Math.ceil(mid / h_middle);
			{ // Bottom
				actor = DrawableActor.newInstance(this.bottom);
				actor.setBounds(0f, 0f, width, h_bottom);
				super.addActor(actor);
			}
			// Middle
			for (int i = 0; i < count; i++) {
				actor = DrawableActor.newInstance(this.middle);
				actor.setBounds(0f, (float)i * h_middle + h_bottom, width, h_middle);
				super.addActor(actor);
			}
			{ // Top
				actor = DrawableActor.newInstance(this.top);
				actor.setBounds(0f, (float)count * h_middle + h_bottom, width, h_top);
				super.addActor(actor);
			}
		}
	}

	private static class Slope extends Group {
		private static final float HEIGHT = AppConfig.VIEWPORT_HEIGHT - GameHeader.HEADER_HEIGHT;
		private final Texture peak;
		private final Texture texture;
		private final List<DrawableActor> list;
		private boolean peak_showed;
		private boolean show_peak;
		private float position;
		private Slope(Texture texture, Texture peak) {
			this.peak = peak;
			this.texture = texture;
			this.list = new LinkedList<DrawableActor>();
			this.peak_showed = false;
			this.show_peak = false;
			this.position = 0f;
		}
		public static Slope newInstance(Texture texture, Texture peak) {
			Slope instance = new Slope(texture, peak);
			instance.setBounds(0f, 0f, AppConfig.VIEWPORT_WIDTH, HEIGHT);
			return instance;
		}
		@Override
		public void act(float delta) {
			int total = (int)(HEIGHT - this.position);
			int int_height = (int)SECTION_HEIGHT;
			if (this.peak_showed) total -= (865 - int_height);
			int count = total / int_height;
			if (total % int_height != 0) count++;
			while (count > this.list.size()) {
				DrawableActor actor = null;
				if (this.show_peak && !this.peak_showed && this.list.size() % SECTIONS_COUNT == 0) {
					actor = DrawableActor.newInstance(this.peak);
					actor.setBounds(0f, this.getNextY(), 1024f, 865f);
					this.peak_showed = true;
				} else {
					actor = DrawableActor.newInstance(this.texture);
					actor.setBounds(0f, this.getNextY(), 1024f, SECTION_HEIGHT);
				}
				super.addActor(actor);
				this.list.add(actor);
			}
		}
		public void showPeak() {
			this.show_peak = true;
		}
		public void changePosition(float delta) {
			this.position += delta;
			if (this.position > 0f)
				this.position = 0f;
			this.update();
		}
		private float getNextY() {
			if (this.list.size() > 0) {
				return this.list.get(this.list.size() - 1).getY() + SECTION_HEIGHT;
			} else return this.position;
		}
		private void update() {
			int count = this.list.size();
			for (int i = 0; i < count; i++) {
				this.list.get(i).setY(this.position + i * SECTION_HEIGHT);
			}
		}
	}

	private static class Climber extends Actor {
		private static int PAUSE = 15;
		private static float[][] BOUNDS = {
			{ .0f, .0f, 1.f, 1.f, .2f },			// bag
			{ .07f, .083f, .84f, 1.03f, .12f },		// climb
			{ -.29f, .037f, 1.5f, .96f, .05f },		// drop
			{ .013f, .073f, .923f, .93f, .2f },		// fall
			{ -.48f, .055f, 1.25f, 1.09f, .2f },	// flag
			{ -.256f, .073f, 1.346f, .89f, .1f },	// hit
			{ .0f, .0f, 1.f, 1.014f, .2f },			// look
			{ -.48f, .055f, 1.365f, 1.38f, .2f },	// put_in
			{ -.12f, .0f, 1.314f, 1.005f, .2f },	// stand
			{ .0f, .0f, 1.1f, 1.032f, .1f },		// success
			{ .0f, .0f, 1.f, 1.f, .2f },			// bag_down
			{ -.29f, .037f, 1.5f, .96f, .2f }		// stuck
		};
		private int index;
		private int frame;
		private int pause;
		private float time;
		private boolean paused;
		private final TextureRegion[][] textures;
		private Climber(TextureAtlas atlas) {
			this.textures = new TextureRegion[][] {
				Machine.loadTextures(atlas, "bag", 12, 0),
				Machine.loadTextures(atlas, "climb", 10, 0),
				Machine.loadTextures(atlas, "drop", 9, 0),
				Machine.loadTextures(atlas, "fall", 4, 0),
				Machine.loadTextures(atlas, "flag", 10, 0),
				Machine.loadTextures(atlas, "hit", 9, 0),
				Machine.loadTextures(atlas, "look", 10, 0),
				Machine.loadTextures(atlas, "put_in", 16, 0),
				Machine.loadTextures(atlas, "stand", 9, 0),
				Machine.loadTextures(atlas, "success", 18, 0),
				Machine.loadTextures(atlas, "bag_down", 8, 0),
				Machine.loadTextures(atlas, "stuck", 4, 0)
			};
			this.index = 0;
			this.frame = 0;
			this.pause = PAUSE;
			this.paused = false;
			this.time = 0f;
		}
		public static Climber newInstance(TextureAtlas atlas) {
			Climber instance = new Climber(atlas);
			instance.setSize(100f, 140f);
			return instance;
		}
		@Override
		public void act(float delta) {
			if (this.paused) return;
			this.time += delta;
			while (this.time >= BOUNDS[this.index][4]) {
				this.time -= BOUNDS[this.index][4];
				this.frame++;
				if (this.frame >= this.textures[this.index].length) {
					this.frame = 0;
					if (this.index == 0) {
						this.index = 6;
					} else if (this.index == 6) {
						this.index = 8;
					} else if (this.index == 8) {
						this.index = 10;
					} else if (this.index == 10) {
						this.pause--;
						if (this.pause < 0) {
							this.index = 0;
							this.pause = PAUSE;
						} else this.frame = this.textures[this.index].length - 1;
					} else if (this.index == 2) {
						this.index = 11;
					} else if (this.index == 9) {
						this.index = 0;
					} else if (this.index == 5) {
						this.index = 3;
					} else if (this.index == 7) {
						this.index = 4;
					}
				}
			}
		}
		@Override
		public void draw(Batch batch, float parentAlpha) {
			float x = super.getX();
			float y = super.getY();
			float w = super.getWidth();
			float h = super.getHeight();
			batch.draw(
				this.textures[this.index][this.frame],
				BOUNDS[this.index][0] * w + x,
				BOUNDS[this.index][1] * h + y,
				BOUNDS[this.index][2] * w,
				BOUNDS[this.index][3] * h
			);
		}
		public void playFall() {
			this.frame = 0;
			this.index = 5;
			this.time = 0f;
		}
		public void playClimb() {
			this.frame = 0;
			this.index = 1;
			this.time = 0f;
		}
		public void playSuccess() {
			this.frame = 0;
			this.index = 9;
			this.time = 0f;
		}
		public void playDrop() {
			this.frame = 0;
			this.index = 2;
			this.time = 0f;
		}
		public void playFlag() {
			this.frame = 0;
			this.index = 7;
			this.time = 0f;
		}
		public void pause() {
			this.paused = true;
		}
		public void play() {
			this.paused = false;
		}
	}

	private static class Cave extends Actor {
		private static final float[][] BOUNDS = {
			{ .378f, .492f, .382f, .102f, .3f },
			{ .0f, .0f, 1.f, 1.f, .14f },
			{ -.2f, -0.332f, 1.516f, 1.589f, 1f }
		};
		private int index;
		private int frame;
		private float time;
		private boolean active;
		private BeastHitListener listener;
		private final TextureRegion[][] textures;
		private Cave(TextureAtlas atlas) {
			this.textures = new TextureRegion[][] {
				Machine.loadTextures(atlas, "beast_look", 8, 0),
				Machine.loadTextures(atlas, "beast_beat", 20, 0),
				Machine.loadTextures(atlas, "cave", 1, 0)
			};
			this.listener = null;
			this.active = false;
			this.index = 0;
			this.frame = 0;
			this.time = 0f;
		}
		private Cave(TextureRegion[][] textures) {
			this.textures = textures;
			this.listener = null;
			this.active = false;
			this.index = 0;
			this.frame = 0;
			this.time = 0f;
		}
		public static Cave newInstance(TextureAtlas atlas) {
			Cave instance = new Cave(atlas);
			return instance;
		}
		public static Cave newInstance(TextureRegion[][] textures) {
			Cave instance = new Cave(textures);
			return instance;
		}
		@Override
		public void act(float delta) {
			if (!this.active) return;
			this.time += delta;
			while (this.time >= BOUNDS[this.index][4]) {
				this.time -= BOUNDS[this.index][4];
				this.frame++;
				if (this.frame >= this.textures[this.index].length) {
					if (this.index > 0) this.index = 0;
					this.frame = 0;
				} else if (this.frame == 10) {
					if (this.listener != null) {
						this.listener.hit();
					}
				}
			}
		}
		@Override
		public void draw(Batch batch, float parentAlpha) {
			if (!this.active) return;
			float x = super.getX();
			float y = super.getY();
			float w = super.getWidth();
			float h = super.getHeight();
			batch.draw(
				this.textures[2][0],
				BOUNDS[2][0] * w + x,
				BOUNDS[2][1] * h + y,
				BOUNDS[2][2] * w,
				BOUNDS[2][3] * h
			);
			batch.draw(
				this.textures[this.index][this.frame],
				BOUNDS[this.index][0] * w + x,
				BOUNDS[this.index][1] * h + y,
				BOUNDS[this.index][2] * w,
				BOUNDS[this.index][3] * h
			);
		}
		public TextureRegion[][] getTextures() {
			return this.textures;
		}
		public void setActive(boolean active) {
			this.active = active;
		}
		public void setListener(BeastHitListener listener) {
			this.listener = listener;
		}
		public void playAction() {
			this.frame = 0;
			this.index = 1;
			this.time = 0f;
		}
	}

	public static class RockClimberAward extends Group implements Resizable {
		private final TextureRegionDrawable background;
		private final PixelLabel label_title;
		private final PixelLabel label_award;
		private RockClimberAward(TextureAtlas atlas) {
			this.background = new TextureRegionDrawable(atlas.findRegion("award_frame"));
			LabelStyle ls = new LabelStyle();
			ls.font = FontsFactory.getAsync("courbd.ttf", 34);
			ls.fontColor = new Color(1f, 1f, 0f, 1f);
			this.label_title = new PixelLabel(Connection.getString("common_award"), ls);
			this.label_title.setAlignment(Align.center, Align.center);
			ls = new LabelStyle();
			ls.font = FontsFactory.getAsync("arial.ttf", 42);
			ls.fontColor = new Color(1f, 1f, 0f, 1f);
			this.label_award = new PixelLabel("0", ls);
			this.label_award.setAlignment(Align.center, Align.center);
			super.addActor(this.label_title);
			super.addActor(this.label_award);
		}
		public static RockClimberAward newInstance(TextureAtlas atlas) {
			RockClimberAward instance = new RockClimberAward(atlas);
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
			this.label_title.setBounds(.094f * width, 0f, .4f * width, height);
			this.label_award.setBounds(.5f * width, 0f, .36f * width, height);
		}
		@Override
		public void resize(int width, int height) {
			this.label_title.resize(width, height);
			this.label_award.resize(width, height);
		}
	}

}
