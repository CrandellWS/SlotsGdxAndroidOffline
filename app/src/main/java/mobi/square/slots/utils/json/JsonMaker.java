package mobi.square.slots.utils.json;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class JsonMaker {

    public static String make(HashMap<String, JsonNode> data) {
        if (data == null) return "{}";
        if (data.size() < 1) return "{}";
        StringBuilder json = new StringBuilder("{");
        Iterator<String> iterator = data.keySet().iterator();
        String key = iterator.next();
        json.append("\"").append(key).append("\":");
        json.append(data.get(key));
        while (iterator.hasNext()) {
            key = iterator.next();
            json.append(",\"").append(key).append("\":");
            json.append(data.get(key).toString());
        }
        return json.append("}").toString();
    }

    public static String make(List<JsonNode> data) {
        if (data == null) return "[]";
        if (data.size() < 1) return "[]";
        StringBuilder json = new StringBuilder("[");
        json.append(data.get(0).toString());
        for (int i = 1; i < data.size(); i++)
            json.append(",").append(data.get(i).toString());
        return json.append("]").toString();
    }

    public static String make(int[] data) {
        if (data == null) return "[]";
        if (data.length < 1) return "[]";
        StringBuilder json = new StringBuilder("[");
        json.append(data[0]);
        for (int i = 1; i < data.length; json.append(",").append(data[i++])) ;
        return json.append("]").toString();
    }

    public static String make(String[] data) {
        if (data == null) return "[]";
        if (data.length < 1) return "[]";
        StringBuilder json = new StringBuilder("[");
        json.append("\"").append(data[0]).append("\"");
        for (int i = 1; i < data.length; json.append(",\"").append(data[i++]).append("\"")) ;
        return json.append("]").toString();
    }

}
