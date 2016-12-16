package mobi.square.slots.containers;

import mobi.square.slots.enums.CardColor;
import mobi.square.slots.enums.CardRank;
import mobi.square.slots.enums.CardSuit;

public class CardInfo {

	private CardRank rank = CardRank.ACE;
	private CardSuit suit = CardSuit.SPADES;

	public CardInfo() {}
	public CardInfo(CardRank rank, CardSuit suit) {
		this.rank = rank;
		this.suit = suit;
	}

	public CardColor getColor() {
		switch (this.suit) {
			case DIAMONDS:
			case HEARTS:
				return CardColor.RED;
			default:
				return CardColor.BLACK;
		}
	}

	public CardRank getRank() {
		return this.rank;
	}

	public void setRank(CardRank rank) {
		this.rank = rank;
	}

	public void setRank(String rank) {
		this.rank = CardRank.valueOf(rank);
	}

	public CardSuit getSuit() {
		return this.suit;
	}

	public void setSuit(CardSuit suit) {
		this.suit = suit;
	}

	public void setSuit(String suit) {
		this.suit = CardSuit.valueOf(suit);
	}

}
