package mobi.square.slots.stages.bonus;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

import mobi.square.slots.api.Connection;
import mobi.square.slots.enums.BoxState;
import mobi.square.slots.enums.SlotsType;
import mobi.square.slots.error.StringCodeException;
import mobi.square.slots.handlers.AsyncJsonHandler;
import mobi.square.slots.handlers.LoadingHandler;
import mobi.square.slots.logger.Log;
import mobi.square.slots.screens.SBasic;
import mobi.square.slots.screens.SSinglePlayer;
import mobi.square.slots.stages.Header;
import mobi.square.slots.ui.bonus.GarageLocksGroup;
import mobi.square.slots.ui.bonus.GarageLocksGroup.SafeOpenedListener;
import mobi.square.slots.utils.json.JsonArray;
import mobi.square.slots.utils.json.JsonObject;

public class GarageLocks extends Header {

    private GarageLocksGroup game;
    private int current_lock;
    private boolean open_time;
    private boolean over;
    private Timer timer;
    private final AsyncJsonHandler proc_handler = new AsyncJsonHandler() {
        @Override
        public void onCompleted(String json) {
            open_time = false;
            try {
                JsonObject state = Connection.getInstance().responseBonusProc(json);
                updateState(state);
            } catch (StringCodeException e) {
                Log.log(e);
            }
        }
    };
    private final SafeOpenedListener safe_handler = new SafeOpenedListener() {
        @Override
        public void opened(int index, boolean left) {
            if (open_time || over || current_lock != index) return;
            open_time = true;
            try {
                Connection.getInstance().requestBonusProc(proc_handler, left ? 1 : 2);
            } catch (StringCodeException e) {
                Log.log(e);
            }
        }
    };

    public GarageLocks(SBasic parent) {
        super(parent);
        this.game = null;
        this.current_lock = 0;
        this.open_time = false;
        this.over = false;
        this.timer = null;
    }

    @Override
    public void load(LoadingHandler handler) {
        super.load(false);
        Texture boxes_bg = Connection.getManager().get("atlas/GarageBoxesBg.jpg", Texture.class);
        Texture locks_bg = Connection.getManager().get("atlas/GarageLocksBg.jpg", Texture.class);
        TextureAtlas common = Connection.getManager().get("atlas/GarageCommon.pack", TextureAtlas.class);
        TextureAtlas locks = Connection.getManager().get("atlas/GarageLocks.pack", TextureAtlas.class);
        this.game = GarageLocksGroup.newInstance(boxes_bg, locks_bg, locks, common);
        this.game.setSafeListener(this.safe_handler);
        this.game.addSuperKeyListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (open_time || over) return;
                open_time = true;
                try {
                    Connection.getInstance().requestBonusProc(proc_handler, 0);
                } catch (StringCodeException e) {
                    Log.log(e);
                }
            }
        });
        super.addActor(this.game);
        super.addActors();
        try {
            JsonObject state = Connection.getInstance().responseBonusState(handler.getJson());
            updateState(state);
        } catch (StringCodeException e) {
            Log.log(e);
        }
        super.updateInfo();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        this.game.resize(width, height);
    }

    private void updateState(JsonObject json) throws StringCodeException {
        this.over = json.getBoolean("over");
        JsonArray locks = json.getJsonArray("locks");
        boolean all_opened = true;
        for (int i = 0; i < locks.length(); i++) {
            JsonObject lock = locks.getJsonObject(i);
            boolean current = lock.getBoolean("current");
            boolean opened = lock.getBoolean("opened");
            BoxState left_state = BoxState.valueOf(lock.getString("left_state"));
            BoxState right_state = BoxState.valueOf(lock.getString("right_state"));
            this.game.setSafeState(i, true, left_state);
            this.game.setSafeState(i, false, right_state);
            this.game.setArrowsVisible(i, current);
            this.game.setLockOpened(i, opened);
            if (current) this.current_lock = i;
            if (!opened) all_opened = false;
        }
        this.game.setSuperKeyUsed(json.getBoolean("super_key_used"));
        this.game.setTotalAward(json.getInt("award"));
        this.game.setGarageOpened(all_opened);
        if (this.over) {
            this.open_time = true;
            this.game.showGameOverWindow(true);
            this.timer = new Timer();
            this.timer.scheduleTask(new Task() {
                @Override
                public void run() {
                    timer.stop();
                    try {
                        LoadingHandler handler = new LoadingHandler();
                        Connection.getInstance().requestInitSlots(SlotsType.GARAGE, handler);
                        parent_screen.parent.showLoading(new SSinglePlayer(SlotsType.GARAGE), handler);
                    } catch (StringCodeException e) {
                        Log.log(e);
                    }
                }
            }, 2);
        }
    }

}
