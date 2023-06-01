package database;

import database.utils.structure.prenotazione;
import database.utils.structure.proiezione;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.*;

public class RegisterManager {

    private static RegisterManager register;
    Map<Integer, proiezione> database;

    private RegisterManager() {
        database = new HashMap<>();
        loadData();
    }

    private void loadData() {
        try (BufferedReader reader = new BufferedReader(new FileReader("proiezioni.csv"))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                database.put(Integer.parseInt(parts[0].replaceAll(" ", "")),
                            new proiezione(parts[1].replaceAll(" ", ""), Integer.parseInt(parts[2].replaceAll(" ", "")), parts[3]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        int b = 0;

    }

    public String getStanze() {
        StringBuilder output = new StringBuilder("+");
        boolean before = false;
        for(int id : database.keySet()) {
            if (before)
                output.append("\r\n");
            output.append(String.format("%d%s", id, database.get(id).toString()));
            before = true;
        }
        return output.toString();
    }

    public String getPostiOccupati(int id) {
        if (database.containsKey(id)) {
            return database.get(id).getPostiOccupati();
        } else return "-Id inesistente";
    }

    public String prenotaPosti(int id, int prenotazione, List<Integer> posti) {
        if (database.containsKey(id)) {
            return database.get(id).aggiungiPosti(posti, prenotazione);
        } else return "-Id inesistente";
    }

    public String rimuoviPosti(int id, int prenotazione) {
        if (database.containsKey(id)) {
            return database.get(id).rimuoviPosti(prenotazione);
        }else return "-Id inesistente";
    }

    public String rimuoviPosti(int id, int prenotazione, List<Integer> posti) {
        if (posti.size() == 0)
            return rimuoviPosti(id, prenotazione);
        if (database.containsKey(id)) {
            return database.get(id).rimuoviPosti(posti);
        }else return "-Id inesistente";
    }




    public static synchronized RegisterManager getInstance(){
        if (register == null)
            register = new RegisterManager();
        return register;
    }




}
