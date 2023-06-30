package it.unimib.finalproject.database.structure.film;



import it.unimib.finalproject.database.structure.Sala;
import it.unimib.finalproject.database.utils.ReadWriteLock;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Alessandro Condello
 * @since 1/06/23
 * @last-modified 29/06/23
 */
public class Proiezione {
    private final Film film;
    private final Sala sala;
    private final List<prenotazionePosto> postiOccupati;
    private final LocalDateTime dateTime;
    private final int id;
    private final ReadWriteLock locker;

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
        this.locker = new ReadWriteLock();
        //this.postiOccupati = postiOccupati;
    }

    public int getId() {
        return id;
    }

    public Film getFilmStructure() {
        return film;
    }

    public int getPosti() {
        int output;
        try {
            this.locker.lockRead();
            output = sala.getPosti();
        }catch (InterruptedException ignored) {
        }finally {
            this.locker.unlockRead();
            output = -1;
        }

        return output;
    }

    public String getPostiOccupati() {
        StringBuilder output = new StringBuilder("+[");
        try {
            this.locker.lockRead();
            for (prenotazionePosto pren : this.postiOccupati) {
                output.append(pren.toString());
            }
        }catch (InterruptedException e) {
            this.locker.unlockRead();
            return "";
        }
        this.locker.unlockRead();
        output.append("]");
        return output.toString();
    }

    public String aggiungiPosti(List<Integer> pren, int idPrenotazione) {
        String output = "";
        try {
            this.locker.lockWrite();
            if (this.postiOccupati.size() + pren.size() >= sala.getPosti())
                return "-$Non ci sono abbastanza posti";
            for (int prenCheck : pren)
                for (prenotazionePosto occupati : this.postiOccupati)
                    if (occupati.posto() == prenCheck)
                        return "-$Qualche posto è già stato occupato";
                    else if (prenCheck > this.sala.getPosti())
                        return "-$Posto non esistente";
            for (int prenCheck : pren)
                this.postiOccupati.add(new prenotazionePosto(idPrenotazione, prenCheck));
            output = "+$Prenotazione effettuata con successo";
        }catch (InterruptedException ignored) {
        }finally {
            this.locker.unlockWrite();
        }

        return output;
    }

    public String rimuoviPosti(int idPrenotazione) {
        boolean removedOne = false;
        String output;
        try {
            this.locker.lockWrite();
            for (int i = 0; i < this.postiOccupati.size(); i++)
                if (this.postiOccupati.get(i).idPrenotazione() == idPrenotazione) {
                    this.postiOccupati.remove(i);
                    i--;
                    removedOne = true;
                }
            output =  removedOne
                    ? "+$Prenotazione rimossa con successo"
                    : "-$Non è stata trovata nessuna prenotazione con id " + idPrenotazione;
        }catch (InterruptedException ignored) {
            output = "-$Internal error";
        }
        finally {
            this.locker.unlockWrite();
        }
        return output;
    }

    public String rimuoviPosti(List<Integer> pren, int idPrenotazione) {
        ArrayList<prenotazionePosto> toRemove = new ArrayList<>();
        boolean found = false;
        String output = "";
        try {
            this.locker.lockWrite();
            for (int toCheck : pren)
                for (prenotazionePosto prenCheck : this.postiOccupati)
                    if (prenCheck.posto() == toCheck) {
                        for (prenotazionePosto addedBefore : toRemove)
                            if (addedBefore.posto() == prenCheck.posto()) {
                                output = "-$Sono stati trovati dei posti duplicati";
                                break;
                            }
                        if (prenCheck.idPrenotazione() != idPrenotazione) {
                            output = "-$Questa non è una tua prenotazione";
                            break;
                        }
                        toRemove.add(prenCheck);
                        found = true;
                        break;
                    }
            if (toRemove.size() == pren.size()) {
                while (!toRemove.isEmpty())
                    this.postiOccupati.remove(toRemove.remove(0));
            } else output = "-$Sono stati inseriti posti non esistenti";
            if (found && output.equals(""))
                output = "+$Posti rimossi con successo";
        }catch (InterruptedException ignored) {
            output = "-$Internal Error";
        }finally {
            this.locker.unlockWrite();
        }
        return output;
    }

    @Override
    public String toString() {
        StringBuilder postiOccupatiSTR = new StringBuilder();
        for(prenotazionePosto pren : postiOccupati) {
            postiOccupatiSTR.append(pren);
        }
        return String.format("%s%s[%s]$%s-%s-%s", film, sala.toString(), postiOccupatiSTR, dateTime.toLocalDate(), dateTime.getHour(), dateTime.getMinute());
    }

    public int generaIdPrenotazione() {
        int min = 0;
        try {
            this.locker.lockWrite();
            Set<Integer> set = new TreeSet<>();
            for (prenotazionePosto pren : this.postiOccupati)
                set.add(pren.idPrenotazione());
            for (int value : set) {
                if (value == min)
                    min++;
                else
                    return min;
            }
        }catch (InterruptedException ignored) {
            min = -1;
        }finally {
            this.locker.unlockWrite();
        }

        return min;
    }
}

