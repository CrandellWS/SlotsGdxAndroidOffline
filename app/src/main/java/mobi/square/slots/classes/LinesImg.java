package mobi.square.slots.classes;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mobi.square.slots.api.Connection;
import mobi.square.slots.config.LinesConfig;
import mobi.square.slots.config.SoundList;
import mobi.square.slots.containers.LinesExt.LineExt;
import mobi.square.slots.enums.Lines;
import mobi.square.slots.enums.SymbolType;

public class LinesImg {

    private static final float LINE_SHOW_TIME = .5f;
    private static final int[] ORDER = {2, 6, 4, 7, 0, 8, 3, 5, 1};

    private final Machine parent;
    private final List<LineImg> lines;
    private final Set<Integer> pay_lines;
    private final Lines type;
    private final TextureRegion[] active;
    private final TextureRegion[] inactive;
    private final Sound[] line_wins_sound;
    private TextureAtlas atlas;
    private int active_lines;
    private boolean showed;
    private boolean one_by_one;
    private boolean flash;
    private boolean disable_flash;
    private int current;
    private float time;

    public LinesImg(Machine parent, Lines type, TextureAtlas atlas) {
        this.parent = parent;
        this.lines = new ArrayList<LineImg>();
        this.pay_lines = new HashSet<Integer>();
        this.atlas = atlas;
        this.type = type;
        this.showed = false;
        if (this.type == Lines.LINES_3X_09_V1) {
            this.active = new TextureRegion[9];
            this.inactive = new TextureRegion[9];
            for (int i = 0; i < 9; i++) {
                this.active[i] = atlas.findRegion("active", i);
                this.inactive[i] = atlas.findRegion("inactive", i);
            }
        } else {
            this.active = new TextureRegion[0];
            this.inactive = new TextureRegion[0];
        }
        this.active_lines = 0;
        this.one_by_one = false;
        this.current = -1;
        this.disable_flash = false;
        this.flash = true;
        this.time = 0f;
        if (parent == null) {
            this.line_wins_sound = new Sound[9];
            for (int i = 0; i < 9; i++) {
                this.line_wins_sound[i] = Connection.getManager().get(SoundList.LINE_WINS_9X[i], Sound.class);
            }
        } else this.line_wins_sound = parent.getLinesSound();
    }

    public void act(float delta) {
        if (this.parent.started) return;
        if (this.one_by_one) {
            this.time += delta;
            if (this.time >= LINE_SHOW_TIME) {
                this.time = 0f;
                if (this.lines.size() < 1) {
                    if (!this.showed) {
                        this.showed = true;
                        this.parent.linesShowed();
                    }
                    return;
                }
                if (this.current < 0 || this.current >= this.lines.size()) {
                    this.current = -1;
                } else this.lines.get(this.current).visible = false;
                if (++this.current >= this.lines.size()) {
                    this.current = 0;
                    LineImg line = this.lines.get(this.current);
                    if (line != null) line.visible = true;
                    if (!this.showed) {
                        this.showed = true;
                        this.parent.linesShowed();
                    }
                } else {
                    LineImg line = this.lines.get(this.current);
                    if (line != null) {
                        line.visible = true;
                        /*if (!this.showed) {
							this.parent.lineShowed(line.award);
						}*/
                    }
                }
            }
        } else {
            if (!this.disable_flash &&
                    this.type == Lines.LINES_3X_09_V1 &&
                    this.showed && this.lines.size() > 0) {
                this.time += delta;
                if (this.time >= LINE_SHOW_TIME) {
                    this.time = 0f;
                    this.flash = !this.flash;
                }
            }
            if (this.showed) return;
            this.time += delta;
            if (this.time >= LINE_SHOW_TIME || this.current < 0) {
                this.time = 0f;
                if (this.lines.size() < 1) {
                    if (!this.showed) {
                        this.showed = true;
                        this.parent.linesShowed();
                    }
                    return;
                }
                if (this.current < 0 ||
                        this.current >= this.lines.size())
                    this.current = -1;

                if (++this.current >= this.lines.size()) {
                    this.showed = true;
                    this.parent.linesShowed();
                    this.flash = false;
                } else {
                    LineImg line = this.lines.get(this.current);
                    line.visible = true;
                    if (Connection.getInstance().isSoundOn() && this.line_wins_sound != null) {
                        int index = this.current < 0 ? 0 : this.current >= this.line_wins_sound.length ? this.line_wins_sound.length - 1 : this.current;
                        Sound sound = this.line_wins_sound[index];
                        if (sound != null) sound.play();
                    }
                    this.parent.lineShowed(line.index + 1, line.award);
                }
            }
        }
    }

    public void draw(Batch batch) {
        if (this.type == Lines.LINES_3X_09_V1) {
            float w = parent.getWidth();
            float h = parent.getHeight();
            float[] l = parent.config.lines_l_bounds;
            float[] r = parent.config.lines_r_bounds;
            for (int i = 0; i < ORDER.length; i++) {
                TextureRegion texture =
                        (ORDER[i] < this.active_lines) ?
                                (this.pay_lines.contains(Integer.valueOf(ORDER[i])) && !this.flash) ?
                                        this.inactive[ORDER[i]] :
                                        this.active[ORDER[i]] :
                                this.inactive[ORDER[i]];
                batch.draw(texture, l[0] * w, (l[1] + i * (l[4] + l[3])) * h, l[2] * w, l[3] * h);
                batch.draw(texture, r[0] * w, (r[1] + i * (r[4] + r[3])) * h, r[2] * w, r[3] * h);
            }
        }
        for (LineImg line : this.lines) {
            line.draw(batch);
        }
    }

    public void showAll() {
        this.one_by_one = false;
        for (LineImg line : this.lines) {
            line.visible = true;
        }
        this.disable_flash = true;
        this.flash = true;
        if (!this.showed) {
            this.showed = true;
            this.parent.linesShowed();
        }
    }

    public void oneByOne() {
        this.one_by_one = true;
        this.showed = false;
    }

    public void addLines(List<LineExt> lines) {
        if (lines == null) return;
        for (LineExt line : lines) {
            this.addLine(line.getLine(), line.getAward(), line.isBonusLine(), line.getCount(), line.isLeft());
        }
    }

    public void addLine(int[] positions, int award, boolean bonus_line, int count, boolean left) {
        int index = LinesConfig.getIndex(this.type, positions);
        this.pay_lines.add(Integer.valueOf(index));
        LineImg line = new LineImg(
                this, this.atlas, this.type, award, index,
                bonus_line ? Color.WHITE.toFloatBits() : LinesConfig.getColor(this.type, index)
        );
        line.count = count;
        line.left = left;
        line.positions = positions;
        this.lines.add(line);
    }

    public int[] getHighlights(int reel, SymbolType[] highlight_all) {
        Set<Integer> set = new HashSet<Integer>();
        for (LineImg line : this.lines) {
            if (line.left) {
                if (line.count > reel) {
                    set.add(Integer.valueOf(line.positions[reel]));
                }
            } else {
                if (line.count > 4 - reel) {
                    set.add(Integer.valueOf(line.positions[reel]));
                }
            }
        }
        if (highlight_all != null) {
            List<Symbol> symbols = this.parent.getReel(reel).getSymbols();
            for (SymbolType type : highlight_all) {
                for (int i = 1; i < symbols.size(); i++) {
                    if (symbols.get(i).getType() == type) {
                        set.add(Integer.valueOf(i - 1));
                    }
                }
            }
        }
        int[] array = new int[set.size()];
        int i = 0;
        for (Integer value : set) {
            array[i++] = value.intValue();
        }
        return array;
    }

    public int getCount() {
        return this.lines.size();
    }

    public void clear() {
        this.lines.clear();
        this.pay_lines.clear();
        this.disable_flash = false;
        this.flash = true;
        this.showed = false;
        this.one_by_one = false;
        this.current = -1;
        this.time = 0f;
    }

    public int getActiveLines() {
        return this.active_lines;
    }

    public void setActiveLines(int active_lines) {
        this.active_lines = active_lines;
    }

    public boolean isShowed() {
        return this.showed;
    }

    public class LineImg {
        private final int index;
        private final TextureRegion texture;
        private final float color;
        private final int award;
        public boolean visible;
        private LinesImg parent;
        private int count;
        private boolean left;
        private int[] positions;

        public LineImg(LinesImg parent, TextureAtlas atlas, Lines type, int award, int[] positions) {
            this(parent, atlas, type, award, LinesConfig.getIndex(type, positions), LinesConfig.getColor(type, LinesConfig.getIndex(type, positions)));
        }

        public LineImg(LinesImg parent, TextureAtlas atlas, Lines type, int award, int[] positions, float color) {
            this(parent, atlas, type, award, LinesConfig.getIndex(type, positions), color);
        }

        public LineImg(LinesImg parent, TextureAtlas atlas, Lines type, int award, int index) {
            this(parent, atlas, type, award, index, LinesConfig.getColor(type, index));
        }

        public LineImg(LinesImg parent, TextureAtlas atlas, Lines type, int award, int index, float color) {
            this.parent = parent;
            this.color = color;
            this.index = index;
            this.texture = atlas.findRegion("line", index);
            this.award = award;
            this.visible = false;
            this.count = 0;
            this.left = true;
        }

        public void draw(Batch batch) {
            if (!this.visible) return;
            float w = parent.parent.getWidth();
            float h = parent.parent.getHeight();
            float[] bounds = parent.parent.config.lines_bounds;
            Color color = batch.getColor();
            batch.setColor(this.color);
            batch.draw(
                    this.texture,
                    bounds[0] * w,
                    bounds[1] * h,
                    bounds[2] * w,
                    bounds[3] * h
            );
            batch.setColor(color);
        }
    }

}
