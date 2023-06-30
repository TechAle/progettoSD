package it.unimib.finalproject.server.requests;

import it.unimib.finalproject.server.utils.ErrorManager;
import it.unimib.finalproject.server.utils.msgParser;
import it.unimib.finalproject.server.utils.socket.clientDB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import it.unimib.finalproject.server.utils.queryAssembler;

import java.util.ArrayList;

@Path("rimuoviPosti")
public class rimuoviPosti {

    @Path("/{idProiezione}/{idPrenotazione}")
    @OPTIONS
    public Response avoidCORSBlocking() {
        return Response.ok().header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "*")
                .header("Access-Control-Allow-Headers", "*").header("Access-Control-Allow-Credentials", "false")
                .header("Access-Control-Max-Age", "3600").header("Access-Control-Request-Method", "*").header("Access-Control-Request-Headers", "origin, x-request-with").build();
    }

    /**
     * Implementazione di PUT "/rimuoviPosti/{idProiezione}{idPrenotazione}".
     */
    @Path("/{idProiezione}/{idPrenotazione}")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteSeats(@PathParam("idProiezione") int idProiezione, @PathParam("idPrenotazione") int idPrenotazione, String posti){
        //  Inizializzo query da inviare al database
        String databaseQuery = "";

        if(posti.equals(""))
            databaseQuery = queryAssembler.generateDelete(idProiezione, idPrenotazione);
        else
            databaseQuery = queryAssembler.generateDelete(idProiezione, idPrenotazione, posti);

        //  Invio databaseQuery al database tramite socket.
        clientDB dbSocket = new clientDB("127.0.0.1", 3030, databaseQuery);
        dbSocket.start();

        //  Ricevo risposta dal database.
        String dbResponse = dbSocket.getResponse();
        ArrayList<Object> parsedMsg = msgParser.parse(dbResponse);

        //  Se il database ha restituito un successo, invio messaggio "No Content".
        Object pMsgType = parsedMsg.remove(0);
        if(pMsgType.equals('+'))
            return Response.status(200).header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "*")
                    .header("Access-Control-Allow-Headers", "*").header("Access-Control-Allow-Credentials", "false")
                    .header("Access-Control-Max-Age", "3600").header("Access-Control-Request-Method", "*").header("Access-Control-Request-Headers", "origin, x-request-with").build();

        String error = (String) parsedMsg.get(0);
        Response output = ErrorManager.getCommonError(error);
        if (output != null)
            return output;
        switch (error) {
            case "deve per forza avere un body":
            case "deve avere come primo parametro l'id della proiezione":
            case "il terzo parametro o deve essere vuoto oppure la lista di posti da eliminare":
            case "il secondo parametro deve l'id della prenotazione":
            case "Sono stati trovati dei posti duplicati":
            case "Sono stati inseriti posti non esistenti":
                return Response.status(400, error).build();
            case "Questa non è una tua prenotazione":
                return Response.status(401, error).build();
            case "Non è stata trovata nessuna prenotazione con id ":
                return Response.status(404, error).build();
            default:
                return Response.status(500, error).build();
        }
    }
}