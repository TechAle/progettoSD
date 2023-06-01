package database.utils.structure;

public class headRESP {

    public static String[] allowedActions = new String[] {"VIEW",
            "ADD",
            "DEL"};

    private bodyRESP next = null;
    private final String action;
    private final boolean error;

    public headRESP(String action) {
        this.error = action.startsWith("ERROR");
        this.action = action;
    }

    public bodyRESP getNext() {
        return next;
    }

    public void setNext(bodyRESP next) {
        this.next = next;
    }

    public String getAction() {
        return action;
    }
}

