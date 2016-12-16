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
import mobi.square.slots.ui.GameHeader;
import mobi.square.slots.ui.Resizable;
import mobi.square.slots.ui.bonus.CrazyMonkeyRopesGroup.CrazyMonkeyAward;
import mobi.square.slots.ui.bonus.CrazyMonkeyRopesGroup.CrazyMonkeyHerself;
import mobi.square.slots.ui.bonus.CrazyMonkeyRopesGroup.GameOverWindow;

public class CrazyMonkeyBoxesGroup extends Group implements Resizable {

    private final Texture background;
    private final CrazyMonkeyHerself monkey;
    private final CrazyMonkeyAward label;
    private final CrazyMonkeyBox[] boxes;
    private final GameOverWindow game_over;
    private BoxOpenedListener listener;

    private CrazyMonkeyBoxesGroup(Texture background, TextureAtlas atlas, TextureAtlas common) {
        super();
        this.background = background;
        this.boxes = new CrazyMonkeyBox[2];
        for (int i = 0; i < this.boxes.length; i++) {
            this.boxes[i] = CrazyMonkeyBox.newInstance(atlas, i == 0);
            this.boxes[i].addListener(new BoxListener(i == 0) {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (listener != null) {
                        listener.opened(super.left);
                    }
                }
            });
            super.addActor(this.boxes[i]);
        }
        this.monkey = CrazyMonkeyHerself.newInstance(common);
        this.game_over = GameOverWindow.newInstance(common);
        this.label = CrazyMonkeyAward.newInstance(common);
        super.addActor(this.monkey);
        super.addActor(this.label);
        super.addActor(this.game_over);
        this.listener = null;
    }

    public static CrazyMonkeyBoxesGroup newInstance(Texture background, TextureAtlas atlas, TextureAtlas common) {
        CrazyMonkeyBoxesGroup instance = new CrazyMonkeyBoxesGroup(background, atlas, common);
        instance.setBounds(0f, 0f, AppConfig.VIEWPORT_WIDTH, AppConfig.VIEWPORT_HEIGHT - GameHeader.HEADER_HEIGHT);
        instance.boxes[0].setBounds(130f, 30f, 229f, 237f);
        instance.boxes[1].setBounds(670f, 30f, 229f, 237f);
        instance.monkey.setBounds(410f, 40f, 204f, 322f);
        instance.label.setBounds(257f, instance.getHeight() - 85f, 509f, 105f);
        instance.game_over.setBounds(230f, 154f, 564f, 236f);
        instance.game_over.setVisible(false);
        return instance;
    }

    public void setBoxListener(BoxOpenedListener listener) {
        this.listener = listener;
    }

    public void setTotalAward(int award) {
        this.label.setAward(award);
    }

    public void open(boolean left, boolean win) {
        this.boxes[left ? 0 : 1].open(win);
    }

    public void showGameOverWindow() {
        this.game_over.setTextGameOver();
        this.game_over.setVisible(true);
    }

    public void showSuperPrizeWindow() {
        this.game_over.setTextSuperPrize();
        this.game_over.setVisible(true);
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

    public interface BoxOpenedListener {
        void opened(boolean left);
    }

    private static class CrazyMonkeyBox extends Actor {
        private final TextureRegionDrawable box_tex;
        private final TextureRegionDrawable anvil_tex;
        private final TextureRegionDrawable banana_tex;
        private final float[] bounds;
        private int state;

        private CrazyMonkeyBox(TextureAtlas atlas, boolean left) {
            super();
            this.box_tex = new TextureRegionDrawable(atlas.findRegion(left ? "box_left" : "box_right"));
            this.banana_tex = new TextureRegionDrawable(atlas.findRegion("prize_banana"));
            this.anvil_tex = new TextureRegionDrawable(atlas.findRegion("prize_anvil"));
            this.bounds = new float[4];
            this.state = 0;
        }

        public static CrazyMonkeyBox newInstance(TextureAtlas atlas, boolean left) {
            CrazyMonkeyBox instance = new CrazyMonkeyBox(atlas, left);
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
            switch (this.state) {
                case 1:
                    this.banana_tex.draw(batch, x, y + .1f * height, 1.074f * width, .7806f * height);
                    break;
                case 2:
                    this.anvil_tex.draw(batch, x, y + .1f * height, 1.074f * width, .7806f * height);
                    break;
                default:
                    this.box_tex.draw(batch, x, y, width, height);
                    break;
            }
        }

        @Override
        public void setBounds(float x, float y, float width, float height) {
            super.setBounds(x, y, width, height);
            this.bounds[0] = x;
            this.bounds[1] = y;
            this.bounds[2] = width;
            this.bounds[3] = height;
        }
    }

    private class BoxListener extends ClickListener {
        protected final boolean left;

        public BoxListener(boolean left) {
            super();
            this.left = left;
        }
    }

}
