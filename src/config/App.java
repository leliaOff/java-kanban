package config;

import enums.Mode;

public class App {

    /**
     * Режим работы
     */
    private static final Mode mode = Mode.FILE;

    /**
     * Файл - список задач
     */
    private static final String taskFilename = "storage/tasks.csv";

    /**
     * Файл - история задачника
     */
    private static final String historyFilename = "storage/history.csv";

    /**
     * Получить режим работы
     *
     * @return Режим работы
     */
    public static Mode getMode() {
        return mode;
    }

    /**
     * Получить путь к файлу с задачами
     *
     * @return Режим работы
     */
    public static String getTaskFilename() {
        return taskFilename;
    }

    /**
     * Получить путь к файлу с историей задачника
     *
     * @return Режим работы
     */
    public static String getHistoryFilename() {
        return historyFilename;
    }
}
