package mobi.square.slots.b.rockclimber;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import mobi.square.slots.api.Connection;
import mobi.square.slots.b.rockclimber.MRockClimber.ClimberMan;
import mobi.square.slots.b.rockclimber.MRockClimber.MessageBoard;
import mobi.square.slots.classes.Card;
import mobi.square.slots.classes.Card.CardFlippedListener;
import mobi.square.slots.config.AppConfig;
import mobi.square.slots.containers.CardInfo;
import mobi.square.slots.ui.DrawableActor;
import mobi.square.slots.ui.GameHeader;
import mobi.square.slots.ui.IntClickListener;
import mobi.square.slots.ui.PixelLabel;
import mobi.square.slots.ui.Resizable;
import mobi.square.slots.ui.RiskGreaterGroup.RiskGreaterListener;

public class RiskClimberGroup extends Group implements Resizable {

    private final DrawableActor pick;
    private final DrawableActor dealer;
    private final DrawableActor background;
    private final TextureAtlas cards_atlas;
    private final ClimberMan climber;
    private final MessageBoard board;
    private final MessageFrame frame;
    private final Card[] cards;
    private final RiskButton button;

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

    private RiskClimberGroup(Texture background, TextureAtlas atlas, TextureAtlas cards, TextureAtlas ani) {
        this.listener = null;
        this.background = DrawableActor.newInstance(background);
        super.addActor(this.background);
        this.climber = ClimberMan.newInstance(ani);
        super.addActor(this.climber);
        this.board = MessageBoard.newInstance(ani, true);
        super.addActor(this.board);
        this.pick = DrawableActor.newInstance(atlas.findRegion(Connection.getTextureLanguage("pick")));
        super.addActor(this.pick);
        this.dealer = DrawableActor.newInstance(atlas.findRegion(Connection.getTextureLanguage("dealer")));
        super.addActor(this.dealer);
        this.frame = MessageFrame.newInstance(atlas);
        super.addActor(this.frame);
        this.cards_atlas = cards;
        this.cards = new Card[]{
                Card.newInstance(this.cards_atlas),
                Card.newInstance(this.cards_atlas),
                Card.newInstance(this.cards_atlas),
                Card.newInstance(this.cards_atlas),
                Card.newInstance(this.cards_atlas)
        };
        for (int i = 0; i < this.cards.length; super.addActor(this.cards[i++])) ;
        for (int i = 0; i < 4; i++) {
            this.cards[i].addListener(new IntClickListener(i) {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (listener != null) {
                        listener.choiceCardClicked(super.value);
                    }
                }
            });
        }
        this.cards[4].setCardFlippedListener(this.game_flipped);
        this.button = RiskButton.newInstance(atlas);
        super.addActor(this.button);
    }

    public static RiskClimberGroup newInstance(Texture background, TextureAtlas atlas, TextureAtlas cards, TextureAtlas ani) {
        RiskClimberGroup instance = new RiskClimberGroup(background, atlas, cards, ani);
        instance.setBounds(0f, 0f, AppConfig.VIEWPORT_WIDTH, AppConfig.VIEWPORT_HEIGHT - GameHeader.HEADER_HEIGHT);
        for (int i = 0; i < 4; i++)
            instance.cards[i].setBounds(264f + 182f * i, 170f, 174f, 230);
        instance.cards[4].setBounds(65f, 174f, 164f, 223f);
        instance.button.setBounds(820f, 6f, 180f, 70f);
        instance.frame.setBounds(520f, 70f, 150f, 80f);
        instance.dealer.setBounds(86f, 132f, 124f, 38f);
        instance.pick.setBounds(288f, 132f, 124f, 38f);
        instance.pick.setVisible(false);
        return instance;
    }

    @Override
    public void resize(int width, int height) {
        this.button.resize(width, height);
        this.board.resize(width, height);
        this.frame.resize(width, height);
    }

    @Override
    public void setBounds(float x, float y, float width, float height) {
        super.setBounds(x, y, width, height);
        this.background.setBounds(0f, 0f, width, height);
        this.climber.setBounds(.42f * width, .01f * height, .12f * width, .24f * height);
        this.board.setBounds(.1f * width, .01f * height, .22f * width, .24f * height);
    }

    public RiskButton getButton() {
        return this.button;
    }

    public void setListener(RiskGreaterListener listener) {
        this.listener = listener;
    }

    public void pickCard(int index) {
        this.pick.setX(288f + (float) index * 182f);
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
        } else this.frame.showLoss();
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
        this.frame.hide();
    }

    public void setAward(int award) {
        this.board.showDoubleTo(award * 2);
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
            this.label.setBounds(0f, 0f, width, height);
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
        private static final float MESSAGE_DELAY = .4f;
        private final DrawableActor[] background;
        private final PixelLabel message;
        private float time;
        private int state;

        private MessageFrame(TextureAtlas atlas) {
            this.time = 0f;
            this.state = 0;
            this.background = new DrawableActor[]{
                    DrawableActor.newInstance(atlas.findRegion("message", 0)),
                    DrawableActor.newInstance(atlas.findRegion("message", 1)),
                    DrawableActor.newInstance(atlas.findRegion("message", 2))
            };
            for (int i = 0; i < this.background.length; super.addActor(this.background[i++])) ;
            this.message = PixelLabel.newInstance("", "arialbd.ttf", 14, Color.BLACK, Align.center);
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
            if (this.state > 0) {
                this.time += delta;
                if (this.time >= MESSAGE_DELAY) {
                    this.time -= MESSAGE_DELAY;
                    if (this.state == 1) {
                        this.background[1].setVisible(true);
                        this.state++;
                    } else {
                        this.background[2].setVisible(true);
                        this.message.setVisible(true);
                        this.state = 0;
                    }
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
            this.message.setBounds(.37f * width, 0f, .6f * width, height);
        }

        public void showWin() {
            this.hide();
            this.message.setText(Connection.getString("rock_climber_win"));
            this.background[0].setVisible(true);
            this.time = 0f;
            this.state = 1;
        }

        public void showLoss() {
            this.hide();
            this.message.setText(Connection.getString("rock_climber_loss"));
            this.background[0].setVisible(true);
            this.time = 0f;
            this.state = 1;
        }

        public void hide() {
            for (int i = 0; i < this.background.length; this.background[i++].setVisible(false)) ;
            this.message.setVisible(false);
            this.state = 0;
        }
    }

}
