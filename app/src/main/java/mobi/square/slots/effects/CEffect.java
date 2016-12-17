package mobi.square.slots.effects;

import mobi.square.slots.classes.Effects;

public abstract class CEffect implements IEffect {

    protected final Effects parent;

    protected CEffect(Effects parent) {
        this.parent = parent;
    }

}
