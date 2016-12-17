package mobi.square.slots.game.slots.risk;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import mobi.square.slots.enums.CardColor;
import mobi.square.slots.enums.CardRank;
import mobi.square.slots.enums.CardSuit;
import mobi.square.slots.game.slots.CCard;
import mobi.square.slots.game.slots.CSlots;
import mobi.square.slots.utils.utils;

public class CRiskColor implements Serializable {

    private static final long serialVersionUID = -4404943851214190337L;

    private transient CSlots parent;

    private List<CCard> last_cards = null;

    // Constructors

    public CRiskColor(CSlots parent) {
        this.parent = parent;
    }

    // Public Methods

    /**
     * Игра на риск
     */
    public boolean risk(CardColor color) {
        CCard card = this.generateCard();
        if (this.getLastCards().size() > 4)
            this.getLastCards().remove(0);
        this.getLastCards().add(card);
        boolean result = false;
        if (card.getColor() == color) {
            this.getParent().setAward(this.getParent().getAward() * 2);
            result = true;
        } else {
            this.getParent().setAward(0);
            this.getParent().setRiskGame(false);
            result = false;
        }
        //this.getParent().getParent().getController().save();
        return result;
    }

    /**
     * Возвращает последнюю карту.
     *
     * @return CCard
     */
    public CCard getLastCard() {
        int index = this.getLastCards().size() - 1;
        if (index < 0) return null;
        return this.getLastCards().get(index);
    }

    /**
     * Генерация игральной карты
     *
     * @return CCard
     */
    private CCard generateCard() {
        int luck = utils.getRandom(54);
        CardSuit suit = null;
        CardRank rank = null;
        if (luck < 52) {
            int suit_val = (int) ((double) luck / 13.0);
            int rank_val = luck - 13 * suit_val;
            suit = CardSuit.convert(suit_val);
            rank = CardRank.convert(rank_val);
        } else if (luck == 52) {
            suit = CardSuit.CLUBS;
            rank = CardRank.JOKER;
        } else {
            suit = CardSuit.DIAMONDS;
            rank = CardRank.JOKER;
        }
        return new CCard(suit, rank);
    }

    // Getters & Setters

    public CSlots getParent() {
        return this.parent;
    }

    public void setParent(CSlots parent) {
        this.parent = parent;
    }

    public List<CCard> getLastCards() {
        if (this.last_cards == null)
            this.last_cards = new LinkedList<CCard>();
        return this.last_cards;
    }

    public void setLastCards(List<CCard> last_cards) {
        this.last_cards = last_cards;
    }

}
