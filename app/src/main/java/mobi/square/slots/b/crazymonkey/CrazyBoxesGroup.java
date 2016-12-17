package mobi.square.slots.b.crazymonkey;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

import mobi.square.slots.api.Connection;
import mobi.square.slots.b.crazymonkey.CrazyRopesGroup.MonkeyAnim;
import mobi.square.slots.classes.Machine;
import mobi.square.slots.config.AppConfig;
import mobi.square.slots.ui.DrawableActor;
import mobi.square.slots.ui.GameHeader;
import mobi.square.slots.ui.IntClickListener;

public class CrazyBoxesGroup extends Group {

    private final MonkeyAnim monkey;
    private final DrawableActor background;
    private final DrawableActor label;
    private final CrazyBox[] boxes;

    private CrazyMonkeyListener listener;

    private CrazyBoxesGroup(Texture background, TextureAtlas atlas) {
        this.background = DrawableActor.newInstance(background);
        super.addActor(this.background);
        this.label = DrawableActor.newInstance(atlas.findRegion(Connection.getTextureLanguage("find_prize")));
        super.addActor(this.label);
        this.monkey = MonkeyAnim.newInstance(atlas);
        super.addActor(this.monkey);
        this.boxes = new CrazyBox[]{
                CrazyBox.newInstance(atlas),
                CrazyBox.newInstance(atlas)
        };
        for (int i = 0; i < this.boxes.length; i++) {
            this.boxes[i].addListener(new IntClickListener(i) {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (listener != null) {
                        listener.clicked(super.value);
                    }
                }
            });
            super.addActor(this.boxes[i]);
        }
        this.listener = null;
    }

    public static CrazyBoxesGroup newInstance(Texture background, TextureAtlas atlas) {
        CrazyBoxesGroup instance = new CrazyBoxesGroup(background, atlas);
        instance.setBounds(0f, 0f, AppConfig.VIEWPORT_WIDTH, AppConfig.VIEWPORT_HEIGHT - GameHeader.HEADER_HEIGHT);
        instance.label.setBounds(272f, 340f, 480f, 110f);
        instance.boxes[0].setBounds(100f, 60f, 200f, 210f);
        instance.boxes[1].setBounds(720f, 60f, 200f, 210f);
        instance.monkey.setBounds(390f, 40f, 240f, 270f);
        instance.monkey.playChooseAction();
        return instance;
    }

    @Override
    public void setBounds(float x, float y, float width, float height) {
        super.setBounds(x, y, width, height);
        this.background.setBounds(0f, 0f, width, height);
    }

    public void setHelmeted(boolean value) {
        this.monkey.setHelmeted(value);
    }

    public void setListener(CrazyMonkeyListener listener) {
        this.listener = listener;
    }

    public void openBoxex(boolean l_win, boolean r_win, int index) {
        if (l_win) {
            this.boxes[0].showWin();
        } else this.boxes[0].showLoss();
        if (r_win) {
            this.boxes[1].showWin();
        } else this.boxes[1].showLoss();
        if ((index == 0 && l_win) || (index == 1 && r_win)) {
            this.monkey.playSuperWinAction();
        } else this.monkey.playSuperLossAction();
    }

    public interface CrazyMonkeyListener {
        void clicked(int index);
    }

    private static class CrazyBox extends Actor {
        private static final float TIME = .2f;
        private final TextureRegion[][] textures;
        private int state;
        private int frame;
        private float time;

        private CrazyBox(TextureAtlas atlas) {
            this.textures = new TextureRegion[][]{
                    Machine.loadTextures(atlas, "box_none", 6, 0),
                    Machine.loadTextures(atlas, Connection.getTextureLanguage("box_win"), 6, 0),
                    Machine.loadTextures(atlas, Connection.getTextureLanguage("box_loss"), 6, 0)
            };
            this.state = 0;
            this.frame = 0;
            this.time = 0f;
        }

        public static CrazyBox newInstance(TextureAtlas atlas) {
            CrazyBox instance = new CrazyBox(atlas);
            return instance;
        }

        @Override
        public void act(float delta) {
            this.time += delta;
            while (this.time >= TIME) {
                this.time -= TIME;
                this.frame++;
                if (this.frame >= this.textures[0].length) {
                    this.frame = 0;
                }
            }
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            batch.draw(
                    this.textures[this.state][this.frame],
                    super.getX(),
                    super.getY(),
                    super.getWidth(),
                    super.getHeight()
            );
        }

        public void showWin() {
            this.state = 1;
        }

        public void showLoss() {
            this.state = 2;
        }
    }

}
