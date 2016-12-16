package mobi.square.slots.game.slots.lines;

import mobi.square.slots.game.slots.CSlots;

public class CLines_3x_09_v1 extends CLines {

    private static final int[][] lines = {
            {1, 1, 1, 1, 1},    // 0 (1)
            {0, 0, 0, 0, 0},
            {2, 2, 2, 2, 2},
            {0, 1, 2, 1, 0},
            {2, 1, 0, 1, 2},
            {0, 0, 1, 0, 0},    // 5 (6)
            {2, 2, 1, 2, 2},
            {1, 2, 2, 2, 1},
            {1, 0, 0, 0, 1}
    };

    private static final String[] colors = {
            "#ff0000",
            "#01fe01",
            "#0000ff",
            "#f26421",
            "#ffff00",
            "#ff00ff",
            "#00a450",
            "#00ffff",
            "#ffffff"
    };

    public CLines_3x_09_v1(CSlots parent) {
        super(parent);
    }

    public int[][] getLines() {
        return lines;
    }

    public String[] getColors() {
        return colors;
    }

}
