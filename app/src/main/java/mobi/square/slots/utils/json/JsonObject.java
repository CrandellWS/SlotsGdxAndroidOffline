package mobi.square.slots.utils.json;

import org.json.JSONException;
import org.json.JSONObject;

import mobi.square.slots.error.StringCodeException;

public class JsonObject {

    private String source = null;
    private JSONObject json = null;

    // Constructors

    public JsonObject(String json) {
        this.source = json;
    }

    public JsonObject(JSONObject object) {
        this.source = object.toString();
    }

    // Public Methods

    public boolean isNull(String name) throws StringCodeException {
        return this.getJson().isNull(name);
    }

    public JsonObject getJsonObject(String name) throws StringCodeException {
        try {
            return new JsonObject(this.getJson().getJSONObject(name));
        } catch (JSONException e) {
            throw new StringCodeException("json_error");
        } catch (StringCodeException e) {
            throw e;
        }
    }

    public JsonArray getJsonArray(String name) throws StringCodeException {
        try {
            return new JsonArray(this.getJson().getJSONArray(name));
        } catch (JSONException e) {
            throw new StringCodeException("json_error");
        } catch (StringCodeException e) {
            throw e;
        }
    }

    public String getString(String name) throws StringCodeException {
        try {
            return this.getJson().getString(name);
        } catch (JSONException e) {
            throw new StringCodeException("json_error");
        } catch (StringCodeException e) {
            throw e;
        }
    }

    public boolean getBoolean(String name) throws StringCodeException {
        try {
            return this.getJson().getBoolean(name);
        } catch (JSONException e) {
            throw new StringCodeException("json_error");
        } catch (StringCodeException e) {
            throw e;
        }
    }

    public boolean getBoolean(String name, boolean def) throws StringCodeException {
        try {
            return this.getJson().has(name) ? this.getJson().getBoolean(name) : def;
        } catch (JSONException e) {
            throw new StringCodeException("json_error");
        } catch (StringCodeException e) {
            throw e;
        }
    }

    public int getInt(String name) throws StringCodeException {
        try {
            return this.getJson().getInt(name);
        } catch (JSONException e) {
            throw new StringCodeException("json_error");
        } catch (StringCodeException e) {
            throw e;
        }
    }

    public int getInt(String name, int def) throws StringCodeException {
        try {
            return this.getJson().has(name) ? this.getJson().getInt(name) : def;
        } catch (JSONException e) {
            throw new StringCodeException("json_error");
        } catch (StringCodeException e) {
            throw e;
        }
    }

    @Override
    public String toString() {
        if (this.source == null)
            this.source = (this.json != null) ? this.json.toString() : "{}";
        return this.source;
    }

    // Getters & Setters

    private JSONObject getJson() throws StringCodeException {
        if (this.json == null) {
            try {
                this.json = new JSONObject(this.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                System.out.println(this.toString());
                throw new StringCodeException("json_error");
            }
        }
        return this.json;
    }

}
