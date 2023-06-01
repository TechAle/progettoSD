package database.utils.structure;

public abstract class bodyRESP {
    protected Object value;
    private bodyRESP next;
    public boolean error = false;

    public bodyRESP getNext() {
        return next;
    }

    public void setNext(bodyRESP next) {
        this.next = next;
    }

    public void setError(String error) {
        this.error = true;
        this.value = error;
    }
    public String getError() {
        return (String) this.value;
    }
}
