package mobi.square.slots.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

import mobi.square.slots.api.Connection;
import mobi.square.slots.b.bookofra.TBookOfRa;
import mobi.square.slots.classes.Machine;
import mobi.square.slots.classes.Reel;
import mobi.square.slots.config.AppConfig;
import mobi.square.slots.config.SoundList;
import mobi.square.slots.enums.RiskType;
import mobi.square.slots.enums.SlotsType;
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
import mobi.square.slots.screens.bonus.SFairytaleBonus;
import mobi.square.slots.screens.bonus.SGarageBoxes;
import mobi.square.slots.screens.bonus.SGarageLocks;
import mobi.square.slots.screens.bonus.SGladiatorsBonus;
import mobi.square.slots.screens.bonus.SResidentLocks;
import mobi.square.slots.screens.bonus.SUnderwaterLifeBonus;
import mobi.square.slots.ui.GameFooter;
import mobi.square.slots.ui.GameFooter.BetWindow.BetClickHandler;
import mobi.square.slots.ui.payout.TBasic;
import mobi.square.slots.ui.payout.TFairytale;
import mobi.square.slots.ui.payout.TGarage;
import mobi.square.slots.ui.payout.TGladiators;
import mobi.square.slots.ui.payout.TMoneyGame;
import mobi.square.slots.ui.payout.TResident;
import mobi.square.slots.ui.payout.TUnderwaterLife;
import mobi.square.slots.utils.json.JsonArray;

public class SinglePlayer extends Header {

    public static GameFooter footer;
    protected SlotsType type;
    protected Machine machine;
    protected Timer timer;
    protected TBasic pay_table;
    protected boolean spin_get;

    protected Sound sound_change_bet;
    protected Sound sound_change_lines;
    protected Sound sound_money;

    protected JsonArray effects;

    protected AsyncJsonHandler spin_handler = new AsyncJsonHandler() {
        @Override
        public void onCompleted(String json) {
            try {
                effects = Connection.getInstance().responseSpin(json, machine);
                spin_get = true;
                //System.out.println("Stop Called!");
                updateComboBoxes();
            } catch (StringCodeException e) {
                Log.log("Spin handler caused error.", e);
                machine.stopError();
            }
        }
    };

    protected AsyncJsonHandler conf_handler = new AsyncJsonHandler() {
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
            betChanged();
        }
    };

    protected MachineStoppedListener stopped_handler = new MachineStoppedListener() {
        @Override
        public void stopped(Machine machine) {
            updateNumbers();
            if (!footer.isAutoMode()) {
                System.out.println("is updating button here jeje");
                updateButtons();
            }
            if (updateBonusGame()) {
                footer.setAutoMode(false);
                footer.setSpinButtonDisabled(true);
            }
        }
    };

    protected ReelStoppedListener reel_stopped_handler = new ReelStoppedListener() {
        @Override
        public void stopped(Reel reel) {
            // Nothing need to do
        }
    };

    protected LineShowedListener line_showed_handler = new LineShowedListener() {
        @Override
        public void line_showed(Machine machine, int line, int award) {
            footer.addAwardMoney(award);
        }
    };

    protected LinesShowedListener lines_showed_handler = new LinesShowedListener() {
        @Override
        public void lines_showed(Machine machine) {
            footer.setAwardMoney(Connection.getInstance().getAward(), false);
            if (footer.isAutoMode()) {
                if (!Connection.getInstance().isCanSpin()) {
                    footer.setAutoMode(false);
                } else spin();
            }
        }
    };

    public SinglePlayer(SBasic parent, SlotsType type) {
        super(parent);
        this.type = type;
        footer = null;
        this.machine = null;
        this.effects = null;
        this.timer = null;
        this.pay_table = null;
        this.sound_change_bet = null;
        this.sound_change_lines = null;
        this.sound_money = null;
        this.spin_get = false;
    }

    @Override
    public void load(LoadingHandler handler) {
        super.load(false);
        super.addLowActors();
        Connection.setLastScreen(this.type);
        // Sounds
        this.sound_change_bet = this.getChangeBetSound();
        this.sound_change_lines = this.getChangeLinesSound();
        this.sound_money = Connection.getManager().get(SoundList.MONEY_COMMON, Sound.class);
        // Footer
        TextureAtlas atlas = Connection.getManager().get("atlas/Footer.pack", TextureAtlas.class);
        TextureAtlas a2 = Connection.getManager().get("atlas/Autospin.pack", TextureAtlas.class);
        footer = GameFooter.newInstance(this, atlas, a2);
        super.addActor(footer);
        super.addHighActors();
        // Machine
        this.machine = this.getMachineInstance();
        this.machine.setBounds(0f, footer.getHeight(), AppConfig.VIEWPORT_WIDTH, AppConfig.VIEWPORT_HEIGHT - this.header.getHeight() - footer.getHeight());
        this.machine.addListener(this.line_showed_handler);
        this.machine.addListener(this.lines_showed_handler);
        this.machine.addListener(this.reel_stopped_handler);
        this.machine.addListener(this.stopped_handler);
        this.machine.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        // Pay table
        this.pay_table = this.getPayTableInstance();
        if (this.pay_table != null) {
            super.addActor(this.pay_table);
        }
        // Spin button
        footer.setSpinClickedTask(new Runnable() {
            @Override
            public void run() {
                spin();
            }
        });
        footer.setAutoClickedTask(new Runnable() {
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
        footer.getRiskButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (footer.getRiskButton().isDisabled()) return;
                SinglePlayer.this.showRiskScreen();
            }
        });
        // Max bet button
        footer.getMaxBetButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (footer.getMaxBetButton().isDisabled()) return;
                try {
                    Connection.getInstance().requestMaxBet(type, conf_handler);
                } catch (StringCodeException e) {
                    Log.log(e);
                }
            }
        });
        // Payout table button
        if (this.pay_table != null) {
            footer.getPayoutButton().addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (footer.getPayoutButton().isDisabled()) return;
                    pay_table.show();
                }
            });
        } else {
            footer.getPayoutButton().setDisabled(true);
        }
        // Response
        try {
            Connection.getInstance().responseInitSlots(handler.getJson(), this.machine);
        } catch (StringCodeException e) {
            Log.log(e);
        }
        // Bet combo
        footer.getBetCombo().setValidValues(Connection.getInstance().getValidBets(), Connection.getInstance().getCurrentBet());
        footer.getBetCombo().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (footer.getBetCombo().isDisabled()) return;
                footer.getBetWindow().show(Connection.getInstance().getValidBets(), Connection.getInstance().getCurrentBet(), new BetClickHandler() {
                    @Override
                    public void clicked(int index) {
                        try {
                            Connection.getInstance().requestChangeConfig(Connection.getInstance().getCurrentLines(), index, conf_handler);
                            if (Connection.getInstance().isSoundOn() && sound_change_bet != null) {
                                sound_change_bet.play();
                            }
                        } catch (StringCodeException e) {
                            Log.log(e);
                        }
                    }
                });
            }
        });
        // Lines combo
        footer.getLinesCombo().setValidValues(Connection.getInstance().getValidLines(), Connection.getInstance().getCurrentLines());
        footer.getLinesCombo().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (footer.getLinesCombo().isDisabled()) return;
                footer.getLinesWindow().show(Connection.getInstance().getValidLines(), Connection.getInstance().getCurrentLines(), new BetClickHandler() {
                    @Override
                    public void clicked(int index) {
                        try {
                            Connection.getInstance().requestChangeConfig(index, Connection.getInstance().getCurrentBet(), conf_handler);
                            if (Connection.getInstance().isSoundOn() && sound_change_lines != null) {
                                sound_change_lines.play();
                            }
                        } catch (StringCodeException e) {
                            Log.log(e);
                        }
                    }
                });
            }
        });
        footer.updateTotalBet();
        // User info
        this.machine.setActiveLines(Connection.getInstance().getCurrentLinesCount());
        footer.setAwardMoney(Connection.getInstance().getAward(), false);
        super.updateInfo();
        this.updateButtons();
        this.updateBonusGame();
        this.betChanged();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        footer.resize(width, height);
        if (this.pay_table != null)
            this.pay_table.resize(width, height);
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
                Connection.getInstance().requestSpin(type, spin_handler);
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

    protected Machine getMachineInstance() {
        return Machine.newInstance(this, this.type);
    }

    protected TBasic getPayTableInstance() {
        Texture pay_background = this.machine.config.pay_table_bg != null ? Connection.getManager().get(this.machine.config.pay_table_bg, Texture.class) : null;
        TextureAtlas pay_atlas = this.machine.config.pay_table != null ? Connection.getManager().get(this.machine.config.pay_table, TextureAtlas.class) : null;
        if (pay_background != null || pay_atlas != null) {
            TextureAtlas windows_atlas = Connection.getManager().get("atlas/Windows.pack", TextureAtlas.class);
            switch (this.type) {
                case GARAGE:
                    return TGarage.newInstance(pay_background, pay_atlas, this.machine.atlas, windows_atlas);
                case GLADIATORS:
                    return TGladiators.newInstance(pay_background, pay_atlas, this.machine.atlas, windows_atlas);
                case MONEY_GAME:
                    return TMoneyGame.newInstance(pay_background, pay_atlas, this.machine.atlas, windows_atlas);
                case RESIDENT:
                    return TResident.newInstance(pay_background, pay_atlas, this.machine.atlas, windows_atlas);
                case FAIRYTALE:
                    return TFairytale.newInstance(pay_background, pay_atlas, this.machine.atlas, windows_atlas);
                case UNDERWATER_LIFE:
                    return TUnderwaterLife.newInstance(pay_background, pay_atlas, this.machine.atlas, windows_atlas);
                case BOOK_OF_RA:
                    return TBookOfRa.newInstance(pay_background, pay_atlas, this.machine.atlas, windows_atlas);
                default:
                    return null;
            }
        }
        return null;
    }

    protected void showRiskScreen() {
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

    protected Sound getChangeBetSound() {
        return null;
    }

    protected Sound getChangeLinesSound() {
        return null;
    }

    private void updateComboBoxes() {
        footer.getBetCombo().setActiveIndex(Connection.getInstance().getCurrentBet());
        footer.getLinesCombo().setActiveIndex(Connection.getInstance().getCurrentLines());
        footer.updateTotalBet();
    }

    private void disableBetButtons() {
        footer.disableBetCombo(true);
        footer.disableLinesCombo(true);
        footer.disableMaxbetButton(true);
    }

    private void updateNumbers() {
        footer.setFreeSpins(Connection.getInstance().getFreeSpins());
        //int award = Connection.getInstance().getAward();
        //this.footer.changeAwardMoney(award);
        footer.setAwardMoney(0, true);
        //if (award > 0 && this.sound_line_wins != null && Connection.getInstance().isSoundOn())
        //this.sound_line_wins.play();
        this.header.getMoneyLabel().changeMoneyNoAnimation(Connection.getInstance().getMoney());
    }


    private void updateButtons() {
        footer.disableMaxbetButton(!Connection.getInstance().isCanBetMax());
        footer.disableLinesCombo(!Connection.getInstance().isCanChangeLines());
        footer.disableBetCombo(!Connection.getInstance().isCanChangeBet());
        System.out.println("creo que aki donde cambia le button Spin2");
        footer.disableSpinButton(!Connection.getInstance().isCanSpin());
        footer.disableRiskButton(!Connection.getInstance().isCanRisk());
        footer.setFreeSpins(Connection.getInstance().getFreeSpins());
    }

    protected void betChanged() {
        // Nothing need to do
    }

    protected boolean updateBonusGame() {
        switch (Connection.getInstance().getBonusGame()) {
            case GARAGE_BOXES:
                this.showBonusGameScreen(new SGarageBoxes(), 2f);
                return true;
            case GARAGE_LOCKS:
                this.showBonusGameScreen(new SGarageLocks(), 2f);
                return true;
            case FAIRYTALE_KEGS:
                this.showBonusGameScreen(new SFairytaleBonus(), 2f);
                return true;
            case UNDERWATER_LIFE_SEASHELLS:
                this.showBonusGameScreen(new SUnderwaterLifeBonus(), 2f);
                return true;
            case RESIDENT_SAFES:
                this.showBonusGameScreen(new SResidentLocks(), 2f);
                return true;
            case CHESTS:
                this.showBonusGameScreen(new SGladiatorsBonus(), 2f);
                return true;
            default:
                return false;
        }
    }

    protected void showBonusGameScreen(final SBasic screen, float time) {
        footer.setSpinButtonDisabled(true);
        this.timer = new Timer();
        this.timer.scheduleTask(new Task() {
            @Override
            public void run() {
                try {
                    LoadingHandler handler = new LoadingHandler();
                    Connection.getInstance().requestBonusState(handler);
                    parent_screen.parent.showLoading(screen, handler);
                } catch (StringCodeException e) {
                    Log.log(e);
                }
            }
        }, time);
    }

}
