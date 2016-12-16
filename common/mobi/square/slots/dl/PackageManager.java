package mobi.square.slots.dl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import mobi.square.slots.api.Connection;
import mobi.square.slots.dl.InstallManager.ProgressHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.async.AsyncTask;

public class PackageManager {

	private static final int BUFFER_SIZE = 49152;

	ExtractTask extract(String filename, ProgressHandler handler) {
		ExtractTask task = new ExtractTask(filename, handler);
		Connection.getExecutor().submit(task);
		return task;
	}

	class ExtractTask implements AsyncTask<Integer> {
		
		private final String filename;
		private final ProgressHandler handler;
		private boolean cancelled;
		
		public ExtractTask(String filename, ProgressHandler handler) {
			this.filename = filename;
			this.handler = handler;
			this.cancelled = false;
		}
		
		@Override
		public Integer call() throws Exception {
			if (this.filename == null || this.filename.isEmpty()) {
				if (this.handler != null)
					this.handler.cancelled();
				return Integer.valueOf(1);
			}
			FileHandle file = Gdx.files.external(FilesList.INSTALL_PATH.concat(this.filename));
			if (!file.exists()) {
				if (this.handler != null)
					this.handler.cancelled();
				return Integer.valueOf(2);
			}
			long total = 0L;
			long file_length = file.length();
			BufferedInputStream input = file.read(BUFFER_SIZE);
			try {
				byte[] data = new byte[BUFFER_SIZE];
				byte[] b_data = new byte[1];
				byte[] i_data = new byte[4];
				if (input.read(data, 0, 4) != 4)
					throw new IOException();
				total += 4L;
				String header = new String(data, 0, 4);
				if (!header.equals("%SSP")) {
					input.close();
					if (this.handler != null)
						this.handler.cancelled();
					return Integer.valueOf(4);
				}
				if (input.read(i_data, 0, 4) != 4)
					throw new IOException();
				total += 4L;
				int count = ByteBuffer.wrap(i_data).getInt();
				if (input.read(b_data, 0, 1) != 1)
					throw new IOException();
				total += 1L;
				int value = (int)ByteBuffer.wrap(b_data).get();
				if (input.read(data, 0, value) != value)
					throw new IOException();
				total += (long)value;
				String folder_name = new String(data, 0, value);
				Gdx.files.external(FilesList.INSTALL_PATH.concat(folder_name)).mkdirs();
				File folder = new File(folder_name);
				folder.mkdir();
				for (int i = 0; i < count; i++) {
					if (input.read(b_data, 0, 1) != 1)
						throw new IOException();
					total += 1L;
					value = (int)ByteBuffer.wrap(b_data).get();
					if (input.read(data, 0, value) != value)
						throw new IOException();
					total += (long)value;
					String file_name = new String(data, 0, value);
					if (input.read(i_data, 0, 4) != 4)
						throw new IOException();
					total += 4L;
					int len = ByteBuffer.wrap(i_data).getInt();
					file_name = FilesList.INSTALL_PATH.concat(folder_name).concat("/").concat(file_name);
					FileHandle output = Gdx.files.external(file_name);
					output.delete();
					int current = 0;
					int steps = len / BUFFER_SIZE;
					if (len % BUFFER_SIZE != 0) steps++;
					for (int j = 0; j < steps; j++) {
						if (this.cancelled) {
							input.close();
							if (this.handler != null)
								this.handler.cancelled();
							return Integer.valueOf(0);
						}
						int read = len - current;
						if (read > BUFFER_SIZE)
							read = BUFFER_SIZE;
						if (input.read(data, 0, read) != read)
							throw new IOException();
						output.writeBytes(data, 0, read, true);
						current += read;
						total += (long)read;
						if (this.handler != null) {
							this.handler.progress((float)total / (float)file_length * 100f);
						}
					}
				}
			} catch (Exception e) {
				input.close();
				if (this.handler != null)
					this.handler.cancelled();
				return Integer.valueOf(3);
			}
			input.close();
			file.delete();
			if (this.handler != null)
				this.handler.completed();
			return Integer.valueOf(0);
		}
		
		public void cancel() {
			this.cancelled = true;
		}
	}

}
