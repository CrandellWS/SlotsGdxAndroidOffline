package mobi.square.slots.game.slots.lines;

import mobi.square.slots.game.slots.CSlots;

public class CLines_4x_50_v1 extends CLines {

    private static final int[][] lines = {

            {1, 1, 1, 1, 1},    // 0
            {2, 2, 2, 2, 2},
            {0, 0, 0, 0, 0},
            {3, 3, 3, 3, 3},
            {0, 1, 2, 1, 0},
            {3, 2, 1, 2, 3},    // 5
            {1, 2, 3, 2, 1},
            {2, 1, 0, 1, 2},
            {0, 0, 1, 0, 0},
            {3, 3, 2, 3, 3},
            {1, 1, 2, 1, 1},    // 10
            {2, 2, 1, 2, 2},
            {2, 2, 3, 2, 2},
            {1, 1, 0, 1, 1},
            {0, 1, 0, 1, 0},
            {3, 2, 3, 2, 3},    // 15
            {1, 2, 1, 2, 1},
            {2, 1, 2, 1, 2},

            {2, 3, 2, 3, 2},
            {1, 0, 1, 0, 1},
            {0, 3, 3, 3, 0},    // 20
            {3, 0, 0, 0, 3},
            {2, 0, 0, 0, 2},
            {1, 3, 3, 3, 1},
            {3, 1, 1, 1, 3},
            {0, 2, 2, 2, 0},    // 25
            {0, 1, 1, 1, 1},
            {3, 2, 2, 2, 2},
            {1, 2, 2, 2, 2},
            {2, 1, 1, 1, 1},
            {2, 3, 3, 3, 3},    // 30
            {1, 0, 0, 0, 0},
            {0, 0, 1, 1, 1},
            {3, 3, 2, 2, 2},
            {1, 1, 2, 2, 2},
            {2, 2, 1, 1, 1},    // 35

            {2, 2, 3, 3, 3},
            {1, 1, 0, 0, 0},
            {0, 1, 1, 2, 3},
            {3, 2, 2, 1, 0},
            {0, 1, 2, 2, 3},    // 40
            {3, 2, 1, 1, 0},
            {0, 3, 0, 3, 0},
            {3, 0, 3, 0, 3},
            {0, 3, 2, 2, 2},
            {3, 0, 1, 1, 1},    // 41
            {1, 0, 1, 2, 3},
            {2, 3, 2, 1, 0},
            {0, 1, 2, 3, 2},
            {3, 2, 1, 0, 1},

    };

    private static final String[] colors = {
            "#ff0000",    // 0
            "#ffe400",
            "#24ff00",
            "#005aff",
            "#ff8a00",
            "#ff00f6",    // 5
            "#36ff00",
            "#0048ff",
            "#c0ff00",
            "#00ff30",
            "#8400ff",    // 10
            "#ff0000",
            "#00fff0",
            "#8400ff",
            "#aeff00",
            "#ff0000",    // 15
            "#0c00ff",
            "#fff600",
            "#ffa200",
            "#deff00",
            "#ff0000",    // 20
            "#ffe400",
            "#24ff00",
            "#005aff",
            "#ff8a00",
            "#ff00f6",    // 25
            "#36ff00",
            "#0048ff",
            "#c0ff00",
            "#00ff30",
            "#8400ff",    // 30
            "#ff0000",
            "#00fff0",
            "#8400ff",
            "#aeff00",
            "#ff0000",    // 35
            "#0c00ff",
            "#fff600",
            "#ffa200",
            "#deff00",
            "#ff0000",    // 40
            "#ffe400",
            "#24ff00",
            "#005aff",
            "#ff8a00",
            "#ff00f6",    // 45
            "#36ff00",
            "#0048ff",
            "#c0ff00",
            "#00ff30"
    };

    public CLines_4x_50_v1(CSlots parent) {
        super(parent);
    }

    public int[][] getLines() {
        return lines;
    }

    public String[] getColors() {
        return colors;
    }

}