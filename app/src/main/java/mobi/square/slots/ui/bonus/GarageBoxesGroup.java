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
import mobi.square.slots.error.StringCodeException;
import mobi.square.slots.tools.FontsFactory;
import mobi.square.slots.ui.GameHeader;
import mobi.square.slots.ui.PixelLabel;
import mobi.square.slots.ui.Resizable;
import mobi.square.slots.ui.bonus.GarageLocksGroup.GameOverWindow;
import mobi.square.slots.utils.json.JsonArray;

public class GarageBoxesGroup extends Group implements Resizable {

    private final Texture background;
    private final AwardLabel label;
    private final BonusGauge[] gauge;
    private final PrizeBox[] box;
    private final GameOverWindow game_over;
    private BoxOpenedListener box_listener;

    private GarageBoxesGroup(Texture background, TextureAtlas atlas, TextureAtlas common) {
        this.box_listener = null;
        this.background = background;
        this.game_over = GameOverWindow.newInstance(common);
        this.label = AwardLabel.newInstance(atlas);
        this.gauge = new BonusGauge[]{
                BonusGauge.newInstance(atlas, 2),
                BonusGauge.newInstance(atlas, 3),
                BonusGauge.newInstance(atlas, 4)
        };
        this.box = new PrizeBox[]{
                PrizeBox.newInstance(atlas),
                PrizeBox.newInstance(atlas),
                PrizeBox.newInstance(atlas),
                PrizeBox.newInstance(atlas),
                PrizeBox.newInstance(atlas)
        };
        for (int i = 0; i < 3; super.addActor(this.gauge[i++])) ;
        for (int i = 0; i < 5; i++) {
            this.box[i].addListener(new BoxListener(i) {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (box_listener != null) {
                        if (!box[super.box_index].isOpened()) {
                            box_listener.opened(super.box_index);
                        }
                    }
                }
            });
            super.addActor(this.box[i]);
        }
        super.addActor(this.label);
        super.addActor(this.game_over);
    }

    public static GarageBoxesGroup newInstance(Texture background, TextureAtlas atlas, TextureAtlas common) {
        GarageBoxesGroup instance = new GarageBoxesGroup(background, atlas, common);
        instance.setBounds(0f, 0f, AppConfig.VIEWPORT_WIDTH, AppConfig.VIEWPORT_HEIGHT - GameHeader.HEADER_HEIGHT);
        instance.gauge[0].setBounds(300f, 138f, 134f, 362f);
        instance.gauge[1].setBounds(445f, 138f, 134f, 362f);
        instance.gauge[2].setBounds(590f, 138f, 134f, 362f);
        instance.box[0].setBounds(154f, 34f, 132f, 108f);
        instance.box[1].setBounds(296f, 34f, 132f, 108f);
        instance.box[2].setBounds(438f, 34f, 132f, 108f);
        instance.box[3].setBounds(580f, 34f, 132f, 108f);
        instance.box[4].setBounds(722f, 34f, 132f, 108f);
        instance.label.setBounds(392f, 464f, 240f, 47f);
        instance.label.setAward(0);
        instance.game_over.setBounds(256f, 160f, 504f, 202f);
        instance.game_over.setVisible(false);
        return instance;
    }

    public void setBoxListener(BoxOpenedListener listener) {
        this.box_listener = listener;
    }

    public void setBoxState(int index, boolean opened, int award, String type) {
        this.box[index].setPrize(opened ? type : null, award);
    }

    public void setGaugeValues(JsonArray values, int index) throws StringCodeException {
        if (index < 0 || index >= this.gauge.length) return;
        this.gauge[index].setGaugeValue(values);
    }

    public void setTotalAward(int award) {
        this.label.setAward(award);
    }

    public void showGameOverWindow(boolean show) {
        this.game_over.setVisible(show);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(this.background, super.getX(), super.getY(), super.getWidth(), super.getHeight());
        super.draw(batch, parentAlpha);
    }

    @Override
    public void resize(int width, int height) {
        this.label.resize(width, height);
        this.game_over.resize(width, height);
        for (int i = 0; i < this.gauge.length; this.gauge[i++].resize(width, height)) ;
        for (int i = 0; i < this.box.length; this.box[i++].resize(width, height)) ;
    }

    public interface BoxOpenedListener {
        void opened(int index);
    }

    private static class BonusGauge extends Group implements Resizable {
        private final TextureRegionDrawable background;
        private final TextureRegionDrawable active_gauge;
        //private final TextureRegionDrawable multiplier;
        private final TextureRegionDrawable prize;
        private final PixelLabel[] award_labels;
        private final float[] active_offsets;
        private int gauge_value;

        private BonusGauge(TextureAtlas atlas, int multiplier) {
            this.background = new TextureRegionDrawable(atlas.findRegion("gauge"));
            this.active_gauge = new TextureRegionDrawable(atlas.findRegion("gauge_active"));
            if (multiplier == 4) {
                //this.multiplier = new TextureRegionDrawable(atlas.findRegion("multiplier_x4"));
                this.prize = new TextureRegionDrawable(atlas.findRegion("gauge_prize_jigsaw"));
            } else if (multiplier == 3) {
                //this.multiplier = new TextureRegionDrawable(atlas.findRegion("multiplier_x3"));
                this.prize = new TextureRegionDrawable(atlas.findRegion("gauge_prize_hammer"));
            } else {
                //this.multiplier = new TextureRegionDrawable(atlas.findRegion("multiplier_x2"));
                this.prize = new TextureRegionDrawable(atlas.findRegion("gauge_prize_wrench"));
            }
            LabelStyle ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("arial.ttf", 14);
            ls.fontColor = Color.WHITE;
            this.award_labels = new PixelLabel[]{
                    new PixelLabel("", ls),
                    new PixelLabel("", ls),
                    new PixelLabel("", ls),
                    new PixelLabel("", ls),
                    new PixelLabel("", ls)
            };
            for (int i = 0; i < this.award_labels.length; i++) {
                this.award_labels[i].setVisible(false);
                this.award_labels[i].setAlignment(Align.center, Align.center);
                super.addActor(this.award_labels[i]);
            }
            this.active_offsets = new float[10];
            this.gauge_value = 0;
        }

        public static final BonusGauge newInstance(TextureAtlas atlas, int multiplier) {
            BonusGauge instance = new BonusGauge(atlas, multiplier);
            instance.active_offsets[0] = .25f;
            instance.active_offsets[1] = .348f;
            instance.active_offsets[2] = .445f;
            instance.active_offsets[3] = .54f;
            instance.active_offsets[4] = .637f;
            instance.active_offsets[5] = 0.0718f; // Active gauge size
            instance.active_offsets[6] = .89f; // Background size
            instance.active_offsets[7] = .1f; // Multiplier size
            instance.active_offsets[8] = .06f; // Prize offset
            instance.active_offsets[9] = .15f; // Prize size
            return instance;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            float x = super.getX();
            float y = super.getY();
            float width = super.getWidth();
            float height = super.getHeight();
            float bg_height = (float) ((int) (height * this.active_offsets[6]));
            this.background.draw(batch, x, y, width, bg_height);
            for (int i = 0; i < this.gauge_value && i < 5; i++) {
                this.active_gauge.draw(batch, x, y + this.active_offsets[i] * bg_height, width, this.active_offsets[5] * bg_height);
            }
            this.prize.draw(batch, x, y + this.active_offsets[8] * height, width, this.active_offsets[9] * height);
            //float offset = (1f - this.active_offsets[7]) * height;
            //this.multiplier.draw(batch, x, y + offset, width, this.active_offsets[7] * height);
            super.draw(batch, parentAlpha);
        }

        @Override
        public void setBounds(float x, float y, float width, float height) {
            super.setBounds(x, y, width, height);
            float bg_height = (float) ((int) (height * this.active_offsets[6]));
            for (int i = 0; i < 5; i++) {
                this.award_labels[i].setBounds(0f, (this.active_offsets[i] - .016f) * bg_height, width, .1f * bg_height);
            }
        }

        @Override
        public void resize(int width, int height) {
            for (int i = 0; i < this.award_labels.length; this.award_labels[i++].resize(width, height))
                ;
        }

        public void setGaugeValue(JsonArray values) throws StringCodeException {
            int count = values.length();
            this.gauge_value = 0;
            for (int i = 0; i < count; i++) {
                int award = values.getInt(i);
                if (award > 0) {
                    this.award_labels[count - i - 1].setText(String.valueOf(award));
                    this.award_labels[count - i - 1].setVisible(true);
                    if (this.gauge_value == 0) {
                        this.gauge_value = count - i;
                    }
                } else {
                    this.award_labels[count - i - 1].setVisible(false);
                }
            }
        }
    }

    private static class PrizeBox extends Group implements Resizable {
        private final TextureRegionDrawable box_opened;
        private final TextureRegionDrawable box_closed;
        private final TextureRegionDrawable[] prize;
        private final PixelLabel award_label;
        private int prize_type;

        private PrizeBox(TextureAtlas atlas, LabelStyle style) {
            this.box_opened = new TextureRegionDrawable(atlas.findRegion("box_opened"));
            this.box_closed = new TextureRegionDrawable(atlas.findRegion("box_closed"));
            this.prize = new TextureRegionDrawable[]{
                    new TextureRegionDrawable(atlas.findRegion("box_disc")),
                    new TextureRegionDrawable(atlas.findRegion("box_flashlight")),
                    new TextureRegionDrawable(atlas.findRegion("box_wrench")),
                    new TextureRegionDrawable(atlas.findRegion("box_hammer")),
                    new TextureRegionDrawable(atlas.findRegion("box_jigsaw")),
                    new TextureRegionDrawable(atlas.findRegion("box_stop"))
            };
            this.award_label = new PixelLabel("", style);
            this.award_label.setAlignment(Align.center, Align.center);
            this.award_label.setVisible(false);
            super.addActor(this.award_label);
            this.prize_type = -1;
        }

        public static PrizeBox newInstance(TextureAtlas atlas) {
            LabelStyle style = new LabelStyle();
            style.font = FontsFactory.getAsync("arial.ttf", 18);
            style.fontColor = Color.WHITE;
            PrizeBox instance = new PrizeBox(atlas, style);
            return instance;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            float x = super.getX();
            float y = super.getY();
            float width = super.getWidth();
            float height = super.getHeight();
            if (this.prize_type >= 0) {
                this.box_opened.draw(batch, x, y, width, height);
                this.prize[this.prize_type].draw(batch, x, y, width, height);
            } else {
                this.box_closed.draw(batch, x, y, width, height);
            }
            super.draw(batch, parentAlpha);
        }

        @Override
        public void setBounds(float x, float y, float width, float height) {
            super.setBounds(x, y, width, height);
            this.award_label.setBounds(.05f * width, height, width, .1f * height);
        }

        @Override
        public void resize(int width, int height) {
            this.award_label.resize(width, height);
        }

        public boolean isOpened() {
            return this.prize_type >= 0;
        }

        public void setPrize(String type, int award) {
            if (type != null) {
                this.award_label.setText(String.valueOf(award));
                this.award_label.setVisible(true);
                type = type.trim().toUpperCase();
                if (type.equals("DISC")) {
                    this.prize_type = 0;
                } else if (type.equals("LANTERN")) {
                    this.prize_type = 1;
                } else if (type.equals("WRENCH")) {
                    this.prize_type = 2;
                } else if (type.equals("HAMMER")) {
                    this.prize_type = 3;
                } else if (type.equals("JIGSAW")) {
                    this.prize_type = 4;
                } else if (type.equals("POLICEMAN")) {
                    this.prize_type = 5;
                    this.award_label.setVisible(false);
                }
            } else {
                this.award_label.setVisible(false);
                this.prize_type = -1;
            }
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
            super.setBounds(x, y, width, height);
        }
    }

    private class BoxListener extends ClickListener {
        protected final int box_index;

        public BoxListener(int index) {
            super();
            this.box_index = index;
        }
    }

}
