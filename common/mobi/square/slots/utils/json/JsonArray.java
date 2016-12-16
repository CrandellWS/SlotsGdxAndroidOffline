package mobi.square.slots.utils.json;

import mobi.square.slots.error.StringCodeException;

import org.json.JSONArray;
import org.json.JSONException;

public class JsonArray {
	
	private String source = null;
	private JSONArray json = null;
	
	// Constructors
	
	public JsonArray(String json) {
		this.source = json;
	}
	
	public JsonArray(JSONArray array) {
		this.json = array;
	}
	
	// Public Methods
	
	public JsonObject getJsonObject(int index) throws StringCodeException {
		try {
			return new JsonObject(this.getJson().getJSONObject(index));
		} catch (JSONException e) {
			throw new StringCodeException("json_error");
		} catch (StringCodeException e) {
			throw e;
		}
	}
	
	public JsonArray getJsonArray(int index) throws StringCodeException {
		try {
			return new JsonArray(this.getJson().getJSONArray(index));
		} catch (JSONException e) {
			throw new StringCodeException("json_error");
		} catch (StringCodeException e) {
			throw e;
		}
	}
	
	public String getString(int index) throws StringCodeException {
		try {
			return this.getJson().getString(index);
		} catch (JSONException e) {
			throw new StringCodeException("json_error");
		} catch (StringCodeException e) {
			throw e;
		}
	}
	
	public boolean getBoolean(int index) throws StringCodeException {
		try {
			return this.getJson().getBoolean(index);
		} catch (JSONException e) {
			throw new StringCodeException("json_error");
		} catch (StringCodeException e) {
			throw e;
		}
	}
	
	public int getInt(int index) throws StringCodeException {
		try {
			return this.getJson().getInt(index);
		} catch (JSONException e) {
			throw new StringCodeException("json_error");
		} catch (StringCodeException e) {
			throw e;
		}
	}
	
	public int length() throws StringCodeException {
		return this.getJson().length();
	}
	
	@Override
	public String toString() {
		if (this.source == null)
			this.source = (this.json != null) ? this.json.toString() : "{}";
		return this.source;
	}
	
	// Getters & Setters

	private JSONArray getJson() throws StringCodeException {
		if (this.json == null) {
			try {
				this.json = new JSONArray(this.toString());
			} catch (JSONException e) {
				throw new StringCodeException("json_error");
			}
		}
		return this.json;
	}

}
