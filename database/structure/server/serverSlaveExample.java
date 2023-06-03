package database.structure.server;

import database.RegisterManager;
import database.utils.server.tcpServer;
import database.utils.server.tcpSlave;
import common.parserUtils;
import database.structure.RESP.bodyRESP;
import database.structure.RESP.commandRESP;
import database.structure.RESP.types.arrayRESP;
import database.structure.RESP.types.intRESP;

import java.net.Socket;
import java.util.ArrayList;

import static common.parserUtils.getValuesArray;

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
        commandRESP comando = parserUtils.parseRedisCommand(message);
        // Se la prima azione è un errore, allora vuol dire che c'è un problema con la sintassi del comando
        String primaAzione = comando.getAction();
        if (comando.isError()) {
            System.out.println("[" + primaAzione + "]");
            return;
        }
        StringBuilder output = new StringBuilder();
        boolean aggiungiLimitatore = false;
        do {
            if (aggiungiLimitatore)
                output.append("\r\n");
            ArrayList<Integer> array;
            switch (comando.getAction()) {
                /*
                    Comandi accettati:
                    VIEW
                    VIEW:idProiezione
                 */
                case "VIEW" -> {
                    if (comando.getOperazione() == null)
                        output.append(RegisterManager.getInstance().getStanze());
                    else if (comando.getOperazione() instanceof intRESP)
                        output.append(RegisterManager.getInstance().getStanza(
                                ((intRESP) comando.getOperazione()).getValue())
                        );
                }
                /*
                    Comandi accettati:
                    ADD:idProiezione[:posto:posto:...]
                    ADD:idProiezione:idPrenotazione[:posto:posto:...]
                 */
                case "ADD" -> {
                    if (comando.getOperazione() == null)
                        output.append("-$ADD deve per forza avere un body");
                    else if (!(comando.getOperazione() instanceof intRESP))
                        output.append("-$ADD deve avere come primo parametro l'id della proiezione");
                    else {
                        bodyRESP prossimoControllo = comando.getOperazione().getNext();
                        // ADD:idProiezione[:posto:posto:...]
                        if (prossimoControllo instanceof arrayRESP) {
                            array = getValuesArray((arrayRESP) prossimoControllo);
                            output.append(RegisterManager.getInstance().prenotaPosti(
                                    ((intRESP) comando.getOperazione()).getValue(),
                                    array
                            ));
                            // ADD:idProiezione:idPrenotazione[:posto:posto:...]
                        } else if (prossimoControllo instanceof intRESP) {
                            prossimoControllo = prossimoControllo.getNext();
                            if (prossimoControllo instanceof arrayRESP) {
                                array = getValuesArray((arrayRESP) prossimoControllo);
                                output.append(RegisterManager.getInstance().prenotaPosti(
                                        ((intRESP) comando.getOperazione()).getValue(),
                                        ((intRESP) comando.getOperazione().getNext()).getValue(),
                                        array));
                            } else output.append("-$ADD il terzo parametro vede rappresentare i posti da prenotare");
                        } else output.append("-$ADD il secondo parametro deve essere o i posti da prenotare oppure  l'id della prenotazione");
                    }
                }
                /*
                    Comandi accettati:
                    DEL:idProiezione:idPrenotazione
                    DEL:idProiezione:idPrenotazione[:posto:posto:...]
                 */
                case "DEL" -> {
                    if (comando.getOperazione() == null)
                        output.append("-$DEL deve per forza avere un body");
                    else if (!(comando.getOperazione() instanceof intRESP))
                        output.append("-$DEL deve avere come primo parametro l'id della proiezione");
                    else {
                        bodyRESP nextCheck = comando.getOperazione().getNext();
                        if (nextCheck instanceof intRESP) {
                            nextCheck = nextCheck.getNext();
                            // DEL:idProiezione:idPrenotazione[:posto:posto:...]
                            if (nextCheck instanceof arrayRESP) {
                                array = getValuesArray((arrayRESP) nextCheck);
                                output.append(RegisterManager.getInstance().rimuoviPosti(
                                        ((intRESP) comando.getOperazione()).getValue(),
                                        ((intRESP) comando.getOperazione().getNext()).getValue(),
                                        array));
                                // DEL:idProiezione:idPrenotazione
                            } else if (nextCheck == null) {
                                output.append(RegisterManager.getInstance().rimuoviPosti(
                                        ((intRESP) comando.getOperazione()).getValue(),
                                        ((intRESP) comando.getOperazione().getNext()).getValue()));
                            } else
                                output.append("-$DEL il terzo parametro o deve essere vuoto oppure la lista di posti da eliminare");
                        } else output.append("-$DEL il secondo parametro deve l'id della prenotazione");
                    }
                }
            }
            aggiungiLimitatore = true;
        }while ((comando = comando.getNext()) != null);

        this.sendMessage("[" + output + "]");
    }
}
