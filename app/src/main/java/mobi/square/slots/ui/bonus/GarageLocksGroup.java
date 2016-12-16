package mobi.square.slots.ui.bonus;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import mobi.square.slots.api.Connection;
import mobi.square.slots.config.AppConfig;
import mobi.square.slots.enums.BoxState;
import mobi.square.slots.tools.FontsFactory;
import mobi.square.slots.ui.GameHeader;
import mobi.square.slots.ui.PixelLabel;
import mobi.square.slots.ui.Resizable;

public class GarageLocksGroup extends Group implements Resizable {

    private final Texture boxes_bg;
    private final Texture locks_bg;
    private final TextureRegionDrawable car_tex;
    private final GarageLock[] locks;
    private final GarageSafe[][] safes;
    private final GarageArrows[] arrows;
    private final AwardLabel label;
    private final SuperKey super_key;
    private final GameOverWindow game_over;
    private SafeOpenedListener safe_listener;
    private boolean opened;

    private GarageLocksGroup(Texture bg_boxes, Texture bg_locks, TextureAtlas atlas, TextureAtlas common) {
        this.safe_listener = null;
        this.boxes_bg = bg_boxes;
        this.locks_bg = bg_locks;
        this.car_tex = new TextureRegionDrawable(atlas.findRegion("car"));
        this.game_over = GameOverWindow.newInstance(common);
        this.super_key = SuperKey.newInstance(atlas);
        this.label = AwardLabel.newInstance(atlas);
        this.opened = false;
        this.locks = new GarageLock[]{
                GarageLock.newInstance(atlas),
                GarageLock.newInstance(atlas),
                GarageLock.newInstance(atlas),
                GarageLock.newInstance(atlas),
                GarageLock.newInstance(atlas)
        };
        this.arrows = new GarageArrows[]{
                GarageArrows.newInstance(atlas),
                GarageArrows.newInstance(atlas),
                GarageArrows.newInstance(atlas),
                GarageArrows.newInstance(atlas),
                GarageArrows.newInstance(atlas)
        };
        this.safes = new GarageSafe[][]{
                new GarageSafe[]{
                        GarageSafe.newInstance(atlas),
                        GarageSafe.newInstance(atlas)
                },
                new GarageSafe[]{
                        GarageSafe.newInstance(atlas),
                        GarageSafe.newInstance(atlas)
                },
                new GarageSafe[]{
                        GarageSafe.newInstance(atlas),
                        GarageSafe.newInstance(atlas)
                },
                new GarageSafe[]{
                        GarageSafe.newInstance(atlas),
                        GarageSafe.newInstance(atlas)
                },
                new GarageSafe[]{
                        GarageSafe.newInstance(atlas),
                        GarageSafe.newInstance(atlas)
                }
        };
        for (int i = 0; i < this.locks.length; super.addActor(this.locks[i++])) ;
        for (int i = 0; i < this.arrows.length; super.addActor(this.arrows[i++])) ;
        for (int i = 0; i < this.safes.length; i++) {
            for (int j = 0; j < this.safes[i].length; j++) {
                this.safes[i][j].addListener(new SafeListener(i, j == 0) {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if (opened) return;
                        if (!safes[super.index][0].isOpened() &&
                                !safes[super.index][1].isOpened() &&
                                safe_listener != null) {
                            safe_listener.opened(super.index, super.left);
                        }
                    }
                });
                super.addActor(this.safes[i][j]);
            }
        }
        super.addActor(this.super_key);
        super.addActor(this.label);
        super.addActor(this.game_over);
    }

    public static GarageLocksGroup newInstance(Texture bg_boxes, Texture bg_locks, TextureAtlas atlas, TextureAtlas common) {
        GarageLocksGroup instance = new GarageLocksGroup(bg_boxes, bg_locks, atlas, common);
        instance.setBounds(0f, 0f, AppConfig.VIEWPORT_WIDTH, AppConfig.VIEWPORT_HEIGHT - GameHeader.HEADER_HEIGHT);
        for (int i = 0; i < instance.locks.length; i++) {
            //instance.locks[i].setBounds(460f, 406f - 90f * (float)i, 96f, 76f);
            instance.locks[i].setBounds(460f, 46f + 90f * (float) i, 96f, 76f);
        }
        for (int i = 0; i < instance.arrows.length; i++) {
            //instance.arrows[i].setBounds(421f, 420f - 88f * (float)i, 178f, 39f);
            instance.arrows[i].setBounds(421f, 68f + 88f * (float) i, 178f, 39f);
        }
        for (int i = 0; i < instance.safes.length; i++) {
            //instance.safes[i][0].setBounds(270f, 394f - 88f * (float)i, 140f, 91f);
            //instance.safes[i][1].setBounds(624f, 394f - 88f * (float)i, 140f, 91f);
            instance.safes[i][0].setBounds(270f, 42f + 88f * (float) i, 140f, 91f);
            instance.safes[i][1].setBounds(624f, 42f + 88f * (float) i, 140f, 91f);
        }
        instance.super_key.setBounds(0f, 0f, 176f, 144f);
        instance.label.setBounds(774f, 380f, 250f, 108f);
        instance.game_over.setBounds(256f, 160f, 504f, 202f);
        instance.game_over.setVisible(false);
        return instance;
    }

    public void setSafeListener(SafeOpenedListener listener) {
        this.safe_listener = listener;
    }

    public void addSuperKeyListener(EventListener listener) {
        this.super_key.addListener(listener);
    }

    public void setSuperKeyUsed(boolean used) {
        this.super_key.setVisible(!used);
    }

    public void setGarageOpened(boolean opened) {
        this.opened = opened;
    }

    public void setTotalAward(int award) {
        this.label.setAward(award);
    }

    public void setSafeState(int index, boolean left, BoxState state) {
        this.safes[index][left ? 0 : 1].setState(state);
    }

    public void setLockOpened(int index, boolean opened) {
        this.locks[index].setOpened(opened);
    }

    public void setArrowsVisible(int index, boolean visible) {
        this.arrows[index].setVisible(visible);
    }

    public void showGameOverWindow(boolean show) {
        this.game_over.setVisible(show);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (this.opened) {
            float x = super.getX();
            float y = super.getY();
            float width = super.getWidth();
            float height = super.getHeight();
            batch.draw(this.boxes_bg, x, y, width, height);
            this.car_tex.draw(batch, x + .28f * width, y, width * .45f, height * .74f);
        } else {
            batch.draw(this.locks_bg, super.getX(), super.getY(), super.getWidth(), super.getHeight());
            super.draw(batch, parentAlpha);
        }
    }

    @Override
    public void resize(int width, int height) {
        this.label.resize(width, height);
        this.game_over.resize(width, height);
    }

    public interface SafeOpenedListener {
        void opened(int index, boolean left);
    }

    private static class GarageLock extends Actor {
        private final TextureRegionDrawable closed_tex;
        private final TextureRegionDrawable opened_tex;
        private boolean opened;

        private GarageLock(TextureAtlas atlas) {
            this.closed_tex = new TextureRegionDrawable(atlas.findRegion("hinges_locked"));
            this.opened_tex = new TextureRegionDrawable(atlas.findRegion("hinges_unlocked"));
            this.opened = false;
        }

        public static GarageLock newInstance(TextureAtlas atlas) {
            GarageLock instance = new GarageLock(atlas);
            return instance;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            if (this.opened) {
                this.opened_tex.draw(batch, super.getX(), super.getY(), super.getWidth(), super.getHeight());
            } else {
                this.closed_tex.draw(batch, super.getX(), super.getY(), super.getWidth(), super.getHeight());
            }
        }

        public void setOpened(boolean opened) {
            this.opened = opened;
        }
    }

    private static class GarageSafe extends Actor {
        private final TextureRegionDrawable closed_tex;
        private final TextureRegionDrawable empty_tex;
        private final TextureRegionDrawable keyin_tex;
        private BoxState state;

        private GarageSafe(TextureAtlas atlas) {
            this.closed_tex = new TextureRegionDrawable(atlas.findRegion("safe_closed"));
            this.empty_tex = new TextureRegionDrawable(atlas.findRegion("safe_empty"));
            this.keyin_tex = new TextureRegionDrawable(atlas.findRegion("safe_key"));
            this.state = BoxState.CLOSED;
        }

        public static GarageSafe newInstance(TextureAtlas atlas) {
            GarageSafe instance = new GarageSafe(atlas);
            return instance;
        }

        public boolean isOpened() {
            return this.state != BoxState.CLOSED;
        }

        public void setState(BoxState state) {
            this.state = state;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            switch (this.state) {
                case OPENED_FAIL:
                    this.empty_tex.draw(batch, super.getX(), super.getY(), super.getWidth(), super.getHeight());
                    break;
                case OPENED_WIN:
                    this.keyin_tex.draw(batch, super.getX(), super.getY(), super.getWidth(), super.getHeight());
                    break;
                default:
                    this.closed_tex.draw(batch, super.getX(), super.getY(), super.getWidth(), super.getHeight());
                    break;
            }
        }
    }

    private static class GarageArrows extends Actor {
        private final TextureRegionDrawable left_tex;
        private final TextureRegionDrawable right_tex;

        private GarageArrows(TextureAtlas atlas) {
            this.left_tex = new TextureRegionDrawable(atlas.findRegion("arrow_left"));
            this.right_tex = new TextureRegionDrawable(atlas.findRegion("arrow_right"));
        }

        public static GarageArrows newInstance(TextureAtlas atlas) {
            GarageArrows instance = new GarageArrows(atlas);
            return instance;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            if (super.isVisible()) {
                float x = super.getX();
                float y = super.getY();
                float width = super.getWidth();
                float height = super.getHeight();
                this.left_tex.draw(batch, x, y, .191f * width, height);
                this.right_tex.draw(batch, x + .809f * width, y, .191f * width, height);
            }
        }
    }

    private static class SuperKey extends Button {
        private SuperKey(ButtonStyle style) {
            super(style);
        }

        public static SuperKey newInstance(TextureAtlas atlas) {
            TextureRegion region = atlas.findRegion("super_key");
            ButtonStyle style = new ButtonStyle();
            style.up = new TextureRegionDrawable(region);
            region = new TextureRegion(region.getTexture(), region.getRegionX(), region.getRegionY() - 1, region.getRegionWidth(), region.getRegionHeight());
            style.down = new TextureRegionDrawable(region);
            SuperKey instance = new SuperKey(style);
            return instance;
        }
    }

    private static class AwardLabel extends PixelLabel {
        private final TextureRegionDrawable background;
        private final float[] bg_bounds;

        private AwardLabel(TextureAtlas atlas, LabelStyle style) {
            super("", style);
            this.background = new TextureRegionDrawable(atlas.findRegion("label_background"));
            this.bg_bounds = new float[4];
        }

        public static AwardLabel newInstance(TextureAtlas atlas) {
            LabelStyle style = new LabelStyle();
            style.font = FontsFactory.getAsync("Junegull.ttf", 18);
            style.fontColor = Color.WHITE;
            AwardLabel instance = new AwardLabel(atlas, style);
            instance.setAlignment(Align.center, Align.center);
            return instance;
        }

        public void setAward(int award) {
            super.setText(Connection.getString("common_award").concat(": ").concat(String.valueOf(award)));
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            this.background.draw(batch, this.bg_bounds[0], this.bg_bounds[1], this.bg_bounds[2], this.bg_bounds[3]);
            super.draw(batch, parentAlpha);
        }

        @Override
        public void setBounds(float x, float y, float width, float height) {
            this.bg_bounds[0] = x;
            this.bg_bounds[1] = y;
            this.bg_bounds[2] = width;
            this.bg_bounds[3] = height;
            super.setBounds(x + .2f * width, y + .296f * height, .688f * width, .25f * height);
        }
    }

    public static class GameOverWindow extends PixelLabel {
        private final TextureRegionDrawable background;
        private final float[] bg_bounds;

        private GameOverWindow(TextureAtlas atlas, LabelStyle style) {
            super(Connection.getString("bonus_game_over"), style);
            this.background = new TextureRegionDrawable(atlas.findRegion("game_over_label"));
            this.bg_bounds = new float[4];
        }

        public static GameOverWindow newInstance(TextureAtlas atlas) {
            LabelStyle style = new LabelStyle();
            style.font = FontsFactory.getAsync("Junegull.ttf", 36);
            style.fontColor = new Color(1f, .517f, 0f, 1f);
            GameOverWindow instance = new GameOverWindow(atlas, style);
            instance.setAlignment(Align.center, Align.center);
            return instance;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            this.background.draw(batch, this.bg_bounds[0], this.bg_bounds[1], this.bg_bounds[2], this.bg_bounds[3]);
            super.draw(batch, parentAlpha);
        }

        @Override
        public void setBounds(float x, float y, float width, float height) {
            this.bg_bounds[0] = x;
            this.bg_bounds[1] = y;
            this.bg_bounds[2] = width;
            this.bg_bounds[3] = height;
            super.setBounds(x + .087f * width, y + .337f * height, .81f * width, .428f * height);
        }
    }

    private class SafeListener extends ClickListener {
        protected final int index;
        protected final boolean left;

        public SafeListener(int index, boolean left) {
            super();
            this.index = index;
            this.left = left;
        }
    }

}
