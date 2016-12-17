package mobi.square.slots.game.slots.bonusgames;

import mobi.square.slots.enums.BonusGame;
import mobi.square.slots.game.slots.CSlots;

public class CUnderwaterLifeSeashells extends CChests {

    private static final long serialVersionUID = 1030973931408656828L;

    public CUnderwaterLifeSeashells(CSlots slots) {
        super(slots);
    }

    @Override
    public BonusGame getType() {
        return BonusGame.UNDERWATER_LIFE_SEASHELLS;
    }


}
