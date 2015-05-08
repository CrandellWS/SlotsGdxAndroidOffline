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
import mobi.square.slots.screens.bonus.SResidentDoors;
import mobi.square.slots.stages.Header;
import mobi.square.slots.ui.bonus.ResidentLocksGroup;
import mobi.square.slots.ui.bonus.ResidentLocksGroup.SafeOpenedListener;
import mobi.square.slots.utils.json.JsonArray;
import mobi.square.slots.utils.json.JsonObject;

public class ResidentLocks extends Header {

	private ResidentLocksGroup game;
	//private int current_lock;
	private boolean open_time;
	private boolean over;
	private Timer timer;

	private final SafeOpenedListener safe_handler = new SafeOpenedListener() {
		@Override
		public void opened(int index) {
			if (open_time || over/* || current_lock != index*/) return;
			open_time = true;
			try {
				Connection.getInstance().requestBonusProc(proc_handler, index);
			} catch (StringCodeException e) {
				Log.log(e);
			}
		}
	};

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

	public ResidentLocks(SBasic parent) {
		super(parent);
		this.game = null;
		//this.current_lock = 0;
		this.open_time = false;
		this.over = false;
		this.timer = null;
	}

	@Override
	public void load(LoadingHandler handler) {
		super.load(false);
		Texture background = Connection.getManager().get("atlas/ResidentLocksBg.jpg", Texture.class);
		TextureAtlas locks = Connection.getManager().get("atlas/ResidentLocks.pack", TextureAtlas.class);
		TextureAtlas common = Connection.getManager().get("atlas/ResidentCommon.pack", TextureAtlas.class);
		this.game = ResidentLocksGroup.newInstance(background, locks, common);
		this.game.setSafeListener(this.safe_handler);
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
		JsonArray safes = json.getJsonArray("safes");
		for (int i = 0; i < safes.length(); i++) {
			JsonObject safe = safes.getJsonObject(i);
			boolean current = safe.getBoolean("current");
			boolean opened = safe.getBoolean("opened");
			int index = safe.getInt("index");
			int amount = safe.getInt("amount");
			if (current) {
				//this.current_lock = index;
				this.game.setResidentIndex(index);
			}
			if (opened) {
				this.game.setSafeOpened(index, amount, this.over);
			}
		}
		this.game.setFireBottleAvailable(json.getBoolean("extinguisher"));
		this.game.setTotalAward(json.getInt("award"));
		boolean super_game = json.getBoolean("super_game");
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
						Connection.getInstance().requestInitSlots(SlotsType.RESIDENT, handler);
						parent_screen.parent.showLoading(new SSinglePlayer(SlotsType.RESIDENT), handler);
					} catch (StringCodeException e) {
						Log.log(e);
					}
				}
			}, 2);
		} else if (super_game) {
			this.game.showSuperGameWindow();
			this.timer = new Timer();
			this.timer.scheduleTask(new Task() {
				@Override
				public void run() {
					timer.stop();
					try {
						LoadingHandler handler = new LoadingHandler();
						Connection.getInstance().requestBonusState(handler);
						parent_screen.parent.showLoading(new SResidentDoors(), handler);
					} catch (StringCodeException e) {
						Log.log(e);
					}
				}
			}, 2);
		}
	}

}
