package mobi.square.slots.tools;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AtlasLoader {

    private static final Map<String, TextureAtlas> atlas;
    private static final Set<String> dispose_queue;

    static {
        atlas = new HashMap<String, TextureAtlas>();
        dispose_queue = new HashSet<String>();
    }

    public static TextureAtlas get(String internal_pack_name) {
        synchronized (dispose_queue) {
            dispose_queue.remove(internal_pack_name);
        }
        TextureAtlas result;
        synchronized (atlas) {
            result = atlas.get(internal_pack_name);
            if (result == null) {
                result = new TextureAtlas(internal_pack_name);
                atlas.put(internal_pack_name, result);
            }
        }
        return result;
    }

    public static void prepareDispose(String internal_pack_name) {
        synchronized (dispose_queue) {
            dispose_queue.add(internal_pack_name);
        }
    }

    public static void disposePrepared() {
        synchronized (dispose_queue) {
            for (String name : dispose_queue) {
                TextureAtlas object = atlas.get(name);
                if (object != null) {
                    atlas.remove(name);
                    object.dispose();
                }
            }
            dispose_queue.clear();
        }
    }

    public static void dispose(String internal_pack_name) {
        synchronized (dispose_queue) {
            TextureAtlas object = atlas.get(internal_pack_name);
            if (object != null) {
                atlas.remove(internal_pack_name);
                object.dispose();
            }
        }
    }

    public static void disposeAll() {
        synchronized (dispose_queue) {
            dispose_queue.clear();
        }
        synchronized (atlas) {
            Set<String> keys = atlas.keySet();
            for (String key : keys) {
                atlas.get(key).dispose();
            }
            atlas.clear();
        }
    }

}
