package mobi.square.slots.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import mobi.square.slots.config.AppConfig;
import mobi.square.slots.tools.FontsFactory;

public class DailyBonusPanel extends Group implements Resizable {

    private final static float ASPECT = .23f;
    private final DrawableActor background;
    private final BonusButton bonus_button;
    private final BonusButton roulette_button;
    private final BonusScale bonus_scale;
    private BonusPanelListener listener;
    private float panel_width;

    private DailyBonusPanel(TextureAtlas atlas) {
        this.listener = null;
        this.panel_width = 0f;
        this.background = DrawableActor.newInstance(atlas.findRegion("background"));
        super.addActor(this.background);
        this.bonus_button = BonusButton.newInstance(
                atlas.findRegion("bonus_active"),
                atlas.findRegion("bonus_inactive"),
                atlas.findRegion("bonus_sign")
        );
        this.bonus_button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (bonus_button.isDisabled()) return;
                if (listener != null) {
                    listener.bonusPressed();
                }
            }
        });
        super.addActor(this.bonus_button);
        this.roulette_button = BonusButton.newInstance(
                atlas.findRegion("roulette_active"),
                atlas.findRegion("roulette_inactive"),
                atlas.findRegion("roulette_sign")
        );
        this.roulette_button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //if (roulette_button.isDisabled()) return;
                if (listener != null) {
                    listener.roulettePressed();
                }
            }
        });
        super.addActor(this.roulette_button);
        this.bonus_scale = BonusScale.newInstance(atlas);
        super.addActor(this.bonus_scale);
    }

    public static DailyBonusPanel newInstance(TextureAtlas atlas) {
        DailyBonusPanel instance = new DailyBonusPanel(atlas);
        instance.setBounds(0f, 0f, 1f, AppConfig.VIEWPORT_HEIGHT - GameHeader.HEADER_HEIGHT);
        return instance;
    }

    @Override
    public void resize(int width, int height) {
        float h = super.getHeight();
        float ppu_x = (float) width / (float) AppConfig.VIEWPORT_WIDTH;
        float ppu_y = (float) height / (float) AppConfig.VIEWPORT_HEIGHT;
        float ph = h * ppu_y;
        float pw = ph * ASPECT;
        float w = pw / ppu_x;
        super.setWidth(w);
        this.background.setSize(w, h);
        float button_x = (.0f * pw) / ppu_x;
        float button_w = (.97f * pw) / ppu_x;
        float button_h = (.97f * pw) / ppu_y;
        float bonus_y = (.007f * ph) / ppu_y;
        float roulette_y = (.77f * ph) / ppu_y;
        this.bonus_button.setBounds(button_x, bonus_y, button_w, button_h);
        this.roulette_button.setBounds(button_x, roulette_y, button_w, button_h);
        float scale_x = (.17f * pw) / ppu_x;
        float scale_y = (.26f * ph) / ppu_y;
        float scale_w = (.35f * pw) / ppu_x;
        float scale_h = (.47f * ph) / ppu_y;
        this.bonus_scale.setBounds(scale_x, scale_y, scale_w, scale_h);
        this.panel_width = (.62f * pw) / ppu_x;
        this.roulette_button.resize(width, height);
        this.bonus_button.resize(width, height);
    }

    @Override
    public void setBounds(float x, float y, float width, float height) {
        super.setBounds(x, y, width, height);
        this.background.setBounds(x, y, width, height);
        this.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void disableBonusButton() {
        this.bonus_button.setDisabled(true);
    }

    public void disableRouletteButton() {
        this.roulette_button.setDisabled(true);
    }

    public void setListener(BonusPanelListener listener) {
        this.listener = listener;
    }

    public void setBonusTime(int total, int current, int chunks) {
        this.bonus_scale.chunks_active = chunks;
        this.bonus_scale.chunks_time = total;
        this.bonus_scale.current_time = (float) (total - current);
        this.bonus_button.setCountdown(current);
    }

    public void setRouletteTime(int total, int current) {
        this.roulette_button.setCountdown(current);
    }

    public float getPanelWidth() {
        return this.panel_width;
    }

    public interface BonusPanelListener {
        void bonusPressed();

        void roulettePressed();
    }

    private static class BonusButton extends Button implements Resizable {
        private static final float BLINK_TIME = .8f;
        private final DrawableActor sign;
        private final PixelLabel label;
        private float blink_time;
        private int int_countdown;
        private float countdown;

        private BonusButton(ButtonStyle style, TextureRegion sign) {
            super(style);
            this.blink_time = 0f;
            this.countdown = 0f;
            this.int_countdown = 0;
            LabelStyle ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("Junegull.ttf", 18);
            ls.fontColor = Color.WHITE;
            this.label = new PixelLabel("00:00", ls);
            this.label.setAlignment(Align.center, Align.center);
            this.sign = DrawableActor.newInstance(sign);
            super.addActor(this.label);
            super.addActor(this.sign);
        }

        public static BonusButton newInstance(TextureRegion active, TextureRegion inactive, TextureRegion sign) {
            ButtonStyle style = new ButtonStyle();
            if (active != null) {
                style.up = new TextureRegionDrawable(active);
                style.down = new TextureRegionDrawable(new TextureRegion(
                        active.getTexture(),
                        active.getRegionX(),
                        active.getRegionY() - 1,
                        active.getRegionWidth(),
                        active.getRegionHeight()
                ));
            }
            style.disabled = inactive != null ? new TextureRegionDrawable(inactive) : null;
            style.pressedOffsetY = -1f;
            BonusButton instance = new BonusButton(style, sign);
            instance.label.setVisible(false);
            return instance;
        }

        @Override
        public void act(float delta) {
            if (this.countdown > 0f) {
                this.countdown -= delta;
                if (this.countdown <= 0f) {
                    super.setDisabled(false);
                    this.label.setVisible(false);
                } else {
                    int seconds = (int) this.countdown;
                    if (seconds != this.int_countdown) {
                        this.int_countdown = seconds;
                        this.setButtonTime();
                    }
                }
            } else {
                /*if (this.blink_time > 0f) {
					this.sign.alpha += delta / BLINK_TIME;
					if (this.sign.alpha >= 1f) {
						this.sign.alpha = 1f;
						this.blink_time = 0f;
					}
				} else {
					this.sign.alpha -= delta / BLINK_TIME;
					if (this.sign.alpha <= 0f) {
						this.sign.alpha = 0f;
						this.blink_time = 1f;
					}
				}*/
                this.blink_time += delta;
                while (this.blink_time >= BLINK_TIME) {
                    this.blink_time -= BLINK_TIME;
                    this.sign.setVisible(!this.sign.isVisible());
                }
            }
        }

        @Override
        public void resize(int width, int height) {
            this.label.resize(width, height);
        }

        @Override
        public void setBounds(float x, float y, float width, float height) {
            super.setBounds(x, y, width, height);
            this.sign.setBounds(0f, 0f, width, height);
            this.label.setBounds(0f, .3f * height, .8f * width, .4f * height);
        }

        public void setCountdown(int seconds) {
            this.countdown = (float) seconds;
            this.int_countdown = seconds;
            if (seconds <= 0) {
                this.label.setVisible(false);
                super.setDisabled(false);
            } else {
                this.label.setVisible(true);
                this.sign.setVisible(false);
                super.setDisabled(true);
                this.setButtonTime();
            }
        }

        private void setButtonTime() {
            int h = this.int_countdown / 3600;
            int t = this.int_countdown - h * 3600;
            int m = t / 60;
            int s = t - m * 60;
            this.label.setText(String.valueOf(h).concat(m < 10 ? ":0" : ":").concat(String.valueOf(m)).concat(s < 10 ? ":0" : ":").concat(String.valueOf(s)));
        }
    }

    private static class BonusScale extends Actor {
        private final TextureRegion empty;
        private final TextureRegion ready;
        private final TextureRegion taken;
        private int chunks_active;
        private int chunks_time;
        private float current_time;

        private BonusScale(TextureAtlas atlas) {
            this.empty = atlas.findRegion("scale_empty");
            this.ready = atlas.findRegion("scale_ready");
            this.taken = atlas.findRegion("scale_taken");
            this.chunks_active = 0;
            this.chunks_time = 5;
            this.current_time = 0;
        }

        public static BonusScale newInstance(TextureAtlas atlas) {
            BonusScale instance = new BonusScale(atlas);
            return instance;
        }

        @Override
        public void act(float delta) {
            this.current_time += delta;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            float x = super.getX();
            float y = super.getY();
            float w = super.getWidth();
            float h = super.getHeight();
            float ppu_x = (float) Gdx.graphics.getWidth() / (float) AppConfig.VIEWPORT_WIDTH;
            float ppu_y = (float) Gdx.graphics.getHeight() / (float) AppConfig.VIEWPORT_HEIGHT;
            int px = (int) (x * ppu_x) - 1;
            int pw = (int) (w * ppu_x) + 2;
            float dh = .01f * h;
            float h2 = h - 2 * dh;
            // Taken area scissors
            int ty = (int) (y * ppu_y) - 1;
            int th = (int) ((((float) this.chunks_active / 5f) * h2 + dh) * ppu_y);
            batch.flush();
            Gdx.gl20.glEnable(GL20.GL_SCISSOR_TEST);
            Gdx.gl20.glScissor(px, ty, pw, th);
            batch.draw(this.taken, x, y, w, h);
            // Ready area scissors
            int ry = ty + th;
            float rp = this.current_time / (float) this.chunks_time;
            if (rp > 1f) rp = 1f;
            int rh = (int) ((((float) this.chunks_active / 5f + rp / 5f) * h2 + dh) * ppu_y) - th;
            if (rh > 0) {
                batch.flush();
                Gdx.gl20.glScissor(px, ry, pw, rh);
                batch.draw(this.ready, x, y, w, h);
            }
            // Empty area scissors
            int ey = ry + rh;
            int eh = (int) ((h2 + 2 * dh) * ppu_y) - th - rh + 2;
            batch.flush();
            Gdx.gl20.glScissor(px, ey, pw, eh);
            batch.draw(this.empty, x, y, w, h);
            batch.flush();
            Gdx.gl20.glDisable(GL20.GL_SCISSOR_TEST);
        }
    }

}
