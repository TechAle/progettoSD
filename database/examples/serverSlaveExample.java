package database.examples;

import database.cheatsheet.server.tcpServer;
import database.cheatsheet.server.tcpSlave;
import database.utils.parserUtils;
import database.utils.structure.bodyRESP;
import database.utils.structure.commandRESP;
import database.utils.structure.types.arrayRESP;
import database.utils.structure.types.intRESP;

import java.net.Socket;
import java.util.ArrayList;

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
        commandRESP command = parserUtils.parseRedisCommand(message);
        String firstAction = command.getAction();
        if (firstAction.startsWith("-")) {
            System.out.println("[" + firstAction + "]");
            return;
        }
        StringBuilder output = new StringBuilder();
        boolean before = false;
        do {
            if (before)
                output.append("\r\n");
            switch (command.getAction()) {
                case "VIEW" -> {
                    if (command.getOperations() == null)
                        output.append(((serverExample) father).register.getStanze());
                    else if (command.getOperations() instanceof intRESP)
                        output.append(((serverExample) father).register.getStanza(((intRESP) command.getOperations()).getValue()));
                    else
                        output.append("-$VIEW non può possedere più di 1 parametro");
                }
                case "ADD" -> {
                    if (command.getOperations() == null)
                        output.append("-$ADD deve per forza avere un body");
                    else if (!(command.getOperations() instanceof intRESP))
                        output.append("-$ADD deve avere come primo parametro l'id della stanza");
                    else {
                        bodyRESP nextCheck = command.getOperations().getNext();
                        ArrayList<Integer> array;
                        if (nextCheck instanceof arrayRESP) {
                            array = new ArrayList<>();
                            nextCheck = ((arrayRESP) nextCheck).getValue();
                            while (nextCheck instanceof intRESP) {
                                array.add(((intRESP) nextCheck).getValue());
                                nextCheck = nextCheck.getNext();
                            }
                            output.append(((serverExample) father).register.prenotaPosti(
                                    ((intRESP) command.getOperations()).getValue(),
                                    array
                            ));
                        } else if (nextCheck instanceof intRESP) {
                            nextCheck = nextCheck.getNext();
                            if (nextCheck instanceof arrayRESP) {
                                array = new ArrayList<>();
                                nextCheck = ((arrayRESP) nextCheck).getValue();
                                while (nextCheck instanceof intRESP) {
                                    array.add(((intRESP) nextCheck).getValue());
                                    nextCheck = nextCheck.getNext();
                                }
                                output.append(((serverExample) father).register.prenotaPosti(
                                        ((intRESP) command.getOperations()).getValue(),
                                        ((intRESP) command.getOperations().getNext()).getValue(),
                                        array));
                            } else output.append("-$ADD il terzo parametro vede rappresentare i posti da prenotare");
                        } else
                            output.append("-$ADD il secondo parametro deve essere o i posti da prenotare oppure  l'id della prenotazione");
                    }
                }
                case "DEL" -> {
                    if (command.getOperations() == null)
                        output.append("-$DEL deve per forza avere un body");
                    else if (!(command.getOperations() instanceof intRESP))
                        output.append("-$DEL deve avere come primo parametro l'id della stanza");
                    else {
                        bodyRESP nextCheck = command.getOperations().getNext();
                        ArrayList<Integer> array;
                        if (nextCheck instanceof intRESP) {
                            nextCheck = nextCheck.getNext();
                            if (nextCheck instanceof arrayRESP) {
                                array = new ArrayList<>();
                                nextCheck = ((arrayRESP) nextCheck).getValue();
                                while (nextCheck instanceof intRESP) {
                                    array.add(((intRESP) nextCheck).getValue());
                                    nextCheck = nextCheck.getNext();
                                }
                                output.append(((serverExample) father).register.rimuoviPosti(
                                        ((intRESP) command.getOperations()).getValue(),
                                        ((intRESP) command.getOperations().getNext()).getValue(),
                                        array));
                            } else if (nextCheck == null) {
                                output.append(((serverExample) father).register.rimuoviPosti(
                                        ((intRESP) command.getOperations()).getValue(),
                                        ((intRESP) command.getOperations().getNext()).getValue()));
                            } else
                                output.append("-$DEL il terzo parametro o deve essere vuoto oppure la lista di posti da eliminare");
                        } else output.append("-$DEL il secondo parametro deve l'id della prenotazione");
                    }
                }
            }
            before = true;
        }while ((command = command.getNext()) != null);

        this.sendMessage("[" + output + "]");
    }
}
