package database.structure.film;

public record Film(String nome, String descrizione) {
    @Override
    public String toString() {
        return String.format("$%s$%s", this.nome, this.descrizione);
    }
}
