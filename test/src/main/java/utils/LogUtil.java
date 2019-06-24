package utils;

public class LogUtil {
    public static LogUtil getLogger(Class clazz) {
        return getLogger(clazz.getName());
    }

    public static LogUtil getLogger(String name) {
        return new LogUtil(name);
    }

    private String tag;
    private boolean closed = false;
    private LogUtil(String name) {
        this.tag = name;
    }

    public void info(String ...messages) {
        this.log("info", messages);
    }


    public void close() {
        closed = true;
    }

    private void log(String type, String ...messages) {
        if (closed) return;
        System.out.println("【" + this.tag + "】【" + type + "】:");
        for (String msg : messages) {
            System.out.println(msg);
        }
        System.out.println();
    }
}
