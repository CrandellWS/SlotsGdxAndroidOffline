package mobi.square.slots.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import mobi.square.slots.api.AndroidApi;
import mobi.square.slots.api.Connection;
import mobi.square.slots.config.AppConfig;
import mobi.square.slots.stages.Basic;
import mobi.square.slots.tools.FontsFactory;

public class SettingsWindow extends Group implements Resizable {

    private static final float SHOW_SPEED = 2000f;

    private final Basic basic_parent;
    private final TextureRegionDrawable background;
    private final PixelLabel label_title;
    private final PixelLabel label_notify;
    private final PixelLabel label_sound;
    private final PixelLabel label_id;
    private final HideButton button_hide;
    private final InviteButton button_invite;
    private final SettingsCheckBox box_notify;
    private final SettingsCheckBox box_sound;

    private float target_x;
    private int state;

    private SettingsWindow(Basic parent, TextureAtlas atlas) {
        super();
        this.basic_parent = parent;
        this.background = new TextureRegionDrawable(atlas.findRegion("settings_background"));
        LabelStyle ls = new LabelStyle();
        ls.font = FontsFactory.getAsync("Junegull.ttf", 30);
        ls.fontColor = Color.WHITE;
        this.label_title = new PixelLabel(Connection.getString("settings_title"), ls);
        ls = new LabelStyle();
        ls.font = FontsFactory.getAsync("Taurus.ttf", 28);
        ls.fontColor = Color.WHITE;
        this.label_notify = new PixelLabel(Connection.getString("settings_option_notify"), ls);
        ls = new LabelStyle();
        ls.font = FontsFactory.getAsync("Taurus.ttf", 28);
        ls.fontColor = Color.WHITE;
        this.label_sound = new PixelLabel(Connection.getString("settings_option_sound"), ls);
        ls = new LabelStyle();
        ls.font = FontsFactory.getAsync("Taurus.ttf", 20);
        ls.fontColor = Color.WHITE;
        this.label_id = new PixelLabel("ID: 0", ls);
        this.button_hide = HideButton.newInstance(atlas);
        this.button_invite = InviteButton.newInstance(atlas);
        this.button_invite.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (button_invite.isDisabled()) return;
                Connection.getInstance().setDeviceId(null);
                basic_parent.parent_screen.parent.showLoginScreenAuth();
            }
        });
        this.box_notify = SettingsCheckBox.newInstance(atlas);
        this.box_sound = SettingsCheckBox.newInstance(atlas);
        super.addActor(this.label_title);
        super.addActor(this.label_notify);
        super.addActor(this.label_sound);
        super.addActor(this.label_id);
        super.addActor(this.button_hide);
        if (AndroidApi.ONLINE)
            super.addActor(this.button_invite);
        super.addActor(this.box_notify);
        super.addActor(this.box_sound);
        this.target_x = 0f;
        this.state = 0;
        this.button_hide.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (button_hide.isDisabled()) return;
                hide();
            }
        });
    }

    public static SettingsWindow newInstance(Basic parent, TextureAtlas atlas) {
        SettingsWindow instance = new SettingsWindow(parent, atlas);
        instance.setBounds(AppConfig.VIEWPORT_WIDTH - 460f, 0f, 460f, AppConfig.VIEWPORT_HEIGHT - GameHeader.HEADER_HEIGHT);
        instance.label_title.setBounds(50f, 400f, 100f, 50f);
        instance.label_notify.setBounds(50f, 300f, 100f, 50f);
        instance.label_sound.setBounds(50f, 220f, 100f, 50f);
        instance.label_id.setBounds(50f, 450f, 100f, 50f);
        instance.button_hide.setBounds(300f, 395f, 160f, 60f);
        instance.button_invite.setBounds(100f, 130f, 280f, 54f);
        instance.box_notify.setBounds(300f, 295f, 100f, 60f);
        instance.box_sound.setBounds(300f, 210f, 100f, 60f);
        instance.hideNow();
        return instance;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        switch (this.state) {
            case 1:
                super.setX(super.getX() - delta * SHOW_SPEED);
                if (super.getX() <= this.target_x) {
                    super.setX(this.target_x);
                    super.setVisible(true);
                    this.state = 3;
                }
                break;
            case 2:
                super.setX(super.getX() + delta * SHOW_SPEED);
                if (super.getX() >= AppConfig.VIEWPORT_WIDTH) {
                    super.setX(AppConfig.VIEWPORT_WIDTH);
                    super.setVisible(false);
                    this.state = 0;
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        this.background.draw(batch, super.getX(), super.getY(), super.getWidth(), super.getHeight());
        super.draw(batch, parentAlpha);
    }

    @Override
    public void setBounds(float x, float y, float width, float height) {
        super.setBounds(x, y, width, height);
        this.target_x = x;
    }

    @Override
    public void resize(int width, int height) {
        this.button_hide.resize(width, height);
        this.button_invite.resize(width, height);
        this.label_title.resize(width, height);
        this.label_sound.resize(width, height);
        this.label_notify.resize(width, height);
        this.label_id.resize(width, height);
    }

    public void show() {
        if (this.state == 3) return;
        super.setVisible(true);
        this.state = 1;
    }

    public void hide() {
        if (this.state == 0) return;
        this.state = 2;
    }

    public void hideNow() {
        this.state = 0;
        super.setX(AppConfig.VIEWPORT_WIDTH);
        super.setVisible(false);
    }

    public void setUserId(String id) {
        this.label_id.setText("User ID: " + id);
    }

    public HideButton getHideButton() {
        return this.button_hide;
    }

    public InviteButton getInviteButton() {
        return this.button_invite;
    }

    public SettingsCheckBox getNotifyBox() {
        return this.box_notify;
    }

    public SettingsCheckBox getSoundBox() {
        return this.box_sound;
    }

    public interface SettingsChecked {
        void checked(boolean checked);
    }

    public static class HideButton extends Button implements Resizable {
        private final PixelLabel label;

        public HideButton(ButtonStyle style) {
            super(style);
            LabelStyle ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("Junegull.ttf", 24);
            ls.fontColor = Color.WHITE;
            this.label = new PixelLabel(Connection.getString("common_hide"), ls);
            this.label.setAlignment(Align.center, Align.center);
            super.addActor(this.label);
        }

        public static HideButton newInstance(TextureAtlas atlas) {
            ButtonStyle style = new ButtonStyle();
            style.up = new TextureRegionDrawable(atlas.findRegion("hide_button_active"));
            style.down = new TextureRegionDrawable(atlas.findRegion("hide_button_pressed"));
            style.disabled = new TextureRegionDrawable(atlas.findRegion("hide_button_inactive"));
            style.pressedOffsetX = 2f;
            HideButton instance = new HideButton(style);
            return instance;
        }

        @Override
        public void setBounds(float x, float y, float width, float height) {
            super.setBounds(x, y, width, height);
            this.label.setBounds(0f, 0f, .9f * width, height);
        }

        @Override
        public void resize(int width, int height) {
            this.label.resize(width, height);
        }
    }

    public static class InviteButton extends Button implements Resizable {
        private final PixelLabel label;

        public InviteButton(ButtonStyle style) {
            super(style);
            LabelStyle ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("Taurus.ttf", 24);
            ls.fontColor = Color.WHITE;
            //this.label = new PixelLabel(Connection.getString("settings_button_friend"), ls);
            this.label = new PixelLabel(Connection.getString("settings_button_logout"), ls);
            this.label.setAlignment(Align.center, Align.center);
            super.addActor(this.label);
        }

        public static InviteButton newInstance(TextureAtlas atlas) {
            ButtonStyle style = new ButtonStyle();
            style.up = new TextureRegionDrawable(atlas.findRegion("invite_button_active"));
            style.down = new TextureRegionDrawable(atlas.findRegion("invite_button_pressed"));
            style.disabled = new TextureRegionDrawable(atlas.findRegion("invite_button_inactive"));
            style.pressedOffsetY = -2f;
            InviteButton instance = new InviteButton(style);
            return instance;
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

    public static class SettingsCheckBox extends Button {
        private final TextureRegionDrawable texture_checked;
        private final TextureRegionDrawable texture_unchecked;
        private boolean checked;
        private SettingsChecked listener;

        private SettingsCheckBox(TextureRegionDrawable texture_checked, TextureRegionDrawable texture_unchecked) {
            super(texture_unchecked, texture_unchecked);
            this.texture_checked = texture_checked;
            this.texture_unchecked = texture_unchecked;
            this.checked = false;
            this.listener = null;
            super.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (isDisabled()) return;
                    setBoxChecked(!checked);
                }
            });
        }

        public static SettingsCheckBox newInstance(TextureAtlas atlas) {
            SettingsCheckBox instance = new SettingsCheckBox(
                    new TextureRegionDrawable(atlas.findRegion("settings_checkbox_active")),
                    new TextureRegionDrawable(atlas.findRegion("settings_checkbox_inactive"))
            );
            return instance;
        }

        public boolean isBoxChecked() {
            return this.checked;
        }

        public void setBoxChecked(boolean checked) {
            this.checked = checked;
            ButtonStyle style = super.getStyle();
            if (checked) {
                style.up = this.texture_checked;
                style.down = this.texture_checked;
            } else {
                style.down = this.texture_unchecked;
                style.up = this.texture_unchecked;
            }
            if (this.listener != null) {
                this.listener.checked(checked);
            }
        }

        public void setListener(SettingsChecked listener) {
            this.listener = listener;
        }
    }

}
