package mobi.square.slots.ui.bonus;

import mobi.square.slots.api.Connection;
import mobi.square.slots.ui.PixelLabel;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public abstract class BasicGameOverWindow extends PixelLabel {

	private final TextureRegionDrawable background;
	private final float[] bg_bounds;

	protected BasicGameOverWindow(TextureAtlas atlas, LabelStyle style) {
		super(Connection.getString("bonus_game_over"), style);
		this.background = new TextureRegionDrawable(atlas.findRegion("game_over_label"));
		this.bg_bounds = new float[4];
		super.setAlignment(Align.center, Align.center);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		this.background.draw(batch, this.bg_bounds[0], this.bg_bounds[1], this.bg_bounds[2], this.bg_bounds[3]);
		super.draw(batch, parentAlpha);
	}

	@Override
	public void setBounds(float x, float y, float width, float height) {
		this.bg_bounds[0] = x;
		this.bg_bounds[1] = y;
		this.bg_bounds[2] = width;
		this.bg_bounds[3] = height;
		this.setLabelBounds(x, y, width, height);
	}

	public void setLabelBounds(float x, float y, float width, float height) {
		super.setBounds(x, y, width, height);
	}

}
