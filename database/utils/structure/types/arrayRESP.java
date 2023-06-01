package database.utils.structure.types;

import database.utils.structure.bodyRESP;

public class arrayRESP extends bodyRESP {

    public bodyRESP getValue() {
         return (bodyRESP)this.value;
    }
    public void setInnerValue(bodyRESP value) {
        this.value = value;
    }
}
