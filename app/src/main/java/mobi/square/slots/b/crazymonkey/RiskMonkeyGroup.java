package mobi.square.slots.b.crazymonkey;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import mobi.square.slots.api.Connection;
import mobi.square.slots.b.crazymonkey.MCrazyMonkey.MessageBoard;
import mobi.square.slots.classes.Card;
import mobi.square.slots.classes.Card.CardFlippedListener;
import mobi.square.slots.classes.Machine;
import mobi.square.slots.config.AppConfig;
import mobi.square.slots.containers.CardInfo;
import mobi.square.slots.tools.FontsFactory;
import mobi.square.slots.ui.DrawableActor;
import mobi.square.slots.ui.GameHeader;
import mobi.square.slots.ui.IntClickListener;
import mobi.square.slots.ui.PixelLabel;
import mobi.square.slots.ui.Resizable;
import mobi.square.slots.ui.RiskGreaterGroup.RiskGreaterListener;
import mobi.square.slots.utils.utils;

public class RiskMonkeyGroup extends Group implements Resizable {

    private final DrawableActor pick;
    private final DrawableActor dealer;
    private final DrawableActor background;
    private final TextureAtlas cards_atlas;
    private final CrazyMonkeyAward award;
    private final MessageBoard board;
    private final MessageFrame frame;
    private final Card[] cards;
    private final RiskButton button;
    private final MonkeyAnim monkey;

    private RiskGreaterListener listener;
    private int choice_index;

    private CardFlippedListener choice_flipped = new CardFlippedListener() {
        @Override
        public void flipped() {
            if (listener != null) {
                listener.choiceCardOpened();
            }
        }
    };

    private CardFlippedListener all_flipped = new CardFlippedListener() {
        @Override
        public void flipped() {
            if (listener != null) {
                listener.choiceCardsOpened();
            }
        }
    };

    private CardFlippedListener game_flipped = new CardFlippedListener() {
        @Override
        public void flipped() {
            if (listener != null) {
                if (cards[4].isOpened()) {
                    listener.gameCardOpened();
                } else {
                    listener.gameCardClosed();
                }
            }
        }
    };

    private RiskMonkeyGroup(Texture background, TextureAtlas atlas, TextureAtlas cards, TextureAtlas ani) {
        this.listener = null;
        this.background = DrawableActor.newInstance(background);
        super.addActor(this.background);
        this.board = MessageBoard.newInstance(ani, true);
        super.addActor(this.board);
        this.award = CrazyMonkeyAward.newInstance(atlas);
        super.addActor(this.award);
        this.cards_atlas = cards;
        this.cards = new Card[]{
                Card.newInstance(this.cards_atlas),
                Card.newInstance(this.cards_atlas),
                Card.newInstance(this.cards_atlas),
                Card.newInstance(this.cards_atlas),
                Card.newInstance(this.cards_atlas)
        };
        for (int i = 0; i < this.cards.length; super.addActor(this.cards[i++])) ;
        this.pick = DrawableActor.newInstance(atlas.findRegion(Connection.getTextureLanguage("pick")));
        super.addActor(this.pick);
        this.dealer = DrawableActor.newInstance(atlas.findRegion(Connection.getTextureLanguage("dealer")));
        super.addActor(this.dealer);
        for (int i = 0; i < 4; i++) {
            this.cards[i].addListener(new IntClickListener(i) {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (listener != null) {
                        listener.choiceCardClicked(super.value);
                        monkey.playChooseAction();
                    }
                }
            });
        }
        this.cards[4].setCardFlippedListener(this.game_flipped);
        this.button = RiskButton.newInstance(atlas);
        super.addActor(this.button);
        this.monkey = MonkeyAnim.newInstance(ani);
        super.addActor(this.monkey);
        this.frame = MessageFrame.newInstance(atlas);
        super.addActor(this.frame);
    }

    public static RiskMonkeyGroup newInstance(Texture background, TextureAtlas atlas, TextureAtlas cards, TextureAtlas ani) {
        RiskMonkeyGroup instance = new RiskMonkeyGroup(background, atlas, cards, ani);
        instance.setBounds(0f, 0f, AppConfig.VIEWPORT_WIDTH, AppConfig.VIEWPORT_HEIGHT - GameHeader.HEADER_HEIGHT);
        for (int i = 0; i < 4; i++)
            instance.cards[i].setBounds(294f + 150f * i, 232f, 140f, 200);
        instance.cards[4].setBounds(144f, 232f, 140f, 200f);
        instance.monkey.setBounds(16f, 12f, 190f, 220f);
        instance.award.setBounds(300f, 452f, 424f, 60f);
        instance.board.setBounds(380f, 40f, 264f, 100f);
        instance.button.setBounds(780f, 20f, 210f, 100f);
        instance.frame.setBounds(150f, 130f, 240f, 110f);
        instance.dealer.setBounds(154f, 204f, 120f, 42f);
        instance.pick.setBounds(304f, 204f, 124f, 42f);
        instance.pick.setVisible(false);
        return instance;
    }

    @Override
    public void resize(int width, int height) {
        this.button.resize(width, height);
        this.board.resize(width, height);
        this.frame.resize(width, height);
        this.award.resize(width, height);
    }

    @Override
    public void setBounds(float x, float y, float width, float height) {
        super.setBounds(x, y, width, height);
        this.background.setBounds(0f, 0f, width, height);
    }

    public RiskButton getButton() {
        return this.button;
    }

    public void setListener(RiskGreaterListener listener) {
        this.listener = listener;
    }

    public void pickCard(int index) {
        this.pick.setX(304f + (float) index * 150f);
        this.pick.setVisible(true);
        this.choice_index = index;
    }

    public void showGameCard(CardInfo card) {
        this.cards[4].setCard(this.cards_atlas, card.getSuit(), card.getRank(), card.getColor());
        this.cards[4].open();
    }

    public void setChoiceCards(CardInfo[] cards) {
        if (cards == null) return;
        for (int i = 0; i < cards.length; i++) {
            this.cards[i].setCard(this.cards_atlas, cards[i].getSuit(), cards[i].getRank(), cards[i].getColor());
        }
    }

    public void showChoiceCard(int index, boolean win) {
        for (int i = 0; i < 4; this.cards[i++].setCardFlippedListener(null)) ;
        this.cards[index].setCardFlippedListener(this.choice_flipped);
        this.cards[index].open();
        this.choice_index = index;
        if (win) {
            this.frame.showWin();
            this.monkey.playWinAction();
        } else {
            this.frame.showLoss();
            this.monkey.playLossAction();
        }
    }

    public void showChoiceCards() {
        for (int i = 0; i < 4; i++) {
            if (i == this.choice_index) {
                this.cards[i].setCardFlippedListener(null);
                continue;
            }
            this.cards[i].open();
        }
        this.cards[this.choice_index == 3 ? 2 : 3].setCardFlippedListener(this.all_flipped);
    }

    public void closeGameCard() {
        this.cards[4].close();
    }

    public void closeChoiceCards() {
        for (int i = 0; i < 4; this.cards[i++].close()) ;
        this.pick.setVisible(false);
        this.monkey.playDefault();
        this.frame.hide();
    }

    public void setAward(int award) {
        this.award.setAward(award);
        this.board.showDoubleTo(award * 2);
    }

    public void setHelmeted(boolean value) {
        this.monkey.setHelmeted(value);
    }

    private static class MonkeyAnim extends Actor {
        private static final float[] TIME = {.4f, .2f, .2f, .2f, .2f, .2f};
        private static final int[] RANDOMS = {1, 2};
        private final float[] bounds;
        private final TextureRegion[][] monkey;
        private final TextureRegion[][] helmet;
        private boolean helmeted;
        private int state;
        private int frame;
        private int loop;
        private float time;

        private MonkeyAnim(TextureAtlas atlas) {
            this.time = 0f;
            this.loop = 4;
            this.state = 0;
            this.frame = 0;
            this.helmeted = false;
            this.bounds = new float[4];
            this.monkey = new TextureRegion[][]{
                    Machine.loadTextures(atlas, "a016", 4, 1),
                    Machine.loadTextures(atlas, "a017", 6, 1),
                    Machine.loadTextures(atlas, "a018", 5, 1),
                    Machine.loadTextures(atlas, "a019", 1, 1),
                    Machine.loadTextures(atlas, "a020", 4, 1),
                    Machine.loadTextures(atlas, "a021", 4, 1)
            };
            this.helmet = new TextureRegion[][]{
                    Machine.loadTextures(atlas, "c016", 4, 1),
                    Machine.loadTextures(atlas, "c017", 6, 1),
                    Machine.loadTextures(atlas, "c018", 5, 1),
                    Machine.loadTextures(atlas, "c019", 1, 1),
                    Machine.loadTextures(atlas, "c020", 4, 1),
                    Machine.loadTextures(atlas, "c021", 4, 1)
            };
        }

        public static MonkeyAnim newInstance(TextureAtlas atlas) {
            MonkeyAnim instance = new MonkeyAnim(atlas);
            return instance;
        }

        @Override
        public void act(float delta) {
            this.time += delta;
            while (this.time >= TIME[this.state]) {
                this.time -= TIME[this.state];
                this.frame++;
                if (this.frame >= this.monkey[this.state].length) {
                    this.frame = 0;
                    switch (this.state) {
                        case 0:
                            this.loop--;
                            if (this.loop <= 0) {
                                this.state = RANDOMS[utils.getRandom(RANDOMS.length)];
                                this.loop = utils.getRandom(1, 2);
                            }
                            break;
                        case 1:
                        case 2:
                            this.loop();
                            break;
                        case 3:
                            break;
                        case 4:
                        case 5:
                            this.frame = this.monkey[this.state].length - 1;
                            break;
                        default:
                            this.state = 0;
                            break;
                    }
                }
            }
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            batch.draw(
                    this.monkey[this.state][this.frame],
                    this.bounds[0], this.bounds[1],
                    this.bounds[2], this.bounds[3]
            );
            if (this.helmeted) {
                batch.draw(
                        this.helmet[this.state][this.frame],
                        this.bounds[0], this.bounds[1],
                        this.bounds[2], this.bounds[3]
                );
            }
        }

        @Override
        public void setBounds(float x, float y, float width, float height) {
            this.bounds[0] = x;
            this.bounds[1] = y;
            this.bounds[2] = width;
            this.bounds[3] = height;
            super.setBounds(x, y, width, height);
        }

        public void setHelmeted(boolean value) {
            this.helmeted = value;
        }

        public void playDefault() {
            this.time = 0f;
            this.frame = 0;
            this.state = 0;
            this.loop = 3;
        }

        public void playChooseAction() {
            this.time = 0f;
            this.frame = 0;
            this.state = 3;
        }

        public void playWinAction() {
            this.time = 0f;
            this.frame = 0;
            this.state = 4;
        }

        public void playLossAction() {
            this.time = 0f;
            this.frame = 0;
            this.state = 5;
        }

        private void loop() {
            this.loop--;
            if (this.loop <= 0) {
                this.state = 0;
                this.loop = utils.getRandom(2, 4);
            }
        }
    }

    public static class RiskButton extends Button implements Resizable {
        private final PixelLabel label;

        private RiskButton(ButtonStyle style) {
            super(style);
            this.label = PixelLabel.newInstance("", "arialbd.ttf", 20, Color.BLACK, Align.center);
            super.addActor(this.label);
        }

        public static RiskButton newInstance(TextureAtlas atlas) {
            ButtonStyle style = new ButtonStyle();
            style.up = new TextureRegionDrawable(atlas.findRegion("button_active"));
            style.disabled = new TextureRegionDrawable(atlas.findRegion("button_inactive"));
            style.down = new TextureRegionDrawable(atlas.findRegion("button_pressed"));
            style.pressedOffsetY = -1f;
            RiskButton instance = new RiskButton(style);
            instance.setTextTake();
            return instance;
        }

        @Override
        public void resize(int width, int height) {
            this.label.resize(width, height);
        }

        @Override
        public void setBounds(float x, float y, float width, float height) {
            super.setBounds(x, y, width, height);
            this.label.setBounds(0f, .1f * height, width, .9f * height);
        }

        public void setTextRepeat() {
            this.label.setText(Connection.getString("risk_button_repeat"));
        }

        public void setTextTake() {
            this.label.setText(Connection.getString("risk_button_take_money"));
        }

        public void setTextLeave() {
            this.label.setText(Connection.getString("risk_button_leave"));
        }
    }

    private static class MessageFrame extends Group implements Resizable {
        private static final float MESSAGE_DELAY = .1f;
        private final DrawableActor[] background;
        private final PixelLabel message;
        private float time;
        private int state;

        private MessageFrame(TextureAtlas atlas) {
            this.time = 0f;
            this.state = -1;
            this.background = new DrawableActor[]{
                    DrawableActor.newInstance(atlas.findRegion("message", 0)),
                    DrawableActor.newInstance(atlas.findRegion("message", 1)),
                    DrawableActor.newInstance(atlas.findRegion("message", 2)),
                    DrawableActor.newInstance(atlas.findRegion("message", 3)),
                    DrawableActor.newInstance(atlas.findRegion("message", 4)),
                    DrawableActor.newInstance(atlas.findRegion("message", 5)),
                    DrawableActor.newInstance(atlas.findRegion("message", 6)),
                    DrawableActor.newInstance(atlas.findRegion("message", 7)),
                    DrawableActor.newInstance(atlas.findRegion("message", 8)),
                    DrawableActor.newInstance(atlas.findRegion("message", 9)),
                    DrawableActor.newInstance(atlas.findRegion("message", 10))
            };
            for (int i = 0; i < this.background.length; super.addActor(this.background[i++])) ;
            this.message = PixelLabel.newInstance("", "arialbd.ttf", 20, Color.BLACK, Align.center);
            super.addActor(this.message);
        }

        public static MessageFrame newInstance(TextureAtlas atlas) {
            MessageFrame instance = new MessageFrame(atlas);
            instance.hide();
            return instance;
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            if (this.state >= 0) {
                this.time += delta;
                if (this.time >= MESSAGE_DELAY) {
                    this.time -= MESSAGE_DELAY;
                    this.state++;
                    if (this.state >= this.background.length) {
                        this.state = this.background.length - 1;
                    }
                    this.updateState();
                }
            }
        }

        @Override
        public void resize(int width, int height) {
            this.message.resize(width, height);
        }

        @Override
        public void setBounds(float x, float y, float width, float height) {
            super.setBounds(x, y, width, height);
            for (int i = 0; i < this.background.length; this.background[i++].setBounds(0f, 0f, width, height))
                ;
            this.message.setBounds(.38f * width, 0f, .6f * width, height);
        }

        public void showWin() {
            this.hide();
            this.message.setText(Connection.getString("rock_climber_win"));
            this.time = 0f;
            this.state = 0;
            this.updateState();
        }

        public void showLoss() {
            this.hide();
            this.message.setText(Connection.getString("rock_climber_loss"));
            this.time = 0f;
            this.state = 0;
            this.updateState();
        }

        public void hide() {
            this.message.setVisible(false);
            this.state = -1;
            this.updateState();
        }

        private void updateState() {
            this.message.setVisible(false);
            for (int i = 0; i < this.background.length; i++) {
                this.background[i].setVisible(false);
            }
            if (this.state >= 0 && this.state < this.background.length) {
                this.background[this.state].setVisible(true);
                if (this.state == this.background.length - 1) {
                    this.message.setVisible(true);
                }
            }
        }
    }

    public static class CrazyMonkeyAward extends Group implements Resizable {
        private final TextureRegionDrawable background;
        private final PixelLabel label_title;
        private final PixelLabel label_award;

        private CrazyMonkeyAward(TextureAtlas atlas) {
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

        public static CrazyMonkeyAward newInstance(TextureAtlas atlas) {
            CrazyMonkeyAward instance = new CrazyMonkeyAward(atlas);
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
            this.label_title.setBounds(.094f * width, 0f, .4f * width, height);
            this.label_award.setBounds(.5f * width, 0f, .42f * width, height);
        }

        @Override
        public void resize(int width, int height) {
            this.label_title.resize(width, height);
            this.label_award.resize(width, height);
        }
    }

}
