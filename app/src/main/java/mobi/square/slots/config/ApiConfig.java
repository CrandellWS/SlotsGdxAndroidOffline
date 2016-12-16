package mobi.square.slots.config;

public class ApiConfig {

    public static final String SECRET_KEY = "Hj8K0Ert2L";
    public static final String BILLING_KEY = "Pw5F13eZ0x";
    public static final String SERVER_URL = "http://192.168.1.3:8080/SlotsWeb3/android";
    //public static final String SERVER_URL = "http://playhall.mobi/slots/Android";
    public static final String DOWNLOAD_URL = "http://playhall.mobi/SlotsWeb3/resources/dl/";
    public static final String CLIENT_VERSION = "3.3.0";
    public static final int CLIENT_REVISION = 27;
    public static final String DEFAULT_LANGUAGE = "RU";
    public static final int CONNECTION_TIMEOUT = 5000;
    public static final boolean BILLING_DEBUG_LOG = false;

    public static final String OK_APP_ID = "185562368";
    public static final String OK_APP_SECRET = "CA61EE3F9C175620C1DD7E41";
    public static final String OK_APP_KEY = "CBAGHQAMABABABABA";

    public static final int HOURLY_BONUS_TIME = 2 * 60;//2 * 3600; //in seconds
    public static final int HOURLY_MONEY_BONUS = 50000;
    public static final int HOURLY_VIP_MONEY_BONUS = 750;
    public static final int SUPER_BONUS_MAX = 5;
    public static final int SUPER_BONUS[] = {500, 800, 3000};

    public static final int RULETTE_TIME = 30;//(30 * 60); //(2*(60 * 60))//2 hours
    public static final int RULETTE_RESET_TIME = 31;//(31 * 60);//29800; 8 hours + 1000???
    public static final int RULETTE_MAX_MULTIPLER = 5;
    public static final int RULETTE_WINS[] = {100000, 5000, 9000, 10000, 4000, 25000, 6000, 7000, 10000, 3000, 30000, 2000};
    public static final int RULETTE_SPINS = 360 * 10;
    public static final String RULETTE_SKU = "first";

}
