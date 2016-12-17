package mobi.square.slots.dl;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Packer {

    /**
     * <b>Коды ошибок:</b><br>
     * 1 - не передано имя папки;<br>
     * 2 - объект не является папкой;<br>
     * 3 - не удалось создать файл;<br>
     * 4 - ошибка при записи в файл.
     *
     * @param folder - имя папки.
     * @return Код ошибки (int)
     */
    public int pack(String folder) {
        if (folder == null || folder.isEmpty())
            return 1;
        File dir = new File(folder);
        File[] files = dir.listFiles();
        if (files != null) {
            int count = 0;
            for (File file : files) {
                if (file.isFile()) count++;
            }
            DataOutputStream output = null;
            try {
                output = new DataOutputStream(new FileOutputStream(dir.getAbsolutePath().concat(".ssp")));
            } catch (Exception e) {
                return 3;
            }
            try {
                output.write("%SSP".getBytes());
                output.writeInt(count);
                output.writeByte(dir.getName().getBytes().length);
                output.write(dir.getName().getBytes());
                for (File file : files) {
                    if (file.isFile()) {
                        output.writeByte(file.getName().getBytes().length);
                        output.write(file.getName().getBytes());
                        FileInputStream input = new FileInputStream(file);
                        long length = file.length();
                        if (length > 4194304L)
                            throw new IOException();
                        int len = (int) length;
                        output.writeInt(len);
                        byte[] data = new byte[len];
                        if (input.read(data) != len)
                            throw new IOException();
                        output.write(data);
                        input.close();
                    }
                }
                output.close();
            } catch (IOException e) {
                try {
                    output.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                return 4;
            }
        } else {
            // Handle the case where dir is not really a directory.
            // Checking dir.isDirectory() above would not be sufficient
            // to avoid race conditions with another process that deletes
            // directories.
            return 2;
        }
        return 0;
    }

    /**
     * <b>Коды ошибок:</b><br>
     * 1 - не передано имя файла;<br>
     * 2 - файл не существует;<br>
     * 3 - ошибка при чтении файла;<br>
     * 4 - ошибка при записи в файл.
     *
     * @param filename - имя файла.
     * @return Код ошибки (int)
     */
    public int extract(String filename) {
        if (filename == null || filename.isEmpty())
            return 1;
        File file = new File(filename);
        if (!file.exists()) return 2;
        DataInputStream input = null;
        try {
            input = new DataInputStream(new FileInputStream(file));
            byte[] data = new byte[4194304];
            if (input.read(data, 0, 4) != 4)
                throw new IOException();
            int count = input.readInt();
            int value = (int) input.readByte();
            if (input.read(data, 0, value) != value)
                throw new IOException();
            String folder_name = new String(data, 0, value);
            File folder = new File(folder_name);
            folder.mkdir();
            for (int i = 0; i < count; i++) {
                value = (int) input.readByte();
                if (input.read(data, 0, value) != value)
                    throw new IOException();
                String file_name = new String(data, 0, value);
                int len = input.readInt();
                DataOutputStream output = null;
                try {
                    output = new DataOutputStream(new FileOutputStream(folder.getAbsolutePath().concat("/").concat(file_name)));
                } catch (Exception e) {
                    input.close();
                    return 4;
                }
                input.read(data, 0, len);
                output.write(data, 0, len);
                output.close();
            }
            input.close();
        } catch (Exception e) {
            return 3;
        }
        return 0;
    }

}
