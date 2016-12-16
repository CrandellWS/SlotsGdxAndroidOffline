package mobi.square.slots.b.bookofra;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import mobi.square.slots.api.Connection;
import mobi.square.slots.classes.Machine;
import mobi.square.slots.classes.Reel;
import mobi.square.slots.config.AppConfig;
import mobi.square.slots.config.SoundList;
import mobi.square.slots.containers.LinesExt;
import mobi.square.slots.containers.LinesExt.LineExt;
import mobi.square.slots.containers.LinesExt.LineExt.Multiplier;
import mobi.square.slots.enums.BonusGame;
import mobi.square.slots.enums.RiskType;
import mobi.square.slots.enums.SlotsType;
import mobi.square.slots.enums.SymbolType;
import mobi.square.slots.error.StringCodeException;
import mobi.square.slots.handlers.AsyncJsonHandler;
import mobi.square.slots.handlers.LoadingHandler;
import mobi.square.slots.listeners.LineShowedListener;
import mobi.square.slots.listeners.LinesShowedListener;
import mobi.square.slots.listeners.MachineStoppedListener;
import mobi.square.slots.listeners.ReelStoppedListener;
import mobi.square.slots.logger.Log;
import mobi.square.slots.screens.SBasic;
import mobi.square.slots.screens.SRiskColor;
import mobi.square.slots.screens.SRiskGreater;
import mobi.square.slots.stages.Header;
import mobi.square.slots.ui.GameFooter;
import mobi.square.slots.ui.GameFooter.BetWindow.BetClickHandler;
import mobi.square.slots.ui.bonus.BookOfRaBonusWindow;
import mobi.square.slots.ui.bonus.BookOfRaBonusWindow.BookAnimationOver;
import mobi.square.slots.ui.payout.TBasic;
import mobi.square.slots.utils.json.JsonArray;
import mobi.square.slots.utils.json.JsonObject;

public class BookOfRa extends Header {

    private GameFooter footer;
    private Machine machine;
    private TBasic pay_table;
    private BookOfRaBonusWindow message;
    private boolean spin_get;

    private Sound sound_bet;
    private Sound sound_lines;
    private Sound sound_bonus;
    private Sound sound_money;

    private LinesExt lines_ra;
    private SymbolType[][] symbols_ra;
    private SymbolType bonus_symbol;
    private boolean bonus_game_active;

    private JsonArray effects;
    private AsyncJsonHandler spin_handler = new AsyncJsonHandler() {
        @Override
        public void onCompleted(String json) {
            try {
                effects = Connection.getInstance().responseSpin(json, machine);
                spin_get = true;
                try {
                    JsonObject js = new JsonObject(json);
                    if (!js.isNull("bonus_symbol")) {
                        bonus_symbol = SymbolType.valueOf(js.getString("bonus_symbol"));
                    } else bonus_symbol = null;
                } catch (StringCodeException e) {
                    Log.log(e);
                }
                updateComboBoxes();
            } catch (StringCodeException e) {
                Log.log("Spin handler caused error.", e);
                machine.stopError();
            }
        }
    };
    private AsyncJsonHandler conf_handler = new AsyncJsonHandler() {
        @Override
        public void onCompleted(String json) {
            try {
                Connection.getInstance().responseChangeConfig(json, machine);
            } catch (StringCodeException e) {
                Log.log("Conf handler caused error.", e);
            }
            machine.setActiveLines(Connection.getInstance().getCurrentLinesCount());
            updateComboBoxes();
            updateButtons();
        }
    };
    private MachineStoppedListener stopped_handler = new MachineStoppedListener() {
        @Override
        public void stopped(Machine machine) {
            if (bonus_game_active) return;
            updateNumbers();
            if (!footer.isAutoMode()) {
                updateButtons();
            }
            if (updateBonusGame()) {
                message.show(bonus_symbol, Connection.getInstance().getFreeSpins());
                if (Connection.getInstance().isSoundOn() && sound_bonus != null) {
                    sound_bonus.play();
                }
            } else {
                message.setVisible(false);
                footer.setVisible(true);
            }
        }
    };
    private ReelStoppedListener reel_stopped_handler = new ReelStoppedListener() {
        @Override
        public void stopped(Reel reel) {
            // Nothing need to do
        }
    };
    private LineShowedListener line_showed_handler = new LineShowedListener() {
        @Override
        public void line_showed(Machine machine, int line, int award) {
            footer.addAwardMoney(award);
        }
    };
    private LinesShowedListener lines_showed_handler = new LinesShowedListener() {
        @Override
        public void lines_showed(Machine machine) {
            if (bonus_game_active) return;
            footer.setAwardMoney(Connection.getInstance().getAward(), false);
            if (footer.isAutoMode()) {
                if (!Connection.getInstance().isCanSpin()) {
                    footer.setAutoMode(false);
                } else spin();
            }
        }
    };
    private AsyncJsonHandler spin_handler_bonus = new AsyncJsonHandler() {
        @Override
        public void onCompleted(String json) {
            try {
                JsonObject spin = updateState(Connection.getInstance().responseBonusState(json));
                machine.stop(parseSymbols(spin.getJsonArray("symbols")), parseLines(spin.getJsonArray("lines")).getLines(), null);
                spin_get = true;
            } catch (StringCodeException e) {
                Log.log(e);
            }
        }
    };
    private BookAnimationOver book_handler = new BookAnimationOver() {
        @Override
        public void bookAnimationOver() {
            spinBonus();
        }
    };
    private LinesShowedListener lines_showed_handler_bonus = new LinesShowedListener() {
        @Override
        public void lines_showed(Machine machine) {
            if (!bonus_game_active) return;
            if (symbols_ra != null) {
                machine.clearLines();
                machine.setSymbolsBookOfRa(symbols_ra, bonus_symbol);
                machine.addLines(lines_ra.getLines());
                symbols_ra = null;
                lines_ra = null;
            } else {
                if (!bonus_game_active) {
                    showFooter(true);
                } else spinBonus();
            }
        }
    };
    private MachineStoppedListener stopped_handler_bonus = new MachineStoppedListener() {
        @Override
        public void stopped(Machine machine) {
            if (!bonus_game_active) return;
            updateNumbers();
        }
    };

    public BookOfRa(SBasic parent) {
        super(parent);
        this.footer = null;
        this.machine = null;
        this.effects = null;
        this.pay_table = null;
        this.sound_money = null;
        this.lines_ra = null;
        this.symbols_ra = null;
        this.bonus_symbol = null;
        this.bonus_game_active = false;
        this.spin_get = false;
    }

    @Override
    public void load(LoadingHandler handler) {
        super.load(false);
        super.addLowActors();
        Connection.setLastScreen(SlotsType.BOOK_OF_RA);
        // Sounds
        this.sound_bet = Connection.getManager().get("BookOfRa/change_bet.ogg", Sound.class);
        this.sound_lines = Connection.getManager().get("BookOfRa/change_lines.ogg", Sound.class);
        this.sound_bonus = Connection.getManager().get("BookOfRa/bonus_game.ogg", Sound.class);
        this.sound_money = Connection.getManager().get(SoundList.MONEY_COMMON, Sound.class);
        // Footer
        TextureAtlas atlas = Connection.getManager().get("atlas/Footer.pack", TextureAtlas.class);
        TextureAtlas a2 = Connection.getManager().get("atlas/Autospin.pack", TextureAtlas.class);
        this.footer = GameFooter.newInstance(this, atlas, a2);
        super.addActor(this.footer);
        // Machine
        this.machine = MBookOfRa.newInstance(this);
        this.machine.setBounds(0f, 0f, AppConfig.VIEWPORT_WIDTH, AppConfig.VIEWPORT_HEIGHT - this.header.getHeight());
        this.machine.addListener(this.line_showed_handler);
        this.machine.addListener(this.lines_showed_handler);
        this.machine.addListener(this.lines_showed_handler_bonus);
        this.machine.addListener(this.reel_stopped_handler);
        this.machine.addListener(this.stopped_handler_bonus);
        this.machine.addListener(this.stopped_handler);
        this.machine.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        // Message window
        this.message = BookOfRaBonusWindow.newInstance(this.machine.atlas);
        this.message.setAnimationHandler(this.book_handler);
        this.message.setVisible(false);
        super.addActor(this.message);
        super.addHighActors();
        // Pay table
        Texture pay_background = Connection.getManager().get(this.machine.config.pay_table_bg, Texture.class);
        TextureAtlas pay_atlas = Connection.getManager().get(this.machine.config.pay_table, TextureAtlas.class);
        TextureAtlas windows_atlas = Connection.getManager().get("atlas/Windows.pack", TextureAtlas.class);
        this.pay_table = TBookOfRa.newInstance(pay_background, pay_atlas, this.machine.atlas, windows_atlas);
        super.addActor(this.pay_table);
        // Spin button
        this.footer.setSpinClickedTask(new Runnable() {
            @Override
            public void run() {
                spin();
            }
        });
        this.footer.setAutoClickedTask(new Runnable() {
            @Override
            public void run() {
                disableBetButtons();
                footer.disableRiskButton(true);
                if (!machine.isStarted()) {
                    spin();
                }
            }
        });
        // Risk button
        this.footer.getRiskButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (footer.getRiskButton().isDisabled()) return;
                RiskType type = Connection.getInstance().getRiskType();
                switch (type) {
                    case GREATER:
                        try {
                            LoadingHandler handler = new LoadingHandler();
                            Connection.getInstance().requestInitRiskGreater(handler);
                            parent_screen.parent.showLoading(new SRiskGreater(), handler);
                        } catch (StringCodeException e) {
                            Log.log(e);
                        }
                        break;
                    case COLOR:
                        try {
                            LoadingHandler handler = new LoadingHandler();
                            Connection.getInstance().requestInitRisk(handler);
                            parent_screen.parent.showLoading(new SRiskColor(), handler);
                        } catch (StringCodeException e) {
                            Log.log(e);
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        // Max bet button
        this.footer.getMaxBetButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (footer.getMaxBetButton().isDisabled()) return;
                try {
                    Connection.getInstance().requestMaxBet(SlotsType.BOOK_OF_RA, conf_handler);
                } catch (StringCodeException e) {
                    Log.log(e);
                }
            }
        });
        // Payout table button
        if (this.pay_table != null) {
            this.footer.getPayoutButton().addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (footer.getPayoutButton().isDisabled()) return;
                    pay_table.show();
                }
            });
        } else {
            this.footer.getPayoutButton().setDisabled(true);
        }
        // Response
        try {
            Connection.getInstance().responseInitSlots(handler.getJson(), this.machine);
        } catch (StringCodeException e) {
            Log.log(e);
        }
        // Bet combo
        this.footer.getBetCombo().setValidValues(Connection.getInstance().getValidBets(), Connection.getInstance().getCurrentBet());
        this.footer.getBetCombo().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (footer.getBetCombo().isDisabled()) return;
                footer.getBetWindow().show(Connection.getInstance().getValidBets(), Connection.getInstance().getCurrentBet(), new BetClickHandler() {
                    @Override
                    public void clicked(int index) {
                        try {
                            Connection.getInstance().requestChangeConfig(Connection.getInstance().getCurrentLines(), index, conf_handler);
                            if (Connection.getInstance().isSoundOn() && sound_bet != null) {
                                sound_bet.play();
                            }
                        } catch (StringCodeException e) {
                            Log.log(e);
                        }
                    }
                });
            }
        });
        // Lines combo
        this.footer.getLinesCombo().setValidValues(Connection.getInstance().getValidLines(), Connection.getInstance().getCurrentLines());
        this.footer.getLinesCombo().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (footer.getLinesCombo().isDisabled()) return;
                footer.getLinesWindow().show(Connection.getInstance().getValidLines(), Connection.getInstance().getCurrentLines(), new BetClickHandler() {
                    @Override
                    public void clicked(int index) {
                        try {
                            Connection.getInstance().requestChangeConfig(index, Connection.getInstance().getCurrentBet(), conf_handler);
                            if (Connection.getInstance().isSoundOn() && sound_lines != null) {
                                sound_lines.play();
                            }
                        } catch (StringCodeException e) {
                            Log.log(e);
                        }
                    }
                });
            }
        });
        this.footer.updateTotalBet();
        // User info
        this.machine.setActiveLines(Connection.getInstance().getCurrentLinesCount());
        this.footer.setAwardMoney(Connection.getInstance().getAward(), false);
        super.updateInfo();
        this.updateButtons();
        if (this.updateBonusGame()) {
            if (Connection.getInstance().getFreeSpins() > 0) {
                try {
                    JsonObject js = new JsonObject(handler.getJson());
                    this.bonus_symbol = SymbolType.valueOf(js.getString("bonus_symbol"));
                } catch (StringCodeException e) {
                    Log.log(e);
                }
                if (this.bonus_symbol == null) this.bonus_symbol = SymbolType.N01;
                this.message.show(this.bonus_symbol, Connection.getInstance().getFreeSpins());
                this.machine.clearLines();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        this.footer.resize(width, height);
        if (this.pay_table != null)
            this.pay_table.resize(width, height);
        this.message.resize(width, height);
        this.machine.resize(width, height);
    }

    @Override
    public void act(float delta) {
        this.machine.act(delta);
        super.act(delta);
    }

    @Override
    public void draw() {
        Batch batch = super.getBatch();
        batch.begin();
        this.machine.draw(batch, 1f);
        batch.end();
        super.draw();
    }

    public void spin() {
        if (!machine.isStarted()) {
            if (!Connection.getInstance().isCanSpin()) return;
            int money = Connection.getInstance().getMoney();
            int award = Connection.getInstance().getAward();
            int free_spins = Connection.getInstance().getFreeSpins();
            if (free_spins <= 0) {
                header.getMoneyLabel().changeMoneyNoAnimation(money + award - footer.getTotalBet());
                if (award > 0 && this.sound_money != null && Connection.getInstance().isSoundOn()) {
                    this.sound_money.play();
                }
                footer.changeAwardMoney(0);
            } else {
                footer.setFreeSpins(free_spins - 1);
            }
            machine.addEffects(effects);
            machine.start();
            this.spin_get = false;
            footer.disableRiskButton(true);
            try {
                Connection.getInstance().requestSpin(SlotsType.BOOK_OF_RA, spin_handler);
            } catch (StringCodeException e) {
                Log.log("Spin failed.", e);
                return;
            }
        } else if (this.spin_get) {
            machine.stopNow();
        }
    }

    @Override
    protected boolean isLobbyButtonMakeAction() {
        if (this.pay_table != null &&
                this.pay_table.isVisible()) {
            this.pay_table.hide();
            return true;
        } else return super.isLobbyButtonMakeAction();
    }

    private void updateComboBoxes() {
        this.footer.getBetCombo().setActiveIndex(Connection.getInstance().getCurrentBet());
        this.footer.getLinesCombo().setActiveIndex(Connection.getInstance().getCurrentLines());
        this.footer.updateTotalBet();
    }

    // Bonus game

    private void disableBetButtons() {
        this.footer.disableBetCombo(true);
        this.footer.disableLinesCombo(true);
        this.footer.disableMaxbetButton(true);
    }

    private void updateNumbers() {
        this.footer.setFreeSpins(Connection.getInstance().getFreeSpins());
        this.footer.setAwardMoney(0, true);
        this.header.getMoneyLabel().changeMoneyNoAnimation(Connection.getInstance().getMoney());
    }

    private void updateButtons() {
        this.footer.disableMaxbetButton(!Connection.getInstance().isCanBetMax());
        this.footer.disableLinesCombo(!Connection.getInstance().isCanChangeLines());
        this.footer.disableBetCombo(!Connection.getInstance().isCanChangeBet());

        System.out.println("creo que aki donde cambia le button Spin233");
        this.footer.disableSpinButton(!Connection.getInstance().isCanSpin());
        this.footer.disableRiskButton(!Connection.getInstance().isCanRisk());
        this.footer.setFreeSpins(Connection.getInstance().getFreeSpins());
    }

    private void showFooter(boolean show) {
        this.footer.setVisible(show);
    }

    private boolean updateBonusGame() {
        boolean active = Connection.getInstance().getBonusGame() == BonusGame.BOOK_OF_RA_SLOT;
        if (Connection.getInstance().getFreeSpins() < 1) active = false;
        if (active) this.showFooter(false);
        return active;
    }

    private void spinBonus() {
        this.machine.start();
        try {
            Connection.getInstance().requestBonusProc(this.spin_handler_bonus, 0);
        } catch (StringCodeException e) {
            Log.log(e);
        }
    }

    private JsonObject updateState(JsonObject state) throws StringCodeException {
        this.bonus_game_active = !state.getBoolean("over");
        this.bonus_symbol = SymbolType.valueOf(state.getString("bonus_symbol"));
        boolean require_update = state.getBoolean("required_update_symbols");
        if (require_update) {
            this.symbols_ra = parseSymbols(state.getJsonArray("symbols2"));
            this.lines_ra = parseSuperLines(state.getJsonArray("super_lines"));
        } else {
            this.symbols_ra = null;
            this.lines_ra = null;
        }
        JsonObject spin = state.getJsonObject("spin");
        Connection.getInstance().setMoney(spin.getInt("credit"));
        Connection.getInstance().setFreeSpins(spin.getInt("free_spins"));
        this.message.setSpinsCount(Connection.getInstance().getFreeSpins());
        return spin;
    }

    private SymbolType[][] parseSymbols(JsonArray array) throws StringCodeException {
        if (array == null) return null;
        SymbolType[][] symbols = new SymbolType[array.length()][];
        for (int i = 0; i < symbols.length; i++) {
            JsonArray reel = array.getJsonArray(i);
            symbols[i] = new SymbolType[reel.length()];
            for (int j = 0; j < symbols[i].length; j++) {
                symbols[i][j] = SymbolType.valueOf(reel.getString(j));
            }
        }
        return symbols;
    }

    private LinesExt parseLines(JsonArray array) throws StringCodeException {
        LinesExt linesExt = new LinesExt();
        int array_length = array.length();
        for (int i = 0; i < array_length; i++) {
            JsonObject json_line = array.getJsonObject(i);
            int[] positions = new int[5];
            JsonArray json_positions = json_line.getJsonArray("symbols");
            for (int j = 0; j < positions.length; j++) {
                positions[j] = json_positions.getInt(j);
            }
            LineExt lineExt = new LineExt(SymbolType.valueOf(json_line.getString("symbol")), positions, json_line.getInt("count"), json_line.getBoolean("left", true));
            lineExt.setLeft(json_line.getBoolean("left"));
            lineExt.setBonusLine(json_line.getBoolean("bonus_line"));
            lineExt.setAward(json_line.getInt("pay", 0));
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
        return linesExt;
    }

    private LinesExt parseSuperLines(JsonArray array) throws StringCodeException {
        LinesExt linesExt = new LinesExt();
        int count = array.length();
        for (int i = 0; i < count; i++) {
            LineExt line = new LineExt(null, null, 0, true);
            JsonObject line_json = array.getJsonObject(i);
            JsonArray array_json = line_json.getJsonArray("positions");
            int count_json = array_json.length();
            int[] positions = new int[count_json];
            for (int j = 0; j < count_json; j++)
                positions[j] = array_json.getInt(j);
            array_json = line_json.getJsonArray("rectangles");
            count_json = array_json.length();
            line.setAward(line_json.getInt("pay", 0));
            line.setLine(positions);
            line.setBonusLine(false);
            line.setLeft(true);
            linesExt.add(line);
        }
        return linesExt;
    }

}
