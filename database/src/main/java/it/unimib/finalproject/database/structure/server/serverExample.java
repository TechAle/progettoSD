package it.unimib.finalproject.database.structure.server;


import it.unimib.finalproject.database.databaseManager;
import it.unimib.finalproject.database.utils.server.tcpServer;
import it.unimib.finalproject.database.utils.server.tcpSlave;

/**
 * @author Alessandro Condello
 * @since 2/06/23
 * @last-modified 03/06/23
 */
public class serverExample extends tcpServer {

    databaseManager register;
    /**
     * @param porta     La porta dove il server ascolterà
     * @param toIstance
     */
    public serverExample(int porta, Class<tcpSlave> toIstance) {
        super(porta, toIstance);
        register = databaseManager.getInstance();
    }
}
