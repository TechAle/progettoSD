package it.unimib.finalproject.server;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import it.unimib.finalproject.server.utils.queryAssembler;

@Path("getFilm")
public class getFilm {

    /**
     *  Implementazione di GET "/getFilm/{idProiezione}.
     */
    @Path("/{idProiezione}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInfo(@PathParam("idProiezione") int idProiezione){
        //  Ottengo stringa query da inviare al database
        String databaseQuery = queryAssembler.generateView(idProiezione);

        //  Invio databaseQuery al database tramite socket.

        //  Ricevo risposta dal database, la converto in una response adatta.

        return Response.ok().build();
    }

}
