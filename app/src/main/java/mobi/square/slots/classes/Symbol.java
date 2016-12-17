package mobi.square.slots.classes;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

import mobi.square.slots.enums.SymbolType;

public class Symbol extends Actor {

    protected final Reel parent;
    protected final TextureRegion texture;
    protected final SymbolType type;

    protected TextureRegion[] animation;
    protected int current_frame;
    protected float time_limit;
    protected float time;

    Symbol(Reel parent, SymbolType type) {
        this.type = type;
        this.parent = parent;
        this.texture = this.getSymbolTexture(type, null);
        this.animation = new TextureRegion[0];
        this.current_frame = -1;
        this.time_limit = 0f;
        this.time = 0f;
    }

    Symbol(Reel parent, SymbolType type, String suffix) {
        this.type = type;
        this.parent = parent;
        this.texture = this.getSymbolTexture(type, suffix);
        this.animation = new TextureRegion[0];
        this.current_frame = -1;
        this.time_limit = 0f;
        this.time = 0f;
    }

    private TextureRegion getSymbolTexture(SymbolType type, String suffix) {
        String region = suffix != null ? type.toLowerString().concat(suffix) : type.toLowerString();
        return this.parent.parent.atlas.findRegion(region);
    }

    public void setAnimation(TextureRegion[] textures, int frames_count, float frames_time) {
        if (textures == null || frames_count < 0 || frames_time <= 0f) {
            this.animation = new TextureRegion[0];
            return;
        }
        this.animation = new TextureRegion[frames_count];
        for (int i = 0; i < frames_count; i++)
            this.animation[i] = textures[i];
        this.current_frame = -1;
        this.time_limit = frames_time;
    }

    public void playAnimation() {
        if (this.animation.length > 0) {
            this.current_frame = 0;
        } else this.current_frame = -1;
    }

    public void stopAnimation() {
        this.current_frame = -1;
    }

    @Override
    public void act(float delta) {
        if (this.current_frame >= 0 && this.time_limit > 0f) {
            this.time += delta;
            while (this.time >= this.time_limit) {
                this.time -= this.time_limit;
                this.current_frame++;
                if (this.current_frame >= this.animation.length) {
                    this.current_frame = 0;
                }
            }
        }
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (this.current_frame >= 0) {
            if (this.current_frame >= 0 && this.current_frame < this.animation.length) {
                batch.draw(this.animation[this.current_frame], super.getX(), super.getY(), super.getWidth(), super.getHeight());
            }
        } else
            batch.draw(this.texture, super.getX(), super.getY(), super.getWidth(), super.getHeight());
    }

    public SymbolType getType() {
        return this.type;
    }

}
