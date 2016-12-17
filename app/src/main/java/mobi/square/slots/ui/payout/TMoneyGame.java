package mobi.square.slots.ui.payout;

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

import java.util.ArrayList;
import java.util.List;

import mobi.square.slots.api.Connection;
import mobi.square.slots.config.AppConfig;
import mobi.square.slots.tools.FontsFactory;
import mobi.square.slots.ui.GameHeader;
import mobi.square.slots.ui.PixelLabel;
import mobi.square.slots.ui.SettingsWindow.HideButton;

public class TMoneyGame extends TBasic {

    private final Texture background;
    private final TextureRegionDrawable shadow;
    private final TableTitle title_label;
    private final HideButton hide_button;
    private final TableScroll scroll;
    private final List<PixelLabel> labels;

    private TMoneyGame(Texture background, TextureAtlas atlas, TextureAtlas symbols, TextureAtlas windows_atlas) {
        super();
        this.labels = new ArrayList<PixelLabel>();
        this.background = background;
        this.shadow = new TextureRegionDrawable(atlas.findRegion("table_shadow"));
        this.title_label = TableTitle.newInstance(atlas.findRegion("title_background"), new Color(.157f, .149f, .082f, 1f), .05f, .1f, .96f, .9f);
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

    public static TMoneyGame newInstance(Texture background, TextureAtlas atlas, TextureAtlas symbols, TextureAtlas windows_atlas) {
        TMoneyGame instance = new TMoneyGame(background, atlas, symbols, windows_atlas);
        instance.setBounds(0f, 0f, AppConfig.VIEWPORT_WIDTH, AppConfig.VIEWPORT_HEIGHT - GameHeader.HEADER_HEIGHT);
        instance.title_label.setBounds(65f, 400f, 712f, 85f);
        instance.hide_button.setBounds(AppConfig.VIEWPORT_WIDTH - 160f, 412f, 160f, 60f);
        instance.scroll.setBounds(65f, 17f, AppConfig.VIEWPORT_WIDTH - 130f, 360f);
        TextureRegionDrawable symbol_background = new TextureRegionDrawable(atlas.findRegion("symbol_background"));
        TextureRegionDrawable n01 = new TextureRegionDrawable(symbols.findRegion("n01"));
        TextureRegionDrawable n02 = new TextureRegionDrawable(symbols.findRegion("n02"));
        TextureRegionDrawable n03 = new TextureRegionDrawable(symbols.findRegion("n03"));
        TextureRegionDrawable n04 = new TextureRegionDrawable(symbols.findRegion("n04"));
        TextureRegionDrawable n05 = new TextureRegionDrawable(symbols.findRegion("n05"));
        TextureRegionDrawable n06 = new TextureRegionDrawable(symbols.findRegion("n06"));
        TextureRegionDrawable n07 = new TextureRegionDrawable(symbols.findRegion("n07"));
        TextureRegionDrawable n08 = new TextureRegionDrawable(symbols.findRegion("n08"));
        TextureRegionDrawable n09 = new TextureRegionDrawable(symbols.findRegion("n09"));
        TextureRegionDrawable n10 = new TextureRegionDrawable(symbols.findRegion("n10"));
        TextureRegionDrawable n11 = new TextureRegionDrawable(symbols.findRegion("n11"));
        TextureRegionDrawable scatter = new TextureRegionDrawable(symbols.findRegion("scatter"));
        TextureRegionDrawable wild = new TextureRegionDrawable(symbols.findRegion("wild"));
        Color yellow = new Color(1f, 1f, 0f, 1f);
        Color black = Color.BLACK;
        BitmapFont font = FontsFactory.getAsync("Junegull.ttf", 32);
        { // SCATTER & WILD
            Group group = instance.scroll.newRow(180f, 20f);
            // Bonus labels
            group.setBounds(0f, 0f, AppConfig.VIEWPORT_WIDTH - 130f, 360f);
            LabelStyle ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("Junegull.ttf", 30);
            ls.fontColor = yellow;
            PixelLabel label = new PixelLabel("SCATTER", ls);
            label.setBounds(0f, 146f, 140f, 34f);
            label.setAlignment(Align.center);
            group.addActor(label);
            instance.labels.add(label);
            label = new PixelLabel("WILD", ls);
            label.setBounds(440f, 146f, 140f, 34f);
            label.setAlignment(Align.center);
            group.addActor(label);
            instance.labels.add(label);
            // Info labels
            ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("Taurus.ttf", 20);
            ls.fontColor = black;
            label = new PixelLabel(Connection.getString("money_game_scatter"), ls);
            label.setBounds(146f, 0f, 250f, 140f);
            label.setAlignment(Align.top | Align.left);
            group.addActor(label);
            instance.labels.add(label);
            label = new PixelLabel(Connection.getString("money_game_wild"), ls);
            label.setBounds(586f, 0f, 250f, 140f);
            label.setAlignment(Align.top | Align.left);
            group.addActor(label);
            instance.labels.add(label);
            // Symbols
            TableSymbol symbol = newTableSymbol(symbol_background, scatter);
            symbol.setBounds(0f, 0f, 140f, 140f);
            group.addActor(symbol);
            symbol = newTableSymbol(symbol_background, wild);
            symbol.setBounds(440f, 0f, 140f, 140f);
            group.addActor(symbol);
        }
        { // N07
            Group group = instance.scroll.newRow(140f, 20f);
            // Bonus labels
            group.setBounds(0f, 0f, AppConfig.VIEWPORT_WIDTH - 130f, 360f);
            /*LabelStyle ls = new LabelStyle();
			ls.font = FontsFactory.getAsync("Junegull.ttf", 30);
			ls.fontColor = yellow;
			PixelLabel label = new PixelLabel("COIN", ls);
			label.setBounds(0f, 146f, 140f, 34f);
			label.setAlignment(Align.center);
			group.addActor(label);
			instance.labels.add(label);*/
            // Info labels
			/*ls = new LabelStyle();
			ls.font = FontsFactory.getAsync("Taurus.ttf", 20);
			ls.fontColor = black;
			label = new PixelLabel(Connection.getString("money_game_coin"), ls);
			label.setBounds(146f, 0f, 250f, 140f);
			label.setAlignment(Align.top | Align.left);
			group.addActor(label);
			instance.labels.add(label);*/
            LabelStyle ls = new LabelStyle();
            ls.font = font;
            ls.fontColor = black;
            PixelLabel label = new PixelLabel("5 x 250\n4 x 75\n3 x 15", ls);
            label.setBounds(154f, 0f, 250f, 140f);
            label.setAlignment(Align.top | Align.left);
            group.addActor(label);
            instance.labels.add(label);
            // Symbols
			/*TableSymbol symbol = newTableSymbol(symbol_background, n07);
			symbol.setBounds(0f, 0f, 140f, 140f);
			group.addActor(symbol);*/
            TableSymbol symbol = newTableSymbol(symbol_background, n07);
            symbol.setBounds(0f, 0f, 140f, 140f);
            group.addActor(symbol);
        }
        { // SCATTER & WILD
            Group group = instance.scroll.newRow(140f, 40f);
            group.setBounds(0f, 0f, AppConfig.VIEWPORT_WIDTH - 130f, 360f);
            // Info labels
            LabelStyle ls = new LabelStyle();
            ls.font = font;
            ls.fontColor = black;
            PixelLabel label = new PixelLabel("5 x 500\n4 x 20\n3 x 5\n2 x 2", ls);
            label.setBounds(154f, 0f, 250f, 140f);
            label.setAlignment(Align.top | Align.left);
            group.addActor(label);
            instance.labels.add(label);
            label = new PixelLabel("5 x 100\n4 x 50\n3 x 25\n2 x 10", ls);
            label.setBounds(594f, 0f, 250f, 140f);
            label.setAlignment(Align.top | Align.left);
            group.addActor(label);
            instance.labels.add(label);
            // Symbols
            TableSymbol symbol = newTableSymbol(symbol_background, scatter);
            symbol.setBounds(0f, 0f, 140f, 140f);
            group.addActor(symbol);
            symbol = newTableSymbol(symbol_background, wild);
            symbol.setBounds(440f, 0f, 140f, 140f);
            group.addActor(symbol);
        }
        { // N10 & N09
            Group group = instance.scroll.newRow(140f, 20f);
            group.setBounds(0f, 0f, AppConfig.VIEWPORT_WIDTH - 130f, 360f);
            // Info labels
            LabelStyle ls = new LabelStyle();
            ls.font = font;
            ls.fontColor = black;
            PixelLabel label = new PixelLabel("5 x 750\n4 x 125\n3 x 25", ls);
            label.setBounds(154f, 0f, 250f, 140f);
            label.setAlignment(Align.top | Align.left);
            group.addActor(label);
            instance.labels.add(label);
            label = new PixelLabel("5 x 400\n4 x 100\n3 x 20", ls);
            label.setBounds(594f, 0f, 250f, 140f);
            label.setAlignment(Align.top | Align.left);
            group.addActor(label);
            instance.labels.add(label);
            // Symbols
            TableSymbol symbol = newTableSymbol(symbol_background, n10);
            symbol.setBounds(0f, 0f, 140f, 140f);
            group.addActor(symbol);
            symbol = newTableSymbol(symbol_background, n09);
            symbol.setBounds(440f, 0f, 140f, 140f);
            group.addActor(symbol);
        }
        { // N11 & N08
            Group group = instance.scroll.newRow(140f, 20f);
            group.setBounds(0f, 0f, AppConfig.VIEWPORT_WIDTH - 130f, 360f);
            // Info labels
            LabelStyle ls = new LabelStyle();
            ls.font = font;
            ls.fontColor = black;
            PixelLabel label = new PixelLabel("5 x 750\n4 x 125\n3 x 25", ls);
            label.setBounds(154f, 0f, 250f, 140f);
            label.setAlignment(Align.top | Align.left);
            group.addActor(label);
            instance.labels.add(label);
            label = new PixelLabel("5 x 250\n4 x 75\n3 x 15", ls);
            label.setBounds(594f, 0f, 250f, 140f);
            label.setAlignment(Align.top | Align.left);
            group.addActor(label);
            instance.labels.add(label);
            // Symbols
            TableSymbol symbol = newTableSymbol(symbol_background, n11);
            symbol.setBounds(0f, 0f, 140f, 140f);
            group.addActor(symbol);
            symbol = newTableSymbol(symbol_background, n08);
            symbol.setBounds(440f, 0f, 140f, 140f);
            group.addActor(symbol);
        }
        { // N06 & N05
            Group group = instance.scroll.newRow(140f, 20f);
            group.setBounds(0f, 0f, AppConfig.VIEWPORT_WIDTH - 130f, 360f);
            // Info labels
            LabelStyle ls = new LabelStyle();
            ls.font = font;
            ls.fontColor = black;
            PixelLabel label = new PixelLabel("5 x 125\n4 x 50\n3 x 10", ls);
            label.setBounds(154f, 0f, 250f, 140f);
            label.setAlignment(Align.top | Align.left);
            group.addActor(label);
            instance.labels.add(label);
            label = new PixelLabel("5 x 125\n4 x 50\n3 x 10", ls);
            label.setBounds(594f, 0f, 250f, 140f);
            label.setAlignment(Align.top | Align.left);
            group.addActor(label);
            instance.labels.add(label);
            // Symbols
            TableSymbol symbol = newTableSymbol(symbol_background, n06);
            symbol.setBounds(0f, 0f, 140f, 140f);
            group.addActor(symbol);
            symbol = newTableSymbol(symbol_background, n05);
            symbol.setBounds(440f, 0f, 140f, 140f);
            group.addActor(symbol);
        }
        { // N04 & N03
            Group group = instance.scroll.newRow(140f, 20f);
            group.setBounds(0f, 0f, AppConfig.VIEWPORT_WIDTH - 130f, 360f);
            // Info labels
            LabelStyle ls = new LabelStyle();
            ls.font = font;
            ls.fontColor = black;
            PixelLabel label = new PixelLabel("5 x 100\n4 x 25\n3 x 5", ls);
            label.setBounds(154f, 0f, 250f, 140f);
            label.setAlignment(Align.top | Align.left);
            group.addActor(label);
            instance.labels.add(label);
            label = new PixelLabel("5 x 100\n4 x 25\n3 x 5", ls);
            label.setBounds(594f, 0f, 250f, 140f);
            label.setAlignment(Align.top | Align.left);
            group.addActor(label);
            instance.labels.add(label);
            // Symbols
            TableSymbol symbol = newTableSymbol(symbol_background, n04);
            symbol.setBounds(0f, 0f, 140f, 140f);
            group.addActor(symbol);
            symbol = newTableSymbol(symbol_background, n03);
            symbol.setBounds(440f, 0f, 140f, 140f);
            group.addActor(symbol);
        }
        { // N02 & N01
            Group group = instance.scroll.newRow(140f, 20f);
            group.setBounds(0f, 0f, AppConfig.VIEWPORT_WIDTH - 130f, 360f);
            // Info labels
            LabelStyle ls = new LabelStyle();
            ls.font = font;
            ls.fontColor = black;
            PixelLabel label = new PixelLabel("5 x 100\n4 x 25\n3 x 5", ls);
            label.setBounds(154f, 0f, 250f, 140f);
            label.setAlignment(Align.top | Align.left);
            group.addActor(label);
            instance.labels.add(label);
            label = new PixelLabel("5 x 100\n4 x 25\n3 x 5", ls);
            label.setBounds(594f, 0f, 250f, 140f);
            label.setAlignment(Align.top | Align.left);
            group.addActor(label);
            instance.labels.add(label);
            // Symbols
            TableSymbol symbol = newTableSymbol(symbol_background, n02);
            symbol.setBounds(0f, 0f, 140f, 140f);
            group.addActor(symbol);
            symbol = newTableSymbol(symbol_background, n01);
            symbol.setBounds(440f, 0f, 140f, 140f);
            group.addActor(symbol);
        }
        instance.hideNow();
        return instance;
    }

    private static TableSymbol newTableSymbol(TextureRegionDrawable background, TextureRegionDrawable symbol) {
        return TableSymbol.newInstance(background, symbol, .07f, .085f, .86f, .85f);
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
