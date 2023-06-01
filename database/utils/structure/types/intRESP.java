package database.utils.structure.types;

import database.utils.structure.bodyRESP;

public class intRESP extends bodyRESP {

    public int getValue() {
        return (int) this.value;
    }
    public void setValue(int value) {
        this.value = value;
    }
}
