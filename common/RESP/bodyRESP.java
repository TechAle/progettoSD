package common.RESP;

/**
 * @author Alessandro Condello
 * @since 1/06/23
 * @last-modified 03/06/23
 */
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
