package mobi.square.slots.screens;

import com.badlogic.gdx.assets.loaders.I18NBundleLoader;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.I18NBundle;

import mobi.square.slots.api.Connection;
import mobi.square.slots.app.Game;
import mobi.square.slots.stages.Login;
import mobi.square.slots.tools.FontsFactory;

public class SLogin extends SBasic {

	public SLogin(Game parent) {
		super(parent);
		super.stage = null;
	}

	@Override
	public void show() {
		//Connection.cancelRequests();
		Connection.getManager().clear();
		FontsFactory.dispose();
		AtlasInfo[] list = this.getAtlasList();
		for (AtlasInfo info : list) {
			Connection.getManager().load(info.name, TextureAtlas.class);
		}
		FontInfo[] fonts = this.getFontsList();
		for (FontInfo info : fonts) {
			FontsFactory.loadAsync(info.name, info.size);
		}
		I18NInfo[] i18n = this.getI18NList();
		for (I18NInfo info : i18n) {
			Connection.getManager().load(info.name, I18NBundle.class, new I18NBundleLoader.I18NBundleParameter(Connection.getLocale()));
		}
		super.stage = new Login(this);
		super.show();
	}

	@Override
	public AtlasInfo[] getAtlasList() {
		if (super.atlas_ext == null)
			super.atlas_ext = new AtlasInfo[] {
				new AtlasInfo("atlas/LoginScreen.pack", false)
			};
		return super.atlas_ext;
	}

	@Override
	public FontInfo[] getFontsList() {
		if (super.fonts_ext == null)
			super.fonts_ext = new FontInfo[] {
				new FontInfo("Junegull.ttf", 54f, false),
				new FontInfo("Junegull.ttf", 40f, false)
			};
		return super.fonts_ext;
	}

	@Override
	public I18NInfo[] getI18NList() {
		if (super.i18n_ext == null)
			super.i18n_ext = new I18NInfo[] {
				new I18NInfo("i18n/message", true)
			};
		return super.i18n_ext;
	}

}
