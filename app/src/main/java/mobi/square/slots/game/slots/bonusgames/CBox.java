package mobi.square.slots.game.slots.bonusgames;

import java.io.Serializable;

public class CBox implements Serializable {

    private static final long serialVersionUID = -6254635688901298730L;

    int amount;
    int index;
    boolean opened;
    boolean current;

    public CBox(int amount, int index) {
        this.amount = amount;
        this.index = index;
        this.opened = false;
        this.current = false;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isOpened() {
        return this.opened;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    public int getAmount() {
        return this.amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean isCurrent() {
        return this.current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

}