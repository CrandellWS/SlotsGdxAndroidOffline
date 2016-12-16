package mobi.square.slots.classes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import mobi.square.slots.containers.LinesExt.LineExt;
import mobi.square.slots.enums.SymbolType;
import mobi.square.slots.utils.utils;

public class Lines {

    private static final float LINE_SHOW_TIME = .75f;

    final Machine parent;
    private final List<Line> lines;
    private float time;
    private int current_index;
    private boolean showed;
    private float custom_time;

    Lines(Machine parent) {
        this.parent = parent;
        this.lines = new LinkedList<Line>();
        this.time = 0f;
        this.current_index = 0;
        this.showed = true;
        this.custom_time = 0f;
    }

    public void act(float delta) {
        if (!this.parent.started) {
            this.time -= delta;
            while (this.time <= 0f) {
                this.time += this.getLinesTime();
                this.current_index++;
                if (this.current_index >= this.lines.size()) {
                    this.current_index = 0;
                    if (!this.showed) {
                        this.showed = true;
                        this.parent.linesShowed();
                    }
                } else if (!this.showed) {
                    this.parent.lineShowed(0, this.lines.get(this.current_index).getAward());
                }
            }
        }
    }

    public void draw(ShapeRenderer renderer, float size) {
        if (!this.parent.started) {
            if (this.current_index < this.lines.size()) {
                this.lines.get(this.current_index).draw(renderer, size);
            }
        }
    }

    public void setCustomTime(float time) {
        this.custom_time = time;
    }

    public int[] getHighlights(int reel, SymbolType[] highlight_all) {
        Set<Integer> set = new HashSet<Integer>();
        for (Line line : this.lines) {
            if (line.highlights[reel]) {
                set.add(Integer.valueOf(line.positions[reel]));
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

    public void addLines(List<LineExt> lines) {
        this.lines.clear();
        if (lines == null) return;
        for (LineExt line : lines) {
            Line l = new Line(this, line.isBonusLine() ? Color.WHITE : this.getRandomColor(), line.getAward());
            l.setPositions(line.getLine());
            l.setCount(line.getCount(), line.isLeft());
            l.setMultipliers(line.getMultipliers());
            this.lines.add(l);
        }
        this.time = lines.size() > 0 ? this.getLinesTime() : 0f;
        this.current_index = 0;
        this.showed = false;
    }

    public void addLine(int[] positions, int count, boolean left, boolean bonus_line, int award) {
        Line line = new Line(this, bonus_line ? Color.WHITE : this.getRandomColor(), award);
        line.setPositions(positions);
        line.setCount(count, left);
        this.lines.add(line);
    }

    public int getCount() {
        return this.lines.size();
    }

    public void clear() {
        this.lines.clear();
        this.current_index = 0;
        this.showed = true;
    }

    private float getLinesTime() {
        return this.custom_time > 0f ? this.custom_time : LINE_SHOW_TIME;
    }

    private Color getRandomColor() {
        int r = 0, g = 0, b = 0;
        int c0 = utils.getRandom(50);
        int c1 = utils.getRandom(200, 256);
        int c2 = utils.getRandom(50, 256);
        int rand = utils.getRandom(6);
        switch (rand) {
            case 0:
                r = c0;
                g = c1;
                b = c2;
                break;
            case 1:
                r = c0;
                g = c2;
                b = c1;
                break;
            case 2:
                r = c1;
                g = c0;
                b = c2;
                break;
            case 3:
                r = c1;
                g = c2;
                b = c0;
                break;
            case 4:
                r = c2;
                g = c0;
                b = c1;
                break;
            case 5:
                r = c2;
                g = c1;
                b = c0;
                break;
            default:
                break;
        }
        return new Color(
                (float) r / 255f,
                (float) g / 255f,
                (float) b / 255f,
                1f
        );
    }

    public boolean isShowed() {
        return this.showed;
    }

}
