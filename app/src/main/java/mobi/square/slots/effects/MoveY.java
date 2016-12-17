package mobi.square.slots.effects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import mobi.square.slots.classes.Effects;
import mobi.square.slots.config.SlotsConfig;
import mobi.square.slots.enums.SymbolType;

public class MoveY extends CEffect {

    private static final float MOVE_SPEED = .5f;

    private final TextureRegionDrawable symbol;
    private final float stop;
    private final int reel;
    private float progress;
    private float speed;

    public MoveY(Effects parent, SymbolType symbol, int start, int stop, int reel) {
        super(parent);
        this.symbol = new TextureRegionDrawable(parent.parent.atlas.findRegion(symbol.toLowerString()));
        SlotsConfig conf = super.parent.parent.config;
        this.stop = conf.bottom_padding + (conf.symbols_count - stop - 1) * (conf.symbol_height + conf.vertical_padding);
        this.progress = conf.bottom_padding + (conf.symbols_count - start - 1) * (conf.symbol_height + conf.vertical_padding);
        this.speed = this.progress < this.stop ? MOVE_SPEED : -MOVE_SPEED;
        this.reel = reel;
    }

    @Override
    public void act(float delta) {
        if (this.speed == 0f) return;
        this.progress += this.speed * delta;
        if (this.speed > 0f) {
            if (this.progress >= this.stop) {
                this.progress = this.stop;
                this.speed = 0f;
            }
        } else {
            if (this.progress <= this.stop) {
                this.progress = this.stop;
                this.speed = 0f;
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float px = super.parent.parent.getX();
        float py = super.parent.parent.getY();
        float pw = super.parent.parent.getWidth();
        float ph = super.parent.parent.getHeight();
        SlotsConfig conf = super.parent.parent.config;
        float x = px + conf.left_padding[this.reel] * pw;
        float y = py + this.progress * ph;
        float width = conf.symbol_width * pw;
        float height = conf.symbol_height * ph;
        this.symbol.draw(batch, x, y, width, height);
    }

}
