package mobi.square.slots.enums;

public enum Symbol {

    SCATTER,    // 0
    BONUS,        // 1
    WILD,        // 2
    N01,        // 3
    N02,        // 4
    N03,        // 5
    N04,        // 6
    N05,        // 7
    N06,        // 8
    N07,        // 9
    N08,        // 10
    N09,        // 11
    N10,        // 12
    N11,        // 13
    N12;        // 14

    public static Symbol convert(int value) {
        return Symbol.class.getEnumConstants()[value];
    }

    public static int getSymbolsCount() {
        return 15;
    }

    public String getName() {
        return this.name();
    }
}
