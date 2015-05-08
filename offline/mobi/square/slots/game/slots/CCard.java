package mobi.square.slots.game.slots;

import java.io.Serializable;

import mobi.square.slots.enums.CardColor;
import mobi.square.slots.enums.CardRank;
import mobi.square.slots.enums.CardSuit;

public class CCard implements Serializable {
	
	private static final long serialVersionUID = -5281327754136352439L;
	
	private CardSuit suit = null;
	private CardRank rank = null;
	
	public CCard(CardSuit suit, CardRank rank) {
		this.suit = suit;
		this.rank = rank;
	}
	
	// Public Methods
	
	public CardColor getColor() {
		switch (this.getSuit()) {
			case CLUBS:			return CardColor.BLACK;
			case DIAMONDS:		return CardColor.RED;
			case HEARTS:		return CardColor.RED;
			case SPADES:		return CardColor.BLACK;
			default:			return CardColor.BLACK;
		}
	}
	
	// Getters & Setters
	
	public CardSuit getSuit() {
		return this.suit;
	}
	
	public void setSuit(CardSuit suit) {
		this.suit = suit;
	}

	public CardRank getRank() {
		return this.rank;
	}

	public void setRank(CardRank rank) {
		this.rank = rank;
	}
	
}
