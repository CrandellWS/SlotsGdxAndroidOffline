package mobi.square.slots.game.slots.risk;

import java.io.Serializable;

import mobi.square.slots.enums.CardRank;
import mobi.square.slots.enums.CardSuit;
import mobi.square.slots.game.slots.CCard;
import mobi.square.slots.game.slots.CSlots;
import mobi.square.slots.utils.utils;

public class CRiskGreater implements Serializable {

    private static final long serialVersionUID = 1829650076811641277L;
    private static final int CARDS_COUNT = 4;
    private static final double CHANCE = 0.5;
    private transient CSlots parent = null;
    private transient CCard current_card = null;
    private transient CCard[] user_cards = null;
    private transient boolean opened = false;
    private transient boolean result = false;
    private transient int index = 0;

    // Constructors

    public CRiskGreater(CSlots parent) {
        this.parent = parent;
        this.opened = true;
    }

    // Public Methods

    public boolean risk(int index) {
        // Генерируем карты
        this.result = utils.getRandomBoolean(CHANCE);
        CCard card;
        CCard[] cards = new CCard[CARDS_COUNT];
        for (int i = 0; i < CARDS_COUNT; cards[i++] = null) ;
        if (!this.result) {
            cards[index] = this.generateLesserCard(this.getCurrentCard());
            int j = index;
            while (j == index) j = utils.getRandomInt(CARDS_COUNT);
            cards[j] = this.generateGreaterCard(this.getCurrentCard());
            for (int i = 0; i < CARDS_COUNT; i++) {
                if (i == index || i == j) continue;
                int k = 0;
                while (++k < 10) {
                    card = this.generateRandomCard();
                    if (!this.isExists(cards, card)) {
                        cards[i] = card;
                        break;
                    }
                }
            }
        } else {
            cards[index] = this.generateGreaterCard(this.getCurrentCard());
            for (int i = 0; i < CARDS_COUNT; i++) {
                if (i == index) continue;
                int k = 0;
                while (++k < 10) {
                    card = this.generateRandomCard();
                    if (!this.isExists(cards, card)) {
                        cards[i] = card;
                        break;
                    }
                }
            }
        }
        this.setUserCards(cards);
        this.setOpened(true);
        this.setIndex(index);
        // Сохраняем результат
        if (this.result) {
            this.getParent().setAward(this.getParent().getAward() * 2);
        } else {
            this.getParent().setAward(0);
            this.getParent().setRiskGame(false);
        }
        //this.getParent().getParent().getController().save();
        return this.result;
    }

    public void generateNewCard() {
        if (this.isOpened()) {
            this.setCurrentCard(this.loadCurrentCard());
            this.setOpened(false);
        }
    }

    // Private Methods

    private boolean isExists(CCard[] cards, CCard card) {
        for (int i = 0; i < cards.length; i++) {
            if (cards[i] == null) continue;
            if (cards[i].getRank() == card.getRank() &&
                    cards[i].getSuit() == card.getSuit()) {
                return true;
            }
        }
        return false;
    }

    private CCard loadCurrentCard() {
        CardRank rank = CardRank.convert(utils.getRandom(CardRank.FOUR.ordinal(), CardRank.QUEEN.ordinal()));
        CardSuit suit = CardSuit.convert(utils.getRandom(CardSuit.CLUBS.ordinal(), CardSuit.SPADES.ordinal()));
        return new CCard(suit, rank);
    }

    private CCard generateGreaterCard(CCard card) {
        CardRank rank = CardRank.convert(utils.getRandom(card.getRank().ordinal() + 1, CardRank.ACE.ordinal()));
        CardSuit suit = CardSuit.convert(utils.getRandom(CardSuit.CLUBS.ordinal(), CardSuit.SPADES.ordinal()));
        return new CCard(suit, rank);
    }

    private CCard generateLesserCard(CCard card) {
        CardRank rank = CardRank.convert(utils.getRandom(CardRank.TWO.ordinal(), card.getRank().ordinal()));
        CardSuit suit = CardSuit.convert(utils.getRandom(CardSuit.CLUBS.ordinal(), CardSuit.SPADES.ordinal()));
        return new CCard(suit, rank);
    }

    private CCard generateRandomCard() {
        CardRank rank = CardRank.convert(utils.getRandom(CardRank.TWO.ordinal(), CardRank.ACE.ordinal()));
        CardSuit suit = CardSuit.convert(utils.getRandom(CardSuit.CLUBS.ordinal(), CardSuit.SPADES.ordinal()));
        return new CCard(suit, rank);
    }

    // Getters & Setters

    public CSlots getParent() {
        return this.parent;
    }

    public void setParent(CSlots parent) {
        this.parent = parent;
    }

    public CCard getCurrentCard() {
        if (this.current_card == null)
            this.current_card = this.loadCurrentCard();
        return this.current_card;
    }

    public void setCurrentCard(CCard current_card) {
        this.current_card = current_card;
    }

    public CCard[] getUserCards() {
        return this.user_cards;
    }

    public void setUserCards(CCard[] user_cards) {
        this.user_cards = user_cards;
    }

    public int getCardsCount() {
        return CARDS_COUNT;
    }

    public boolean isOpened() {
        return this.opened;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    public boolean getResult() {
        return this.result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

}
