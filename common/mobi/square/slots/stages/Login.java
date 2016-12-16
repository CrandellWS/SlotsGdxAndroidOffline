package mobi.square.slots.stages;

import mobi.square.slots.api.Connection;
import mobi.square.slots.api.SlotsApi.LoginType;
import mobi.square.slots.config.AppConfig;
import mobi.square.slots.error.StringCodeException;
import mobi.square.slots.handlers.LoadingHandler;
import mobi.square.slots.logger.Log;
import mobi.square.slots.screens.SBasic;
import mobi.square.slots.screens.SBasic.AtlasInfo;
import mobi.square.slots.screens.SBasic.FontInfo;
import mobi.square.slots.screens.SBasic.I18NInfo;
import mobi.square.slots.screens.SLobby;
import mobi.square.slots.tools.FontsFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.I18NBundle;

public class Login extends Basic {

	private boolean loaded;

	public Login(SBasic parent) {
		super(parent);
		this.loaded = false;
	}

	@Override
	public void load(LoadingHandler handler) {
		// Background
		TextureAtlas atlas = Connection.getManager().get("atlas/LoginScreen.pack", TextureAtlas.class);
		Image background = new Image(atlas.findRegion("login_background"));
		background.setBounds(0f, 0f, AppConfig.VIEWPORT_WIDTH, AppConfig.VIEWPORT_HEIGHT);
		super.addActor(background);
		// Button FB
		ButtonStyle style = new ButtonStyle();
		AtlasRegion region = atlas.findRegion("login_facebook_active");
		style.up = new TextureRegionDrawable(region);
		region = new AtlasRegion(
			region.getTexture(),
			region.getRegionX(),
			region.getRegionY() - 2,
			region.getRegionWidth(),
			region.getRegionHeight()
		);
		style.down = new TextureRegionDrawable(region);
		style.disabled = new TextureRegionDrawable(atlas.findRegion("login_facebook_inactive"));
		Button button = new Button(style);
		button.setBounds(312f, 182f, 104f, 104f);
		button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Connection.getWrapper().authorize(LoginType.FACEBOOK);
			}
		});
		super.addActor(button);
		// Button OK
		style = new ButtonStyle();
		region = atlas.findRegion("login_ok_active");
		style.up = new TextureRegionDrawable(region);
		region = new AtlasRegion(
			region.getTexture(),
			region.getRegionX(),
			region.getRegionY() - 2,
			region.getRegionWidth(),
			region.getRegionHeight()
		);
		style.down = new TextureRegionDrawable(region);
		style.disabled = new TextureRegionDrawable(atlas.findRegion("login_ok_inactive"));
		button = new Button(style);
		button.setBounds(460f, 182f, 104f, 104f);
		button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Connection.getWrapper().authorize(LoginType.OK);
			}
		});
		super.addActor(button);
		// Button G+
		style = new ButtonStyle();
		region = atlas.findRegion("login_google_active");
		style.up = new TextureRegionDrawable(region);
		region = new AtlasRegion(
			region.getTexture(),
			region.getRegionX(),
			region.getRegionY() - 2,
			region.getRegionWidth(),
			region.getRegionHeight()
		);
		style.down = new TextureRegionDrawable(region);
		style.disabled = new TextureRegionDrawable(atlas.findRegion("login_google_inactive"));
		button = new Button(style);
		button.setBounds(608f, 182f, 104f, 104f);
		button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Connection.getWrapper().authorize(LoginType.GOOGLE);
			}
		});
		super.addActor(button);
		// Button Play
		style = new ButtonStyle();
		region = atlas.findRegion("login_phone_active");
		style.up = new TextureRegionDrawable(region);
		region = new AtlasRegion(
			region.getTexture(),
			region.getRegionX(),
			region.getRegionY() - 2,
			region.getRegionWidth(),
			region.getRegionHeight()
		);
		style.down = new TextureRegionDrawable(region);
		style.disabled = new TextureRegionDrawable(atlas.findRegion("login_phone_inactive"));
		style.pressedOffsetY = -2f;
		button = new Button(style);
		button.setBounds(312f, 50f, 400f, 104f);
		button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Connection.getInstance().useDefaultDeviceId();
				LoadingHandler handler = new LoadingHandler();
				try {
					Connection.getInstance().requestSlotsList(handler);
					parent_screen.parent.showLoading(new SLobby(), handler);
				} catch (StringCodeException e) {
					Log.log(e);
				}
			}
		});
		super.addActor(button);
		// Label Play
		LabelStyle ls = new LabelStyle();
		ls.fontColor = Color.WHITE;
		ls.font = FontsFactory.getAsync("Junegull.ttf", 40f);
		Label label = new Label(Connection.getString("login_play"), ls);
		label.setFontScale(
			(float)AppConfig.VIEWPORT_WIDTH / (float)Gdx.graphics.getWidth(),
			(float)AppConfig.VIEWPORT_HEIGHT / (float)Gdx.graphics.getHeight()
		);
		label.setAlignment(Align.center, Align.center);
		label.setBounds(0f, 0f, button.getWidth(), button.getHeight());
		button.addActor(label);
		// Label Welcome
		ls = new LabelStyle();
		ls.fontColor = Color.WHITE;
		ls.font = FontsFactory.getAsync("Junegull.ttf", 54f);
		label = new Label(Connection.getString("login_welcome"), ls);
		label.setFontScale(
			(float)AppConfig.VIEWPORT_WIDTH / (float)Gdx.graphics.getWidth(),
			(float)AppConfig.VIEWPORT_HEIGHT / (float)Gdx.graphics.getHeight()
		);
		label.setAlignment(Align.center, Align.center);
		label.setBounds(0f, 460f, AppConfig.VIEWPORT_WIDTH, 54f);
		super.addActor(label);
		// Label Auth
		ls = new LabelStyle();
		ls.fontColor = Color.WHITE;
		ls.font = FontsFactory.getAsync("Junegull.ttf", 40f);
		label = new Label(Connection.getString("login_type"), ls);
		label.setFontScale(
			(float)AppConfig.VIEWPORT_WIDTH / (float)Gdx.graphics.getWidth(),
			(float)AppConfig.VIEWPORT_HEIGHT / (float)Gdx.graphics.getHeight()
		);
		label.setAlignment(Align.center, Align.center);
		label.setBounds(0f, 370f, AppConfig.VIEWPORT_WIDTH, 50f);
		super.addActor(label);
		// Loaded
		this.loaded = true;
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		if (!this.loaded) {
			AtlasInfo[] list = super.parent_screen.getAtlasList();
			for (AtlasInfo info : list) {
				if (!Connection.getManager().isLoaded(info.name, TextureAtlas.class)) {
					Connection.getManager().update();
					return;
				}
			}
			FontInfo[] fonts = super.parent_screen.getFontsList();
			for (FontInfo info : fonts) {
				if (FontsFactory.getAsync(info.name, info.size) == null) {
					Connection.getManager().update();
					return;
				}
			}
			I18NInfo[] i18n = super.parent_screen.getI18NList();
			for (I18NInfo info : i18n) {
				if (!Connection.getManager().isLoaded(info.name, I18NBundle.class)) {
					Connection.getManager().update();
					return;
				}
			}
			this.load(null);
		}
	}

	@Override
	public void draw() {
		if (this.loaded) {
			super.draw();
		}
	}

}
