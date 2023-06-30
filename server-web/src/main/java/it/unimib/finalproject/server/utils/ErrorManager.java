package it.unimib.finalproject.server.utils;

import jakarta.ws.rs.core.Response;

public class ErrorManager {
    public static Response getCommonError(String input) {

        switch (input) {
            case "Non e' stato inserito un numero":
            case "Non e' stato inserito un numero valido":
            case "Errore sintassi":
            case "Azione non trovata":
                return Response.status(400, input).build();
            case "Questo non dovrebbe succedere":
            case "Internal Error":
                return Response.status(500, "Internal error").build();
            case "Id inesistente":
                return Response.status(404, input).build();
            default:
                return null;
        }

    }
}
