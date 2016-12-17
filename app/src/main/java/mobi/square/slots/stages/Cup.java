package mobi.square.slots.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import mobi.square.slots.api.Connection;
import mobi.square.slots.classes.Machine;
import mobi.square.slots.classes.Reel;
import mobi.square.slots.config.AppConfig;
import mobi.square.slots.config.SoundList;
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
import mobi.square.slots.screens.SLobby;
import mobi.square.slots.ui.CupFooter;
import mobi.square.slots.ui.CupResultWindow;
import mobi.square.slots.utils.json.JsonArray;

public class Cup extends Header {

    private SlotsType type;
    private CupFooter footer;
    private CupResultWindow result;
    private Machine machine;
    private boolean spin_get;

    private Sound sound_line_wins;
    private JsonArray effects;

    private AsyncJsonHandler spin_handler = new AsyncJsonHandler() {
        @Override
        public void onCompleted(String json) {
            try {
                effects = Connection.getInstance().responseSpin(json, machine);
                footer.setSpinsCount(Connection.getInstance().getCupSpinsRemain());
                spin_get = true;
            } catch (StringCodeException e) {
                Log.log("Spin handler caused error.", e);
                machine.stopError();
            }
        }
    };

    private MachineStoppedListener stopped_handler = new MachineStoppedListener() {
        @Override
        public void stopped(Machine machine) {
            updateNumbers();
            if (Connection.getInstance().isForceSpin()) {
                footer.setScore(Connection.getInstance().getCupScore(), true);
                spin();
            } else if (!footer.isAutoMode()) {
                updateButtons(false);
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
            footer.addScore(award);
        }
    };

    private LinesShowedListener lines_showed_handler = new LinesShowedListener() {
        @Override
        public void lines_showed(Machine machine) {
            footer.setScore(Connection.getInstance().getCupScore(), false);
            if (footer.isAutoMode()) {
                if (!Connection.getInstance().isCanCupSpin()) {
                    footer.setAutoMode(false);
                    updateButtons(true);
                } else spin();
            } else updateButtons(true);
        }
    };

    public Cup(SBasic parent, SlotsType type) {
        super(parent);
        this.type = type;
        this.footer = null;
        this.result = null;
        this.machine = null;
        this.effects = null;
        this.sound_line_wins = null;
        this.spin_get = false;
    }

    @Override
    public void load(LoadingHandler handler) {
        super.load(false);
        super.addLowActors();
        // Sounds
        this.sound_line_wins = Connection.getManager().get(SoundList.LINE_WINS, Sound.class);
        // Footer
        TextureAtlas atlas = Connection.getManager().get("atlas/Footer.pack", TextureAtlas.class);
        TextureAtlas a2 = Connection.getManager().get("atlas/Autospin.pack", TextureAtlas.class);
        TextureAtlas a3 = Connection.getManager().get("atlas/Cup.pack", TextureAtlas.class);
        this.footer = CupFooter.newInstance(atlas, a2, a3);
        super.addActor(this.footer);
        super.addHighActors();
        // Result window
        this.result = CupResultWindow.newInstance(a3);
        this.result.setListener(new CupResultWindow.CupWindowListener() {
            @Override
            public void closeClicked() {
                try {
                    LoadingHandler handler = new LoadingHandler();
                    Connection.getInstance().requestSlotsList(handler);
                    parent_screen.parent.showLoading(new SLobby(), handler);
                } catch (StringCodeException e) {
                    Log.log(e);
                }
            }

            @Override
            public void actionClicked() {
                result.hide();
                if (Connection.getInstance().getCupPay() >
                        Connection.getInstance().getMoney()) {
                    showBankWindow();
                } else try {
                    Connection.getInstance().requestCupRegister(cup_register_handler);
                } catch (StringCodeException e) {
                    Log.log(e);
                }
            }
        });
        super.addActor(this.result);
        // Machine
        this.machine = Machine.newInstance(this, this.type);
        this.machine.setBounds(0f, this.footer.getHeight(), AppConfig.VIEWPORT_WIDTH, AppConfig.VIEWPORT_HEIGHT - this.header.getHeight() - this.footer.getHeight());
        this.machine.addListener(this.line_showed_handler);
        this.machine.addListener(this.lines_showed_handler);
        this.machine.addListener(this.reel_stopped_handler);
        this.machine.addListener(this.stopped_handler);
        if (this.type == SlotsType.JACK_HAMMER)
            this.machine.setLinesTime(.1f);
        this.machine.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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
                if (!machine.isStarted()) {
                    spin();
                }
            }
        });
        // Response
        try {
            Connection.getInstance().responseCupInit(handler.getJson(), this.machine);
        } catch (StringCodeException e) {
            Log.log(e);
        }
        // User info
        this.footer.setScore(Connection.getInstance().getCupScore(), false);
        super.updateInfo();
        this.updateButtons(true);
        this.updateCupInfo();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        this.footer.resize(width, height);
        this.result.resize(width, height);
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
            if (!Connection.getInstance().isCanCupSpin()) return;
            //footer.setSpinsCount(Connection.getInstance().getCupSpinsRemain() - 1);
            machine.addEffects(effects);
            machine.start();
            this.spin_get = false;
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

    private void updateNumbers() {
        //this.footer.changeScore(Connection.getInstance().getCupScore());
        this.footer.setSpinsCount(Connection.getInstance().getCupSpinsRemain());
        if (this.machine.getAwardLinesCount() > 0 &&
                Connection.getInstance().isSoundOn() &&
                this.sound_line_wins != null) {
            //this.sound_line_wins.play();
        }
    }

    private void updateButtons(boolean show_message) {
        this.footer.disableSpinButton(!Connection.getInstance().isCanCupSpin());
        if (!show_message) return;
        if (Connection.getInstance().getCupSpinsRemain() < 1 &&
                !Connection.getInstance().isForceSpin()) {
            this.result.show(
                    Connection.getInstance().getCupScore(),
                    Connection.getInstance().getCupPlace(),
                    Connection.getInstance().getMoney(),
                    Connection.getInstance().getCupPay()
            );
        }
    }

    private void updateCupInfo() {
        this.footer.setPrizePool(Connection.getInstance().getCupAward());
        this.footer.setSpinsCount(Connection.getInstance().getCupSpinsRemain());
        this.footer.setLeaders(Connection.getInstance().getCupLeaders());
    }

}
