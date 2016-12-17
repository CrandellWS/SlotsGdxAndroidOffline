package mobi.square.slots.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import mobi.square.slots.api.Connection;
import mobi.square.slots.config.AppConfig;
import mobi.square.slots.tools.FontsFactory;

public class SpinButton extends Button implements Resizable {

    private static final float HIDE_TIME = 3f;
    private static final float SHOW_TIME = 1f;
    private static final float FADE_TIME = 1f;
    private static final float ROTATE_SPEED = -45f;
    private static final float START_ANGLE = 20f;
    private static final int LABEL_TIME = 8;
    private static final int MESSAGE_TIME = 3;
    private static final int CHANGE_TIME = 1;
    private static final float AUTO_FRAMES_TIME = .1f;
    private final Matrix4 pixel_matrix;
    private final Matrix4 backup_matrix;
    private final PixelLabel spin_label;
    private final PixelLabel auto_label;
    private final PixelLabel free_label;
    private final TextureRegionDrawable flare_texture;
    private final TextureRegionDrawable[] autospin_texture;
    private final float[] bounds;
    private float flare_angle;
    private float flare_alpha;
    private float flare_time;
    private int flare_state;
    private float change_time;
    private boolean label_is_visible;
    private float spin_alpha;
    private float auto_alpha;
    private int free_spins;
    private boolean switch_labels;
    private boolean auto_mode;
    private float auto_time;
    private int auto_index;

    private SpinButton(TextureAtlas atlas, TextureAtlas autospin_atlas, ButtonStyle style) {
        super(style);
        this.pixel_matrix = new Matrix4();
        this.backup_matrix = new Matrix4();
        super.addActor(new ButtonAnimation(this));
        this.bounds = new float[4];
        this.autospin_texture = new TextureRegionDrawable[16];
        for (int i = 0; i < 16; i++)
            this.autospin_texture[i] = new TextureRegionDrawable(autospin_atlas.findRegion("button_spin_auto", i));
        this.flare_texture = new TextureRegionDrawable(atlas.findRegion("button_spin_flare"));
        this.flare_angle = 0f;
        this.flare_alpha = 0f;
        this.flare_time = 0f;
        this.flare_state = 0;
        LabelStyle ls = new LabelStyle();
        ls.fontColor = Color.WHITE;
        ls.font = FontsFactory.getAsync("Junegull.ttf", 36);
        this.spin_label = new PixelLabel(Connection.getString("footer_button_spin"), ls);
        this.spin_label.setAlignment(Align.center, Align.center);
        ls = new LabelStyle();
        ls.fontColor = Color.WHITE;
        ls.font = FontsFactory.getAsync("Junegull.ttf", 24);
        this.free_label = new PixelLabel(Connection.getString("footer_button_spin_bonus"), ls);
        this.free_label.setAlignment(Align.center, Align.center);
        ls = new LabelStyle();
        ls.fontColor = Color.WHITE;
        ls.font = FontsFactory.getAsync("Taurus.ttf", 18);
        this.auto_label = new PixelLabel(Connection.getString("footer_auto_info"), ls);
        this.auto_label.setAlignment(Align.center, Align.center);
        super.addActor(this.spin_label);
        super.addActor(this.auto_label);
        super.addActor(this.free_label);
        this.change_time = LABEL_TIME;
        this.label_is_visible = true;
        this.spin_alpha = 1f;
        this.auto_alpha = 0f;
        this.auto_label.setColor(1f, 1f, 1f, 0f);
        this.free_label.setColor(1f, 1f, 1f, 0f);
        this.free_spins = 0;
        this.auto_mode = false;
        this.auto_time = 0f;
        this.auto_index = 0;
        this.switch_labels = true;
    }

    public static SpinButton newInstance(TextureAtlas atlas, TextureAtlas autospin_atlas) {
        ButtonStyle style = new ButtonStyle();
        style.up = new TextureRegionDrawable(atlas.findRegion("button_spin_active"));
        style.down = new TextureRegionDrawable(atlas.findRegion("button_spin_pressed"));
        style.disabled = new TextureRegionDrawable(atlas.findRegion("button_spin_inactive"));
        style.pressedOffsetY = -2f;
        SpinButton instance = new SpinButton(atlas, autospin_atlas, style);
        return instance;
    }

    @Override
    public void resize(int width, int height) {
        if (this.spin_label != null) {
            this.spin_label.setBounds(0f, super.getHeight() * .07f, super.getWidth(), super.getHeight() * .93f);
            this.spin_label.resize(width, height);
        }
        if (this.auto_label != null) {
            this.auto_label.setBounds(0f, 0f, super.getWidth(), super.getHeight());
            this.auto_label.resize(width, height);
        }
        if (this.free_label != null) {
            this.free_label.setBounds(0f, super.getHeight() * .07f, super.getWidth(), super.getHeight() * .93f);
            this.free_label.resize(width, height);
        }
        if (this.pixel_matrix != null) {
            this.pixel_matrix.setToOrtho2D(0, 0, width, height);
        }
    }

    public boolean isAutoMode() {
        return this.auto_mode;
    }

    public void setAutoMode(boolean auto_mode) {
        this.auto_mode = auto_mode;
        this.auto_time = AUTO_FRAMES_TIME;
        this.auto_index = 0;
        this.clearAnimationVars(super.isDisabled());
        this.setLabelText();
    }

    public void setFreeSpins(int free_spins) {
        this.free_spins = free_spins;
        this.setLabelText();
        this.setLabelsAlpha();
    }

    private void setLabelText() {
        this.free_label.setText(Connection.getString("footer_button_spin_bonus").concat(" ").concat(String.valueOf(this.free_spins)));
        if (this.auto_mode) {
            this.spin_label.setText(Connection.getString("footer_auto_stop"));
        } else {
            this.spin_label.setText(Connection.getString("footer_button_spin"));
        }
    }

    private void setLabelsAlpha() {
        this.auto_label.setColor(1f, 1f, 1f, this.auto_alpha);
        if (this.free_spins > 0) {
            this.free_label.setColor(1f, 1f, 1f, this.spin_alpha);
            this.spin_label.setColor(1f, 1f, 1f, 0f);
        } else {
            this.free_label.setColor(1f, 1f, 1f, 0f);
            this.spin_label.setColor(1f, 1f, 1f, this.spin_alpha);
        }
    }

    public void setSwitchLabels(boolean state) {
        this.switch_labels = state;
    }

    private void clearAnimationVars(boolean disabled) {
        this.flare_time = 0f;
        this.flare_angle = START_ANGLE;
        this.flare_alpha = 0f;
        this.flare_state = disabled ? 0 : 1;
        this.change_time = LABEL_TIME;
        this.label_is_visible = true;
        this.spin_alpha = 1f;
        this.auto_alpha = 0f;
        this.setLabelsAlpha();
    }

    @Override
    public void setDisabled(boolean isDisabled) {
        if (isDisabled != super.isDisabled())
            this.clearAnimationVars(isDisabled);
        super.setDisabled(isDisabled);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (!super.isDisabled()) {
            this.flare_time -= delta;
            switch (this.flare_state) {
                case 0:
                    if (this.flare_time <= 0f) {
                        this.flare_time = 0f;
                        this.flare_angle = START_ANGLE;
                        this.flare_alpha = 0f;
                        this.flare_state = 1;
                    }
                    break;
                case 1:
                    this.flare_alpha += delta / FADE_TIME;
                    if (this.flare_alpha >= 1f) {
                        this.flare_time = SHOW_TIME;
                        this.flare_alpha = 1f;
                        this.flare_state = 2;
                    }
                    this.flare_angle += ROTATE_SPEED * delta;
                    break;
                case 2:
                    if (this.flare_time <= 0f) {
                        this.flare_time = 0f;
                        this.flare_state = 3;
                    }
                    this.flare_angle += ROTATE_SPEED * delta;
                    break;
                case 3:
                    this.flare_alpha -= delta / FADE_TIME;
                    if (this.flare_alpha <= 0f) {
                        this.flare_time = HIDE_TIME;
                        this.flare_alpha = 0f;
                        this.flare_state = 0;
                    }
                    this.flare_angle += ROTATE_SPEED * delta;
                    break;
                default:
                    this.flare_time = HIDE_TIME;
                    this.flare_angle = 0f;
                    this.flare_alpha = 0f;
                    this.flare_state = 0;
                    break;
            }
        }
        if (this.auto_mode) {
            this.auto_time -= delta;
            while (this.auto_time <= 0f) {
                this.auto_time += AUTO_FRAMES_TIME;
                this.auto_index++;
                if (this.auto_index >= this.autospin_texture.length) {
                    this.auto_index = 0;
                }
            }
        } else if (this.switch_labels) {
            if (this.change_time <= 0f) {
                if (this.label_is_visible) {
                    this.spin_alpha -= delta / (float) CHANGE_TIME;
                    if (this.spin_alpha <= .3f)
                        this.auto_alpha += delta / (float) CHANGE_TIME;
                    if (this.spin_alpha <= 0f) this.spin_alpha = 0f;
                    if (this.auto_alpha >= 1f) this.auto_alpha = 1f;
                    if (this.spin_alpha <= 0f && this.auto_alpha >= 1f) {
                        this.spin_alpha = 0f;
                        this.auto_alpha = 1f;
                        this.label_is_visible = false;
                        this.change_time = MESSAGE_TIME;
                    }
                } else {
                    this.auto_alpha -= delta / (float) CHANGE_TIME;
                    if (this.auto_alpha <= .3f)
                        this.spin_alpha += delta / (float) CHANGE_TIME;
                    if (this.auto_alpha <= 0f) this.auto_alpha = 0f;
                    if (this.spin_alpha >= 1f) this.spin_alpha = 1f;
                    if (this.spin_alpha >= 1f && this.auto_alpha <= 0f) {
                        this.spin_alpha = 1f;
                        this.auto_alpha = 0f;
                        this.label_is_visible = true;
                        this.change_time = LABEL_TIME;
                    }
                }
                this.setLabelsAlpha();
            } else this.change_time -= delta;
        }
    }

    void drawAnimation(Batch batch, float parentAlpha) {
        if (this.auto_mode) {
            float x = this.bounds[0];
            float y = this.bounds[1];
            float width = this.bounds[2];
            float height = this.bounds[3];
            this.autospin_texture[this.auto_index].draw(batch, x, y, width, height);
        }
        if (this.flare_state > 0) {
            float x_aspect = (float) Gdx.graphics.getWidth() / (float) AppConfig.VIEWPORT_WIDTH;
            float y_aspect = (float) Gdx.graphics.getHeight() / (float) AppConfig.VIEWPORT_HEIGHT;
            float x = this.bounds[0] * x_aspect;
            float y = this.bounds[1] * y_aspect;
            float width = this.bounds[2] * x_aspect;
            float height = this.bounds[3] * y_aspect;
            batch.setColor(1f, 1f, 1f, this.flare_alpha);
            this.backup_matrix.set(batch.getProjectionMatrix());
            batch.setProjectionMatrix(this.pixel_matrix);
            this.flare_texture.draw(batch, x, y, width / 2f, height / 2f, width, height, 1f, 1f, this.flare_angle);
            batch.setProjectionMatrix(this.backup_matrix);
            batch.setColor(1f, 1f, 1f, 1f);
        }
    }

    @Override
    public void setBounds(float x, float y, float width, float height) {
        super.setBounds(x, y, width, height);
        this.bounds[0] = x;
        this.bounds[1] = y;
        this.bounds[2] = width;
        this.bounds[3] = height;
        this.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private class ButtonAnimation extends Actor {
        private final SpinButton parent;

        public ButtonAnimation(SpinButton parent) {
            this.parent = parent;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            this.parent.drawAnimation(batch, parentAlpha);
        }
    }

}
