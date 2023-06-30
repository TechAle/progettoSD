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
     * Implementazione di POST "/aggiungiPosto/{idProiezione}".
     */
    @Path("/{idProiezione}/{posti}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response bookSeats(@PathParam("idProiezione") int idProiezione, @PathParam("posti") String posti){
        //  Query da inviare al database.
        String databaseQuery = queryAssembler.generateAdd(idProiezione, posti);

        //  Gestione lista vuota: viene restituita una stringa vuota,
        //  per cui viene inviato un messaggio "bad request" al client.
        if(databaseQuery.equals(""))
            return Response.status(400).build();

        //  Invio databaseQuery al database tramite socket.
        clientDB dbSocket = new clientDB("127.0.0.1", 3030, databaseQuery);
        dbSocket.start();

        //  Ricevo risposta dal database.
        String dbResponse = dbSocket.getResponse();
        ArrayList<Object> parsedMsg = msgParser.parse(dbResponse);

        //  Se il database ha restituito un successo, restituisco un JSON con dentro la location (URL).
        Object pMsgType = parsedMsg.remove(0);
        if(pMsgType.equals('+')){
            String temp = "{\"motivation\":" +
                    "\"" + parsedMsg.get(0) + "\"" +
                    ",\"location\":" + parsedMsg.get(1) +
                    '}';
            return Response.ok(temp, MediaType.APPLICATION_JSON).build();
        } else {
            switch ((String) parsedMsg.get(0)) {
                case "Qualche posto è già stato occupat":
                    return Response.status(403, "Qualche posto è giù occupato").build();
            }
        }

        return Response.status(400, (String) parsedMsg.get(0)).build();
    }


    /**
     * Implementazione di PUT "/aggiungiPosto/{idProiezione}{idPrenotazione}{posti}".
     */
    @Path("/{idProiezione}/{idPrenotazione}")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response bookNewSeats(@PathParam("idProiezione") int idProiezione, @PathParam("idPrenotazione") int idPrenotazione, String posti){
        //  Query da inviare al database.
        String databaseQuery = queryAssembler.generateAdd(idProiezione, idPrenotazione, posti);

        //  Gestione lista vuota: viene restituita una stringa vuota,
        //  per cui viene inviato un messaggio "no content" al client.
        if(databaseQuery.equals(""))
            return Response.status(408).build();

        //  Invio databaseQuery al database tramite socket.
        clientDB dbSocket = new clientDB("127.0.0.1", 3030, databaseQuery);
        dbSocket.start();

        //  Ricevo risposta dal database.
        String dbResponse = dbSocket.getResponse();
        ArrayList<Object> parsedMsg = msgParser.parse(dbResponse);

        //  Se il database ha restituito un successo, restituisco un JSON con dentro la location (URL).
        Object pMsgType = parsedMsg.remove(0);
        if(pMsgType.equals('+')){
            String temp = "{\"motivation\":" +
                            "\"" + parsedMsg.get(0) + "\"" +
                            '}';
            return Response.ok(temp, MediaType.APPLICATION_JSON).build();
        } else {
            switch ((String) parsedMsg.get(0)) {
                case "Qualche posto è già stato occupat":
                    return Response.status(403, "Qualche posto è giù occupato").build();
            }
        }

        return Response.status(400, (String) parsedMsg.get(0)).build();
    }


}