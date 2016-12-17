package mobi.square.slots.game.slots;

import java.io.Serializable;

import mobi.square.slots.enums.Symbol;

public class CSymbol implements Serializable {

    private static final long serialVersionUID = -1015826447860628391L;

    private CSlots parent = null;

    private Symbol symbol = null;
    private boolean win = false;
    private boolean special = false;

    public CSymbol(CSlots parent, Symbol symbol) {
        this.parent = parent;
        this.symbol = symbol;
    }

    // Public Methods

    // Getters & Setters

    public CSlots getParent() {
        return this.parent;
    }

    public void setParent(CSlots parent) {
        this.parent = parent;
    }

    public Symbol getSymbol() {
        return this.symbol;
    }

    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
    }

    public boolean isWin() {
        return this.win;
    }

    public void setWin(boolean win) {
        this.win = win;
    }

    public boolean isSpecial() {
        return this.special;
    }

    public void setSpecial(boolean special) {
        this.special = special;
    }

}
