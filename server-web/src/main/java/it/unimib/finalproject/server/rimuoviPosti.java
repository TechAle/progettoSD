package it.unimib.finalproject.server;

import it.unimib.finalproject.server.structure.client.clientDB;
import it.unimib.finalproject.server.utils.msgParser;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import it.unimib.finalproject.server.utils.queryAssembler;

import java.util.ArrayList;

@Path("rimuoviPosti")
public class rimuoviPosti {

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
            return Response.status(204).build();

        return Response.status(400).build();
    }
}