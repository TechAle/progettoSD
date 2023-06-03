package common.RESP.types;

import common.RESP.bodyRESP;

/**
 * @author Alessandro Condello
 * @since 1/06/23
 * @last-modified 03/06/23
 */
public class intRESP extends bodyRESP {

    public int getValue() {
        return (int) this.value;
    }
    public void setValue(int value) {
        this.value = value;
    }
}
