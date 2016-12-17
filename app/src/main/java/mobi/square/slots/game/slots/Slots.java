package mobi.square.slots.game.slots;

import java.io.Serializable;

import mobi.square.slots.enums.SlotsType;
import mobi.square.slots.game.slots.config.ISlotsConfig;

public class Slots implements Serializable {

    private static final long serialVersionUID = 6928960779740835170L;

    private SlotsType type = null;
    private int min_level = 1;
    private int max_level = 9999;
    private int room = 0;
    private boolean hidden = false;
    private boolean closed = false;

    private transient ISlotsConfig config = null;

    // Public Methods

    public ISlotsConfig getConfig() {
        if (this.config == null)
            this.config = CSlots.getConfig(null, this.getType());
        return this.config;
    }

    // Getters & Setters

    public String getTitle() {
        return this.getType().toString();
    }

    public Integer getTypeNumber() {
        return this.getType().ordinal();
    }

    public SlotsType getType() {
        return this.type;
    }

    public void setType(SlotsType type) {
        this.type = type;
    }

    public int getMinLevel() {
        return this.min_level;
    }

    public void setMinLevel(int min_level) {
        this.min_level = min_level;
    }

    public int getMaxLevel() {
        return this.max_level;
    }

    public void setMaxLevel(int max_level) {
        this.max_level = max_level;
    }

    public int getRoom() {
        return room;
    }

    public void setRoom(int room) {
        this.room = room;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isClosed() {
        return this.closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

}
