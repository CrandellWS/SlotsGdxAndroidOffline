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
import mobi.square.slots.ui.bonus.UnderwaterLifeBonusGroup;
import mobi.square.slots.ui.bonus.UnderwaterLifeBonusGroup.ShellOpenedListener;
import mobi.square.slots.utils.json.JsonArray;
import mobi.square.slots.utils.json.JsonObject;

public class UnderwaterLifeBonus extends Header {

    private UnderwaterLifeBonusGroup game;
    private boolean open_time;
    private boolean over;
    private Timer timer;

    private AsyncJsonHandler response_handler = new AsyncJsonHandler() {
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

    private ShellOpenedListener barrel_listener = new ShellOpenedListener() {
        @Override
        public void opened(int index) {
            if (open_time || over) return;
            open_time = true;
            try {
                Connection.getInstance().requestBonusProc(response_handler, index);
            } catch (StringCodeException e) {
                Log.log(e);
            }
        }
    };

    public UnderwaterLifeBonus(SBasic parent) {
        super(parent);
        this.game = null;
        this.open_time = false;
        this.over = false;
        this.timer = null;
    }

    @Override
    public void load(LoadingHandler handler) {
        super.load(false);
        Texture background = Connection.getManager().get("atlas/UnderwaterLifeBonusBg.jpg", Texture.class);
        TextureAtlas atlas = Connection.getManager().get("atlas/UnderwaterLifeBonus.pack", TextureAtlas.class);
        this.game = UnderwaterLifeBonusGroup.newInstance(background, atlas);
        this.game.setListener(this.barrel_listener);
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
            if (opened) this.game.open(index, amount);
        }
        this.game.setAward(json.getInt("award"));
        if (this.over) {
            this.open_time = true;
            this.game.showGameOverWindow();
            this.timer = new Timer();
            this.timer.scheduleTask(new Task() {
                @Override
                public void run() {
                    timer.stop();
                    try {
                        LoadingHandler handler = new LoadingHandler();
                        Connection.getInstance().requestInitSlots(SlotsType.UNDERWATER_LIFE, handler);
                        parent_screen.parent.showLoading(new SSinglePlayer(SlotsType.UNDERWATER_LIFE), handler);
                    } catch (StringCodeException e) {
                        Log.log(e);
                    }
                }
            }, 2);
        }
    }

}
