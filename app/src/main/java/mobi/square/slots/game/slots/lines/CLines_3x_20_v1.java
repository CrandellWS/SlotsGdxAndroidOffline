package mobi.square.slots.game.slots.lines;

import mobi.square.slots.game.slots.CSlots;

public class CLines_3x_20_v1 extends CLines {

    private static final int[][] lines = {
            {1, 1, 1, 1, 1},    // 0 (1)
            {0, 0, 0, 0, 0},
            {2, 2, 2, 2, 2},
            {0, 1, 2, 1, 0},
            {2, 1, 0, 1, 2},
            {0, 0, 1, 0, 0},    // 5 (6)
            {2, 2, 1, 2, 2},
            {1, 0, 0, 0, 1},
            {1, 2, 2, 2, 1},
            {0, 1, 0, 1, 0},
            {2, 1, 2, 1, 2},    // 10 (11)
            {0, 1, 1, 1, 0},
            {2, 1, 1, 1, 2},
            {1, 0, 1, 0, 1},
            {1, 2, 1, 2, 1},
            {1, 1, 2, 1, 1},    // 15 (16)
            {1, 1, 0, 1, 1},
            {0, 0, 0, 1, 2},
            {2, 2, 2, 1, 0},
            {2, 0, 2, 0, 2}    // 19 (20)
    };

    private static final String[] colors = {
            "#f97600",
            "#3f0578",
            "#0cce23",
            "#0b4681",
            "#4205bc",
            "#11c51f",
            "#780378",
            "#fac800",
            "#f20161",
            "#c8fa01",
            "#c40031",
            "#1943b4",
            "#f00505",
            "#910101",
            "#0707a8",
            "#006231",
            "#f9d72f",
            "#5dc5f8",
            "#ca3297",
            "#930031"
    };

    public CLines_3x_20_v1(CSlots parent) {
        super(parent);
    }

    public int[][] getLines() {
        return lines;
    }

    public String[] getColors() {
        return colors;
    }

}
