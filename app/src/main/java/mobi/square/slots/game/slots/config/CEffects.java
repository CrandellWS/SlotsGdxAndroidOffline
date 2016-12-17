package mobi.square.slots.game.slots.config;

import mobi.square.slots.enums.Symbol;
import mobi.square.slots.game.slots.CSlots;

public final class CEffects {
    private CEffects() {
    }

    public static String getMoveXEffect(int row, int start_reel, int stop_reel, Symbol symbol, String draw) {
        return getMoveXEffect(row, start_reel, stop_reel, symbol, draw, 2);
    }

    public static String getMoveXEffect(int row, int start_reel, int stop_reel, Symbol symbol, String draw, int speed) {
        StringBuilder effect = new StringBuilder();
        effect.append("{");
        effect.append("\"type\":\"move_x\",");
        effect.append("\"draw\":\"").append(draw).append("\",");
        effect.append("\"symbol\":\"").append(symbol.toString()).append("\",");
        effect.append("\"start_reel\":").append(start_reel).append(",");
        effect.append("\"stop_reel\":").append(stop_reel).append(",");
        effect.append("\"speed\":").append(start_reel > stop_reel ? -speed : speed).append(",");
        effect.append("\"row\":").append(row);
        effect.append("}");
        return effect.toString();
    }

    public static String getMoveYEffect(int start_row, int stop_row, int reel, Symbol symbol, String draw) {
        return getMoveYEffect(start_row, stop_row, reel, symbol, draw, 5);
    }

    public static String getMoveYEffect(int start_row, int stop_row, int reel, Symbol symbol, String draw, int speed) {
        StringBuilder effect = new StringBuilder();
        effect.append("{");
        effect.append("\"type\":\"move_y\",");
        effect.append("\"draw\":\"").append(draw).append("\",");
        effect.append("\"symbol\":\"").append(symbol.toString()).append("\",");
        effect.append("\"start_row\":").append(start_row).append(",");
        effect.append("\"stop_row\":").append(stop_row).append(",");
        effect.append("\"speed\":").append(start_row > stop_row ? -speed : speed).append(",");
        effect.append("\"reel\":").append(reel);
        effect.append("}");
        return effect.toString();
    }

    public static String getFreezeEffect(int row, int reel, Symbol symbol, String draw) {
        return getFreezeEffect(row, reel, symbol, draw, false);
    }

    public static String getFreezeEffect(int row, int reel, Symbol symbol, String draw, boolean special) {
        StringBuilder effect = new StringBuilder();
        effect.append("{");
        effect.append("\"type\":\"freeze\",");
        effect.append("\"draw\":\"").append(draw).append("\",");
        effect.append("\"symbol\":\"").append(symbol.toString()).append("\",");
        effect.append("\"row\":").append(row).append(",");
        effect.append("\"reel\":").append(reel).append(",");
        effect.append("\"special\":").append(special);
        effect.append("}");
        return effect.toString();
    }

    public static String getWildBlurEffect(int reel) {
        StringBuilder effect = new StringBuilder();
        effect.append("{");
        effect.append("\"type\":\"blur\",");
        effect.append("\"draw\":\"gemstones\",");
        effect.append("\"symbol\":\"WILD\",");
        effect.append("\"reel\":").append(reel);
        effect.append("}");
        return effect.toString();
    }

    public static String getMultiWildVertical(CSlots slots, int i) {
        StringBuilder json = new StringBuilder();
        int wilds = 0;
        for (int j = 0; j < 3; j++) {
            if (slots.getPreviousSymbols()[i][j].getSymbol() == Symbol.WILD) wilds++;
        }
        if (wilds > 0 && wilds < 3) {
            for (int j = 0; j < 3; j++) {
                slots.getCurrentSymbols()[i][j].setSymbol(Symbol.WILD);
            }
        }
        wilds = 0;
        for (int j = 0; j < 3; j++) {
            if (slots.getCurrentSymbols()[i][j].getSymbol() == Symbol.WILD) wilds++;
        }
        if (wilds > 0 && wilds < 3) {
            if (slots.getCurrentSymbols()[i][0].getSymbol() == Symbol.WILD) {
                if (slots.getCurrentSymbols()[i][2].getSymbol() != Symbol.WILD) {
                    if (!json.toString().isEmpty()) json.append(",");
                    json.append(CEffects.getMoveYEffect(0, 2, i, Symbol.WILD, "none"));
                }
                if (slots.getCurrentSymbols()[i][1].getSymbol() != Symbol.WILD) {
                    if (!json.toString().isEmpty()) json.append(",");
                    json.append(CEffects.getMoveYEffect(0, 1, i, Symbol.WILD, "none"));
                }
                if (!json.toString().isEmpty()) json.append(",");
                json.append(CEffects.getFreezeEffect(0, i, Symbol.WILD, "none"));
                //json.append(",").append(CEffects.getWildBlurEffect(i));
            }
            if (slots.getCurrentSymbols()[i][1].getSymbol() == Symbol.WILD) {
                if (slots.getCurrentSymbols()[i][0].getSymbol() != Symbol.WILD) {
                    if (!json.toString().isEmpty()) json.append(",");
                    json.append(CEffects.getMoveYEffect(1, 0, i, Symbol.WILD, "none"));
                }
                if (slots.getCurrentSymbols()[i][2].getSymbol() != Symbol.WILD) {
                    if (!json.toString().isEmpty()) json.append(",");
                    json.append(CEffects.getMoveYEffect(1, 2, i, Symbol.WILD, "none"));
                }
                if (!json.toString().isEmpty()) json.append(",");
                json.append(CEffects.getFreezeEffect(1, i, Symbol.WILD, "none"));
                //json.append(",").append(CEffects.getWildBlurEffect(i));
            }
            if (slots.getCurrentSymbols()[i][2].getSymbol() == Symbol.WILD) {
                if (slots.getCurrentSymbols()[i][0].getSymbol() != Symbol.WILD) {
                    if (!json.toString().isEmpty()) json.append(",");
                    json.append(CEffects.getMoveYEffect(2, 0, i, Symbol.WILD, "none"));
                }
                if (slots.getCurrentSymbols()[i][1].getSymbol() != Symbol.WILD) {
                    if (!json.toString().isEmpty()) json.append(",");
                    json.append(CEffects.getMoveYEffect(2, 1, i, Symbol.WILD, "none"));
                }
                if (!json.toString().isEmpty()) json.append(",");
                json.append(CEffects.getFreezeEffect(2, i, Symbol.WILD, "none"));
                //json.append(",").append(CEffects.getWildBlurEffect(i));
            }
        }
        return json.toString();
    }

    public static void performMultiWildVertical(CSlots slots, int i) {
        int wilds = 0;
        for (int j = 0; j < 3; j++) {
            if (slots.getPreviousSymbols()[i][j].getSymbol() == Symbol.WILD) wilds++;
        }
        if (wilds > 0 && wilds < 3) {
            for (int j = 0; j < 3; j++) {
                slots.getCurrentSymbols()[i][j].setSymbol(Symbol.WILD);
            }
        }
    }

    public static String checkMultiWildVertical(CSlots slots, int i) {
        StringBuilder json = new StringBuilder();
        int wilds = 0;
        for (int j = 0; j < 3; j++) {
            if (slots.getPreviousSymbols()[i][j].getSymbol() == Symbol.WILD) wilds++;
        }
        wilds = 0;
        for (int j = 0; j < 3; j++) {
            if (slots.getCurrentSymbols()[i][j].getSymbol() == Symbol.WILD) wilds++;
        }
        if (wilds > 0 && wilds < 3) {
            if (slots.getCurrentSymbols()[i][0].getSymbol() == Symbol.WILD) {
                if (slots.getCurrentSymbols()[i][2].getSymbol() != Symbol.WILD) {
                    if (!json.toString().isEmpty()) json.append(",");
                    json.append(CEffects.getMoveYEffect(0, 2, i, Symbol.WILD, "none"));
                }
                if (slots.getCurrentSymbols()[i][1].getSymbol() != Symbol.WILD) {
                    if (!json.toString().isEmpty()) json.append(",");
                    json.append(CEffects.getMoveYEffect(0, 1, i, Symbol.WILD, "none"));
                }
                if (!json.toString().isEmpty()) json.append(",");
                json.append(CEffects.getFreezeEffect(0, i, Symbol.WILD, "none"));
                //json.append(",").append(CEffects.getWildBlurEffect(i));
            }
            if (slots.getCurrentSymbols()[i][1].getSymbol() == Symbol.WILD) {
                if (slots.getCurrentSymbols()[i][0].getSymbol() != Symbol.WILD) {
                    if (!json.toString().isEmpty()) json.append(",");
                    json.append(CEffects.getMoveYEffect(1, 0, i, Symbol.WILD, "none"));
                }
                if (slots.getCurrentSymbols()[i][2].getSymbol() != Symbol.WILD) {
                    if (!json.toString().isEmpty()) json.append(",");
                    json.append(CEffects.getMoveYEffect(1, 2, i, Symbol.WILD, "none"));
                }
                if (!json.toString().isEmpty()) json.append(",");
                json.append(CEffects.getFreezeEffect(1, i, Symbol.WILD, "none"));
                //json.append(",").append(CEffects.getWildBlurEffect(i));
            }
            if (slots.getCurrentSymbols()[i][2].getSymbol() == Symbol.WILD) {
                if (slots.getCurrentSymbols()[i][0].getSymbol() != Symbol.WILD) {
                    if (!json.toString().isEmpty()) json.append(",");
                    json.append(CEffects.getMoveYEffect(2, 0, i, Symbol.WILD, "none"));
                }
                if (slots.getCurrentSymbols()[i][1].getSymbol() != Symbol.WILD) {
                    if (!json.toString().isEmpty()) json.append(",");
                    json.append(CEffects.getMoveYEffect(2, 1, i, Symbol.WILD, "none"));
                }
                if (!json.toString().isEmpty()) json.append(",");
                json.append(CEffects.getFreezeEffect(2, i, Symbol.WILD, "none"));
                //json.append(",").append(CEffects.getWildBlurEffect(i));
            }
        }
        return json.toString();
    }

    public static String checkMultiSymbolVertical(CSlots slots, Symbol symbol, int i) {
        StringBuilder json = new StringBuilder();
        int wilds = 0;
        for (int j = 0; j < 3; j++) {
            if (slots.getPreviousSymbols()[i][j].getSymbol() == symbol) wilds++;
        }
        wilds = 0;
        for (int j = 0; j < 3; j++) {
            if (slots.getCurrentSymbols()[i][j].getSymbol() == symbol) wilds++;
        }
        if (wilds > 0 && wilds < 3) {
            if (slots.getCurrentSymbols()[i][0].getSymbol() == symbol) {
                if (slots.getCurrentSymbols()[i][2].getSymbol() != symbol) {
                    if (!json.toString().isEmpty()) json.append(",");
                    json.append(CEffects.getMoveYEffect(0, 2, i, symbol, "none"));
                }
                if (slots.getCurrentSymbols()[i][1].getSymbol() != symbol) {
                    if (!json.toString().isEmpty()) json.append(",");
                    json.append(CEffects.getMoveYEffect(0, 1, i, symbol, "none"));
                }
                if (!json.toString().isEmpty()) json.append(",");
                json.append(CEffects.getFreezeEffect(0, i, symbol, "none"));
                //json.append(",").append(CEffects.getWildBlurEffect(i));
            }
            if (slots.getCurrentSymbols()[i][1].getSymbol() == symbol) {
                if (slots.getCurrentSymbols()[i][0].getSymbol() != symbol) {
                    if (!json.toString().isEmpty()) json.append(",");
                    json.append(CEffects.getMoveYEffect(1, 0, i, symbol, "none"));
                }
                if (slots.getCurrentSymbols()[i][2].getSymbol() != symbol) {
                    if (!json.toString().isEmpty()) json.append(",");
                    json.append(CEffects.getMoveYEffect(1, 2, i, symbol, "none"));
                }
                if (!json.toString().isEmpty()) json.append(",");
                json.append(CEffects.getFreezeEffect(1, i, symbol, "none"));
                //json.append(",").append(CEffects.getWildBlurEffect(i));
            }
            if (slots.getCurrentSymbols()[i][2].getSymbol() == symbol) {
                if (slots.getCurrentSymbols()[i][0].getSymbol() != symbol) {
                    if (!json.toString().isEmpty()) json.append(",");
                    json.append(CEffects.getMoveYEffect(2, 0, i, symbol, "none"));
                }
                if (slots.getCurrentSymbols()[i][1].getSymbol() != symbol) {
                    if (!json.toString().isEmpty()) json.append(",");
                    json.append(CEffects.getMoveYEffect(2, 1, i, symbol, "none"));
                }
                if (!json.toString().isEmpty()) json.append(",");
                json.append(CEffects.getFreezeEffect(2, i, symbol, "none"));
                //json.append(",").append(CEffects.getWildBlurEffect(i));
            }
        }
        return json.toString();
    }

    public static String getBonusLineEffect(int[] line) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"type\":\"bonus_line\"");
        json.append(",\"draw\":\"none\"");
        json.append(",\"line\":[");
        for (int i = 0; i < line.length - 1; i++) {
            json.append(line[i]);
            json.append(",");
        }
        json.append(line[line.length - 1]);
        json.append("]");
        json.append("}");
        return json.toString();
    }

    public static String getReelImage(String name, int reel) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"type\":\"reel_image\"");
        json.append(",\"after_stop\": true");
        json.append(",\"draw\":\"none\"");
        json.append(",\"name\":").append("\"").append(name).append("\"");
        json.append(",\"reel\":").append(reel);
        json.append("}");
        return json.toString();
    }
}
