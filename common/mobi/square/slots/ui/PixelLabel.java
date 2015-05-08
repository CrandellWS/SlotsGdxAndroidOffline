package mobi.square.slots.ui;

import mobi.square.slots.config.AppConfig;
import mobi.square.slots.tools.FontsFactory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class PixelLabel extends Label implements Resizable {

	private final Matrix4 proj_matrix;
	private final Matrix4 trans_matrix;
	private final Matrix4 pixel_matrix;

	private float v_width;
	private float v_height;
	private float ppu_x;
	private float ppu_y;

	public PixelLabel(CharSequence text, Skin skin) {
		this(text, skin.get(LabelStyle.class));
	}

	public PixelLabel(CharSequence text, Skin skin, String styleName) {
		this(text, skin.get(styleName, LabelStyle.class));
	}

	public PixelLabel(CharSequence text, Skin skin, String fontName, Color color) {
		this(text, new LabelStyle(skin.getFont(fontName), color));
	}

	public PixelLabel(CharSequence text, Skin skin, String fontName, String colorName) {
		this(text, new LabelStyle(skin.getFont(fontName), skin.getColor(colorName)));
	}

	public PixelLabel(CharSequence text, LabelStyle style) {
		super(text, style);
		this.proj_matrix = new Matrix4();
		this.trans_matrix = new Matrix4();
		this.pixel_matrix = new Matrix4();
		this.v_height = 0f;
		this.v_width = 0f;
		this.ppu_x = 1f;
		this.ppu_y = 1f;
	}

	public static PixelLabel newInstance(String text, String font, float size, Color color) {
		LabelStyle style = new LabelStyle();
		style.font = FontsFactory.getAsync(font, size);
		style.fontColor = color;
		PixelLabel instance = new PixelLabel(text, style);
		return instance;
	}

	public static PixelLabel newInstance(String text, String font, float size, Color color, int alignment) {
		LabelStyle style = new LabelStyle();
		style.font = FontsFactory.getAsync(font, size);
		style.fontColor = color;
		PixelLabel instance = new PixelLabel(text, style);
		instance.setAlignment(alignment);
		return instance;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		this.proj_matrix.set(batch.getProjectionMatrix());
		this.trans_matrix.set(batch.getTransformMatrix());
		batch.getTransformMatrix().idt();
		batch.setProjectionMatrix(this.pixel_matrix);
		float cx = super.getX();
		float cy = super.getY();
		super.setX((float)(int)((cx + this.trans_matrix.val[Matrix4.M03]) * this.ppu_x));
		super.setY((float)(int)((cy + this.trans_matrix.val[Matrix4.M13]) * this.ppu_y));
		super.draw(batch, parentAlpha);
		super.setX(cx);
		super.setY(cy);
		batch.setTransformMatrix(this.trans_matrix);
		batch.setProjectionMatrix(this.proj_matrix);
	}

	@Override
	public void setBounds(float x, float y, float width, float height) {
		this.v_width = width;
		this.v_height = height;
		super.setBounds(x, y, (float)(int)(width * this.ppu_x), (float)(int)(height * this.ppu_y));
	}

	@Override
	public void setSize(float width, float height) {
		this.v_width = width;
		this.v_height = height;
		super.setSize((float)(int)(width * this.ppu_x), (float)(int)(height * this.ppu_y));
	}

	@Override
	public Actor hit(float x, float y, boolean touchable) {
		//if (touchable && super.getTouchable() != Touchable.enabled) return null;
		return x >= 0 && x < this.v_width && y >= 0 && y < this.v_height ? this : null;
	}

	@Override
	public void setWidth(float width) {
		this.v_width = width;
		super.setWidth((float)(int)(width * this.ppu_x));
	}

	@Override
	public void setHeight(float height) {
		this.v_height = height;
		super.setHeight((float)(int)(height * this.ppu_y));
	}

	@Override
	public void resize(int width, int height) {
		this.pixel_matrix.setToOrtho2D(0f, 0f, (float)width, (float)height);
		this.ppu_x = (float)width / (float)AppConfig.VIEWPORT_WIDTH;
		this.ppu_y = (float)height / (float)AppConfig.VIEWPORT_HEIGHT;
		this.setSize(this.v_width, this.v_height);
		this.setFontScale(1f);
	}

}
