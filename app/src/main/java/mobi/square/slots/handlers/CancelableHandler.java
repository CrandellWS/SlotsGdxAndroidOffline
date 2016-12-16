package mobi.square.slots.handlers;

import com.badlogic.gdx.Net.HttpResponseListener;

public interface CancelableHandler extends HttpResponseListener {

    void cancel();

    void handleHttpResponse(String response);

}
