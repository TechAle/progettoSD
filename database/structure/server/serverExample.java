package database.structure.server;

import database.RegisterManager;
import database.utils.server.tcpServer;
import database.utils.server.tcpSlave;

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
