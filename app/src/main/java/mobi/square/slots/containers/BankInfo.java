package mobi.square.slots.containers;

public class BankInfo {

    private String id = null;
    private int price = 0;
    private int discount = 0;
    private int gold = 0;
    private int bonus_gold;
    private boolean recommended;
    private String price_string = null;

    // Public Methods

    public BankInfo copy() {
        BankInfo copy = new BankInfo();
        copy.id = this.id;
        copy.price = this.price;
        copy.discount = this.discount;
        copy.gold = this.gold;
        copy.bonus_gold = this.bonus_gold;
        copy.recommended = this.recommended;
        copy.price_string = this.price_string;
        return copy;
    }

    public boolean isPriced() {
        return this.price_string != null;
    }

    // Getters & Setters

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPrice() {
        return this.price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getDiscount() {
        return this.discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public int getGold() {
        return this.gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getBonusGold() {
        return this.bonus_gold;
    }

    public void setBonusGold(int bonus_gold) {
        this.bonus_gold = bonus_gold;
    }

    public boolean isRecommended() {
        return this.recommended;
    }

    public void setRecommended(boolean recommended) {
        this.recommended = recommended;
    }

    public String getPriceString() {
        return this.price_string;
    }

    public void setPriceString(String price_string) {
        this.price_string = price_string;
    }

}
