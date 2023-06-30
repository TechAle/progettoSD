package it.unimib.finalproject.server.requests;

import it.unimib.finalproject.server.utils.ErrorManager;
import it.unimib.finalproject.server.utils.msgParser;
import it.unimib.finalproject.server.utils.socket.clientDB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import it.unimib.finalproject.server.utils.queryAssembler;

import java.util.ArrayList;

@Path("aggiungiPosto")
public class aggiungiPosto {

    @Path("/{idProiezione}")
    @OPTIONS
    @Produces(MediaType.APPLICATION_JSON)
    public Response avoidCORSBlocking() {
        return Response.ok().header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "*")
                .header("Access-Control-Allow-Headers", "*").header("Access-Control-Allow-Credentials", "false")
                .header("Access-Control-Max-Age", "3600").header("Access-Control-Request-Method", "*").header("Access-Control-Request-Headers", "origin, x-request-with").build();
    }

    /**
     * Implementazione di POST "/aggiungiPosto/{idProiezione}".
     */
    @Path("/{idProiezione}")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response bookSeats(@PathParam("idProiezione") int idProiezione, String posti){
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
            return Response.ok(temp, MediaType.APPLICATION_JSON).header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "*")
                    .header("Access-Control-Allow-Headers", "*").header("Access-Control-Allow-Credentials", "false")
                    .header("Access-Control-Max-Age", "3600").header("Access-Control-Request-Method", "*").header("Access-Control-Request-Headers", "origin, x-request-with").build();
        }
        String error = (String) parsedMsg.get(0);
        Response output = ErrorManager.getCommonError(error);
        if (output != null)
            return output;
        switch (error) {
            case "Qualche posto è già stato occupat":
                return Response.status(406, "Qualche posto è giù occupato").build();
            case "deve per forza avere un body":
            case "deve avere come primo parametro l'id della proiezione":
            case "il terzo parametro vede rappresentare i posti da prenotare":
            case "il secondo parametro deve essere o i posti da prenotare oppure  l'id della prenotazione":
            case "Non ci sono abbastanza posti":
                return Response.status(400, error).build();
            case "Posto non esistente":
                return Response.status(404, error).build();
            default:
                return Response.status(500, error).build();
        }
    }


    /**
     * Implementazione di PUT "/aggiungiPosto/{idProiezione}{idPrenotazione}{posti}".
     */
    @Path("/{idProiezione}/{idPrenotazione}")
    @POST
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
            return Response.status(204).header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "*")
                    .header("Access-Control-Allow-Headers", "*").header("Access-Control-Allow-Credentials", "false")
                    .header("Access-Control-Max-Age", "3600").build();
        }
        String error = (String) parsedMsg.get(0);
        Response output = ErrorManager.getCommonError(error);
        if (output != null)
            return output;
        switch (error) {
            case "deve per forza avere un body":
            case "deve avere come primo parametro l'id della proiezione":
            case "il terzo parametro vede rappresentare i posti da prenotare":
            case "il secondo parametro deve essere o i posti da prenotare oppure  l'id della prenotazione":
            case "Non ci sono abbastanza posti":
            case "Qualche posto è già stato occupato":
                return Response.status(400, error).build();
            case "Posto non esistente":
                return Response.status(404, error).build();
            default:
                return Response.status(500, error).build();
        }
    }


}