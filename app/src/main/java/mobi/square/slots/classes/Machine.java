package mobi.square.slots.classes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import mobi.square.slots.api.Connection;
import mobi.square.slots.config.SlotsConfig;
import mobi.square.slots.config.SoundList;
import mobi.square.slots.containers.LinesExt.LineExt;
import mobi.square.slots.enums.SlotsType;
import mobi.square.slots.enums.SymbolType;
import mobi.square.slots.error.StringCodeException;
import mobi.square.slots.listeners.LineShowedListener;
import mobi.square.slots.listeners.LinesShowedListener;
import mobi.square.slots.listeners.MachineStoppedListener;
import mobi.square.slots.listeners.ReelStoppedListener;
import mobi.square.slots.logger.Log;
import mobi.square.slots.stages.Basic;
import mobi.square.slots.ui.Resizable;
import mobi.square.slots.utils.json.JsonArray;
import mobi.square.slots.utils.utils;

public class Machine extends Group implements Resizable {

    public final int REELS_COUNT;
    protected final ShapeRenderer renderer;
    protected final Basic parent;
    protected final Effects effects;
    protected final Rectangle scissors;
    protected final Reel[] reels;
    protected final Set<ReelStoppedListener> reel_stopped_listeners;
    protected final Set<MachineStoppedListener> machine_stopped_listeners;
    protected final Set<LinesShowedListener> lines_showed_listeners;
    protected final Set<LineShowedListener> line_showed_listeners;
    public SlotsConfig config;
    public TextureAtlas atlas;
    public boolean started;
    protected Lines lines;
    protected LinesImg img_lines;
    protected SymbolType[][] target;
    protected Texture background;
    protected TextureRegion foreground;
    protected float lines_width_pixels;
    protected float lines_width_units;
    protected Sound sound_reel_stop;
    protected Sound sound_machine_start;
    protected Sound sound_start_spin;
    protected AnimationInfo[] animation;

    protected Machine(Basic parent, SlotsType type) {
        this.parent = parent;
        this.config = null;
        this.atlas = null;
        this.started = false;
        this.background = null;
        this.foreground = null;
        this.sound_reel_stop = null;
        this.sound_machine_start = null;
        this.REELS_COUNT = 5;
        this.lines_width_pixels = 1.f;
        this.lines_width_units = 1.f;
        this.effects = new Effects(this);
        this.scissors = new Rectangle();
        this.lines = null;
        this.img_lines = null;
        this.animation = null;
        this.reels = new Reel[REELS_COUNT];
        this.renderer = new ShapeRenderer();
        this.reel_stopped_listeners = new HashSet<ReelStoppedListener>();
        this.machine_stopped_listeners = new HashSet<MachineStoppedListener>();
        this.lines_showed_listeners = new HashSet<LinesShowedListener>();
        this.line_showed_listeners = new HashSet<LineShowedListener>();
        for (int i = 0; i < REELS_COUNT; ++i) {
            this.reels[i] = new Reel(this, i);
            super.addActor(this.reels[i]);
        }
        this.initialize(type);
    }

    public static Machine newInstance(Basic parent, SlotsType type) {
        Machine instance = new Machine(parent, type);
        return instance;
    }

    /**
     * Загружает набор регионов из указанного атласа.
     *
     * @param atlas - атлас с текстурами;
     * @param name  - имя региона;
     * @param count - количество регионов;
     * @param start - начальный индекс.
     * @return {@link TextureRegion}[]
     */
    public static TextureRegion[] loadTextures(TextureAtlas atlas, String name, int count, int start) {
        if (atlas == null || name == null || name.isEmpty() || count < 1)
            return new TextureRegion[0];
        if (count > 1) {
            TextureRegion[] textures = new TextureRegion[count];
            for (int i = 0; i < count; i++, start++) {
                textures[i] = atlas.findRegion(name, start);
            }
            return textures;
        }
        return new TextureRegion[]{atlas.findRegion(name)};
    }

    public void initialize(SlotsType type) {
        this.config = SlotsConfig.get(type);
        if (this.config.lines_type == mobi.square.slots.enums.Lines.LINES_3X_09_V1 ||
                this.config.lines_type == mobi.square.slots.enums.Lines.LINES_3X_20_V1) {
            this.img_lines = new LinesImg(
                    this, this.config.lines_type,
                    Connection.getManager().get(this.config.getLinesAtlas(), TextureAtlas.class)
            );
            this.lines = null;
        } else {
            this.lines = new Lines(this);
            this.img_lines = null;
        }
        this.sound_reel_stop = this.getReelStopSound();
        this.sound_machine_start = this.getMachineStartSound();
        this.sound_start_spin = this.getStartSpinSound();
        this.atlas = Connection.getManager().get(this.config.filename, TextureAtlas.class);
        this.background = Connection.getManager().get(this.config.machine_bg, Texture.class);
        this.foreground = this.atlas.findRegion("foreground");
        for (int i = 0; i < REELS_COUNT; ++i) {
            this.reels[i].initialize(i);
        }
    }

    public void start() {
        if (this.started) return;
        this.stopSymbolsAnimation();
        this.started = true;
        this.target = null;
        if (this.lines != null)
            this.lines.clear();
        if (this.img_lines != null)
            this.img_lines.clear();
        for (int i = 0; i < REELS_COUNT; ++i) {
            this.reels[i].start();
        }
        if (Connection.getInstance().isSoundOn()) {
            if (this.sound_machine_start != null) {
                this.sound_machine_start.play();
            }
            if (this.sound_start_spin != null) {
                this.sound_start_spin.play();
            }
        }
    }

    public void stop() {
        if (!this.started) return;
        int count = this.config.symbols_count;
        SymbolType[][] symbols = new SymbolType[REELS_COUNT][];
        for (int i = 0; i < REELS_COUNT; ++i) {
            symbols[i] = new SymbolType[count];
            for (int j = 0; j < count; ++j) {
                symbols[i][j] = this.getRandomSymbol();
            }
        }
        this.stop(symbols, null, null);
    }

    public void stopError() {
        if (!this.started) return;
        int count = this.config.symbols_count;
        SymbolType[][] symbols = new SymbolType[REELS_COUNT][];
        for (int i = 0; i < REELS_COUNT; ++i) {
            symbols[i] = new SymbolType[count];
            for (int j = 0; j < count; ++j) {
                symbols[i][j] = SymbolType.N01;
            }
        }
        this.stop(symbols, null, null);
    }

    public void stopNow() {
        if (!this.started) return;
        if (this.target == null) {
            int count = this.config.symbols_count;
            SymbolType[] symbols = new SymbolType[count];
            for (int j = 0; j < count; ++j) {
                symbols[j] = SymbolType.N01;
            }
            for (int i = 0; i < REELS_COUNT; ++i) {
                this.reels[i].stopNow(symbols);
            }
        } else {
            for (int i = 0; i < REELS_COUNT; ++i) {
                this.reels[i].stopNow(this.target[i]);
            }
        }
        this.started = false;
        this.machineStopped();
    }

    public void stop(SymbolType[][] symbols, List<LineExt> lines, List<boolean[]> specials) {
        if (!this.started) return;
        if (symbols == null) return;
        if (specials == null)
            specials = new LinkedList<boolean[]>();
        if (this.lines != null) {
            this.lines.clear();
            this.lines.addLines(lines);
        }
        if (this.img_lines != null) {
            this.img_lines.clear();
            this.img_lines.addLines(lines);
        }
        this.target = symbols;
        this.reels[0].stop(symbols[0]);
        // Debug
        /*for (int j = 0; j < this.config.symbols_count; j++) {
			for (int i = 0; i < REELS_COUNT; i++) {
				System.out.print(symbols[i][j].toString() + " ");
			}
			System.out.println("");
		}*/
    }

    Reel getReel(int index) {
        if (index < 0 || index >= this.reels.length) return null;
        return this.reels[index];
    }

    protected SymbolType[] getHighlightedSymbols() {
        return null;
    }

    public Sound[] getLinesSound() {
        Sound[] array = new Sound[9];
        for (int i = 0; i < 9; i++) {
            array[i] = Connection.getManager().get(SoundList.LINE_WINS_9X[i], Sound.class);
        }
        return array;
    }

    protected Sound getReelStopSound() {
        return Connection.getManager().get(SoundList.REEL_STOP, Sound.class);
    }

    protected Sound getMachineStartSound() {
        return Connection.getManager().get(SoundList.MACHINE_START, Sound.class);
    }

    protected Sound getStartSpinSound() {
        return Connection.getManager().get(SoundList.START_SPIN, Sound.class);
    }

    private void playSymbolsAnimation() {
        for (int i = 0; i < this.reels.length; i++) {
            if (this.img_lines != null) {
                this.reels[i].playAnimation(this.img_lines.getHighlights(i, this.getHighlightedSymbols()));
            } else if (this.lines != null) {
                this.reels[i].playAnimation(this.lines.getHighlights(i, this.getHighlightedSymbols()));
            }
        }
    }

    private void stopSymbolsAnimation() {
        for (Reel reel : this.reels) {
            reel.stopAnimation();
        }
    }

    public void setLinesTime(float time) {
        if (this.lines != null) {
            this.lines.setCustomTime(time);
        }
    }

    public void setActiveLines(int lines) {
        if (this.img_lines != null) {
            this.img_lines.setActiveLines(lines);
        }
    }

    public boolean isStarted() {
        return this.started;
    }

    public void clearLines() {
        if (this.lines != null) {
            this.lines.clear();
        }
        if (this.img_lines != null) {
            this.img_lines.clear();
        }
    }

    public void addLine(int[] positions, int award, int count, boolean left) {
        if (this.lines != null) {
            this.lines.addLine(positions, 0, left, false, award);
        }
        if (this.img_lines != null) {
            this.img_lines.addLine(positions, award, false, count, left);
        }
    }

    public void addLines(List<LineExt> lines) {
        if (this.lines != null) {
            this.lines.addLines(lines);
        }
        if (this.img_lines != null) {
            this.img_lines.addLines(lines);
        }
    }

    public void showAllLines() {
        if (this.img_lines != null) {
            this.img_lines.showAll();
        }
    }

    public void showLinesOneByOne() {
        if (this.img_lines != null) {
            this.img_lines.oneByOne();
        }
    }

    public int getAwardLinesCount() {
        if (this.lines != null) {
            return this.lines.getCount();
        }
        if (this.img_lines != null) {
            return this.img_lines.getCount();
        }
        return 0;
    }

    public boolean isAwardLinesShowed() {
        if (this.lines != null) {
            return this.lines.isShowed();
        }
        if (this.img_lines != null) {
            return this.img_lines.isShowed();
        }
        return true;
    }

    public void addEffects(JsonArray effects) {
        if (effects == null) return;
        try {
            int length = effects.length();
            for (int i = 0; i < length; i++) {
                this.effects.add(effects.getJsonObject(i));
            }
        } catch (StringCodeException e) {
            Log.log(e);
        }
    }

    public void clearEffects() {
        this.effects.clear();
    }

    public void setSymbols(SymbolType[][] symbols) {
        for (int i = 0; i < REELS_COUNT; i++) {
            if (i >= symbols.length) break;
            this.reels[i].stopNow(symbols[i]);
        }
    }

    public void setSymbolsBookOfRa(SymbolType[][] symbols, SymbolType bonus_symbol) {
        for (int i = 0; i < REELS_COUNT; i++) {
            if (i >= symbols.length) break;
            this.reels[i].stopNowBookOfRa(symbols[i], bonus_symbol);
        }
    }

    public SymbolType getRandomSymbol() {
        int rand = utils.getRandom(this.config.symbols.length);
        return this.config.symbols[rand];
    }

    public void addListener(LineShowedListener listener) {
        this.line_showed_listeners.add(listener);
    }

    public void addListener(LinesShowedListener listener) {
        this.lines_showed_listeners.add(listener);
    }

    public void addListener(ReelStoppedListener listener) {
        this.reel_stopped_listeners.add(listener);
    }

    public void addListener(MachineStoppedListener listener) {
        this.machine_stopped_listeners.add(listener);
    }

    public void clearListeners() {
        this.reel_stopped_listeners.clear();
        this.machine_stopped_listeners.clear();
        this.lines_showed_listeners.clear();
    }

    protected void stopNext(Reel reel) {
        if (reel.index < REELS_COUNT - 1) {
            int index = reel.index + 1;
            this.reels[index].stop(this.target[index]);
        }/* else {
			this.started = false;
			machineStopped();
		}*/
    }

    protected void reelStopped(Reel reel) {
        if (reel.index >= REELS_COUNT - 1) {
            this.started = false;
            machineStopped();
        }
        if (this.sound_reel_stop != null && Connection.getInstance().isSoundOn())
            this.sound_reel_stop.play();
        for (ReelStoppedListener listener : this.reel_stopped_listeners) {
            listener.stopped(reel);
        }
    }

    protected void machineStopped() {
        this.effects.clear();
        for (MachineStoppedListener listener : this.machine_stopped_listeners) {
            listener.stopped(this);
        }
    }

    protected void linesShowed() {
        this.playSymbolsAnimation();
        for (LinesShowedListener listener : this.lines_showed_listeners) {
            listener.lines_showed(this);
        }
    }

    protected void lineShowed(int line, int award) {
        for (LineShowedListener listener : this.line_showed_listeners) {
            listener.line_showed(this, line, award);
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
		/*{
			this.last_reload += delta;
			if (this.last_reload >= .4f) {
				this.last_reload = 0f;
				this.config = SlotsConfig.reload(this.config.type);
			}
		}*/
        this.effects.act(delta);
        if (this.lines != null) {
            this.lines.act(delta);
        }
        if (this.img_lines != null) {
            this.img_lines.act(delta);
        }
    }

    //private float last_reload = 0f;

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float x = super.getX();
        float y = super.getY();
        float width = super.getWidth();
        float height = super.getHeight();
        batch.draw(this.background, x, y, width, height);
        super.draw(batch, parentAlpha);
        this.effects.draw(batch, parentAlpha);
        batch.draw(this.foreground, x, y, width, height);
        batch.flush();
        if (this.img_lines != null)
            this.img_lines.draw(batch);
        this.drawShapes(batch, parentAlpha);
    }

    @Override
    public void resize(int width, int height) {
        float ppu = (float) width / this.parent.getViewport().getWorldWidth();
        float size = super.getWidth() * ppu;
        this.lines_width_pixels = this.config.lines_stroke_width * size;
        if (this.lines_width_pixels > 10) this.lines_width_pixels = 10;
        this.lines_width_units = this.lines_width_pixels / ppu;
        this.parent.calculateScissors(new Rectangle(super.getX(), super.getY(), super.getWidth(), super.getHeight()), this.scissors);
    }

    private void drawShapes(Batch batch, float parentAlpha) {
        if (this.lines != null) {
            if (!this.started) {
                Gdx.gl20.glLineWidth(utils.getRound(this.lines_width_pixels, 0.5d));
                this.renderer.setProjectionMatrix(batch.getProjectionMatrix());
                this.renderer.begin(ShapeType.Line);
                this.lines.draw(this.renderer, this.lines_width_units);
                this.renderer.end();
            }
        }
    }

    public class AnimationInfo {
        public final String name;
        public final SymbolType type;
        public final int frames_count;
        public final float frames_time;
        private TextureRegion[] textures;

        public AnimationInfo(SymbolType type, String name, int frames, float time) {
            this.type = type;
            this.name = name;
            this.frames_count = frames;
            this.frames_time = time;
            this.textures = null;
        }

        public TextureRegion[] getTextures() {
            if (this.textures == null) {
                if (this.frames_count < 1 || this.name == null || this.name.isEmpty() || this.type == null) {
                    this.textures = new TextureRegion[0];
                } else {
                    this.textures = new TextureRegion[this.frames_count];
                    for (int i = 0; i < this.frames_count; i++) {
                        this.textures[i] = atlas.findRegion(this.name, i);
                    }
                }
            }
            return this.textures;
        }
    }

}
