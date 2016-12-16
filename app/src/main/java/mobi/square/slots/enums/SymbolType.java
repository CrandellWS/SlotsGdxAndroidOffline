package mobi.square.slots.enums;

public enum SymbolType {

    ERROR,
    N01,
    N02,
    N03,
    N04,
    N05,
    N06,
    N07,
    N08,
    N09,
    N10,
    N11,
    N12,
    BONUS,
    SCATTER,
    WILD;

    public static SymbolType convert(int value) {
        return SymbolType.class.getEnumConstants()[value];
    }

    public String toLowerString() {
        switch (this) {
            case BONUS:
                return "bonus";
            case N01:
                return "n01";
            case N02:
                return "n02";
            case N03:
                return "n03";
            case N04:
                return "n04";
            case N05:
                return "n05";
            case N06:
                return "n06";
            case N07:
                return "n07";
            case N08:
                return "n08";
            case N09:
                return "n09";
            case N10:
                return "n10";
            case N11:
                return "n11";
            case N12:
                return "n12";
            case SCATTER:
                return "scatter";
            case WILD:
                return "wild";
            default:
                return "error";
        }
    }

}
