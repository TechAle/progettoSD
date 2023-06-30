package it.unimib.finalproject.server.requests;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import it.unimib.finalproject.server.utils.*;
import it.unimib.finalproject.server.structure.client.clientDB;

import java.util.ArrayList;

@Path("getFilm")
public class getFilm {

    /**
     *  Implementazione di GET "/getFilm/{idProiezione}.
     */
    @Path("/{idProiezione}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response viewFilm(@PathParam("idProiezione") int idProiezione){
        //  Ottengo stringa query da inviare al database
        String databaseQuery = queryAssembler.generateView(idProiezione);

        //  Invio databaseQuery al database tramite socket.
        clientDB dbSocket = new clientDB("127.0.0.1", 3030, databaseQuery);
        dbSocket.start();

        //  Ricevo risposta dal database.
        String dbResponse = dbSocket.getResponse();
        ArrayList<Object> parsedMsg = msgParser.parse(dbResponse);

        //  Se il database ha restituito un successo, costruiamo l'oggetto da mandare al client.
        Object pMsgType = parsedMsg.remove(0);
        if(pMsgType.equals('+')) {
            ArrayList<Object> castedQ = parsedMsg;
            StringBuilder jsonString = new StringBuilder();
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
                            Object idProiezioneTemp = a.get(j*2);
                            Object idPrenotazione = a.get((j*2)+1);
                            jsonString.append(String.format("[%s,%s]", idProiezioneTemp, idPrenotazione));
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

            String temp = jsonString.toString();
            return Response.ok(temp, MediaType.APPLICATION_JSON).build();
        }

        /*
        String errorMsg = (String) parsedMsg.get(0);
        int errorCode;
        switch(errorMsg){
            case
        }
        return Response.status(404).type(errorMsg).build();
         */
        return Response.ok().build();
    }

}