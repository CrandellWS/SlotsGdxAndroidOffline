package mobi.square.slots.game.slots.lines;

import mobi.square.slots.enums.Lines;
import mobi.square.slots.game.slots.CSlots;
import mobi.square.slots.game.slots.CSymbol;

public abstract class CLines implements ILines {

    private CSlots parent = null;

    public CLines(CSlots parent) {
        this.parent = parent;
    }

    // Public Methods

    public static ILines newLines(CSlots slots) {
        switch (slots.getConfig().getLinesSet()) {
            case LINES_3X_20_V1:
                return new CLines_3x_20_v1(slots);
            case LINES_3X_99_V1:
                return new CLines_3x_99_v1(slots);
            case LINES_4X_50_V1:
                return new CLines_4x_50_v1(slots);
            case LINES_3X_09_V1:
                return new CLines_3x_09_v1(slots);
            default:
                return null;
        }
    }

    public static ILines newLines(Lines lines) {
        switch (lines) {
            case LINES_3X_20_V1:
                return new CLines_3x_20_v1(null);
            case LINES_3X_99_V1:
                return new CLines_3x_99_v1(null);
            case LINES_4X_50_V1:
                return new CLines_4x_50_v1(null);
            case LINES_3X_09_V1:
                return new CLines_3x_09_v1(null);
            default:
                return null;
        }
    }

    /**
     * Вернуть линию символов
     *
     * @param index - индекс
     * @return Symbol[]
     */
    public CSymbol[] getLine(int index) {
        int reels_count = this.getParent().getConfig().getReelsCount();
        CSymbol[] line = new CSymbol[reels_count];
        for (int i = 0; i < reels_count; i++)
            line[i] = this.getParent().getCurrentSymbols()[i][this.getLines()[index][i]];
        return line;
    }

    // Getters & Setters

    public CSlots getParent() {
        return this.parent;
    }

    public void setParent(CSlots parent) {
        this.parent = parent;
    }

}
