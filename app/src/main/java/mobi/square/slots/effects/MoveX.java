package mobi.square.slots.effects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import mobi.square.slots.classes.Effects;
import mobi.square.slots.config.SlotsConfig;
import mobi.square.slots.enums.SymbolType;

public class MoveX extends CEffect {

    private static final float MOVE_SPEED = .25f;

    private final TextureRegionDrawable symbol;
    private final float stop;
    private final int row;
    private float progress;
    private float speed;

    public MoveX(Effects parent, SymbolType symbol, int start, int stop, int row) {
        super(parent);
        this.symbol = new TextureRegionDrawable(parent.parent.atlas.findRegion(symbol.toLowerString()));
        this.row = parent.parent.config.symbols_count - row - 1;
        this.stop = this.getPadding(stop);
        this.progress = this.getPadding(start);
        this.speed = this.progress < this.stop ? MOVE_SPEED : -MOVE_SPEED;
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
        float x = px + this.progress * pw;
        float y = py + (conf.bottom_padding + (float) this.row * (conf.symbol_height + conf.vertical_padding)) * ph;
        float width = conf.symbol_width * pw;
        float height = conf.symbol_height * ph;
        this.symbol.draw(batch, x, y, width, height);
    }

    private float getPadding(int index) {
        float[] pad = super.parent.parent.config.left_padding;
        if (index < 0) {
            return pad[0] - (pad[1] - pad[0]);
        } else if (index >= pad.length) {
            return pad[4] + (pad[1] - pad[0]);
        } else return pad[index];
    }

}
