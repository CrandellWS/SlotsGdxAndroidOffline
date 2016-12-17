package mobi.square.slots.enums;

public enum SlotsType {

    NONE,                // 0 - гладиаторы
    BOOK_OF_RA,
    GLADIATORS,            // 1 - гладиаторы
    FAIRYTALE,            // 2 - сказка
    GEMSTONES,            // 3 - самоцветы
    ZOMBIES,            // 4 - зомби
    FRUITS,                // 5 - светлые
    BATMAN,                // 6 - бэтмен
    GOLF,                // 7 - гольф
    LUCKY_RABBIT,        // 8 - кролик
    JACK_BEANSTALK,        // 9
    CRAZY_MONKEY,        // 10 - мартышка
    MONEY_GAME,            // 11 - деньги
    TREASURE_HUNTERS,    // 12 - сокровища
    MAD_MONKEY,            // 13 - обезьянка
    JACK_HAMMER,        // 14
    AZTECK,            // 15 - ацтеки
    FARAON,            // 16 - фараон
    GARAGE,            // 17 - гараж
    INDIA,                // 18 - индия
    LUCKY_FRUITS,        // 19 - фрукты темные
    LUCKY_HAUNTER,        // 20
    UNDERWATER_LIFE,    // 21 - океан
    CHAMPION,            // 22
    SWEETS,                // 23
    ZOOMA,                // 24
    EGYPT,                // 25
    EVOLUTION,            // 26
    CHRISTMAS,             //27
    ROBOTS,              //28
    RESIDENT,            //29
    LUCKY_ANGLER,        //30
    FULL_MOON_FORTUNES,  //31
    MYTHIC_MAIDEN,       //32
    VALENTINES_DAY,       //33
    GOLD,       //34
    THUNDERSTRUCK,       //35
    RICHES_OF_RA,       //36
    SECRET_OF_THE_STONES,       //37
    SEVEN_GEMS,       //38
    EIGHTH_MARCH,       //39
    FRUIT_COCKTAIL,       //40
    GARAGE_DELUXE,       //41
    CRAZY_MONKEY_DELUXE,       //42
    FAIRY_LAND,       //43
    ROCKCLIMBER,       //44
    MAKEUP_GAME;       //45

    public static SlotsType convert(int value) {
        return SlotsType.class.getEnumConstants()[value];
    }

    public static int getMin() {
        return 1;
    }

    public static int getMax() {
        return 48;
    }

    public String toLowerString() {
        switch (this) {
            case GLADIATORS:
                return "gladiators";
            case FAIRYTALE:
                return "fairytale";
            case GEMSTONES:
                return "gemstones";
            case ZOMBIES:
                return "zombies";
            case FRUITS:
                return "fruits";
            case BATMAN:
                return "batman";
            case GOLF:
                return "golf";
            case LUCKY_RABBIT:
                return "lucky_rabbit";
            case JACK_BEANSTALK:
                return "jack_beanstalk";
            case CRAZY_MONKEY:
                return "crazy_monkey";
            case MONEY_GAME:
                return "money_game";
            case TREASURE_HUNTERS:
                return "treasure_hunters";
            case MAD_MONKEY:
                return "mad_monkey";
            case JACK_HAMMER:
                return "jack_hammer";
            case AZTECK:
                return "azteck";
            case FARAON:
                return "faraon";
            case GARAGE:
                return "garage";
            case INDIA:
                return "india";
            case LUCKY_FRUITS:
                return "lucky_fruits";
            case LUCKY_HAUNTER:
                return "lucky_hunter";
            case UNDERWATER_LIFE:
                return "underwater_life";
            case CHAMPION:
                return "champion";
            case SWEETS:
                return "sweets";
            case ZOOMA:
                return "zooma";
            case EGYPT:
                return "egypt";
            case EVOLUTION:
                return "evolution";
            case ROBOTS:
                return "robots";
            case BOOK_OF_RA:
                return "book_of_ra";
            case CHRISTMAS:
                return "christmas";
            case CRAZY_MONKEY_DELUXE:
                return "crazy_monkey_deluxe";
            case EIGHTH_MARCH:
                return "eight_march";
            case FAIRY_LAND:
                return "fairy_land";
            case FRUIT_COCKTAIL:
                return "fruit_cocktail";
            case FULL_MOON_FORTUNES:
                return "full_moon_fortunes";
            case GARAGE_DELUXE:
                return "garage_deluxe";
            case GOLD:
                return "gold";
            case LUCKY_ANGLER:
                return "lucky_angler";
            case MYTHIC_MAIDEN:
                return "mythic_maiden";
            case RESIDENT:
                return "resident";
            case RICHES_OF_RA:
                return "riches_of_ra";
            case SECRET_OF_THE_STONES:
                return "secret_of_the_stones";
            case SEVEN_GEMS:
                return "seven_gems";
            case THUNDERSTRUCK:
                return "thinderstruck";
            case VALENTINES_DAY:
                return "valentines_day";
            case ROCKCLIMBER:
                return "rockclimber";
            case MAKEUP_GAME:
                return "makeup_game";
            default:
                return "none";
        }
    }

}
