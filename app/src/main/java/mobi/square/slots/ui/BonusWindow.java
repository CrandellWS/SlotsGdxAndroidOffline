package mobi.square.slots.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import mobi.square.slots.api.Connection;
import mobi.square.slots.tools.FontsFactory;

public class BonusWindow extends Window implements Resizable {

    private static final float MESSAGE_TIME = 1.5f;

    private final PixelLabel title;
    private final BonusBox[] boxes;
    private final BonusMessage message;
    private BonusWindowListener listener;

    private int index;
    private boolean clicked;
    private boolean opened;
    private float open_time;

    private BonusWindow(WindowStyle style, TextureAtlas atlas) {
        super("", style);
        LabelStyle ls = new LabelStyle();
        ls.font = FontsFactory.getAsync("Junegull.ttf", 24);
        ls.fontColor = new Color(0.314f, 0.188f, 0f, 1f);
        this.title = new PixelLabel(Connection.getString("bonus_rules"), ls);
        this.title.setAlignment(Align.center);
        super.addActor(this.title);
        this.message = new BonusMessage(atlas);
        this.message.hide();
        this.boxes = new BonusBox[]{
                new BonusBox(atlas),
                new BonusBox(atlas),
                new BonusBox(atlas)
        };
        for (int i = 0; i < 3; i++) {
            super.addActor(this.boxes[i]);
            this.boxes[i].addListener(new IntClickListener(i) {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (!clicked) {
                        index = super.value;
                        clicked = true;
                        if (listener != null) {
                            listener.boxClicked();
                        }
                    }
                }
            });
        }
        super.addActor(this.message);
        this.listener = null;
        this.clicked = false;
        this.opened = false;
        this.open_time = 0f;
        this.index = 0;
    }

    public static BonusWindow newInstance(TextureAtlas atlas) {
        WindowStyle style = new WindowStyle();
        style.titleFont = FontsFactory.getAsync("Junegull.ttf", 18);
        style.titleFontColor = Color.WHITE;
        style.background = new TextureRegionDrawable(atlas.findRegion("bonus_window"));
        BonusWindow instance = new BonusWindow(style, atlas);
        instance.setBounds(150f, 90f, 724f, 380f);
        instance.setMovable(false);
        instance.setModal(true);
        instance.title.setBounds(0f, 291f, 724f, 80f);
        instance.boxes[0].setBounds(62f, 50f, 200f, 180f);
        instance.boxes[1].setBounds(262f, 50f, 200f, 180f);
        instance.boxes[2].setBounds(462f, 50f, 200f, 180f);
        instance.message.setBounds(100f, 60f, 524f, 280f);
        instance.hide();
        return instance;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (this.opened) {
            this.open_time += delta;
            if (this.open_time >= MESSAGE_TIME) {
                this.hide();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        this.title.resize(width, height);
        this.message.resize(width, height);
    }

    public void show() {
        for (int i = 0; i < 3; this.boxes[i++].setOpened(false)) ;
        this.message.hide();
        this.clicked = false;
        this.opened = false;
        super.setVisible(true);
    }

    public void hide() {
        super.setVisible(false);
    }

    public void openBox(int value) {
        this.boxes[this.index].setOpened(true);
        this.message.show(value);
        this.open_time = 0f;
        this.opened = true;
    }

    public void setListener(BonusWindowListener listener) {
        this.listener = listener;
    }

    public interface BonusWindowListener {
        void boxClicked();
    }

    private static class BonusBox extends Actor {
        private final TextureRegion closed;
        private final TextureRegion opened;
        private boolean state;

        public BonusBox(TextureAtlas atlas) {
            this.closed = atlas.findRegion("box_closed");
            this.opened = atlas.findRegion("box_opened");
            this.state = false;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            batch.draw(this.state ? this.opened : this.closed, super.getX(), super.getY(), super.getWidth(), super.getHeight());
        }

        public void setOpened(boolean opened) {
            this.state = opened;
        }
    }

    private static class BonusMessage extends Group implements Resizable {
        private final TextureRegion background;
        private final PixelLabel label;

        public BonusMessage(TextureAtlas atlas) {
            this.background = atlas.findRegion("bonus_award");
            LabelStyle ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("Junegull.ttf", 36);
            ls.fontColor = new Color(0.812f, 0f, 0f, 1f);
            this.label = new PixelLabel("1000", ls);
            this.label.setAlignment(Align.center);
            super.addActor(this.label);
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            batch.draw(this.background, super.getX(), super.getY(), super.getWidth(), super.getHeight());
            super.draw(batch, parentAlpha);
        }

        @Override
        public void setBounds(float x, float y, float width, float height) {
            super.setBounds(x, y, width, height);
            this.label.setBounds(0f, 0f, width, .9f * height);
        }

        @Override
        public void resize(int width, int height) {
            this.label.resize(width, height);
        }

        public void show(int value) {
            this.setValue(value);
            super.setVisible(true);
        }

        public void hide() {
            super.setVisible(false);
        }

        public void setValue(int value) {
            this.label.setText(String.valueOf(value));
        }
    }

}
