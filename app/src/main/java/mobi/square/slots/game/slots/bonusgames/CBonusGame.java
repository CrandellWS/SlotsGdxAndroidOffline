package mobi.square.slots.game.slots.bonusgames;

import java.util.HashMap;

import mobi.square.slots.game.slots.CSlots;
import mobi.square.slots.utils.json.JsonNode;
import mobi.square.slots.utils.utils;

public abstract class CBonusGame implements IBonusGame {

    private static final long serialVersionUID = -5809673455473971661L;
    protected transient int award;
    private transient CSlots parent;
    private transient boolean over;

    public CBonusGame(CSlots parent) {
        this.parent = parent;
        this.over = true;
        this.award = 0;
    }

    public static int getRandomMultiplier(int[] multipliers) {
        return multipliers[utils.getRandomIntMTF(multipliers.length)];
    }

    @Override
    public CSlots getParent() {
        return this.parent;
    }

    @Override
    public void setParent(CSlots parent) {
        this.parent = parent;
    }

    @Override
    public void save() {
    }

    @Override
    public boolean isOver() {
        return this.over;
    }

    @Override
    public void setOver(boolean over) {
        this.over = over;
    }

    @Override
    public int getAward() {
        return this.award;
    }

    @Override
    public HashMap<String, JsonNode> getStateJson() {
        HashMap<String, JsonNode> json = new HashMap<String, JsonNode>();
        json.put("type", new JsonNode(this.getType().toLowerCase()));
        json.put("over", new JsonNode(this.isOver()));
        json.put("award", new JsonNode(this.getAward()));
        return json;
    }

    /**
     * Не использовать!
     * Только для эмуляции.
     */
    protected final void takeAward() {
        this.getParent().addAward(this.getAward());
        this.getParent().takeAward();
        this.getParent().setBonusGameInstance(null);
        this.end();
    }

}
