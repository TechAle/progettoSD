package it.unimib.finalproject.server;

import it.unimib.finalproject.server.structure.client.clientDB;
import it.unimib.finalproject.server.utils.msgParser;
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
            for(Object q : arrayQuack) {
                for (int i = 0; i < parsedMsg.size(); i++) {
                    switch (i) {
                        case 0:
                            jsonString.append("id:");
                            break;
                        case 1:
                            jsonString.append("nome:");
                            break;
                        case 2:
                            jsonString.append("descrizione:");
                            break;
                        case 3:
                            jsonString.append("sala:");
                            break;
                        case 4:
                            jsonString.append("orario:");
                            break;
                        case 5:
                            jsonString.append("postiPrenotati:[");
                            ArrayList<Object> a = (ArrayList<Object>) parsedMsg.get(i);
                            for (int j = 0; j < a.size(); j++) {
                                ArrayList<Object> b =(ArrayList<Object>) a.get(j);
                                jsonString.append('[');
                                for(int k = 0; k < b.size(); k++){
                                    jsonString.append(b.get(k));
                                    if(k != b.size() - 1)
                                        jsonString.append(',');
                                }
                                jsonString.append(']');
                                if (j != a.size() - 1)
                                    jsonString.append(',');
                            }
                            jsonString.append(']');
                            break;
                        case 6:
                            jsonString.append("data:");
                            break;
                    }
                    jsonString.append(parsedMsg.get(i));
                    jsonString.append(',');
                }
                jsonString.append(',');
            }
            jsonString.append(']');

            String temp = jsonString.toString();
            return Response.ok(temp, MediaType.APPLICATION_JSON).build();
        }
        return Response.ok().build();
    }


}