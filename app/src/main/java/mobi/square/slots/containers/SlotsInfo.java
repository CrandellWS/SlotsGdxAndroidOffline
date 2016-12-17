package mobi.square.slots.containers;

import mobi.square.slots.enums.SlotsType;

public class SlotsInfo {

    private SlotsType type = SlotsType.NONE;
    private int min_level = 1;
    private int max_level = 9999;
    private int revision = 1;
    private boolean requires_vip = false;

    public SlotsType getType() {
        return this.type;
    }

    public void setType(SlotsType type) {
        this.type = type;
    }

    public int getRevision() {
        return this.revision;
    }

    public void setRevision(int revision) {
        this.revision = revision;
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

    @Override
    public String toString() {
        return type.toLowerString();
    }

    public boolean isRequiresVip() {
        return requires_vip;
    }

    public void setRequiresVip(boolean requires_vip) {
        this.requires_vip = requires_vip;
    }
}
