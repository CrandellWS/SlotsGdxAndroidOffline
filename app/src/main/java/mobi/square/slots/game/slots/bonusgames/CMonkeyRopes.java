package mobi.square.slots.game.slots.bonusgames;

import java.util.HashMap;

import mobi.square.slots.enums.BonusGame;
import mobi.square.slots.game.slots.CSlots;
import mobi.square.slots.utils.json.JsonNode;

public class CMonkeyRopes extends CBonusGame {

    private static final long serialVersionUID = 1970247922417701522L;
    private static final int[] multipliers_rope = {0, 0, 0, 0, 0, 20, 15, 10, 5, 5};
    private static final int[] multipliers_super_game = {0, 0, 0, 0, 250, 200, 100, 100, 50, 50};
    private static final int ropes_count = 5;
    private boolean super_game;
    private boolean in_helmet;
    private int opened_count;
    private CBox[] ropes;
    private BoxState left_box_state;
    private BoxState right_box_state;

    public CMonkeyRopes(CSlots parent) {
        super(parent);
    }

    @Override
    public void proc(int index) {
        if (this.isOver() || index < 0 || index > 4 || (this.isSuperGame() && index > 1)) {
            return;
        }
        if (isSuperGame()) {
            int mult = CBonusGame.getRandomMultiplier(multipliers_super_game);
            if (mult > 0) {
                if (index == 0) {
                    this.setLeftBoxState(BoxState.OPENED_WIN);
                    this.setRightBoxState(BoxState.OPENED_FAIL);
                } else {
                    this.setLeftBoxState(BoxState.OPENED_FAIL);
                    this.setRightBoxState(BoxState.OPENED_WIN);
                }
                this.award += this.getParent().getBet() * this.getParent().getLinesCount() * mult;
            } else {
                if (index == 0) {
                    this.setLeftBoxState(BoxState.OPENED_FAIL);
                    this.setRightBoxState(BoxState.OPENED_WIN);
                } else {
                    this.setLeftBoxState(BoxState.OPENED_WIN);
                    this.setRightBoxState(BoxState.OPENED_FAIL);
                }
            }
            this.setOver(true);
        } else {
            CBox rope = this.getRopes()[index];
            if (rope.isOpened()) {
                return;
            }
            int mult = CBonusGame.getRandomMultiplier(multipliers_rope);
            rope.setOpened(true);
            this.opened_count += 1;

            if (mult > 0) {
                int amount = this.getParent().getTotalBet() * mult;
                this.award += amount;
                rope.setAmount(amount);
            } else {
                rope.setAmount(0);
                if (this.isInHelmet()) {
                    this.setInHelmet(false);
                } else {
                    this.setOver(true);
                }
            }

            if (this.opened_count == ropes_count && !this.isOver()) {
                this.setSuperGame(true);
            }
        }

    }

    public void generate(boolean in_helment) {
        CBox[] ropes = new CBox[ropes_count];
        for (int i = 0; i < ropes_count; i++) {
            ropes[i] = new CBox(0, i);
        }
        this.setRopes(ropes);
        this.opened_count = 0;
        this.setLeftBoxState(BoxState.CLOSED);
        this.setRightBoxState(BoxState.CLOSED);
        this.setInHelmet(in_helment);
        this.setOver(false);
    }

    @Override
    public void end() {

    }

    @Override
    public BonusGame getType() {
        return BonusGame.MONKEY_ROPES;
    }

    public boolean isSuperGame() {
        return this.super_game;
    }

    public void setSuperGame(boolean super_game) {
        this.super_game = super_game;
    }


    public boolean isInHelmet() {
        return in_helmet;
    }


    public void setInHelmet(boolean in_helmet) {
        this.in_helmet = in_helmet;
    }


    public CBox[] getRopes() {
        return ropes;
    }


    public void setRopes(CBox[] ropes) {
        this.ropes = ropes;
    }


    public BoxState getLeftBoxState() {
        return left_box_state;
    }


    public void setLeftBoxState(BoxState left_box_state) {
        this.left_box_state = left_box_state;
    }


    public BoxState getRightBoxState() {
        return right_box_state;
    }


    public void setRightBoxState(BoxState right_box_state) {
        this.right_box_state = right_box_state;
    }

    @Override
    public HashMap<String, JsonNode> getStateJson() {
        HashMap<String, JsonNode> json = super.getStateJson();
        json.put("super_game", new JsonNode(this.isSuperGame()));
        json.put("in_helmet", new JsonNode(this.isInHelmet()));
        java.util.List<JsonNode> ropes = new java.util.ArrayList<JsonNode>(5);
        for (int i = 0; i < 5; i++) {
            CBox box = this.getRopes()[i];
            HashMap<String, JsonNode> json_rope = new HashMap<String, JsonNode>();
            json_rope.put("index", new JsonNode(box.getIndex()));
            json_rope.put("amount", new JsonNode(box.isOpened() ? box.getAmount() : 0));
            json_rope.put("opened", new JsonNode(box.isOpened()));
            ropes.add(new JsonNode(json_rope));
        }
        json.put("ropes", new JsonNode(ropes));
        json.put("left_box", new JsonNode(this.getLeftBoxState().toString()));
        json.put("right_box", new JsonNode(this.getRightBoxState().toString()));
        return json;
    }

}
