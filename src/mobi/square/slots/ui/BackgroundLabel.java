package mobi.square.slots.ui;

import mobi.square.slots.tools.FontsFactory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

public class BackgroundLabel extends PixelLabel {

	protected final TextureRegion background;
	protected final float[] bounds;
	protected final float[] offset;

	public BackgroundLabel(String text, LabelStyle style, TextureRegion background) {
		this(text, style, background, null);
	}

	public BackgroundLabel(String text, LabelStyle style, TextureRegion background, float[] offset) {
		super(text, style == null ? new LabelStyle(FontsFactory.getAsync("Junegull.ttf", 18), Color.WHITE) : style);
		this.background = background;
		this.bounds = new float[4];
		this.offset = offset;
		super.setAlignment(Align.center);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		if (this.background != null)
			batch.draw(this.background, this.bounds[0], this.bounds[1], this.bounds[2], this.bounds[3]);
		super.draw(batch, parentAlpha);
	}

	@Override
	public void setBounds(float x, float y, float width, float height) {
		this.bounds[0] = x;
		this.bounds[1] = y;
		this.bounds[2] = width;
		this.bounds[3] = height;
		if (this.offset != null && this.offset.length >= 4) {
			super.setBounds(
				x + width * this.offset[0],
				y + height * this.offset[1],
				width * this.offset[2],
				height * this.offset[3]
			);
		} else super.setBounds(x, y, width, height);
	}

}
