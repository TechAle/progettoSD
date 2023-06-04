package database.structure;


import database.structure.film.Proiezione;

import java.util.ArrayList;
import java.util.List;

public class Sala {
    String nome;
    int posti;
    List<Proiezione> proiezione;

    public Sala(String nome, int posti) {
        this.nome = nome;
        this.posti = posti;
        proiezione = new ArrayList<>();
    }

    public void addProiezione(Proiezione proiezione) {
        this.proiezione.add(proiezione);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getPosti() {
        return posti;
    }

    public void setPosti(int posti) {
        this.posti = posti;
    }

    @Override
    public String toString() {
        return String.format("$%s:%s", this.nome, this.posti);
    }
}
