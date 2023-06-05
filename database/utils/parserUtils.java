package database.utils;

import database.utils.RESP.bodyRESP;
import database.utils.RESP.commandRESP;
import database.utils.RESP.types.arrayRESP;
import database.utils.RESP.types.intRESP;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Alessandro Condello
 * @since 1/06/23
 * @last-modified 03/06/23
 */
public class parserUtils {


    /**
     * @param input La stringa di cui vogliamo la struttura
     * @return la struttura dati
     */
    public static commandRESP parseRedisCommand(String input) {
        // Togliamo le parentesi quadre
        input = input.substring(1, input.length() - 1);
        commandRESP prev = null;
        commandRESP first = null;
        // Ogni operazione si differenza dalle altre da \r\n
        for(String operation : input.split("\\r\\n")) {
            // Struttura: Azione unito dopo con una combinazione di : $ []
            int indexIniziale = getAzione(operation);
            if (indexIniziale == -1) // Non è stata trovata l'azione
                return new commandRESP("-$Azione non trovata");
            // Crea la struttura
            bodyRESP body = parserBody(operation, indexIniziale, operation.length(), null);
            // Vari errori
            if (body == null) return new commandRESP("-$Errore nella sintassi");
            else if (body.error)
                return new commandRESP("-$" + body.getError());
            // Inizializzo l'output finale
            commandRESP output = new commandRESP(operation.substring(0, indexIniziale));
            output.setOperations(body);
            if (prev != null)
                prev.setNext(output);
            if (first == null)
                first = output;
            prev = output;
        }
        return first;
    }

    // TODO  \\:
    // TODO testing ricorsione
    /**
     * Questa è una funzione ricorsiva che ha come caso base quando i due indici si invertono.
     * Il funzionamento è il seguente:
     * 1) Cerchiamo il secondo carattere limitatore più vicino dopo il nostro indice di partenza
     *      Il carattere limitatore è ciò che ci limita un valore da un altro : $ []
     * 2) Dal nostro indice di partenza all'indice di questo carattere limitatore estraiamo la stringa
     * 3) A seconda del primo carattere limitatore facciamo determinate operazioni con ciò che abbiamo trovato
     * 4) Richiama la funzione ricorsiva con indice del secondo carattere limitatore
     *      Nel caso il secondo carattere limitatore sia [, allora prima chiamo la funzione ricorsiva
     *      Tra la parentesi quadra aperta e quella che la chiude, e poi dalla parentesi chiusa fino all'indice finale
     * @param input La stringa di input
     * @param startingIdx L'indice da dove iniziamo ad analizzare la stringa
     * @param endingIdx L'indice dove finiamo di analizzare la stringa
     * @param prev la sezione che abbiamo codificato prima
     * @return La struttura dati
     */
    public static bodyRESP parserBody(String input, int startingIdx, int endingIdx, bodyRESP prev) {
        if (startingIdx >= endingIdx)
            return prev;
        bodyRESP output;
        StringBuilder info = new StringBuilder();
        for(int i = startingIdx; i < endingIdx; i++) {
            char toCheck = input.charAt(i);
            if (i == startingIdx) {
                if (!isLimited(toCheck))
                    return null;
            } else {
                if (isLimited(toCheck))
                    break;
            }

            info.append(toCheck);
            startingIdx = i;

        }
        startingIdx += 1;
        String infoStr = info.toString();
        switch (infoStr.charAt(0)) {
            case '[' -> {
                output = new arrayRESP();
                bodyRESP toAdd = parserBody(input, startingIdx, getNextClosedBar(input, startingIdx), null);
                startingIdx += getNextClosedBar(input.substring(startingIdx), 0);
                ((arrayRESP) output).setInnerValue(toAdd);
            }
            case ':' -> {
                output = new intRESP();
                if (infoStr.length() == 1) {
                    output.setError("-$Non e' stato inserito un numero");
                } else {
                    try {
                        ((intRESP) output).setValue(Integer.parseInt(infoStr.substring(1)));
                    } catch (NumberFormatException e) {
                        output.setError("-$Non e' stato inserito un numero valido");
                    }
                }
            }
            case ']' -> {
                return parserBody(input, startingIdx, endingIdx, prev);
            }
            default -> {
                output = new intRESP();
                output.setError("-$Questo non dovrebbe succedere");
            }
        }
        if (output.error)
            return output;
        if (prev != null)
            prev.setNext(output);
        parserBody(input, startingIdx, endingIdx, output);
        return output;
    }

    private static int getNextClosedBar(String input, int startingInput) {
        int opened = 0;
        for(int i = startingInput; i < input.length(); i++)
            switch (input.charAt(i)) {
                case '[' -> {
                    opened++;
                }
                case ']' -> {
                    if (opened == 0)
                        return i;
                    opened--;
                }
            }
        return -1;
    }

    private static boolean isLimited(char car) {
        return switch (car) {
            case '[', ']', ':' -> true;
            default -> false;
        };
    }

    /**
     *
     * @return index of where the action start in the string. -1 if the action doesnt exists
     */
    private static int getAzione(String input) {
        ArrayList<String> possibleActions = new ArrayList<>();
        Collections.addAll(possibleActions, commandRESP.allowedActions);
        StringBuilder toCheck = new StringBuilder();
        char[] inputChar = input.toCharArray();
        int size = possibleActions.size();
        for(int i = 0; i < input.length(); i++) {
            char toCheckCharacter = inputChar[i];
            toCheck.append(inputChar[i]);
            for(int j = 0; j < size; j++) {
                String toCheckString = possibleActions.get(j);
                if (toCheckString.toCharArray()[i] != toCheckCharacter) {
                    possibleActions.remove(j);
                    j--;
                    if ((size -= 1) == 0)
                        return -1;
                } else if (toCheck.toString().equals(toCheckString))
                    return i + 1;
            }
        }

        return -1;
    }

    public static ArrayList<Integer> getValuesArray(arrayRESP input) {
        bodyRESP nextCheck = input.getValue();
        ArrayList<Integer> output = new ArrayList<>();
        while (nextCheck instanceof intRESP) {
            output.add(((intRESP) nextCheck).getValue());
            nextCheck = nextCheck.getNext();
        }
        return output;
    }
}
