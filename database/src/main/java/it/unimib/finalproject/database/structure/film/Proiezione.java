package it.unimib.finalproject.database.structure.film;



import it.unimib.finalproject.database.structure.Sala;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Alessandro Condello
 * @since 1/06/23
 * @last-modified 03/06/23
 */
public class Proiezione {
    private final Film film;
    private final Sala sala;
    private final List<prenotazionePosto> postiOccupati;
    private final LocalDateTime dateTime;
    private final int id;

    public Proiezione(Film film, Sala sala, String postiOccupati, String date, String hour, int id) {
        this.film = film;
        this.sala = sala;
        postiOccupati = postiOccupati.replaceAll(" ", "");
        postiOccupati = postiOccupati.substring(1, postiOccupati.length() -1);
        this.postiOccupati = new ArrayList<>();
        if (postiOccupati.length() > 0)
            for(String value : postiOccupati.split(";")) {
                value = value.substring(1, value.length() - 1);
                int b = 0;
                String[] values = value.split("\\.");
                this.postiOccupati.add(new prenotazionePosto(Integer.parseInt(values[0]), Integer.parseInt(values[1])));
            }
        String[] dateSplitted = date.split("-");
        String[] hourSplitted = hour.split(":");
        this.dateTime = LocalDateTime.of(Integer.parseInt(dateSplitted[2]), Integer.parseInt(dateSplitted[1]), Integer.parseInt(dateSplitted[0]),
                                         Integer.parseInt(hourSplitted[0]), Integer.parseInt(hourSplitted[1]), 0);
        this.id = id;
        //this.postiOccupati = postiOccupati;
    }

    public int getId() {
        return id;
    }

    public Film getFilmStructure() {
        return film;
    }

    public int getPosti() {
        return sala.getPosti();
    }

    public String getPostiOccupati() {
        StringBuilder output = new StringBuilder("+[");
        for(prenotazionePosto pren : this.postiOccupati) {
            output.append(pren.toString());
        }
        output.append("]");
        return output.toString();
    }

    public synchronized String aggiungiPosti(List<Integer> pren, int idPrenotazione) {
        if (this.postiOccupati.size() + pren.size() >= sala.getPosti())
            return "-$Non ci sono abbastanza posti";
        for(int prenCheck : pren)
            for(prenotazionePosto occupati : this.postiOccupati)
                if (occupati.posto() == prenCheck)
                    return "-$Qualche posto è già stato occupato";
                else if (prenCheck > this.sala.getPosti())
                    return "-$Posto non esistente";
        for (int prenCheck : pren)
            this.postiOccupati.add(new prenotazionePosto(idPrenotazione, prenCheck));
        return "+$Prenotazione effettuata con successo";
    }

    public synchronized String rimuoviPosti(int idPrenotazione) {
        boolean removedOne = false;
        for(int i = 0; i < this.postiOccupati.size(); i++)
            if (this.postiOccupati.get(i).idPrenotazione() == idPrenotazione) {
                this.postiOccupati.remove(i);
                i--;
                removedOne = true;
            }
        return removedOne
                ? "+$Prenotazione rimossa con successo"
                : "-$Non è stata trovata nessuna prenotazione con id " + idPrenotazione;
    }

    public synchronized String rimuoviPosti(List<Integer> pren, int idPrenotazione) {
        ArrayList<prenotazionePosto> toRemove = new ArrayList<>();
        for(int toCheck : pren)
            for(prenotazionePosto prenCheck : this.postiOccupati)
                if (prenCheck.posto() == toCheck) {
                    for(prenotazionePosto addedBefore : toRemove)
                        if (addedBefore.posto() == prenCheck.posto())
                            return "-$Sono stati trovati dei posti duplicati";
                    if (prenCheck.idPrenotazione() != idPrenotazione)
                        return "-$Questa non è una tua prenotazione";
                    toRemove.add(prenCheck);
                    break;
                }
        if (toRemove.size() == pren.size()) {
            while (!toRemove.isEmpty())
                this.postiOccupati.remove(toRemove.remove(0));
        } else return "-$Sono stati inseriti posti non esistenti";
        return "+$Posti rimossi con successo";
    }

    @Override
    public String toString() {
        StringBuilder postiOccupatiSTR = new StringBuilder();
        for(prenotazionePosto pren : postiOccupati) {
            postiOccupatiSTR.append(pren);
        }
        return String.format("%s%s[%s]$%s", film, sala.toString(), postiOccupatiSTR, dateTime.toLocalDate());
    }

    public int generaIdPrenotazione() {
        Set<Integer> set = new TreeSet<>();
        for(prenotazionePosto pren : this.postiOccupati)
            set.add(pren.idPrenotazione());
        int min = 0;
        for(int value : set) {
            if (value == min)
                min++;
            else
                return min;
        }

        return min;
    }
}

