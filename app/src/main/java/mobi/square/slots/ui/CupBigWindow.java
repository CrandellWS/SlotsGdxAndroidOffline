package mobi.square.slots.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import mobi.square.slots.config.AppConfig;
import mobi.square.slots.tools.FontsFactory;
import mobi.square.slots.ui.MessageWindow.CloseButton;

public class CupBigWindow extends Group implements Resizable {

    private final DrawableActor bg_title;
    private final DrawableActor bg_glance;
    private final DrawableActor bg_main;
    private final CloseButton close_button;
    private final PixelLabel title_label;

    protected CupBigWindow(TextureAtlas atlas, TextureAtlas windows_atlas, String title) {
        super();
        this.bg_title = DrawableActor.newInstance(atlas.findRegion("window_title_background"));
        this.bg_glance = DrawableActor.newInstance(atlas.findRegion("window_title_glance"));
        this.bg_main = DrawableActor.newInstance(atlas.findRegion("window_big_background"));
        super.addActor(this.bg_main);
        super.addActor(this.bg_title);
        super.addActor(this.bg_glance);
        LabelStyle ls = new LabelStyle();
        ls.font = FontsFactory.getAsync("Junegull.ttf", 30);
        ls.fontColor = Color.WHITE;
        this.title_label = new PixelLabel(title, ls);
        this.title_label.setAlignment(Align.center);
        super.addActor(this.title_label);
        this.close_button = CloseButton.newInstance(windows_atlas);
        this.close_button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (close_button.isDisabled()) return;
                hide();
            }
        });
        super.addActor(this.close_button);
    }

    protected void initialize() {
        float width = (float) AppConfig.VIEWPORT_WIDTH;
        float height = (float) AppConfig.VIEWPORT_HEIGHT - GameHeader.HEADER_HEIGHT;
        super.setBounds(0f, 0f, width, height);
        this.bg_title.setBounds(0f, height - 96f, width, 96f);
        this.bg_glance.setBounds(5f, height - 56f, width - 200f, 50f);
        this.bg_main.setBounds(0f, 0f, width, height - 96f);
        this.title_label.setBounds(0f, height - 70f, width - 140f, 64f);
        this.close_button.setBounds(width - 195f, height - 69f, 190f, 65f);
        this.close_button.setTextClose();
        this.hide();
    }

    @Override
    public void resize(int width, int height) {
        this.close_button.resize(width, height);
        this.title_label.resize(width, height);
    }

    public void show() {
        super.setVisible(true);
    }

    public void hide() {
        super.setVisible(false);
    }

}
