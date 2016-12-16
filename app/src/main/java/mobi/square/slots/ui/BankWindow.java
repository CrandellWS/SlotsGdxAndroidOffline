package mobi.square.slots.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import mobi.square.slots.api.Connection;
import mobi.square.slots.containers.BankInfo;
import mobi.square.slots.tools.FontsFactory;
import mobi.square.slots.ui.MessageWindow.CloseButton;
import mobi.square.slots.utils.utils;

public class BankWindow extends Window implements Resizable {

    private final TextureRegionDrawable shadow_top;
    private final TextureRegionDrawable shadow_bottom;
    private final CloseButton close_button;
    private final CloseButton enterCode;
    private final CloseButton GVC;
    private final PixelLabel title_label;
    private final BankScroll scroll;
    private final LoadingIcon loading;

    private BankLoadingHandler handler;
    private boolean loaded;

    private BankWindow(WindowStyle style, TextureAtlas loading_atlas, TextureAtlas atlas) {
        super("", style);
        AtlasRegion shadow = atlas.findRegion("bank_shadow");
        this.shadow_top = new TextureRegionDrawable(shadow);
        shadow = new AtlasRegion(shadow);
        shadow.flip(false, true);
        this.shadow_bottom = new TextureRegionDrawable(shadow);
        this.loading = LoadingIcon.newInstance(loading_atlas);
        this.close_button = CloseButton.newInstance(atlas);
        this.enterCode = CloseButton.newInstance(atlas);
        this.GVC = CloseButton.newInstance(atlas);

        this.enterCode.setVisible(Connection.getWrapper().checkShowButton());

        this.close_button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (close_button.isDisabled()) return;
                hide();
            }
        });

        this.GVC.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (GVC.isDisabled()) return;
                Connection.getWrapper().gotoCGV();
            }
        });

        this.enterCode.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("esta presionando2");
                if (enterCode.isDisabled()) return;
                hide();
                Connection.getWrapper().startEnterCode();
            }
        });
        super.addActor(this.close_button);
        super.addActor(this.enterCode);
        super.addActor(this.GVC);

        LabelStyle ls = new LabelStyle();
        ls.font = FontsFactory.getAsync("Junegull.ttf", 30);
        ls.fontColor = Color.WHITE;
        this.title_label = new PixelLabel(Connection.getString("bank_window_title"), ls);
        super.addActor(this.title_label);
        this.scroll = BankScroll.newInstance();
        super.addActor(this.scroll);
        super.addActor(this.loading);
        this.handler = null;
        this.loaded = false;
    }

    public static BankWindow newInstance(TextureAtlas loading_atlas, TextureAtlas atlas) {
        WindowStyle style = new WindowStyle();
        style.background = new TextureRegionDrawable(atlas.findRegion("bank_background"));
        style.titleFont = FontsFactory.getAsync("Junegull.ttf", 30);
        style.titleFontColor = Color.WHITE;
        BankWindow instance = new BankWindow(style, loading_atlas, atlas);
        instance.setBounds(94f, 10f, 828f, 502f);
        instance.setVisible(false);
        instance.setMovable(false);
        instance.setModal(true);
        instance.close_button.setTextClose();
        instance.close_button.setBounds(654f, 424f, 160f, 66f);

        instance.enterCode.setText("Saisir code");
        instance.enterCode.setBounds(354f, 424f, 160f, 66f);

        instance.GVC.setText("GVC");
        instance.GVC.setBounds(504f, 424f, 160f, 66f);

        instance.title_label.setBounds(50f, 424, 350f, 70f);//500f, 70f);
        instance.scroll.setBounds(16f, 22f, 801f, 400f);
        instance.loading.setBounds(16f, 160f, 800f, 140f);
        instance.loading.setVisible(true);
        return instance;
    }

    public void show(BankLoadingHandler handler) {
        this.handler = handler;
        this.loading.setVisible(true);
        this.loaded = false;
        super.setVisible(true);
    }

    public void hide() {
        this.handler = null;
        super.setVisible(false);
        this.scroll.clearList();
    }

    @Override
    public void act(float delta) {
        if (!this.loaded) {
            if (this.handler != null) {
                if (this.handler.isLoaded()) {
                    this.scroll.fillList(this.handler.items);
                    this.loading.setVisible(false);
                    this.handler = null;
                    this.loaded = true;
                }
            }
        }
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (super.isVisible()) {
            float x = super.getX();
            float y = super.getY();
            float width = super.getWidth();
            float height = super.getHeight();
            this.shadow_bottom.draw(batch, x + .02f * width, y + .037f * height, .968f * width, .06f * height);
            this.shadow_top.draw(batch, x + .02f * width, y + .79f * height, .968f * width, .06f * height);
        }
    }

    @Override
    public void resize(int width, int height) {
        this.title_label.resize(width, height);
        this.close_button.resize(width, height);
        this.enterCode.resize(width, height);
        this.GVC.resize(width, height);
        this.scroll.resize(width, height);
    }

    public static class BankLoadingHandler {
        private BankInfo[] items;

        public BankLoadingHandler() {
            this.items = null;
        }

        public BankInfo[] getItems() {
            return this.items;
        }

        public void setItems(BankInfo[] items) {
            this.items = items;
        }

        public boolean isLoaded() {
            return this.items != null;
        }
    }

    private static class BankScroll extends ScrollPane {
        private final Table table;
        private final List<BankItem> list;

        private BankScroll(Table table) {
            super(table);
            this.table = table;
            this.list = new LinkedList<BankItem>();
        }

        public static BankScroll newInstance() {
            Table table = new Table();
            BankScroll instance = new BankScroll(table);
            instance.setScrollingDisabled(true, false);
            instance.setFadeScrollBars(false);
            return instance;
        }

        public void clearList() {
            this.table.clear();
            this.table.invalidate();
            this.list.clear();
        }

        public void fillList(BankInfo[] items) {
            this.clearList();
            TextureAtlas atlas = Connection.getManager().get("atlas/Windows.pack", TextureAtlas.class);
            for (int i = 0; i < items.length; i++) {
                if (i > 0) this.table.row();
                String color;
                boolean single;
                if (i == 0) {
                    color = "orange";
                    single = false;
                } else if (i == 1) {
                    color = "yellow";
                    single = false;
                } else if (i == 2) {
                    color = "green";
                    single = false;
                } else if (i == 3) {
                    color = "blue";
                    single = true;
                } else {
                    color = "blue";
                    single = true;
                }
                BankItem item = BankItem.newInstance(atlas, items[i], color, single);
                Cell<?> cell = this.table.add(item);
                cell.size(item.getWidth(), item.getHeight());
                if (i == 0) cell.padTop(8f);
                cell.padBottom(4f);
                this.list.add(item);
            }
            this.table.invalidate();
            this.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }

        public void resize(int width, int height) {
            for (BankItem item : this.list) {
                item.resize(width, height);
            }
            this.table.invalidate();
            super.invalidate();
        }
    }

    private static class BankItem extends Group {
        private final TextureRegionDrawable background;
        private final AmountFrame amount;
        private final SaleLabel sale_frame;
        private final BuyButton buy_button;
        private final String sku;

        private BankItem(TextureAtlas atlas, String color, boolean single, String id) {
            this.background = new TextureRegionDrawable(atlas.findRegion("item_background"));
            this.amount = AmountFrame.newInstance(atlas, color, single);
            this.sale_frame = SaleLabel.newInstance(atlas);
            this.buy_button = BuyButton.newInstance(atlas);
            super.addActor(this.sale_frame);
            super.addActor(this.buy_button);
            super.addActor(this.amount);
            this.sku = id;
            this.buy_button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    System.out.println("is purchasing" + sku);
                    System.out.println("the amount " + amount);
                    utils.idPurchase = Integer.parseInt(sku);
                    String msg = "Get & coins for %";
                    Connection.getWrapper().showMessagePIXTEL(msg, Integer.valueOf(sku));

                }
            });
        }

        public static BankItem newInstance(TextureAtlas atlas, BankInfo info, String color, boolean single) {
            BankItem instance = new BankItem(atlas, color, single, info.getId());
            instance.setBounds(0f, 0f, 801f, 82f);
            instance.amount.setAmount(info.getGold(), info.getBonusGold());
            instance.sale_frame.setPercent(0);//info.getDiscount());
            instance.buy_button.setPrice(info.getPriceString());
            return instance;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            this.background.draw(batch, super.getX(), super.getY(), super.getWidth(), super.getHeight());
            super.draw(batch, parentAlpha);
        }

        public void resize(int width, int height) {
            this.amount.resize(width, height);
            this.sale_frame.resize(width, height);
            this.buy_button.resize(width, height);
        }
    }

    private static class AmountFrame extends Group implements Resizable {
        private static final Map<String, Color> COLORS;

        static {
            COLORS = new HashMap<String, Color>();
            COLORS.put("blue", new Color(0f, 0f, .294f, 1f));
            COLORS.put("green", new Color(0f, .157f, .063f, 1f));
            COLORS.put("orange", new Color(.094f, .020f, 0f, 1f));
            COLORS.put("yellow", new Color(.161f, .098f, 0f, 1f));

        }

        private final TextureRegionDrawable background;
        private final TextureRegionDrawable chips;
        private final PixelLabel top_label;
        private final PixelLabel bottom_label;
        private final PixelLabel middle_label;
        private final PixelLabel plus_label;

        private AmountFrame(TextureAtlas atlas, String color, boolean single) {
            this.background = new TextureRegionDrawable(atlas.findRegion("item_price_".concat(color)));
            this.chips = new TextureRegionDrawable(atlas.findRegion(single ? "chips_single" : "chips_many"));
            LabelStyle ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("Junegull.ttf", 24);
            ls.fontColor = COLORS.get(color);
            this.top_label = new PixelLabel("", ls);
            this.top_label.setAlignment(Align.center, Align.center);
            ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("Junegull.ttf", 24);
            ls.fontColor = COLORS.get(color);
            this.bottom_label = new PixelLabel("", ls);
            this.bottom_label.setAlignment(Align.center, Align.center);
            ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("Junegull.ttf", 24);
            ls.fontColor = COLORS.get(color);
            this.middle_label = new PixelLabel("", ls);
            this.middle_label.setAlignment(Align.center, Align.center);
            ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("Junegull.ttf", 24);
            ls.fontColor = COLORS.get(color);
            this.plus_label = new PixelLabel("+", ls);
            this.plus_label.setAlignment(Align.left, Align.center);
            super.addActor(this.middle_label);
            super.addActor(this.bottom_label);
            super.addActor(this.plus_label);
            super.addActor(this.top_label);
        }

        public static AmountFrame newInstance(TextureAtlas atlas, String color, boolean single) {
            AmountFrame instance = new AmountFrame(atlas, color, single);
            instance.setBounds(3f, 6f, 340f, 76f);
            return instance;
        }

        public void setAmount(int amount, int bonus) {
            if (bonus > 0) {
                this.top_label.setText(String.valueOf(amount));
                this.top_label.setVisible(true);
                this.bottom_label.setText(String.valueOf(bonus));
                this.bottom_label.setVisible(true);
                this.middle_label.setVisible(false);
                this.plus_label.setVisible(true);
            } else {
                this.top_label.setVisible(false);
                this.plus_label.setVisible(false);
                this.bottom_label.setVisible(false);
                this.middle_label.setText(String.valueOf(amount));
                this.middle_label.setVisible(true);
            }
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            float x = super.getX();
            float y = super.getY();
            float width = super.getWidth();
            float height = super.getHeight();
            this.background.draw(batch, x, y, width, height);
            this.chips.draw(batch, .11f * width, .1f * height, .33f * width, 1.1f * height);
            super.draw(batch, parentAlpha);
        }

        @Override
        public void setBounds(float x, float y, float width, float height) {
            super.setBounds(x, y, width, height);
            this.top_label.setBounds(.4f * width, .5f * height, .6f * width, .4f * height);
            this.bottom_label.setBounds(.4f * width, .1f * height, .6f * width, .4f * height);
            this.middle_label.setBounds(.4f * width, 0f, .6f * width, height);
            this.plus_label.setBounds(.5f * width, 0f, .5f * width, height);
        }

        @Override
        public void resize(int width, int height) {
            this.top_label.resize(width, height);
            this.plus_label.resize(width, height);
            this.middle_label.resize(width, height);
            this.bottom_label.resize(width, height);
        }
    }

    private static class SaleLabel extends Group implements Resizable {
        private final TextureRegionDrawable background;
        private final PixelLabel percent_label;
        private final PixelLabel free_label;

        private SaleLabel(TextureAtlas atlas) {
            super();
            this.background = new TextureRegionDrawable(atlas.findRegion("sale_frame"));
            LabelStyle ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("Junegull.ttf", 24);
            ls.fontColor = Color.WHITE;
            this.percent_label = new PixelLabel("", ls);
            this.percent_label.setAlignment(Align.bottom | Align.center, Align.center);
            ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("Junegull.ttf", 18);
            ls.fontColor = Color.WHITE;
            this.free_label = new PixelLabel(Connection.getString("bank_purchase_for_free"), ls);
            this.free_label.setAlignment(Align.center, Align.center);
            super.addActor(this.percent_label);
            super.addActor(this.free_label);
        }

        public static SaleLabel newInstance(TextureAtlas atlas) {
            SaleLabel instance = new SaleLabel(atlas);
            instance.setBounds(450f, 12f, 160f, 60f);
            return instance;
        }

        public void setPercent(int percent) {
            this.percent_label.setText("+".concat(String.valueOf(percent)).concat("%"));
            super.setVisible(percent > 0);
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            this.background.draw(batch, super.getX(), super.getY(), super.getWidth(), super.getHeight());
            super.draw(batch, parentAlpha);
        }

        @Override
        public void setBounds(float x, float y, float width, float height) {
            super.setBounds(x, y, width, height);
            this.percent_label.setBounds(0f, .44f * height, width, .4f * height);
            this.free_label.setBounds(0f, .12f * height, width, .4f * height);
        }

        @Override
        public void resize(int width, int height) {
            this.percent_label.resize(width, height);
            this.free_label.resize(width, height);
        }
    }

    private static class BuyButton extends Button implements Resizable {
        private final PixelLabel label;

        private BuyButton(ButtonStyle style) {
            super(style);
            LabelStyle ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("Junegull.ttf", 30);
            ls.fontColor = new Color(0f, .157f, .075f, 1f);
            this.label = new PixelLabel("", ls);
            this.label.setAlignment(Align.center, Align.center);
            super.addActor(this.label);
        }

        public static BuyButton newInstance(TextureAtlas atlas) {
            ButtonStyle style = new ButtonStyle();
            style.up = new TextureRegionDrawable(atlas.findRegion("button_buy_active"));
            style.down = new TextureRegionDrawable(atlas.findRegion("button_buy_pressed"));
            style.disabled = new TextureRegionDrawable(atlas.findRegion("button_buy_inactive"));
            style.pressedOffsetY = -1f;
            BuyButton instance = new BuyButton(style);
            instance.setBounds(594f, 8f, 200f, 70f);
            return instance;
        }

        public void setPrice(String price) {
            //price = price.replace(",00", "");
            //price = price.replace(".00", "");
            this.label.setText(price);
        }

        @Override
        public void setBounds(float x, float y, float width, float height) {
            super.setBounds(x, y, width, height);
            this.label.setBounds(0f, 0f, width, height);
        }

        @Override
        public void resize(int width, int height) {
            this.label.resize(width, height);
        }
    }

}
