package it.unimib.finalproject.database.utils.RESP.types;


import it.unimib.finalproject.database.utils.RESP.bodyRESP;

/**
 * @author Alessandro Condello
 * @since 1/06/23
 * @last-modified 03/06/23
 */
public class arrayRESP extends bodyRESP {

    public bodyRESP getValue() {
         return (bodyRESP)this.value;
    }
    public void setInnerValue(bodyRESP value) {
        this.value = value;
    }
}
