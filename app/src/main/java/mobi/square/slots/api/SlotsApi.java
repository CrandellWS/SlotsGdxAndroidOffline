package mobi.square.slots.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import mobi.square.slots.classes.Machine;
import mobi.square.slots.config.SlotsConfig;
import mobi.square.slots.containers.BankInfo;
import mobi.square.slots.containers.CardInfo;
import mobi.square.slots.containers.LinesExt;
import mobi.square.slots.containers.LinesExt.LineExt;
import mobi.square.slots.containers.LinesExt.LineExt.Multiplier;
import mobi.square.slots.containers.SlotsInfo;
import mobi.square.slots.containers.UserInfo;
import mobi.square.slots.enums.BonusGame;
import mobi.square.slots.enums.CardColor;
import mobi.square.slots.enums.RiskType;
import mobi.square.slots.enums.SlotsType;
import mobi.square.slots.enums.SymbolType;
import mobi.square.slots.error.StringCodeException;
import mobi.square.slots.handlers.CancelableHandler;
import mobi.square.slots.utils.json.JsonArray;
import mobi.square.slots.utils.json.JsonObject;
import mobi.square.slots.utils.utils;

public class SlotsApi extends AndroidApi {

    private String user_id = null;

    private int money = 0;

    private int award = 0;
    private int current_lines = 1;
    private int current_bet = 1;
    private int free_spins = 0;

    private boolean force_spin = false;
    private boolean can_bet_max = false;
    private boolean can_change_lines = false;
    private boolean can_change_bet = false;
    private boolean can_spin = false;
    private boolean can_risk = false;
    private boolean sound_on = true;
    private boolean notifications_on = true;

    private int[] valid_lines = null;
    private int[] valid_bets = null;
    private int[] valid_autospins = null;
    private BonusGame bonus_game = BonusGame.NONE;
    private SlotsType slots_type = SlotsType.NONE;

    // Mail
    private String bonus_mail_message = null;
    private int bonus_mail_money = 0;
    private String bonus_mail_id = null;

    // Leaders
    private int leaders_hourly_time = 0;
    private int leaders_daily_time = 0;
    private int leaders_update_time = 0;

    // Bonus
    private int update_time = 0;
    private int hourly_bonus_time = 0;
    private int hourly_bonus_total = 0;
    private int super_bonus_count = 0;
    private int roulette_time = 0;
    private int roulette_total = 0;
    private int roulette_multipler = 0;
    private int roulette_position = 0;
    private int roulette_bonus_spins = 0;

    // Cup
    private boolean cup_active = false;
    private boolean cup_registered = false;
    private boolean cup_winned = false;
    private UserInfo[] cup_leaders = null;
    private int[] cup_awards = null;
    private int cup_spins_remain = 0;
    private int cup_award = 0;
    private int cup_spins = 0;
    private int cup_time = 0;
    private int cup_pay = 0;
    private int cup_ends = 0;
    private int cup_starts = 0;
    private int cup_score = 0;
    private SlotsType cup_type = SlotsType.NONE;
    private RiskType risk_type = RiskType.COLOR;
    private int risk_cards_count = 0;
    private int cup_update_time = 0;

    private LoginType login_type = null;

    public SlotsApi() {
        super();
    }

    public LoginType getLoginType() {
        if (this.login_type == null)
            this.login_type = LoginType.NONE;
        return this.login_type;
    }

    public void setLoginType(LoginType type) {
        this.login_type = type;
    }

    // Constructors

    public boolean isHasOkId() {
        return super.ok_uid != null;
    }

    // Public Methods

    public void requestSetMoney(int money, CancelableHandler response_handler) throws StringCodeException {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("money", String.valueOf(money));
        super.asyncMethod("jsSetUserMoney", params, response_handler);
    }

    public void responseSetMoney(String response) throws StringCodeException {
        JsonObject json = super.parseResponse(response);
        this.setMoney(json.getInt("money"));
    }

    public void requestLogin(String token, LoginType type, String name, CancelableHandler response_handler) throws StringCodeException {
        HashMap<String, String> params = new HashMap<String, String>();
        if (name != null) params.put("name", name);
        switch (type) {
            case OK:
                params.put("oauth", "ok");
                break;
            case FACEBOOK:
                params.put("oauth", "fb");
                break;
            case GOOGLE:
                params.put("oauth", "gg");
                break;
            default:
                return;
        }
        params.put("token", token);
        super.asyncMethod("jsLogin", params, response_handler);
    }

    public void responseLogin(String response) throws StringCodeException {
        JsonObject json = super.parseResponse(response);
        String user_id = json.getString("user_id");
        String social_id = json.getString("social_id");
        super.ok_uid = social_id;
        Connection.getWrapper().writeDeviceId(social_id);
        this.setUserId(user_id);
        super.setLogged(true);
    }

    public void requestSlotsList(CancelableHandler response_handler) throws StringCodeException {
        super.asyncMethod("jsLobby", null, response_handler);
    }

    public List<SlotsInfo> responseSlotsList(String response) throws StringCodeException {
        JsonObject json = super.parseResponse(response);
        this.updateUserInfo(json.getJsonObject("user"), true);
        // Cup
        if (!json.isNull("cup")) {
            this.cup_type = SlotsType.NONE;
            this.initCupState(json.getJsonObject("cup"), false);
        } else this.cup_type = null;
        if (!json.isNull("cup_top")) {
            this.updateCupTop(json.getJsonArray("cup_top"));
        }
        // Daily Bonus
        if (!json.isNull("r_bonus")) {
            this.initBonus(json.getJsonObject("r_bonus"));
        }
        // Mail Bonus
        if (!json.isNull("mail")) {
            this.initMail(json.getJsonObject("mail"));
        } else this.initMail(null);
        // Slots List
        List<SlotsInfo> result = new LinkedList<SlotsInfo>();
        JsonArray slots = json.getJsonArray("slots");
        int count = slots.length();
        for (int i = 0; i < count; i++) {
            JsonObject slot = slots.getJsonObject(i);
            SlotsInfo info = new SlotsInfo();
            try {
                info.setType(SlotsType.valueOf(slot.getString("type")));
            } catch (IllegalArgumentException e) {
                continue;
            } catch (Exception e) {
                throw new StringCodeException("json_error");
            }


            info.setRevision(slot.getInt("revision"));
            if (info.getRevision() > SlotsConfig.getSlotsRevision(info.getType()))
                continue;
            info.setMinLevel(slot.getInt("min_level"));
            info.setMaxLevel(slot.getInt("max_level"));
            System.out.println("info Slot jose " + info.getType() + " max " + info.getMaxLevel() + " min " + info.getMinLevel() + " revi " + info.getRevision());
            info.setRequiresVip(!slot.isNull("requires_vip") && slot.getBoolean("requires_vip"));

            if (info.getType() == SlotsType.BOOK_OF_RA || info.getType() == SlotsType.FAIRYTALE || info.getType() == SlotsType.GLADIATORS) {
                //System.out.println("info Slot jose " + info.getType());
                result.add(info);
            } else {
                result.add(info);
            }

        }
        return result;
    }

    public void requestInitSlots(SlotsType type, CancelableHandler response_handler) throws StringCodeException {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("type", type.toString());
        this.setSlotsType(type);
        super.asyncMethod("jsInit", params, response_handler);
    }

    public void responseInitSlots(String response, Machine view) throws StringCodeException {
        JsonObject json = super.parseResponse(response);
        this.updateUserInfo(json.getJsonObject("user"));
        this.setRiskType(RiskType.valueOf(json.getString("risk_type")));
        // Lines
        view.clearLines();
        JsonArray array = json.getJsonArray("lines");
        int array_length = array.length();
        for (int i = 0; i < array_length; i++) {
            JsonArray line = array.getJsonArray(i);
            int symbols_count = line.length();
            int[] positions = new int[symbols_count];
            for (int j = 0; j < symbols_count; j++)
                positions[j] = line.getInt(j);
            view.addLine(positions, 0, 0, true);
        }
        view.showLinesOneByOne();
        // Valid autospins
        array = json.getJsonArray("valid_autospins");
        array_length = array.length();
        this.setValidAutospins(new int[array_length]);
        for (int i = 0; i < array_length; i++) {
            this.getValidAutospins()[i] = array.getInt(i);
        }
        // Valid lines
        array = json.getJsonArray("valid_lines");
        array_length = array.length();
        this.setValidLines(new int[array_length]);
        for (int i = 0; i < array_length; i++) {
            this.getValidLines()[i] = array.getInt(i);
        }
        // Valid bets
        array = json.getJsonArray("valid_bets");
        array_length = array.length();
        this.setValidBets(new int[array_length]);
        for (int i = 0; i < array_length; i++) {
            this.getValidBets()[i] = array.getInt(i);
        }
        // Symbols
        array = json.getJsonArray("symbols");
        array_length = array.length();
        SymbolType[][] symbols = new SymbolType[array_length][];
        for (int i = 0; i < array_length; i++) {
            JsonArray reel = array.getJsonArray(i);
            int symbols_count = reel.length();
            SymbolType[] values = new SymbolType[symbols_count];
            for (int j = 0; j < symbols_count; j++)
                values[j] = SymbolType.valueOf(reel.getString(j));
            symbols[i] = values;
        }
        view.setSymbols(symbols);
        // Config
        this.initConfig(json.getJsonObject("config"));
        // Buttons
        this.initButtons(json.getJsonObject("state"));
    }

    public void requestSpin(SlotsType type, CancelableHandler response_handler) throws StringCodeException {
        HashMap<String, String> params = new HashMap<String, String>();
        if (type == null) type = this.getSlotsType();
        params.put("type", type.toString());
        this.setSlotsType(type);
        super.asyncMethod("jsSpin", params, response_handler);
    }

    public JsonArray responseSpin(String response, Machine view) throws StringCodeException {
        JsonObject json = super.parseResponse(response);
        this.updateUserInfo(json.getJsonObject("user"));

        // Symbols
        JsonArray array = json.getJsonArray("symbols");
        int array_length = array.length();
        SymbolType[][] symbols = new SymbolType[array_length][];
        for (int i = 0; i < array_length; i++) {
            JsonArray reel = array.getJsonArray(i);
            int symbols_count = reel.length();
            SymbolType[] values = new SymbolType[symbols_count];
            for (int j = 0; j < symbols_count; j++)
                values[j] = SymbolType.valueOf(reel.getString(j));
            symbols[i] = values;
        }

        // Lines Ext
        LinesExt linesExt = new LinesExt();
        array = json.getJsonArray("lines_ext");
        array_length = array.length();
        for (int i = 0; i < array_length; i++) {
            JsonObject json_line = array.getJsonObject(i);
            int[] positions = new int[5];
            JsonArray json_positions = json_line.getJsonArray("symbols");
            for (int j = 0; j < positions.length; j++) {
                positions[j] = json_positions.getInt(j);
            }
            LineExt lineExt = new LineExt(SymbolType.valueOf(json_line.getString("symbol")), positions, json_line.getInt("count"), json_line.getBoolean("left", true));
            lineExt.setLeft(json_line.getBoolean("left", true));
            lineExt.setAward(json_line.getInt("award", 0));
            lineExt.setBonusLine(json_line.getBoolean("bonus_line", false));
            JsonArray json_muls = json_line.getJsonArray("multiplers");
            int json_muls_len = json_muls.length();
            Multiplier[] multipliers = new Multiplier[json_muls_len];
            for (int k = 0; k < json_muls_len; k++) {
                JsonObject json_mul = json_muls.getJsonObject(k);
                multipliers[k] = new Multiplier(json_mul.getInt("multiplier"), json_mul.getInt("reel"));
            }
            lineExt.setMultipliers(multipliers);
            linesExt.add(lineExt);
        }

        // Specials
        List<boolean[]> specials = new ArrayList<boolean[]>();
        array = json.getJsonArray("specials");
        array_length = array.length();
        for (int i = 0; i < array_length; i++) {
            JsonArray reel = array.getJsonArray(i);
            int symbols_count = reel.length();
            boolean[] state = new boolean[symbols_count];
            for (int j = 0; j < symbols_count; j++) {
                state[j] = reel.getBoolean(j);
            }
            specials.add(state);
        }

        // Stop
        view.stop(symbols, linesExt.getLines(), specials);

        // Buttons
        if (!json.isNull("is_cup") && json.getBoolean("is_cup")) {
            if (!json.isNull("cup_top"))
                this.updateCupTop(json.getJsonArray("cup_top"));
            this.initCupState(json.getJsonObject("cup"), true);
        } else {
            this.initButtons(json.getJsonObject("state"));
            this.initConfig(json.getJsonObject("config"));
        }
        int extra_award = json.getInt("extra_award");
        this.setAward(this.getAward() + extra_award);
        this.setCupScore(this.getCupScore() + extra_award);
        this.setFreeSpins(json.getInt("free_spins"));
        this.setForceSpin(json.getBoolean("force_spin"));
        return json.getJsonArray("effects");
    }

    public void requestMaxBet(SlotsType type, CancelableHandler response_handler) throws StringCodeException {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("type", type.toString());
        super.asyncMethod("jsMaxBet", params, response_handler);
    }

    public void requestChangeConfig(int lines, int bet, CancelableHandler response_handler) throws StringCodeException {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("bet", String.valueOf(bet));
        params.put("lines", String.valueOf(lines));
        super.asyncMethod("jsChangeConfig", params, response_handler);
    }

    public void responseChangeConfig(String response, Machine view) throws StringCodeException {
        JsonObject json = super.parseResponse(response);
        this.updateUserInfo(json.getJsonObject("user"));
        this.initButtons(json.getJsonObject("state"));
        this.initConfig(json.getJsonObject("config"));
        // Lines
        view.clearLines();
        JsonArray array = json.getJsonArray("lines");
        int array_length = array.length();
        for (int i = 0; i < array_length; i++) {
            JsonArray line = array.getJsonArray(i);
            int symbols_count = line.length();
            int[] positions = new int[symbols_count];
            for (int j = 0; j < symbols_count; j++)
                positions[j] = line.getInt(j);
            view.addLine(positions, 0, 0, true);
        }
        view.showAllLines();
    }

    public void requestTakeBonus(CancelableHandler response_handler) throws StringCodeException {
        super.asyncMethod("jsTakeBonus", null, response_handler);
    }

    public int responseTakeBonus(String response) throws StringCodeException {
        JsonObject json = super.parseResponse(response);
        this.initBonus(json.getJsonObject("r_bonus"));
        this.updateUserInfo(json.getJsonObject("user"));
        return json.getInt("bonus", 0);
    }

    public void requestSuperBonus(CancelableHandler response_handler) throws StringCodeException {
        super.asyncMethod("jsTakeSuperBonus", null, response_handler);
    }

    public int responseSuperBonus(String response) throws StringCodeException {
        JsonObject json = super.parseResponse(response);
        this.initBonus(json.getJsonObject("r_bonus"));
        this.updateUserInfo(json.getJsonObject("user"));
        return json.getInt("bonus", 0);
    }

    public void requestSpinRoulette(CancelableHandler response_handler) throws StringCodeException {
        super.asyncMethod("jsSpinRoulette", null, response_handler);
    }

    public int responseSpinRoulette(String response) throws StringCodeException {
        JsonObject json = super.parseResponse(response);
        this.initBonus(json.getJsonObject("r_bonus"));
        this.updateUserInfo(json.getJsonObject("user"));
        return json.getInt("bonus", 0);
    }

    public void requestInitRisk(CancelableHandler response_handler) throws StringCodeException {
        super.asyncMethod("jsInitRisk", null, response_handler);
    }

    public CardInfo[] responseInitRisk(String response) throws StringCodeException {
        JsonObject json = super.parseResponse(response);
        this.updateUserInfo(json.getJsonObject("user"));
        this.setCanRisk(json.getBoolean("risk"));
        JsonArray cards = json.getJsonArray("last_cards");
        int count = cards.length();
        CardInfo[] result = new CardInfo[count];
        for (int i = 0; i < count; i++) {
            JsonObject card = cards.getJsonObject(i);
            CardInfo card_info = new CardInfo();
            card_info.setRank(card.getString("rank"));
            card_info.setSuit(card.getString("suit"));
            result[i] = card_info;
        }
        return result;
    }

    public void requestInitRiskGreater(CancelableHandler response_handler) throws StringCodeException {
        super.asyncMethod("jsInitGreater", null, response_handler);
    }

    public CardInfo responseInitGreater(String response) throws StringCodeException {
        JsonObject json = super.parseResponse(response);
        this.updateUserInfo(json.getJsonObject("user"));
        this.setCanRisk(json.getBoolean("risk"));
        this.setRiskCardsCount(json.getInt("cards_count"));
        CardInfo result = new CardInfo();
        JsonObject card = json.getJsonObject("card");
        result.setRank(card.getString("rank"));
        result.setSuit(card.getString("suit"));
        return result;
    }

    public void requestRisk(CardColor color, CancelableHandler response_handler) throws StringCodeException {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("color", color.toString());
        super.asyncMethod("jsRisk", params, response_handler);
    }

    public CardInfo responseRisk(String response) throws StringCodeException {
        JsonObject json = super.parseResponse(response);
        this.updateUserInfo(json.getJsonObject("user"));
        CardInfo card = new CardInfo();
        JsonObject js_card = json.getJsonObject("card");
        card.setRank(js_card.getString("rank"));
        card.setSuit(js_card.getString("suit"));
        return card;
    }

    public void requestRiskGreater(int index, CancelableHandler response_handler) throws StringCodeException {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("index", String.valueOf(index));
        super.asyncMethod("jsRiskGreater", params, response_handler);
    }

    public CardInfo[] responseRiskGreater(String response) throws StringCodeException {
        JsonObject json = super.parseResponse(response);
        this.updateUserInfo(json.getJsonObject("user"));
        JsonArray js_cards = json.getJsonArray("cards");
        CardInfo[] cards = new CardInfo[js_cards.length()];
        for (int i = 0; i < cards.length; i++) {
            cards[i] = new CardInfo();
            JsonObject js_card = js_cards.getJsonObject(i);
            cards[i].setRank(js_card.getString("rank"));
            cards[i].setSuit(js_card.getString("suit"));
        }
        return cards;
    }

    public void requestGetTop(int type, CancelableHandler response_handler) throws StringCodeException {
        HashMap<String, String> params = new HashMap<String, String>();
        if (type < 1 && type > 4) type = 1;
        params.put("type", String.valueOf(type));
        super.asyncMethod("jsLeaders", params, response_handler);
    }

    public UserInfo[] responseGetTop(String response) throws StringCodeException {
        JsonObject json = super.parseResponse(response);
        JsonArray top = json.getJsonArray("top");
        int count = top.length();
        UserInfo[] result = new UserInfo[count];
        for (int i = 0; i < count; i++) {
            JsonObject user = top.getJsonObject(i);
            UserInfo info = new UserInfo();
            info.setId(user.getString("id"));
            info.setName(user.getString("name"));
            info.setImage(user.getString("image"));
            info.setValue(user.getInt("value"));
            result[i] = info;
        }
        this.leaders_hourly_time = json.getInt("hourly_time");
        this.leaders_daily_time = json.getInt("daily_time");
        this.leaders_update_time = utils.getTimestamp();
        return result;
    }

    public void requestInitBank(CancelableHandler response_handler) throws StringCodeException {
        super.asyncMethod("jsBank", null, response_handler);
    }

    public BankInfo[] responseInitBank(String response) throws StringCodeException {
        JsonObject json = super.parseResponse(response);
        JsonArray items = json.getJsonArray("items");
        int count = items.length();
        BankInfo[] result = new BankInfo[count];
        /*for (int i = 0; i < count; i++) {
			JsonObject item = items.getJsonObject(i);
			System.out.println("item id from json " + item.getString("id"));
			BankInfo info = new BankInfo();
			info.setId(item.getString("id"));
			info.setPrice(item.getInt("price"));
			info.setDiscount(item.getInt("discount"));
			info.setGold(item.getInt("gold"));
			info.setBonusGold(item.getInt("bonus_gold", 0));
			info.setRecommended(item.getBoolean("recommended", false));
			result[i] = info;
		}*/

        int index = 0;
        for (int i = 7; i >= 3; i--) {
            //JsonObject item = items.getJsonObject(i);
            BankInfo info = new BankInfo();
            info.setId("" + i);
            info.setPrice(1000);
            info.setDiscount(0);
            info.setGold(1000);
            info.setBonusGold(0);
            info.setRecommended(false);
            result[index] = info;
            index++;
        }
        return result;
    }

    public void requestGetPurchase(String id, CancelableHandler response_handler) throws StringCodeException {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id", id);
        super.asyncMethod("jsGetPurchase", params, response_handler);
    }

    public BankInfo responseGetPurchase(String response) throws StringCodeException {
        JsonObject json = super.parseResponse(response);
        BankInfo info = new BankInfo();
        if (!json.isNull("item")) {
            JsonObject item = json.getJsonObject("item");
            info.setId(item.getString("id"));
            info.setPrice(item.getInt("price"));
            info.setDiscount(item.getInt("discount"));
            info.setGold(item.getInt("gold"));
            info.setBonusGold(item.getInt("bonus_gold", 0));
            info.setRecommended(item.getBoolean("recommended", false));
        }
        return info;
    }

    public void requestChangeName(CancelableHandler response_handler, String name) throws StringCodeException {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("name", name);
        super.asyncMethod("jsChangeName", params, response_handler);
    }

    public void responseChangeName(String response) throws StringCodeException {
        JsonObject json = super.parseResponse(response);
        this.updateUserInfo(json.getJsonObject("user"), true);
    }

    public void requestBonusState(CancelableHandler response_handler) throws StringCodeException {
        HashMap<String, String> params = new HashMap<String, String>();
        super.asyncMethod("jsBonusState", params, response_handler);
    }

    public JsonObject responseBonusState(String response) throws StringCodeException {
        JsonObject json = super.parseResponse(response);
        this.updateUserInfo(json.getJsonObject("user"));
        return json.getJsonObject("state");
    }

    public void requestBonusProc(CancelableHandler response_handler, int index) throws StringCodeException {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("index", String.valueOf(index));
        super.asyncMethod("jsBonusProc", params, response_handler);
    }

    public JsonObject responseBonusProc(String response) throws StringCodeException {
        JsonObject json = super.parseResponse(response);
        this.updateUserInfo(json.getJsonObject("user"));
        return json.getJsonObject("state");
    }

    public void requestInitChests(CancelableHandler response_handler) throws StringCodeException {
        super.asyncMethod("jsInitChests", null, response_handler);
    }

    public int[] responseInitChests(String response) throws StringCodeException {
        JsonObject json = super.parseResponse(response);
        this.updateUserInfo(json.getJsonObject("user"));
        JsonArray values = json.getJsonArray("values");
        int length = values.length();
        int[] chests = new int[length];
        for (int i = 0; i < length; i++)
            chests[i] = values.getInt(i);
        return chests;
    }

    public void requestOpenChest(CancelableHandler response_handler, int index) throws StringCodeException {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("index", String.valueOf(index));
        super.asyncMethod("jsOpenChest", params, response_handler);
    }

    public int responseOpenChest(String response) throws StringCodeException {
        JsonObject json = super.parseResponse(response);
        this.updateUserInfo(json.getJsonObject("user"));
        return json.getInt("value");
    }

    public void requestAddGold(CancelableHandler response_handler, String purchase_id, String order_id) throws StringCodeException {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("purchase_id", String.valueOf(purchase_id));
        params.put("order_id", String.valueOf(order_id));
        super.asyncMethod("jsAddGold", params, response_handler, true);
    }

    public boolean responseAddGold(String response) throws StringCodeException {
        JsonObject json = super.parseResponse(response);
        this.updateUserInfo(json.getJsonObject("user"));
        if (!json.isNull("r_bonus"))
            this.initBonus(json.getJsonObject("r_bonus"));
        return json.getBoolean("result");
    }

    public void requestTakeAward() throws StringCodeException {
        super.asyncMethod("jsTakeAward", null, null);
    }

    public void requestCupRegister(CancelableHandler response_handler) throws StringCodeException {
        super.asyncMethod("jsCupRegister", null, response_handler);
    }

    public void responseCupRegister(String response) throws StringCodeException {
        JsonObject json = super.parseResponse(response);
        this.updateUserInfo(json.getJsonObject("user"));
        this.cup_type = SlotsType.NONE;
        this.initCupState(json.getJsonObject("cup"), false);
        if (!json.isNull("cup_top")) {
            this.updateCupTop(json.getJsonArray("cup_top"));
        }
    }

    public void requestCupInit(CancelableHandler response_handler, SlotsType type) throws StringCodeException {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("type", type.toString());
        super.asyncMethod("jsCupInit", params, response_handler);
    }

    public void responseCupInit(String response, Machine view) throws StringCodeException {
        JsonObject json = super.parseResponse(response);
        this.updateUserInfo(json.getJsonObject("user"));
        this.cup_type = SlotsType.NONE;
        this.initCupState(json.getJsonObject("cup"), false);
        if (!json.isNull("cup_top")) {
            this.updateCupTop(json.getJsonArray("cup_top"));
        }
        // Lines
        if (view == null) return;
        view.clearLines();
        JsonArray array = json.getJsonArray("lines");
        int array_length = array.length();
        for (int i = 0; i < array_length; i++) {
            JsonArray line = array.getJsonArray(i);
            int symbols_count = line.length();
            int[] positions = new int[symbols_count];
            for (int j = 0; j < symbols_count; j++)
                positions[j] = line.getInt(j);
            view.addLine(positions, 0, 0, true);
        }
        view.showLinesOneByOne();
        // Symbols
        array = json.getJsonArray("symbols");
        array_length = array.length();
        SymbolType[][] symbols = new SymbolType[array_length][];
        for (int i = 0; i < array_length; i++) {
            JsonArray reel = array.getJsonArray(i);
            int symbols_count = reel.length();
            SymbolType[] values = new SymbolType[symbols_count];
            for (int j = 0; j < symbols_count; j++)
                values[j] = SymbolType.valueOf(reel.getString(j));
            symbols[i] = values;
        }
        view.setSymbols(symbols);
    }

    public void requestCupTop(CancelableHandler response_handler) throws StringCodeException {
        super.asyncMethod("jsCupTop", null, response_handler, true);
    }

    public void responseCupTop(String response) throws StringCodeException {
        JsonObject json = super.parseResponse(response);
        this.cup_type = SlotsType.NONE;
        this.initCupState(json.getJsonObject("cup"), false);
        JsonArray top = json.getJsonArray("cup_top");
        this.updateCupTop(top);
    }

    private void updateCupTop(JsonArray top) throws StringCodeException {
        if (top == null) return;
        List<UserInfo> result = new LinkedList<UserInfo>();
        this.setCupWinned(false);
        int count = top.length();
        for (int i = 0; i < count; i++) {
            JsonObject user = top.getJsonObject(i);
            UserInfo info = new UserInfo();
            info.setId(user.getString("id"));
            info.setName(user.getString("name"));
            info.setImage(user.getString("image"));
            info.setScore(user.getInt("score"));
            info.setValue(user.getInt("award"));
            result.add(info);
            if (info.getId().equals(this.getUserId())) {
                this.setCupWinned(true);
            }
        }
        this.cup_leaders = result.toArray(new UserInfo[0]);
    }

    public void requestTakeBonusMail(String id, CancelableHandler response_handler) throws StringCodeException {
        if (id == null) return;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id", id);
        super.asyncMethod("jsTakeBonusMail", params, response_handler);
    }

    public void responseTakeBonusMail(String response) throws StringCodeException {
        JsonObject json = super.parseResponse(response);
        this.updateUserInfo(json.getJsonObject("user"), false);
        // Mail Bonus
        if (!json.isNull("mail")) {
            this.initMail(json.getJsonObject("mail"));
        } else this.initMail(null);
    }

    private void updateUserInfo(JsonObject user) throws StringCodeException {
        this.updateUserInfo(user, false);
    }

    // Private Methods

    private void updateUserInfo(JsonObject user, boolean update_name) throws StringCodeException {
        if (user == null) return;
        this.setMoney(user.getInt("money"));
        this.setAward(user.getInt("award"));
        if (update_name) {
            this.setUserId(user.getString("user_id"));
        }
    }

    private void initButtons(JsonObject state) throws StringCodeException {
        this.setCanSpin(state.getBoolean("spin"));
        this.setCanRisk(state.getBoolean("risk"));
        this.setCanBetMax(state.getBoolean("max_bet"));
        this.setCanChangeLines(state.getBoolean("lines"));
        this.setCanChangeBet(state.getBoolean("bets"));
    }

    private void initConfig(JsonObject config) throws StringCodeException {
        this.setCurrentLines(config.getInt("lines"));
        this.setCurrentBet(config.getInt("bet"));
        this.setFreeSpins(config.getInt("free_spins"));
        this.setBonusGame(BonusGame.valueOf(config.getString("bonus_game")));
    }

    private void initCupState(JsonObject state, boolean short_info) throws StringCodeException {
        this.setCupActive(state.getBoolean("active"));
        if (this.isCupActive()) {
            this.setCupEnds(state.getInt("ends") + 1);
            this.setCupScore(state.getInt("score"));
            this.setCupType(SlotsType.valueOf(state.getString("type")));
            this.setCupSpinsRemain(state.getInt("spins_remain"));
            this.setCupRegistered(state.getBoolean("registered"));
        } else {
            this.setCupSpinsRemain(0);
            this.setCupRegistered(false);
            this.setCupStarts(state.getInt("starts") + 1);
        }
        if (!short_info) {
            JsonArray js_awards = state.getJsonArray("awards");
            int[] awards = new int[js_awards.length()];
            int total = 0;
            for (int i = 0; i < awards.length; i++) {
                awards[i] = js_awards.getInt(i);
                total += awards[i];
            }
            this.setCupAwards(awards);
            this.setCupAward(total);
        }
        this.cup_update_time = utils.getTimestamp();
        this.setCupSpins(state.getInt("spins"));
        this.setCupTime(state.getInt("time"));
        this.setCupPay(state.getInt("pay"));
    }

    private void initBonus(JsonObject state) throws StringCodeException {
        if (state == null) return;
        this.update_time = utils.getTimestamp();
        this.hourly_bonus_time = state.getInt("b_time", 0);
        this.hourly_bonus_total = state.getInt("b_total", 0);
        this.super_bonus_count = state.getInt("b_count", 0);
        this.roulette_time = state.getInt("r_time", 0);
        this.roulette_total = state.getInt("r_total", 0);
        this.roulette_multipler = state.getInt("r_mult", 0);
        this.roulette_position = state.getInt("r_position", 0);
        this.roulette_bonus_spins = state.getInt("r_spins", 0);
    }

    private void initMail(JsonObject state) throws StringCodeException {
        if (state != null) {
            this.bonus_mail_message = state.getString("message");
            this.bonus_mail_money = state.getInt("money", 0);
            this.bonus_mail_id = state.getString("id");
        } else {
            this.bonus_mail_message = null;
            this.bonus_mail_money = 0;
            this.bonus_mail_id = null;
        }
    }

    public String getUserId() {
        if (this.user_id == null)
            this.user_id = "0";
        return this.user_id;
    }

    // Getters & Setters

    public void setUserId(String user_id) {
        this.user_id = user_id;
    }

    public int getMoney() {
        return this.money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int[] getValidLines() {
        return this.valid_lines;
    }

    private void setValidLines(int[] valid_lines) {
        this.valid_lines = valid_lines;
    }

    public int[] getValidBets() {
        return this.valid_bets;
    }

    private void setValidBets(int[] valid_bets) {
        this.valid_bets = valid_bets;
    }

    public int[] getValidAutospins() {
        return this.valid_autospins;
    }

    private void setValidAutospins(int[] valid_autospins) {
        this.valid_autospins = valid_autospins;
    }

    public int getCurrentBet() {
        return this.current_bet;
    }

    private void setCurrentBet(int current_bet) {
        this.current_bet = current_bet;
    }

    public int getCurrentLines() {
        return this.current_lines;
    }

    private void setCurrentLines(int current_lines) {
        this.current_lines = current_lines;
    }

    public int getCurrentLinesCount() {
        if (this.valid_lines == null) return 0;
        if (this.current_lines >= this.valid_lines.length) return 0;
        return this.valid_lines[this.current_lines];
    }

    public boolean isMaxBet() {
        if (this.valid_bets == null) return false;
        if (this.valid_lines == null) return false;
        if (this.current_lines < this.valid_lines.length - 1) return false;
        return this.current_bet >= this.valid_bets.length - 1;
    }

    public BonusGame getBonusGame() {
        if (this.bonus_game == null)
            this.bonus_game = BonusGame.NONE;
        return this.bonus_game;
    }

    private void setBonusGame(BonusGame bonus_game) {
        this.bonus_game = bonus_game;
    }

    public int getAward() {
        return this.award;
    }

    private void setAward(int award) {
        this.award = award;
    }

    public boolean isCanBetMax() {
        return can_bet_max;
    }

    private void setCanBetMax(boolean can_bet_max) {
        this.can_bet_max = can_bet_max;
    }

    public boolean isCanChangeLines() {
        return can_change_lines;
    }

    private void setCanChangeLines(boolean can_change_lines) {
        this.can_change_lines = can_change_lines;
    }

    public boolean isCanChangeBet() {
        return can_change_bet;
    }

    private void setCanChangeBet(boolean can_change_bet) {
        this.can_change_bet = can_change_bet;
    }

    public boolean isCanCupSpin() {
        return (this.cup_active && this.cup_spins_remain > 0) || this.force_spin;
    }

    public boolean isCanSpin() {
        return can_spin;
    }

    public void setCanSpin(boolean can_spin) {
        this.can_spin = can_spin;
    }

    public boolean isCanRisk() {
        return can_risk;
    }

    private void setCanRisk(boolean can_risk) {
        this.can_risk = can_risk;
    }

    public int getFreeSpins() {
        return this.free_spins;
    }

    public void setFreeSpins(int free_spins) {
        this.free_spins = free_spins;
    }

    public SlotsType getSlotsType() {
        if (this.slots_type == null || this.slots_type == SlotsType.NONE)
            this.slots_type = SlotsType.GARAGE;
        return this.slots_type;
    }

    public void setSlotsType(SlotsType slots_type) {
        this.slots_type = slots_type;
    }

    public boolean isSoundOn() {
        return this.sound_on;
    }

    public void setSoundOn(boolean sound_on) {
        this.sound_on = sound_on;
        AppWrapper wrapper = Connection.getWrapper();
        if (wrapper != null) {
            wrapper.writeSoundState(sound_on);
        }
    }

    public boolean isCupActive() {
        return this.cup_active;
    }

    // Cups

    public void setCupActive(boolean cup_active) {
        this.cup_active = cup_active;
    }

    public boolean isCupRegistered() {
        return this.cup_registered;
    }

    public void setCupRegistered(boolean cup_registered) {
        this.cup_registered = cup_registered;
    }

    public int[] getCupAwards() {
        return this.cup_awards;
    }

    public void setCupAwards(int[] cup_awards) {
        this.cup_awards = cup_awards;
    }

    public int getCupSpinsRemain() {
        return this.cup_spins_remain;
    }

    public void setCupSpinsRemain(int cup_spins_remain) {
        this.cup_spins_remain = cup_spins_remain;
    }

    public int getCupAward() {
        return this.cup_award;
    }

    public void setCupAward(int cup_award) {
        this.cup_award = cup_award;
    }

    public int getCupSpins() {
        return this.cup_spins;
    }

    public void setCupSpins(int cup_spins) {
        this.cup_spins = cup_spins;
    }

    public int getCupTime() {
        return this.cup_time;
    }

    public void setCupTime(int cup_time) {
        this.cup_time = cup_time;
    }

    public int getCupPay() {
        return this.cup_pay;
    }

    public void setCupPay(int cup_pay) {
        this.cup_pay = cup_pay;
    }

    public int getCupEnds() {
        return this.cup_ends;
    }

    public void setCupEnds(int cup_ends) {
        this.cup_ends = cup_ends;
    }

    public int getCupStarts() {
        return this.cup_starts;
    }

    public void setCupStarts(int cup_starts) {
        this.cup_starts = cup_starts;
    }

    public int getCupCountdown() {
        if (this.cup_type == null) return 0;
        int time = utils.getTimestamp();
        int countdown = this.cup_active ? this.cup_ends : this.cup_starts;
        int total = countdown - (time - this.cup_update_time);
        return total < 0 ? 0 : total > 86400 ? 86400 : total;
    }

    public int getCupPlace() {
        if (this.cup_leaders == null) return 0;
        String id = this.getUserId();
        if (id == null) return 0;
        for (int i = 0; i < this.cup_leaders.length; i++) {
            if (this.cup_leaders[i].getScore() < this.cup_score) {
                int place = i + 1;
                return place > 10 ? 0 : place;
            }
        }
        return 0;
    }

    public int getCupScore() {
        return this.cup_score;
    }

    public void setCupScore(int cup_score) {
        this.cup_score = cup_score;
    }

    public SlotsType getCupType() {
        return this.cup_type;
    }

    public void setCupType(SlotsType cup_type) {
        this.cup_type = cup_type;
    }

    public UserInfo[] getCupLeaders() {
        return this.cup_leaders;
    }

    public boolean isForceSpin() {
        return this.force_spin;
    }

    public void setForceSpin(boolean force_spin) {
        this.force_spin = force_spin;
    }

    public boolean isCupWinned() {
        return this.cup_winned;
    }

    public void setCupWinned(boolean cup_winned) {
        this.cup_winned = cup_winned;
    }

    public RiskType getRiskType() {
        return this.risk_type;
    }

    public void setRiskType(RiskType risk_type) {
        this.risk_type = risk_type;
    }

    public int getRiskCardsCount() {
        return this.risk_cards_count;
    }

    public void setRiskCardsCount(int risk_cards_count) {
        this.risk_cards_count = risk_cards_count;
    }

    public boolean isNotificationsOn() {
        return this.notifications_on;
    }

    public void setNotificationsOn(boolean notifications_on) {
        this.notifications_on = notifications_on;
        AppWrapper wrapper = Connection.getWrapper();
        if (wrapper != null) {
            wrapper.writeNotificationState(notifications_on);
        }
    }

    public int getHourlyBonusTime() {
        return this.update_time - utils.getTimestamp() + this.hourly_bonus_time;
    }

    public void setHourlyBonusTime(int hourly_bonus_time) {
        this.hourly_bonus_time = hourly_bonus_time;
    }

    public int getHourlyBonusTotal() {
        return this.hourly_bonus_total;
    }

    public void setHourlyBonusTotal(int hourly_bonus_total) {
        this.hourly_bonus_total = hourly_bonus_total;
    }

    public int getSuperBonusCount() {
        return this.super_bonus_count;
    }

    public void setSuperBonusCount(int super_bonus_count) {
        this.super_bonus_count = super_bonus_count;
    }

    public int getRouletteTime() {
        int time = this.update_time - utils.getTimestamp() + this.roulette_time;
        return time >= 0 ? time : 0;
    }

    public void setRouletteTime(int roulette_time) {
        this.roulette_time = roulette_time;
    }

    public int getRouletteTotal() {
        return this.roulette_total;
    }

    public void setRouletteTotal(int roulette_total) {
        this.roulette_total = roulette_total;
    }

    public int getRouletteMultipler() {
        return this.roulette_multipler;
    }

    public void setRouletteMultipler(int roulette_multipler) {
        this.roulette_multipler = roulette_multipler;
    }

    public int getRoulettePosition() {
        return this.roulette_position;
    }

    public void setRoulettePosition(int roulette_position) {
        this.roulette_position = roulette_position;
    }

    public int getRouletteBonusSpins() {
        return this.roulette_bonus_spins;
    }

    public void setRouletteBonusSpins(int roulette_bonus_spins) {
        this.roulette_bonus_spins = roulette_bonus_spins;
    }

    public int getRouletteSpins() {
        return this.roulette_bonus_spins + (this.getRouletteTime() > 0 ? 0 : 1);
    }

    public int getLeadersHourlyTime() {
        int time = this.leaders_hourly_time + this.leaders_update_time - utils.getTimestamp();
        return time > 0 ? time : 0;
    }

    public void setLeadersHourlyTime(int leaders_hourly_time) {
        this.leaders_hourly_time = leaders_hourly_time;
    }

    public int getLeadersDailyTime() {
        int time = this.leaders_daily_time + this.leaders_update_time - utils.getTimestamp();
        return time > 0 ? time : 0;
    }

    public void setLeadersDailyTime(int leaders_daily_time) {
        this.leaders_daily_time = leaders_daily_time;
    }

    public String getBonusMailMessage() {
        return this.bonus_mail_message;
    }

    public void setBonusMailMessage(String bonus_mail_message) {
        this.bonus_mail_message = bonus_mail_message;
    }

    public int getBonusMailMoney() {
        return this.bonus_mail_money;
    }

    public void setBonusMailMoney(int bonus_mail_money) {
        this.bonus_mail_money = bonus_mail_money;
    }

    public String getBonusMailId() {
        return this.bonus_mail_id;
    }

    public void setBonusMailId(String bonus_mail_id) {
        this.bonus_mail_id = bonus_mail_id;
    }

    public boolean isBonusMailExists() {
        return this.bonus_mail_id != null;
    }

    public enum LoginType {
        NONE,
        OK,
        FACEBOOK,
        GOOGLE
    }

}
