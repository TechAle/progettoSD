package it.unimib.finalproject.database;


import it.unimib.finalproject.database.structure.Sala;
import it.unimib.finalproject.database.structure.film.Film;
import it.unimib.finalproject.database.structure.film.Proiezione;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Alessandro Condello
 * @since 1/06/23
 * @last-modified 03/06/23
 */
public class databaseManager {

    private static databaseManager register;
    Map<String, Sala> database;
    List<Proiezione> proiezione;

    private databaseManager() {
        database = new HashMap<>();
        proiezione = new ArrayList<>();
        loadData();
    }

    private void loadData() {


        try (BufferedReader reader = new BufferedReader(new FileReader("resources/sala.csv"))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                database.put(parts[0], new Sala(parts[0], Integer.parseInt(parts[1])));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<Integer, Film> films = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("resources/film.csv"))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                films.put(Integer.parseInt(parts[0]), new Film(parts[1], parts[2]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        try (BufferedReader reader = new BufferedReader(new FileReader("resources/proiezioni.csv"))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                Film toAdd = films.get(Integer.parseInt(parts[1]));
                Proiezione proiezione = new Proiezione(toAdd, database.get(parts[4]), parts[2], parts[3], parts[5], Integer.parseInt(parts[0]));
                database.get(parts[4]).addProiezione(proiezione);
                this.proiezione.add(proiezione);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        int a = 0;

    }


    public String getStanze() {
        StringBuilder output = new StringBuilder("+[");
        for(Proiezione pro : this.proiezione)
            output.append(pro.toString());
        return output.append("]").toString();
    }

    private Proiezione getProiezioneById(int id) {
        for(Proiezione pro : this.proiezione)
            if (pro.getId() == id)
                return pro;
        return null;
    }

    public String getStanza(int id) {
        Proiezione toCheck = getProiezioneById(id);
        if (toCheck != null) {
            return toCheck.toString();
        } else return "-$Id inesistente";
    }

    public String prenotaPosti(int id, int prenotazione, List<Integer> posti) {
        Proiezione toCheck = getProiezioneById(id);
        if (toCheck != null) {
            return toCheck.aggiungiPosti(posti, prenotazione);
        } else return "-$Id inesistente";
    }

    public String prenotaPosti(int id, List<Integer> posti) {
        Proiezione toCheck = getProiezioneById(id);
        if (toCheck != null) {
            int idPrenotazione = toCheck.generaIdPrenotazione();
            return toCheck.aggiungiPosti(posti, idPrenotazione);
        } else return "-$Id inesistente";
    }

    public String rimuoviPosti(int id, int prenotazione) {
        Proiezione toCheck = getProiezioneById(id);
        if (toCheck != null) {
            return toCheck.rimuoviPosti(prenotazione);
        }else return "-$Id inesistente";
    }

    public String rimuoviPosti(int id, int prenotazione, List<Integer> posti) {
        if (posti.size() == 0)
            return rimuoviPosti(id, prenotazione);
        Proiezione toCheck = getProiezioneById(id);
        if (toCheck != null) {
            return toCheck.rimuoviPosti(posti, prenotazione);
        }else return "-$Id inesistente";
    }





    public static synchronized databaseManager getInstance(){
        if (register == null)
            register = new databaseManager();
        return register;
    }




}
