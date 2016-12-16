package mobi.square.slots.utils;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class utils {

    public static int idPurchase = 0;
    public static int maxSpinToLock = 0;
    public static int numberSpinDone = 0;
    public static int maxBetToLock = 0;
    public static int accumalatedBet = 0;
    public static boolean gameUnlocked = false;
    private static Random random = new Random();
    private static MersenneTwisterFast mtf = new MersenneTwisterFast();

    public static String file_get_contents(String url) {

        byte buf[] = new byte[4096];
        URL url_object;
        try {
            url_object = new URL(url);
        } catch (MalformedURLException e) {
            return "";
        }
        BufferedInputStream input;
        try {
            input = new BufferedInputStream(url_object.openStream(), 8192);
        } catch (IOException e) {
            return "";
        }
        StringBuilder output = new StringBuilder("");

        Integer readBytes = 0;
        int max_reads = 512; // 2MB

        try {
            while ((readBytes = input.read(buf)) != -1 && max_reads > 0) {
                output.append(new String(buf, 0, readBytes));
                max_reads--;
            }
        } catch (IOException e) {
            return "";
        } finally {
            try {
                input.close();
            } catch (IOException e) {
                return "";
            }
        }

        return output.toString();
    }

    public static String UtfToIso(String text) {
        try {
            return new String(text.getBytes("UTF-8"), "ISO-8859-1");
        } catch (Exception e) {
            return text;
        }
    }

    public static String IsoToUtf(String text) {
        try {
            return new String(text.getBytes("ISO-8859-1"), "UTF-8");
        } catch (Exception e) {
            return text;
        }
    }

    public static String IsoToAscii(String text) {
        try {
            return new String(text.getBytes("ISO-8859-1"), "ASCII");
        } catch (Exception e) {
            return text;
        }
    }

    public static String stripNull(String value) {
        if (value == null)
            return "";
        else return value;
    }

    /**
     * Возвращает текущее время в секундах.
     *
     * @return int timestamp
     */
    public static int getTimestamp() {
        return (int) (System.currentTimeMillis() / 1000L);
    }

    /**
     * Разделяет число пробелами на группы.<br>
     * Работает только с положительными числами.
     *
     * @param value - положительное число;
     * @param group - количество цифр в группе.
     * @return String
     */
    public static String splitNumber(int value, int group) {
        if (value == 0) return "0";
        boolean negative = false;
        if (value < 0) {
            negative = true;
            value = -value;
        }
        StringBuilder result = new StringBuilder();
        int current = 0;
        while (value > 0) {
            if (++current > group) {
                result.insert(0, " ");
                current = 1;
            }
            int i = value % 10;
            value /= 10;
            result.insert(0, i);
        }
        if (negative) result.insert(0, "-");
        return result.toString();
    }

    public static void saveData(String register, String data, Context mContex) {
        FileOutputStream fos = null;
        try {
            fos = mContex.openFileOutput(register, 0);
            DataOutputStream dos = new DataOutputStream(fos);
            dos.writeUTF(data);
            dos.flush();
            dos.close();
        } catch (Exception e) {
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception exc) {
            }
        }
    }

    public static String loadData(String register, Context mContex) {
        String info = "-1";

        FileInputStream fis = null;

        try {
            fis = mContex.openFileInput(register);
            DataInputStream dis = new DataInputStream(fis);
            info = dis.readUTF();
        } catch (Exception e) {
            return null;
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception exc) {
            }
        }

        if (info.equals("-1")) {
            return null;
        }

        return info;
    }

    /**
     * Поиск строки в массиве.
     *
     * @param strings - массив строк;
     * @param target  - строка для поиска.
     * @return true/false - результат поиска
     */
    public static boolean contains(String[] strings, String target) {
        if (target == null) return false;
        for (int i = strings.length - 1; i >= 0; i--) {
            if (strings[i] != null) {
                if (strings[i].equals(target)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Поиск числа в массиве.
     *
     * @param array  - массив чисел;
     * @param target - искомое число.
     * @return true/false
     */
    public static boolean contains(int[] array, int target) {
        for (int i = array.length - 1; i >= 0; i--) {
            if (array[i] == target) {
                return true;
            }
        }
        return false;
    }

    /**
     * Поиск объекта в массиве.<br>
     * Для сравнения используется оператор <b>==</b>.
     *
     * @param array  - массив объектов;
     * @param target - искомый объект.
     * @return true/false
     */
    public static <Type> boolean contains(Type[] array, Type target) {
        for (int i = array.length - 1; i >= 0; i--) {
            if (array[i] == target) {
                return true;
            }
        }
        return false;
    }


    /**
     * Упаковывает массив в строку через разделитель.
     *
     * @param strings   - массив строк;
     * @param separator - разделитель.
     * @return String
     */
    public static String implode(String[] strings, String separator) {
        if (strings == null) return "";
        if (strings.length < 1) return "";
        StringBuilder result = new StringBuilder(strings[0]);
        for (int i = 1; i < strings.length; i++) {
            result.append(separator);
            result.append(strings[i]);
        }
        return result.toString();
    }

    /**
     * Упаковывает массив в строку через разделитель.
     *
     * @param strings   - список строк;
     * @param separator - разделитель.
     * @return String
     */
    public static String implode(List<String> strings, String separator) {
        if (strings == null) return "";
        if (strings.size() < 1) return "";
        StringBuilder result = new StringBuilder(strings.get(0));
        for (int i = 1; i < strings.size(); i++) {
            result.append(separator);
            result.append(strings.get(i));
        }
        return result.toString();
    }

    public static String implode(HashMap<String, String> strings, String sep_pair, String separator) {
        if (strings == null) return "";
        if (strings.size() < 1) return "";
        String[] pairs = new String[strings.size()];
        StringBuilder result = new StringBuilder("");
        Iterator<String> iterator = strings.keySet().iterator();
        String key;
        Integer current = 0;
        while (iterator.hasNext()) {
            key = iterator.next();
            result = new StringBuilder("");
            result.append(key);
            result.append(sep_pair);
            result.append(strings.get(key));
            pairs[current++] = result.toString();
        }
        return implode(pairs, separator);
    }

    public static String getHash(String plaintext) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return "NULL MD5";
        }
        md.reset();
        md.update(plaintext.getBytes());
        byte[] digest = md.digest();
        BigInteger bigInt = new BigInteger(1, digest);
        String hashtext = bigInt.toString(16);
        while (hashtext.length() < 32) {
            hashtext = "0" + hashtext;
        }
        return hashtext;
    }

    public static final byte[] intToByteArray(int value) {
        return new byte[]{
                (byte) (value >>> 24),
                (byte) (value >>> 16),
                (byte) (value >>> 8),
                (byte) (value)
        };
    }

    public static final int byteArrayToInt(byte[] arr) {
        return (arr[0] << 24)
                + ((arr[1] & 0xFF) << 16)
                + ((arr[2] & 0xFF) << 8)
                + (arr[3] & 0xFF);
    }

    /**
     * Возведение целочисленного числа в степень
     *
     * @param a - число возводимое в степень
     * @param b - степень
     * @return int
     */
    public static int pow(int a, int b) {
        if (b < 0) return 0;
        if (b == 0) return 1;
        int result = a;
        for (int i = 1; i < b; i++)
            result *= a;
        return result;
    }

    /**
     * Возвращает случайное число с плавающей точкой в диапазоне от 0.0 (включая) до 1.0 (исключая)
     *
     * @return double
     */
    public static double getRandomDouble() {
        return utils.random.nextDouble();
    }

    /**
     * Возвращает случайное булево значение.<br>
     * Используется MTF.
     *
     * @param chance - шанс
     * @return true/false
     */
    public static boolean getRandomBoolean(double chance) {
        if (chance < 0.0) return false;
        if (chance > 1.0) return true;
        return utils.mtf.nextBoolean(chance);
    }

    /**
     * Возвращает случайное число с плавающей точкой в диапазоне от 0.0 (включая) до 1.0 (исключая).<br>
     * Используется MTF.
     *
     * @return double
     */
    public static double getRandomDoubleMTF() {
        return utils.mtf.nextDouble();
    }

    /**
     * Возвращает случайное целое число от 0 (включая) до max (исключая)
     *
     * @param max - максимальное число
     * @return int
     */
    public static int getRandom(int max) {
        return utils.random.nextInt(max);
    }

    /**
     * Возвращает случайное целое число от min (включая) до max (включая)
     *
     * @param min - минимальное число
     * @param max - максимальное число
     * @return int
     */
    public static int getRandom(int min, int max) {
        int rnd = utils.random.nextInt(max - min + 1);
        return (rnd + min);
    }

    /**
     * Возвращает случайное целое число от 0 (включая) до max (исключая)
     *
     * @param max - максимальное число
     * @return int
     */
    public static int getRandomInt(int max) {
        if (max < 1) max = 1;
        return utils.random.nextInt(max);
    }

    public static int getRandomIntMTF(int max) {
        if (max < 1) max = 1;
        return utils.mtf.nextInt(max);
    }

    /**
     * Возвращает случайное целое число от min (включая) до max (исключая)
     *
     * @param min - минимальное число
     * @param max - максимальное число
     * @return int
     */
    public static int getRandomInt(int min, int max) {
        if (min < 0) min = 0;
        if (max < min) max = min;
        if (max < 1) max = 1;
        int rnd = utils.random.nextInt(max - min);
        return (rnd + min);
    }

    /**
     * Округляет число с плавающей точкой
     *
     * @param value - число
     * @param size  - количество разрядов
     * @return double
     */
    public static double getRound(double value, int size) {
        BigDecimal dec = new BigDecimal(value);
        return dec.setScale(size, RoundingMode.HALF_UP).doubleValue();
    }

    public static int getRound(double value, double threshold) {
        double check = value - (double) (int) value;
        return (check >= threshold) ? (int) value + 1 : (int) value;
    }

}
