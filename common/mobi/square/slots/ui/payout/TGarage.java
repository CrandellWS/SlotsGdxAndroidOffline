package mobi.square.slots.ui.payout;

import java.util.ArrayList;
import java.util.List;

import mobi.square.slots.api.Connection;
import mobi.square.slots.config.AppConfig;
import mobi.square.slots.tools.FontsFactory;
import mobi.square.slots.ui.DrawableActor;
import mobi.square.slots.ui.GameHeader;
import mobi.square.slots.ui.PixelLabel;
import mobi.square.slots.ui.SettingsWindow.HideButton;

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

public class TGarage extends TBasic {

	private final Texture background;
	private final TextureRegionDrawable shadow;
	private final TableTitle title_label;
	private final HideButton hide_button;
	private final TableScroll scroll;

	private final List<PixelLabel> labels;

	private TGarage(Texture background, TextureAtlas atlas, TextureAtlas symbols, TextureAtlas windows_atlas) {
		super();
		this.labels = new ArrayList<PixelLabel>();
		this.background = background;
		this.shadow = new TextureRegionDrawable(atlas.findRegion("table_shadow"));
		this.title_label = TableTitle.newInstance(atlas.findRegion("title_background"), new Color(.886f, .392f, .027f, 1f), .04f, .1f, .96f, .9f);
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

	public static TGarage newInstance(Texture background, TextureAtlas atlas, TextureAtlas symbols, TextureAtlas windows_atlas) {
		TGarage instance = new TGarage(background, atlas, symbols, windows_atlas);
		instance.setBounds(0f, 0f, AppConfig.VIEWPORT_WIDTH, AppConfig.VIEWPORT_HEIGHT - GameHeader.HEADER_HEIGHT);
		instance.title_label.setBounds(65f, 400f, 744f, 86f);
		instance.hide_button.setBounds(AppConfig.VIEWPORT_WIDTH - 160f, 412f, 160f, 60f);
		instance.scroll.setBounds(65f, 20f, AppConfig.VIEWPORT_WIDTH - 130f, 360f);
		TextureRegionDrawable symbol_background = new TextureRegionDrawable(atlas.findRegion("symbol_background"));
		TextureRegionDrawable n01 = new TextureRegionDrawable(symbols.findRegion("n01"));
		TextureRegionDrawable n02 = new TextureRegionDrawable(symbols.findRegion("n02"));
		TextureRegionDrawable n03 = new TextureRegionDrawable(symbols.findRegion("n03"));
		TextureRegionDrawable n04 = new TextureRegionDrawable(symbols.findRegion("n04"));
		TextureRegionDrawable n05 = new TextureRegionDrawable(symbols.findRegion("n05"));
		TextureRegionDrawable n06 = new TextureRegionDrawable(symbols.findRegion("n06"));
		TextureRegionDrawable n07 = new TextureRegionDrawable(symbols.findRegion("n07"));
		TextureRegionDrawable n08 = new TextureRegionDrawable(symbols.findRegion("n08"));
		TextureRegionDrawable wild = new TextureRegionDrawable(symbols.findRegion("wild"));
		TextureRegionDrawable any = new TextureRegionDrawable(atlas.findRegion("symbol_any"));
		TextureRegionDrawable frame = new TextureRegionDrawable(atlas.findRegion("orange_frame"));
		Color green = new Color(.368f, .816f, .177f, 1f);
		Color yellow = new Color(.976f, .827f, .302f, 1f);
		BitmapFont font = FontsFactory.getAsync("Junegull.ttf", 36);
		{ // Bonus symbols
			Group group = instance.scroll.newRow(180f, 20f);
			// Bonus labels
			group.setBounds(0f, 0f, AppConfig.VIEWPORT_WIDTH - 130f, 360f);
			LabelStyle ls = new LabelStyle();
			ls.font = FontsFactory.getAsync("Junegull.ttf", 30);
			ls.fontColor = green;
			PixelLabel label = new PixelLabel("BONUS", ls);
			label.setBounds(0f, 146f, 140f, 34f);
			label.setAlignment(Align.center);
			group.addActor(label);
			instance.labels.add(label);
			label = new PixelLabel("BONUS", ls);
			label.setBounds(440f, 146f, 140f, 34f);
			label.setAlignment(Align.center);
			group.addActor(label);
			instance.labels.add(label);
			// Info labels
			ls = new LabelStyle();
			ls.font = FontsFactory.getAsync("Taurus.ttf", 20);
			ls.fontColor = yellow;
			label = new PixelLabel(Connection.getString("garage_bonus_1"), ls);
			label.setBounds(146f, 0f, 250f, 140f);
			label.setAlignment(Align.top | Align.left);
			group.addActor(label);
			instance.labels.add(label);
			label = new PixelLabel(Connection.getString("garage_bonus_2"), ls);
			label.setBounds(586f, 0f, 250f, 140f);
			label.setAlignment(Align.top | Align.left);
			group.addActor(label);
			instance.labels.add(label);
			// Symbols
			TableSymbol symbol = TableSymbol.newInstance(symbol_background, n08);
			symbol.setBounds(0f, 0f, 140f, 140f);
			group.addActor(symbol);
			symbol = TableSymbol.newInstance(symbol_background, n07);
			symbol.setBounds(440f, 0f, 140f, 140f);
			group.addActor(symbol);
		}
		{ // Wild symbol
			Group group = instance.scroll.newRow(140f, 20f);
			TextureRegionDrawable arrow_bg = new TextureRegionDrawable(atlas.findRegion("table_arrow"));
			DrawableActor arrow = DrawableActor.newInstance(arrow_bg);
			arrow.setBounds(153f, 10f, 74f, 51f);
			group.addActor(arrow);
			arrow = DrawableActor.newInstance(arrow_bg);
			arrow.setBounds(153f, 79f, 74f, 51f);
			group.addActor(arrow);
			TableSymbol symbol = TableSymbol.newInstance(symbol_background, wild);
			symbol.setBounds(0f, 0f, 140f, 140f);
			group.addActor(symbol);
			symbol = TableSymbol.newInstance(symbol_background, n01);
			symbol.setBounds(240f, 10f, 120f, 120f);
			group.addActor(symbol);
			symbol = TableSymbol.newInstance(symbol_background, n02);
			symbol.setBounds(360f, 10f, 120f, 120f);
			group.addActor(symbol);
			symbol = TableSymbol.newInstance(symbol_background, n03);
			symbol.setBounds(480f, 10f, 120f, 120f);
			group.addActor(symbol);
			symbol = TableSymbol.newInstance(symbol_background, n04);
			symbol.setBounds(600f, 10f, 120f, 120f);
			group.addActor(symbol);
			symbol = TableSymbol.newInstance(symbol_background, n05);
			symbol.setBounds(720f, 10f, 120f, 120f);
			group.addActor(symbol);
		}
		{ // N01
			instance.labels.add(makeSymbolsLine(instance.scroll.newRow(150f, 0f), "x10", font, green, frame, symbol_background, n01, any, 5, true));
			instance.labels.add(makeSymbolsLine(instance.scroll.newRow(150f, 0f), "x3", font, green, frame, symbol_background, n01, any, 4, true));
			instance.labels.add(makeSymbolsLine(instance.scroll.newRow(150f, 0f), "x3", font, green, frame, symbol_background, n01, any, 4, false));
			instance.labels.add(makeSymbolsLine(instance.scroll.newRow(150f, 0f), "x2", font, green, frame, symbol_background, n01, any, 3, true));
			instance.labels.add(makeSymbolsLine(instance.scroll.newRow(150f, 20f), "x2", font, green, frame, symbol_background, n01, any, 3, false));
		}
		{ // N02
			instance.labels.add(makeSymbolsLine(instance.scroll.newRow(150f, 0f), "x20", font, green, frame, symbol_background, n02, any, 5, true));
			instance.labels.add(makeSymbolsLine(instance.scroll.newRow(150f, 0f), "x5", font, green, frame, symbol_background, n02, any, 4, true));
			instance.labels.add(makeSymbolsLine(instance.scroll.newRow(150f, 0f), "x5", font, green, frame, symbol_background, n02, any, 4, false));
			instance.labels.add(makeSymbolsLine(instance.scroll.newRow(150f, 0f), "x3", font, green, frame, symbol_background, n02, any, 3, true));
			instance.labels.add(makeSymbolsLine(instance.scroll.newRow(150f, 20f), "x3", font, green, frame, symbol_background, n02, any, 3, false));
		}
		{ // N03
			instance.labels.add(makeSymbolsLine(instance.scroll.newRow(150f, 0f), "x50", font, green, frame, symbol_background, n03, any, 5, true));
			instance.labels.add(makeSymbolsLine(instance.scroll.newRow(150f, 0f), "x10", font, green, frame, symbol_background, n03, any, 4, true));
			instance.labels.add(makeSymbolsLine(instance.scroll.newRow(150f, 0f), "x10", font, green, frame, symbol_background, n03, any, 4, false));
			instance.labels.add(makeSymbolsLine(instance.scroll.newRow(150f, 0f), "x5", font, green, frame, symbol_background, n03, any, 3, true));
			instance.labels.add(makeSymbolsLine(instance.scroll.newRow(150f, 20f), "x5", font, green, frame, symbol_background, n03, any, 3, false));
		}
		{ // N04
			instance.labels.add(makeSymbolsLine(instance.scroll.newRow(150f, 0f), "x100", font, green, frame, symbol_background, n04, any, 5, true));
			instance.labels.add(makeSymbolsLine(instance.scroll.newRow(150f, 0f), "x30", font, green, frame, symbol_background, n04, any, 4, true));
			instance.labels.add(makeSymbolsLine(instance.scroll.newRow(150f, 0f), "x30", font, green, frame, symbol_background, n04, any, 4, false));
			instance.labels.add(makeSymbolsLine(instance.scroll.newRow(150f, 0f), "x10", font, green, frame, symbol_background, n04, any, 3, true));
			instance.labels.add(makeSymbolsLine(instance.scroll.newRow(150f, 20f), "x10", font, green, frame, symbol_background, n04, any, 3, false));
		}
		{ // N05
			instance.labels.add(makeSymbolsLine(instance.scroll.newRow(150f, 0f), "x200", font, green, frame, symbol_background, n05, any, 5, true));
			instance.labels.add(makeSymbolsLine(instance.scroll.newRow(150f, 0f), "x50", font, green, frame, symbol_background, n05, any, 4, true));
			instance.labels.add(makeSymbolsLine(instance.scroll.newRow(150f, 0f), "x50", font, green, frame, symbol_background, n05, any, 4, false));
			instance.labels.add(makeSymbolsLine(instance.scroll.newRow(150f, 0f), "x20", font, green, frame, symbol_background, n05, any, 3, true));
			instance.labels.add(makeSymbolsLine(instance.scroll.newRow(150f, 20f), "x20", font, green, frame, symbol_background, n05, any, 3, false));
		}
		{ // N06
			instance.labels.add(makeSymbolsLine(instance.scroll.newRow(150f, 0f), "x5000", font, green, frame, symbol_background, n06, any, 5, true));
			instance.labels.add(makeSymbolsLine(instance.scroll.newRow(150f, 0f), "x500", font, green, frame, symbol_background, n06, any, 4, true));
			instance.labels.add(makeSymbolsLine(instance.scroll.newRow(150f, 0f), "x500", font, green, frame, symbol_background, n06, any, 4, false));
			instance.labels.add(makeSymbolsLine(instance.scroll.newRow(150f, 0f), "x100", font, green, frame, symbol_background, n06, any, 3, true));
			instance.labels.add(makeSymbolsLine(instance.scroll.newRow(150f, 20f), "x100", font, green, frame, symbol_background, n06, any, 3, false));
		}
		{ // WILD
			instance.labels.add(makeSymbolsLine(instance.scroll.newRow(150f, 0f), "x1000", font, green, frame, symbol_background, wild, any, 5, true));
			instance.labels.add(makeSymbolsLine(instance.scroll.newRow(150f, 0f), "x200", font, green, frame, symbol_background, wild, any, 4, true));
			instance.labels.add(makeSymbolsLine(instance.scroll.newRow(150f, 0f), "x200", font, green, frame, symbol_background, wild, any, 4, false));
			instance.labels.add(makeSymbolsLine(instance.scroll.newRow(150f, 0f), "x50", font, green, frame, symbol_background, wild, any, 3, true));
			instance.labels.add(makeSymbolsLine(instance.scroll.newRow(150f, 20f), "x50", font, green, frame, symbol_background, wild, any, 3, false));
		}
		instance.hideNow();
		return instance;
	}

	private static PixelLabel makeSymbolsLine(Group group, String text, BitmapFont font, Color color, TextureRegionDrawable frame, TextureRegionDrawable background, TextureRegionDrawable target, TextureRegionDrawable any, int count, boolean left) {
		PixelLabel label = new PixelLabel(Connection.getString("payout_table_bet").concat("\n").concat(text), new LabelStyle(font, color));
		label.setBounds(726f, 0f, 160f, 150f);
		group.addActor(label);
		DrawableActor frame_actor = DrawableActor.newInstance(frame);
		frame_actor.setBounds(0f, 0f, 710f, 150f);
		group.addActor(frame_actor);
		TableSymbol symbol = TableSymbol.newInstance(background, left ? count > 0 ? target : any : count > 4 ? target : any);
		symbol.setBounds(5f, 5f, 140f, 140f);
		group.addActor(symbol);
		symbol = TableSymbol.newInstance(background, left ? count > 1 ? target : any : count > 3 ? target : any);
		symbol.setBounds(145f, 5f, 140f, 140f);
		group.addActor(symbol);
		symbol = TableSymbol.newInstance(background, left ? count > 2 ? target : any : count > 2 ? target : any);
		symbol.setBounds(285f, 5f, 140f, 140f);
		group.addActor(symbol);
		symbol = TableSymbol.newInstance(background, left ? count > 3 ? target : any : count > 1 ? target : any);
		symbol.setBounds(425f, 5f, 140f, 140f);
		group.addActor(symbol);
		symbol = TableSymbol.newInstance(background, left ? count > 4 ? target : any : count > 0 ? target : any);
		symbol.setBounds(565f, 5f, 140f, 140f);
		group.addActor(symbol);
		return label;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		float x = super.getX();
		float y = super.getY();
		float width = super.getWidth();
		float height = super.getHeight();
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
