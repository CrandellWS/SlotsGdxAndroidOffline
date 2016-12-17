package mobi.square.slots.utils;

import java.util.HashMap;
import java.util.Set;

public class IntMap<Type> {

    private HashMap<Type, Integer> map = null;

    protected HashMap<Type, Integer> getMap() {
        if (this.map == null)
            this.map = new HashMap<Type, Integer>();
        return this.map;
    }

    public int getValue(Type key) {
        Integer value = this.getMap().get(key);
        return value != null ? value.intValue() : 0;
    }

    public void setValue(Type key, int value) {
        this.getMap().put(key, Integer.valueOf(value));
    }

    public void addValue(Type key, int increment) {
        Integer value = this.getMap().get(key);
        if (value != null) {
            this.getMap().put(key, Integer.valueOf(value.intValue() + increment));
        } else this.getMap().put(key, Integer.valueOf(increment));
    }

    public Set<Type> keySet() {
        return this.getMap().keySet();
    }

    public boolean contains(Type key) {
        return this.getMap().containsKey(key);
    }

    public int size() {
        return this.getMap().size();
    }

}
