package database.structure.RESP.types;

import database.structure.RESP.bodyRESP;

public class intRESP extends bodyRESP {

    public int getValue() {
        return (int) this.value;
    }
    public void setValue(int value) {
        this.value = value;
    }
}
