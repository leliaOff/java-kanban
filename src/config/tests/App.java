package config.tests;

import enums.Mode;

public class App extends config.App {
    private static final Mode mode = Mode.FILE;
    private static final String taskFilename = "storage/test/tasks.csv";
    private static final String historyFilename = "storage/test/history.csv";

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
