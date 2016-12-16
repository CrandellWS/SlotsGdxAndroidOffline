package mobi.square.slots.ui.bonus;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import mobi.square.slots.config.AppConfig;
import mobi.square.slots.ui.DrawableActor;
import mobi.square.slots.ui.GameHeader;
import mobi.square.slots.ui.Resizable;
import mobi.square.slots.ui.bonus.ResidentLocksGroup.GameOverWindow;
import mobi.square.slots.ui.bonus.ResidentLocksGroup.ResidentAward;

public class ResidentDoorsGroup extends Group implements Resizable {

    private final Texture background;
    private final ResidentDoor[] doors;
    private final ResidentAward label;
    private final DrawableActor resident;
    private final GameOverWindow game_over;
    private DoorOpenedListener listener;

    private ResidentDoorsGroup(Texture background, TextureAtlas atlas, TextureAtlas common) {
        this.background = background;
        this.resident = DrawableActor.newInstance(new TextureRegionDrawable(common.findRegion("resident")));
        this.game_over = GameOverWindow.newInstance(common);
        this.label = ResidentAward.newInstance(common);
        this.doors = new ResidentDoor[2];
        for (int i = 0; i < 2; i++) {
            this.doors[i] = ResidentDoor.newInstance(atlas);
            this.doors[i].addListener(new DoorListener(i == 0) {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (listener != null) {
                        listener.opened(super.left);
                    }
                }
            });
            super.addActor(this.doors[i]);
        }
        super.addActor(this.resident);
        super.addActor(this.label);
        super.addActor(this.game_over);
    }

    public static ResidentDoorsGroup newInstance(Texture background, TextureAtlas atlas, TextureAtlas common) {
        ResidentDoorsGroup instance = new ResidentDoorsGroup(background, atlas, common);
        instance.setBounds(0f, 0f, AppConfig.VIEWPORT_WIDTH, AppConfig.VIEWPORT_HEIGHT - GameHeader.HEADER_HEIGHT);
        instance.doors[0].setBounds(170f, 54f, 327f, 388f);
        instance.doors[1].setBounds(590f, 54f, 327f, 388f);
        instance.resident.setBounds(-30f, 0f, 201f, 319f);
        instance.label.setBounds(320f, 434f, 379f, 67f);
        instance.game_over.setBounds(262f, 160f, 500f, 157f);
        instance.game_over.setVisible(false);
        return instance;
    }

    public void setDoorListener(DoorOpenedListener listener) {
        this.listener = listener;
    }

    public void setTotalAward(int award) {
        this.label.setAward(award);
    }

    public void showGameOverWindow() {
        this.game_over.setTextGameOver();
        this.game_over.setVisible(true);
    }

    public void showSuperPrizeWindow() {
        this.game_over.setTextSuperPrize();
        this.game_over.setVisible(true);
    }

    public void open(boolean left, boolean win) {
        this.doors[left ? 0 : 1].open(win);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(this.background, super.getX(), super.getY(), super.getWidth(), super.getHeight());
        super.draw(batch, parentAlpha);
    }

    @Override
    public void resize(int width, int height) {
        this.game_over.resize(width, height);
        this.label.resize(width, height);
    }

    public interface DoorOpenedListener {
        void opened(boolean left);
    }

    private static class ResidentDoor extends Actor {
        private final TextureRegionDrawable opened_tex;
        private final TextureRegionDrawable closed_tex;
        private final TextureRegionDrawable[] light_tex;
        private final TextureRegionDrawable girl_tex;
        private int state;

        private ResidentDoor(TextureAtlas atlas) {
            this.opened_tex = new TextureRegionDrawable(atlas.findRegion("door_opened"));
            this.closed_tex = new TextureRegionDrawable(atlas.findRegion("door_closed"));
            this.girl_tex = new TextureRegionDrawable(atlas.findRegion("girl"));
            this.light_tex = new TextureRegionDrawable[]{
                    new TextureRegionDrawable(atlas.findRegion("light_yellow")),
                    new TextureRegionDrawable(atlas.findRegion("light_green")),
                    new TextureRegionDrawable(atlas.findRegion("light_red"))
            };
            this.state = 0;
        }

        public static ResidentDoor newInstance(TextureAtlas atlas) {
            ResidentDoor instance = new ResidentDoor(atlas);
            return instance;
        }

        public void open(boolean win) {
            this.state = win ? 1 : 2;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            float x = super.getX();
            float y = super.getY();
            float width = super.getWidth();
            float height = super.getHeight();
            if (this.state != 0) {
                this.opened_tex.draw(batch, x, y, width, height);
                if (this.state == 1) {
                    this.girl_tex.draw(batch, x + .4f * width, y + .05f * height, .434f * width, .755f * height);
                }
            } else this.closed_tex.draw(batch, x, y, width, height);
            this.light_tex[this.state].draw(batch, x, y + .7f * height, .1835f * width, .1546f * height);
        }
    }

    private class DoorListener extends ClickListener {
        protected final boolean left;

        public DoorListener(boolean left) {
            super();
            this.left = left;
        }
    }

}
