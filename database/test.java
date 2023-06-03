package database;

import common.parserUtils;
import database.structure.RESP.bodyRESP;
import database.structure.RESP.commandRESP;
import database.structure.RESP.types.arrayRESP;
import database.structure.RESP.types.intRESP;

import java.util.ArrayList;

import static common.parserUtils.getValuesArray;

public class test {
    public static void main(String[] args) {
        testCommand();

    }

    static void testCommand() {
        commandRESP comando = parserUtils.parseRedisCommand("[VIEW:48\r\nDEL:48:0[:4]\r\nADD:10:1[:0:1]]");
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

        System.out.println("[" + output + "]");
    }

}
