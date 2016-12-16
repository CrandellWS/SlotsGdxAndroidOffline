package mobi.square.slots.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.I18NBundleLoader;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.I18NBundle;

import mobi.square.slots.api.Connection;
import mobi.square.slots.app.Game;
import mobi.square.slots.handlers.LoadingHandler;
import mobi.square.slots.stages.Loading;
import mobi.square.slots.tools.FontsFactory;

public class SLoading extends SBasic {

    private LoadingHandler handler;
    private SBasic current_screen;
    private SBasic loading_screen;

    public SLoading(Game parent, SBasic current_screen, SBasic loading_screen, LoadingHandler handler) {
        super(parent);
        super.stage = new Loading(this);
        this.current_screen = current_screen;
        this.loading_screen = loading_screen;
        this.handler = handler;
    }

    //private float temp = 0f;

    private boolean isLoaded() {
        if (this.loading_screen != null) {
            TextureInfo[] t_list = this.loading_screen.getTextureList();
            for (TextureInfo info : t_list) {
                if (info == null) continue;
                if (!Connection.getManager().isLoaded(info.name, Texture.class)) {
                    return false;
                }
            }
            AtlasInfo[] a_list = this.loading_screen.getAtlasList();
            for (AtlasInfo info : a_list) {
                if (info == null) continue;
                if (!Connection.getManager().isLoaded(info.name, TextureAtlas.class)) {
                    return false;
                }
            }
            FontInfo[] f_list = this.loading_screen.getFontsList();
            for (FontInfo info : f_list) {
                if (info == null) continue;
                if (!FontsFactory.isLoaded(info.name, info.size)) {
                    return false;
                }
            }
            SoundInfo[] s_list = this.loading_screen.getSoundList();
            for (SoundInfo info : s_list) {
                if (info == null) continue;
                if (!Connection.getManager().isLoaded(info.name, Sound.class)) {
                    return false;
                }
            }
            I18NInfo[] i_list = this.loading_screen.getI18NList();
            for (I18NInfo info : i_list) {
                if (info == null) continue;
                if (!Connection.getManager().isLoaded(info.name, I18NBundle.class)) {
                    return false;
                }
            }
        }
        if (this.handler != null) {
            if (!this.handler.isLoaded()) {
                //if (this.temp == 0f) System.out.println("NO RESPONSE");
                return false;
            }
        }
        if (this.loading_screen != null) {
            { // Set linear filter
                TextureInfo[] t_list = this.loading_screen.getTextureList();
                for (TextureInfo info : t_list) {
                    if (info == null) continue;
                    Texture tex = Connection.getManager().get(info.name, Texture.class);
                    tex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
                }
            }
            this.loading_screen.getStage().load(this.handler);
        }
        return true;
    }

    // Screen implementation

    @Override
    public void render(float delta) {
        /*this.temp += delta;
		if (this.temp >= 2f)
			this.temp = 0f;*/
        if (this.isLoaded()) {
            if (this.handler != null) {
                this.handler.complete();
            }
            Connection.getWrapper().clearVars();
            super.parent.changeScreen(this.loading_screen);
        } else {
            Connection.getManager().update();
            super.render(delta);
        }
    }

    @Override
    public void show() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.input.setInputProcessor(null);
        this.stage.load(null);
        if (this.current_screen != null) {
            { // Unload textures
                TextureInfo[] list = this.current_screen.getTextureList();
                for (TextureInfo info : list) {
                    if (info == null) continue;
                    if (!info.permanent && Connection.getManager().isLoaded(info.name, Texture.class)) {
                        Connection.getManager().unload(info.name);
                    }
                }
            }
            { // Unload atlas
                AtlasInfo[] list = this.current_screen.getAtlasList();
                for (AtlasInfo info : list) {
                    if (info == null) continue;
                    if (!info.permanent && Connection.getManager().isLoaded(info.name, TextureAtlas.class)) {
                        Connection.getManager().unload(info.name);
                    }
                }
            }
            { // Unload fonts
                FontInfo[] list = this.current_screen.getFontsList();
                for (FontInfo info : list) {
                    if (info == null) continue;
                    if (!info.permanent && FontsFactory.isLoaded(info.name, info.size)) {
                        FontsFactory.unload(info.name, info.size);
                    }
                }
            }
            { // Unload sounds
                SoundInfo[] list = this.current_screen.getSoundList();
                for (SoundInfo info : list) {
                    if (info == null) continue;
                    if (!info.permanent && Connection.getManager().isLoaded(info.name, Sound.class)) {
                        Connection.getManager().unload(info.name);
                    }
                }
            }
            { // Unload i18n
                I18NInfo[] list = this.current_screen.getI18NList();
                for (I18NInfo info : list) {
                    if (info == null) continue;
                    if (!info.permanent && Connection.getManager().isLoaded(info.name, I18NBundle.class)) {
                        Connection.getManager().unload(info.name);
                    }
                }
            }
        }
        if (this.loading_screen != null) {
            { // Load textures
                TextureInfo[] list = this.loading_screen.getTextureList();
                for (TextureInfo info : list) {
                    if (info == null) continue;
                    if (!Connection.getManager().isLoaded(info.name, Texture.class)) {
                        Connection.getManager().load(info.name, Texture.class);
                    }
                }
            }
            { // Load atlas
                AtlasInfo[] list = this.loading_screen.getAtlasList();
                for (AtlasInfo info : list) {
                    if (info == null) continue;
                    if (!Connection.getManager().isLoaded(info.name, TextureAtlas.class)) {
                        Connection.getManager().load(info.name, TextureAtlas.class);
                    }
                }
            }
            { // Load fonts
                FontInfo[] list = this.loading_screen.getFontsList();
                for (FontInfo info : list) {
                    if (info == null) continue;
                    if (!FontsFactory.isLoaded(info.name, info.size)) {
                        FontsFactory.loadAsync(info.name, info.size);
                    }
                }
            }
            { // Load sounds
                SoundInfo[] list = this.loading_screen.getSoundList();
                for (SoundInfo info : list) {
                    if (info == null) continue;
                    if (!Connection.getManager().isLoaded(info.name, Sound.class)) {
                        Connection.getManager().load(info.name, Sound.class);
                    }
                }
            }
            { // Load i18n
                I18NInfo[] list = this.loading_screen.getI18NList();
                for (I18NInfo info : list) {
                    if (info == null) continue;
                    if (!Connection.getManager().isLoaded(info.name, I18NBundle.class)) {
                        Connection.getManager().load(info.name, I18NBundle.class, new I18NBundleLoader.I18NBundleParameter(Connection.getLocale()));
                    }
                }
            }
        }
    }

}
