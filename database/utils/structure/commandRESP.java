package database.utils.structure;

public class commandRESP {

    public static String[] allowedActions = new String[] {"VIEW",
            "ADD",
            "DEL"};

    private bodyRESP operations = null;
    private commandRESP next = null;
    private commandRESP prev = null;
    private final String action;


    public commandRESP getNext() {
        return next;
    }
    public commandRESP getPrev() {
        return prev;
    }

    public void setPrev(commandRESP prev) {
        this.prev = prev;
    }

    public void setNext(commandRESP next) {
        this.next = next;
        next.setPrev(this);
    }

    public commandRESP(String action) {
        this.action = action;
    }



    public bodyRESP getOperations() {
        return operations;
    }

    public void setOperations(bodyRESP operations) {
        this.operations = operations;
    }

    public String getAction() {
        return action;
    }
}

