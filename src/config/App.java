package config;

import enums.Mode;

public class App {
    private static final Mode mode = Mode.FILE;
    private static final String taskFilename = "storage/tasks.csv";
    private static final String historyFilename = "storage/history.csv";
    public static Mode getMode() {
        return mode;
    }
    public static String getTaskFilename() {
        return taskFilename;
    }
    public static String getHistoryFilename() {
        return historyFilename;
    }
}
