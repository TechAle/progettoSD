package database.examples;

import database.cheatsheet.server.tcpServer;
import database.cheatsheet.server.tcpSlave;

public class serverExample extends tcpServer {
    /**
     * @param porta     La porta dove il server ascolterà
     * @param toIstance
     */
    public serverExample(int porta, Class<tcpSlave> toIstance) {
        super(porta, toIstance);
    }
}
