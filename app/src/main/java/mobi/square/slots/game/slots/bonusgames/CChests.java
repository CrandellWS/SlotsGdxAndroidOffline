package mobi.square.slots.game.slots.bonusgames;

import java.util.HashMap;

import mobi.square.slots.enums.BonusGame;
import mobi.square.slots.game.slots.CSlots;
import mobi.square.slots.utils.json.JsonNode;
import mobi.square.slots.utils.utils;

public class CChests extends CBonusGame {

    private static final long serialVersionUID = 8473477594946451052L;
    // Количество сундуков с окончанием игры
    private static final int bonus_game_loose = 2;
    // Общее количество сундуков (менять нельзя)
    private static final int bonus_game_total = 9;
    private CBox[][] chests;

    public CChests(CSlots slots) {
        super(slots);
    }

    public void generate(int min, int max) {
        int total = bonus_game_total;
        this.chests = new CBox[3][];
        this.chests[0] = new CBox[3];
        this.chests[1] = new CBox[3];
        this.chests[2] = new CBox[3];
        boolean[] status = new boolean[total];

        int[] loose = new int[bonus_game_loose];
        for (int i = 0; i < bonus_game_loose; i++) {
            int value = utils.getRandomInt(total);
            for (int j = 0, k = 0; j < i && k < 20; j++, k++) {
                if (loose[j] == value) {
                    value = utils.getRandomInt(total);
                    j = -1;
                }
            }
            loose[i] = value;
        }

        for (int i = 0; i < total; i++) {
            status[i] = false;
            int amount = 0;
            if (!utils.contains(loose, i)) {
                amount = utils.getRandom(min, max);
            }
            int x = i % 3;
            int y = i / 3;
            this.chests[x][y] = new CBox(amount, i);
        }
        this.setOver(false);
    }

    @Override
    public void proc(int index) {
        if (this.isOver()) return;
        if (index < 0 || index > 8) return;
        int x = index % 3;
        int y = index / 3;
        CBox chest = this.getChests()[x][y];
        if (chest.isOpened()) {
            return;
        }
        chest.setOpened(true);
        if (chest.getAmount() > 0) {
            this.award += chest.getAmount();
        } else {
            this.setOver(true);
        }
    }

    public CBox[][] getChests() {
        return chests;
    }

    public CBox[] getFirstLine() {
        return this.getChests()[0];
    }

    public CBox[] getSecondLine() {
        return this.getChests()[1];
    }

    public CBox[] getThirdLine() {
        return this.getChests()[2];
    }

    @Override
    public BonusGame getType() {
        return BonusGame.CHESTS;
    }

    @Override
    public void end() {
        this.award = 0;
    }

    @Override
    public HashMap<String, JsonNode> getStateJson() {
        HashMap<String, JsonNode> json = super.getStateJson();
        java.util.List<JsonNode> boxes = new java.util.ArrayList<JsonNode>(bonus_game_total);
        for (int i = 0; i < bonus_game_total; i++) {
            int x = i % 3;
            int y = i / 3;
            CBox box = this.getChests()[x][y];
            HashMap<String, JsonNode> json_box = new HashMap<String, JsonNode>();
            json_box.put("index", new JsonNode(box.getIndex()));
            json_box.put("amount", new JsonNode(box.isOpened() ? box.getAmount() : 0));
            json_box.put("opened", new JsonNode(box.isOpened()));
            boxes.add(new JsonNode(json_box));
        }
        json.put("boxes", new JsonNode(boxes));
        return json;
    }

}
