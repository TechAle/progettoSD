package database.examples;

import database.cheatsheet.server.tcpServer;
import database.cheatsheet.server.tcpSlave;
import database.utils.parserUtils;
import database.utils.structure.commandRESP;

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
        String message = this.getMessage();
        commandRESP command = parserUtils.parseRedis(message);
        String firstAction = command.getAction();
        if (firstAction.startsWith("-")) {
            this.sendMessage("[" + firstAction + "]");
            return;
        }
        commandRESP start = command;
        StringBuilder output = new StringBuilder();
        while (command.getNext() != start) {
            switch (command.getAction()) {
                case "VIEW":
                    break;
                case "ADD":
                    break;
                case "DEL":
                    break;
            }

            command = command.getNext();
        }
        this.sendMessage("[" + output + "]");
    }
}
