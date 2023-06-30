package it.unimib.finalproject.server;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import it.unimib.finalproject.server.utils.queryAssembler;

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

        //  Passo query al database tramite socket.

        //  Ricevo risposta dal database, la converto in una response adatta.

        return Response.ok().build();
    }


}
