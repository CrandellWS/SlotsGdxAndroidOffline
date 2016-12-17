package mobi.square.slots.game.bank;

import java.util.ArrayList;
import java.util.List;

public class Bank {

    private static List<CPurchase> list;

    static {
        list = new ArrayList<CPurchase>();
        CPurchase unit;

        unit = new CPurchase();
        unit.setType("5");
        unit.setAmount(1000);
        unit.setGold(8000);
        unit.setBonusGold(2000);
        unit.setDiscount(0);
        unit.setRecommended(false);
        list.add(unit);

        unit = new CPurchase();
        unit.setType("4");
        unit.setAmount(500);
        unit.setGold(4000);
        unit.setBonusGold(1000);
        unit.setDiscount(0);
        unit.setRecommended(false);
        list.add(unit);

        unit = new CPurchase();
        unit.setType("3");
        unit.setAmount(250);
        unit.setGold(2000);
        unit.setBonusGold(500);
        unit.setDiscount(0);
        unit.setRecommended(false);
        list.add(unit);

        unit = new CPurchase();
        unit.setType("2");
        unit.setAmount(50);
        unit.setGold(400);
        unit.setBonusGold(100);
        unit.setDiscount(0);
        unit.setRecommended(false);
        list.add(unit);

        unit = new CPurchase();
        unit.setType("1");
        unit.setAmount(10);
        unit.setGold(10);
        unit.setBonusGold(0);
        unit.setDiscount(0);
        unit.setRecommended(false);
        list.add(unit);
    }

    public static List<CPurchase> getGoldList() {
        return list;
    }

    public static void setGoldList(List<CPurchase> newList) {
        list.clear();
        list = newList;
    }

    public static CPurchase getPurchase(String type) {
        if (type == null) return null;
        for (CPurchase purchase : list) {
            if (type.equals(purchase.getType())) {
                return purchase;
            }
        }
        return null;
    }

}
