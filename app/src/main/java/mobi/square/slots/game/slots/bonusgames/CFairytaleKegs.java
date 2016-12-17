package mobi.square.slots.game.slots.bonusgames;

import mobi.square.slots.enums.BonusGame;
import mobi.square.slots.game.slots.CSlots;

public class CFairytaleKegs extends CChests {

    private static final long serialVersionUID = -5447388288264641848L;

    public CFairytaleKegs(CSlots slots) {
        super(slots);
    }

    @Override
    public BonusGame getType() {
        return BonusGame.FAIRYTALE_KEGS;
    }

}
