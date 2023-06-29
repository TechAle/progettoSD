package it.unimib.finalproject.server.utils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Martina Elli
 * @since 26/06/23
 *
 */
public class msgParser {

    public static ArrayList<Object> parse(String query){

        ArrayList<Object> queryElements = new ArrayList<>();
        ArrayList<Object> arrayAppoggio = new ArrayList<>();
        ArrayList<Character> accepted = new ArrayList<>(Arrays.asList(':', '[', ']', '$'));

        queryElements.add(query.charAt(1));
        query = query.substring(2);
        query = query.substring(0, query.length() - 2);
        int i = 0;
        try {
            while (i < query.length()) {
                int addValues = -1;
                switch (query.charAt(i)) {
                    case '$':
                        addValues = 0;
                        break;
                    case ':':
                        addValues = 1;
                        break;
                    case '[':
                        arrayAppoggio.add(new ArrayList<>());
                        if (arrayAppoggio.size() == 1)
                            queryElements.add(arrayAppoggio.get(0));
                        else
                            ((ArrayList<Object>) arrayAppoggio.get(arrayAppoggio.size() - 2)).add(arrayAppoggio.get(arrayAppoggio.size() - 1));
                        i++;
                        continue;
                    case ']':
                        arrayAppoggio.remove(arrayAppoggio.size() - 1);
                        i++;
                        continue;
                    default:
                        return null;
                }


                //noinspection ConstantValue
                if (addValues != -1) {
                    Object temp;
                    StringBuilder sb = new StringBuilder();
                    i++;
                    while (i < query.length() && !((accepted).contains(query.charAt(i)))) {
                        sb.append(query.charAt(i));
                        i++;
                    }
                    if (addValues == 1)
                        temp = Integer.parseInt(sb.toString());
                    else
                        temp = sb.toString();

                    if (arrayAppoggio.isEmpty())
                        queryElements.add(temp);
                    else {
                        ((ArrayList<Object>) (arrayAppoggio.get(arrayAppoggio.size() - 1))).add(temp);
                    }
                }


            }
        }catch (StackOverflowError e) {
            return null;
        }
        return queryElements;

    }


}
