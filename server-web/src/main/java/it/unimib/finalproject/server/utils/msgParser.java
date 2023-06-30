package it.unimib.finalproject.server.utils;

import java.util.ArrayList;

/**
 *
 * @author Martina Elli
 * @since 26/06/23
 *
 */
public class msgParser {

    /**
     * Parser per i messaggi di risposta del database.
     * @param msg stringa contenente un messaggio inviato dal database.
     * @return un ArrayList di oggetti (stringhe, liste contenenti stringhe e/o altre liste).
     */
    public static ArrayList<Object> parser(String msg){

        ArrayList<Object> msgElements = new ArrayList<>();
        ArrayList<Object> arrayAppoggio = new ArrayList<>();

        char msgType = msg.charAt(0);
        msgElements.add(msgType);
        msg = msg.substring(1);

        int i = 0;
        try {
            while (i < msg.length()) {
                Object temp = null;
                //  Identificatore stringa.
                if (msg.charAt(i) == '$') {
                    i++;
                    StringBuilder sb = new StringBuilder();
                    while (i < msg.length() && msg.charAt(i) != ':' && msg.charAt(i) != '[' && msg.charAt(i) != ']' && msg.charAt(i) != '$') {
                        sb.append(msg.charAt(i));
                        i++;
                    }
                    temp = sb.toString();
                }
                //  Identificatore stringa rappresentante un numero intero.
                else if (msg.charAt(i) == ':') {
                    i++;
                    StringBuilder sb = new StringBuilder();
                    while (i < msg.length() && msg.charAt(i) != ':' && msg.charAt(i) != '[' && msg.charAt(i) != ']' && msg.charAt(i) != '$') {
                        sb.append(msg.charAt(i));
                        i++;
                    }
                    temp = Integer.parseInt(sb.toString());
                }
                //  Apertura lista.
                else if (msg.charAt(i) == '[') {
                    arrayAppoggio.add(new ArrayList<>());
                    if (arrayAppoggio.size() == 1)
                        msgElements.add(arrayAppoggio.get(0));
                    else
                        ((ArrayList<Object>) arrayAppoggio.get(arrayAppoggio.size() - 2)).add(arrayAppoggio.get(arrayAppoggio.size() - 1));
                    i++;
                }
                //  Chiusura lista.
                else if (msg.charAt(i) == ']') {
                    arrayAppoggio.remove(arrayAppoggio.size() - 1);
                    i++;
                }

                if (temp != null) {
                    if (arrayAppoggio.isEmpty())
                        msgElements.add(temp);
                    else
                        ((ArrayList<Object>) arrayAppoggio.get(arrayAppoggio.size() - 1)).add(temp);
                }

            }
        }catch(StackOverflowError e){
            return null;
        }
        return msgElements;
    }

}
