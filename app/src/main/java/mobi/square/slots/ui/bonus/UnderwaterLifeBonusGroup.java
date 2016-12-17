package mobi.square.slots.ui.bonus;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import mobi.square.slots.api.Connection;
import mobi.square.slots.config.AppConfig;
import mobi.square.slots.tools.FontsFactory;
import mobi.square.slots.ui.GameHeader;
import mobi.square.slots.ui.PixelLabel;
import mobi.square.slots.ui.Resizable;
import mobi.square.slots.utils.utils;

public class UnderwaterLifeBonusGroup extends Group implements Resizable {

    private static final int PRIZE_COUNT = 9;
    private static final int GAME_OVER_COUNT = 2;
    private final Texture background;
    private final UnderwaterShell[] shells;
    private final UnderwaterLifeAward award_frame;
    private final GameOverWindow game_over;
    private ShellOpenedListener listener;

    private UnderwaterLifeBonusGroup(Texture background, TextureAtlas atlas) {
        super();
        this.listener = null;
        this.background = background;
        TextureRegionDrawable shell = new TextureRegionDrawable(atlas.findRegion("shell"));
        this.shells = new UnderwaterShell[9];
        for (int i = 0; i < 9; i++) {
            this.shells[i] = UnderwaterShell.newInstance(shell);
            this.shells[i].addListener(new ShellClickListener(i) {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (listener != null && shells[super.index].closed) {
                        listener.opened(super.index);
                    }
                }
            });
            super.addActor(this.shells[i]);
        }
        this.award_frame = UnderwaterLifeAward.newInstance(atlas);
        this.game_over = GameOverWindow.newInstance(atlas);
        super.addActor(this.award_frame);
        super.addActor(this.game_over);
    }

    public static UnderwaterLifeBonusGroup newInstance(Texture background, TextureAtlas atlas) {
        UnderwaterLifeBonusGroup instance = new UnderwaterLifeBonusGroup(background, atlas);
        instance.setBounds(0f, 0f, AppConfig.VIEWPORT_WIDTH, AppConfig.VIEWPORT_HEIGHT - GameHeader.HEADER_HEIGHT);
        instance.award_frame.setBounds(282f, 0f, 460f, 85f);
        instance.shells[0].setBounds(82f, 280f, 160f, 160f);
        instance.shells[1].setBounds(252f, 350f, 160f, 160f);
        instance.shells[2].setBounds(602f, 350f, 160f, 160f);
        instance.shells[3].setBounds(772f, 280f, 160f, 160f);
        instance.shells[4].setBounds(62f, 80f, 180f, 180f);
        instance.shells[5].setBounds(242f, 150f, 180f, 180f);
        instance.shells[6].setBounds(422f, 220f, 180f, 180f);
        instance.shells[7].setBounds(602f, 150f, 180f, 180f);
        instance.shells[8].setBounds(782f, 80f, 180f, 180f);
        instance.game_over.setBounds(235f, 160f, 554f, 196f);
        instance.game_over.setVisible(false);
        return instance;
    }

    public void setListener(ShellOpenedListener listener) {
        this.listener = listener;
    }

    public void open(int index, int award) {
        if (index < 0 || index > 8) return;
        if (this.shells[index].closed) {
            this.shells[index].open(award > 0 ? utils.getRandom(PRIZE_COUNT) : utils.getRandom(GAME_OVER_COUNT), award);
        }
    }

    public void showGameOverWindow() {
        this.game_over.setVisible(true);
    }

    public void setAward(int award) {
        this.award_frame.setAward(award);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(this.background, super.getX(), super.getY(), super.getWidth(), super.getHeight());
        super.draw(batch, parentAlpha);
    }

    @Override
    public void resize(int width, int height) {
        for (int i = 0; i < 9; this.shells[i++].resize(width, height)) ;
        this.award_frame.resize(width, height);
        this.game_over.resize(width, height);
    }

    public interface ShellOpenedListener {
        void opened(int index);
    }

    private static class UnderwaterShell extends Group implements Resizable {
        private final TextureRegionDrawable shell;
        private final PixelLabel award_label;
        private boolean closed;
        private TextureRegionDrawable prize;

        private UnderwaterShell(TextureRegionDrawable shell) {
            this.closed = true;
            this.shell = shell;
            this.prize = null;
            LabelStyle ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("arial.ttf", 28);
            ls.fontColor = new Color(.9f, .9f, .9f, 1f);
            this.award_label = new PixelLabel("", ls);
            this.award_label.setAlignment(Align.center, Align.center);
            super.addActor(this.award_label);
        }

        public static UnderwaterShell newInstance(TextureRegionDrawable shell) {
            UnderwaterShell instance = new UnderwaterShell(shell);
            instance.close();
            return instance;
        }

        public void close() {
            this.closed = true;
        }

        public void open(int index, int award) {
            TextureAtlas atlas = Connection.getManager().get("atlas/UnderwaterLifeBonus.pack", TextureAtlas.class);
            this.prize = new TextureRegionDrawable(atlas.findRegion(award > 0 ? "shell_prize" : "shell_over", index));
            this.award_label.setText(String.valueOf(award));
            this.award_label.setVisible(award > 0);
            this.closed = false;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            if (this.closed) {
                if (this.shell != null) {
                    this.shell.draw(batch, super.getX(), super.getY(), super.getWidth(), super.getHeight());
                }
            } else {
                if (this.prize != null) {
                    this.prize.draw(batch, super.getX(), super.getY(), super.getWidth(), super.getHeight());
                }
            }
            super.draw(batch, parentAlpha);
        }

        @Override
        public void setBounds(float x, float y, float width, float height) {
            super.setBounds(x, y, width, height);
            this.award_label.setBounds(0f, -.1f * height, width, .2f * height);
        }

        @Override
        public void resize(int width, int height) {
            this.award_label.resize(width, height);
        }
    }

    private static class UnderwaterLifeAward extends Group implements Resizable {
        private final TextureRegionDrawable background;
        private final PixelLabel label_title;
        private final PixelLabel label_award;

        private UnderwaterLifeAward(TextureAtlas atlas) {
            this.background = new TextureRegionDrawable(atlas.findRegion("award_frame"));
            LabelStyle ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("Junegull.ttf", 30);
            ls.fontColor = Color.WHITE;
            this.label_title = new PixelLabel(Connection.getString("common_award"), ls);
            this.label_title.setAlignment(Align.center, Align.center);
            ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("Junegull.ttf", 30);
            ls.fontColor = Color.WHITE;
            this.label_award = new PixelLabel("0", ls);
            this.label_award.setAlignment(Align.center, Align.center);
            super.addActor(this.label_title);
            super.addActor(this.label_award);
        }

        public static UnderwaterLifeAward newInstance(TextureAtlas atlas) {
            UnderwaterLifeAward instance = new UnderwaterLifeAward(atlas);
            return instance;
        }

        public void setAward(int award) {
            this.label_award.setText(String.valueOf(award));
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            this.background.draw(batch, super.getX(), super.getY(), super.getWidth(), super.getHeight());
            super.draw(batch, parentAlpha);
        }

        @Override
        public void setBounds(float x, float y, float width, float height) {
            super.setBounds(x, y, width, height);
            this.label_title.setBounds(.136f * width, .02f * height, .314f * width, height);
            this.label_award.setBounds(.48f * width, .08f * height, .4f * width, height);
        }

        @Override
        public void resize(int width, int height) {
            this.label_title.resize(width, height);
            this.label_award.resize(width, height);
        }
    }

    private static class GameOverWindow extends BasicGameOverWindow {
        protected GameOverWindow(TextureAtlas atlas, LabelStyle style) {
            super(atlas, style);
        }

        public static GameOverWindow newInstance(TextureAtlas atlas) {
            LabelStyle style = new LabelStyle();
            style.font = FontsFactory.getAsync("Junegull.ttf", 36);
            style.fontColor = Color.WHITE;
            GameOverWindow instance = new GameOverWindow(atlas, style);
            return instance;
        }

        @Override
        public void setLabelBounds(float x, float y, float width, float height) {
            super.setLabelBounds(x + .087f * width, y + .3f * height, .81f * width, .49f * height);
        }
    }

    public class ShellClickListener extends ClickListener {
        protected int index;

        public ShellClickListener(int index) {
            this.index = index;
        }
    }

}
