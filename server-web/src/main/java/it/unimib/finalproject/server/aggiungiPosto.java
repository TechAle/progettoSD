package it.unimib.finalproject.server;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import it.unimib.finalproject.server.utils.queryAssembler;

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
        //  per cui viene inviato un messaggio di errore al client.

        //  Invio databaseQuery al database tramite socket.

        //  Ricevo risposta dal database, la converto in una response adatta.

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
        //  per cui viene inviato un messaggio di errore al client.

        //  Invio databaseQuery al database tramite socket.

        //  Ricevo risposta dal database, la converto in una response adatta.

        return Response.ok().build();
    }


}
