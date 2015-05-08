package mobi.square.slots.b.rockclimber;

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

public class RockClimberBonus extends Header {

	private RockClimberGroup game;
	private Timer timer;
	private int award;

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

	public RockClimberBonus(SBasic parent) {
		super(parent);
		this.game = null;
		this.timer = null;
		this.award = 0;
	}

	@Override
	public void load(LoadingHandler handler) {
		super.load(false);
		Texture slope = Connection.getManager().get("RockClimber/RockClimberSlope.jpg", Texture.class);
		Texture peak = Connection.getManager().get("RockClimber/RockClimberPeak.jpg", Texture.class);
		TextureAtlas atlas = Connection.getManager().get("RockClimber/RockClimberBonus.pack", TextureAtlas.class);
		this.game = RockClimberGroup.newInstance(atlas, slope, peak);
		this.game.setListener(new RockClimberGroup.RockClimberListener() {
			@Override
			public void clicked(int index) {
				try {
					Connection.getInstance().requestBonusProc(RockClimberBonus.this.proc_handler, index);
				} catch (StringCodeException e) {
					Log.log(e);
				}
			}
			@Override
			public void success() {
				game.setAward(award);
			}
			@Override
			public void fail() {
				game.setAward(award);
				backToGame(2f);
			}
			@Override
			public void win() {
				backToGame(4f);
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

	private void updateState(JsonObject json, boolean climb) throws StringCodeException {
		boolean over = json.getBoolean("super_game");
		JsonArray ropes = json.getJsonArray("safes");
		this.award = json.getInt("award");
		int[] state = new int[5];
		for (int i = 0; i < ropes.length(); i++) {
			JsonObject rope = ropes.getJsonObject(i);
			boolean opened = rope.getBoolean("opened");
			boolean current = rope.getBoolean("current");
			int index = rope.getInt("index");
			int amount = rope.getInt("amount");
			state[index] = current ? amount > 0 ? 2 : 1 : opened ? 0 : 1;
		}
		if (climb) {
			this.game.startClimb(state, over);
		} else this.game.setAward(this.award);
	}

	private void backToGame(float delay) {
		this.timer = new Timer();
		this.timer.scheduleTask(new Task() {
			@Override
			public void run() {
				RockClimberBonus.this.timer.stop();
				try {
					LoadingHandler handler = new LoadingHandler();
					Connection.getInstance().requestInitSlots(SlotsType.ROCKCLIMBER, handler);
					RockClimberBonus.this.parent_screen.parent.showLoading(new SRockClimber(), handler);
				} catch (StringCodeException e) {
					Log.log(e);
				}
			}
		}, delay);
	}

}
