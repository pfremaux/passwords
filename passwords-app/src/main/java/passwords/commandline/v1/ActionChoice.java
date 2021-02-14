package passwords.commandline.v1;

import java.util.HashMap;
import java.util.Map;

public enum ActionChoice {
    LIST(1),
    DETAILS(2),
    ADD(3),
    DELETE(4),
    SAVE(5),
    MOVE(6),
    ADD_DIR(7),
    EXIT(9);
    private static Map<Integer, ActionChoice> actionsPerChoice;

    static {
        actionsPerChoice = new HashMap<>();
        for (ActionChoice value : values()) {
            ActionChoice.actionsPerChoice.put(value.getChoice(), value);
        }
    }

    private int choice;

    ActionChoice(int choice) {
        this.choice = choice;
    }

    public static ActionChoice findByChoice(Integer choice) {
        return actionsPerChoice.getOrDefault(choice, null);
    }

    public int getChoice() {
        return choice;
    }
}
