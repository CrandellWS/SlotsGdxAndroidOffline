package mobi.square.slots.enums;

public enum CardRank {

    TWO,
    THREE,
    FOUR,
    FIVE,
    SIX,
    SEVEN,
    EIGHT,
    NINE,
    TEN,
    JACK,
    QUEEN,
    KING,
    ACE,
    JOKER;

    public static CardRank convert(int value) {
        return CardRank.class.getEnumConstants()[value];
    }

    public String toLowerString() {
        switch (this) {
            case TWO:
                return "two";
            case THREE:
                return "three";
            case FOUR:
                return "four";
            case FIVE:
                return "five";
            case SIX:
                return "six";
            case SEVEN:
                return "seven";
            case EIGHT:
                return "eight";
            case NINE:
                return "nine";
            case TEN:
                return "ten";
            case JACK:
                return "jack";
            case QUEEN:
                return "queen";
            case KING:
                return "king";
            case ACE:
                return "ace";
            case JOKER:
                return "joker";
            default:
                return "unknown";
        }
    }

}
