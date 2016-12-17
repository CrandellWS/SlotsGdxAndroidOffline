package mobi.square.slots.game.slots.bonusgames;

import java.util.ArrayList;
import java.util.HashMap;

import mobi.square.slots.enums.BonusGame;
import mobi.square.slots.enums.Symbol;
import mobi.square.slots.game.slots.CSlots;
import mobi.square.slots.game.slots.v4.config.BookOfRa;
import mobi.square.slots.game.slots.v4.config.BookOfRa.SuperLine;
import mobi.square.slots.utils.json.JsonNode;
import mobi.square.slots.utils.utils;

public class CBookOfRaSlot extends CBonusGame {
    private static final long serialVersionUID = -2790284944763087757L;
    private static final Symbol[] bonus_symbols = {
            Symbol.N01,
            Symbol.N02,
            Symbol.N03,
            Symbol.N04,
            Symbol.N05,
            Symbol.N06,
            Symbol.N07,
            Symbol.N08
    };
    private BookOfRa config;
    private Symbol bonus_symbol = Symbol.N01;

    public CBookOfRaSlot(CSlots parent) {
        super(parent);
    }

    @Override
    public void proc(int index) {
        if (this.isOver() || index != 0) {
            return;
        }

        try {
            this.getParent().spin();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (!this.getParent().isFreeSpin()) {
            this.setOver(true);
        }
    }

    public void generate(BookOfRa config) {
        this.setBonusSymbol(bonus_symbols[utils.getRandomInt(bonus_symbols.length)]);
        // this.setBonusSymbol(Symbol.N01);
        this.config = config;
        this.config.setBonusMode(true);
        this.config.setBonusSymbol(this.getBonusSymbol());
        this.setOver(false);
    }

    @Override
    public void end() {
        this.config.setBonusMode(false);
    }

    @Override
    public BonusGame getType() {
        return BonusGame.BOOK_OF_RA_SLOT;
    }

    @Override
    public HashMap<String, JsonNode> getStateJson() {
        HashMap<String, JsonNode> json = super.getStateJson();
        json.put("bonus_symbol", new JsonNode(this.getBonusSymbol().toString()));
        json.put("spin", new JsonNode(this.getParent().getJSON(), false));
        json.put("effects", new JsonNode("[".concat(this.config.getEffects() != null ? this.config.getEffects() : "").concat("]"), false));
        json.put("required_update_symbols", new JsonNode(this.config.isRequiredUpdateSymbols()));
        if (this.config.isRequiredUpdateSymbols()) {
            Symbol[][] symbols2 = this.config.getSymbols2();
            StringBuilder builder = new StringBuilder();
            builder.append("[");
            for (int i = 0; i < symbols2.length; i++) {
                builder.append("[");
                for (int j = 0; j < symbols2[i].length; j++) {
                    builder.append("\"");
                    builder.append(symbols2[i][j].toString());
                    if (j >= symbols2[i].length - 1) {
                        builder.append("\"");
                    } else builder.append("\",");
                }
                if (i >= symbols2.length - 1) {
                    builder.append("]");
                } else builder.append("],");
            }
            builder.append("]");
            json.put("symbols2", new JsonNode(builder.toString(), false));

            // super_lines
            builder.setLength(0);
            ArrayList<SuperLine> super_lines = this.config.getSuperLines();
            builder.append("[");
            for (int i = 0; i < super_lines.size(); i++) {
                SuperLine line = super_lines.get(i);
                builder.append("{\"positions\":[");
                for (int j = 0; j < line.getPositions().length; j++) {
                    builder.append(line.getPositions()[j]);
                    if (j < line.getPositions().length - 1) {
                        builder.append(',');
                    }
                }
                builder.append("]");
                builder.append(",\"pay\":").append(line.getAward());
                builder.append(",\"rectangles\":[");
                for (int j = 0; j < line.getRectangles().length; j++) {
                    builder.append(line.getRectangles()[j]);
                    if (j < line.getRectangles().length - 1) {
                        builder.append(',');
                    }
                }
                builder.append("]");

                builder.append(i == super_lines.size() - 1 ? "}" : "},");
            }
            builder.append("]");
            json.put("super_lines", new JsonNode(builder.toString(), false));
        }
        // System.out.println(json.toString());
        return json;
    }

    public Symbol getBonusSymbol() {
        return bonus_symbol;
    }

    public void setBonusSymbol(Symbol symbol) {
        this.bonus_symbol = symbol;
    }

}
