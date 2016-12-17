package mobi.square.slots.game.slots.bonusgames;

import java.util.HashMap;

import mobi.square.slots.enums.BonusGame;
import mobi.square.slots.game.slots.CSlots;
import mobi.square.slots.utils.json.JsonNode;
import mobi.square.slots.utils.utils;

public class CRockClimberCaves extends CBonusGame {

    private static final long serialVersionUID = 1L;
    private static final int[] multipliers = {0, 1, 2, 0, 3, 4, 5, 0, 6, 7, 8, 0, 9, 10};
    private static final int caves_count = 5;
    private boolean super_game;
    private int opened_count;
    private CBox[] caves;

    public CRockClimberCaves(CSlots parent) {
        super(parent);
    }

    @Override
    public void proc(int index) {
        if (this.isOver()) {
            return;
        }
        if (isSuperGame()) {
            this.setOver(true);
        } else {
            CBox cave = this.getCaves()[index];
            if (cave.isOpened()) {
                return;
            }
            for (int i = this.getCaves().length - 1; i >= 0; i--) {
                this.getCaves()[i].current = i == index;
            }
            cave.setOpened(true);
            this.opened_count += 1;

            if (cave.getAmount() > 0) {
                int amount = this.getParent().getLinesCount() * this.getParent().getBet() * cave.getAmount();
                this.award += amount;
                cave.setAmount(amount);
            } else {
                this.setOver(true);
            }

            if (this.opened_count == caves_count && !this.isOver()) {
                this.setSuperGame(true);
                this.award *= utils.getRandom(3, 7);
                this.setOver(true);
            }
        }
    }

    public void generate() {
        CBox[] caves = new CBox[caves_count];
        for (int i = 0; i < caves_count; i++) {
            caves[i] = new CBox(CBonusGame.getRandomMultiplier(multipliers), i);
        }
        caves[0].current = true;
        this.setCaves(caves);
        this.opened_count = 0;
        this.setOver(false);
    }

    @Override
    public void end() {
    }

    @Override
    public BonusGame getType() {
        return BonusGame.ROCKCLIMBER_CAVES;
    }

    public boolean isSuperGame() {
        return this.super_game;
    }

    public void setSuperGame(boolean super_game) {
        this.super_game = super_game;
    }

    public CBox[] getCaves() {
        return caves;
    }

    public void setCaves(CBox[] safes) {
        this.caves = safes;
    }

    @Override
    public HashMap<String, JsonNode> getStateJson() {
        HashMap<String, JsonNode> json = super.getStateJson();
        json.put("super_game", new JsonNode(this.isSuperGame()));
        java.util.List<JsonNode> ropes = new java.util.ArrayList<JsonNode>(caves_count);
        for (int i = 0; i < caves_count; i++) {
            CBox box = this.getCaves()[i];
            HashMap<String, JsonNode> json_rope = new HashMap<String, JsonNode>();
            json_rope.put("index", new JsonNode(box.getIndex()));
            json_rope.put("amount", new JsonNode(box.isOpened() ? box.getAmount() : 0));
            json_rope.put("current", new JsonNode(box.isCurrent()));
            json_rope.put("opened", new JsonNode(box.isOpened()));
            ropes.add(new JsonNode(json_rope));
        }
        json.put("safes", new JsonNode(ropes));
        return json;
    }

}
