package mobi.square.slots.b.crazymonkey;

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
import mobi.square.slots.stages.Header;
import mobi.square.slots.utils.json.JsonArray;
import mobi.square.slots.utils.json.JsonObject;

public class CrazyMonkeyRopes extends Header {

    private CrazyRopesGroup game;
    private boolean in_progress;
    private boolean over;
    private Timer timer;
    private int index;

    private final AsyncJsonHandler proc_handler = new AsyncJsonHandler() {
        @Override
        public void onCompleted(String json) {
            try {
                JsonObject state = Connection.getInstance().responseBonusProc(json);
                updateState(state, true);
            } catch (StringCodeException e) {
                Log.log(e);
            }
        }
    };

    public CrazyMonkeyRopes(SBasic parent) {
        super(parent);
        this.game = null;
        this.timer = null;
        this.over = false;
        this.in_progress = false;
        this.index = 0;
    }

    @Override
    public void load(LoadingHandler handler) {
        super.load(false);
        Texture background = Connection.getManager().get("CrazyMonkey/CrazyMonkeyBonusBg.jpg", Texture.class);
        TextureAtlas atlas = Connection.getManager().get("CrazyMonkey/CrazyMonkeyBonus.pack", TextureAtlas.class);
        this.game = CrazyRopesGroup.newInstance(background, atlas);
        this.game.setListener(new CrazyRopesGroup.CrazyMonkeyListener() {
            @Override
            public void clicked(int index) {
                if (in_progress || over) return;
                CrazyMonkeyRopes.this.index = index;
                game.gotoRope(index);
                try {
                    Connection.getInstance().requestBonusProc(proc_handler, index);
                } catch (StringCodeException e) {
                    Log.log(e);
                }
                in_progress = true;
            }

            @Override
            public void done() {
                in_progress = false;
            }
        });
        super.addActor(this.game);
        super.addActors();
        try {
            JsonObject json = Connection.getInstance().responseBonusState(handler.getJson());
            updateState(json, false);
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

    private void updateState(JsonObject json, boolean use) throws StringCodeException {
        this.over = json.getBoolean("over");
        boolean super_game = json.getBoolean("super_game");
        boolean helmet = json.getBoolean("in_helmet");
        JsonArray ropes = json.getJsonArray("ropes");
        //int award = json.getInt("award");
        int[] awards = new int[5];
        for (int i = 0; i < ropes.length(); i++) {
            JsonObject rope = ropes.getJsonObject(i);
            boolean opened = rope.getBoolean("opened");
            int index = rope.getInt("index");
            int amount = rope.getInt("amount");
            awards[index] = opened ? amount : -1;
        }
        if (use) {
            this.game.useRope(awards[this.index]);
        } else {
            this.game.setAwards(awards);
            this.game.setHelmeted(helmet);
        }
        if (super_game) {
            this.in_progress = true;
            this.timer = new Timer();
            this.timer.scheduleTask(new Task() {
                @Override
                public void run() {
                    timer.stop();
                    try {
                        LoadingHandler handler = new LoadingHandler();
                        Connection.getInstance().requestBonusState(handler);
                        parent_screen.parent.showLoading(new SCrazyMonkeyBoxes(), handler);
                    } catch (StringCodeException e) {
                        Log.log(e);
                    }
                }
            }, 4f);
        } else if (this.over) {
            this.timer = new Timer();
            this.timer.scheduleTask(new Task() {
                @Override
                public void run() {
                    timer.stop();
                    try {
                        LoadingHandler handler = new LoadingHandler();
                        Connection.getInstance().requestInitSlots(SlotsType.CRAZY_MONKEY, handler);
                        parent_screen.parent.showLoading(new SCrazyMonkey(), handler);
                    } catch (StringCodeException e) {
                        Log.log(e);
                    }
                }
            }, 6f);
        }
    }

}
