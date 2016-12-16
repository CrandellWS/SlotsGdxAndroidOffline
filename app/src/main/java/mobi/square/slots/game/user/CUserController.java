package mobi.square.slots.game.user;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import mobi.square.slots.api.Connection;
import mobi.square.slots.config.ApiConfig;
import mobi.square.slots.utils.utils;

public class CUserController implements Serializable {

    private static final long serialVersionUID = -988812851761531070L;

    private transient CUser parent = null;

    public CUserController(CUser parent) {
        this.parent = parent;
    }

    // Database Methods

    /**
     * Создать нового пользователя.
     *
     * @param uid - ID пользователя;
     * @return
     */
    public static CUser newUser(String uid) {
        CUser user = new CUser(uid);
        user.setMoney(5000);
        user.getController().load();
        return user;
    }

    /**
     * Сохранить пользователя
     */
    public void save() {
        CUser parent = this.getParent();
        Map<String, String> strings = new HashMap<String, String>();
        Map<String, Integer> integers = new HashMap<String, Integer>();
        integers.put("money", parent.getMoney());
        integers.put("hourly_bonus_time", parent.getHourlyBonusTime());
        integers.put("super_bonus_count", parent.getSuperBonusCount());
        integers.put("roulette_time", parent.getRouletteTime());
        integers.put("roulette_multipler", parent.getRouletteMultipler());
        integers.put("roulette_position", parent.getRoulettePosition());
        integers.put("roulette_bonus_spins", parent.getRouletteBonusSpins());
        this.getParent().getSlots().pack(strings, integers);
        Connection.getWrapper().writeData(strings, integers);
    }

    /**
     * Загрузить пользователя
     */
    public void load() {
        CUser parent = this.getParent();
        Map<String, String> strings = new HashMap<String, String>();
        Map<String, Integer> integers = new HashMap<String, Integer>();
        integers.put("money", parent.getMoney());
        integers.put("hourly_bonus_time", parent.getHourlyBonusTime());
        integers.put("super_bonus_count", parent.getSuperBonusCount());
        integers.put("roulette_time", parent.getRouletteTime());
        integers.put("roulette_multipler", parent.getRouletteMultipler());
        integers.put("roulette_position", parent.getRoulettePosition());
        integers.put("roulette_bonus_spins", parent.getRouletteBonusSpins());
        parent.getSlots().pack(strings, integers);
        Connection.getWrapper().readData(strings, integers);
        parent.setMoney(integers.get("money"));
        parent.setHourlyBonusTime(integers.get("hourly_bonus_time"));
        parent.setSuperBonusCount(integers.get("super_bonus_count"));
        parent.setRouletteTime(integers.get("roulette_time"));
        parent.setRouletteMultipler(integers.get("roulette_multipler"));
        parent.setRoulettePosition(integers.get("roulette_position"));
        parent.setRouletteBonusSpins(integers.get("roulette_bonus_spins"));
        parent.getSlots().unpack(strings, integers);
    }

    // Public Methods

    /**
     * Начисление денег игроку
     *
     * @param money - количество
     */
    public void addMoney(int money) {
        int user_money = this.getParent().getMoney();
        user_money += money;
        this.getParent().setMoney(user_money);
    }

    /**
     * Списать деньги со счёта игрока
     *
     * @param money - количетсво
     * @return true/false
     */
    public boolean withdrawMoney(int money) {
        int total = this.getParent().getMoney();
        if (total < money) return false;
        total -= money;
        this.getParent().setMoney(total);
        return true;
    }

    public boolean isHourlyBonusAvailable() {
        return utils.getTimestamp() >= this.getParent().getHourlyBonusTime() + ApiConfig.HOURLY_BONUS_TIME;
    }

    public int getHourlyBonusAmount() {
        return ApiConfig.HOURLY_MONEY_BONUS;
    }

    public int takeHourlyBonus() {
        if (this.isHourlyBonusAvailable()) {
            int money = this.getHourlyBonusAmount();
            int count = this.getParent().getSuperBonusCount();
            count++;
            if (count >= ApiConfig.SUPER_BONUS_MAX) count = ApiConfig.SUPER_BONUS_MAX;
            this.addMoney(money);
            this.getParent().setHourlyBonusTime(utils.getTimestamp());
            this.getParent().setSuperBonusCount(count);
            this.getParent().getController().save();
            return money;
        }
        return 0;
    }

    public boolean isSuperBonusAvailable() {
        return this.getParent().getSuperBonusCount() >= ApiConfig.SUPER_BONUS_MAX;
    }

    public int takeSuperBonus() {
        if (this.isSuperBonusAvailable()) {
            int money = ApiConfig.SUPER_BONUS[(int) (Math.random() * ApiConfig.SUPER_BONUS.length)];
            this.addMoney(money);
            this.getParent().setSuperBonusCount(0);
            this.getParent().getController().save();
            return money;
        }
        return 0;
    }

    public boolean isRouletteAvailable() {
        return utils.getTimestamp() >= this.getParent().getRouletteTime() + ApiConfig.RULETTE_TIME;
    }

    public int rotateRulette() {
        if (!this.isRouletteAvailable() && this.getParent().getRouletteBonusSpins() <= 0) return 0;
        int mul = this.getParent().getRouletteMultipler();
        int next_mul = mul + 1;
        if (next_mul >= ApiConfig.RULETTE_MAX_MULTIPLER)
            next_mul = ApiConfig.RULETTE_MAX_MULTIPLER;
        int position = utils.getRandom(ApiConfig.RULETTE_WINS.length - 1);
        int win = ApiConfig.RULETTE_WINS[position];
        win *= mul;
        this.addMoney(win);
        this.getParent().setRouletteMultipler(next_mul);
        if (this.getParent().getRouletteBonusSpins() > 0) {
            this.getParent().setRouletteBonusSpins(this.getParent().getRouletteBonusSpins() - 1);
        } else {
            this.getParent().setRouletteTime(utils.getTimestamp());
        }
        this.getParent().setRoulettePosition(position);
        return win;
    }

    // Private Methods

    // Getters & Setters

    public CUser getParent() {
        return this.parent;
    }

    public void setParent(CUser parent) {
        this.parent = parent;
    }

}
