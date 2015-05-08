package mobi.square.slots.api;

import java.util.HashMap;

import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.net.NetJavaImpl;
import com.badlogic.gdx.utils.Timer;

import mobi.square.slots.enums.SlotsType;
import mobi.square.slots.error.StringCodeException;
import mobi.square.slots.handlers.CancelableHandler;
import mobi.square.slots.utils.json.JsonObject;

public class AndroidApi {

	private String device_id = null;
	protected String ok_uid = null;
	private boolean logged = false;
	public NetHttpClient client = null;
	private Gateway gateway = null;

	public static final SlotsType[] TYPES = {
		SlotsType.BOOK_OF_RA,
		SlotsType.CRAZY_MONKEY,
		SlotsType.FAIRYTALE,
		SlotsType.GARAGE,
		SlotsType.GLADIATORS,
		SlotsType.MONEY_GAME,
		SlotsType.RESIDENT,
		SlotsType.UNDERWATER_LIFE,
		SlotsType.ROCKCLIMBER
	};

	public static final boolean ONLINE = false;
	public static void load() {
		mobi.square.slots.game.slots.v3.config.Fairytale.load();
		mobi.square.slots.game.slots.v3.config.UnderwaterLife.load();
	}

	// Classes

	public class NetHttpClient extends NetJavaImpl {
		@Override
		public void sendHttpRequest(HttpRequest httpRequest, HttpResponseListener httpResponseListener) {
			Connection.addHandler(httpRequest, httpResponseListener);
			super.sendHttpRequest(httpRequest, httpResponseListener);
		}
	}

	// Constructors

	public AndroidApi() {
		this.client = new NetHttpClient();
		this.device_id = Connection.getWrapper().getDeviceId();
	}

	// Public Methods

	public void close() {
		this.client = null;
	}

	// Protected Methods

	protected JsonObject parseResponse(String response) throws StringCodeException {
		JsonObject json = new JsonObject(response);
		if (json.isNull("error")) return json;
		int error = json.getInt("error");
		switch (error) {
			case 105:
				throw new StringCodeException("billing_error");
			case 107:
				throw new StringCodeException("version_error");
			case 0:
				return json;
			default:
				throw new StringCodeException("server_error"); 
		}
	}

	protected void asyncMethod(String method, HashMap<String, String> params, CancelableHandler response_handler) throws StringCodeException
		{ this.asyncMethod(method, params, response_handler, false); }
	protected void asyncMethod(String method, HashMap<String, String> params, CancelableHandler response_handler, boolean billing) throws StringCodeException {
		if (params == null)
			params = new HashMap<String, String>();
		params.put("method", method);
		params.put("device_id", this.getDeviceId());
		Timer.schedule(new Task(this, params, response_handler), .1f);
	}

	// Private Methods

	private class Task extends Timer.Task {
		private final AndroidApi api;
		private final HashMap<String, String> params;
		private final CancelableHandler handler;
		public Task(AndroidApi api, HashMap<String, String> params, CancelableHandler handler) {
			this.api = api;
			this.params = params;
			this.handler = handler;
		}
		@Override
		public void run() {
			String response;
			try {
				response = this.api.getGateway().call(this.params);
			} catch (StringCodeException e) {
				return;
			}
			if (this.handler != null) {
				this.handler.handleHttpResponse(response);
			}
		}
	}

	private Gateway getGateway() {
		if (this.gateway == null)
			this.gateway = new Gateway();
		return this.gateway;
	}

	// Getters & Setters

	public void useDefaultDeviceId() {
		this.ok_uid = Connection.getWrapper().getDeviceId();
		Connection.getWrapper().writeDeviceId(this.ok_uid);
	}

	public void setDeviceId(String device_id) {
		this.ok_uid = device_id;
		Connection.getWrapper().writeDeviceId(this.ok_uid);
	}

	public boolean loadDeviceId() {
		this.ok_uid = Connection.getWrapper().readDeviceId();
		return this.ok_uid != null;
	}

	public String getDeviceId() {
		if (this.ok_uid != null)
			return this.ok_uid;
		if (this.device_id == null) {
			AppWrapper wrapper = Connection.getWrapper();
			if (wrapper != null)
				this.device_id = wrapper.getDeviceId();
			if (this.device_id == null) {
				this.device_id = "UNKNOWN";
			}
		}
		return this.device_id;
	}

	public NetHttpClient getClient() {
		if (this.client == null)
			this.client = new NetHttpClient();
		return this.client;
	}

	public boolean isLogged() {
		return this.logged;
	}

	public void setLogged(boolean logged) {
		this.logged = logged;
	}

}
