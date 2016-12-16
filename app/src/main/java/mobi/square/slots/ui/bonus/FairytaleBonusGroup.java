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

public class FairytaleBonusGroup extends Group implements Resizable {

    private static final String[] PRIZE = {
            "apple",
            "arrow",
            "beads",
            "feather",
            "hat",
            "key",
            "mirror"
    };
    private static final String GAME_OVER = "over";
    private final Texture background;
    private final FairytaleBarrel[] barrels;
    private final FairytaleAward award_frame;
    private final GameOverWindow game_over;
    private BarrelOpenedListener listener;

    private FairytaleBonusGroup(Texture background, TextureAtlas atlas) {
        super();
        this.listener = null;
        this.background = background;
        TextureRegionDrawable barrel = new TextureRegionDrawable(atlas.findRegion("barrel"));
        this.barrels = new FairytaleBarrel[9];
        for (int i = 0; i < 9; i++) {
            this.barrels[i] = FairytaleBarrel.newInstance(barrel);
            this.barrels[i].addListener(new BarrelClickListener(i) {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (listener != null && barrels[super.index].closed) {
                        listener.opened(super.index);
                    }
                }
            });
            super.addActor(this.barrels[i]);
        }
        this.award_frame = FairytaleAward.newInstance(atlas);
        this.game_over = GameOverWindow.newInstance(atlas);
        super.addActor(this.award_frame);
        super.addActor(this.game_over);
    }

    public static FairytaleBonusGroup newInstance(Texture background, TextureAtlas atlas) {
        FairytaleBonusGroup instance = new FairytaleBonusGroup(background, atlas);
        instance.setBounds(0f, 0f, AppConfig.VIEWPORT_WIDTH, AppConfig.VIEWPORT_HEIGHT - GameHeader.HEADER_HEIGHT);
        instance.award_frame.setBounds(292f, 4f, 440f, 59f);
        for (int i = 0; i < 4; i++) {
            instance.barrels[i].setBounds(170f + 172f * (float) i, 270f, 160f, 176f);
        }
        for (int i = 4; i < 9; i++) {
            instance.barrels[i].setBounds(80f + 172f * (float) (i - 4), 70f, 172f, 189f);
        }
        instance.game_over.setBounds(306f, 160f, 412f, 251f);
        instance.game_over.setVisible(false);
        return instance;
    }

    public void setListener(BarrelOpenedListener listener) {
        this.listener = listener;
    }

    public void open(int index, int award) {
        if (index < 0 || index > 8) return;
        if (this.barrels[index].closed) {
            this.barrels[index].open(award > 0 ? PRIZE[utils.getRandom(PRIZE.length)] : GAME_OVER, award);
        }
    }

    public void setAward(int award) {
        this.award_frame.setAward(award);
    }

    public void showGameOverWindow() {
        this.game_over.setVisible(true);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(this.background, super.getX(), super.getY(), super.getWidth(), super.getHeight());
        super.draw(batch, parentAlpha);
    }

    @Override
    public void resize(int width, int height) {
        for (int i = 0; i < 9; this.barrels[i++].resize(width, height)) ;
        this.award_frame.resize(width, height);
        this.game_over.resize(width, height);
    }

    public interface BarrelOpenedListener {
        void opened(int index);
    }

    private static class FairytaleBarrel extends Group implements Resizable {
        private final TextureRegionDrawable barrel;
        private final PixelLabel award_label;
        private boolean closed;
        private TextureRegionDrawable prize;

        private FairytaleBarrel(TextureRegionDrawable barrel) {
            this.closed = true;
            this.barrel = barrel;
            this.prize = null;
            LabelStyle ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("arial.ttf", 28);
            ls.fontColor = Color.WHITE;
            this.award_label = new PixelLabel("", ls);
            this.award_label.setAlignment(Align.center, Align.center);
            super.addActor(this.award_label);
        }

        public static FairytaleBarrel newInstance(TextureRegionDrawable barrel) {
            FairytaleBarrel instance = new FairytaleBarrel(barrel);
            instance.close();
            return instance;
        }

        public void close() {
            this.closed = true;
        }

        public void open(String type, int award) {
            TextureAtlas atlas = Connection.getManager().get("atlas/FairytaleBonus.pack", TextureAtlas.class);
            this.prize = new TextureRegionDrawable(atlas.findRegion("barrel_".concat(type)));
            this.award_label.setText(String.valueOf(award));
            this.award_label.setVisible(award > 0);
            this.closed = false;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            if (this.closed) {
                if (this.barrel != null) {
                    this.barrel.draw(batch, super.getX(), super.getY(), super.getWidth(), super.getHeight());
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
            this.award_label.setBounds(0f, .75f * height, width, .25f * height);
        }

        public void resize(int width, int height) {
            this.award_label.resize(width, height);
        }
    }

    private static class FairytaleAward extends Group implements Resizable {
        private final TextureRegionDrawable background;
        private final PixelLabel label_title;
        private final PixelLabel label_award;

        private FairytaleAward(TextureAtlas atlas) {
            this.background = new TextureRegionDrawable(atlas.findRegion("award_frame"));
            LabelStyle ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("Junegull.ttf", 24);
            ls.fontColor = new Color(.827f, .643f, .012f, 1f);
            this.label_title = new PixelLabel(Connection.getString("common_award"), ls);
            this.label_title.setAlignment(Align.center, Align.center);
            ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("Junegull.ttf", 24);
            ls.fontColor = Color.WHITE;
            this.label_award = new PixelLabel("0", ls);
            this.label_award.setAlignment(Align.center, Align.center);
            super.addActor(this.label_title);
            super.addActor(this.label_award);
        }

        public static FairytaleAward newInstance(TextureAtlas atlas) {
            FairytaleAward instance = new FairytaleAward(atlas);
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
            this.label_title.setBounds(.136f * width, .04f * height, .314f * width, height);
            this.label_award.setBounds(.45f * width, .04f * height, .42f * width, height);
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
            style.font = FontsFactory.getAsync("Junegull.ttf", 32);
            style.fontColor = new Color(0.416f, 0f, 0f, 1f);
            GameOverWindow instance = new GameOverWindow(atlas, style);
            return instance;
        }

        @Override
        public void setLabelBounds(float x, float y, float width, float height) {
            super.setLabelBounds(x + .087f * width, y + .24f * height, .81f * width, .49f * height);
        }
    }

    public class BarrelClickListener extends ClickListener {
        protected int index;

        public BarrelClickListener(int index) {
            this.index = index;
        }
    }

}
