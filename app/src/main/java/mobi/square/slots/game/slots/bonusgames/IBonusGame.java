package mobi.square.slots.game.slots.bonusgames;

import java.io.Serializable;
import java.util.HashMap;

import mobi.square.slots.enums.BonusGame;
import mobi.square.slots.game.slots.CSlots;
import mobi.square.slots.utils.json.JsonNode;

public interface IBonusGame extends Serializable {

    void save();

    void proc(int index);

    void end();

    boolean isOver();

    void setOver(boolean over);

    int getAward();

    BonusGame getType();

    CSlots getParent();

    void setParent(CSlots parent);

    HashMap<String, JsonNode> getStateJson();

}
