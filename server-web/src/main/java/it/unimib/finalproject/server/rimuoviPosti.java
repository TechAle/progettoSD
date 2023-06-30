package it.unimib.finalproject.server;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import it.unimib.finalproject.server.utils.queryAssembler;

@Path("rimuoviPosti")
public class rimuoviPosti {

    /**
     * Implementazione di PUT "/rimuoviPosti/{idProiezione}{idPrenotazione}{posti}".
     */
    @Path("/{idProiezione}{idPrenotazione}{posti}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteSeats(@PathParam("idProiezione") int idProiezione, @PathParam("idPrenotazione") int idPrenotazione, @PathParam("posti") String posti){
        //  Inizializzo query da inviare al database
        String databaseQuery = "";

        if(posti.equals(""))
            databaseQuery = queryAssembler.generateDelete(idProiezione, idPrenotazione);
        else
            databaseQuery = queryAssembler.generateDelete(idProiezione, idPrenotazione, posti);

        //  Invio databaseQuery al database tramite socket.

        //  Ricevo risposta dal database, la converto in una response adatta.

        return Response.ok().build();
    }

}
