package mobi.square.slots.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import mobi.square.slots.api.AndroidApi;
import mobi.square.slots.api.Connection;
import mobi.square.slots.config.AppConfig;
import mobi.square.slots.tools.FontsFactory;

public class GameHeader extends Group implements Resizable {

    public static final float HEADER_HEIGHT = 60f;
    private final MoneyLabel money_label;
    private final BackButton button_back;
    private final SettingsButton button_settings;
    private final CupButton button_cup;
    private final TopButton button_top;
    private NinePatchDrawable background;

    private GameHeader(TextureAtlas atlas) {
        super();
        NinePatch patch = atlas.createPatch("header_background");
        this.background = new NinePatchDrawable(patch);
        this.money_label = MoneyLabel.newInstance(atlas);
        this.button_back = new BackButton(atlas);
        this.button_settings = new SettingsButton(atlas);
        this.button_cup = new CupButton(atlas);
        this.button_top = new TopButton(atlas);
        super.addActor(this.money_label);
        super.addActor(this.button_back);
        super.addActor(this.button_settings);
        if (AndroidApi.ONLINE) {
            super.addActor(this.button_cup);
            super.addActor(this.button_top);
        }
    }

    public static GameHeader newInstance(TextureAtlas atlas) {
        GameHeader instance = new GameHeader(atlas);
        float header_position = AppConfig.VIEWPORT_HEIGHT - HEADER_HEIGHT;
        instance.setBounds(0f, header_position, AppConfig.VIEWPORT_WIDTH, HEADER_HEIGHT);
        instance.money_label.setBounds(605, 5, 370, 55);
        instance.button_back.setBounds(50, 10, 110, 46);
        instance.button_settings.setBounds(178, 10, 46, 46);
        instance.button_cup.setBounds(245, 8, 160, 50);
        instance.button_top.setBounds(425, 8, 160, 50);
        instance.button_cup.setDisabled(true);
        return instance;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        this.background = new NinePatchDrawable(Connection.getManager().get("atlas/Header.pack", TextureAtlas.class).createPatch("header_background"));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        this.background.draw(batch, super.getX(), super.getY(), super.getWidth(), super.getHeight());
        super.draw(batch, parentAlpha);
    }

    @Override
    public void resize(int width, int height) {
        this.button_back.resize(width, height);
        this.button_cup.resize(width, height);
        this.button_top.resize(width, height);
        this.money_label.resize(width, height);
    }

    public void setCupTimer(int time, boolean active) {
        this.button_cup.setTimer(time, active);
    }

    public BackButton getBackButton() {
        return this.button_back;
    }

    public SettingsButton getSettingsButton() {
        return this.button_settings;
    }

    public CupButton getCupButton() {
        return this.button_cup;
    }

    public TopButton getTopButton() {
        return this.button_top;
    }

    public Button getBankButton() {
        return this.money_label.getBankButton();
    }

    public MoneyLabel getMoneyLabel() {
        return this.money_label;
    }

    public class BackButton extends Button implements Resizable {
        private final PixelLabel label;

        private BackButton(TextureAtlas atlas) {
            super(new ButtonStyle());
            super.getStyle().up = new TextureRegionDrawable(atlas.findRegion("button_lobby_active"));
            super.getStyle().down = new TextureRegionDrawable(atlas.findRegion("button_lobby_pressed"));
            super.getStyle().disabled = new TextureRegionDrawable(atlas.findRegion("button_lobby_inactive"));
            super.getStyle().pressedOffsetY = -2f;
            LabelStyle ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("Taurus.ttf", 20);
            ls.font.setScale(1f, 1f);
            ls.font.setUseIntegerPositions(true);
            this.label = new PixelLabel("", ls);
            this.label.setAlignment(Align.center, Align.center);
            this.setLobbyText();
            super.addActor(this.label);
        }

        @Override
        public void setBounds(float x, float y, float width, float height) {
            this.label.setBounds(width * .15f, 0f, width * .85f, height);
            super.setBounds(x, y, width, height);
        }

        @Override
        public void resize(int width, int height) {
            this.label.resize(width, height);
        }

        public void setLobbyText() {
            this.label.setText(Connection.getString("header_button_lobby"));
        }

        public void setLoginText() {
            this.label.setText(Connection.getString("header_button_login"));
        }
    }

    public class SettingsButton extends Button {
        private SettingsButton(TextureAtlas atlas) {
            super(new ButtonStyle());
            super.getStyle().up = new TextureRegionDrawable(atlas.findRegion("button_settings_active"));
            super.getStyle().down = new TextureRegionDrawable(atlas.findRegion("button_settings_pressed"));
            super.getStyle().disabled = new TextureRegionDrawable(atlas.findRegion("button_settings_inactive"));
        }
    }

    public class CupButton extends Button implements Resizable {
        private static final int LABEL_TIME = 3;
        private static final int TIMER_TIME = 10;
        private static final int CHANGE_TIME = 1;
        private final PixelLabel label;
        private final PixelLabel timer_label;
        private float time;
        private boolean active;
        private float change_time;
        private boolean label_is_visible;
        private float label_alpha;
        private float timer_alpha;

        private CupButton(TextureAtlas atlas) {
            super(new ButtonStyle());
            super.getStyle().up = new TextureRegionDrawable(atlas.findRegion("button_cup_active"));
            super.getStyle().down = new TextureRegionDrawable(atlas.findRegion("button_cup_pressed"));
            super.getStyle().disabled = new TextureRegionDrawable(atlas.findRegion("button_cup_inactive"));
            super.getStyle().pressedOffsetY = -2f;
            LabelStyle ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("Taurus.ttf", 28);
            this.label = new PixelLabel(Connection.getString("header_button_cup"), ls);
            this.label.setAlignment(Align.center, Align.center);
            super.addActor(this.label);
            ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("whitrabt.ttf", 24);
            this.timer_label = new PixelLabel("00:00:00", ls);
            this.timer_label.setAlignment(Align.center, Align.center);
            super.addActor(this.timer_label);
            this.timer_label.setColor(1f, 1f, 1f, 0f);
            this.active = false;
            this.time = 0f;
            this.change_time = 0f;
            this.label_is_visible = true;
            this.label_alpha = 1f;
            this.timer_alpha = 0f;
        }

        private void setTimer(int time, boolean active) {
            this.time = (float) time;
            this.active = active;
            super.setDisabled(!active);
            this.label.setColor(1f, 1f, 1f, 1f);
            this.timer_label.setColor(1f, 1f, 1f, 0f);
            this.change_time = (float) LABEL_TIME;
            this.label_is_visible = true;
            this.label_alpha = 1f;
            this.timer_alpha = 0f;
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            if (this.time > 1f) {
                int last_time = (int) this.time;
                this.time -= delta;
                int new_time = (int) this.time;
                if (this.time <= 1f) {
                    this.time = 0f;
                    this.active = !this.active;
                    super.setDisabled(!this.active);
                    this.label.setColor(1f, 1f, 1f, 1f);
                    this.timer_label.setColor(1f, 1f, 1f, 0f);
                } else {
                    if (!this.active) {
                        if (this.change_time <= 0f) {
                            if (this.label_is_visible) {
                                this.label_alpha -= delta / (float) CHANGE_TIME;
                                if (this.label_alpha <= .3f)
                                    this.timer_alpha += delta / (float) CHANGE_TIME;
                                if (this.label_alpha <= 0f) this.label_alpha = 0f;
                                if (this.timer_alpha >= 1f) this.timer_alpha = 1f;
                                if (this.label_alpha <= 0f && this.timer_alpha >= 1f) {
                                    this.label_alpha = 0f;
                                    this.timer_alpha = 1f;
                                    this.label_is_visible = false;
                                    this.change_time = TIMER_TIME;
                                }
                            } else {
                                this.timer_alpha -= delta / (float) CHANGE_TIME;
                                if (this.timer_alpha <= .3f)
                                    this.label_alpha += delta / (float) CHANGE_TIME;
                                if (this.timer_alpha <= 0f) this.timer_alpha = 0f;
                                if (this.label_alpha >= 1f) this.label_alpha = 1f;
                                if (this.label_alpha >= 1f && this.timer_alpha <= 0f) {
                                    this.label_alpha = 1f;
                                    this.timer_alpha = 0f;
                                    this.label_is_visible = true;
                                    this.change_time = LABEL_TIME;
                                }
                            }
                            this.label.setColor(1f, 1f, 1f, this.label_alpha);
                            this.timer_label.setColor(1f, 1f, 1f, this.timer_alpha);
                        } else this.change_time -= delta;
                    }
                    if (last_time != new_time) {
                        StringBuilder builder = new StringBuilder();
                        int hours = new_time / 3600;
                        new_time -= hours * 3600;
                        int minutes = new_time / 60;
                        new_time -= minutes * 60;
                        builder.append(hours);
                        builder.append(":");
                        if (minutes < 10)
                            builder.append("0");
                        builder.append(minutes);
                        builder.append(":");
                        if (new_time < 10)
                            builder.append("0");
                        builder.append(new_time);
                        this.timer_label.setText(builder.toString());
                    }
                }
            }
        }

        @Override
        public void setBounds(float x, float y, float width, float height) {
            this.label.setBounds(0f, 0f, width, height);
            this.timer_label.setBounds(0f, 0f, width, height);
            super.setBounds(x, y, width, height);
        }

        @Override
        public void resize(int width, int height) {
            this.label.resize(width, height);
            this.timer_label.resize(width, height);
        }
    }

    public class TopButton extends Button implements Resizable {
        private final PixelLabel label;

        private TopButton(TextureAtlas atlas) {
            super(new ButtonStyle());
            super.getStyle().up = new TextureRegionDrawable(atlas.findRegion("button_top_active"));
            super.getStyle().down = new TextureRegionDrawable(atlas.findRegion("button_top_pressed"));
            super.getStyle().disabled = new TextureRegionDrawable(atlas.findRegion("button_top_inactive"));
            super.getStyle().pressedOffsetY = -2f;
            LabelStyle ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("Taurus.ttf", 28);
            this.label = new PixelLabel(Connection.getString("header_button_leaders"), ls);
            this.label.setAlignment(Align.center, Align.center);
            super.addActor(this.label);
        }

        @Override
        public void setBounds(float x, float y, float width, float height) {
            this.label.setBounds(0f, 0f, width, height);
            super.setBounds(x, y, width, height);
        }

        @Override
        public void resize(int width, int height) {
            this.label.resize(width, height);
        }
    }

}
