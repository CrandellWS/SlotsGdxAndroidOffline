package mobi.square.slots.stages.bonus;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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
import mobi.square.slots.screens.bonus.SResidentLocks;
import mobi.square.slots.stages.Header;
import mobi.square.slots.ui.bonus.ResidentDoorsGroup;
import mobi.square.slots.ui.bonus.ResidentDoorsGroup.DoorOpenedListener;
import mobi.square.slots.utils.json.JsonObject;

public class ResidentDoors extends Header {

    private ResidentDoorsGroup game;
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
    private final DoorOpenedListener door_handler = new DoorOpenedListener() {
        @Override
        public void opened(boolean left) {
            if (open_time || over) return;
            open_time = true;
            try {
                Connection.getInstance().requestBonusProc(proc_handler, left ? 0 : 1);
            } catch (StringCodeException e) {
                Log.log(e);
            }
        }
    };

    public ResidentDoors(SBasic parent) {
        super(parent);
        this.game = null;
        this.open_time = false;
        this.over = false;
        this.timer = null;
    }

    @Override
    public void load(LoadingHandler handler) {
        super.load(false);
        Texture background = Connection.getManager().get("atlas/ResidentDoorsBg.jpg", Texture.class);
        TextureAtlas doors = Connection.getManager().get("atlas/ResidentDoors.pack", TextureAtlas.class);
        TextureAtlas common = Connection.getManager().get("atlas/ResidentCommon.pack", TextureAtlas.class);
        this.game = ResidentDoorsGroup.newInstance(background, doors, common);
        this.game.setDoorListener(this.door_handler);
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
        BoxState left_state = BoxState.valueOf(json.getString("left_box"));
        if (left_state != BoxState.CLOSED)
            this.game.open(true, left_state == BoxState.OPENED_WIN);
        BoxState right_state = BoxState.valueOf(json.getString("right_box"));
        if (right_state != BoxState.CLOSED)
            this.game.open(false, right_state == BoxState.OPENED_WIN);
        this.game.setTotalAward(json.getInt("award"));
        boolean super_game = json.getBoolean("super_game");
        if (this.over) {
            this.open_time = true;
            if (left_state == BoxState.OPENED_WIN ||
                    right_state == BoxState.OPENED_WIN) {
                this.game.showSuperPrizeWindow();
            } else this.game.showGameOverWindow();
            this.timer = new Timer();
            this.timer.scheduleTask(new Task() {
                @Override
                public void run() {
                    timer.stop();
                    try {
                        LoadingHandler handler = new LoadingHandler();
                        Connection.getInstance().requestInitSlots(SlotsType.RESIDENT, handler);
                        parent_screen.parent.showLoading(new SSinglePlayer(SlotsType.RESIDENT), handler);
                    } catch (StringCodeException e) {
                        Log.log(e);
                    }
                }
            }, 2);
        } else if (!super_game) {
            try {
                LoadingHandler handler = new LoadingHandler();
                Connection.getInstance().requestBonusState(handler);
                parent_screen.parent.showLoading(new SResidentLocks(), handler);
            } catch (StringCodeException e) {
                Log.log(e);
            }
        }
    }

}
