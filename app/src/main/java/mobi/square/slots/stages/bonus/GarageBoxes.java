package mobi.square.slots.stages.bonus;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

import mobi.square.slots.api.Connection;
import mobi.square.slots.enums.SlotsType;
import mobi.square.slots.error.StringCodeException;
import mobi.square.slots.handlers.AsyncJsonHandler;
import mobi.square.slots.handlers.LoadingHandler;
import mobi.square.slots.logger.Log;
import mobi.square.slots.screens.SBasic;
import mobi.square.slots.screens.SSinglePlayer;
import mobi.square.slots.stages.Header;
import mobi.square.slots.ui.bonus.GarageBoxesGroup;
import mobi.square.slots.ui.bonus.GarageBoxesGroup.BoxOpenedListener;
import mobi.square.slots.utils.json.JsonArray;
import mobi.square.slots.utils.json.JsonObject;

public class GarageBoxes extends Header {

    private GarageBoxesGroup game;
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
    private final BoxOpenedListener box_handler = new BoxOpenedListener() {
        @Override
        public void opened(int index) {
            if (open_time || over) return;
            open_time = true;
            try {
                Connection.getInstance().requestBonusProc(proc_handler, index);
            } catch (StringCodeException e) {
                Log.log(e);
            }
        }
    };

    public GarageBoxes(SBasic parent) {
        super(parent);
        this.game = null;
        this.open_time = false;
        this.over = false;
        this.timer = null;
    }

    @Override
    public void load(LoadingHandler handler) {
        super.load(false);
        Texture background = Connection.getManager().get("atlas/GarageBoxesBg.jpg", Texture.class);
        TextureAtlas common = Connection.getManager().get("atlas/GarageCommon.pack", TextureAtlas.class);
        TextureAtlas boxes = Connection.getManager().get("atlas/GarageBoxes.pack", TextureAtlas.class);
        this.game = GarageBoxesGroup.newInstance(background, boxes, common);
        this.game.setBoxListener(box_handler);
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
        JsonArray boxes = json.getJsonArray("boxes");
        for (int i = 0; i < boxes.length(); i++) {
            JsonObject box = boxes.getJsonObject(i);
            int index = box.getInt("index");
            int amount = box.getInt("amount");
            boolean opened = box.getBoolean("opened");
            String content = box.getString("content");
            this.game.setBoxState(index, opened, amount, content);
        }
        this.game.setGaugeValues(json.getJsonArray("left_awards"), 0);
        this.game.setGaugeValues(json.getJsonArray("middle_awards"), 1);
        this.game.setGaugeValues(json.getJsonArray("right_awards"), 2);
        this.game.setTotalAward(json.getInt("award"));
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
