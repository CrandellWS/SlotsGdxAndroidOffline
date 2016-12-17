package mobi.square.slots.ui.bonus;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import mobi.square.slots.api.Connection;
import mobi.square.slots.enums.SymbolType;
import mobi.square.slots.tools.FontsFactory;
import mobi.square.slots.ui.PixelLabel;
import mobi.square.slots.ui.Resizable;

public class BookOfRaBonusWindow extends Group implements Resizable {

    private static final float ANIMATION_TIME = 1f;

    private final MessageWindow message;
    private final BonusSymbolBook book;
    private final float[] ani_current;
    private final float[] ani_target;
    private final float[] ani_speed;
    private BookAnimationOver animation_handler;
    private boolean animation_in_progress;
    private final BonusSymbolBook.BonusSymbolChoosen book_handler = new BonusSymbolBook.BonusSymbolChoosen() {
        @Override
        public void symbolChoosen() {
            ani_current[0] = 342f;
            ani_current[1] = 190f;
            ani_current[2] = 340f;
            ani_current[3] = 135f;
            for (int i = 0; i < 4; i++) {
                ani_speed[i] = (ani_target[i] - ani_current[i]) / ANIMATION_TIME;
            }
            updateBookPosition();
            animation_in_progress = true;
            message.setVisible(false);
        }
    };

    private BookOfRaBonusWindow(TextureAtlas atlas) {
        super();
        this.message = MessageWindow.newInstance(atlas);
        this.book = BonusSymbolBook.newInstance(atlas);
        this.book.setSymbolHandler(this.book_handler);
        super.addActor(this.message);
        super.addActor(this.book);
        this.animation_handler = null;
        this.animation_in_progress = false;
        this.ani_current = new float[4];
        this.ani_target = new float[4];
        this.ani_speed = new float[4];
    }

    public static BookOfRaBonusWindow newInstance(TextureAtlas atlas) {
        BookOfRaBonusWindow instance = new BookOfRaBonusWindow(atlas);
        instance.message.setBounds(197f, 100f, 630f, 366f);
        instance.book.setBounds(342f, 190f, 340f, 135f);
        instance.ani_target[0] = 392f;
        instance.ani_target[1] = 5f;
        instance.ani_target[2] = 240f;
        instance.ani_target[3] = 95f;
        return instance;
    }

    public void setAnimationHandler(BookAnimationOver handler) {
        this.animation_handler = handler;
    }

    public void show(SymbolType target, int count) {
        this.message.setVisible(true);
        this.message.setSpinsCount(count);
        this.book.setBounds(342f, 190f, 340f, 135f);
        //this.book.setBounds(392f, 5f, 240f, 95f);
        this.book.setSpinsCount(count);
        this.book.setVisible(true);
        this.book.beginSwitch(target);
        super.setVisible(true);
    }

    public void setSpinsCount(int count) {
        this.message.setSpinsCount(count);
        this.book.setSpinsCount(count);
    }

    @Override
    public void act(float delta) {
        if (this.animation_in_progress) {
            boolean over = false;
            for (int i = 0; i < 4; i++) {
                this.ani_current[i] += this.ani_speed[i] * delta;
                if (this.ani_speed[i] >= 0f) {
                    if (this.ani_current[i] >= this.ani_target[i]) {
                        over = true;
                    }
                } else {
                    if (this.ani_current[i] < this.ani_target[i]) {
                        over = true;
                    }
                }
                if (over) {
                    for (int j = 0; j < 4; j++) {
                        this.ani_current[i] = this.ani_target[i];
                    }
                    this.animation_in_progress = false;
                    this.book.showSpinsCount(true);
                    if (this.animation_handler != null) {
                        this.animation_handler.bookAnimationOver();
                    }
                    break;
                }
            }
            this.updateBookPosition();
        }
        super.act(delta);
    }

    @Override
    public void resize(int width, int height) {
        this.message.resize(width, height);
        this.book.resize(width, height);
    }

    private void updateBookPosition() {
        this.book.setBounds(this.ani_current[0], this.ani_current[1], this.ani_current[2], this.ani_current[3]);
    }

    public interface BookAnimationOver {
        void bookAnimationOver();
    }

    private static class MessageWindow extends PixelLabel {
        private final TextureRegionDrawable background;
        private final TextureRegionDrawable text_background;
        private final float[] bg_bounds;

        private MessageWindow(TextureAtlas atlas, LabelStyle style) {
            super("0 ".concat(Connection.getString("book_of_ra_bonus_message")), style);
            this.background = new TextureRegionDrawable(atlas.findRegion("plate"));
            this.text_background = new TextureRegionDrawable(atlas.findRegion("text_background"));
            this.bg_bounds = new float[4];
        }

        public static MessageWindow newInstance(TextureAtlas atlas) {
            LabelStyle style = new LabelStyle();
            style.font = FontsFactory.getAsync("Junegull.ttf", 32);
            style.fontColor = new Color(.329f, .106f, 0f, 1f);
            MessageWindow instance = new MessageWindow(atlas, style);
            instance.setAlignment(Align.center, Align.center);
            return instance;
        }

        public void setSpinsCount(int count) {
            super.setText(String.valueOf(count).concat(" ").concat(Connection.getString("book_of_ra_bonus_message")));
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            this.background.draw(batch, this.bg_bounds[0], this.bg_bounds[1], this.bg_bounds[2], this.bg_bounds[3]);
            this.text_background.draw(
                    batch,
                    this.bg_bounds[0] + .1f * this.bg_bounds[2],
                    this.bg_bounds[1] + .45f * this.bg_bounds[3],
                    .81f * this.bg_bounds[2],
                    .4f * this.bg_bounds[3]
            );
            super.draw(batch, parentAlpha);
        }

        @Override
        public void setBounds(float x, float y, float width, float height) {
            this.bg_bounds[0] = x;
            this.bg_bounds[1] = y;
            this.bg_bounds[2] = width;
            this.bg_bounds[3] = height;
            super.setBounds(x + .1f * width, y + .5f * height, .81f * width, .4f * height);
        }
    }

    private static class BonusSymbolBook extends Group implements Resizable {
        private static final float SWITCH_TIME = .1f;
        private final TextureRegionDrawable background;
        private final TextureRegionDrawable symbol;
        private final TextureRegionDrawable[] symbols;
        private final PixelLabel label;
        private BonusSymbolChoosen symbol_handler;
        private int current_index;
        private int target_index;
        private int loops_remain;
        private boolean switch_time;
        private float next_switch;

        private BonusSymbolBook(TextureAtlas atlas) {
            this.symbols = new TextureRegionDrawable[]{
                    new TextureRegionDrawable(atlas.findRegion("n01")),
                    new TextureRegionDrawable(atlas.findRegion("n02")),
                    new TextureRegionDrawable(atlas.findRegion("n03")),
                    new TextureRegionDrawable(atlas.findRegion("n04")),
                    new TextureRegionDrawable(atlas.findRegion("n05")),
                    new TextureRegionDrawable(atlas.findRegion("n06")),
                    new TextureRegionDrawable(atlas.findRegion("n07")),
                    new TextureRegionDrawable(atlas.findRegion("n08"))
            };
            this.background = new TextureRegionDrawable(atlas.findRegion("book"));
            this.symbol = new TextureRegionDrawable(atlas.findRegion("book_symbol"));
            LabelStyle ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("Junegull.ttf", 36);
            ls.fontColor = new Color(.329f, .106f, 0f, 1f);
            this.label = new PixelLabel("", ls);
            this.label.setAlignment(Align.center, Align.center);
            super.addActor(this.label);
            this.symbol_handler = null;
            this.current_index = 0;
            this.target_index = 0;
            this.loops_remain = 0;
            this.switch_time = false;
            this.next_switch = 0f;
        }

        public static BonusSymbolBook newInstance(TextureAtlas atlas) {
            BonusSymbolBook instance = new BonusSymbolBook(atlas);
            return instance;
        }

        public void setSymbolHandler(BonusSymbolChoosen handler) {
            this.symbol_handler = handler;
        }

        public void beginSwitch(SymbolType target) {
            this.switch_time = true;
            this.current_index = 0;
            this.loops_remain = 3;
            this.next_switch = SWITCH_TIME;
            this.label.setVisible(false);
            switch (target) {
                case N01:
                    this.target_index = 0;
                    break;
                case N02:
                    this.target_index = 1;
                    break;
                case N03:
                    this.target_index = 2;
                    break;
                case N04:
                    this.target_index = 3;
                    break;
                case N05:
                    this.target_index = 4;
                    break;
                case N06:
                    this.target_index = 5;
                    break;
                case N07:
                    this.target_index = 6;
                    break;
                case N08:
                    this.target_index = 7;
                    break;
                default:
                    this.target_index = 0;
                    break;
            }
        }

        public void setSpinsCount(int count) {
            this.label.setText(String.valueOf(count));
        }

        public void showSpinsCount(boolean show) {
            this.label.setVisible(show);
        }

        @Override
        public void act(float delta) {
            if (this.switch_time) {
                this.next_switch -= delta;
                while (this.next_switch <= 0f) {
                    this.next_switch += SWITCH_TIME;
                    this.current_index++;
                    if (this.current_index >= this.symbols.length) {
                        this.current_index = 0;
                        this.loops_remain--;
                    }
                    if (this.current_index == this.target_index && this.loops_remain <= 0) {
                        this.switch_time = false;
                        if (this.symbol_handler != null) {
                            this.symbol_handler.symbolChoosen();
                        }
                    }
                }
            }
            super.act(delta);
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            float x = super.getX();
            float y = super.getY();
            float width = super.getWidth();
            float height = super.getHeight();
            this.background.draw(batch, x, y, width, height);
            this.symbols[this.current_index].draw(batch, x + .16f * width, y + .18f * height, 0.25f * width, .6f * height);
            if (!this.label.isVisible()) {
                this.symbol.draw(batch, x + .6f * width, y + .26f * height, .231f * width, .431f * height);
            } else super.draw(batch, parentAlpha);
        }

        @Override
        public void setBounds(float x, float y, float width, float height) {
            this.label.setBounds(.5f * width, 0f, .4f * width, height);
            super.setBounds(x, y, width, height);
        }

        @Override
        public void resize(int width, int height) {
            this.label.resize(width, height);
        }

        public interface BonusSymbolChoosen {
            void symbolChoosen();
        }
    }

}
