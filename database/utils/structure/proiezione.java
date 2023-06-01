package database.utils.structure;

import java.util.ArrayList;
import java.util.List;

public class proiezione {
    private final String nome;
    private final int posti;
    private final List<prenotazione> postiOccupati;

    public proiezione(String nome, int posti, String postiOccupati) {
        this.nome = nome;
        this.posti = posti;
        postiOccupati = postiOccupati.replaceAll(" ", "");
        postiOccupati = postiOccupati.substring(1, postiOccupati.length() -1);
        this.postiOccupati = new ArrayList<>();
        if (postiOccupati.length() > 0)
            for(String value : postiOccupati.split(";")) {
                value = value.substring(1, value.length() - 1);
                int b = 0;
                String[] values = value.split("\\.");
                this.postiOccupati.add(new prenotazione(Integer.parseInt(values[0]), Integer.parseInt(values[1])));
            }

        //this.postiOccupati = postiOccupati;
    }


    public String getNome() {
        return nome;
    }

    public int getPosti() {
        return posti;
    }

    public String getPostiOccupati() {
        StringBuilder output = new StringBuilder();
        for(prenotazione pren : this.postiOccupati)
            output.append(pren.toString()).append("\r\n");
        return output.toString();
    }

    public synchronized String aggiungiPosti(List<Integer> pren, int idPrenotazione) {
        if (this.postiOccupati.size() + pren.size() <= posti)
            return "-Non ci sono abbastanza posti";
        for(int prenCheck : pren)
            for(prenotazione occupati : this.postiOccupati)
                if (occupati.posto() == prenCheck)
                    return "-Qualche posto è già stato occupato";
        for (int prenCheck : pren)
            this.postiOccupati.add(new prenotazione(idPrenotazione, prenCheck));
        return "+Prenotazione effettuata con successo";
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
                ? "+Prenotazione rimossa con successo"
                : "-Non è stata trovata nessuna prenotazione con id " + idPrenotazione;
    }

    public synchronized String rimuoviPosti(List<Integer> pren) {
        ArrayList<prenotazione> toRemove = new ArrayList<>();
        for(int toCheck : pren)
            for(prenotazione prenCheck : this.postiOccupati)
                if (prenCheck.posto() == toCheck) {
                    for(prenotazione addedBefore : toRemove)
                        if (addedBefore.posto() == prenCheck.posto())
                            return "-Sono stati trovati dei posti duplicati";
                    toRemove.add(prenCheck);
                    break;
                }
        if (toRemove.size() == pren.size()) {
            while (!toRemove.isEmpty())
                this.postiOccupati.remove(toRemove.remove(0));
        } else return "-Sono stati inseriti posti non esistenti";
        return "+Posti rimossi con successo";
    }

    @Override
    public String toString() {
        StringBuilder postiOccupatiSTR = new StringBuilder();
        boolean first = true;
        for(prenotazione pren : postiOccupati) {
            if (!first)
                postiOccupatiSTR.append(',');
            postiOccupatiSTR.append(pren);
            first = false;
        }
        return String.format("$%s:%s[%s]", nome, posti, postiOccupatiSTR);
    }
}

