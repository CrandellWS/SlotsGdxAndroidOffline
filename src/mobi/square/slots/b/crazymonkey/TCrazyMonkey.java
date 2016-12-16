package mobi.square.slots.b.crazymonkey;

import java.util.ArrayList;
import java.util.List;

import mobi.square.slots.api.Connection;
import mobi.square.slots.config.AppConfig;
import mobi.square.slots.tools.FontsFactory;
import mobi.square.slots.ui.DrawableActor;
import mobi.square.slots.ui.GameHeader;
import mobi.square.slots.ui.PixelLabel;
import mobi.square.slots.ui.SettingsWindow.HideButton;
import mobi.square.slots.ui.payout.TBasic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class TCrazyMonkey extends TBasic {

	private final Texture background;
	private final TextureRegionDrawable shadow;
	private final TableTitle title_label;
	private final HideButton hide_button;
	private final TableScroll scroll;

	private final List<PixelLabel> labels;

	private TCrazyMonkey(Texture background, TextureAtlas atlas, TextureAtlas symbols, TextureAtlas windows_atlas) {
		super();
		this.labels = new ArrayList<PixelLabel>();
		this.background = background;
		this.shadow = new TextureRegionDrawable(atlas.findRegion("table_shadow"));
		this.title_label = TableTitle.newInstance(atlas.findRegion("title_background"), new Color(1f, 1f, 1f, 1f), .06f, .06f, .96f, .9f);
		this.hide_button = HideButton.newInstance(windows_atlas);
		this.scroll = TableScroll.newInstance();
		super.addActor(this.title_label);
		super.addActor(this.hide_button);
		super.addActor(this.scroll);
		this.hide_button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (hide_button.isDisabled()) return;
				hide();
			}
		});
	}

	public static TCrazyMonkey newInstance(Texture background, TextureAtlas atlas, TextureAtlas symbols, TextureAtlas windows_atlas) {
		TCrazyMonkey instance = new TCrazyMonkey(background, atlas, symbols, windows_atlas);
		instance.setBounds(0f, 0f, AppConfig.VIEWPORT_WIDTH, AppConfig.VIEWPORT_HEIGHT - GameHeader.HEADER_HEIGHT);
		instance.title_label.setBounds(40f, 406f, 798f, 76f);
		instance.hide_button.setBounds(AppConfig.VIEWPORT_WIDTH - 160f, 412f, 160f, 60f);
		instance.scroll.setBounds(29f, 20f, AppConfig.VIEWPORT_WIDTH - 58f, 385f);
		TextureRegionDrawable n01 = new TextureRegionDrawable(atlas.findRegion("n01"));
		TextureRegionDrawable n02 = new TextureRegionDrawable(atlas.findRegion("n02"));
		TextureRegionDrawable n03 = new TextureRegionDrawable(atlas.findRegion("n03"));
		TextureRegionDrawable n04 = new TextureRegionDrawable(atlas.findRegion("n04"));
		TextureRegionDrawable n05 = new TextureRegionDrawable(atlas.findRegion("n05"));
		TextureRegionDrawable n06 = new TextureRegionDrawable(atlas.findRegion("n06"));
		TextureRegionDrawable n07 = new TextureRegionDrawable(atlas.findRegion("n07"));
		TextureRegionDrawable bonus = new TextureRegionDrawable(atlas.findRegion(Connection.getTextureLanguage("bonus")));
		TextureRegionDrawable wild = new TextureRegionDrawable(atlas.findRegion("wild"));
		TextureRegionDrawable any = new TextureRegionDrawable(atlas.findRegion(Connection.getTextureLanguage("symbol_any")));
		TextureRegionDrawable frame = new TextureRegionDrawable(atlas.findRegion("symbols_frame"));
		Color yellow = new Color(1f, 1f, 0f, 1f);
		BitmapFont font = FontsFactory.getAsync("arialbd.ttf", 36);
		{ // Bonus symbols
			Group group = instance.scroll.newRow(180f, 20f);
			group.setBounds(0f, 0f, AppConfig.VIEWPORT_WIDTH - 130f, 350f);
			LabelStyle ls = new LabelStyle();
			ls.font = FontsFactory.getAsync("Taurus.ttf", 20);
			ls.fontColor = Color.WHITE;
			PixelLabel label = new PixelLabel(Connection.getString("crazy_monkey_bonus"), ls);
			label.setBounds(207f, 0f, 250f, 160f);
			label.setAlignment(Align.top | Align.left);
			group.addActor(label);
			instance.labels.add(label);
			// Symbols
			TableSymbol symbol = TableSymbol.newInstance(null, bonus, 0f, 0f, 1f, 1f);
			symbol.setBounds(25f, 0f, 170f, 170f);
			group.addActor(symbol);
		}
		{ // Wild symbol
			Group group = instance.scroll.newRow(140f, 0f);
			DrawableActor wild_bg = DrawableActor.newInstance(atlas.findRegion("blue_line"));
			wild_bg.setBounds(0f, 35f, 966f, 70f);
			group.addActor(wild_bg);
			TextureRegionDrawable arrow_bg = new TextureRegionDrawable(atlas.findRegion("table_arrow"));
			DrawableActor arrow = DrawableActor.newInstance(arrow_bg);
			arrow.setBounds(152f, 41f, 80f, 55f);
			group.addActor(arrow);
			TableSymbol symbol = TableSymbol.newInstance(null, wild, .07f, .08f, .86f, .86f);
			symbol.setBounds(20f, 0f, 140f, 140f);
			group.addActor(symbol);
			symbol = TableSymbol.newInstance(null, n01, 0f, 0f, 1f, 1f);
			symbol.setBounds(230f, 15f, 110f, 110f);
			group.addActor(symbol);
			symbol = TableSymbol.newInstance(null, n02, 0f, 0f, 1f, 1f);
			symbol.setBounds(340f, 15f, 110f, 110f);
			group.addActor(symbol);
			symbol = TableSymbol.newInstance(null, n03, 0f, 0f, 1f, 1f);
			symbol.setBounds(450f, 15f, 110f, 110f);
			group.addActor(symbol);
			symbol = TableSymbol.newInstance(null, n04, 0f, 0f, 1f, 1f);
			symbol.setBounds(560f, 15f, 110f, 110f);
			group.addActor(symbol);
			symbol = TableSymbol.newInstance(null, n05, 0f, 0f, 1f, 1f);
			symbol.setBounds(670f, 15f, 110f, 110f);
			group.addActor(symbol);
			symbol = TableSymbol.newInstance(null, n06, 0f, 0f, 1f, 1f);
			symbol.setBounds(780f, 15f, 110f, 110f);
			group.addActor(symbol);
		}
		{ // Lines
			Group group = instance.scroll.newRow(270f, 0f);
			DrawableActor lines = DrawableActor.newInstance(atlas.findRegion(Connection.getTextureLanguage("lines_chart")));
			lines.setBounds(100f, 0f, 766f, 270f);
			group.addActor(lines);
		}
		{ // N07 & WILD
			Group group = instance.scroll.newRow(316f, 14f);
			
			Group inner = new Group();
			DrawableActor bg = DrawableActor.newInstance(frame);
			bg.setBounds(14f, 0f, 462f, 316f);
			inner.addActor(bg);
			inner.setBounds(0f, 0f, 483f, 316f);
			group.addActor(inner);
			
			instance.labels.add(makeSymbolsLine(inner, 5000, 4, font, yellow, n07, any, 5, true));
			instance.labels.add(makeSymbolsLine(inner, 1000, 3, font, yellow, n07, any, 4, false));
			instance.labels.add(makeSymbolsLine(inner, 1000, 2, font, yellow, n07, any, 4, true));
			instance.labels.add(makeSymbolsLine(inner, 200, 1, font, yellow, n07, any, 3, false));
			instance.labels.add(makeSymbolsLine(inner, 200, 0, font, yellow, n07, any, 3, true));
			
			inner = new Group();
			bg = DrawableActor.newInstance(frame);
			bg.setBounds(14f, 0f, 462f, 316f);
			inner.addActor(bg);
			inner.setBounds(476f, 0f, 483f, 316f);
			group.addActor(inner);
			
			instance.labels.add(makeSymbolsLine(inner, 2000, 4, font, yellow, wild, any, 5, true));
			instance.labels.add(makeSymbolsLine(inner, 500, 3, font, yellow, wild, any, 4, false));
			instance.labels.add(makeSymbolsLine(inner, 500, 2, font, yellow, wild, any, 4, true));
			instance.labels.add(makeSymbolsLine(inner, 100, 1, font, yellow, wild, any, 3, false));
			instance.labels.add(makeSymbolsLine(inner, 100, 0, font, yellow, wild, any, 3, true));
		}
		{ // N06 & N05
			Group group = instance.scroll.newRow(316f, 14f);
			
			Group inner = new Group();
			DrawableActor bg = DrawableActor.newInstance(frame);
			bg.setBounds(14f, 0f, 462f, 316f);
			inner.addActor(bg);
			inner.setBounds(0f, 0f, 483f, 316f);
			group.addActor(inner);
			
			instance.labels.add(makeSymbolsLine(inner, 500, 4, font, yellow, n06, any, 5, true));
			instance.labels.add(makeSymbolsLine(inner, 100, 3, font, yellow, n06, any, 4, false));
			instance.labels.add(makeSymbolsLine(inner, 100, 2, font, yellow, n06, any, 4, true));
			instance.labels.add(makeSymbolsLine(inner, 30, 1, font, yellow, n06, any, 3, false));
			instance.labels.add(makeSymbolsLine(inner, 30, 0, font, yellow, n06, any, 3, true));
			
			inner = new Group();
			bg = DrawableActor.newInstance(frame);
			bg.setBounds(14f, 0f, 462f, 316f);
			inner.addActor(bg);
			inner.setBounds(476f, 0f, 483f, 316f);
			group.addActor(inner);
			
			instance.labels.add(makeSymbolsLine(inner, 200, 4, font, yellow, n05, any, 5, true));
			instance.labels.add(makeSymbolsLine(inner, 50, 3, font, yellow, n05, any, 4, false));
			instance.labels.add(makeSymbolsLine(inner, 50, 2, font, yellow, n05, any, 4, true));
			instance.labels.add(makeSymbolsLine(inner, 20, 1, font, yellow, n05, any, 3, false));
			instance.labels.add(makeSymbolsLine(inner, 20, 0, font, yellow, n05, any, 3, true));
		}
		{ // N04 & N03
			Group group = instance.scroll.newRow(316f, 14f);
			
			Group inner = new Group();
			DrawableActor bg = DrawableActor.newInstance(frame);
			bg.setBounds(14f, 0f, 462f, 316f);
			inner.addActor(bg);
			inner.setBounds(0f, 0f, 483f, 316f);
			group.addActor(inner);
			
			instance.labels.add(makeSymbolsLine(inner, 100, 4, font, yellow, n04, any, 5, true));
			instance.labels.add(makeSymbolsLine(inner, 30, 3, font, yellow, n04, any, 4, false));
			instance.labels.add(makeSymbolsLine(inner, 30, 2, font, yellow, n04, any, 4, true));
			instance.labels.add(makeSymbolsLine(inner, 10, 1, font, yellow, n04, any, 3, false));
			instance.labels.add(makeSymbolsLine(inner, 10, 0, font, yellow, n04, any, 3, true));
			
			inner = new Group();
			bg = DrawableActor.newInstance(frame);
			bg.setBounds(14f, 0f, 462f, 316f);
			inner.addActor(bg);
			inner.setBounds(476f, 0f, 483f, 316f);
			group.addActor(inner);
			
			instance.labels.add(makeSymbolsLine(inner, 50, 4, font, yellow, n03, any, 5, true));
			instance.labels.add(makeSymbolsLine(inner, 10, 3, font, yellow, n03, any, 4, false));
			instance.labels.add(makeSymbolsLine(inner, 10, 2, font, yellow, n03, any, 4, true));
			instance.labels.add(makeSymbolsLine(inner, 5, 1, font, yellow, n03, any, 3, false));
			instance.labels.add(makeSymbolsLine(inner, 5, 0, font, yellow, n03, any, 3, true));
		}
		{ // N02 & N01
			Group group = instance.scroll.newRow(316f, 14f);
			
			Group inner = new Group();
			DrawableActor bg = DrawableActor.newInstance(frame);
			bg.setBounds(14f, 0f, 462f, 316f);
			inner.addActor(bg);
			inner.setBounds(0f, 0f, 483f, 316f);
			group.addActor(inner);
			
			instance.labels.add(makeSymbolsLine(inner, 20, 4, font, yellow, n02, any, 5, true));
			instance.labels.add(makeSymbolsLine(inner, 5, 3, font, yellow, n02, any, 4, false));
			instance.labels.add(makeSymbolsLine(inner, 5, 2, font, yellow, n02, any, 4, true));
			instance.labels.add(makeSymbolsLine(inner, 3, 1, font, yellow, n02, any, 3, false));
			instance.labels.add(makeSymbolsLine(inner, 3, 0, font, yellow, n02, any, 3, true));
			
			inner = new Group();
			bg = DrawableActor.newInstance(frame);
			bg.setBounds(14f, 0f, 462f, 316f);
			inner.addActor(bg);
			inner.setBounds(476f, 0f, 483f, 316f);
			group.addActor(inner);
			
			instance.labels.add(makeSymbolsLine(inner, 10, 4, font, yellow, n01, any, 5, true));
			instance.labels.add(makeSymbolsLine(inner, 3, 3, font, yellow, n01, any, 4, false));
			instance.labels.add(makeSymbolsLine(inner, 3, 2, font, yellow, n01, any, 4, true));
			instance.labels.add(makeSymbolsLine(inner, 2, 1, font, yellow, n01, any, 3, false));
			instance.labels.add(makeSymbolsLine(inner, 2, 0, font, yellow, n01, any, 3, true));
		}
		instance.hideNow();
		return instance;
	}

	private static PixelLabel makeSymbolsLine(Group group, int award, int row, BitmapFont font, Color color, TextureRegionDrawable target, TextureRegionDrawable any, int count, boolean left) {
		float offset = (float)row * 60f;
		PixelLabel label = new PixelLabel(String.valueOf(award), new LabelStyle(font, color));
		label.setAlignment(Align.center | Align.right);
		label.setBounds(340f, 9f + offset, 122f, 60f);
		group.addActor(label);
		TableSymbol symbol = TableSymbol.newInstance(null, left ? count > 0 ? target : any : count > 4 ? target : any, 0f, 0f, 1f, 1f);
		symbol.setBounds(20f, 9f + offset, 60f, 60f);
		group.addActor(symbol);
		symbol = TableSymbol.newInstance(null, left ? count > 1 ? target : any : count > 3 ? target : any, 0f, 0f, 1f, 1f);
		symbol.setBounds(80f, 9f + offset, 60f, 60f);
		group.addActor(symbol);
		symbol = TableSymbol.newInstance(null, left ? count > 2 ? target : any : count > 2 ? target : any, 0f, 0f, 1f, 1f);
		symbol.setBounds(140f, 9f + offset, 60f, 60f);
		group.addActor(symbol);
		symbol = TableSymbol.newInstance(null, left ? count > 3 ? target : any : count > 1 ? target : any, 0f, 0f, 1f, 1f);
		symbol.setBounds(200f, 9f + offset, 60f, 60f);
		group.addActor(symbol);
		symbol = TableSymbol.newInstance(null, left ? count > 4 ? target : any : count > 0 ? target : any, 0f, 0f, 1f, 1f);
		symbol.setBounds(260f, 9f + offset, 60f, 60f);
		group.addActor(symbol);
		return label;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		float x = super.getX();
		float y = super.getY();
		float width = super.getWidth();
		float height = super.getHeight();
		//this.background.draw(batch, x, y, width, height);
		batch.draw(this.background, x, y, width, height);
		if (super.state == 3)
			this.shadow.draw(batch, x, y + .96f * height, width, .04f * height);
		super.draw(batch, parentAlpha);
	}

	@Override
	public void resize(int width, int height) {
		this.hide_button.resize(width, height);
		this.title_label.resize(width, height);
		for (PixelLabel label : this.labels) {
			label.resize(width, height);
		}
	}

}
