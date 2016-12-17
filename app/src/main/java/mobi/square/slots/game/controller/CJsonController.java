package mobi.square.slots.game.controller;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import mobi.square.slots.config.ApiConfig;
import mobi.square.slots.enums.BonusGame;
import mobi.square.slots.enums.CardColor;
import mobi.square.slots.enums.SlotsType;
import mobi.square.slots.error.StringCodeException;
import mobi.square.slots.game.bank.Bank;
import mobi.square.slots.game.bank.CPurchase;
import mobi.square.slots.game.slots.CCard;
import mobi.square.slots.game.slots.CSlots;
import mobi.square.slots.game.slots.CSymbol;
import mobi.square.slots.game.slots.CWinLine;
import mobi.square.slots.game.slots.CWinLine.Multiplier;
import mobi.square.slots.game.slots.Slots;
import mobi.square.slots.game.slots.bonusgames.CBookOfRaSlot;
import mobi.square.slots.game.slots.bonusgames.IBonusGame;
import mobi.square.slots.game.user.CUser;
import mobi.square.slots.utils.json.JsonNode;
import mobi.square.slots.utils.utils;

public class CJsonController {

    private static final int VERSION_ERROR = 107;
    private static final int SERVER_ERROR = 101;
    private static final int NO_ERROR_CODE = 0;
    private final CUser parent;

    // Constructors

    public CJsonController(CUser parent) {
        this.parent = parent;
    }

    // Public Methods

    public static JsonNode getJsonPurchase(CPurchase purchase) {
        HashMap<String, JsonNode> item = new HashMap<String, JsonNode>();
        item.put("id", new JsonNode(purchase.getType()));
        item.put("price", new JsonNode(purchase.getAmount()));
        item.put("discount", new JsonNode(purchase.getDiscount()));
        item.put("gold", new JsonNode(purchase.getGold()));
        item.put("bonus_gold", new JsonNode(purchase.getBonusGold()));
        item.put("recommended", new JsonNode(purchase.isRecommended()));
        return new JsonNode(item);
    }

    public boolean isAvailableSlot(SlotsType type) {
        switch (type) {
            case BOOK_OF_RA:
            case GLADIATORS:
            case FAIRYTALE:
            case CRAZY_MONKEY:
            case UNDERWATER_LIFE:
            case MONEY_GAME:
            case MAKEUP_GAME:
            case RESIDENT:
            case GARAGE:
            case ROCKCLIMBER:
                return true;
            default:
                return false;
        }
    }

    public String jsLobby() {
        HashMap<String, JsonNode> data = new HashMap<String, JsonNode>();
        if (!this.checkVersion()) {
            data.put("error", new JsonNode(VERSION_ERROR));
            return new JsonNode(data).toString();
        } else data.put("error", new JsonNode(SERVER_ERROR));
        try {
            data.put("user", new JsonNode(this.getUserInfo()));
            List<JsonNode> slots = new LinkedList<JsonNode>();
            for (Slots item : this.getParent().getSlots().getSlotsList()) {
                if (!this.isAvailableSlot(item.getType())) {
                    continue;
                }
                HashMap<String, JsonNode> slot = new HashMap<String, JsonNode>();
                slot.put("type", new JsonNode(item.getType().toString()));
                slot.put("revision", new JsonNode(item.getConfig().getRevision()));
                slot.put("min_level", new JsonNode(item.getMinLevel()));
                slot.put("max_level", new JsonNode(item.getMaxLevel()));
                slots.add(new JsonNode(slot));
            }
            data.put("slots", new JsonNode(slots));
            data.put("r_bonus", new JsonNode(this.getBonus()));
            data.put("error", new JsonNode(NO_ERROR_CODE));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JsonNode(data).toString();
    }

    public String jsInit(SlotsType type) {
        HashMap<String, JsonNode> data = new HashMap<String, JsonNode>();
        if (!this.checkVersion()) {
            data.put("error", new JsonNode(VERSION_ERROR));
            return new JsonNode(data).toString();
        } else data.put("error", new JsonNode(SERVER_ERROR));
        try {
            CSlots slots = this.getParent().getSlots();
            slots.setSlotsType(type);
            data.put("user", new JsonNode(this.getUserInfo()));
            List<JsonNode> lines = new LinkedList<JsonNode>();
            int count = slots.getLinesCount();
            for (int[] line : slots.getLines().getLines()) {
                lines.add(new JsonNode(line));
                if (lines.size() >= count) break;
            }
            data.put("lines", new JsonNode(lines));
            data.put("state", new JsonNode(this.getButtonsState()));
            data.put("risk_type", new JsonNode(slots.getConfig().getRiskType().toString()));
            if (slots.getBonusGameType() == BonusGame.BOOK_OF_RA_SLOT)
                data.put("bonus_symbol", new JsonNode(CBookOfRaSlot.class.cast(slots.getBonusGameInstance()).getBonusSymbol().toString()));
            // Symbols
            List<JsonNode> node = new LinkedList<JsonNode>();
            for (CSymbol[] symbols : slots.getCurrentSymbols()) {
                List<JsonNode> reel = new LinkedList<JsonNode>();
                for (int i = 0; i < symbols.length; i++)
                    reel.add(new JsonNode(symbols[i].getSymbol().toString()));
                node.add(new JsonNode(reel));
            }
            data.put("symbols", new JsonNode(node));
            // Config
            data.put("valid_autospins", new JsonNode(new int[]{10, 30, 50}));
            data.put("valid_lines", new JsonNode(slots.getConfig().getValidLines()));
            data.put("valid_bets", new JsonNode(slots.getConfig().getValidBets()));
            data.put("config", new JsonNode(this.getBetState()));
            data.put("error", new JsonNode(NO_ERROR_CODE));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JsonNode(data).toString();
    }

    public String jsSpin(SlotsType type) {
        HashMap<String, JsonNode> data = new HashMap<String, JsonNode>();
        if (!this.checkVersion()) {
            data.put("error", new JsonNode(VERSION_ERROR));
            return new JsonNode(data).toString();
        } else data.put("error", new JsonNode(SERVER_ERROR));
        try {
            CSlots slots = this.getParent().getSlots();
            slots.setSlotsType(type);

            System.out.println("before spin jose");
            slots.spin();
            System.out.println("after spin jose");


            data.put("user", new JsonNode(this.getUserInfo()));
            data.put("state", new JsonNode(this.getButtonsState()));
            data.put("config", new JsonNode(this.getBetState()));
            if (slots.getBonusGameType() == BonusGame.BOOK_OF_RA_SLOT)
                data.put("bonus_symbol", new JsonNode(CBookOfRaSlot.class.cast(slots.getBonusGameInstance()).getBonusSymbol().toString()));
            data.put("extra_award", new JsonNode(this.getParent().getSlots().getExtraAward()));
            data.put("force_spin", new JsonNode(this.getParent().getSlots().getExtraSpins() > 0));
            data.put("free_spins", new JsonNode(this.getParent().getSlots().getFreeSpins()));
            // Symbols
            List<JsonNode> node = new LinkedList<JsonNode>();
            for (CSymbol[] symbols : slots.getCurrentSymbols()) {
                List<JsonNode> reel = new LinkedList<JsonNode>();
                for (int i = 0; i < symbols.length; i++)
                    reel.add(new JsonNode(symbols[i].getSymbol().toString()));
                node.add(new JsonNode(reel));
            }
            data.put("symbols", new JsonNode(node));
            // Lines
            node = new LinkedList<JsonNode>();
            for (CWinLine info : slots.getWinningLines()) {
                int[] line = slots.getLines().getLines()[info.getIndex()];
                int[] total = new int[line.length + 2];
                for (int i = 0; i < line.length; i++)
                    total[i] = line[i];
                total[line.length + 1] = info.getCount();
                total[line.length] = info.isLeft() ? 0 : 1;
                node.add(new JsonNode(total));
            }

            //
            // Lines ext
            //
            StringBuilder json_line_ext = new StringBuilder();
            json_line_ext.append("[");
            for (int i = 0; i < slots.getWinningLines().length; i++) {
                CWinLine line = slots.getWinningLines()[i];
                int[] indexes = slots.getLines().getLines()[line.getIndex()];
                json_line_ext.append("{\"symbols\":[");
                json_line_ext.append(indexes[0]);
                for (int j = 1; j < indexes.length; j++)
                    json_line_ext.append(",").append(indexes[j]);
                json_line_ext.append("],\"count\":");
                json_line_ext.append(line.getCount());
                json_line_ext.append(",\"symbol\":\"");
                json_line_ext.append(line.getSymbol().toString());
                json_line_ext.append("\",\"left\":");
                json_line_ext.append(line.isLeft());
                json_line_ext.append(",\"award\":");
                json_line_ext.append(line.getPay());
                json_line_ext.append(",\"color\":\"");
                json_line_ext.append(slots.getLines().getColors()[line.getIndex()]);
                json_line_ext.append("\",\"multiplers\":[");
                if (line.getMultipliers() != null) {
                    for (int j = 0; j < line.getMultipliers().length; j++) {
                        Multiplier multiplier = line.getMultipliers()[j];
                        json_line_ext.append("{\"reel\":");
                        json_line_ext.append(multiplier.getReel());
                        json_line_ext.append(",\"multiplier\":");
                        json_line_ext.append(multiplier.getMultiplier());
                        if (j < line.getMultipliers().length - 1) {
                            json_line_ext.append("},");
                        } else {
                            json_line_ext.append("}");
                        }
                    }
                }
                json_line_ext.append("],\"bonus_line\":");
                json_line_ext.append(line.isBonusLine());
                if (i >= slots.getWinningLines().length - 1) {
                    json_line_ext.append("}");
                } else json_line_ext.append("},");
            }
            json_line_ext.append("]");
            data.put("lines_ext", new JsonNode(json_line_ext.toString(), false));
            //
            //
            //

            data.put("effects", new JsonNode("[".concat(slots.getEffects()).concat("]"), false));
            data.put("lines", new JsonNode(node));
            data.put("error", new JsonNode(NO_ERROR_CODE));
            //data.put("vip_request_to_continue", new JsonNode(!this.getParent().getSlots().getConfig().isRequiresVip() && this.getParent().isVip()));
            StringBuilder json_spec = new StringBuilder();
            json_spec.append("[");
            for (int i = 0; i < this.getParent().getSlots().getConfig().getReelsCount(); i++) {
                json_spec.append("[");
                for (int j = 0; j < this.getParent().getSlots().getConfig().getSymbolsOnReel(); j++) {
                    json_spec.append(this.getParent().getSlots().getCurrentSymbols()[i][j].isSpecial());
                    if (j < this.getParent().getSlots().getConfig().getSymbolsOnReel() - 1) {
                        json_spec.append(",");
                    }
                }
                if (i < this.getParent().getSlots().getConfig().getReelsCount() - 1) {
                    json_spec.append("],");
                } else json_spec.append("]");
            }
            json_spec.append("]");
            data.put("specials", new JsonNode(json_spec.toString(), false));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String json = new JsonNode(data).toString();
        // Debug {
        // System.out.print("Android spin: ");
        // System.out.println(json);
        // }
        return json;
    }

    public String jsChangeConfig() {
        HashMap<String, JsonNode> data = new HashMap<String, JsonNode>();
        if (!this.checkVersion()) {
            data.put("error", new JsonNode(VERSION_ERROR));
            return new JsonNode(data).toString();
        } else data.put("error", new JsonNode(SERVER_ERROR));
        try {
            data.put("user", new JsonNode(this.getUserInfo()));
            CSlots slots = this.getParent().getSlots();
            List<JsonNode> node = new LinkedList<JsonNode>();
            int count = slots.getLinesCount();
            for (int[] line : slots.getLines().getLines()) {
                node.add(new JsonNode(line));
                if (node.size() >= count) break;
            }
            data.put("lines", new JsonNode(node));
            data.put("state", new JsonNode(this.getButtonsState()));
            data.put("config", new JsonNode(this.getBetState()));
            data.put("error", new JsonNode(NO_ERROR_CODE));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JsonNode(data).toString();
    }

    public String jsChangeConfig(int lines, int bet) {
        HashMap<String, JsonNode> data = new HashMap<String, JsonNode>();
        if (!this.checkVersion()) {
            data.put("error", new JsonNode(VERSION_ERROR));
            return new JsonNode(data).toString();
        } else data.put("error", new JsonNode(SERVER_ERROR));
        try {
            data.put("user", new JsonNode(this.getUserInfo()));
            CSlots slots = this.getParent().getSlots();
            if (bet >= slots.getValidBets().length) bet = 0;
            if (lines >= slots.getValidLines().length) lines = 0;
            slots.setBet(slots.getValidBets()[bet]);
            slots.setLinesCount(slots.getValidLines()[lines]);
            List<JsonNode> node = new LinkedList<JsonNode>();
            int count = slots.getLinesCount();
            for (int[] line : slots.getLines().getLines()) {
                node.add(new JsonNode(line));
                if (node.size() >= count) break;
            }
            data.put("lines", new JsonNode(node));
            data.put("state", new JsonNode(this.getButtonsState()));
            data.put("config", new JsonNode(this.getBetState()));
            data.put("error", new JsonNode(NO_ERROR_CODE));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JsonNode(data).toString();
    }

    public String jsInitRisk() {
        HashMap<String, JsonNode> data = new HashMap<String, JsonNode>();
        if (!this.checkVersion()) {
            data.put("error", new JsonNode(VERSION_ERROR));
            return new JsonNode(data).toString();
        } else data.put("error", new JsonNode(SERVER_ERROR));
        try {
            data.put("last_cards", new JsonNode(this.getRiskLastCards()));
            data.put("risk", new JsonNode(this.getParent().getSlots().isCanRisk()));
            data.put("user", new JsonNode(this.getUserInfo()));
            data.put("error", new JsonNode(NO_ERROR_CODE));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JsonNode(data).toString();
    }

    public String jsRisk(CardColor color) {
        HashMap<String, JsonNode> data = new HashMap<String, JsonNode>();
        if (!this.checkVersion()) {
            data.put("error", new JsonNode(VERSION_ERROR));
            return new JsonNode(data).toString();
        } else data.put("error", new JsonNode(SERVER_ERROR));
        try {
            this.getParent().getSlots().getRiskColor().risk(color);
            CCard card = this.getParent().getSlots().getRiskColor().getLastCard();
            HashMap<String, JsonNode> card_node = new HashMap<String, JsonNode>();
            card_node.put("rank", new JsonNode(card.getRank().toString()));
            card_node.put("suit", new JsonNode(card.getSuit().toString()));
            data.put("card", new JsonNode(card_node));
            data.put("user", new JsonNode(this.getUserInfo()));
            data.put("error", new JsonNode(NO_ERROR_CODE));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JsonNode(data).toString();
    }

    public String jsInitRiskGreater() {
        HashMap<String, JsonNode> data = new HashMap<String, JsonNode>();
        if (!this.checkVersion()) {
            data.put("error", new JsonNode(VERSION_ERROR));
            return new JsonNode(data).toString();
        } else data.put("error", new JsonNode(SERVER_ERROR));
        try {
            this.getParent().getSlots().getRiskGreater().generateNewCard();
            CCard card = this.getParent().getSlots().getRiskGreater().getCurrentCard();
            HashMap<String, JsonNode> card_node = new HashMap<String, JsonNode>();
            card_node.put("rank", new JsonNode(card.getRank().toString()));
            card_node.put("suit", new JsonNode(card.getSuit().toString()));
            data.put("card", new JsonNode(card_node));
            data.put("cards_count", new JsonNode(this.getParent().getSlots().getRiskGreater().getCardsCount()));
            data.put("risk", new JsonNode(this.getParent().getSlots().isCanRisk()));
            data.put("user", new JsonNode(this.getUserInfo()));
            data.put("error", new JsonNode(NO_ERROR_CODE));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JsonNode(data).toString();
    }

    public String jsRiskGreater(int index) {
        HashMap<String, JsonNode> data = new HashMap<String, JsonNode>();
        if (!this.checkVersion()) {
            data.put("error", new JsonNode(VERSION_ERROR));
            return new JsonNode(data).toString();
        } else data.put("error", new JsonNode(SERVER_ERROR));
        try {
            this.getParent().getSlots().getRiskGreater().risk(index);
            data.put("cards", new JsonNode(this.getRiskGreaterCards()));
            data.put("user", new JsonNode(this.getUserInfo()));
            data.put("error", new JsonNode(NO_ERROR_CODE));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JsonNode(data).toString();
    }

    public String jsTakeAward() {
        HashMap<String, JsonNode> data = new HashMap<String, JsonNode>();
        if (!this.checkVersion()) {
            data.put("error", new JsonNode(VERSION_ERROR));
            return new JsonNode(data).toString();
        } else data.put("error", new JsonNode(SERVER_ERROR));
        try {
            this.getParent().getSlots().takeAward();
            data.put("user", new JsonNode(this.getUserInfo()));
            data.put("error", new JsonNode(NO_ERROR_CODE));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JsonNode(data).toString();
    }

    public String jsBank() {
        HashMap<String, JsonNode> data = new HashMap<String, JsonNode>();
        if (!this.checkVersion()) {
            data.put("error", new JsonNode(VERSION_ERROR));
            return new JsonNode(data).toString();
        } else data.put("error", new JsonNode(SERVER_ERROR));
        try {
            List<JsonNode> items = new LinkedList<JsonNode>();
            List<CPurchase> list = Bank.getGoldList();
            for (int i = 0; i < list.size(); i++) {
                CPurchase purchase = list.get(i);
                if (purchase.getType().equals("first")) {
                    continue;
                }
                items.add(getJsonPurchase(purchase));
            }
            data.put("items", new JsonNode(items));
            data.put("error", new JsonNode(NO_ERROR_CODE));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JsonNode(data).toString();
    }

    public String jsBonusState() {
        HashMap<String, JsonNode> data = new HashMap<String, JsonNode>();
        if (!this.checkVersion()) {
            data.put("error", new JsonNode(VERSION_ERROR));
            return new JsonNode(data).toString();
        } else data.put("error", new JsonNode(SERVER_ERROR));
        try {
            data.put("state", new JsonNode(this.getParent().getSlots().getBonusGameInstance().getStateJson()));
            data.put("user", new JsonNode(this.getUserInfo()));
            data.put("error", new JsonNode(NO_ERROR_CODE));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String json = new JsonNode(data).toString();
        return json;
    }

    public String jsBonusProc(int index) {
        HashMap<String, JsonNode> data = new HashMap<String, JsonNode>();
        if (!this.checkVersion()) {
            data.put("error", new JsonNode(VERSION_ERROR));
            return new JsonNode(data).toString();
        } else data.put("error", new JsonNode(SERVER_ERROR));
        try {
            IBonusGame bonus_game = this.getParent().getSlots().getBonusGameInstance();
            if (bonus_game != null) {
                bonus_game.proc(index);
                data.put("state", new JsonNode(bonus_game.getStateJson()));
                data.put("user", new JsonNode(this.getUserInfo()));
                data.put("error", new JsonNode(NO_ERROR_CODE));
                if (bonus_game.isOver()) {
                    this.getParent().getSlots().addAward(bonus_game.getAward());
                    this.getParent().getSlots().takeAward();
                    bonus_game.end();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String json = new JsonNode(data).toString();
        // System.out.print("jsBonusProc: ");
        // System.out.println(json);
        return json;
    }

    public String jsAddGold(String order_id, String purchase_id) {
        HashMap<String, JsonNode> data = new HashMap<String, JsonNode>();
        if (!this.checkVersion()) {
            data.put("error", new JsonNode(VERSION_ERROR));
            return new JsonNode(data).toString();
        } else data.put("error", new JsonNode(SERVER_ERROR));
        try {
            if (order_id == null || purchase_id == null)
                throw new StringCodeException("err_invalid_purchase");
            CPurchase purchase = Bank.getPurchase(purchase_id);
//			int result = purchase != null ? purchase.getGold() + purchase.getBonusGold() : 0;
            int result = purchase != null ? (Integer.valueOf(purchase_id) + 1000) : 100;
            if (result > 0) {
                int money = this.getParent().getMoney() + result;
                this.getParent().setMoney(money);
                //this.getParent().getController().save();
            }
            data.put("result", new JsonNode(result > 0));
            data.put("user", new JsonNode(this.getUserInfo()));
            data.put("error", new JsonNode((result == 0) ? 105 : NO_ERROR_CODE));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JsonNode(data).toString();
    }

    public String jsTakeBonus() {
        HashMap<String, JsonNode> data = new HashMap<String, JsonNode>();
        if (!this.checkVersion()) {
            data.put("error", new JsonNode(VERSION_ERROR));
            return new JsonNode(data).toString();
        } else data.put("error", new JsonNode(SERVER_ERROR));
        try {
            int money = this.getParent().getController().takeHourlyBonus();
            data.put("bonus", new JsonNode(money));
            data.put("r_bonus", new JsonNode(this.getBonus()));
            data.put("user", new JsonNode(this.getUserInfo()));
            data.put("error", new JsonNode(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JsonNode(data).toString();
    }

    public String jsTakeSuperBonus() {
        HashMap<String, JsonNode> data = new HashMap<String, JsonNode>();
        if (!this.checkVersion()) {
            data.put("error", new JsonNode(VERSION_ERROR));
            return new JsonNode(data).toString();
        } else data.put("error", new JsonNode(SERVER_ERROR));
        try {
            int money = this.getParent().getController().takeSuperBonus();
            data.put("bonus", new JsonNode(money));
            data.put("r_bonus", new JsonNode(this.getBonus()));
            data.put("user", new JsonNode(this.getUserInfo()));
            data.put("error", new JsonNode(NO_ERROR_CODE));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JsonNode(data).toString();
    }

    public String jsSpinRoulette() {
        HashMap<String, JsonNode> data = new HashMap<String, JsonNode>();
        if (!this.checkVersion()) {
            data.put("error", new JsonNode(VERSION_ERROR));
            return new JsonNode(data).toString();
        } else data.put("error", new JsonNode(SERVER_ERROR));
        try {
            int money = this.getParent().getController().rotateRulette();
            data.put("bonus", new JsonNode(money));
            data.put("r_bonus", new JsonNode(this.getBonus()));
            data.put("user", new JsonNode(this.getUserInfo()));
            data.put("error", new JsonNode(NO_ERROR_CODE));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JsonNode(data).toString();
    }

    public String jsDummy() {
        HashMap<String, JsonNode> data = new HashMap<String, JsonNode>();
        if (!this.checkVersion()) {
            data.put("error", new JsonNode(VERSION_ERROR));
            return new JsonNode(data).toString();
        } else data.put("error", new JsonNode(SERVER_ERROR));
        try {
            // Place Code Here
            data.put("user", new JsonNode(this.getUserInfo()));
            data.put("error", new JsonNode(NO_ERROR_CODE));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JsonNode(data).toString();
    }

    // Private Methods

    private boolean checkVersion() {
        return true;
    }

    private HashMap<String, JsonNode> getUserInfo() {
        HashMap<String, JsonNode> data = new HashMap<String, JsonNode>();
        data.put("money", new JsonNode(this.getParent().getMoney()));
        data.put("award", new JsonNode(this.getParent().getSlots().getAward()));
        data.put("user_id", new JsonNode(this.getParent().getId()));
        return data;
    }

    private HashMap<String, JsonNode> getBonus() {
        HashMap<String, JsonNode> data = new HashMap<String, JsonNode>();
        int time = utils.getTimestamp();
        int t = this.getParent().getHourlyBonusTime() + ApiConfig.HOURLY_BONUS_TIME - time;
        data.put("b_time", new JsonNode(t > 0 ? t : 0));
        data.put("b_count", new JsonNode(this.getParent().getSuperBonusCount()));
        data.put("b_total", new JsonNode(ApiConfig.HOURLY_BONUS_TIME));
        t = this.getParent().getRouletteTime() + ApiConfig.RULETTE_TIME - time;
        data.put("r_time", new JsonNode(t > 0 ? t : 0));
        data.put("r_mult", new JsonNode(this.getParent().getRouletteMultipler()));
        data.put("r_position", new JsonNode(this.getParent().getRoulettePosition()));
        data.put("r_spins", new JsonNode(this.getParent().getRouletteBonusSpins()));
        data.put("r_total", new JsonNode(ApiConfig.RULETTE_TIME));
        return data;
    }

    private List<JsonNode> getRiskLastCards() {
        List<JsonNode> cards = new LinkedList<JsonNode>();
        List<CCard> last_cards = this.getParent().getSlots().getRiskColor().getLastCards();
        for (CCard card : last_cards) {
            HashMap<String, JsonNode> card_node = new HashMap<String, JsonNode>();
            card_node.put("rank", new JsonNode(card.getRank().toString()));
            card_node.put("suit", new JsonNode(card.getSuit().toString()));
            cards.add(new JsonNode(card_node));
        }
        return cards;
    }

    private List<JsonNode> getRiskGreaterCards() {
        List<JsonNode> cards = new LinkedList<JsonNode>();
        CCard[] last_cards = this.getParent().getSlots().getRiskGreater().getUserCards();
        for (CCard card : last_cards) {
            HashMap<String, JsonNode> card_node = new HashMap<String, JsonNode>();
            card_node.put("rank", new JsonNode(card.getRank().toString()));
            card_node.put("suit", new JsonNode(card.getSuit().toString()));
            cards.add(new JsonNode(card_node));
        }
        return cards;
    }

    private HashMap<String, JsonNode> getButtonsState() {
        HashMap<String, JsonNode> data = new HashMap<String, JsonNode>();
        data.put("spin", new JsonNode(this.getParent().getSlots().isCanSpin()));
        data.put("risk", new JsonNode(this.getParent().getSlots().isCanRisk()));
        data.put("max_bet", new JsonNode(this.getParent().getSlots().isCanBetMax()));
        data.put("lines", new JsonNode(this.getParent().getSlots().isCanChangeLines()));
        data.put("bets", new JsonNode(this.getParent().getSlots().isCanChangeBet()));
        //data.put("exit", new JsonNode(this.getParent().getSlots().isCanChangeRoom()));
        return data;
    }

    private HashMap<String, JsonNode> getBetState() {
        CSlots slots = this.getParent().getSlots();
        HashMap<String, JsonNode> config = new HashMap<String, JsonNode>();
        config.put("bonus_game", new JsonNode(slots.getBonusGameType().toString()));
        config.put("free_spins", new JsonNode(slots.getFreeSpins()));
        config.put("extra_spin", new JsonNode(slots.getExtraSpins() > 0));
        config.put("lines", new JsonNode(0));
        for (int i = 0; i < slots.getValidLines().length; i++) {
            if (slots.getLinesCount() == slots.getValidLines()[i]) {
                config.put("lines", new JsonNode(i));
                break;
            }
        }
        config.put("bet", new JsonNode(0));
        for (int i = 0; i < slots.getValidBets().length; i++) {
            if (slots.getBet() == slots.getValidBets()[i]) {
                config.put("bet", new JsonNode(i));
                break;
            }
        }
        return config;
    }

    // Getters & Setters

    public CUser getParent() {
        return this.parent;
    }

}
