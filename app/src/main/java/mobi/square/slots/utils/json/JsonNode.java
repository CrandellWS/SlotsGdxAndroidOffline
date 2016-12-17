package mobi.square.slots.utils.json;

import java.util.HashMap;
import java.util.List;

public class JsonNode {

    private final String json;
    private final boolean quote;

    // Constructors

    public JsonNode(boolean value) {
        this.json = String.valueOf(value);
        this.quote = false;
    }

    public JsonNode(int value) {
        this.json = String.valueOf(value);
        this.quote = false;
    }

    public JsonNode(String value) {
        this.json = value;
        this.quote = true;
    }

    public JsonNode(String value, boolean quote) {
        this.json = value;
        this.quote = quote;
    }

    public JsonNode(int[] values) {
        this.json = JsonMaker.make(values);
        this.quote = false;
    }

    public JsonNode(String[] values) {
        this.json = JsonMaker.make(values);
        this.quote = false;
    }

    public JsonNode(HashMap<String, JsonNode> data) {
        this.json = JsonMaker.make(data);
        this.quote = false;
    }

    public JsonNode(List<JsonNode> data) {
        this.json = JsonMaker.make(data);
        this.quote = false;
    }

    // Getters

    @Override
    public String toString() {
        String result = "";
        if (this.isQuote()) result = "\"";
        result = result.concat(this.json);
        if (this.isQuote())
            result = result.concat("\"");
        return result;
    }

    // Private Methods

    private boolean isQuote() {
        return this.quote;
    }

}
