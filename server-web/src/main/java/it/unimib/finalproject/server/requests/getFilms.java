package it.unimib.finalproject.server.requests;

import it.unimib.finalproject.server.utils.ErrorManager;
import it.unimib.finalproject.server.utils.msgParser;
import it.unimib.finalproject.server.utils.socket.clientDB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import it.unimib.finalproject.server.utils.queryAssembler;

import java.util.ArrayList;

@Path("getFilms")
public class getFilms {

    /**
     * Implementazione di GET "/getFilms".
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response viewFilms() {
        //  Ottengo la query per il database.
        String databaseQuery = queryAssembler.generateView();

        //  Invio databaseQuery al database tramite socket.
        clientDB dbSocket = new clientDB("127.0.0.1", 3030, databaseQuery);
        dbSocket.start();

        //  Ricevo risposta dal database.
        String dbResponse = dbSocket.getResponse();
        ArrayList<Object> parsedMsg = msgParser.parse(dbResponse);

        //  Se il database ha restituito un successo, costruiamo l'oggetto da mandare al client.
        Object pMsgType = parsedMsg.remove(0);
        if(pMsgType.equals('+')) {
            ArrayList<Object> arrayQuack = (ArrayList<Object>) parsedMsg.get(0);
            StringBuilder jsonString = new StringBuilder();
            jsonString.append('[');
            int idx = 0;
            for(Object q : arrayQuack) {
                ArrayList<Object> castedQ = (ArrayList<Object>) q;
                jsonString.append('{');
                for (int i = 0; i < castedQ.size(); i++) {
                    switch (i) {
                        case 0:
                            jsonString.append("\"idProiezione\": " + castedQ.get(i));
                            break;
                        case 1:
                            jsonString.append("\"nome\":").append("\"").append(castedQ.get(i)).append("\"");
                            break;
                        case 2:
                            jsonString.append("\"descrizione\":").append("\"").append(castedQ.get(i)).append("\"");
                            break;
                        case 3:
                            jsonString.append("\"sala\":").append("\"").append(castedQ.get(i)).append("\"");
                            break;
                        case 4:
                            jsonString.append("\"postiTotali\":").append(castedQ.get(i));
                            break;
                        case 5:
                            jsonString.append("\"postiPrenotati\":[");
                            ArrayList<Object> a = (ArrayList<Object>) castedQ.get(i);
                            for (int j = 0; j < a.size() / 2; j++) {
                                Object idProiezione = a.get(j*2);
                                Object idPrenotazione = a.get((j*2)+1);
                                jsonString.append(String.format("[%s,%s]", idProiezione, idPrenotazione));
                                if (j != a.size()/2 - 1)
                                    jsonString.append(',');
                            }
                            jsonString.append(']');
                            break;
                        case 6:
                            jsonString.append("\"giorno\":").append("\"").append(castedQ.get(i)).append("\"");
                            break;
                    }
                    if(i != castedQ.size() - 1)
                        jsonString.append(',');
                    else
                        jsonString.append('}');
                }
                if (idx != arrayQuack.size() - 1)
                    jsonString.append(',');
                idx++;
            }
            jsonString.append(']');

            String temp = jsonString.toString();
            return Response.ok(temp, MediaType.APPLICATION_JSON).build();
        }
        String error = (String) parsedMsg.get(0);
        Response output = ErrorManager.getCommonError(error);
        if (output != null)
            return output;
        return Response.status(500, error).build();
    }
}


