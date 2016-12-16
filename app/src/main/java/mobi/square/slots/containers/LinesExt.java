package mobi.square.slots.containers;

import java.util.ArrayList;
import java.util.List;

import mobi.square.slots.enums.SymbolType;

public class LinesExt {

    private List<LineExt> lines = null;

    public LinesExt() {
        this.lines = new ArrayList<LinesExt.LineExt>();
    }

    public List<LineExt> getLines() {
        return this.lines;
    }

    public void setLines(List<LineExt> lines) {
        this.lines = lines;
    }

    public void add(LineExt line) {
        this.getLines().add(line);
    }

    public static class LineExt {

        private int[] line = null;
        private int count = 0;
        private SymbolType symbol;
        private boolean left = false;
        private boolean bonus_line = false;
        private Multiplier[] multipliers = null;
        private int award = 0;

        public LineExt(SymbolType symbol, int[] line, int count, boolean left) {
            this.line = line;
            this.count = count;
            this.symbol = symbol;
            this.left = left;
        }

        public int[] getLine() {
            return this.line;
        }

        public void setLine(int[] line) {
            this.line = line;
        }

        public int getCount() {
            return this.count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public boolean isLeft() {
            return this.left;
        }

        public void setLeft(boolean left) {
            this.left = left;
        }

        public boolean isBonusLine() {
            return this.bonus_line;
        }

        public void setBonusLine(boolean bonus_line) {
            this.bonus_line = bonus_line;
        }

        public SymbolType getSymbol() {
            return this.symbol;
        }

        public void setSymbol(SymbolType symbol) {
            this.symbol = symbol;
        }

        public Multiplier[] getMultipliers() {
            if (this.multipliers == null)
                this.multipliers = new Multiplier[0];
            return this.multipliers;
        }

        public void setMultipliers(Multiplier[] multipliers) {
            this.multipliers = multipliers;
        }

        public int getAward() {
            return this.award;
        }

        public void setAward(int award) {
            this.award = award;
        }

        public static class Multiplier {

            private int multiplier;
            private int reel;

            public Multiplier(int multiplier, int reel) {
                super();
                this.multiplier = multiplier;
                this.reel = reel;
            }

            public int getMultiplier() {
                return this.multiplier;
            }

            public void setMultiplier(int multiplier) {
                this.multiplier = multiplier;
            }

            public int getReel() {
                return this.reel;
            }

            public void setReel(int reel) {
                this.reel = reel;
            }

        }

    }

}
