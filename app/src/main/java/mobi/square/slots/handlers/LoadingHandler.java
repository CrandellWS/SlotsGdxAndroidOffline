package mobi.square.slots.handlers;

import com.badlogic.gdx.Net.HttpResponse;

import mobi.square.slots.api.Connection;

public class LoadingHandler implements CancelableHandler {

    public final AsyncJsonHandler handler;
    private String json;
    private boolean loading_completed;

    public LoadingHandler() {
        this.handler = new AsyncJsonHandler() {
            @Override
            public void onCompleted(String json) {
            }
        };
        this.loading_completed = false;
        this.json = null;
    }

    public LoadingHandler(AsyncJsonHandler handler) {
        this.handler = handler;
        this.loading_completed = false;
        this.json = null;
    }

    @Override
    public void handleHttpResponse(String response) {
        this.json = response == null ? "{\"error\":105}" : response;
        if (this.loading_completed && this.handler != null) {
            Connection.removeHandler(this);
            this.handler.onCompleted(this.json);
        }
    }

    @Override
    public void handleHttpResponse(HttpResponse httpResponse) {
        Connection.checkSession(httpResponse);
        this.json = httpResponse == null ? "{\"error\":105}" : httpResponse.getResultAsString();
        if (this.loading_completed && this.handler != null) {
            Connection.removeHandler(this);
            this.handler.onCompleted(this.json);
        }
    }

    @Override
    public void failed(Throwable t) {
        if (this.handler != null) {
            this.handler.failed(t);
        }
    }

    @Override
    public void cancelled() {
        if (this.handler != null) {
            this.handler.cancel();
        }
    }

    /**
     * Получен ли ответ от сервера.
     *
     * @return true/false
     */
    public boolean isLoaded() {
        return this.json != null;
    }

    /**
     * Сообщить обработчику, что загрузка завершена.<br>
     * Вложенный обработчик будет вызван после получения ответа от сервера.
     */
    public void complete() {
        if (!this.loading_completed) {
            this.loading_completed = true;
            if (this.json != null && this.handler != null) {
                Connection.removeHandler(this);
                this.handler.onCompleted(this.json);
            }
        }
    }

    /**
     * Возвращает тело ответа сервера, если он был получен.
     *
     * @return String
     */
    public String getJson() {
        if (this.json == null) {
            return "{\"error\":105}";
        } else return this.json;
    }

    /**
     * Отменить обработку событий.
     */
    @Override
    public void cancel() {
        if (this.handler != null) {
            this.handler.cancel();
        }
    }

}
