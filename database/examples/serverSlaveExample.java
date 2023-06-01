package database.examples;

import database.cheatsheet.server.tcpServer;
import database.cheatsheet.server.tcpSlave;

import java.net.Socket;

public class serverSlaveExample extends tcpSlave {
    /**
     * Inizializza tutte le variabili per l'utilizzo
     *
     * @param father Il tcp server chiamante
     * @param client La connessione socket che abbiamo con il client
     */
    public serverSlaveExample(tcpServer father, Socket client) {
        super(father, client);
    }

    @Override
    protected void body() {
    }
}
