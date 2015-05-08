package mobi.square.slots.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class GreyscaleActor extends DrawableActor {

	private static final ShaderProgram default_shader;
	private static final ShaderProgram greyscale_shader;
	static {
		String vertexShader = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
			+ "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
			+ "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
			+ "uniform mat4 u_projTrans;\n" //
			+ "varying vec4 v_color;\n" //
			+ "varying vec2 v_texCoords;\n" //
			+ "\n" //
			+ "void main()\n" //
			+ "{\n" //
			+ "   v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
			+ "   v_color.a = v_color.a * (256.0/255.0);\n" //
			+ "   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
			+ "   gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
			+ "}\n";
		String fragmentShader = "#ifdef GL_ES\n" //
			+ "#define LOWP lowp\n" //
			+ "precision mediump float;\n" //
			+ "#else\n" //
			+ "#define LOWP \n" //
			+ "#endif\n" //
			+ "varying LOWP vec4 v_color;\n" //
			+ "varying vec2 v_texCoords;\n" //
			+ "uniform sampler2D u_texture;\n" //
			+ "void main()\n"//
			+ "{\n" //
			+ "  vec4 c = v_color * texture2D(u_texture, v_texCoords);\n" //
			+ "  float grey = (c.r + c.g + c.b) / 3.0;\n" //
			+ "  gl_FragColor = vec4(grey, grey, grey, c.a);\n" //
			//+ "  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n" //
			+ "}";
		greyscale_shader = new ShaderProgram(vertexShader, fragmentShader);
		default_shader = SpriteBatch.createDefaultShader();
	}

	public boolean greyscale;

	private GreyscaleActor(Texture texture, TextureRegion region, TextureRegionDrawable drawable) {
		super(texture, region, drawable);
		this.greyscale = false;
	}

	public static GreyscaleActor newInstance(TextureRegion region) {
		GreyscaleActor instance = new GreyscaleActor(null, region, null);
		return instance;
	}

	public static GreyscaleActor newInstance(Texture texture) {
		GreyscaleActor instance = new GreyscaleActor(texture, null, null);
		return instance;
	}

	public static DrawableActor newInstance(TextureRegionDrawable drawable) {
		GreyscaleActor instance = new GreyscaleActor(null, null, drawable);
		return instance;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		if (this.greyscale) {
			batch.flush();
			batch.setShader(greyscale_shader);
			super.draw(batch, parentAlpha);
			batch.flush();
			batch.setShader(default_shader);
		} else super.draw(batch, parentAlpha);
	}

}
