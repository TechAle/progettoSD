package database;

import database.structure.proiezione;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.*;

/**
 * @author Alessandro Condello
 * @since 1/06/23
 * @last-modified 03/06/23
 */
public class databaseManager {

    private static databaseManager register;
    Map<Integer, proiezione> database;

    private databaseManager() {
        database = new HashMap<>();
        loadData();
    }

    private void loadData() {
        try (BufferedReader reader = new BufferedReader(new FileReader("proiezioni.csv"))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                database.put(       Integer.parseInt(parts[0].replace(" ", "")),
                                    new proiezione(parts[1].replace(" ", "").replace("+", "\\+").replace("-", "\\-"),
                                    Integer.parseInt(parts[2].replaceAll(" ", "")), parts[3]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getStanze() {
        StringBuilder output = new StringBuilder("+[");
        for(int id : database.keySet()) {
            output.append(String.format("%d%s", id, database.get(id).toString()));
        }
        return output.append("]").toString();
    }

    public String getStanza(int id) {
        if (database.containsKey(id)) {
            return database.get(id).toString();
        } else return "-$Id inesistente";
    }

    public String prenotaPosti(int id, int prenotazione, List<Integer> posti) {
        if (database.containsKey(id)) {
            return database.get(id).aggiungiPosti(posti, prenotazione);
        } else return "-$Id inesistente";
    }

    public String prenotaPosti(int id, List<Integer> posti) {
        if (database.containsKey(id)) {
            int idPrenotazione = database.get(id).generaIdPrenotazione();
            return database.get(id).aggiungiPosti(posti, idPrenotazione);
        } else return "-$Id inesistente";
    }

    public String rimuoviPosti(int id, int prenotazione) {
        if (database.containsKey(id)) {
            return database.get(id).rimuoviPosti(prenotazione);
        }else return "-$Id inesistente";
    }

    public String rimuoviPosti(int id, int prenotazione, List<Integer> posti) {
        if (posti.size() == 0)
            return rimuoviPosti(id, prenotazione);
        if (database.containsKey(id)) {
            return database.get(id).rimuoviPosti(posti, prenotazione);
        }else return "-$Id inesistente";
    }




    public static synchronized databaseManager getInstance(){
        if (register == null)
            register = new databaseManager();
        return register;
    }




}
