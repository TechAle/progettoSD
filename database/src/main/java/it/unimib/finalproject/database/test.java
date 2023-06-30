package it.unimib.finalproject.database;

import it.unimib.finalproject.database.structure.server.serverExample;
import it.unimib.finalproject.database.structure.server.serverSlaveExample;
import it.unimib.finalproject.database.utils.RESP.bodyRESP;
import it.unimib.finalproject.database.utils.RESP.commandRESP;
import it.unimib.finalproject.database.utils.RESP.types.arrayRESP;
import it.unimib.finalproject.database.utils.RESP.types.intRESP;
import it.unimib.finalproject.database.utils.parserUtils;
import it.unimib.finalproject.database.utils.server.tcpSlave;

import java.util.ArrayList;

import static it.unimib.finalproject.database.utils.parserUtils.getValuesArray;


public class test {
    public static void main(String[] args) {
        new serverExample(3030, (Class<tcpSlave>) ((Object) serverSlaveExample.class)).start();
        //ArrayList<Object> test = queryParser.parser("[+[[$nomeFilm1$descrizioneFilm1$sala1:100[]$2002-06-01][$nomeFilm2$descrizioneFilm2$sala2:50[:0:1:0:4:1:6]$2003-06-02][$nomeFilm3$descrizioneFilm3$sala3:40[:0:3]$2002-08-05]]]");
        //int a = 0;
        //testCommand();
    }

    static void testCommand() {
        commandRESP comando = parserUtils.parseRedisCommand("[VIEW:50\r\nDEL:1:0[:4]\r\nADD:2[:0:1]]");
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
                        output.append(databaseManager.getInstance().getStanze());
                    else if (comando.getOperazione() instanceof intRESP)
                        output.append(databaseManager.getInstance().getPrenotazione(
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
                            output.append(databaseManager.getInstance().prenotaPosti(
                                    ((intRESP) comando.getOperazione()).getValue(),
                                    array
                            ));
                        // ADD:idProiezione:idPrenotazione[:posto:posto:...]
                        } else if (prossimoControllo instanceof intRESP) {
                            prossimoControllo = prossimoControllo.getNext();
                            if (prossimoControllo instanceof arrayRESP) {
                                array = getValuesArray((arrayRESP) prossimoControllo);
                                output.append(databaseManager.getInstance().prenotaPosti(
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
                                output.append(databaseManager.getInstance().rimuoviPosti(
                                        ((intRESP) comando.getOperazione()).getValue(),
                                        ((intRESP) comando.getOperazione().getNext()).getValue(),
                                        array));
                            // DEL:idProiezione:idPrenotazione
                            } else if (nextCheck == null) {
                                output.append(databaseManager.getInstance().rimuoviPosti(
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

        System.out.println("[" + output + "]");
    }

}
