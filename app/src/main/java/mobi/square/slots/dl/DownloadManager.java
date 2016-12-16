package mobi.square.slots.dl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.async.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import mobi.square.slots.api.Connection;
import mobi.square.slots.config.ApiConfig;
import mobi.square.slots.dl.InstallManager.ProgressHandler;

public class DownloadManager {

    DownloadTask download(String filename, ProgressHandler handler) {
        DownloadTask task = new DownloadTask(ApiConfig.DOWNLOAD_URL.concat(filename), filename, handler);
        Connection.getExecutor().submit(task);
        return task;
    }

    class DownloadTask implements AsyncTask<FileHandle> {

        private final String url;
        private final String name;
        private final ProgressHandler handler;
        private boolean cancelled;

        public DownloadTask(String url, String name, ProgressHandler handler) {
            this.url = url;
            this.name = name;
            this.handler = handler;
            this.cancelled = false;
        }

        @Override
        public FileHandle call() {

            // URL
            URL url_object;
            try {
                url_object = new URL(this.url);
            } catch (MalformedURLException e) {
                if (this.handler != null)
                    this.handler.cancelled();
                return null;
            }

            // Connection
            HttpURLConnection connection;
            try {
                connection = (HttpURLConnection) url_object.openConnection();
                connection.connect();
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    if (this.handler != null) {
                        this.handler.cancelled();
                        return null;
                    }
                }
            } catch (Exception e) {
                if (this.handler != null)
                    this.handler.cancelled();
                return null;
            }

            // Stream
            InputStream input;
            int file_length = 0;
            try {
                input = connection.getInputStream();
                file_length = connection.getContentLength();
            } catch (IOException e) {
                if (this.handler != null)
                    this.handler.cancelled();
                return null;
            }

            // File & buffers
            FileHandle output = Gdx.files.external(FilesList.INSTALL_PATH.concat(this.name));
            if (output.exists()) output.delete();
            byte buf[] = new byte[8192];
            int read_bytes = 0, total = 0;
            int max_reads = 8192;

            // Read
            try {
                while ((read_bytes = input.read(buf)) != -1 && max_reads > 0) {
                    if (this.cancelled) {
                        output.delete();
                        input.close();
                        if (this.handler != null)
                            this.handler.cancelled();
                        return null;
                    }
                    output.writeBytes(buf, 0, read_bytes, true);
                    total += read_bytes;
                    if (this.handler != null && file_length != 0) {
                        this.handler.progress((float) total / (float) file_length * 100f);
                    }
                    max_reads--;
                }
            } catch (IOException e) {
                if (this.handler != null)
                    this.handler.cancelled();
                return null;
            } finally {
                try {
                    input.close();
                } catch (IOException e) {
                    if (this.handler != null)
                        this.handler.cancelled();
                    return null;
                }
            }

            // Complete
            if (this.handler != null)
                this.handler.completed();
            return output;
        }

        public void cancel() {
            this.cancelled = true;
        }
    }

}
