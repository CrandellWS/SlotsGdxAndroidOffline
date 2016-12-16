package mobi.square.slots.handlers;

import com.badlogic.gdx.Net.HttpResponseListener;

public interface CancelableHandler extends HttpResponseListener {

	public void cancel();

	public void handleHttpResponse(String response);

}
