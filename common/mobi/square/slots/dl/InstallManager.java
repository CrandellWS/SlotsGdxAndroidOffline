package mobi.square.slots.dl;

import mobi.square.slots.dl.DownloadManager.DownloadTask;
import mobi.square.slots.dl.PackageManager.ExtractTask;
import mobi.square.slots.enums.SlotsType;

public class InstallManager {

	private final String filename;
	private final InstallHandler handler;
	private DownloadTask downloader;
	private ExtractTask extractor;

	public InstallManager(String filename, InstallHandler handler) {
		this.filename = filename;
		this.handler = handler;
		this.downloader = null;
		this.extractor = null;
	}

	interface ProgressHandler {
		public void completed();
		public void cancelled();
		public void progress(float percent);
	}

	public interface InstallHandler {
		public void download_started();
		public void download_completed();
		public void download_cancelled();
		public void extract_started();
		public void extract_completed();
		public void extract_cancelled();
		public void progress(float percent);
	}

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

}
