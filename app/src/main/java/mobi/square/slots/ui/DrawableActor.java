package mobi.square.slots.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class DrawableActor extends Actor {

    private final Texture texture;
    private final TextureRegion region;
    private final TextureRegionDrawable drawable;
    public float alpha;

    protected DrawableActor(Texture texture, TextureRegion region, TextureRegionDrawable drawable) {
        super();
        this.alpha = 1f;
        this.region = region;
        this.texture = texture;
        this.drawable = drawable;
    }

    public static DrawableActor newInstance(TextureRegion region) {
        DrawableActor instance = new DrawableActor(null, region, null);
        return instance;
    }

    public static DrawableActor newInstance(Texture texture) {
        DrawableActor instance = new DrawableActor(texture, null, null);
        return instance;
    }

    public static DrawableActor newInstance(TextureRegionDrawable drawable) {
        DrawableActor instance = new DrawableActor(null, null, drawable);
        return instance;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!super.isVisible()) return;
        Color c = null;
        if (this.alpha < 1f && this.alpha >= 0f) {
            c = batch.getColor();
            batch.flush();
            batch.setColor(c.r, c.g, c.b, this.alpha);
        }
        if (this.drawable != null) {
            this.drawable.draw(batch, super.getX(), super.getY(), super.getWidth(), super.getHeight());
        } else if (this.texture != null) {
            batch.draw(this.texture, super.getX(), super.getY(), super.getWidth(), super.getHeight());
        } else if (this.region != null) {
            batch.draw(this.region, super.getX(), super.getY(), super.getWidth(), super.getHeight());
        }
        if (c != null) {
            batch.flush();
            batch.setColor(c.r, c.g, c.b, c.a);
        }
    }

}
