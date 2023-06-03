package database.structure.RESP.types;

import database.structure.RESP.bodyRESP;

public class arrayRESP extends bodyRESP {

    public bodyRESP getValue() {
         return (bodyRESP)this.value;
    }
    public void setInnerValue(bodyRESP value) {
        this.value = value;
    }
}
