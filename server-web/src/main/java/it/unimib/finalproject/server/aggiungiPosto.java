package it.unimib.finalproject.server;

import it.unimib.finalproject.server.structure.client.clientDB;
import it.unimib.finalproject.server.utils.msgParser;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import it.unimib.finalproject.server.utils.queryAssembler;

import java.util.ArrayList;

@Path("aggiungiPosto")
public class aggiungiPosto {

    /**
     * Implementazione di POST "/aggiungiPosto/{idProiezione}{posti}".
     */
    @Path("/{idProiezione}{posti}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response bookSeats(@PathParam("idProiezione") int idProiezione, @PathParam("posti") String posti){
        //  Query da inviare al database.
        String databaseQuery = queryAssembler.generateAdd(idProiezione, posti);

        //  Gestione lista vuota: viene restituita una stringa vuota,
        //  per cui viene inviato un messaggio "bad request" al client.
        if(databaseQuery.equals(""))
            return Response.status(400).build();

        //  Invio databaseQuery al database tramite socket.
        clientDB dbSocket = new clientDB("127.0.0.1", 3030);
        dbSocket.sendMessage(databaseQuery);

        //  Ricevo risposta dal database.
        String dbResponse = dbSocket.getResponse();
        ArrayList<Object> parsedMsg = msgParser.parse(dbResponse);

        //  Se il database ha restituito un successo, restituisco un JSON con dentro la location (URL).

        return Response.ok().build();
    }


    /**
     * Implementazione di PUT "/aggiungiPosto/{idProiezione}{idPrenotazione}{posti}".
     */
    @Path("/{idProiezione}{idPrenotazione}{posti}")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response bookNewSeats(@PathParam("idProiezione") int idProiezione, @PathParam("idPrenotazione") int idPrenotazione, @PathParam("posti") String posti){
        //  Query da inviare al database.
        String databaseQuery = queryAssembler.generateAdd(idProiezione, idPrenotazione, posti);

        //  Gestione lista vuota: viene restituita una stringa vuota,
        //  per cui viene inviato un messaggio "no content" al client.
        if(databaseQuery.equals(""))
            return Response.status(408).build();

        //  Invio databaseQuery al database tramite socket.
        clientDB dbSocket = new clientDB("127.0.0.1", 3030);
        dbSocket.sendMessage(databaseQuery);

        //  Ricevo risposta dal database.
        String dbResponse = dbSocket.getResponse();
        ArrayList<Object> parsedMsg = msgParser.parse(dbResponse);

        //  Se il database ha restituito un successo, restituisce status code 204.


        return Response.ok().build();
    }


}
