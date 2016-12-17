package mobi.square.slots.handlers;

import com.badlogic.gdx.Net.HttpResponse;

import mobi.square.slots.api.Connection;

public abstract class AsyncJsonHandler implements CancelableHandler {

    private Boolean cancelled;

    public AsyncJsonHandler() {
        this.cancelled = false;
    }

    @Override
    public void handleHttpResponse(String response) {
        synchronized (this.cancelled) {
            String json = this.cancelled ? "{\"error\":51}" : response;
            Connection.removeHandler(this);
            this.onCompleted(json);
        }
    }

    @Override
    public void handleHttpResponse(HttpResponse httpResponse) {
        Connection.checkSession(httpResponse);
        synchronized (this.cancelled) {
            String json = this.cancelled ? "{\"error\":51}" : httpResponse.getResultAsString();
            Connection.removeHandler(this);
            this.onCompleted(json);
        }
    }

    @Override
    public void failed(Throwable t) {
        synchronized (this.cancelled) {
            if (this.cancelled) return;
        }
    }

    @Override
    public void cancelled() {
        synchronized (this.cancelled) {
            this.cancelled = true;
        }
    }

    @Override
    public void cancel() {
        synchronized (this.cancelled) {
            this.cancelled = true;
        }
    }

    public abstract void onCompleted(String json);

}
