package database.examples;

import database.cheatsheet.client.tcpClient;

public class clientExample extends tcpClient {
    /**
     * @param address l'ip del server a cui ci connetteremo
     * @param port    la porta del server a cui ci connetteremo
     */
    public clientExample(String address, int port) {
        super(address, port);
    }

    @Override
    protected void body() {
    }

}
