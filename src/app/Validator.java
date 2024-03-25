package app;

import java.util.ArrayList;

public class Validator {
    private final ArrayList<String> messages;

    public Validator() {
        messages = new ArrayList<>();
    }

    public Validator(String message) {
        messages = new ArrayList<>();
        messages.add(message);
    }

    public boolean isValid() {
        return messages.isEmpty();
    }

    public ArrayList<String> getMessages() {
        return messages;
    }

    public void add(String message) {
        messages.add(message);
    }
}
