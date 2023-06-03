package database;

import database.utils.parserUtils;
import database.utils.structure.bodyRESP;
import database.utils.structure.commandRESP;
import database.utils.structure.types.arrayRESP;
import database.utils.structure.types.intRESP;

import java.util.ArrayList;

public class test {
    public static void main(String[] args) {
        testCommand();

    }

    static void testCommand() {
        commandRESP command = parserUtils.parseRedisCommand("[VIEW:48\r\nDEL:48:0[:4]\r\nADD:10:1[:0:1]]");
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
                        output.append(RegisterManager.getInstance().getStanze());
                    else if (command.getOperations() instanceof intRESP)
                        output.append(RegisterManager.getInstance().getStanza(((intRESP) command.getOperations()).getValue()));
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
                            output.append(RegisterManager.getInstance().prenotaPosti(
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
                                output.append(RegisterManager.getInstance().prenotaPosti(
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
                                output.append(RegisterManager.getInstance().rimuoviPosti(
                                        ((intRESP) command.getOperations()).getValue(),
                                        ((intRESP) command.getOperations().getNext()).getValue(),
                                        array));
                            } else if (nextCheck == null) {
                                output.append(RegisterManager.getInstance().rimuoviPosti(
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

        System.out.println("[" + output + "]");
    }
}
