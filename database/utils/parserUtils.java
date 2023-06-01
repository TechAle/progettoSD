package database.utils;

import database.utils.structure.bodyRESP;
import database.utils.structure.headRESP;
import database.utils.structure.types.arrayRESP;
import database.utils.structure.types.intRESP;

import java.util.ArrayList;
import java.util.Collections;

public class parserUtils {



    public static headRESP parseRedis(String input) {
        int indexIniziale = getAzione(input) + 1;
        if (indexIniziale == -1)
            return new headRESP("-Azione non trovata");
        headRESP output = new headRESP(input.substring(0, indexIniziale));
        bodyRESP body = parserBody(input, indexIniziale, input.length(), null);
        output.setNext(body);
        switch (output.getAction()) {
            case "VIEW":
                return null;
            case "ADD":
                return null;
            case "DEL":
                return null;
        }
        return null;
    }

    private static bodyRESP parserBody(String input, int startingIdx, int endingIdx, bodyRESP prev) {
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
                startingIdx += input.substring(startingIdx).indexOf(']');
                ((arrayRESP) output).setInnerValue(toAdd);
            }
            case ':' -> {
                output = new intRESP();
                if (infoStr.length() == 1) {
                    output.setError("Non e' stato inserito un numero");
                } else {
                    try {
                        ((intRESP) output).setValue(Integer.parseInt(infoStr.substring(1)));
                    } catch (NumberFormatException e) {
                        output.setError("Non e' stato inserito un numero valido");
                    }
                }
            }
            case ']' -> {
                return parserBody(input, startingIdx, endingIdx, prev);
            }
            default -> {
                output = new intRESP();
                output.setError("Questo non dovrebbe succedere");
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
        Collections.addAll(possibleActions, headRESP.allowedActions);
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
                    return i;
            }
        }

        return -1;
    }
}
