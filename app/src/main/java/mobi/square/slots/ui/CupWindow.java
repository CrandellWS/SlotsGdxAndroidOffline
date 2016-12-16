package mobi.square.slots.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import mobi.square.slots.api.Connection;
import mobi.square.slots.config.AppConfig;
import mobi.square.slots.tools.FontsFactory;

public class CupWindow extends Window implements Resizable {

    private final CupButton close_button;
    private final CupButton action_button;
    private final TimerLabel timer_label;
    private final TimerFrame timer_frame;
    private final PriceFrame price_frame;
    private final InfoButton rules_button;
    private final InfoButton top_button;
    private final PixelLabel title_label;
    private final PixelLabel price_label;
    private final PixelLabel top_label;
    private CupWindowListener listener;

    private CupWindow(WindowStyle style, TextureAtlas atlas) {
        super("", style);
        this.listener = null;
        { // Labels
            LabelStyle ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("Junegull.ttf", 48);
            ls.fontColor = new Color(.13f, .11f, .09f, 1f);
            this.title_label = new PixelLabel(Connection.getString("cup_title"), ls);
            this.title_label.setAlignment(Align.center);
            super.addActor(this.title_label);
            ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("Junegull.ttf", 24);
            ls.fontColor = new Color(1f, .84f, 0f, 1f);
            this.price_label = new PixelLabel(Connection.getString("cup_price"), ls);
            this.price_label.setAlignment(Align.right);
            super.addActor(this.price_label);
            ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("Junegull.ttf", 24);
            ls.fontColor = Color.WHITE;
            this.top_label = new PixelLabel(Connection.getString("cup_leaders"), ls);
            this.top_label.setAlignment(Align.right);
            super.addActor(this.top_label);
        }
        this.close_button = CupButton.newInstance(atlas, "orange");
        this.close_button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (close_button.isDisabled()) return;
                hide();
            }
        });
        super.addActor(this.close_button);
        this.action_button = CupButton.newInstance(atlas, "blue");
        this.action_button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (action_button.isDisabled()) return;
                if (CupWindow.this.listener != null) {
                    CupWindow.this.listener.actionClicked();
                }
            }
        });
        super.addActor(this.action_button);
        this.timer_label = TimerLabel.newInstance(atlas);
        super.addActor(this.timer_label);
        this.timer_frame = TimerFrame.newInstance(atlas);
        this.timer_frame.setListener(new TimerFrame.CupTimerListener() {
            @Override
            public void timerExpired() {
                if (CupWindow.this.listener != null) {
                    CupWindow.this.listener.timerExpired();
                }
            }
        });
        super.addActor(this.timer_frame);
        this.price_frame = PriceFrame.newInstance(atlas);
        super.addActor(this.price_frame);
        this.rules_button = InfoButton.newInstance(atlas, "rules");
        this.rules_button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (rules_button.isDisabled()) return;
                if (CupWindow.this.listener != null) {
                    CupWindow.this.listener.showRules();
                }
            }
        });
        super.addActor(this.rules_button);
        this.top_button = InfoButton.newInstance(atlas, "leaders");
        this.top_button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (top_button.isDisabled()) return;
                if (CupWindow.this.listener != null) {
                    CupWindow.this.listener.showTop();
                }
            }
        });
        super.addActor(this.top_button);
    }

    public static CupWindow newInstance(TextureAtlas atlas) {
        WindowStyle style = new WindowStyle();
        style.titleFont = FontsFactory.getAsync("Junegull.ttf", 18);
        style.titleFontColor = Color.WHITE;
        style.background = new TextureRegionDrawable(atlas.findRegion("window_background"));
        CupWindow instance = new CupWindow(style, atlas);
        instance.setBounds(150f, 70f, 724f, 420f);
        instance.setMovable(false);
        instance.setModal(true);
        instance.close_button.setBounds(52f, 55f, 300f, 55f);
        instance.action_button.setBounds(372f, 55f, 300f, 55f);
        instance.timer_label.setBounds(90f, 190f, 540f, 55f);
        instance.timer_frame.setBounds(190f, 122f, 340f, 56f);
        instance.price_frame.setBounds(240f, 255f, 130f, 40f);
        instance.rules_button.setBounds(440f, 335f, 60f, 60f);
        instance.top_button.setBounds(550f, 245f, 60f, 60f);
        instance.title_label.setBounds(0f, 320f, 700f, 90f);
        instance.price_label.setBounds(12f, 260f, 200f, 30f);
        instance.top_label.setBounds(370f, 260f, 175f, 30f);
        instance.close_button.setTextClose();
        instance.hide();
        return instance;
    }

    @Override
    public void resize(int width, int height) {
        this.close_button.resize(width, height);
        this.action_button.resize(width, height);
        this.timer_label.resize(width, height);
        this.timer_frame.resize(width, height);
        this.price_frame.resize(width, height);
        this.title_label.resize(width, height);
        this.price_label.resize(width, height);
        this.top_label.resize(width, height);
    }

    public void show(int countdown, boolean started, boolean registered, int spins, int price, int money) {
        if (started) {
            this.top_label.setText(Connection.getString("cup_leaders"));
            this.timer_label.setTextEnd();
            if (registered && spins > 0) {
                this.action_button.setTextPlay();
                this.action_button.setDisabled(false);
            } else if (price > money) {
                this.action_button.setTextBuy();
                this.action_button.setDisabled(false);
            } else {
                this.action_button.setTextRegister();
                this.action_button.setDisabled(false);
            }
        } else {
            this.top_label.setText(Connection.getString("cup_winners"));
            this.timer_label.setTextBegin();
            this.action_button.setTextRegister();
            this.action_button.setDisabled(true);
        }
        this.timer_frame.setCountdown(countdown);
        this.price_frame.setValue(price);
        super.setVisible(true);
    }

    public void hide() {
        super.setVisible(false);
    }

    public void setCountdown(int countdown) {
        this.timer_frame.setCountdown(countdown);
    }

    public void setListener(CupWindowListener listener) {
        this.listener = listener;
    }

    public interface CupWindowListener {
        void actionClicked();

        void timerExpired();

        void showRules();

        void showTop();
    }

    private static class CupButton extends Button implements Resizable {
        private final PixelLabel label;

        private CupButton(ButtonStyle style) {
            super(style);
            LabelStyle ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("Taurus.ttf", 28);
            ls.fontColor = Color.WHITE;
            this.label = new PixelLabel("", ls);
            this.label.setAlignment(Align.center);
            super.addActor(this.label);
        }

        public static CupButton newInstance(TextureAtlas atlas, String color) {
            ButtonStyle style = new ButtonStyle();
            style.up = new TextureRegionDrawable(atlas.findRegion("button_".concat(color).concat("_active")));
            style.down = new TextureRegionDrawable(atlas.findRegion("button_".concat(color).concat("_pressed")));
            style.disabled = new TextureRegionDrawable(atlas.findRegion("button_".concat(color).concat("_inactive")));
            CupButton instance = new CupButton(style);
            return instance;
        }

        @Override
        public void resize(int width, int height) {
            this.label.resize(width, height);
        }

        @Override
        public void setBounds(float x, float y, float width, float height) {
            this.label.setBounds(0f, 0f, width, height);
            super.setBounds(x, y, width, height);
        }

        public void setTextClose() {
            this.label.setText(Connection.getString("common_close"));
        }

        public void setTextRegister() {
            this.label.setText(Connection.getString("cup_register"));
        }

        @SuppressWarnings("unused")
        public void setTextUnavailable() {
            this.label.setText(Connection.getString("cup_unavailable"));
        }

        public void setTextPlay() {
            this.label.setText(Connection.getString("cup_play"));
        }

        public void setTextBuy() {
            this.label.setText(Connection.getString("cup_buy_chips"));
        }
    }

    private static class TimerLabel extends BackgroundLabel {
        private TimerLabel(LabelStyle style, TextureRegion region) {
            super("", style, region);
        }

        public static TimerLabel newInstance(TextureAtlas atlas) {
            LabelStyle style = new LabelStyle();
            style.font = FontsFactory.getAsync("Junegull.ttf", 30);
            style.fontColor = new Color(1f, .84f, 0f, 1f);
            TextureRegion background = atlas.findRegion("timer_title_background");
            TimerLabel instance = new TimerLabel(style, background);
            return instance;
        }

        public void setTextBegin() {
            super.setText(Connection.getString("cup_begin"));
        }

        public void setTextEnd() {
            super.setText(Connection.getString("cup_end"));
        }
    }

    public static class TimerFrame extends BackgroundLabel {
        private int int_countdown;
        private float countdown;
        private CupTimerListener listener;

        private TimerFrame(LabelStyle style, TextureRegion region) {
            super("", style, region);
            this.int_countdown = 0;
            this.countdown = 0f;
            this.listener = null;
        }

        public static TimerFrame newInstance(TextureAtlas atlas) {
            LabelStyle style = new LabelStyle();
            style.font = FontsFactory.getAsync("whitrabt.ttf", 40);
            style.fontColor = new Color(1f, .84f, 0f, 1f);
            TextureRegion background = atlas.findRegion("timer_background");
            TimerFrame instance = new TimerFrame(style, background);
            return instance;
        }

        @Override
        public void act(float delta) {
            if (this.countdown > 0f) {
                this.countdown -= delta;
                if (this.countdown <= 0f) {
                    if (this.listener != null) {
                        this.listener.timerExpired();
                    }
                } else {
                    int seconds = (int) this.countdown;
                    if (seconds != this.int_countdown) {
                        this.int_countdown = seconds;
                        this.setButtonTime();
                    }
                }
            }
            super.act(delta);
        }

        public void setCountdown(int seconds) {
            this.countdown = (float) seconds;
            this.int_countdown = seconds;
            this.setButtonTime();
        }

        public void setListener(CupTimerListener listener) {
            this.listener = listener;
        }

        private void setButtonTime() {
            int h = this.int_countdown / 3600;
            int t = this.int_countdown - h * 3600;
            int m = t / 60;
            int s = t - m * 60;
            StringBuilder builder = new StringBuilder();
            builder.append(h);
            builder.append(":");
            if (m < 10) builder.append("0");
            builder.append(m);
            builder.append(":");
            if (s < 10) builder.append("0");
            builder.append(s);
            super.setText(builder.toString());
        }

        public interface CupTimerListener {
            void timerExpired();
        }
    }

    public static class BlackFrame extends BackgroundLabel {
        private BlackFrame(LabelStyle style, TextureRegion background) {
            super("", style, background);
        }

        public static BlackFrame newInstance(TextureAtlas atlas) {
            LabelStyle style = new LabelStyle();
            style.font = FontsFactory.getAsync("Junegull.ttf", 24);
            style.fontColor = new Color(.39f, .65f, .98f, 1f);
            TextureRegion background = atlas.findRegion("price_background");
            BlackFrame instance = new BlackFrame(style, background);
            return instance;
        }

        public void setValue(int value) {
            super.setText(String.valueOf(value));
        }
    }

    public static class PriceFrame extends BackgroundLabel {
        private static final float ICON_ASPECT = .91f;
        private final TextureRegion icon;
        private final float[] icon_bounds;
        private int value;

        private PriceFrame(LabelStyle style, TextureRegion background, TextureRegion icon, float[] offset) {
            super("", style, background, offset);
            this.icon = icon;
            this.icon_bounds = new float[4];
            this.value = 0;
        }

        public static PriceFrame newInstance(TextureAtlas atlas) {
            LabelStyle style = new LabelStyle();
            style.font = FontsFactory.getAsync("Junegull.ttf", 24);
            style.fontColor = new Color(.39f, .65f, .98f, 1f);
            TextureRegion background = atlas.findRegion("price_background");
            TextureRegion icon = atlas.findRegion("chips_icon");
            float[] offset = {.1f, 0f, .9f, 1f};
            PriceFrame instance = new PriceFrame(style, background, icon, offset);
            return instance;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            super.draw(batch, parentAlpha);
            batch.draw(this.icon, this.icon_bounds[0], this.icon_bounds[1], this.icon_bounds[2], this.icon_bounds[3]);
        }

        @Override
        public void resize(int width, int height) {
            super.resize(width, height);
            this.icon_bounds[1] = super.bounds[1] - .1f * super.bounds[3];
            this.icon_bounds[3] = 1.2f * super.bounds[3];
            float ppu_x = (float) width / (float) AppConfig.VIEWPORT_WIDTH;
            float ppu_y = (float) height / (float) AppConfig.VIEWPORT_HEIGHT;
            float ph = this.icon_bounds[3] * ppu_y;
            float pw = ph * ICON_ASPECT;
            this.icon_bounds[2] = pw / ppu_x;
            this.icon_bounds[0] = super.bounds[0] - this.icon_bounds[2] / 2f;
        }

        public int getValue() {
            return this.value;
        }

        public void setValue(int value) {
            this.value = value;
            super.setText(String.valueOf(value));
        }
    }

    private static class InfoButton extends Button {
        private InfoButton(ButtonStyle style) {
            super(style);
        }

        public static InfoButton newInstance(TextureAtlas atlas, String type) {
            ButtonStyle style = new ButtonStyle();
            style.up = new TextureRegionDrawable(atlas.findRegion("button_".concat(type).concat("_active")));
            style.down = new TextureRegionDrawable(atlas.findRegion("button_".concat(type).concat("_pressed")));
            style.disabled = new TextureRegionDrawable(atlas.findRegion("button_".concat(type).concat("_inactive")));
            InfoButton instance = new InfoButton(style);
            return instance;
        }
    }

}
