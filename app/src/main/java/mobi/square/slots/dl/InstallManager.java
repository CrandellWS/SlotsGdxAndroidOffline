package mobi.square.slots.dl;

import mobi.square.slots.dl.DownloadManager.DownloadTask;
import mobi.square.slots.dl.PackageManager.ExtractTask;
import mobi.square.slots.enums.SlotsType;

public class InstallManager {

    private final String filename;
    private final InstallHandler handler;
    private DownloadTask downloader;
    private ExtractTask extractor;
    private final ProgressHandler package_handler = new ProgressHandler() {

        @Override
        public void progress(float percent) {
            if (InstallManager.this.handler != null) {
                InstallManager.this.handler.progress(percent);
            }
        }

        @Override
        public void completed() {
            InstallManager.this.extractor = null;
            if (InstallManager.this.handler != null) {
                InstallManager.this.handler.extract_completed();
            }
        }

        @Override
        public void cancelled() {
            if (InstallManager.this.handler != null) {
                InstallManager.this.handler.extract_cancelled();
            }
        }
    };
    private final ProgressHandler download_handler = new ProgressHandler() {

        @Override
        public void progress(float percent) {
            if (InstallManager.this.handler != null) {
                InstallManager.this.handler.progress(percent);
            }
        }

        @Override
        public void completed() {
            InstallManager.this.downloader = null;
            if (InstallManager.this.handler != null)
                InstallManager.this.handler.download_completed();
            InstallManager.this.extractor = new PackageManager().extract(InstallManager.this.filename, InstallManager.this.package_handler);
            if (InstallManager.this.handler != null) {
                InstallManager.this.handler.extract_started();
            }
        }

        @Override
        public void cancelled() {
            if (InstallManager.this.handler != null) {
                InstallManager.this.handler.download_cancelled();
            }
        }
    };

    public InstallManager(String filename, InstallHandler handler) {
        this.filename = filename;
        this.handler = handler;
        this.downloader = null;
        this.extractor = null;
    }

    public static InstallManager install(SlotsType type, InstallHandler handler) {
        String filename = FilesList.getFileName(type);
        if (filename == null) {
            if (handler != null)
                handler.download_cancelled();
            return null;
        }
        InstallManager manager = new InstallManager(filename, handler);
        manager.downloader = new DownloadManager().download(filename, manager.download_handler);
        if (handler != null)
            handler.download_started();
        return manager;
    }

    public void cancel() {
        if (this.downloader != null) {
            this.downloader.cancel();
        }
        if (this.extractor != null) {
            this.extractor.cancel();
        }
    }

    interface ProgressHandler {
        void completed();

        void cancelled();

        void progress(float percent);
    }

    public interface InstallHandler {
        void download_started();

        void download_completed();

        void download_cancelled();

        void extract_started();

        void extract_completed();

        void extract_cancelled();

        void progress(float percent);
    }

}
