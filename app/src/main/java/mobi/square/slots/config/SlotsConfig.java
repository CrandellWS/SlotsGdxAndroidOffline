package mobi.square.slots.config;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.util.HashMap;
import java.util.Map;

import mobi.square.slots.api.AndroidApi;
import mobi.square.slots.enums.Lines;
import mobi.square.slots.enums.SlotsType;
import mobi.square.slots.enums.SymbolType;

public class SlotsConfig {

    private final static Map<SlotsType, SlotsConfig> config;

    static {
        config = new HashMap<SlotsType, SlotsConfig>();
        initialize();
    }

    public final float[] left_padding;
    public final float[] lines_bounds;
    public final float[] lines_l_bounds;
    public final float[] lines_r_bounds;
    public SlotsType type;
    public String filename;
    public String machine_bg;
    public String pay_table;
    public String pay_table_bg;
    public float symbol_width;
    public float symbol_height;
    public float bottom_padding;
    public float vertical_padding;
    public float lines_left_padding;
    public float lines_right_padding;
    public float lines_stroke_width;
    public Lines lines_type;
    public float rolling_speed;
    public float back_speed;
    public float min_speed;
    public int symbols_count;
    public int revision;
    public SymbolType[] symbols;

    protected SlotsConfig() {
        this.left_padding = new float[5];
        this.symbols = new SymbolType[0];
        this.type = null;
        this.filename = null;
        this.machine_bg = null;
        this.pay_table = null;
        this.pay_table_bg = null;
        this.symbol_width = .0f;
        this.symbol_height = .0f;
        this.bottom_padding = .0f;
        this.vertical_padding = .0f;
        this.lines_left_padding = .0f;
        this.lines_right_padding = .0f;
        this.lines_stroke_width = .0f;
        this.lines_bounds = new float[4];
        this.lines_type = null;
        this.lines_l_bounds = new float[5];
        this.lines_r_bounds = new float[5];
        this.rolling_speed = .0f;
        this.back_speed = .0f;
        this.min_speed = .0f;
        this.symbols_count = 3;
        this.revision = 0;
    }

    protected static void initialize() {
        config.clear();
        AndroidApi.load();
        SlotsConfig c;
        for (SlotsType name : AndroidApi.TYPES) {
            c = loadConfig(name.toLowerString().concat(".conf"));
            if (c != null) config.put(c.type, c);
        }
    }

    private static SlotsConfig loadConfig(String filename) {
        FileHandle file = Gdx.files.internal("conf/".concat(filename));
        String content = file.readString();
        String[] lines = content.split("\n");
        SlotsConfig conf = new SlotsConfig();
        for (String line : lines) {
            line = line.replaceAll(" ", "");
            if (line.startsWith("//")) continue;
            String[] chunks = line.split("=");
            if (chunks.length != 2) continue;
            FieldName field = FieldName.valueOf(chunks[0].toUpperCase());
            String data = chunks[1].trim();
            switch (field) {
                case TYPE:
                    conf.type = SlotsType.valueOf(data.toUpperCase());
                    break;
                case REVISION:
                    conf.revision = Integer.parseInt(data);
                    break;
                case FILENAME:
                    conf.filename = data;
                    break;
                case MACHINE_BG:
                    conf.machine_bg = data;
                    break;
                case PAY_TABLE:
                    conf.pay_table = data;
                    break;
                case PAY_TABLE_BG:
                    conf.pay_table_bg = data;
                    break;
                case SYMBOL_WIDTH:
                    conf.symbol_width = Float.parseFloat(data);
                    break;
                case SYMBOL_HEIGHT:
                    conf.symbol_height = Float.parseFloat(data);
                    break;
                case BOTTOM_PADDING:
                    conf.bottom_padding = Float.parseFloat(data);
                    break;
                case VERTICAL_PADDING:
                    conf.vertical_padding = Float.parseFloat(data);
                    break;
                case LEFT_PADDING_0:
                    conf.left_padding[0] = Float.parseFloat(data);
                    break;
                case LEFT_PADDING_1:
                    conf.left_padding[1] = Float.parseFloat(data);
                    break;
                case LEFT_PADDING_2:
                    conf.left_padding[2] = Float.parseFloat(data);
                    break;
                case LEFT_PADDING_3:
                    conf.left_padding[3] = Float.parseFloat(data);
                    break;
                case LEFT_PADDING_4:
                    conf.left_padding[4] = Float.parseFloat(data);
                    break;
                case LINES_LEFT_PADDING:
                    conf.lines_left_padding = Float.parseFloat(data);
                    break;
                case LINES_RIGHT_PADDING:
                    conf.lines_right_padding = Float.parseFloat(data);
                    break;
                case LINES_STROKE_WIDTH:
                    conf.lines_stroke_width = Float.parseFloat(data);
                    break;
                case LINES_X:
                    conf.lines_bounds[0] = Float.parseFloat(data);
                    break;
                case LINES_Y:
                    conf.lines_bounds[1] = Float.parseFloat(data);
                    break;
                case LINES_W:
                    conf.lines_bounds[2] = Float.parseFloat(data);
                    break;
                case LINES_H:
                    conf.lines_bounds[3] = Float.parseFloat(data);
                    break;
                case LINES_C: {
                    int n = Integer.parseInt(data);
                    conf.lines_type =
                            (n == 9) ? Lines.LINES_3X_09_V1 :
                                    (n == 20) ? Lines.LINES_3X_20_V1 :
                                            (n == 50) ? Lines.LINES_4X_50_V1 :
                                                    (n == 99) ? Lines.LINES_3X_99_V1 :
                                                            null;
                }
                case LINES_L_X:
                    conf.lines_l_bounds[0] = Float.parseFloat(data);
                    break;
                case LINES_L_Y:
                    conf.lines_l_bounds[1] = Float.parseFloat(data);
                    break;
                case LINES_L_W:
                    conf.lines_l_bounds[2] = Float.parseFloat(data);
                    break;
                case LINES_L_H:
                    conf.lines_l_bounds[3] = Float.parseFloat(data);
                    break;
                case LINES_L_O:
                    conf.lines_l_bounds[4] = Float.parseFloat(data);
                    break;
                case LINES_R_X:
                    conf.lines_r_bounds[0] = Float.parseFloat(data);
                    break;
                case LINES_R_Y:
                    conf.lines_r_bounds[1] = Float.parseFloat(data);
                    break;
                case LINES_R_W:
                    conf.lines_r_bounds[2] = Float.parseFloat(data);
                    break;
                case LINES_R_H:
                    conf.lines_r_bounds[3] = Float.parseFloat(data);
                    break;
                case LINES_R_O:
                    conf.lines_r_bounds[4] = Float.parseFloat(data);
                    break;
                case ROLLING_SPEED:
                    conf.rolling_speed = Float.parseFloat(data);
                    break;
                case BACK_SPEED:
                    conf.back_speed = Float.parseFloat(data);
                    break;
                case MIN_SPEED:
                    conf.min_speed = Float.parseFloat(data);
                    break;
                case SYMBOLS_COUNT:
                    conf.symbols_count = Integer.parseInt(data);
                    break;
                case SYMBOLS: {
                    String[] symbols = data.split(",");
                    if (symbols.length < 3) continue;
                    conf.symbols = new SymbolType[symbols.length];
                    for (int i = 0; i < symbols.length; i++) {
                        conf.symbols[i] = SymbolType.valueOf(symbols[i].trim().toUpperCase());
                    }
                }
                break;
                default:
                    break;
            }
        }
        return conf;
    }

    public static int getSlotsRevision(SlotsType type) {
        SlotsConfig conf = config.get(type);
        return conf != null ? conf.revision : 0;
    }

    public static SlotsConfig get(SlotsType type) {
        return type != null ? config.get(type) : null;
    }

    public static SlotsConfig reload(SlotsType type) {
        if (type == null) return null;
        SlotsConfig conf = loadConfig(type.toLowerString().concat(".conf"));
        config.put(type, conf);
        return conf;
    }

    public String getLinesAtlas() {
        if (this.lines_type == null) return null;
        switch (this.lines_type) {
            case LINES_3X_09_V1:
                return "atlas/Lines9.pack";
            case LINES_3X_20_V1:
                return "atlas/Lines20.pack";
            default:
                return null;
        }
    }

    private enum FieldName {
        TYPE,
        REVISION,
        FILENAME,
        MACHINE_BG,
        PAY_TABLE,
        PAY_TABLE_BG,
        SYMBOL_WIDTH,
        SYMBOL_HEIGHT,
        BOTTOM_PADDING,
        VERTICAL_PADDING,
        LEFT_PADDING_0,
        LEFT_PADDING_1,
        LEFT_PADDING_2,
        LEFT_PADDING_3,
        LEFT_PADDING_4,
        LINES_LEFT_PADDING,
        LINES_RIGHT_PADDING,
        LINES_STROKE_WIDTH,
        LINES_X,
        LINES_Y,
        LINES_W,
        LINES_H,
        LINES_C,
        LINES_L_X,
        LINES_L_Y,
        LINES_L_W,
        LINES_L_H,
        LINES_L_O,
        LINES_R_X,
        LINES_R_Y,
        LINES_R_W,
        LINES_R_H,
        LINES_R_O,
        ROLLING_SPEED,
        BACK_SPEED,
        MIN_SPEED,
        SYMBOLS_COUNT,
        SYMBOLS
    }

}
