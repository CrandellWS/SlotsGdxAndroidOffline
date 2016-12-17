package mobi.square.slots.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import mobi.square.slots.api.Connection;
import mobi.square.slots.classes.Card;
import mobi.square.slots.classes.Card.CardFlippedListener;
import mobi.square.slots.config.AppConfig;
import mobi.square.slots.containers.CardInfo;
import mobi.square.slots.tools.FontsFactory;
import mobi.square.slots.ui.RiskGreaterGroup.RiskAwardFrame;
import mobi.square.slots.ui.RiskGreaterGroup.RiskMainButton;
import mobi.square.slots.ui.RiskGreaterGroup.RiskTitleFrame;

public class RiskColorGroup extends Group implements Resizable {

    private final TextureAtlas cards_atlas;
    private final Texture background;
    private final RiskTitleFrame title_frame;
    private final RiskAwardFrame bank_frame;
    private final RiskAwardFrame double_frame;
    private final RiskMainButton left_button;
    private final RiskMainButton right_button;
    private final LastCardsFrame last_cards;
    private final ColoredButton black_button;
    private final ColoredButton red_button;
    private final Card[] cards;

    private RiskColorListener listener;

    private final CardFlippedListener open_listener = new CardFlippedListener() {
        @Override
        public void flipped() {
            if (listener != null) {
                cards[4].setCardFlippedListener(null);
                listener.gameCardOpened();
            }
        }
    };

    private final CardFlippedListener close_listener = new CardFlippedListener() {
        @Override
        public void flipped() {
            if (listener != null) {
                cards[4].setCardFlippedListener(null);
                listener.gameCardClosed();
            }
        }
    };

    private RiskColorGroup(Texture background, TextureAtlas cards_atlas, TextureAtlas atlas) {
        this.listener = null;
        this.cards_atlas = cards_atlas;
        this.background = background;
        this.title_frame = RiskTitleFrame.newInstance(atlas);
        this.bank_frame = RiskAwardFrame.newInstance(atlas);
        this.double_frame = RiskAwardFrame.newInstance(atlas);
        this.left_button = RiskMainButton.newInstance(atlas, true);
        this.right_button = RiskMainButton.newInstance(atlas, false);
        this.last_cards = LastCardsFrame.newInstance(atlas);
        this.red_button = ColoredButton.newInstance(atlas, false);
        this.black_button = ColoredButton.newInstance(atlas, true);
        this.cards = new Card[]{
                Card.newInstance(this.cards_atlas),
                Card.newInstance(this.cards_atlas),
                Card.newInstance(this.cards_atlas),
                Card.newInstance(this.cards_atlas),
                Card.newInstance(this.cards_atlas)
        };
        super.addActor(this.last_cards);
        for (int i = 0; i < this.cards.length; super.addActor(this.cards[i++])) ;
        super.addActor(this.title_frame);
        super.addActor(this.double_frame);
        super.addActor(this.bank_frame);
        super.addActor(this.left_button);
        super.addActor(this.right_button);
        super.addActor(this.black_button);
        super.addActor(this.red_button);
    }

    public static RiskColorGroup newInstance(Texture background, TextureAtlas cards_atlas, TextureAtlas atlas) {
        RiskColorGroup instance = new RiskColorGroup(background, cards_atlas, atlas);
        instance.setBounds(0f, 0f, AppConfig.VIEWPORT_WIDTH, AppConfig.VIEWPORT_HEIGHT - GameHeader.HEADER_HEIGHT);
        instance.title_frame.setBounds(228f, 436f, 568f, 70f);
        instance.bank_frame.setBounds(228f, 270f, 132f, 132f);
        instance.double_frame.setBounds(664f, 270f, 132f, 132f);
        instance.left_button.setBounds(0f, 130f, 176f, 127f);
        instance.right_button.setBounds(848f, 130f, 176f, 127f);
        instance.bank_frame.setTextBank();
        instance.double_frame.setTextDouble();
        for (int i = 0; i < 4; i++)
            instance.cards[i].setBounds(224f + 152f * i, 30f, 120f, 172);
        instance.cards[4].setBounds(447f, 214f, 130f, 190f);
        instance.last_cards.setBounds(405f, 9f, 214f, 20f);
        instance.black_button.setBounds(24f, 300f, 152f, 152f);
        instance.red_button.setBounds(848f, 300f, 152f, 152f);
        return instance;
    }

    public void setTextRiskRules() {
        this.title_frame.setTextColorRules();
    }

    public void setTextRiskWin() {
        this.title_frame.setTextRiskWin();
    }

    public void setTextRiskLose() {
        this.title_frame.setTextRiskLose();
    }

    public void setColorButtonsDisabled(boolean disabled) {
        this.black_button.setDisabled(disabled);
        this.red_button.setDisabled(disabled);
    }

    public void setAward(int award) {
        this.bank_frame.setAward(award);
        this.double_frame.setAward(2 * award);
    }

    public void showGameCard(CardInfo card) {
        this.cards[4].setCard(this.cards_atlas, card.getSuit(), card.getRank(), card.getColor());
        if (this.cards[4].isOpened()) return;
        this.cards[4].setCardFlippedListener(this.open_listener);
        this.cards[4].open();
    }

    public void closeGameCard() {
        if (!this.cards[4].isOpened()) return;
        this.cards[4].setCardFlippedListener(this.close_listener);
        this.cards[4].close();
    }

    public void setLastCards(CardInfo[] cards) {
        if (cards == null) return;
        for (int i = cards.length - 1, j = 3; i >= 0 && j >= 0; i--, j--) {
            this.cards[j].setCard(this.cards_atlas, cards[i].getSuit(), cards[i].getRank(), cards[i].getColor());
            this.cards[j].setOpened(true);
        }
    }

    public void setListener(RiskColorListener listener) {
        this.listener = listener;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float x = super.getX();
        float y = super.getY();
        float width = super.getWidth();
        float height = super.getHeight();
        batch.draw(this.background, x, y, width, height);
        super.draw(batch, parentAlpha);
    }

    @Override
    public void resize(int width, int height) {
        this.title_frame.resize(width, height);
        this.bank_frame.resize(width, height);
        this.double_frame.resize(width, height);
        this.left_button.resize(width, height);
        this.right_button.resize(width, height);
        this.last_cards.resize(width, height);
        this.black_button.resize(width, height);
        this.red_button.resize(width, height);
    }

    public RiskMainButton getLeftButton() {
        return this.left_button;
    }

    public RiskMainButton getRightButton() {
        return this.right_button;
    }

    public ColoredButton getRedButton() {
        return this.red_button;
    }

    public ColoredButton getBlackButton() {
        return this.black_button;
    }

    public interface RiskColorListener {
        void gameCardOpened();

        void gameCardClosed();
    }

    private static class LastCardsFrame extends PixelLabel {
        private TextureRegionDrawable background;

        private LastCardsFrame(LabelStyle style, TextureAtlas atlas) {
            super(Connection.getString("risk_previous_cards"), style);
            this.background = new TextureRegionDrawable(atlas.findRegion("previous_cards"));
        }

        public static LastCardsFrame newInstance(TextureAtlas atlas) {
            LabelStyle style = new LabelStyle();
            style.font = FontsFactory.getAsync("Taurus.ttf", 20);
            style.fontColor = new Color(.85f, .808f, .094f, 1f);
            LastCardsFrame instance = new LastCardsFrame(style, atlas);
            instance.setAlignment(Align.center, Align.center);
            return instance;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            this.background.draw(batch, super.getX(), super.getY(), super.getWidth(), super.getHeight());
            super.draw(batch, parentAlpha);
        }
    }

    public static class ColoredButton extends Button implements Resizable {
        private final PixelLabel label;

        private ColoredButton(ButtonStyle style, String text) {
            super(style);
            LabelStyle ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("Junegull.ttf", 24);
            ls.fontColor = Color.WHITE;
            this.label = new PixelLabel(text, ls);
            this.label.setAlignment(Align.center, Align.center);
            super.addActor(this.label);
        }

        public static ColoredButton newInstance(TextureAtlas atlas, boolean black) {
            String text;
            ButtonStyle style = new ButtonStyle();
            if (black) {
                style.up = new TextureRegionDrawable(atlas.findRegion("black_button_active"));
                style.down = new TextureRegionDrawable(atlas.findRegion("black_button_pressed"));
                style.disabled = new TextureRegionDrawable(atlas.findRegion("black_button_inactive"));
                text = Connection.getString("risk_color_black");
            } else {
                style.up = new TextureRegionDrawable(atlas.findRegion("red_button_active"));
                style.down = new TextureRegionDrawable(atlas.findRegion("red_button_pressed"));
                style.disabled = new TextureRegionDrawable(atlas.findRegion("red_button_inactive"));
                text = Connection.getString("risk_color_red");
            }
            ColoredButton instance = new ColoredButton(style, text);
            return instance;
        }

        @Override
        public void setBounds(float x, float y, float width, float height) {
            super.setBounds(x, y, width, height);
            this.label.setBounds(0f, .04f * height, width, height);
        }

        @Override
        public void resize(int width, int height) {
            this.label.resize(width, height);
        }
    }

}
