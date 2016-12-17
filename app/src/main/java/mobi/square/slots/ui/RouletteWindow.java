package mobi.square.slots.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
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
import mobi.square.slots.ui.MessageWindow.CloseButton;
import mobi.square.slots.utils.utils;

public class RouletteWindow extends Window implements Resizable {

    private static final float ASPECT = 1.532f;
    private static final float ZERO_ANGLE = 220f;
    private static final float ROTATE_SPEED = -270f;
    private static final int STOP_SPEED = 5;

    private static final float[] AWARD_LABEL = {0.5675f, 0.7f, 0.1976f, 0.1097f};
    private static final float[] AWARD_VALUE = {0.7634f, 0.7f, 0.2022f, 0.1097f};
    private static final float[] RULES_LABEL = {0.5675f, 0.4375f, 0.4f, 0.2125f};
    private static final float[] TIMER_LABEL = {0.579f, 0.1903f, 0.378f, 0.222f};
    private static final float[] TIMER_VALUE = {0.577f, 0.084f, 0.1614f, 0.104f};
    private static final float[] MULTI_VALUE = {0.193f, 0.066f, 0.1888f, 0.094f};
    private static final float[] CLOSE_BUTTON = {0.77f, 0.85f, 0.202f, 0.12f};
    private static final float[] SPIN_BUTTON = {0.765f, 0.06f, 0.1913f, 0.3f};
    private static final float[] ROULETTE = {0.024f, 0.15f, 0.53f, 0.82f};
    private static final float[] POINTER = {0.248f, 0.144f, 0.08f, 0.13f};
    private final PixelLabel award_label;
    private final PixelLabel award_value;
    private final PixelLabel rules_label;
    private final PixelLabel timer_label;
    private final PixelLabel timer_value;
    private final PixelLabel multi_value;
    private final CloseButton close_button;
    private final RouletteButton spin_button;
    private final TextureRegion roulette_pointer;
    private final TextureRegion roulette_shadow;
    private final TextureRegion roulette_wheel;
    private final float[] roulette_bounds;
    private final float[] pointer_bounds;
    private final Matrix4 backup_matrix;
    private final Matrix4 pixel_matrix;
    private RouletteWindowListener listener;
    private int int_countdown;
    private float countdown;
    private float angle;
    private int multiplier;
    private boolean started;
    private int stop_index;
    private int stop_value;
    private boolean stop_next;
    private boolean stop_needed;
    private String price;
    private boolean target_set;
    private float target_angle;
    private int spins_count;
    private int buy_spins;

    private RouletteWindow(WindowStyle style, TextureAtlas atlas, TextureAtlas windows_atlas) {
        super("", style);
        LabelStyle ls;
        { // Award label
            ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("Junegull.ttf", 30);
            ls.fontColor = new Color(1f, 0.96f, 0f, 1f);
            this.award_label = new PixelLabel(Connection.getString("common_award"), ls);
            this.award_label.setAlignment(Align.center);
            super.addActor(this.award_label);
        }
        { // Award value
            ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("Junegull.ttf", 36);
            ls.fontColor = new Color(0f, 0.933f, 0.047f, 1f);
            this.award_value = new PixelLabel("1000", ls);
            this.award_value.setAlignment(Align.center);
            super.addActor(this.award_value);
        }
        { // Rules label
            ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("arial.ttf", 16);
            ls.fontColor = Color.BLACK;
            this.rules_label = new PixelLabel(Connection.getString("roulette_rules"), ls);
            this.rules_label.setAlignment(Align.left);
            super.addActor(this.rules_label);
        }
        { // Timer label
            ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("Junegull.ttf", 28);
            ls.fontColor = new Color(0.118f, 0.118f, 0.118f, 1f);
            this.timer_label = new PixelLabel(Connection.getString("roulette_timer"), ls);
            this.timer_label.setAlignment(Align.left);
            super.addActor(this.timer_label);
        }
        { // Timer value
            ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("Junegull.ttf", 28);
            ls.fontColor = new Color(0f, 0.7255f, 1f, 1f);
            this.timer_value = new PixelLabel("0:00:00", ls);
            this.timer_value.setAlignment(Align.center);
            super.addActor(this.timer_value);
        }
        { // Multiplier value
            ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("Junegull.ttf", 36);
            ls.fontColor = Color.BLACK;
            this.multi_value = new PixelLabel("x1", ls);
            this.multi_value.setAlignment(Align.center);
            super.addActor(this.multi_value);
        }
        this.close_button = CloseButton.newInstance(windows_atlas);
        this.close_button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (close_button.isDisabled()) return;
                hide();
            }
        });
        super.addActor(this.close_button);
        this.spin_button = RouletteButton.newInstance(atlas);
        this.spin_button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (spin_button.isDisabled()) return;
                if (started) return;
                if (Connection.getInstance().getRouletteSpins() > 0)
                    startSpin();
                if (listener != null) {
                    listener.spinClicked();
                }
            }
        });
        super.addActor(this.spin_button);
        this.listener = null;
        this.started = false;
        this.pointer_bounds = new float[4];
        this.roulette_bounds = new float[4];
        this.roulette_pointer = atlas.findRegion("roulette_pointer");
        this.roulette_shadow = atlas.findRegion("roulette_shadow");
        this.roulette_wheel = atlas.findRegion("roulette_wheel");
        this.backup_matrix = new Matrix4();
        this.pixel_matrix = new Matrix4();
        this.int_countdown = 0;
        this.countdown = 0f;
        this.angle = 0f;
        this.stop_index = 0;
        this.stop_value = 0;
        this.stop_needed = false;
        this.stop_next = false;
        this.target_set = false;
        this.target_angle = 0f;
        this.multiplier = 1;
        this.spins_count = 0;
        this.buy_spins = 0;
        this.price = null;
    }

    public static RouletteWindow newInstance(TextureAtlas atlas, TextureAtlas windows_atlas) {
        WindowStyle style = new WindowStyle();
        style.titleFont = FontsFactory.getAsync("Junegull.ttf", 18);
        style.titleFontColor = Color.WHITE;
        style.background = new TextureRegionDrawable(atlas.findRegion("roulette_window"));
        RouletteWindow instance = new RouletteWindow(style, atlas, windows_atlas);
        instance.setBounds(0f, 10f, AppConfig.VIEWPORT_WIDTH, AppConfig.VIEWPORT_HEIGHT - 30f);
        instance.setMovable(false);
        instance.setModal(true);
        instance.close_button.setTextClose();
        instance.hide();
        return instance;
    }

    @Override
    public void act(float delta) {
        if (this.countdown > 0f) {
            this.countdown -= delta;
            if (this.countdown <= 0f) {
                this.spin_button.setDisabled(false);
            } else {
                int seconds = (int) this.countdown;
                if (seconds != this.int_countdown) {
                    this.int_countdown = seconds;
                    this.setTimerValue();
                }
            }
        }
        float dt = delta < 0.05f ? delta : 0.05f;
        while (dt <= delta) {
            if (this.started) {
                if (this.stop_next) {
                    if (target_set) {
                        float ta = this.angle < this.target_angle ? this.target_angle - 360f : this.target_angle;
                        float ddt = Math.abs((this.angle - ta) / ((float) STOP_SPEED * 30f));
                        this.angle += dt * ROTATE_SPEED * ddt;
                        if (Math.abs(this.angle - ta) < 2f) {
                            this.totalStop();
                        }
                    } else {
                        this.angle += dt * ROTATE_SPEED;
                        int prev_index = this.stop_index + STOP_SPEED;
                        if (prev_index > 11) prev_index = prev_index - 12;
                        if (prev_index < 0) prev_index = 11;
                        float min_val = -(float) (prev_index > 0 ? 12 - prev_index : 0) * 30f - 15f;
                        float max_val = -(float) (prev_index > 0 ? 12 - prev_index : 0) * 30f + 15f;
                        if (this.angle >= min_val && this.angle <= max_val) {
                            this.target_angle = this.stop_index * 30f - 360f - utils.getRandom(-10, 15);
                            if (this.target_angle < -360f) this.target_angle += 360f;
                            if (this.target_angle > 0f) this.target_angle -= 360f;
                            this.target_set = true;
                        }
                    }
                } else this.angle += dt * ROTATE_SPEED;
            }
            if (this.stop_needed && this.angle < -360f) this.stop_next = true;
            while (this.angle < (this.stop_needed ? -360f : 0f)) {
                this.angle += 360f;
                if (this.stop_needed) {
                    this.stop_next = true;
                }
            }
            while (this.angle >= (this.stop_needed ? 720f : 360f)) {
                this.angle -= 360f;
                if (this.stop_needed) {
                    this.stop_next = true;
                }
            }
            if (dt == delta) break;
            dt += 0.05f;
            if (dt > delta) dt = delta;
        }
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.flush();
        this.backup_matrix.set(batch.getProjectionMatrix());
        batch.setProjectionMatrix(this.pixel_matrix);
        batch.draw(
                this.roulette_shadow,
                this.roulette_bounds[0],
                this.roulette_bounds[1],
                this.roulette_bounds[2],
                this.roulette_bounds[3]
        );
        batch.draw(
                this.roulette_wheel,
                this.roulette_bounds[0],
                this.roulette_bounds[1],
                this.roulette_bounds[2] / 2f,
                this.roulette_bounds[3] / 2f,
                this.roulette_bounds[2],
                this.roulette_bounds[3],
                1f, 1f,
                this.angle + ZERO_ANGLE
        );
        batch.draw(
                this.roulette_pointer,
                this.pointer_bounds[0],
                this.pointer_bounds[1],
                this.pointer_bounds[2],
                this.pointer_bounds[3]
        );
        batch.setProjectionMatrix(this.backup_matrix);
    }

    @Override
    public void resize(int width, int height) {
        this.pixel_matrix.setToOrtho2D(0f, 0f, width, height);
        float ppu_x = (float) width / (float) AppConfig.VIEWPORT_WIDTH;
        float ppu_y = (float) height / (float) AppConfig.VIEWPORT_HEIGHT;
        float h = super.getHeight();
        float ph = h * ppu_y;
        float pw = ph * ASPECT;
        float w = pw / ppu_x;
        float x = (AppConfig.VIEWPORT_WIDTH - w) / 2f;
        float y = super.getY();
        float px = x * ppu_x;
        float py = y * ppu_y;
        super.setWidth(w);
        super.setX(x);
        this.award_label.setBounds(w * AWARD_LABEL[0], h * AWARD_LABEL[1], w * AWARD_LABEL[2], h * AWARD_LABEL[3]);
        this.award_value.setBounds(w * AWARD_VALUE[0], h * AWARD_VALUE[1], w * AWARD_VALUE[2], h * AWARD_VALUE[3]);
        this.rules_label.setBounds(w * RULES_LABEL[0], h * RULES_LABEL[1], w * RULES_LABEL[2], h * RULES_LABEL[3]);
        this.timer_label.setBounds(w * TIMER_LABEL[0], h * TIMER_LABEL[1], w * TIMER_LABEL[2], h * TIMER_LABEL[3]);
        this.timer_value.setBounds(w * TIMER_VALUE[0], h * TIMER_VALUE[1], w * TIMER_VALUE[2], h * TIMER_VALUE[3]);
        this.multi_value.setBounds(w * MULTI_VALUE[0], h * MULTI_VALUE[1], w * MULTI_VALUE[2], h * MULTI_VALUE[3]);
        this.close_button.setBounds(w * CLOSE_BUTTON[0], h * CLOSE_BUTTON[1], w * CLOSE_BUTTON[2], h * CLOSE_BUTTON[3]);
        this.spin_button.setBounds(w * SPIN_BUTTON[0], h * SPIN_BUTTON[1], w * SPIN_BUTTON[2], h * SPIN_BUTTON[3]);
        this.roulette_bounds[0] = pw * ROULETTE[0] + px;
        this.roulette_bounds[1] = ph * ROULETTE[1] + py;
        this.roulette_bounds[2] = pw * ROULETTE[2];
        this.roulette_bounds[3] = this.roulette_bounds[2];
        this.pointer_bounds[0] = pw * POINTER[0] + px;
        this.pointer_bounds[1] = ph * POINTER[1] + py;
        this.pointer_bounds[2] = pw * POINTER[2];
        this.pointer_bounds[3] = ph * POINTER[3];
        this.award_label.resize(width, height);
        this.award_value.resize(width, height);
        this.rules_label.resize(width, height);
        this.timer_label.resize(width, height);
        this.timer_value.resize(width, height);
        this.multi_value.resize(width, height);
        this.close_button.resize(width, height);
        this.spin_button.resize(width, height);
    }

    @Override
    public void setBounds(float x, float y, float width, float height) {
        super.setBounds(x, y, width, height);
        this.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void updateValues() {
        this.multiplier = Connection.getInstance().getRouletteMultipler();
        this.multi_value.setText("x".concat(String.valueOf(this.multiplier)));
        this.spins_count = Connection.getInstance().getRouletteBonusSpins();
        if (Connection.getInstance().getRouletteTime() < 1) this.spins_count++;
        if (this.spins_count > 0) {
            this.spin_button.setTextSpin(this.spins_count);
            this.spin_button.setDisabled(false);
        } else if (this.price != null) {
            this.spin_button.setTextBuy(this.price, this.buy_spins);
            this.spin_button.setDisabled(false);
        } else {
            this.spin_button.setTextSpin(1);
            this.spin_button.setDisabled(true);
        }
        this.countdown = (float) Connection.getInstance().getRouletteTime();
        this.int_countdown = Connection.getInstance().getRouletteTime();
        this.setTimerValue();
    }

    public void show(int countdown, int multiplier, int count, String price, int spins) {
        this.price = price;
        this.buy_spins = spins;
        this.spins_count = countdown > 0 ? count : count + 1;
        this.multiplier = multiplier;
        this.multi_value.setText("x".concat(String.valueOf(multiplier)));
        if (this.spins_count > 0) {
            this.spin_button.setTextSpin(this.spins_count);
            this.spin_button.setDisabled(false);
        } else if (price != null) {
            this.spin_button.setTextBuy(price, spins);
            this.spin_button.setDisabled(false);
        } else {
            this.spin_button.setTextSpin(1);
            this.spin_button.setDisabled(true);
        }
        this.countdown = (float) countdown;
        this.int_countdown = countdown;
        this.setTimerValue();
        this.setAward(0);
        super.setVisible(true);
    }

    public void hide() {
        super.setVisible(false);
    }

    public void setAward(int value) {
        this.award_value.setText(String.valueOf(value));
    }

    public void stopRoulette(int index, int value, int countdown, int count) {
        if (!this.started) return;
        this.stop_index = index > 0 ? 12 - index : 0;
        this.stop_value = value;
        this.stop_needed = true;
        this.setAward(0);
    }

    public void setListener(RouletteWindowListener listener) {
        this.listener = listener;
    }

    private void startSpin() {
        this.spin_button.setDisabled(true);
        this.target_set = false;
        this.stop_needed = false;
        this.stop_next = false;
        this.started = true;
        this.setAward(0);
    }

    private void totalStop() {
        this.updateValues();
        this.setAward(this.stop_value);
        this.started = false;
        if (this.listener != null) {
            this.listener.rouletteStopped();
        }
    }

    private void setTimerValue() {
        int h = this.int_countdown / 3600;
        int t = this.int_countdown - h * 3600;
        int m = t / 60;
        int s = t - m * 60;
        this.timer_value.setText(String.valueOf(h).concat(m < 10 ? ":0" : ":").concat(String.valueOf(m)).concat(s < 10 ? ":0" : ":").concat(String.valueOf(s)));
    }

    public interface RouletteWindowListener {
        void spinClicked();

        void rouletteStopped();
    }

    private static class RouletteButton extends Button implements Resizable {
        private final PixelLabel label;
        private final PixelLabel buy_label;

        private RouletteButton(ButtonStyle style) {
            super(style);
            LabelStyle ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("Junegull.ttf", 36);
            ls.fontColor = Color.WHITE;
            this.label = new PixelLabel("", ls);
            this.label.setAlignment(Align.center);
            super.addActor(this.label);
            ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("Junegull.ttf", 18);
            ls.fontColor = Color.WHITE;
            this.buy_label = new PixelLabel("", ls);
            this.buy_label.setAlignment(Align.center);
            super.addActor(this.buy_label);
        }

        public static RouletteButton newInstance(TextureAtlas atlas) {
            ButtonStyle style = new ButtonStyle();
            style.up = new TextureRegionDrawable(atlas.findRegion("roulette_spin_active"));
            style.down = new TextureRegionDrawable(atlas.findRegion("roulette_spin_pressed"));
            style.disabled = new TextureRegionDrawable(atlas.findRegion("roulette_spin_inactive"));
            style.pressedOffsetY = -2f;
            RouletteButton instance = new RouletteButton(style);
            return instance;
        }

        public void setTextSpin(int count) {
            if (count > 1) {
                this.label.setText(Connection.getString("footer_button_spin").concat("\n(").concat(String.valueOf(count)).concat(")"));
            } else this.label.setText(Connection.getString("footer_button_spin"));
            this.buy_label.setVisible(false);
            this.label.setVisible(true);
        }

        public void setTextBuy(String price, int count) {
            this.buy_label.setText(Connection.getDefaultI18N().format("roulette_buy", Integer.valueOf(count), price));
            this.buy_label.setVisible(true);
            this.label.setVisible(false);
        }

        @Override
        public void setBounds(float x, float y, float width, float height) {
            super.setBounds(x, y, width, height);
            this.label.setBounds(0f, 0f, width, height);
            this.buy_label.setBounds(0f, 0f, width, height);
        }

        @Override
        public void resize(int width, int height) {
            this.label.resize(width, height);
            this.buy_label.resize(width, height);
        }
    }

}
