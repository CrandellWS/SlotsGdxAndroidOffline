package mobi.square.slots.logger;

public class Log {

    public static void log(String message) {
        System.out.println(message);
    }

    public static void log(String message, Throwable t) {
        System.out.println(message);
        t.printStackTrace();
    }

    public static void log(Throwable t) {
        t.printStackTrace();
    }

}
