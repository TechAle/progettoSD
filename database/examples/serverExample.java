package database.examples;

import database.RegisterManager;
import database.cheatsheet.server.tcpServer;
import database.cheatsheet.server.tcpSlave;

public class serverExample extends tcpServer {

    RegisterManager register;
    /**
     * @param porta     La porta dove il server ascolterà
     * @param toIstance
     */
    public serverExample(int porta, Class<tcpSlave> toIstance) {
        super(porta, toIstance);
        register = RegisterManager.getInstance();
    }
}
