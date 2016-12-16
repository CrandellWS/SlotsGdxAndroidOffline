package mobi.square.slots.effects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import mobi.square.slots.classes.Effects;
import mobi.square.slots.config.SlotsConfig;
import mobi.square.slots.enums.SymbolType;

public class Freeze extends CEffect {

    private final TextureRegionDrawable symbol;
    private final int reel;
    private final int row;

    public Freeze(Effects parent, SymbolType symbol, int row, int reel) {
        super(parent);
        this.symbol = new TextureRegionDrawable(parent.parent.atlas.findRegion(symbol.toLowerString()));
        this.row = parent.parent.config.symbols_count - row - 1;
        this.reel = reel;
    }

    @Override
    public void act(float delta) {
        // Nothing need to do
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float px = super.parent.parent.getX();
        float py = super.parent.parent.getY();
        float pw = super.parent.parent.getWidth();
        float ph = super.parent.parent.getHeight();
        SlotsConfig conf = super.parent.parent.config;
        float x = px + conf.left_padding[this.reel] * pw;
        float y = py + (conf.bottom_padding + (float) this.row * (conf.symbol_height + conf.vertical_padding)) * ph;
        float width = conf.symbol_width * pw;
        float height = conf.symbol_height * ph;
        this.symbol.draw(batch, x, y, width, height);
    }

}
