package mobi.square.slots.effects;

import com.badlogic.gdx.graphics.g2d.Batch;

public interface IEffect {

	public void act(float delta);

	public void draw(Batch batch, float parentAlpha);

}
