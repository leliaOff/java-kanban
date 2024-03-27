package config;

import enums.Mode;

public class App {
    private static final Mode mode = Mode.FILE;
    private static final String taskFilename = "storage/tasks.csv";
    private static final String historyFilename = "storage/history.csv";

    private static boolean testing = false;

    public static Mode getMode() {
        return testing ? config.tests.App.getMode() : mode;
    }

    public static String getTaskFilename() {
        return testing ? config.tests.App.getTaskFilename() : taskFilename;
    }

    public static String getHistoryFilename() {
        return testing ? config.tests.App.getHistoryFilename() : historyFilename;
    }

    public static void test() {
        testing = true;
    }

    public static void product() {
        testing = false;
    }
}
