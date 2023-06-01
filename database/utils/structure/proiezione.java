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

    public List<Integer> getPostiOccupati() {
        List<Integer> output = new ArrayList<>();
        for(prenotazione pren : this.postiOccupati)
            output.add(pren.posto());
        return output;
    }

    public int aggiungiPosti(List<prenotazione> pren) {
        if (this.postiOccupati.size() + pren.size() <= posti)
            return -1;
        for(prenotazione prenCheck : pren)
            for(prenotazione occupati : this.postiOccupati)
                if (occupati.posto() == prenCheck.posto())
                    return 0;
        this.postiOccupati.addAll(pren);
        return 1;
    }

    public boolean rimuoviPosti(int idPrenotazione) {
        boolean removedOne = false;
        for(int i = 0; i < this.postiOccupati.size(); i++)
            if (this.postiOccupati.get(i).idPrenotazione() == idPrenotazione) {
                this.postiOccupati.remove(i);
                i--;
                removedOne = true;
            }
        return removedOne;
    }

    public int rimuoviPosti(List<Integer> pren) {
        ArrayList<prenotazione> toRemove = new ArrayList<>();
        for(int toCheck : pren)
            for(prenotazione prenCheck : this.postiOccupati)
                if (prenCheck.posto() == toCheck) {
                    for(prenotazione addedBefore : toRemove)
                        if (addedBefore.posto() == prenCheck.posto())
                            return -1;
                    toRemove.add(prenCheck);
                    break;
                }
        if (toRemove.size() == pren.size()) {
            while (!toRemove.isEmpty())
                this.postiOccupati.remove(toRemove.remove(0));
        } else return 0;
        return 1;
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

