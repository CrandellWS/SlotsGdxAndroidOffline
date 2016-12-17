package mobi.square.slots.api;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.async.AsyncExecutor;

import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import mobi.square.slots.dl.FilesList;
import mobi.square.slots.enums.SlotsType;

public class Connection {

    private static SlotsApi api = null;
    private static Locale locale = null;
    private static AssetManager manager = null;
    private static Set<HandlerInfo> handlers;
    private static AppWrapper wrapper;
    private static I18NBundle i18n;
    private static String session = null;
    private static SlotsType last_screen = null;
    private static AsyncExecutor executor = null;

    // Classes

    public static void initialize() throws IOException {
        if (api == null)
            api = new SlotsApi();
        if (handlers == null) {
            handlers = new HashSet<HandlerInfo>();
        } else handlers.clear();
    }

    // Public Methods

    public static void addHandler(HttpRequest request, HttpResponseListener handler) {
        getHandlers();
        synchronized (handlers) {
            handlers.add(new HandlerInfo(request, handler));
        }
    }

    public static void removeHandler(HttpResponseListener request_handler) {
        getHandlers();
        //System.out.println("removeHandler");
        synchronized (handlers) {
            HandlerInfo found = null;
            for (HandlerInfo handler : handlers) {
                if (handler.handler == request_handler) {
                    found = handler;
                    break;
                }
            }
            if (found != null) {
                handlers.remove(found);
            }
        }
    }

    public static void cancelRequest(HttpResponseListener request_handler) {
        getHandlers();
        //System.out.println("cancelRequest");
        synchronized (handlers) {
            HandlerInfo found = null;
            for (HandlerInfo handler : handlers) {
                if (handler == request_handler) {
                    found = handler;
                    break;
                }
            }
            if (found != null) {
                getInstance().getClient().cancelHttpRequest(found.request);
                handlers.remove(found);
            }
        }
    }

    public static void cancelRequests() {
        getHandlers();
        //System.out.println("cancelRequests");
        synchronized (handlers) {
            for (HandlerInfo handler : handlers) {
                getInstance().getClient().cancelHttpRequest(handler.request);
            }
            handlers.clear();
        }
    }

    public static void checkSession(HttpResponse httpResponse) {
        String cookieString = httpResponse.getHeader("Set-Cookie");
        if (cookieString != null) {
            String[] keyValPairs = cookieString.split("; ?");
            for (String encodedPair : keyValPairs) {
                String keyVal[] = encodedPair.split("=");
                if (keyVal[0].equalsIgnoreCase("JSESSIONID")) {
                    Connection.session = keyVal[1];
                }
            }
        }
    }

    public static void setSession(HttpRequest httpRequest) {
        if (session != null) {
            String cookie = "JSESSIONID=" + session;
            httpRequest.setHeader("Cookie", cookie);
        }
    }

    private static Set<HandlerInfo> getHandlers() {
        if (handlers == null)
            handlers = new HashSet<HandlerInfo>();
        return handlers;
    }

    public static void dispose() {
        cancelRequests();
        getInstance().close();
        if (manager != null) {
            manager.clear();
        }
    }

    public static AsyncExecutor getExecutor() {
        if (executor == null)
            executor = new AsyncExecutor(1);
        return executor;
    }

    public static AssetManager getManager() {
        if (manager == null)
            manager = new AssetManager(new FileHandleResolver() {
                @Override
                public FileHandle resolve(String fileName) {
                    FileHandle file = Gdx.files.external(FilesList.INSTALL_PATH.concat(fileName));
                    if (file == null || !file.exists()) {
                        return Gdx.files.internal(fileName);
                    }
                    return file;
                }
            });
        return manager;
    }

    public static SlotsApi getInstance() {
        if (api == null)
            api = new SlotsApi();
        return api;
    }

    public static AppWrapper getWrapper() {
        return wrapper;
    }

    public static void setWrapper(AppWrapper app_wrapper) {
        wrapper = app_wrapper;
        if (app_wrapper != null) {
            getInstance().setSoundOn(app_wrapper.readSoundState());
            getInstance().setNotificationsOn(app_wrapper.readNotificationState());
        }
    }

    public static String getString(String key) {
        if (i18n == null)
            i18n = getManager().get("i18n/message", I18NBundle.class);
        if (i18n != null) {
            return i18n.get(key);
        } else return "";
    }

    public static I18NBundle getDefaultI18N() {
        if (i18n == null)
            i18n = getManager().get("i18n/message", I18NBundle.class);
        return i18n;
    }

    public static Locale getLocale() {
        if (locale == null) {
            Locale def = Locale.getDefault();
            if (def.getLanguage().equals("ru")) {
                locale = new Locale("ru", "RU");
            } else locale = new Locale("en", "US");
        }
        return locale;
    }

    public static String getTextureLanguage() {
        Locale def = getLocale();
        if (def.getLanguage().equals("ru")) {
            return "ru";
        } else return "en";
    }

    public static String getTextureLanguage(String name) {
        if (name == null) name = "";
        return name.concat("_").concat(getTextureLanguage());
    }

    public static SlotsType getLastScreen() {
        return last_screen;
    }

    public static void setLastScreen(SlotsType screen) {
        last_screen = screen;
    }

    public static class HandlerInfo {
        HttpRequest request;
        HttpResponseListener handler;

        public HandlerInfo(HttpRequest request, HttpResponseListener handler) {
            this.request = request;
            this.handler = handler;
        }
    }

}
