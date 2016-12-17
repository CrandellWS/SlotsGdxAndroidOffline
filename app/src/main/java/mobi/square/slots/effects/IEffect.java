package mobi.square.slots.effects;

import com.badlogic.gdx.graphics.g2d.Batch;

public interface IEffect {

    void act(float delta);

    void draw(Batch batch, float parentAlpha);

}
