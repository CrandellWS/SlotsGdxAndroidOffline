package mobi.square.slots.classes;

import com.badlogic.gdx.graphics.g2d.Batch;

import java.util.LinkedList;
import java.util.List;

import mobi.square.slots.api.Connection;
import mobi.square.slots.effects.Freeze;
import mobi.square.slots.effects.IEffect;
import mobi.square.slots.effects.MoveX;
import mobi.square.slots.effects.MoveY;
import mobi.square.slots.enums.SymbolType;
import mobi.square.slots.error.StringCodeException;
import mobi.square.slots.logger.Log;
import mobi.square.slots.utils.json.JsonObject;

public class Effects {

    public final Machine parent;
    private final List<IEffect> effects;

    public Effects(Machine parent) {
        this.parent = parent;
        this.effects = new LinkedList<IEffect>();
    }

    public void add(JsonObject params) {
        try {
            String type = params.getString("type");
            if (type.equals("move_x")) {
                SymbolType symbol = SymbolType.valueOf(params.getString("symbol").toUpperCase(Connection.getLocale()));
                int start = params.getInt("start_reel");
                int stop = params.getInt("stop_reel");
                int row = params.getInt("row");
                this.effects.add(new MoveX(this, symbol, start, stop, row));
            } else if (type.equals("move_y")) {
                SymbolType symbol = SymbolType.valueOf(params.getString("symbol").toUpperCase(Connection.getLocale()));
                int start = params.getInt("start_row");
                int stop = params.getInt("stop_row");
                int reel = params.getInt("reel");
                this.effects.add(new MoveY(this, symbol, start, stop, reel));
            } else if (type.equals("freeze")) {
                SymbolType symbol = SymbolType.valueOf(params.getString("symbol").toUpperCase(Connection.getLocale()));
                int row = params.getInt("row");
                int reel = params.getInt("reel");
                this.effects.add(new Freeze(this, symbol, row, reel));
            }
        } catch (StringCodeException e) {
            Log.log(e);
        }
    }

    public void clear() {
        this.effects.clear();
    }

    public void act(float delta) {
        for (IEffect effect : this.effects) {
            effect.act(delta);
        }
    }

    public void draw(Batch batch, float parentAlpha) {
        for (IEffect effect : this.effects) {
            effect.draw(batch, parentAlpha);
        }
    }

}
