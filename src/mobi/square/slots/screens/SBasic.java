package mobi.square.slots.screens;

import mobi.square.slots.api.AndroidApi;
import mobi.square.slots.app.Game;
import mobi.square.slots.config.SoundList;
import mobi.square.slots.stages.Basic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class SBasic implements Screen {

	public Game parent;
	protected Basic stage;
	protected SpriteBatch sprite_batch;
	protected TextureInfo[] texture_ext;
	protected AtlasInfo[] atlas_ext;
	protected FontInfo[] fonts_ext;
	protected SoundInfo[] sound_ext;
	protected I18NInfo[] i18n_ext;

	private static final TextureInfo[] texture_list;
	private static final AtlasInfo[] atlas_list;
	private static final FontInfo[] fonts_list;
	private static final SoundInfo[] sound_list;
	private static final I18NInfo[] i18n_list;

	static {
		texture_list = new TextureInfo[] {};
		if (AndroidApi.ONLINE) {
			atlas_list = new AtlasInfo[] {
				new AtlasInfo("atlas/Header.pack", true),
				new AtlasInfo("atlas/Windows.pack", true),
				new AtlasInfo("atlas/Cup.pack", true)
			};
		} else {
			atlas_list = new AtlasInfo[] {
				new AtlasInfo("atlas/Header.pack", true),
				new AtlasInfo("atlas/Windows.pack", true)
			};
		}
		fonts_list = new FontInfo[] {
			new FontInfo("Junegull.ttf", 18f, true),
			new FontInfo("Junegull.ttf", 22f, true),
			new FontInfo("Junegull.ttf", 24f, true),
			new FontInfo("Junegull.ttf", 30f, true),
			new FontInfo("Junegull.ttf", 48f, true),
			new FontInfo("Taurus.ttf", 20f, true),
			new FontInfo("Taurus.ttf", 24f, true),
			new FontInfo("Taurus.ttf", 28f, true),
			new FontInfo("whitrabt.ttf", 24f, true),
			new FontInfo("whitrabt.ttf", 40f, true)
		};
		sound_list = new SoundInfo[] {
			new SoundInfo(SoundList.MONEY_COMMON, true)
		};
		i18n_list = new I18NInfo[] {
			new I18NInfo("i18n/message", true)
		};
	}

	protected SBasic(Game parent) {
		this.parent = parent;
		this.sprite_batch = new SpriteBatch();
		this.stage = null;
		this.texture_ext = null;
		this.atlas_ext = null;
		this.fonts_ext = null;
		this.sound_ext = null;
		this.i18n_ext = null;
	}

	public SpriteBatch getSpriteBatch() {
		return this.sprite_batch;
	}

	public Basic getStage() {
		return this.stage;
	}

	public static class TextureInfo {
		public final String name;
		public final boolean permanent;
		public TextureInfo(String name, boolean permanent) {
			this.name = name;
			this.permanent = permanent;
		}
	}

	public static class AtlasInfo {
		public final String name;
		public final boolean permanent;
		public AtlasInfo(String name, boolean permanent) {
			this.name = name;
			this.permanent = permanent;
		}
	}

	public static class FontInfo {
		public final String name;
		public final float size;
		public final boolean permanent;
		public FontInfo(String name, float size, boolean permanent) {
			this.name = name;
			this.size = size;
			this.permanent = permanent;
		}
	}

	public static class SoundInfo {
		public final String name;
		public final boolean permanent;
		public SoundInfo(String name, boolean permanent) {
			this.name = name;
			this.permanent = permanent;
		}
	}

	public static class I18NInfo {
		public final String name;
		public final boolean permanent;
		public I18NInfo(String name, boolean permanent) {
			this.name = name;
			this.permanent = permanent;
		}
	}

	public TextureInfo[] getTextureList() {
		return texture_list;
	}

	public AtlasInfo[] getAtlasList() {
		return atlas_list;
	}

	public FontInfo[] getFontsList() {
		return fonts_list;
	}

	public SoundInfo[] getSoundList() {
		return sound_list;
	}

	public I18NInfo[] getI18NList() {
		return i18n_list;
	}

	protected TextureInfo[] getTextureList(TextureInfo[] ext) {
		TextureInfo[] basic = texture_list;
		if (ext == null) {
			this.texture_ext = basic;
			return basic;
		}
		TextureInfo[] result = new TextureInfo[basic.length + ext.length];
		for (int i = 0; i < basic.length; i++)
			result[i] = basic[i];
		for (int i = 0; i < ext.length; i++)
			result[i + basic.length] = ext[i];
		this.texture_ext = result;
		return result;
	}

	protected AtlasInfo[] getAtlasList(AtlasInfo[] ext) {
		AtlasInfo[] basic = atlas_list;
		if (ext == null) {
			this.atlas_ext = basic;
			return basic;
		}
		AtlasInfo[] result = new AtlasInfo[basic.length + ext.length];
		for (int i = 0; i < basic.length; i++)
			result[i] = basic[i];
		for (int i = 0; i < ext.length; i++)
			result[i + basic.length] = ext[i];
		this.atlas_ext = result;
		return result;
	}

	protected FontInfo[] getFontsList(FontInfo[] ext) {
		FontInfo[] basic = fonts_list;
		if (ext == null) {
			this.fonts_ext = basic;
			return basic;
		}
		FontInfo[] result = new FontInfo[basic.length + ext.length];
		for (int i = 0; i < basic.length; i++)
			result[i] = basic[i];
		for (int i = 0; i < ext.length; i++)
			result[i + basic.length] = ext[i];
		this.fonts_ext = result;
		return result;
	}

	protected SoundInfo[] getSoundList(SoundInfo[] ext) {
		SoundInfo[] basic = sound_list;
		if (ext == null) {
			this.sound_ext = basic;
			return basic;
		}
		SoundInfo[] result = new SoundInfo[basic.length + ext.length];
		for (int i = 0; i < basic.length; i++)
			result[i] = basic[i];
		for (int i = 0; i < ext.length; i++)
			result[i + basic.length] = ext[i];
		this.sound_ext = result;
		return result;
	}

	protected I18NInfo[] getI18NList(I18NInfo[] ext) {
		I18NInfo[] basic = i18n_list;
		if (ext == null) {
			this.i18n_ext = basic;
			return basic;
		}
		I18NInfo[] result = new I18NInfo[basic.length + ext.length];
		for (int i = 0; i < basic.length; i++)
			result[i] = basic[i];
		for (int i = 0; i < ext.length; i++)
			result[i + basic.length] = ext[i];
		this.i18n_ext = result;
		return result;
	}

	@Override
	public void render(float delta) {
		if (this.stage != null) {
			this.stage.act(delta);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			this.stage.draw();
		} else {
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		}
	}

	@Override
	public void resize(int width, int height) {
		//System.out.println("BasicScreen resize");
		this.stage.resize(width, height);
	}

	@Override
	public void show() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.input.setInputProcessor(this.stage);
		//System.out.println("BasicScreen show");
	}

	@Override
	public void hide() {
		//System.out.println("BasicScreen hide");
	}

	@Override
	public void pause() {
		//FontsFactory.dispose();
		//AtlasLoader.disposeAll();
		//Connection.dispose();
		//System.out.println("BasicScreen pause");
	}

	@Override
	public void resume() {
		//this.parent.showLoginScreenAuth();
		//System.out.println("BasicScreen resume");
	}

	@Override
	public void dispose() {
		//FontsFactory.dispose();
		//AtlasLoader.disposeAll();
		//Connection.dispose();
		//System.out.println("BasicScreen dispose");
	}

}
