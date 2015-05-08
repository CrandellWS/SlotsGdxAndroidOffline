package mobi.square.slots.b.crazymonkey;

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
import mobi.square.slots.stages.Header;
import mobi.square.slots.utils.json.JsonObject;

public class CrazyMonkeyBoxes extends Header {

	private CrazyBoxesGroup game;
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

	public CrazyMonkeyBoxes(SBasic parent) {
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
		this.game = CrazyBoxesGroup.newInstance(background, atlas);
		this.game.setListener(new CrazyBoxesGroup.CrazyMonkeyListener() {
			@Override
			public void clicked(int index) {
				if (in_progress || over) return;
				try {
					Connection.getInstance().requestBonusProc(proc_handler, index);
				} catch (StringCodeException e) {
					Log.log(e);
				}
				CrazyMonkeyBoxes.this.index = index;
				in_progress = true;
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
	}

	private void updateState(JsonObject json, boolean use) throws StringCodeException {
		this.over = json.getBoolean("over");
		boolean super_game = json.getBoolean("super_game");
		boolean helmet = json.getBoolean("in_helmet");
		BoxState l_box = BoxState.valueOf(json.getString("left_box"));
		BoxState r_box = BoxState.valueOf(json.getString("right_box"));
		if (use) {
			this.game.openBoxex(l_box == BoxState.OPENED_WIN, r_box == BoxState.OPENED_WIN, this.index);
		} else this.game.setHelmeted(helmet);
		if (!super_game) {
			this.in_progress = true;
			this.timer = new Timer();
			this.timer.scheduleTask(new Task() {
				@Override
				public void run() {
					timer.stop();
					try {
						LoadingHandler handler = new LoadingHandler();
						Connection.getInstance().requestBonusState(handler);
						parent_screen.parent.showLoading(new SCrazyMonkeyRopes(), handler);
					} catch (StringCodeException e) {
						Log.log(e);
					}
				}
			}, 3f);
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
			}, 4f);
		}
	}

}
